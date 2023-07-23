package mod.gottsch.fabric.ddenizens.core.client.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.HostileEntity;

public class GhoulModel<T extends HostileEntity> extends DDModel<T> {
	private final ModelPart head;
	private final ModelPart torso;
//	private final ModelPart lower_body;
	private final ModelPart body;
	private final ModelPart leftArm;
//	private final ModelPart left_lower_arm;
//	private final ModelPart left_hand;
//	private final ModelPart left_top_hand;
//	private final ModelPart left_thumb_r1;
	private final ModelPart rightArm;
//	private final ModelPart right_lower_arm;
//	private final ModelPart right_hand;
//	private final ModelPart right_top_hand;
//	private final ModelPart right_thumb_r1;
	private final ModelPart leftLeg;
//	private final ModelPart left_lower_leg_r1;
//	private final ModelPart left_leg_r1;
//	private final ModelPart left_toes;
	private final ModelPart rightLeg;
//	private final ModelPart right_lower_leg_r1;
//	private final ModelPart right_leg_r1;
//	private final ModelPart right_toes;

	private final float bodyY;
	private final float rightArmX;
	private final float leftArmX;

	/**
	 *
	 * @param root
	 */
	public GhoulModel(ModelPart root) {
		this.head = root.getChild("head");
		this.torso = root.getChild("torso");
		this.body = torso.getChild("body");
		this.leftArm =body.getChild("left_arm");
		this.rightArm = body.getChild("right_arm");
		this.leftLeg = root.getChild("left_leg");
		this.rightLeg = root.getChild("right_leg");

		bodyY = body.pivotY;
		rightArmX = rightArm.pivotX;
		leftArmX = leftArm.pivotX;
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -4.0F, -3.0F, 6.0F, 8.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.0F, -6.0F, 0.3054F, 0.0F, 0.0F));

		ModelPartData torso = modelPartData.addChild("torso", ModelPartBuilder.create(), ModelTransform.of(0.0F, 7.0F, 0.0F, 0.3054F, 0.0F, 0.0F));

		ModelPartData lower_body = torso.addChild("lower_body", ModelPartBuilder.create().uv(21, 24).cuboid(-3.0F, -2.5F, -2.0F, 6.0F, 5.0F, 4.0F, new Dilation(0.0F))
				.uv(0, 0).cuboid(-0.5F, -1.5F, 1.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(0, 0).cuboid(-0.5F, 0.5F, 1.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 5.5F, 1.0F));

		ModelPartData innards_r1 = lower_body.addChild("innards_r1", ModelPartBuilder.create().uv(22, 25).cuboid(-2.0F, -3.0F, -1.5F, 6.0F, 6.0F, 3.0F, new Dilation(-0.1F)), ModelTransform.of(-1.0F, -2.5F, 0.0F, 0.1745F, 0.0F, 0.0F));

		ModelPartData body = torso.addChild("body", ModelPartBuilder.create().uv(0, 15).cuboid(-4.0F, -4.0F, -2.0F, 8.0F, 8.0F, 4.0F, new Dilation(0.0F))
				.uv(0, 0).cuboid(-0.5F, -3.0F, 1.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(0, 0).cuboid(-0.5F, -1.0F, 1.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(0, 0).cuboid(-0.5F, 1.0F, 1.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(0, 0).cuboid(-0.5F, 3.0F, 1.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.3491F, 0.0F, 0.0F));

		ModelPartData left_arm = body.addChild("left_arm", ModelPartBuilder.create().uv(13, 34).cuboid(-1.5F, -1.75F, -1.5F, 3.0F, 6.0F, 3.0F, new Dilation(0.0F))
				.uv(26, 34).cuboid(-1.0F, 3.25F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(-0.05F)), ModelTransform.of(5.5F, -2.25F, -0.5F, -1.4399F, 0.0F, 0.0F));

		ModelPartData left_lower_arm = left_arm.addChild("left_lower_arm", ModelPartBuilder.create().uv(0, 28).cuboid(-1.5F, 1.25F, -1.0F, 3.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 4.25F, 0.0F, -0.8727F, 0.0F, 0.0F));
		ModelPartData left_hand = left_arm.addChild("left_hand", ModelPartBuilder.create(), ModelTransform.pivot(-5.5F, 22.25F, 0.5F));
		ModelPartData left_top_hand = left_hand.addChild("left_top_hand", ModelPartBuilder.create().uv(35, 18).cuboid(5.9F, -14.75F, -8.75F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F))
				.uv(0, 3).cuboid(5.9F, -14.0F, -8.75F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(35, 18).cuboid(4.1F, -14.75F, -8.75F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F))
				.uv(0, 3).cuboid(4.1F, -14.0F, -8.75F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData left_thumb_r1 = left_top_hand.addChild("left_thumb_r1", ModelPartBuilder.create().uv(35, 8).cuboid(-0.5F, -0.5F, -2.5F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(3.8F, -12.35F, -5.25F, 0.3491F, 0.0F, 0.0F));
		ModelPartData right_arm = body.addChild("right_arm", ModelPartBuilder.create().uv(13, 34).cuboid(-1.5F, -1.75F, -1.5F, 3.0F, 6.0F, 3.0F, new Dilation(0.0F))
				.uv(26, 34).cuboid(-1.0F, 3.25F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(-0.05F)), ModelTransform.of(-5.5F, -2.25F, -0.5F, -1.309F, 0.0F, 0.0F));

		ModelPartData right_lower_arm = right_arm.addChild("right_lower_arm", ModelPartBuilder.create().uv(0, 28).cuboid(-1.5F, 1.25F, -1.0F, 3.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 4.25F, 0.0F, -0.8727F, 0.0F, 0.0F));
		ModelPartData right_hand = right_arm.addChild("right_hand", ModelPartBuilder.create(), ModelTransform.pivot(-5.5F, 22.25F, 0.5F));
		ModelPartData right_top_hand = right_hand.addChild("right_top_hand", ModelPartBuilder.create().uv(35, 18).cuboid(5.9F, -14.75F, -8.75F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F))
				.uv(0, 3).cuboid(5.9F, -14.0F, -8.75F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
				.uv(35, 18).cuboid(4.1F, -14.75F, -8.75F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F))
				.uv(0, 3).cuboid(4.1F, -14.0F, -8.75F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData right_thumb_r1 = right_top_hand.addChild("right_thumb_r1", ModelPartBuilder.create().uv(35, 8).cuboid(2.9F, -0.5F, -2.5F, 1.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(3.8F, -12.35F, -5.25F, 0.3491F, 0.0F, 0.0F));
		ModelPartData left_leg = modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(35, 34).cuboid(-1.0F, 3.8285F, -5.0601F, 2.0F, 2.0F, 2.0F, new Dilation(0.3F)), ModelTransform.of(2.25F, 12.5215F, 2.8601F, 0.5236F, 0.0F, 0.0F));
		ModelPartData left_lower_leg_r1 = left_leg.addChild("left_lower_leg_r1", ModelPartBuilder.create().uv(25, 11).cuboid(-1.75F, -5.0F, -4.5F, 3.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.25F, 8.4785F, 0.6399F, 0.48F, 0.0F, 0.0F));
		ModelPartData left_leg_r1 = left_leg.addChild("left_leg_r1", ModelPartBuilder.create().uv(25, 0).cuboid(-1.75F, 0.0F, -1.5F, 3.0F, 7.0F, 3.0F, new Dilation(0.1F)), ModelTransform.of(0.25F, -0.5215F, 0.6399F, -0.7854F, 0.0F, 0.0F));
		ModelPartData left_toes = left_leg.addChild("left_toes", ModelPartBuilder.create().uv(13, 28).cuboid(-1.35F, -0.5F, -2.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
				.uv(13, 28).cuboid(0.35F, -0.5F, -2.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 10.9785F, -2.6101F));

		ModelPartData right_leg = modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(35, 34).cuboid(-1.5F, 3.8285F, -5.0601F, 2.0F, 2.0F, 2.0F, new Dilation(0.3F)), ModelTransform.of(-1.75F, 12.5215F, 2.8601F, -0.1309F, 0.0F, 0.0F));
		ModelPartData right_lower_leg_r1 = right_leg.addChild("right_lower_leg_r1", ModelPartBuilder.create().uv(25, 11).cuboid(-2.25F, -5.0F, -4.5F, 3.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.25F, 8.4785F, 0.6399F, 0.48F, 0.0F, 0.0F));
		ModelPartData right_leg_r1 = right_leg.addChild("right_leg_r1", ModelPartBuilder.create().uv(25, 0).cuboid(-2.25F, 0.0F, -1.5F, 3.0F, 7.0F, 3.0F, new Dilation(0.1F)), ModelTransform.of(0.25F, -0.5215F, 0.6399F, -0.7854F, 0.0F, 0.0F));
		ModelPartData right_toes = right_leg.addChild("right_toes", ModelPartBuilder.create().uv(13, 28).cuboid(-1.85F, -0.5F, -2.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F))
				.uv(13, 28).cuboid(-0.15F, -0.5F, -2.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 10.9785F, -2.6101F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void setAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		float legRadians = 0.6F;
		float walkSpeed = 0.5F; // half speed = 0.5

		float armSpeed = 0.25F;
		float armRadians = 0.5235988F; // 30

		animateHead(entity, ageInTicks, netHeadYaw, headPitch);
		animateLegs(entity, limbSwing, limbSwingAmount, legRadians, walkSpeed);
		animateArms(entity, limbSwing, limbSwingAmount, armRadians, armSpeed);

		setupAttackAnimation(entity, ageInTicks);

		// bob the body
		body.pivotY = (float) (bodyY + (Math.cos(ageInTicks * 0.10F) * 0.45F + 0.05F));

		// reset arm rotations before bobbing, because bobbing is an addition to current rotation
		this.leftArm.roll = 0;
		this.rightArm.roll = 0;

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
		this.head.pitch = headPitch * ((float)Math.PI / 180F) + 0.3054326F; // +17.5 degrees because has a slight down angle
	}

	@Override
	public void animateArms(T entity, float limbSwing, float limbSwingAmount, float radians, float armSpeed) {
		this.rightArm.pitch = (float) (-1.309F + Math.cos(limbSwing * armSpeed) * radians * 1.4F * limbSwingAmount);
		this.leftArm.pitch = (float) (-1.4399F + Math.cos(limbSwing * armSpeed + (float)Math.PI) * radians * 1.4F * limbSwingAmount);
	}

	@Override
	public void animateLegs(T entity, float limbSwing, float limbSwingAmount, float radians, float walkSpeed) {
		float magicValue = getMagicValue(entity);
		this.rightLeg.pitch = (float) (Math.cos(limbSwing * walkSpeed) * radians  * 1.4F * limbSwingAmount / magicValue);
		this.leftLeg.pitch = (float) (Math.cos(limbSwing  * walkSpeed + (float)Math.PI) * radians * 1.4F * limbSwingAmount / magicValue);
		this.rightLeg.yaw = 0.0F;
		this.leftLeg.yaw = 0.0F;
		this.rightLeg.roll = 0.0F;
		this.leftLeg.roll = 0.0F;
	}

	@Override
	public void resetSwing(T entity, ModelPart body, ModelPart rightArm, ModelPart leftArm) {
		body.yaw = 0;
		rightArm.pivotX = rightArmX;
		rightArm.pitch = -1.309F;
		rightArm.roll = 0;
		rightArm.yaw = 0;
		leftArm.pivotX = leftArmX;
		leftArm.pitch = -1.4399F;
		leftArm.yaw = 0;
		leftArm.roll = 0;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		head.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		torso.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		leftLeg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		rightLeg.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
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