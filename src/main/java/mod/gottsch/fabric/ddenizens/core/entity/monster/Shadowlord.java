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
package mod.gottsch.fabric.ddenizens.core.entity.monster;

import mod.gottsch.fabric.ddenizens.DD;
import mod.gottsch.fabric.ddenizens.core.entity.ai.goal.SummonGoal;
import mod.gottsch.fabric.ddenizens.core.setup.Registration;
import mod.gottsch.fabric.gottschcore.random.RandomHelper;
import mod.gottsch.fabric.gottschcore.spatial.Coords;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

import static mod.gottsch.fabric.ddenizens.DD.CONFIG;


/**
 * 
 * @author Mark Gottschling on Apr 15, 2022
 *
 */
public class Shadowlord extends HostileEntity {
	private static final int SUN_BURN_SECONDS = 2;
	protected static final double MELEE_DISTANCE_SQUARED = 16D;
	protected static final double SUMMON_DISTANCE_SQUARED = 1024D;
	protected static final double SHOOT_DISTANCE_SQUARED = 4096D;
	protected static final int SUMMON_CHARGE_TIME = 2400;

	private double auraOfBlindessTime;
	private int numSummonDaemons;


	/**
	 *
	 * @param entityType
	 * @param level
	 */
	public Shadowlord(EntityType<? extends HostileEntity> entityType, World level) {
		super(entityType, level);
		setPersistent();
		this.experiencePoints = 8;
	}

	/**
	 * 
	 */
	protected void initGoals() {
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(2, new AvoidSunlightGoal(this));
		this.goalSelector.add(3, new EscapeSunlightGoal(this, 1.0));

				
//		this.goalSelector.add(4, new ShadowlordShootHarmGoal(this, Config.Mobs.SHADOWLORD.harmChargeTime.get()));
		this.goalSelector.add(4, new ShadowlordSummonGoal(this, CONFIG.shadowlordConfig().summonCooldownTime, true));
		// TODO update with strafing movement - see RangedBowAttackGoal
		//		this.goalSelector.addGoal(5, new ShadowlordMeleeAttackGoal(this, 1.0D, false));
		this.goalSelector.add(5, new MeleeAttackGoal(this, 1.1D, false));
		
		this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0D));
		this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 16.0F));
		this.goalSelector.add(7, new LookAroundGoal(this));


		this.targetSelector.add(1, new RevengeGoal(this));
//		this.targetSelector.add(2, new ActiveTargetGoal<>(this, Boulder.class, true, (entity) -> {
//			return (entity instanceof Boulder) && ((Boulder)entity).isActive();
//		}));
		this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
	}

	public static DefaultAttributeContainer.Builder createAttributes() {
		return HostileEntity.createHostileAttributes()
				.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0D)
				.add(EntityAttributes.GENERIC_MAX_HEALTH, 50.0D)
				.add(EntityAttributes.GENERIC_ARMOR, 10.0D)
				.add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 1.0D)
				.add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.8)
				.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2F)
				.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 80D);
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.UNDEAD;
	}

	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {

		if (world.getDifficulty() == Difficulty.NORMAL) {
			numSummonDaemons = 1;
		}
		else if (world.getDifficulty() == Difficulty.HARD) {
			numSummonDaemons = 2;
		}
		else {
			numSummonDaemons = 0;
		}
		return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
	}

	public static boolean checkShadowlordSpawnRules(EntityType<Shadowlord> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, net.minecraft.util.math.random.Random random) {
		if (world.getDimension().effects().equals(DimensionTypes.THE_NETHER_ID)) {
	//		return checkDDNetherSpawnRules(mob, level, spawnType, pos, random);
			return true;
		}
		else {
			//		return checkDDSpawnRules(mob, level, spawnType, pos, random);
			return true;
		}
	}

	/**
	 * Only executed on server side.
	 * @param entity
	 * @param amount
	 */
	public void drain(LivingEntity entity, float amount) {
		// add damage to Shadowlord's health
		DD.LOGGER.debug("draining {} hp from player", amount);
		setHealth(Math.min(getMaxHealth(), getHealth() + amount));
		if (world.isClient()) {
			double d0 = this.getX();
			double d1 = this.getY() + 0.2;
			double d2 = this.getZ();
			for (int p = 0; p < 20; p++) {
				double xSpeed = random.nextGaussian() * 0.02D;
				double ySpeed = random.nextGaussian() * 0.02D;
				double zSpeed = random.nextGaussian() * 0.02D;
				this.world.addParticle(ParticleTypes.SOUL, d0, d1, d2, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	public void tickMovement() {
		/*
		 * Create a ring of smoke particles to delineate the boundary of the Aura of Blindness 
		 */
		if (world.isClient()) {
			double x = 2D * Math.sin(auraOfBlindessTime);
			double z = 2D * Math.cos(auraOfBlindessTime);
			this.world.addParticle(ParticleTypes.SMOKE, this.getX() + x, this.getY(), this.getZ() + z, 0, 0, 0);
			auraOfBlindessTime++;
			auraOfBlindessTime = auraOfBlindessTime % 360;
		}

		/*
		 * Apply Aura of Blindness to Players
		 */
		// get all entities with radius
		double distance = 2;
		Box box = new Box(getBlockPos()).expand(distance);
		List<? extends PlayerEntity> list = this.getWorld().getEntitiesByClass(PlayerEntity.class, box, e -> !e.isSpectator());
		for (PlayerEntity target : list) {
			// test if player is wearing golden helmet
			ItemStack helmetStack = target.getEquippedStack(EquipmentSlot.HEAD);
			if (helmetStack.isEmpty() || helmetStack.getItem() != Items.GOLDEN_HELMET) {
				// inflict blindness
				target.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 10 * 20, 0), this);
			}
		}

		// set on fire if in sun
		boolean flag = this.isAffectedByDaylight();
		if (flag) {
			this.setOnFireFor(4);
		}
		super.tickMovement();
	}

	/**
	 * 
	 */
	@Override
	public boolean tryAttack(Entity target) {
		if (super.tryAttack(target)) {
			if (target instanceof PlayerEntity) {
				int seconds = 10;
				// inflict poison
				((LivingEntity)target).addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, seconds * 20, 0), this);
			}
			return true;
		} 
		return false;
	}

	/**
	 * 
	 */
	@Override
	public boolean damage(DamageSource damageSource, float amount) {
		if (world.isClient()) {
			return false;
		}

		if (damageSource.getSource() != null && damageSource.getSource() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity)damageSource.getSource();
			ItemStack heldStack = ((PlayerEntity)damageSource.getSource()).getStackInHand(Hand.MAIN_HAND);

			if (!heldStack.isEmpty() && heldStack.getItem() == Items.GOLDEN_SWORD) {
				// increase damage to that of iron sword
				amount += 2.0F;
				// negate the weakness from the strike power of the sword
				if (player.hasStatusEffect(StatusEffects.WEAKNESS)) {
					amount += StatusEffects.WEAKNESS.adjustModifierAmount(0, null);
				}
			}
			// TODO add shadow sword condition
			else {
				amount = 1.0F;
			}
			DD.LOGGER.debug("new gold sword strike amount -> {}", amount);
		}

		return super.damage(damageSource, amount);
	}

	/**
	 * Fires harmball at target every 4 secs.
	 * @author Mark Gottschling on Apr 19, 2022
	 *
	 */
	static class ShadowlordShootHarmGoal extends Goal {
		private static final int DEFAULT_CHARGE_TIME = 50;
		private final Shadowlord shadowlord;
		public int maxChargeTime;
		public int chargeTime;

		public ShadowlordShootHarmGoal(Shadowlord mob) {
			this(mob, DEFAULT_CHARGE_TIME);			
		}

		public ShadowlordShootHarmGoal(Shadowlord mob, int maxChargeTime) {
			this.shadowlord = mob;
			this.maxChargeTime = maxChargeTime;
		}

		@Override
		public boolean canStart() {
			return this.shadowlord.getTarget() != null; // && !(Shadowlord.MELEE_DISTANCE_SQUARED >= this.shadowlord.distanceToSqr(shadowlord.getTarget().getX(), shadowlord.getTarget().getY(), shadowlord.getTarget().getZ()));
		}

		@Override
		public void start() {
			this.chargeTime = 0;
		}

		@Override
		public void stop() {
		}

		@Override
		public boolean shouldRunEveryTick() {
			return true;
		}

		@Override
		public void tick() {
			LivingEntity livingentity = this.shadowlord.getTarget();
			if (livingentity != null) {
				if (livingentity.squaredDistanceTo(this.shadowlord) < SHOOT_DISTANCE_SQUARED && this.shadowlord.canSee(livingentity)
						&& livingentity.squaredDistanceTo(this.shadowlord) > Shadowlord.MELEE_DISTANCE_SQUARED) {

					World level = this.shadowlord.world;
					++this.chargeTime;

					if (this.chargeTime >= maxChargeTime) {
						Vec3d vec3d = this.shadowlord.getRotationVec(1.0f);
						double f = livingentity.getX() - (this.shadowlord.getX() + vec3d.x * 2.0);
						double g = livingentity.getBodyY(0.5) - (0.5 + this.shadowlord.getBodyY(0.5));
						double h = livingentity.getZ() - (this.shadowlord.getZ() + vec3d.z * 2.0);


//						Harmball spell = new Harmball(Registration.HARMBALL_ENTITY_TYPE.get(), level);
//						spell.init(this.shadowlord, x, y, z);
//						spell.setPos(this.shadowlord.getX() + vec3.x * 2.0D, this.shadowlord.getY(0.5D), spell.getZ() + vec3.z * 2.0);
//						level.addFreshEntity(spell);
						this.chargeTime = 0;
					}
				} else if (this.chargeTime > 0) {
					--this.chargeTime;
				}

			}
		}

		// this is part of shoot harm spell - this isn't called
		// this is part of MeleeGoal
//		protected double getSquaredMaxAttackDistance(LivingEntity entity) {
//			return (double)(this.shadowlord.getWidth() * 2.0F * this.shadowlord.getWidth() * 2.0F + entity.getWidth() + 0.5F);
//		}
	}

	/**
	 * 
	 * @author Mark Gottschling on Apr 19, 2022
	 *
	 */
	static class ShadowlordSummonGoal extends SummonGoal {
		private final Shadowlord shadowlord;
		private final Random random;

		public ShadowlordSummonGoal(Shadowlord shadowlord) {
			this(shadowlord, SUMMON_CHARGE_TIME, true);
		}

		/**
		 * 
		 * @param mob
		 * @param summonCooldownTime
		 * @param canSummonDaemon
		 */
		public ShadowlordSummonGoal(Shadowlord mob, int summonCooldownTime, boolean canSummonDaemon) {
			super(summonCooldownTime);
			this.shadowlord = mob;			
			this.random = new Random();
		}

		@Override
		public void start() {
			this.cooldownTime = summonCooldownTime / 2;
		}

		@Override
		public void stop() {
		}

		@Override
		public void tick() {
			LivingEntity target = this.shadowlord.getTarget();
			if (target != null) {
				if (target.squaredDistanceTo(this.shadowlord) < SUMMON_DISTANCE_SQUARED && this.shadowlord.canSee(target)) {
					World level = this.shadowlord.getWorld();
					++this.cooldownTime;

					if (this.cooldownTime >= summonCooldownTime) {

						int y = shadowlord.getBlockPos().getY();
						boolean spawnSuccess = false;
						// summon daemon is health < max/3
						if (shadowlord.getHealth() < shadowlord.getMaxHealth() / 3 && shadowlord.numSummonDaemons > 0) {
							spawnSuccess = spawn((ServerWorld) level, level.getRandom(), shadowlord, Registration.DAEMON, new Coords(shadowlord.getBlockPos().getX(), y + 1, shadowlord.getBlockPos().getZ()), target);
							if (spawnSuccess) {
								shadowlord.numSummonDaemons--;
							}
						}
						else {
							int numSpawns = random.nextInt(CONFIG.shadowlordConfig().minSummonSpawns, CONFIG.shadowlordConfig().maxSummonSpawns + 1);
							for (int i = 0; i < numSpawns; i++) {
								EntityType<? extends MobEntity> mob;
								if (RandomHelper.checkProbability(random, 50)) {
									mob = Registration.SHADOW;
								}
								else {
									mob = Registration.GHOUL;
								}
								spawnSuccess |=spawn((ServerWorld)level, level.getRandom(), shadowlord, mob, new Coords(shadowlord.getBlockX(), y + 1, shadowlord.getBlockZ()), target);
							}
						}
						if (!level.isClient() && spawnSuccess) {
							for (int p = 0; p < 20; p++) {
								double xSpeed = random.nextGaussian() * 0.02D;
								double ySpeed = random.nextGaussian() * 0.02D;
								double zSpeed = random.nextGaussian() * 0.02D;
								level.addParticle(ParticleTypes.POOF, shadowlord.getBlockX(), shadowlord.getBlockY(), shadowlord.getBlockZ(), xSpeed, ySpeed, zSpeed);
							}
						}
						this.cooldownTime = 0;
					}
				}
			}
		}

		@Override
		public boolean canStart() {
			return true;
		}
	}

	/**
	 * 
	 * @author Mark Gottschling on Apr 19, 2022
	 *
	 */
	@Deprecated
	public static class ShadowlordMeleeAttackGoal extends MeleeAttackGoal {

		public ShadowlordMeleeAttackGoal(PathAwareEntity mob, double walkSpeedModifier, boolean sprintSpeedModifier) {
			super(mob, walkSpeedModifier, sprintSpeedModifier);
		}

		/**
		 * 
		 */
		@Override
		public boolean canStart() {
			if (mob.getTarget() != null) {
				if (mob.squaredDistanceTo(mob.getTarget().getX(), mob.getTarget().getY(), mob.getTarget().getZ()) <= 100D) {
					boolean x = super.canStart();
					DD.LOGGER.debug("can use melee at distance {} -> {}", mob.squaredDistanceTo(mob.getTarget().getX(), mob.getTarget().getY(), mob.getTarget().getZ()), x);
					return x;
				}
			}
			return false;
		}

		@Override
		public boolean shouldContinue() {
			if (mob.getTarget() != null) {
				if (mob.squaredDistanceTo(mob.getTarget().getX(), mob.getTarget().getY(), mob.getTarget().getZ()) <= 100D) {
					boolean x = super.shouldContinue();
					DD.LOGGER.debug("can use melee at distance {} -> {}", mob.squaredDistanceTo(mob.getTarget().getX(), mob.getTarget().getY(), mob.getTarget().getZ()), x);
					return x;
				}
			}
			return false;
		}
	}
}