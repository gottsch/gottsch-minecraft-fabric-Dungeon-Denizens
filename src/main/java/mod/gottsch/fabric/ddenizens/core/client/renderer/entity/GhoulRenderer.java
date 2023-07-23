package mod.gottsch.fabric.ddenizens.core.client.renderer.entity;

import mod.gottsch.fabric.ddenizens.DD;
import mod.gottsch.fabric.ddenizens.core.client.model.GhoulModel;
import mod.gottsch.fabric.ddenizens.core.entity.monster.Ghoul;
import mod.gottsch.fabric.ddenizens.core.setup.ClientSetup;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

/*
 * A renderer is used to provide an entity model, shadow size, and texture.
 */
public class GhoulRenderer<T extends Ghoul> extends MobEntityRenderer<T, GhoulModel<T>> {

    public GhoulRenderer(EntityRendererFactory.Context context) {
        super(context, new GhoulModel<>(context.getPart(ClientSetup.GHOUL_LAYER)), 0.4f);
    }

    @Override
    public Identifier getTexture(Ghoul entity) {
        return new Identifier(DD.MOD_ID, "textures/entity/ghoul.png");
    }
}
