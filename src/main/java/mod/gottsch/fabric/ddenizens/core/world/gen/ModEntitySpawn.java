package mod.gottsch.fabric.ddenizens.core.world.gen;

import mod.gottsch.fabric.ddenizens.core.entity.monster.Shadow;
import mod.gottsch.fabric.ddenizens.core.entity.monster.Shadowlord;
import mod.gottsch.fabric.ddenizens.core.setup.Registration;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.BiomeKeys;

/**
 * Created by Mark Gottschling on 4/7/2023
 */
public class ModEntitySpawn {
    public static void addEntitySpawn() {
        BiomeModifications.addSpawn(BiomeSelectors.excludeByKey(BiomeKeys.OCEAN), SpawnGroup.MONSTER,
                Registration.HEADLESS, 40, 1, 1);

        BiomeModifications.addSpawn(BiomeSelectors.excludeByKey(BiomeKeys.OCEAN), SpawnGroup.MONSTER,
                Registration.GHOUL, 25, 1, 1);

        BiomeModifications.addSpawn(BiomeSelectors.excludeByKey(BiomeKeys.OCEAN), SpawnGroup.MONSTER,
                Registration.SHADOW, 30, 1, 1);

        BiomeModifications.addSpawn(BiomeSelectors.excludeByKey(BiomeKeys.OCEAN), SpawnGroup.MONSTER,
                Registration.SHADOWLORD, 15, 1, 1);

        // TODO need to update his line to use the correct SpawnRestrictionPredicate ei DDMonster::checkDDSpawnRules
        SpawnRestriction.register(Registration.HEADLESS, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);

        SpawnRestriction.register(Registration.GHOUL, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);

        SpawnRestriction.register(Registration.SHADOW, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Shadow::checkShadowSpawnRules);

        SpawnRestriction.register(Registration.SHADOWLORD, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Shadowlord::checkShadowlordSpawnRules);
    }
}
