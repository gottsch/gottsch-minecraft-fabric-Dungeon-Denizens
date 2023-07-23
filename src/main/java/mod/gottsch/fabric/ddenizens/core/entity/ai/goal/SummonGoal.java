/*
 * This file is part of  Dungeon Denizens.
 * Copyright (c) 2021, Mark Gottschling (gottsch)
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
package mod.gottsch.fabric.ddenizens.core.entity.ai.goal;

import mod.gottsch.fabric.gottschcore.spatial.Coords;
import mod.gottsch.fabric.gottschcore.spatial.ICoords;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

/**
 * 
 * @author Mark Gottschling on Apr 19, 2022
 *
 */
public abstract class SummonGoal extends Goal {
	protected int summonCooldownTime;
	protected int cooldownTime;
	
	/**
	 * 
	 * @param summonCoolDownTime
	 */
	public SummonGoal(int summonCoolDownTime) {
		this.summonCooldownTime = summonCoolDownTime;
	}
	
	/**
	 * 
	 * @param level
	 * @param random
	 * @param owner
	 * @param entityType
	 * @param coords
	 * @param target
	 * @return
	 */
	protected boolean spawn(ServerWorld level, Random random, LivingEntity owner, EntityType<? extends MobEntity> entityType, ICoords coords, LivingEntity target) {
		for (int i = 0; i < 20; i++) { // 20 tries
			int spawnX = coords.getX() + MathHelper.nextInt(random, 1, 2) * MathHelper.nextInt(random, -1, 1);
			int spawnY = coords.getY() + MathHelper.nextInt(random, 1, 2) * MathHelper.nextInt(random, -1, 1);
			int spawnZ = coords.getZ() + MathHelper.nextInt(random, 1, 2) * MathHelper.nextInt(random, -1, 1);

			ICoords spawnCoords = new Coords(spawnX, spawnY, spawnZ);

			boolean isSpawned = false;
			if (level.isClient()) {
				if(SpawnRestriction.canSpawn(entityType, level, SpawnReason.SPAWNER, spawnCoords.toPos(), level.getRandom())) {
					// MageFlame.LOGGER.debug("placement is good");
					// create entity
					MobEntity mob = entityType.create(level);
					if (mob != null) {
						mob.setPos(spawnX, spawnY, spawnZ);
						mob.setTarget(target);
						// add entity into the level (ie EntityJoinWorldEvent)
						level.spawnEntity(mob);
						isSpawned = true;
					}
				}
				if (isSpawned) {
					for (int p = 0; p < 20; p++) {
						double xSpeed = random.nextGaussian() * 0.02D;
						double ySpeed = random.nextGaussian() * 0.02D;
						double zSpeed = random.nextGaussian() * 0.02D;
						level.addParticle(ParticleTypes.POOF, owner.getBlockPos().getX() + 0.5D, owner.getBlockPos().getY(), owner.getBlockPos().getZ() + 0.5D, xSpeed, ySpeed, zSpeed);
						}
					return true;
				}
			}

		}
		return false;
	}
}
