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
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;

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
        /*
         * setAlertOthers() is opposite of what you would expect. if a value is passed in,
         * that is the exception list, as opposed to the included list.
         * setAlertOthers() will alert ALL mobs based on class hierarchry of instanceof Mob.
         */
        // for the included list. ie this mob will alert the other specific listed mobs.
        this.targetSelector.add(1, (new HeadlessRevengeGoal(this, Headless.class)).setGroupRevenge(Headless.class/*, Gazer.class*/));

        /*
         * determines who to target
         * this = pathfinding mob, Player = target, true = must see entity in order to target
         */
        this.targetSelector.add(2, new HeadlessNearestAttackableTargetGoal<>(this, Player.class, true).setAlertOthers(Headless.class, Gazer.class));
    }
}
