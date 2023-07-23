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
package mod.gottsch.fabric.ddenizens.core.client.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.HostileEntity;

/**
 *
 * @param <T>
 */
public class ShadowModel<T extends HostileEntity> extends DDModel<T> {
	private final ModelPart head;
	private final ModelPart bodyLegs;
	private final ModelPart rightLeg;
	private final ModelPart leftLeg;
	private final ModelPart body;
	private final ModelPart rightArm;
	private final ModelPart leftArm;

	public ShadowModel(ModelPart root) {
		super(RenderLayer::getEntityTranslucentCull);
		this.head = root.getChild("head");
		this.bodyLegs = root.getChild("body_legs");
		this.body = bodyLegs.getChild("body");
		this.rightLeg = bodyLegs.getChild("right_leg");
		this.leftLeg = bodyLegs.getChild("left_leg");
		this.rightArm = root.getChild("right_arm");
		this.leftArm = root.getChild("left_arm");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 17).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		ModelPartData body_legs = modelPartData.addChild("body_legs", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));
		ModelPartData right_leg = body_legs.addChild("right_leg", ModelPartBuilder.create().uv(33, 0).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, 12.0F, 0.0F));
		ModelPartData left_leg = body_legs.addChild("left_leg", ModelPartBuilder.create().uv(0, 34).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(2.0F, 12.0F, 0.0F));
		ModelPartData body = body_legs.addChild("body", ModelPartBuilder.create().uv(29, 30).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		ModelPartData right_arm = modelPartData.addChild("right_arm", ModelPartBuilder.create().uv(46, 13).cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-5.0F, 2.0F, 0.0F, -0.9599F, 0.0F, 0.0F));
		ModelPartData left_arm = modelPartData.addChild("left_arm", ModelPartBuilder.create().uv(13, 47).cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(5.0F, 2.0F, 0.0F, -0.8727F, 0.0F, 0.0F));

		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void setAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		float armSpeed = 0;

		float legRadians = 0.3490659F; // 20 degrees
		float walkSpeed = 0.5F; // half speed = 0.5

		animateHead(entity,ageInTicks, netHeadYaw, headPitch);
		animateLegs(entity, limbSwing, limbSwingAmount, legRadians, walkSpeed);
		animateArms(entity, limbSwing, limbSwingAmount, ageInTicks, walkSpeed);

		setupAttackAnimation(entity, ageInTicks);

		// reset arm rotations before bobbing, because bobbing is an addition to current rotation
		this.leftArm.roll = 0F;
		this.leftArm.pitch = -0.8726646F; // 50 degrees

		this.rightArm.roll = 0F;
		this.rightArm.pitch = -0.9599311F; // 55 dgrees

		// bob the arms
		bobModelPart(this.rightArm, ageInTicks, 1.0F);
		bobModelPart(this.leftArm, ageInTicks, -1.0F);

	}

	public static void bobModelPart(ModelPart part, float age, float direction) {
		part.roll += direction * (Math.cos(age * /*0.09F*/ 0.15F) * 0.05F + 0.05F);
	}

	@Override
	public void animateHead(T entity, float ageInTicks, float netHeadYaw, float headPitch) {
		this.head.yaw = netHeadYaw * ((float)Math.PI / 180F);
		this.head.pitch = headPitch * ((float)Math.PI / 180F);
	}

	@Override
	public void animateArms(T entity, float limbSwing, float limbSwingAmount, float radians, float armSpeed) {
		// do nothing
	}

	@Override
	public void animateLegs(T entity, float limbSwing, float limbSwingAmount, float radians, float walkSpeed) {
		float magicValue = getMagicValue(entity);
		this.rightLeg.pitch = (float) (Math.cos(limbSwing * walkSpeed) * radians * 1.4F * limbSwingAmount / magicValue);
		this.leftLeg.pitch = (float) (Math.cos(limbSwing  * walkSpeed + (float)Math.PI) * radians * 1.4F * limbSwingAmount / magicValue);
	}

	@Override
	public void resetSwing(T entity, ModelPart body, ModelPart rightArm, ModelPart leftArm) {

	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		head.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		bodyLegs.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		rightArm.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		leftArm.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
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