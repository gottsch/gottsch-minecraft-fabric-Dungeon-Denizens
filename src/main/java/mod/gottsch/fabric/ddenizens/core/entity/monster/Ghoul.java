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


import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static mod.gottsch.fabric.ddenizens.DD.CONFIG;

/**
 * Slightly faster than zombie.
 * Burns in sun but for shorter time.
 * Attempts to shelter from sun.
 * Attacks all farm animals (chicken, cow, pig, sheep)
 * Picks up meats.
 * Heals self if standing still and holding meat.
 * @author Mark Gottschling on Apr 6, 2022
 *
 */
public class Ghoul extends HostileEntity {
	private boolean canOpenDoors;

	/**
	 * 
	 * @param entityType
	 * @param level
	 */
	public Ghoul(EntityType<? extends HostileEntity> entityType, World level) {
		super(entityType, level);
		this.setCanPickUpLoot(true);
		this.setCanOpenDoors(true); //Config.Mobs.GHOUL.canOpenDoors.get());
		if (this.canOpenDoors()) {
			((MobNavigation)this.getNavigation()).setCanPathThroughDoors(true);
		}
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(2, new AvoidSunlightGoal(this));
		this.goalSelector.add(3, new EscapeSunlightGoal(this, 1.0));
		if (this.canOpenDoors()) {
			this.goalSelector.add(2, new LongDoorInteractGoal(this, true));
		}

//		this.goalSelector.add(3, new FleeEntityGoal<>(this, Boulder.class, 6.0F, 1.0D, 1.2D, entity -> {
//			return (entity instanceof Boulder) && ((Boulder)entity).isActive();
//		}));

		this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0D, false));
		this.goalSelector.add(4, new GhoulHealGoal(this));
		this.goalSelector.add(5, new MoveThroughVillageGoal(this, 1.0D, true, 4, () -> true));
		this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0));
		this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
		this.goalSelector.add(7, new LookAroundGoal(this));

		// target goals
		// for the included list. ie this mob will alert the other specific listed mobs.
		this.targetSelector.add(1, (new RevengeGoal(this)));
		this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
		this.targetSelector.add(2, new ActiveTargetGoal<>(this, VillagerEntity.class, true));
		this.targetSelector.add(3, new ActiveTargetGoal<>(this, ChickenEntity.class, true));
		this.targetSelector.add(3, new ActiveTargetGoal<>(this, CowEntity.class, true));
		this.targetSelector.add(3, new ActiveTargetGoal<>(this, PigEntity.class, true));
		this.targetSelector.add(3, new ActiveTargetGoal<>(this, SheepEntity.class, true));
		this.targetSelector.add(3, new ActiveTargetGoal<>(this, HorseEntity.class, true));
		this.targetSelector.add(4, new ActiveTargetGoal<>(this, TurtleEntity.class, 10, true, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
	}

	public static DefaultAttributeContainer.Builder createAttributes() {
		return HostileEntity.createHostileAttributes()
				.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0D)
				.add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK)
				.add(EntityAttributes.GENERIC_ARMOR, 2.0D)
				.add(EntityAttributes.GENERIC_MAX_HEALTH)
				.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 15.0)
				.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3F);  // faster than zombie
	}

	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
		entityData = super.initialize(world, difficulty, spawnReason, entityData, entityNbt);

		this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE).addPersistentModifier(new EntityAttributeModifier("Random ghoul-spawn bonus", this.random.nextDouble() * (double)0.05F, EntityAttributeModifier.Operation.ADDITION));
		double chance = this.random.nextDouble() * 1.5D * (double)difficulty.getClampedLocalDifficulty();
		if (chance > 1.0D) {
			this.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).addPersistentModifier(new  EntityAttributeModifier("Random ghoul-spawn bonus", chance,  EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
		}
		if (this.random.nextFloat() < chance * 0.05F) {
			this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).addPersistentModifier(new  EntityAttributeModifier("Random ghoul-spawn bonus", this.random.nextDouble() * 3.0D + 1.0D,  EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
		}
		return entityData;
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


	/**
	 * Wants to pick up meats
	 */
	@Override
	public boolean canGather(ItemStack stack) {
		return stack.isOf(Items.ROTTEN_FLESH)
				|| stack.isOf(Items.BEEF)
				|| stack.isOf(Items.MUTTON)
				|| stack.isOf(Items.RABBIT)
				|| stack.isOf(Items.CHICKEN)
				|| stack.isOf(Items.COOKED_BEEF)
				|| stack.isOf(Items.COOKED_MUTTON)
				|| stack.isOf(Items.COOKED_RABBIT)
				|| stack.isOf(Items.COOKED_CHICKEN);
	}

	@Override
	public boolean tryEquip(ItemStack stack) {
		EquipmentSlot slot = EquipmentSlot.MAINHAND;
		ItemStack heldStack = this.getEquippedStack(slot);
		if (heldStack != null && !heldStack.isEmpty()) {
			slot = EquipmentSlot.OFFHAND;
			heldStack = this.getEquippedStack(slot);
		}
		if (heldStack == null || heldStack.isEmpty()) {
			this.equipLootStack(slot, stack);
			// set stack to drop on death
			this.setEquipmentDropChance(slot, 100f);
			return true;
		}
		return false;
	}

	public boolean hasMeatInventory() {
		return !this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()
				|| !this.getEquippedStack(EquipmentSlot.OFFHAND).isEmpty();
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_ZOMBIE_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_ZOMBIE_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_ZOMBIE_DEATH;
	}

	protected SoundEvent getStepSound() {
		return SoundEvents.ENTITY_ZOMBIE_STEP;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(this.getStepSound(), 0.15f, 1.0f);
	}

	@Override
	public EntityGroup getGroup() {

		return EntityGroup.UNDEAD;
	}

	public boolean canOpenDoors() {
		return canOpenDoors;
	}

	public void setCanOpenDoors(boolean canOpenDoors) {
		this.canOpenDoors = canOpenDoors;
	}

	/**
	 * 
	 * @author Mark Gottschling on Apr 8, 2022
	 *
	 */
	public static class GhoulHealGoal extends Goal {
		private final Ghoul ghoul;

		public GhoulHealGoal(Ghoul ghoul) {
			this.ghoul = ghoul;
		}

		@Override
		public boolean canStart() {
			if (!getGhoul().isAlive()) {
				return false;
			}
			else if (!getGhoul().onGround) {
				return false;
			}
			else {
				// if health less than 3/4 && has some meat in inventory
				return getGhoul().getHealth() < (0.75F * getGhoul().getMaxHealth()) && getGhoul().hasMeatInventory();
			}
		}

		@Override
		public void start() {
			// get hand with meat
			ItemStack meatStack = getGhoul().getEquippedStack(EquipmentSlot.MAINHAND);
			if (meatStack.isEmpty()) {
				meatStack = getGhoul().getEquippedStack(EquipmentSlot.OFFHAND);
			}
			if (!meatStack.isEmpty()) {
				meatStack.decrement(1);
				//				getGhoul().setHealth(Math.min(getGhoul().getHealth() + 4.0F, getGhoul().getMaxHealth()));
				getGhoul().heal(CONFIG.ghoulConfig.healAmount());
			}
			super.start();
		}		

		public Ghoul getGhoul() {
			return ghoul;
		}
	}
}