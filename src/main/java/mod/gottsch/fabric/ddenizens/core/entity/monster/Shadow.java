/*
 * This file is part of  Dungeon Denizens.
 * Copyright (c) 2022 Mark Gottschling (gottsch)
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

import mod.gottsch.fabric.gottschcore.random.RandomHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.Random;

/**
 * NOTE: 	mob.level.getBrightness(LightLayer.BLOCK, pos), mob.level.getMaxLocalRawBrightness(pos));
 * @author Mark Gottschling on Apr 12, 2022
 *
 */
public class Shadow extends DDMonster {

	private static final int NORMAL_DIFFICULTY_SECONDS = 10;
	private static final int HARD_DIFFICULTY_SECONDS = 20;
	private static final int WEAKNESS_SECONDS = 5;

	private boolean flee;

	/**
	 *
	 * @param entityType
	 * @param world
	 */
	public Shadow(EntityType<? extends HostileEntity> entityType, World world) {

		super(entityType, world);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(2, new AvoidSunlightGoal(this));
		this.goalSelector.add(3, new EscapeSunlightGoal(this, 1.5));
		this.goalSelector.add(3,  new ShadowFleeGoal<>(this, PlayerEntity.class, 6.0F, 1.2D, 1.2D));

//		this.goalSelector.add(3, new FleeEntityGoal<>(this, Boulder.class, 6.0F, 1.0D, 1.2D, entity -> {
//			return (entity instanceof Boulder) && ((Boulder)entity).isActive();
//		}));

		this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0D, false));
		this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0D));
		this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(7, new LookAroundGoal(this));

		this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
	}

	/**
	 * 
	 * @return
	 */
	public static DefaultAttributeContainer.Builder createAttributes() {
		return HostileEntity.createHostileAttributes()
				.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0D)
				.add(EntityAttributes.GENERIC_MAX_HEALTH)
				.add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0)
				.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.28F);
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.UNDEAD;
	}
	
	@Override
	public void tickMovement() {
		// set on fire if in sun
		boolean flag = this.isAffectedByDaylight();
		if (flag) {
			this.setOnFireFor(4);
		}
		super.tickMovement();
	}

	public static boolean checkShadowSpawnRules(EntityType<Shadow> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, net.minecraft.util.math.random.Random random) {
//		RegistryEntry<Biome> entry = world.getBiome(pos);
//		RegistryKey<Biome> key = entry.getKey().get();
//		RegistryKey<Biome> ocean = BiomeKeys.OCEAN;
		if (world.getDimension().effects().equals(DimensionTypes.THE_NETHER_ID)) {
//		return checkDDNetherSpawnRules(mob, level, spawnType, pos, random);
			return true;
		}
		else {
	//		return checkDDSpawnRules(mob, level, spawnType, pos, random);
			return true;
		}
//		return world.getDifficulty() != Difficulty.PEACEFUL && HostileEntity.isSpawnDark(world, pos, random) && HostileEntity.canMobSpawn(type, world, spawnReason, pos, random);
	}

	// TODO change difficulty seconds to ticks pulled in from Config
	@Override
	public boolean tryAttack(Entity target) {
		Random random = new Random();

		if (super.tryAttack(target)) {
			if (target instanceof PlayerEntity) {
				int seconds = 0;
				if (this.world.getDifficulty() == Difficulty.NORMAL) {
					seconds = NORMAL_DIFFICULTY_SECONDS;
				} else if (this.world.getDifficulty() == Difficulty.HARD) {
					seconds = HARD_DIFFICULTY_SECONDS;
				}
				// inflict blindness and/or weakness
				ItemStack helmetStack = ((PlayerEntity)target).getEquippedStack(EquipmentSlot.HEAD);
				if (helmetStack.isEmpty() || helmetStack.getItem() != Items.GOLDEN_HELMET) {
					if (RandomHelper.checkProbability(random, 90)) {
						if (seconds > 0) {
							((LivingEntity)target).addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, seconds * 20, 0), this);
						}
					}
				}

				if (RandomHelper.checkProbability(random, 20)) {
					if (seconds > 0) {
						((LivingEntity)target).addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, WEAKNESS_SECONDS * 20, 0), this);
					}
				}
			}
			return true;
		} else {
			return false;
		}
	}
//
//	/**
//	 *
//	 */
	@Override
	public boolean damage(DamageSource damageSource, float amount) {
		if (this.world.isClient()) {
			return false;
		}

		this.flee = true;

		if (damageSource.getSource()!= null && damageSource.getSource() instanceof PlayerEntity) {
			ItemStack heldStack = ((PlayerEntity)damageSource.getSource()).getStackInHand(Hand.MAIN_HAND);
			if (!heldStack.isEmpty() && heldStack.getItem() == Items.GOLDEN_SWORD) {
				// increase damage to that of iron sword
				amount += 2.0F;
				// negate the weakness from the strike power of the sword
				if (((PlayerEntity)damageSource.getSource()).hasStatusEffect(StatusEffects.WEAKNESS)) {
					amount += StatusEffects.WEAKNESS.adjustModifierAmount(0, null);
				}
			}
//			// TODO add shadow sword condition
			else {
				amount = 1.0F;
			}
		}
		return super.damage(damageSource, amount);
	}

	/**
	 *
	 * @author Mark Gottschling on Apr 13, 2022
	 *
	 * @param <T>
	 */
	public static class ShadowFleeGoal<T extends LivingEntity> extends FleeEntityGoal<T> {

		/**
		 *
		 * @param mob
		 * @param target
		 * @param maxDistance
		 * @param walkSpeedModifier
		 * @param sprintSpeedModifier
		 */
		public ShadowFleeGoal(PathAwareEntity mob, Class<T> target, float maxDistance, double walkSpeedModifier,
							  double sprintSpeedModifier) {
			super(mob, target, maxDistance, walkSpeedModifier, sprintSpeedModifier);

		}

		@Override
		public boolean canStart() {
			// if 'flee' is activated
			if (((Shadow)mob).flee) {
				return super.canStart();
			}
			return false;
		}

		@Override
		public void stop() {
			((Shadow)mob).flee = false;
			this.targetEntity = null;
		}

		@Override
		public boolean shouldContinue() {

			return !this.fleeingEntityNavigation.isIdle();
		}

	}
}
