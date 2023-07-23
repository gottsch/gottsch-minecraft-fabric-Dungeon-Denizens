package mod.gottsch.fabric.ddenizens.core.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.Identifier;

import java.util.function.Function;

/**
 * Created by Mark Gottschling on 4/7/2023
 */
@Environment(value= EnvType.CLIENT)
public abstract class DDModel<T extends HostileEntity> extends EntityModel<T> implements IHumanlikeModel<T> {

    public DDModel() {
        super();
    }

    public DDModel(Function<Identifier, RenderLayer> renderLayerFactory) {
        super(renderLayerFactory);
    }

    public abstract void resetSwing(T entity, ModelPart body, ModelPart rightArm, ModelPart leftArm);

    @Override
    public abstract ModelPart getHead();
    public abstract ModelPart getBody();
    public abstract ModelPart getRightArm();
    public abstract ModelPart getLeftArm();

    /**
     *
     * @return
     */
    public ModelPart getAttackArm() {
        return getRightArm();
    }

    /**
     * from vanilla
     * @param entity
     * @param age
     */
    protected void setupAttackAnimation(T entity, float age) {
        resetSwing(entity, getBody(), getRightArm(), getLeftArm());
        if (!(this.handSwingProgress <= 0.0F)) {
            ModelPart attackArm = getAttackArm();
            float f = this.handSwingProgress;
            getBody().yaw = (float) (Math.sin(Math.sqrt(f) * ((float)Math.PI * 2F)) * 0.2F);
            if (attackArm == getLeftArm()) {
                getBody().yaw *= -1.0F;
            }

            getRightArm().pivotZ = (float) (Math.sin(getBody().yaw) * 5.0F);
            getRightArm().pivotX = (float) (-Math.cos(getBody().yaw) * 5.0F);
            getLeftArm().pivotZ = (float) (-Math.sin(getBody().yaw) * 5.0F);
            getLeftArm().pivotX = (float) (Math.cos(getBody().yaw) * 5.0F);
            getRightArm().yaw += getBody().yaw;
            getLeftArm().yaw += getBody().yaw;
            getLeftArm().pitch += getBody().yaw;

            f = 1.0F - this.handSwingProgress;
            f *= f;
            f *= f;
            f = 1.0F - f;
            float f1 = (float) Math.sin(f * (float)Math.PI);
            float f2 = (float) (Math.sin(this.handSwingProgress * (float)Math.PI) * -(getHead().pitch - 0.7F) * 0.75F);
            attackArm.pitch = (float)((double)attackArm.pitch - ((double)f1 * 1.2D + (double)f2));
            attackArm.yaw += getBody().yaw * 2.0F;
            attackArm.roll += Math.sin(this.handSwingProgress * (float)Math.PI) * -0.4F;
        }
    }
    
    /**
     * A value calculated in Vanilla, used in animation of arms and legs swing
     */
    public float getMagicValue(T entity) {
        boolean checkLargeRoll = ((LivingEntity)entity).getRoll() > 4;
        float magicValue = 1.0F;
        if (checkLargeRoll) {
            magicValue = (float)((Entity)entity).getVelocity().lengthSquared();
            magicValue /= 0.2f;
            magicValue *= magicValue * magicValue;
        }
        if (magicValue < 1.0f) {
            magicValue = 1.0f;
        }
        return magicValue;
    }
}
