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
package mod.gottsch.fabric.ddenizens.core.client.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;

/**
 * 
 * @author Mark Gottschling on Apr 30, 2022
 *
 */
public interface IHumanlikeModel<T extends Entity> {
	public ModelPart getHead();
	public ModelPart getBody();
	public ModelPart getRightArm();
	public ModelPart getLeftArm();

	default public void animateHead(T entity, float ageInTicks, float netHeadYaw, float headPitch) {
		// do nothing
	}

	default public void animateArms(T entity, float limbSwing, float limbSwingAmount, float radians, float armSpeed) {
		// do nothing
	}

	default public void animateLegs(T entity, float limbSwing, float limbSwingAmount, float radians, float walkSpeed) {
		// do nothing
	}
}
