package mod.gottsch.fabric.ddenizens.core.client.renderer.entity;

import mod.gottsch.fabric.ddenizens.DD;
import mod.gottsch.fabric.ddenizens.core.client.model.HeadlessModel;
import mod.gottsch.fabric.ddenizens.core.entity.monster.Headless;
import mod.gottsch.fabric.ddenizens.core.setup.ClientSetup;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

/*
 * A renderer is used to provide an entity model, shadow size, and texture.
 */
public class HeadlessRenderer<T extends Headless> extends MobEntityRenderer<T, HeadlessModel<T>> {

    public HeadlessRenderer(EntityRendererFactory.Context context) {
        super(context, new HeadlessModel<>(context.getPart(ClientSetup.HEADLESS_LAYER)), 0.5f);
    }

    @Override
    public Identifier getTexture(Headless entity) {
        return new Identifier(DD.MOD_ID, "textures/entity/headless.png");
    }
}
