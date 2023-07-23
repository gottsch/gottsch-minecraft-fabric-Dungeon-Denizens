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
package mod.gottsch.fabric.ddenizens.core.setup;

import mod.gottsch.fabric.ddenizens.DD;
import mod.gottsch.fabric.ddenizens.core.client.model.GhoulModel;
import mod.gottsch.fabric.ddenizens.core.client.model.HeadlessModel;
import mod.gottsch.fabric.ddenizens.core.client.model.ShadowModel;
import mod.gottsch.fabric.ddenizens.core.client.model.ShadowlordModel;
import mod.gottsch.fabric.ddenizens.core.client.renderer.entity.GhoulRenderer;
import mod.gottsch.fabric.ddenizens.core.client.renderer.entity.HeadlessRenderer;
import mod.gottsch.fabric.ddenizens.core.client.renderer.entity.ShadowRenderer;
import mod.gottsch.fabric.ddenizens.core.client.renderer.entity.ShadowlordRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

/**
 * Created by Mark Gottschling on 4/3/2023
 */
@Environment(EnvType.CLIENT)
public class ClientSetup implements ClientModInitializer {
    public static final EntityModelLayer HEADLESS_LAYER = new EntityModelLayer(new Identifier(DD.MOD_ID, "headless"), "main");
    public static final EntityModelLayer GHOUL_LAYER = new EntityModelLayer(new Identifier(DD.MOD_ID, "ghoul"), "main");
    public static final EntityModelLayer SHADOW_LAYER = new EntityModelLayer(new Identifier(DD.MOD_ID, "shadow"), "main");
    public static final EntityModelLayer SHADOWLORD_LAYER = new EntityModelLayer(new Identifier(DD.MOD_ID, "shadowlord"), "main");

    @Override
    public void onInitializeClient() {

        // renderers
        EntityRendererRegistry.register(Registration.HEADLESS, HeadlessRenderer::new);
        EntityRendererRegistry.register(Registration.GHOUL, GhoulRenderer::new);
        EntityRendererRegistry.register(Registration.SHADOW, ShadowRenderer::new);
        EntityRendererRegistry.register(Registration.SHADOWLORD, ShadowlordRenderer::new);

        // models
        EntityModelLayerRegistry.registerModelLayer(HEADLESS_LAYER, HeadlessModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(GHOUL_LAYER, GhoulModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(SHADOW_LAYER, ShadowModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(SHADOWLORD_LAYER, ShadowlordModel::getTexturedModelData);
    }
}
