/*
 * This file is part of the TemplateMod project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2025  Fallen_Breath and contributors
 *
 * TemplateMod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TemplateMod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TemplateMod.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.fallenbreath.template_mod.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class BoxCommand {
   /* private static final Logger LOGGER = LoggerFactory.getLogger("SoloCircuit");
    private static final int SHULKER_BOX_SLOTS = 27;

    public static void register() {
        CommandRegistrationCallback.EVENT.register(BoxCommand::registerCommands);
        TutorialDataComponentTypes.initialize();
    }

    private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher,
                                         CommandRegistryAccess registryAccess,
                                         CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("box")
                .executes(ctx -> packInventory(ctx.getSource()))
                .then(CommandManager.literal("split")
                        .executes(ctx -> unpackShulkerBox(ctx.getSource()))));
    }

    private static int packInventory(ServerCommandSource source) {
        var player = source.getPlayer();
        if (player == null) {
            source.sendError(Text.literal("此命令必须由玩家执行"));
            return 0;
        }

        PlayerInventory inventory = player.getInventory();
        List<ItemStack> itemsToPack = new ArrayList<>();

        LOGGER.debug("Collecting items to pack from inventory");
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty() && stack.getItem() != Items.SHULKER_BOX) {
                itemsToPack.add(stack.copy());
            }
        }

        if (itemsToPack.isEmpty()) {
            player.sendMessage(Text.literal("没有可打包的物品！"), false);
            return 0;
        }

        LOGGER.debug("Packing {} items", itemsToPack.size());
        List<ItemStack> shulkerSlots = packItems(itemsToPack);
        if (shulkerSlots.size() > SHULKER_BOX_SLOTS) {
            player.sendMessage(Text.literal("物品数量过多，无法放入一个潜影盒！"), false);
            return 0;
        }

        List<Integer> clearedSlots = new ArrayList<>();
        List<ItemStack> clearedItems = new ArrayList<>();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty() && stack.getItem() != Items.SHULKER_BOX) {
                clearedSlots.add(i);
                clearedItems.add(stack.copy());
                inventory.setStack(i, ItemStack.EMPTY);
            }
        }

        ItemStack shulkerBox = new ItemStack(Items.SHULKER_BOX);
        List<ItemStack> itemsToSave = new ArrayList<>(itemsToPack);
        LOGGER.debug("Setting PACKED_ITEMS with {} items", itemsToSave.size());
        shulkerBox.set(TutorialDataComponentTypes.PACKED_ITEMS, itemsToSave);

        List<ItemStack> savedItems = shulkerBox.get(TutorialDataComponentTypes.PACKED_ITEMS);
        LOGGER.debug("Immediately retrieved PACKED_ITEMS: {}", savedItems);
        if (savedItems == null || savedItems.isEmpty()) {
            LOGGER.error("Failed to save items to shulker box component");
            for (int i = 0; i < clearedSlots.size(); i++) {
                inventory.setStack(clearedSlots.get(i), clearedItems.get(i));
            }
            player.sendMessage(Text.literal("打包失败，无法保存数据到潜影盒！"), false);
            return 0;
        }

        if (!inventory.insertStack(shulkerBox)) {
            for (int i = 0; i < clearedSlots.size(); i++) {
                inventory.setStack(clearedSlots.get(i), clearedItems.get(i));
            }
            player.sendMessage(Text.literal("背包没有空间存放潜影盒！"), false);
            return 0;
        }

        inventory.markDirty();
        player.getInventory().updateItems();
        player.sendMessage(Text.literal("背包已打包到一个潜影盒中！"), false);
        LOGGER.info("Successfully packed {} items into shulker box", savedItems.size());
        return 1;
    }

    private static int unpackShulkerBox(ServerCommandSource source) {
        var player = source.getPlayer();
        if (player == null) {
            source.sendError(Text.literal("此命令必须由玩家执行"));
            return 0;
        }

        PlayerInventory inventory = player.getInventory();
        ItemStack heldItem = inventory.getMainHandStack();
        int shulkerSlot = -1;

        LOGGER.debug("Searching for shulker box with PACKED_ITEMS");
        if (heldItem.getItem() == Items.SHULKER_BOX && heldItem.contains(TutorialDataComponentTypes.PACKED_ITEMS)) {
            shulkerSlot = inventory.selectedSlot;
            LOGGER.debug("Found shulker box in hand at slot {}", shulkerSlot);
        } else {
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStack(i);
                if (stack.getItem() == Items.SHULKER_BOX && stack.contains(TutorialDataComponentTypes.PACKED_ITEMS)) {
                    shulkerSlot = i;
                    LOGGER.debug("Found shulker box at slot {}", i);
                    break;
                }
            }
        }

        if (shulkerSlot == -1) {
            player.sendMessage(Text.literal("请手持一个包含打包数据的潜影盒或确保背包中有一个！"), false);
            LOGGER.debug("No shulker box with PACKED_ITEMS found");
            return 0;
        }

        ItemStack shulkerBox = inventory.getStack(shulkerSlot);
        LOGGER.debug("Shulker box NBT before unpacking: {}", shulkerBox.toNbtAllowEmpty(player.getServer().getRegistryManager()));
        List<ItemStack> itemsToUnpack = shulkerBox.get(TutorialDataComponentTypes.PACKED_ITEMS);
        LOGGER.debug("Retrieved PACKED_ITEMS: {}", itemsToUnpack);

        if (itemsToUnpack == null || itemsToUnpack.isEmpty()) {
            LOGGER.warn("No packed_items data found in shulker box: NBT={}", shulkerBox.toNbtAllowEmpty(player.getServer().getRegistryManager()));
            player.sendMessage(Text.literal("此潜影盒没有打包数据！"), false);
            return 0;
        }

        int emptySlots = getEmptySlotCount(inventory);
        int requiredSlots = calculateRequiredSlots(itemsToUnpack);
        if (emptySlots < requiredSlots) {
            player.sendMessage(Text.literal("背包空间不足！需要 " + requiredSlots + " 个空槽位"), false);
            return 0;
        }

        List<ItemStack> unpackedItems = new ArrayList<>();
        for (ItemStack item : itemsToUnpack) {
            ItemStack copy = item.copy();
            if (!inventory.insertStack(copy)) {
                for (ItemStack unpacked : unpackedItems) {
                    player.dropItem(unpacked, false);
                }
                player.sendMessage(Text.literal("解包失败，背包空间不足！"), false);
                return 0;
            }
            unpackedItems.add(copy);
        }

        inventory.setStack(shulkerSlot, ItemStack.EMPTY);
        inventory.markDirty();
        player.getInventory().updateItems();
        player.sendMessage(Text.literal("潜影盒已解包！"), false);
        LOGGER.info("Successfully unpacked {} items", unpackedItems.size());
        return 1;
    }

    private static List<ItemStack> packItems(List<ItemStack> items) {
        List<ItemStack> slots = new ArrayList<>();
        for (ItemStack item : items) {
            ItemStack copy = item.copy();
            boolean merged = false;
            for (ItemStack slot : slots) {
                if (ItemStack.areItemsEqual(slot, copy) && slot.getCount() < slot.getMaxCount()) {
                    int space = slot.getMaxCount() - slot.getCount();
                    int toAdd = Math.min(space, copy.getCount());
                    slot.increment(toAdd);
                    copy.decrement(toAdd);
                    merged = true;
                    if (copy.isEmpty()) break;
                }
            }
            if (!merged && !copy.isEmpty()) {
                slots.add(copy);
            }
        }
        return slots;
    }

    private static int getEmptySlotCount(PlayerInventory inventory) {
        int emptySlots = 0;
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.getStack(i).isEmpty()) {
                emptySlots++;
            }
        }
        return emptySlots;
    }

    private static int calculateRequiredSlots(List<ItemStack> items) {
        return packItems(items).size();
    }
    */
}