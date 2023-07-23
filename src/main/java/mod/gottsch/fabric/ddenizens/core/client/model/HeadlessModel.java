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

import mod.gottsch.fabric.ddenizens.core.entity.monster.Headless;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.HostileEntity;

/**
 *
 * @author Mark Gottschling on Apr 6, 2022
 *
 * @param <T>
 */
public class HeadlessModel<T extends HostileEntity> extends DDModel<T> {

	//	created solely to fulfill the contract of BipedEntityModel
	private final ModelPart head;

	private final ModelPart leftLeg;
	private final ModelPart rightLeg;
	private final ModelPart body;
	private final ModelPart upperBody;
	private final ModelPart leftArm;
	private final ModelPart leftLowerArm;
	private final ModelPart rightArm;
	private final ModelPart rightLowerArm;
	private final ModelPart loinCloth;
	private final ModelPart frontCloth;
	private final ModelPart backCloth;

	private float upperBodyY;
	private float leftArmX;
	private float rightArmX;

	private ModelPart attackArm;
	private boolean changeAttackArm;
	private long changeAttackArmTime;

	/**
	 *
	 * @param root
	 */
	public HeadlessModel(ModelPart root) {
		this.leftLeg = root.getChild("left_leg");
		this.rightLeg = root.getChild("right_leg");
		this.body = root.getChild("body");
		this.upperBody = body.getChild("upper_body");
		this.leftArm = upperBody.getChild("left_arm");
		this.rightArm = upperBody.getChild("right_arm");
		this.loinCloth = root.getChild("loin_cloth");

		this.frontCloth = loinCloth.getChild("front_cloth");
		this.backCloth = loinCloth.getChild("back_cloth");
		this.leftLowerArm = leftArm.getChild("left_lower_arm");
		this.rightLowerArm = rightArm.getChild("right_lower_arm");

		this.head = root.getChild("head");
		this.head.visible = false;

		upperBodyY = upperBody.pivotY;
		rightArmX = rightArm.pivotX;
		leftArmX = leftArm.pivotX;

		attackArm = getLeftArm();
		changeAttackArm = false;
		changeAttackArmTime = 0;
	}

	/**
	 *
	 * @return
	 */
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		ModelPartData left_leg = modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(0, 39).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(2.0F, 12.0F, 0.0F));
		ModelPartData right_leg = modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(34, 9).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, 12.0F, 0.0F));
		ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create().uv(29, 31).cuboid(-4.0F, -19.0F, -2.0F, 8.0F, 6.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		ModelPartData upper_body = body.addChild("upper_body", ModelPartBuilder.create().uv(0, 0).cuboid(-6.0F, -24.0F, -3.0F, 12.0F, 6.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		ModelPartData left_arm = upper_body.addChild("left_arm", ModelPartBuilder.create().uv(34, 42).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 6.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, -21.0F, 0.0F, 0.0F, 0.0F, -0.8727F));
		ModelPartData left_lower_arm = left_arm.addChild("left_lower_arm", ModelPartBuilder.create().uv(17, 18).cuboid(-2.0F, -2.0F, -6.0F, 4.0F, 4.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 6.0F, 0.0F));
		ModelPartData right_arm = upper_body.addChild("right_arm", ModelPartBuilder.create().uv(17, 42).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 6.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-6.0F, -21.0F, 0.0F, 0.0F, 0.0F, 0.8727F));
		ModelPartData right_lower_arm = right_arm.addChild("right_lower_arm", ModelPartBuilder.create().uv(0, 13).cuboid(-2.0F, -2.0F, -6.0F, 4.0F, 4.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 6.0F, 0.0F));
		ModelPartData loin_cloth = modelPartData.addChild("loin_cloth", ModelPartBuilder.create().uv(0, 31).cuboid(-4.5F, -14.0F, -2.5F, 9.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		ModelPartData front_cloth = loin_cloth.addChild("front_cloth", ModelPartBuilder.create().uv(44, 0).cuboid(-2.5F, 0.0F, -2.5F, 5.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -12.0F, 0.0F));
		ModelPartData back_cloth = loin_cloth.addChild("back_cloth", ModelPartBuilder.create().uv(31, 0).cuboid(-2.5F, 0.0F, 1.5F, 5.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -12.0F, 0.0F));
		// create  "non-parts" to fulfill contract in BipedEntityModel
		ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().cuboid(0F, 0.0F, 0F, 0F, 0F, 0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0F, 0.0F));

		return TexturedModelData.of(modelData, 64, 64);
	}

	// SAME as Forge public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	/**
	 * pitch = x-axis;
	 * yaw = y-axis;
	 * roll = z-axis;
	 * ==============
	 /* Notes
	 * 0.6662 radians = ~38.17 degrees.

	 * general function: cos(x * r * s) * (ease).
	 * x = position on cos wave/time (limbSwing).
	 * r = max arc in radians.
	 * s = speed. 1 = no change, 0.5 = half speed, 2 = double speed. (essentially moving further down the cos wave per n ticks).
	 * ease (1.4 * limbSwingAmount) = the easement at max swing and stand still. don't really need to touch this part.
	 * 1 radian = pi / 180;
	 * increasing 1.4 makes the arc larger.
	 * ===============
	 * limbSwing = (vanilla) value of time on the x-axis of a sine/cosine wave (not to be confused with ageInTicks).
	 * limbSwingAmount = (vanilla) determines how much of the arc to use (value = 0.0 - 1.0).
	 * radians = speed (vanilla)
	 * walkSpeed = speed
	 * 1.4 = some multiplier that vanilla figured out to use.
	 *
	 */
	@Override
	public void setAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		float armRadians = 0.5235988F;
		float legRadians = 0.6662F;

		float armSpeed = 0.25F;
		float walkSpeed = 1.0F; // half speed = 0.5

		// animate arms
		animateArms(entity, limbSwing, limbSwingAmount, armRadians, armSpeed);

		// legs
		animateLegs(entity, limbSwing, limbSwingAmount, legRadians, walkSpeed);

		// reset arm rotations before bobbing, because bobbing is an addition to current rotation
		this.leftArm.pitch = 0F;
		this.leftArm.roll = -0.8726646F; // angle unique to headless
		this.rightArm.pitch = 0F;
		this.rightArm.roll = 0.8726646F;

		setupAttackAnimation(entity, ageInTicks);

		// bob the body
		upperBody.pivotY = (float) (upperBodyY + (Math.cos(ageInTicks * 0.15F) * 0.5F + 0.05F));
		// bob the arms
		bobArm(entity, this.rightArm, ageInTicks, 1.0F);
		bobArm(entity, this.leftArm, ageInTicks, -1.0F);
	}

	@Override
	public void animateArms(T entity, float limbSwing, float limbSwingAmount, float radians, float armSpeed) {
		float magicValue = getMagicValue(entity);
		this.rightLowerArm.pitch = (float) (Math.cos(limbSwing * armSpeed) * radians * 1.4F * limbSwingAmount);
		this.leftLowerArm.pitch = (float) (Math.cos(limbSwing * armSpeed + (float)Math.PI) * radians * 1.4F * limbSwingAmount);
	}

	/**
	 *
	 * @param entity
	 * @param limbSwing
	 * @param limbSwingAmount
	 * @param radians
	 * @param walkSpeed
	 */
	@Override
	public void animateLegs(T entity, float limbSwing, float limbSwingAmount, float radians, float walkSpeed) {
		float magicValue = getMagicValue(entity);
		this.rightLeg.pitch = (float) (Math.cos((limbSwing + ((Headless)entity).getAnimationSeed()) * radians * walkSpeed) * 1.4F * limbSwingAmount / magicValue);
		this.leftLeg.pitch = (float) (Math.cos((limbSwing + ((Headless)entity).getAnimationSeed()) * radians  * walkSpeed + (float)Math.PI) * 1.4F * limbSwingAmount / magicValue);
		this.rightLeg.roll = 0.0F;
		this.leftLeg.roll = 0.0F;
		this.rightLeg.yaw = 0.0F;
		this.leftLeg.yaw = 0.0F;

		// loin cloth
		this.backCloth.pitch = Math.max(Math.max(0,  this.rightLeg.pitch), this.leftLeg.pitch);
		this.frontCloth.pitch = -this.backCloth.pitch;
	}

	/**
	 *
	 * @param part
	 * @param age
	 * @param direction
	 */
	public void bobArm(T entity, ModelPart part, float age, float direction) {
		part.roll += direction * (Math.cos((age + ((Headless)entity).getAnimationSeed()) * 0.15F) * 0.05F + 0.05F);
	}

	@Override
	public void resetSwing(T entity, ModelPart body, ModelPart rightArm, ModelPart leftArm) {
		body.yaw = 0;
		rightArm.pivotX = rightArmX;
		rightArm.roll = 0.8726646F;
		rightArm.yaw = 0;
		leftArm.pivotX = leftArmX;
		leftArm.yaw = 0;
		leftArm.roll = -rightArm.roll;
		if (this.handSwingProgress > 0) {
			if (changeAttackArm && entity.world.getTime() - changeAttackArmTime > 20) {
				if (attackArm == getRightArm()) {
					attackArm = getLeftArm();
				}
				else {
					attackArm = getRightArm();
				}
				changeAttackArm = false;
			}
		}
		else {
			if (!changeAttackArm) {
				changeAttackArm = true;
				changeAttackArmTime = entity.world.getTime();
			}
		}
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		leftLeg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		rightLeg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		body.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		loinCloth.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart getAttackArm() {
		return attackArm;
	}

	@Override
	public ModelPart getHead() {
		return head;
	}
	@Override
	public ModelPart getBody() {
		return body;
	}
	@Override
	public ModelPart getRightArm() {
		return rightArm;
	}
	@Override
	public ModelPart getLeftArm() {
		return leftArm;
	}
}