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
package mod.gottsch.fabric.ddenizens.core.client.renderer.entity;

import mod.gottsch.fabric.ddenizens.DD;
import mod.gottsch.fabric.ddenizens.core.client.model.ShadowlordModel;
import mod.gottsch.fabric.ddenizens.core.entity.monster.Shadowlord;
import mod.gottsch.fabric.ddenizens.core.setup.ClientSetup;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

/*
 * A renderer is used to provide an entity model, shadow size, and texture.
 */
public class ShadowlordRenderer<T extends Shadowlord> extends MobEntityRenderer<T, ShadowlordModel<T>> {

    public ShadowlordRenderer(EntityRendererFactory.Context context) {
        super(context, new ShadowlordModel<>(context.getPart(ClientSetup.SHADOWLORD_LAYER)), 0f);
    }

    @Override
    public Identifier getTexture(Shadowlord entity) {
        return new Identifier(DD.MOD_ID, "textures/entity/shadowlord.png");
    }
}
