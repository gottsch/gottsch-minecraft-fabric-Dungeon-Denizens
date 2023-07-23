package mod.gottsch.fabric.ddenizens.core.client.renderer.entity;

import mod.gottsch.fabric.ddenizens.DD;
import mod.gottsch.fabric.ddenizens.core.client.model.ShadowModel;
import mod.gottsch.fabric.ddenizens.core.entity.monster.Shadow;
import mod.gottsch.fabric.ddenizens.core.setup.ClientSetup;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

/*
 * A renderer is used to provide an entity model, shadow size, and texture.
 */
public class ShadowRenderer<T extends Shadow> extends MobEntityRenderer<T, ShadowModel<T>> {

    public ShadowRenderer(EntityRendererFactory.Context context) {
        super(context, new ShadowModel<>(context.getPart(ClientSetup.SHADOW_LAYER)), 0f);
    }

    @Override
    public Identifier getTexture(Shadow entity) {
        return new Identifier(DD.MOD_ID, "textures/entity/shadow.png");
    }
}
