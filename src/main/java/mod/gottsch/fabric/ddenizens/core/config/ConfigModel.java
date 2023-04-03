package mod.gottsch.fabric.ddenizens.core.config;

import io.wispforest.owo.config.annotation.*;

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

        public CommonSpawnConfig(int i, int j, int k, int minHeight, int maxHeight) {
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
            spawnConfig = new CommonSpawnConfig(25, 1, 1, MIN_HEIGHT, MAX_HEIGHT);/
        }
    }
}
