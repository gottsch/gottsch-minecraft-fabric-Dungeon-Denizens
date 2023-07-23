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
package mod.gottsch.fabric.ddenizens.core.client.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.HostileEntity;

public class ShadowlordModel<T extends HostileEntity> extends EntityModel<T> {
	private final ModelPart shadowlord;
	private final ModelPart head;
//	private final ModelPart left_hood_r1;
//	private final ModelPart right_hood_r1;
//	private final ModelPart body;
	private final ModelPart leftArm;
	private final ModelPart rightArm;
//	private final ModelPart cloak;
	private final ModelPart leftFrontCloak;
	private final ModelPart leftBackCloak;
	private final ModelPart rightFrontCloak;
	private final ModelPart rightBackCloak;


	public ShadowlordModel(ModelPart root) {
		super(RenderLayer::getEntityTranslucentCull);
		this.shadowlord = root.getChild("shadowlord");
		this.head = shadowlord.getChild("head");
		rightArm = shadowlord.getChild("right_arm");
		leftArm = shadowlord.getChild("left_arm");

		// shadowlord 4
		rightFrontCloak = shadowlord.getChild("cloak").getChild("right_front_cloak");
		leftFrontCloak = shadowlord.getChild("cloak").getChild("left_front_cloak");

		rightBackCloak = shadowlord.getChild("cloak").getChild("right_back_cloak");
		leftBackCloak = shadowlord.getChild("cloak").getChild("left_back_cloak");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData shadowlord = modelPartData.addChild("shadowlord", ModelPartBuilder.create(), ModelTransform.pivot(5.5F, 2.0F, -3.0F));

		ModelPartData head = shadowlord.addChild("head", ModelPartBuilder.create().uv(51, 44).cuboid(-4.0F, -9.3757F, -4.92F, 8.0F, 1.0F, 9.0F, new Dilation(0.0F))
		.uv(25, 37).cuboid(-5.0F, -8.3757F, -2.92F, 10.0F, 8.0F, 7.0F, new Dilation(0.0F))
		.uv(53, 38).cuboid(-5.0F, -8.3757F, -4.82F, 10.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-5.5F, -13.6243F, 2.92F));

		ModelPartData left_hood_r1 = head.addChild("left_hood_r1", ModelPartBuilder.create().uv(64, 19).cuboid(0.0F, 0.0F, -4.5F, 1.0F, 9.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -9.3757F, -0.42F, 0.0F, 0.0F, -0.0873F));

		ModelPartData right_hood_r1 = head.addChild("right_hood_r1", ModelPartBuilder.create().uv(73, 0).cuboid(-1.0F, 0.0F, -4.5F, 1.0F, 9.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, -9.3757F, -0.42F, 0.0F, 0.0F, 0.0873F));

		ModelPartData body = shadowlord.addChild("body", ModelPartBuilder.create().uv(25, 53).cuboid(-4.0F, -36.0F, 0.0F, 8.0F, 14.0F, 6.0F, new Dilation(0.0F))
		.uv(76, 19).cuboid(3.5F, -36.0F, 1.0F, 5.0F, 4.0F, 4.0F, new Dilation(0.1F))
		.uv(17, 74).cuboid(-8.5F, -36.0F, 1.0F, 5.0F, 4.0F, 4.0F, new Dilation(0.1F)), ModelTransform.pivot(-5.5F, 22.0F, 0.0F));

		ModelPartData left_arm = shadowlord.addChild("left_arm", ModelPartBuilder.create().uv(27, 0).cuboid(-6.5884F, -0.1369F, -2.0F, 9.0F, 32.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.6052F, -10.1531F, 3.0F, 0.0F, 0.0F, -0.1309F));

		ModelPartData right_arm = shadowlord.addChild("right_arm", ModelPartBuilder.create().uv(0, 0).cuboid(-2.4116F, -0.1369F, -2.0F, 9.0F, 32.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-11.5943F, -10.3188F, 3.0F, 0.0F, 0.0F, 0.1309F));

		ModelPartData cloak = shadowlord.addChild("cloak", ModelPartBuilder.create().uv(0, 37).cuboid(-4.0F, -22.0F, 1.0F, 8.0F, 22.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-5.5F, 22.0F, 0.0F));

		ModelPartData left_front_cloak = cloak.addChild("left_front_cloak", ModelPartBuilder.create().uv(71, 55).cuboid(-2.0F, 0.0F, 0.0F, 4.0F, 22.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(2.0F, -22.0F, 0.0F));

		ModelPartData left_back_cloak = cloak.addChild("left_back_cloak", ModelPartBuilder.create().uv(0, 64).cuboid(-2.0F, 0.0F, -4.0F, 4.0F, 22.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(2.0F, -22.0F, 6.0F));

		ModelPartData right_front_cloak = cloak.addChild("right_front_cloak", ModelPartBuilder.create().uv(54, 0).cuboid(-2.0F, 0.0F, -0.5F, 4.0F, 22.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, -22.0F, 0.5F));

		ModelPartData right_back_cloak = cloak.addChild("right_back_cloak", ModelPartBuilder.create().uv(54, 55).cuboid(-2.0F, 0.0F, -3.5F, 4.0F, 22.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, -22.0F, 5.5F));
		return TexturedModelData.of(modelData, 128, 128);
	}
	@Override
	public void setAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		animateHead(entity,ageInTicks, netHeadYaw, headPitch);

		// arms swing
		float armSpeed = 0.025F;
		float radians = 0.1F;
		this.rightArm.pitch = (float) (Math.cos(limbSwing * armSpeed) * radians * 1.4F * limbSwingAmount);
		this.leftArm.pitch = (float) (Math.cos(limbSwing * armSpeed + (float)Math.PI) * radians * 1.4F * limbSwingAmount);

		// bobs
		float speed = 0.05F; // 1/20th speed

		rightFrontCloak.pitch = (float) Math.min(0, -1.0F * (Math.sin(ageInTicks * speed) *  0.125F));
		leftFrontCloak.pitch = (float) Math.min(0, -1.0F * (Math.cos(ageInTicks * speed) *  0.125F));

		rightBackCloak.pitch = (float) Math.max(0, (Math.cos(ageInTicks * speed) *  0.125F));
		leftBackCloak.pitch = (float) Math.max(0, (Math.sin(ageInTicks * speed) *  0.125F));

		// sway arms
		leftArm.roll = (float) (Math.cos(ageInTicks * armSpeed) *  0.1F);
		rightArm.roll = (float) (Math.cos(ageInTicks * armSpeed) *  0.1F);
	}

	public void animateHead(T entity, float ageInTicks, float netHeadYaw, float headPitch) {
		this.head.yaw = netHeadYaw * ((float)Math.PI / 180F);
		this.head.pitch = headPitch * ((float)Math.PI / 180F);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		shadowlord.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}