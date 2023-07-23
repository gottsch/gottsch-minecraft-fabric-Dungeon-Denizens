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
package mod.gottsch.fabric.ddenizens;

import mod.gottsch.fabric.ddenizens.core.config.MyConfig;
import mod.gottsch.fabric.ddenizens.core.setup.Registration;
import mod.gottsch.fabric.ddenizens.core.world.gen.ModEntitySpawn;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Mark Gottschling on 4/2/2023
 */
public class DD implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(DD.MOD_ID);

    public static final String MOD_ID = "ddenizens";

    public static final MyConfig CONFIG = MyConfig.createAndLoad();

    @Override
    public void onInitialize() {

        Registration.register();
        ModEntitySpawn.addEntitySpawn();
    }
}
