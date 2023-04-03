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
package mod.gottsch.fabric.ddenizens.core.config;

import io.wispforest.owo.config.annotation.*;

import java.util.List;

/**
 * Created by Mark Gottschling on 4/2/2023
 */
@Modmenu(modId = "ddenizens")
@Config(name = "ddenizens", wrapperName = "MyConfig")
public class ConfigModel {
    public static final int MIN_HEIGHT = -64;
    public static final int MAX_HEIGHT = 319;

    @SectionHeader("mobProperties")

    @Nest
    public GhoulConfig ghoulConfig = new GhoulConfig();

    public static class CommonSpawnConfig {
        @RangeConstraint(min =-64, max = 319)
        public int minHeight;

        @RangeConstraint(min = -64, max = 319)
        public int maxHeight;

        public CommonSpawnConfig() {}

        public CommonSpawnConfig(int weight, int minSpawn, int maxSpawn, int minHeight, int maxHeight) {
            this.minHeight = minHeight;
            this.maxHeight = maxHeight;
        }
    }

    public static interface IMobConfig {
        public CommonSpawnConfig getSpawnConfig();
    }

    public static abstract class MobConfig implements IMobConfig {
        public CommonSpawnConfig spawnConfig;
        public CommonSpawnConfig getSpawnConfig() {
            return spawnConfig;
        }
    }

    public static class GhoulConfig extends MobConfig {
        public GhoulConfig() {
            spawnConfig = new CommonSpawnConfig(25, 1, 1, MIN_HEIGHT, MAX_HEIGHT);
        }
    }

    public static class HeadlessConfig extends MobConfig {
        List<String> injuredAlertOtherList;
        List<String> targetsAlertOtherList;

        public HeadlessConfig() {
            spawnConfig = new CommonSpawnConfig(40, 1, 2, MIN_HEIGHT, MAX_HEIGHT);
        }
    }
}
