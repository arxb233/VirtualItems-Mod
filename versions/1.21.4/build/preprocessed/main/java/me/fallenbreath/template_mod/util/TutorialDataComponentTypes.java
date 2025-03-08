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

package me.fallenbreath.template_mod.util;

import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class TutorialDataComponentTypes {
  /*  private static final Logger LOGGER = LoggerFactory.getLogger("SoloCircuit");

    public static final ComponentType<List<ItemStack>> PACKED_ITEMS = register(
            "packed_items",
            builder -> builder
                    .codec(createItemStackListCodec())
                    .packetCodec(createItemStackListPacketCodec())
    );

    private static <T> ComponentType<T> register(String path, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(
                Registries.DATA_COMPONENT_TYPE,
                Identifier.of("solocircuit", path),
                builderOperator.apply(ComponentType.builder()).build()
        );
    }

    private static com.mojang.serialization.Codec<List<ItemStack>> createItemStackListCodec() {
        return com.mojang.serialization.Codec.list(ItemStack.CODEC)
                .xmap(ArrayList::new, ArrayList::new);
    }

    private static PacketCodec<RegistryByteBuf, List<ItemStack>> createItemStackListPacketCodec() {
        return PacketCodec.of(
                (value, buf) -> {
                    try {
                        NbtCompound compound = new NbtCompound();
                        NbtList list = new NbtList();
                        for (ItemStack stack : value) {
                            if (stack != null && !stack.isEmpty()) {
                                NbtCompound itemNbt = (NbtCompound) stack.toNbt(buf.getRegistryManager());
                                list.add(itemNbt);
                            }
                        }
                        compound.put("packed_items", list);
                        LOGGER.debug("Encoding packed_items with {} items: {}", list.size(), compound);
                        buf.writeNbt(compound);
                    } catch (Exception e) {
                        LOGGER.error("Failed to encode packed_items: {}", e.getMessage(), e);
                        buf.writeNbt(new NbtCompound());
                    }
                },
                (buf) -> {
                    try {
                        NbtElement element = buf.readNbt();
                        LOGGER.debug("Raw NBT received: {}", element);
                        if (!(element instanceof NbtCompound compound)) {
                            LOGGER.warn("Received NBT is not a compound tag: {}, returning empty list", element);
                            return new ArrayList<>();
                        }
                        if (!compound.contains("packed_items", NbtElement.LIST_TYPE)) {
                            LOGGER.warn("NBT compound lacks 'packed_items' list: {}, returning empty list", compound);
                            return new ArrayList<>();
                        }
                        NbtList list = compound.getList("packed_items", NbtElement.COMPOUND_TYPE);
                        List<ItemStack> items = new ArrayList<>();
                        for (int i = 0; i < list.size(); i++) {
                            NbtCompound itemCompound = list.getCompound(i);
                            ItemStack stack = ItemStack.fromNbt(buf.getRegistryManager(), itemCompound).orElse(ItemStack.EMPTY);
                            if (!stack.isEmpty()) {
                                items.add(stack);
                            }
                        }
                        LOGGER.debug("Decoded {} items from packed_items", items.size());
                        return items;
                    } catch (Exception e) {
                        LOGGER.error("Failed to decode packed_items: {}", e.getMessage(), e);
                        return new ArrayList<>();
                    }
                }
        );
    }

    public static void initialize() {} */
}