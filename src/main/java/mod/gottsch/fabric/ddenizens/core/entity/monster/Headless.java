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

import mod.gottsch.fabric.ddenizens.core.entity.ai.goal.target.HeadlessRevengeGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark Gottschling on 4/3/2023
 */
public class Headless extends HostileEntity {

    protected Headless(EntityType<? extends Headless> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));

        /*
         * executes attack behaviour
         * this = pathfinding mob, 1.0 = speed modifier, false = following even if not seen
         */
        this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(6, new LookAroundGoal(this));

        /*
         * 6 = max distance, 1 = walk speed modifier, 1.2 spring speed modifier
         */
        this.goalSelector.add(3, new FleeEntityGoal<>(this, CreeperEntity.class, 6.0F, 1.0D, 1.2D));

        // target goals
        // for the included list. ie this mob will alert the other specific listed mobs.
        this.targetSelector.add(1, (new RevengeGoal(this, Headless.class)).setGroupRevenge(Headless.class/*, Gazer.class*/));

        /*
         * determines who to target
         * this = pathfinding mob, Player = target, true = must see entity in order to target
         */
        this.targetSelector.add(2, new HeadlessNearestAttackableTargetGoal<>(this, PlayerEntity.class, true).setGroupRevenge(Headless.class/*, Gazer.class*/));
    }



    /**
     *
     * @param <T>
     */
    public static class HeadlessNearestAttackableTargetGoal<T extends LivingEntity> extends ActiveTargetGoal<T> {
        private static final int ALERT_RANGE_Y = 10;
        private List<Class<?>> othersToAlert = new ArrayList<>();

        /**
         *
         * @param mob
         * @param targetType
         * @param mustSee
         */
        public HeadlessNearestAttackableTargetGoal(Mob mob, Class<T> targetType, boolean mustSee) {
            super(mob, targetType, mustSee);
        }

        /**
         *
         * @param toAlert
         * @return
         */
        public HeadlessNearestAttackableTargetGoal<?> setAlertOthers(Class<?>... toAlert) {
            this.othersToAlert.addAll(Arrays.asList(toAlert));
            return this;
        }

        @Override
        public void start() {
            this.mob.setTarget(this.target);
            if (this.targetType == Player.class || this.targetType == ServerPlayer.class) {
                this.alertOthers();
            }
            super.start();
        }

        protected void alertOthers() {
            double distance = this.getFollowDistance();
            AABB aabb = AABB.unitCubeFromLowerCorner(this.mob.position()).inflate(distance, ALERT_RANGE_Y, distance);
            List<? extends Mob> list = this.mob.level.getEntitiesOfClass(Mob.class, aabb, EntitySelector.NO_SPECTATORS);
            Iterator<? extends Mob> iterator = list.iterator();
            while (iterator.hasNext()) {
                Mob otherMob = (Mob)iterator.next();
                if (this.mob != otherMob && otherMob.getTarget() == null) {
                    if (this.othersToAlert.contains(otherMob.getClass())) {
                        alertOther(otherMob, this.mob.getLastHurtByMob());
                    }
                }
            }
        }

        protected void alertOther(Mob otherMob, LivingEntity target) {
            otherMob.setTarget(target);
        }
    }

}
