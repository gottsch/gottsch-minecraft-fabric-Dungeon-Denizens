/**
 * 
 */
package mod.gottsch.fabric.ddenizens.core.entity.monster;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;

/**
 * @author Mark Gottschling on Apr 6, 2022
 *
 */
public class Daemon extends DDMonster {
	public static final double MELEE_DISTANCE_SQUARED = 25D;

	private double flameParticlesTime;
	private int particlesReset = 4;
	
	/**
	 * 
	 * @param entityType
	 * @param world
	 */
	public Daemon(EntityType<? extends HostileEntity> entityType, World world) {
		super(entityType, world);
		setPersistent();
		this.experiencePoints = 10;
	}

	protected void registerGoals() {
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(4, new DaemonShootSpellsGoal(this, Config.Mobs.DAEMON.firespoutCooldownTime.get()));
		this.goalSelector.add(4, new MeleeAttackGoal(this, 1.3D, false));
		this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0D));
		this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 10.0F, 0.2F));
		this.goalSelector.add(8, new LookAroundGoal(this));
		this.targetSelector.add(1, new RevengeGoal(this));
//		this.goalSelector.add(2, new FleeEntityGoal<>(this, Boulder.class, 6.0F, 1.0D, 1.2D, entity -> {
//			return (entity instanceof Boulder) && ((Boulder)entity).isActive();
//		}));
		this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
	}

	/**
	 * 
	 * @return
	 */
	public static DefaultAttributeContainer.Builder createAttributes() {
		return HostileEntity.createHostileAttributes()
				.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0D)
				.add(EntityAttributes.GENERIC_MAX_HEALTH, 80.0D)
				.add(EntityAttributes.GENERIC_ARMOR, 20.0D)
				.add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 5.0D)
				.add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.6D)
				.add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 4.5D)
				.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25D)
				.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 50D);
	}

	/**
	 * 
	 * @param mob
	 * @param level
	 * @param spawnType
	 * @param pos
	 * @param random
	 * @return
	 */
	public static boolean checkDaemonSpawnRules(EntityType<Shadow> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, net.minecraft.util.math.random.Random random) {
		if (world.getDimension().effects().equals(DimensionTypes.THE_NETHER_ID)) {
			return true;
		} else {
			return true;
		}
			//		if (level.getBiome(pos).is(BiomeTags.IS_NETHER)) {
//			return checkDDNetherSpawnRules(mob, level, spawnType, pos, random);
//		}
//		else {
//			return checkDDSpawnRules(mob, level, spawnType, pos, random);
//		}
	}

	/**
	 * 
	 */
	@Override
	public void tickMovement() {
		if (this.world.isClient() && !isInWater()) {
			for(int i = 0; i < 2; ++i) {
				this.world.addParticle(ParticleTypes.FLAME, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0.0D, 0.0D, 0.0D);
			}
			this.world.addParticle(ParticleTypes.LARGE_SMOKE, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0.0D, 0.0D, 0.0D);

			if (--particlesReset <= 0) {
					//RandomHelper.checkProbability(new Random(), 24)) {
				double x = 0.75D * Math.sin(flameParticlesTime);
				double z = 0.75D * Math.cos(flameParticlesTime);
				this.level.addParticle(ParticleTypes.FLAME, this.position().x + x, this.position().y + 0.1D, position().z + z, 0, 0, 0);
				particlesReset = 4;
			}
			flameParticlesTime++;
			flameParticlesTime = flameParticlesTime % 360;
		}
		super.tickMovement();
	}

	/**
	 * 
	 * @author Mark Gottschling on Apr 21, 2022
	 *
	 */
	static class DaemonShootSpellsGoal extends Goal {
		private static final int DEFAULT_COOLDOWN_TIME = 200;
		private final Daemon daemon;
		private int cooldownTime;
		private int maxCooldownTime;
		private boolean shootFireSpouts;
		private int maxFireSpouts;
		private int fireSpoutCount;
		private Vec3 viewVector;
		private Vec3 daemonPosition;
		private Vec3 targetPosition;
		private Double lastY;	

		public DaemonShootSpellsGoal(Daemon mob) {
			this(mob, DEFAULT_COOLDOWN_TIME);
		}

		public DaemonShootSpellsGoal(Daemon mob, int maxCooldownTime) {
			this.daemon = mob;
			this.maxCooldownTime = maxCooldownTime;
			this.maxFireSpouts = Config.Mobs.DAEMON.firespoutMaxDistance.get();
		}

		@Override
		public boolean canUse() {			
			return this.daemon.getTarget() != null;
		}
		
		@Override
		public void start() {
		}

		@Override
		public void stop() {
			this.cooldownTime = 0;
		}

		@Override
		public boolean requiresUpdateEveryTick() {
			return true;
		}

		@Override
		public void tick() {
			--this.cooldownTime;
			if (cooldownTime < 0) {
				cooldownTime = 0;
			}

			LivingEntity target = daemon.getTarget();
			if (target != null) {
				double distanceToTarget = getDistanceToTarget();
				if (distanceToTarget > MELEE_DISTANCE_SQUARED && daemon.hasLineOfSight(target)) {

					if (this.cooldownTime == 0) {
						// +6.0 gives you a 2 block buffer from the spell radius
						if (distanceToTarget < (9.0 + (maxFireSpouts * maxFireSpouts))) {
							shootFireSpouts = true;

							// save viewVector, daemon position, player position
							this.viewVector = daemon.getViewVector(1.0F);
							this.daemonPosition = new Vec3(daemon.getX(), daemon.getY(0.5), daemon.getZ());
							this.targetPosition = new Vec3(target.getX(), target.getY(0.5), target.getZ());							
							this.cooldownTime = maxCooldownTime;
							lastY = null;
						}
					}
				}
			}

			if (shootFireSpouts && daemon.level.getGameTime() % 3 == 0) {
				if (fireSpoutCount < maxFireSpouts) {
					// view vector
					Vec3 vec3 = this.viewVector;
					double offsetMultiplier = 1;
					// calculate the offset multiplier to position (2 + x) blocks away from daemon towards target
					if (Math.abs(vec3.x) > Math.abs(vec3.z)) {
						offsetMultiplier = Math.abs((2 + fireSpoutCount) /  vec3.x);
					}
					else {
						offsetMultiplier = Math.abs((2 + fireSpoutCount) / vec3.z);
					}

					// distance vector to target originating at offset
					double x = targetPosition.x - (daemonPosition.x + vec3.x * offsetMultiplier);
					double y = targetPosition.y - (daemonPosition.y);
					double z = targetPosition.z - (daemonPosition.z + vec3.z * offsetMultiplier);

					// move vector to originating position
					x = daemonPosition.x + vec3.x * offsetMultiplier;
					y = Math.floor(daemonPosition.y);
					z = daemonPosition.z + vec3.z * offsetMultiplier;

					if (lastY == null) {
						lastY = Double.valueOf(y);
					}

					// calculate Y (on ground)
					BlockPos pos = new BlockPos(Math.floor(x), y, Math.floor(z));
					BlockState state = daemon.level.getBlockState(pos);
					int count = 0;
					while (true) {
						count++;
						if (state.getMaterial().isLiquid() || daemon.level.getBlockState(pos.below()).getMaterial().isLiquid()) {
							resetShootFireSpout();
							return;
						}
						else if (state.getBlock() == Blocks.AIR) {
							if (daemon.level.getBlockState(pos.below()).getBlock() != Blocks.AIR) {
								break;
							}
							pos = pos.below();
						}
						else {
							pos = pos.above();
							if (daemon.level.getBlockState(pos).getBlock() == Blocks.AIR) {
								break;
							}							
						}
						if (count > maxFireSpouts) {
							resetShootFireSpout();
							return;
						}
						state = daemon.level.getBlockState(pos);
					}
					if (pos.getY() - lastY > 2 || lastY - pos.getY() > 2) {
						resetShootFireSpout();
						return;
					}								
					y = pos.getY();
					lastY = Double.valueOf(y);

					// caculate vector of spout ie upwards target position = y + 5.0D
					double x2 = x;
					double y2 = y + 5.0D;
					double z2 = z;

					// create the firespout and initialize
					FireSpout spell = new FireSpout(Registration.FIRESPOUT_ENTITY_TYPE.get(), daemon.level);
					spell.init(daemon, x, y, z, x2, y2, z2);

					// add the entity to the level
					daemon.level.addFreshEntity(spell);
					// add some particle effects
					((ServerLevel) daemon.level).sendParticles(ParticleTypes.LARGE_SMOKE, x, y + 0.2, z, 5, 0, 0, 0, (double)0.15F);

					// increment the count
					fireSpoutCount++;
					// turn off is max spouts are reached
					if (fireSpoutCount >= maxFireSpouts) {
						resetShootFireSpout();
					}
				}
				else {
					resetShootFireSpout();
				}
			}
		}

		public void resetShootFireSpout() {
			shootFireSpouts = false;
			fireSpoutCount = 0;
			lastY = null;
		}

		/**
		 * 
		 * @param entity
		 * @return
		 */
		protected double getAttackReachSqr(LivingEntity entity) {
			return (double)(daemon.getBbWidth() * 2.2F * daemon.getBbWidth() * 2.0F + entity.getBbWidth());
		}

		/**
		 * 
		 * @return
		 */
		public double getDistanceToTarget() {
			if (daemon.getTarget() != null) {
				return daemon.distanceToSqr(daemon.getTarget().getX(), daemon.getTarget().getY(), daemon.getTarget().getZ());
			}
			return 0;
		}
	}
}
