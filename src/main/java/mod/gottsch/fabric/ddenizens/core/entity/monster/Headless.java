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

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Mark Gottschling on 4/3/2023
 */
public class Headless extends HostileEntity {
    // move to abstract class
    private static final TrackedData<Integer> ANIMATION_SEED;

    static {
        ANIMATION_SEED = DataTracker.registerData(Headless.class, TrackedDataHandlerRegistry.INTEGER);
    }

    /**
     *
     * @param entityType
     * @param world
     */
    public Headless(EntityType<? extends Headless> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        Random random = new Random();
        this.dataTracker.startTracking(ANIMATION_SEED, random.nextInt(360));
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
        this.targetSelector.add(2, new HeadlessActiveTargetGoal<>(this, PlayerEntity.class, true).setGroupRevenge(Headless.class/*, Gazer.class*/));
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.5)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.5D)
                .add(EntityAttributes.GENERIC_ARMOR, 1.0D)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 1.0D)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 24.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 40.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.28F);
    }

//    @Override
    public Integer getAnimationSeed() {
        return this.dataTracker.get(ANIMATION_SEED);
    }

//    @Override
    public void setAnimationSeed(Integer seed) {
        this.dataTracker.set(ANIMATION_SEED, seed);
    }

    /**
     *
     * @param <T>
     */
    public static class HeadlessActiveTargetGoal<T extends LivingEntity> extends ActiveTargetGoal<T> {
        private static final int ALERT_RANGE_Y = 10;
        private boolean groupRevenge;
        private final List<Class<?>> noHelpTypes = new ArrayList<>();

        public HeadlessActiveTargetGoal(MobEntity mob, Class<T> targetType, boolean checkVisibility) {
            super(mob, targetType, checkVisibility);
        }

        public HeadlessActiveTargetGoal<T> setGroupRevenge(Class<?> ... noHelpTypes) {
            this.groupRevenge = true;
            this.noHelpTypes.addAll(List.of(noHelpTypes));
            return this;
        }

        @Override
        public void start() {
            this.mob.setTarget(this.target);
            if (this.targetClass == PlayerEntity.class || this.targetClass == ServerPlayerEntity.class) {
                this.callSameTypeForRevenge();
            }
            super.start();
        }

        protected void callSameTypeForRevenge() {
            double d = this.getFollowRange();
            Box box = Box.from(this.mob.getPos()).expand(d, 10.0, d);
            List<MobEntity> list = this.mob.world.getEntitiesByClass(MobEntity.class, box, EntityPredicates.EXCEPT_SPECTATOR);
            for (MobEntity mobEntity : list) {
                if (this.mob == mobEntity || mobEntity.getTarget() != null || (this.mob.getAttacker() != null && mobEntity.isTeammate(this.mob.getAttacker()))) continue;
                if (this.noHelpTypes != null) {
                    boolean bl = false;
                    for (Class<?> class_ : this.noHelpTypes) {
                        if (mobEntity.getClass() != class_) continue;
                        bl = true;
                        break;
                    }
                    if (bl) continue;
                }
                this.setMobEntityTarget(mobEntity, this.mob.getAttacker());
            }
        }

        protected void setMobEntityTarget(MobEntity mob, LivingEntity target) {

            mob.setTarget(target);
        }
    }
}
