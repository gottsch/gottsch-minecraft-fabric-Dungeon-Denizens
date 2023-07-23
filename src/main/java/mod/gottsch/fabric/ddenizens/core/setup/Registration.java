/*
 * This file is part of  Dungeon Denizens.
 * Copyright (c) 2023 Mark Gottschling (gottsch)
 *
 * All rights reserved.
 *
 * Dungeon Denizens is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dungeon Denizens is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Dungeon Denizens.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.fabric.ddenizens.core.setup;

import com.google.common.collect.Lists;
import mod.gottsch.fabric.ddenizens.DD;
import mod.gottsch.fabric.ddenizens.core.entity.monster.Ghoul;
import mod.gottsch.fabric.ddenizens.core.entity.monster.Headless;
import mod.gottsch.fabric.ddenizens.core.entity.monster.Shadow;
import mod.gottsch.fabric.ddenizens.core.entity.monster.Shadowlord;
import mod.gottsch.fabric.ddenizens.core.entity.projectile.Harmball;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;

/**
 * Created by Mark Gottschling on 4/2/2023
 */
public class Registration {
    // mob collections
    public static final List<EntityType<?>> ALL_MOBS = Lists.newArrayList();

    // entities
    public static final EntityType<Headless> DAEMON = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(DD.MOD_ID, "daemon"),
            FabricEntityTypeBuilder.create(
                            SpawnGroup.MONSTER, Headless::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.5f))
                    .trackRangeBlocks(50)
                    .forceTrackedVelocityUpdates(false)
                    .build()
    );

    public static final EntityType<Headless> HEADLESS = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(DD.MOD_ID, "headless"),
            FabricEntityTypeBuilder.create(
                            SpawnGroup.MONSTER, Headless::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.5f))
                    .trackRangeBlocks(50)
                    .forceTrackedVelocityUpdates(false)
                    .build()
    );

    public static final EntityType<Ghoul> GHOUL = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(DD.MOD_ID, "ghoul"),
            FabricEntityTypeBuilder.create(
                            SpawnGroup.MONSTER, Ghoul::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.68f))
                    .trackRangeBlocks(20)
                    .forceTrackedVelocityUpdates(false)
                    .build()
    );

    public static final EntityType<Shadow> SHADOW = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(DD.MOD_ID, "shadow"),
            FabricEntityTypeBuilder.create(
                            SpawnGroup.MONSTER, Shadow::new)
                    .dimensions(EntityDimensions.fixed(0.6F, 1.95F))
                    .forceTrackedVelocityUpdates(false)
                    .build()
    );

    public static final EntityType<Shadowlord> SHADOWLORD = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(DD.MOD_ID, "shadowlord"),
            FabricEntityTypeBuilder.create(
                            SpawnGroup.MONSTER, Shadowlord::new)
                    .dimensions(EntityDimensions.fixed(0.625F, 2.95F))
                    .forceTrackedVelocityUpdates(false)
                    .build()
    );

    // projectiles
    public static final EntityType<Harmball> HARMBALL = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(DD.MOD_ID, "harmball"),
            FabricEntityTypeBuilder.create(
                            SpawnGroup.MISC, Harmball::new)
                    .dimensions(EntityDimensions.fixed(0.5F, 0.5F))
                    .forceTrackedVelocityUpdates(false)
                    .build()
    );

    // items / eggs
    private static final Item HEADLESS_EGG = new SpawnEggItem(HEADLESS, 0xc8b486, 0x6f5e48, new FabricItemSettings().group(ItemGroup.MISC));
    private static final Item GHOUL_EGG = new SpawnEggItem(GHOUL,  0x93aba3, 0x869e96, new FabricItemSettings().group(ItemGroup.MISC));
    private static final Item SHADOW_EGG = new SpawnEggItem(SHADOW, 0x000000, 0x2b2b2b, new FabricItemSettings().group(ItemGroup.MISC));
    private static final Item SHADOWLORD_EGG = new SpawnEggItem(SHADOWLORD, 0x000000, 0x050831, new FabricItemSettings().group(ItemGroup.MISC));

    public static final Item HARMBALL_ITEM = new Item(new FabricItemSettings().group(ItemGroup.MISC));

    // NOTE must add mob to ALL_MOBS collection in order to register them to the biomes - see CommonSetup.onBiomeLoading
    // DOES THIS HOLD TRUE IN FABRIC?

    static {
        ALL_MOBS.add(DAEMON);
        ALL_MOBS.add(HEADLESS);
        ALL_MOBS.add(GHOUL);
        ALL_MOBS.add(SHADOW);
        ALL_MOBS.add(SHADOWLORD);
    }

    /**
     *
     */
    public static void register() {
        // register blocks

        // items
        Registry.register(Registry.ITEM, new Identifier(DD.MOD_ID, "headless_egg"), HEADLESS_EGG);
        Registry.register(Registry.ITEM, new Identifier(DD.MOD_ID, "ghoul_egg"), GHOUL_EGG);
        Registry.register(Registry.ITEM, new Identifier(DD.MOD_ID, "shadow_egg"), SHADOW_EGG);
        Registry.register(Registry.ITEM, new Identifier(DD.MOD_ID, "shadowlord_egg"), SHADOWLORD_EGG);

        // register entity attributes
        FabricDefaultAttributeRegistry.register(HEADLESS, Headless.createAttributes());
        FabricDefaultAttributeRegistry.register(GHOUL, Ghoul.createAttributes());
        FabricDefaultAttributeRegistry.register(SHADOW, Shadow.createAttributes());
        FabricDefaultAttributeRegistry.register(SHADOWLORD, Shadowlord.createAttributes());
    }
}
