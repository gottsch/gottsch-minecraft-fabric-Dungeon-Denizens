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
package mod.gottsch.fabric.ddenizens.core.entity.monster;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;

/**
 * Created by Mark Gottschling on 4/3/2023
 */
public abstract class DDMonster extends HostileEntity {
    private static final int UNDERGROUND_HEIGHT = 60;

    protected DDMonster(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

//    public static boolean checkDDSpawnRules(EntityType<? extends MobEntity> mob, World world, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
//
//        IMobConfig mobConfig = Config.Mobs.MOBS.get(EntityType.getKey(mob));
//        CommonSpawnConfig config = mobConfig.getSpawnConfig();
//
//        return pos.getY() > config.minHeight.get() && pos.getY() < config.maxHeight.get() && checkMobSpawnRules(mob, level, spawnType, pos, random);
//    }


}
