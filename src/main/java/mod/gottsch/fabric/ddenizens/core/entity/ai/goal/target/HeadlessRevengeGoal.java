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
package mod.gottsch.fabric.ddenizens.core.entity.ai.goal.target;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.Box;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

/**
 * Almost identical to RevengeGoal except the setRevengeGroup() method functions is opposite
 * ie. instead of providing a list of included entities from the revenge group, the list provided is a list of entities
 * to exclude from the revenge group. ie all entities do revenge except ...
 * Remade entire class instead of extending because vanilla class hides some necessary properties.
 * @author Mark Gottschling on Apr 7, 2022
 *
 */
public class HeadlessRevengeGoal extends TrackTargetGoal {
	private static final TargetPredicate VALID_AVOIDABLES_PREDICATE = TargetPredicate.createAttackable().ignoreVisibility().ignoreDistanceScalingFactor();
	private static final int BOX_VERTICAL_EXPANSION = 10;
	private boolean groupRevenge;
	private int lastAttackedTime;
	private final Class<?>[] noRevengeTypes;
	@Nullable
	private Class<?>[] noHelpTypes;

	/**
	 * 
	 * @param headless the mob entity
	 * @param noRevengeTypes mob class to ignore
	 */
	public HeadlessRevengeGoal(PathAwareEntity headless, Class<?>... noRevengeTypes) {
		super(headless, true);
		this.noRevengeTypes = noRevengeTypes;
		this.setControls(EnumSet.of(Control.TARGET));
	}

	/**
	 * from vanilla
	 */
	public boolean canStart() {
		int i = this.mob.getLastAttackedTime();
		LivingEntity livingEntity = this.mob.getAttacker();
		if (i != this.lastAttackedTime && livingEntity != null) {
			if (livingEntity.getType() == EntityType.PLAYER && this.mob.world.getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER)) {
				return false;
			} else {
				Class[] var3 = this.noRevengeTypes;
				int var4 = var3.length;

				for(int var5 = 0; var5 < var4; ++var5) {
					Class<?> class_ = var3[var5];
					if (class_.isAssignableFrom(livingEntity.getClass())) {
						return false;
					}
				}

				return this.canTrack(livingEntity, VALID_AVOIDABLES_PREDICATE);
			}
		} else {
			return false;
		}
	}

	public HeadlessRevengeGoal setGroupRevenge(Class<?>... noHelpTypes) {
		this.groupRevenge = true;
		this.noHelpTypes = noHelpTypes;
		return this;
	}

	public void start() {
		this.mob.setTarget(this.mob.getAttacker());
		this.target = this.mob.getTarget();
		this.lastAttackedTime = this.mob.getLastAttackedTime();
		this.maxTimeWithoutVisibility = 300;
		if (this.groupRevenge) {
			this.callSameTypeForRevenge();
		}

		super.start();
	}


	protected void callSameTypeForRevenge() {
		double d = this.getFollowRange();
		Box box = Box.from(this.mob.getPos()).expand(d, 10.0, d);
		List<? extends MobEntity> list = this.mob.world.getEntitiesByClass(this.mob.getClass(), box, EntityPredicates.EXCEPT_SPECTATOR);
		Iterator<? extends MobEntity> var5 = list.iterator();

		while(true) {
			MobEntity mobEntity;
			boolean bl;
			do {
				do {
					do {
						do {
							do {
								if (!var5.hasNext()) {
									return;
								}

								mobEntity = (MobEntity)var5.next();
							} while(this.mob == mobEntity);
						} while(mobEntity.getTarget() != null);
					} while(this.mob instanceof TameableEntity && ((TameableEntity)this.mob).getOwner() != ((TameableEntity)mobEntity).getOwner());
				} while(mobEntity.isTeammate(this.mob.getAttacker()));

				if (this.noHelpTypes == null) {
					break;
				}

				bl = false;
				Class[] var8 = this.noHelpTypes;
				int var9 = var8.length;

				for(int var10 = 0; var10 < var9; ++var10) {
					Class<?> class_ = var8[var10];
					if (mobEntity.getClass() == class_) {
						bl = true;
						break;
					}
				}
			} while(bl);

			this.setMobEntityTarget(mobEntity, this.mob.getAttacker());
		}
	}

	protected void setMobEntityTarget(MobEntity mob, LivingEntity target) {
		mob.setTarget(target);

	}
//
//	/**
//	 *
//	 */
//	protected void alertOthers() {
//		double distance = this.getFollowDistance();
//		AABB aabb = AABB.unitCubeFromLowerCorner(this.mob.position()).inflate(distance, ALERT_RANGE_Y, distance);
//		List<? extends Mob> list = this.mob.level.getEntitiesOfClass(Mob.class, aabb, EntitySelector.NO_SPECTATORS);
//		Iterator<? extends Mob> iterator = list.iterator();
//		while (iterator.hasNext()) {
//			Mob otherMob = (Mob)iterator.next();
////			DD.LOGGER.debug("process mob alert -> {}", otherMob.getName().getString());
//			if (this.mob != otherMob && otherMob.getTarget() == null) {
//				if (this.othersToAlert.contains(otherMob.getClass())) {
//					DD.LOGGER.debug("alerting mob of targer -> {}", otherMob.getName().getString());
//					alertOther(otherMob, this.mob.getLastHurtByMob());
//				}
//			}
//		}
//	}
//
//	protected void alertOther(Mob otherMob, LivingEntity target) {
//		otherMob.setTarget(target);
//	}
}
