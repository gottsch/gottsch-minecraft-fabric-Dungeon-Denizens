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
package mod.gottsch.fabric.ddenizens.core.entity.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Util;
import net.minecraft.world.World;

import mod.gottsch.fabric.ddenizens.core.setup.Registration;


/**
 * 
 * @author Mark Gottschling on Apr 19, 2022
 *
 */
public class Harmball extends ExplosiveProjectileEntity implements FlyingItemEntity {
	private static final TrackedData<ItemStack> DATA_ITEM_STACK = DataTracker.registerData(Harmball.class, TrackedDataHandlerRegistry.ITEM_STACK);

	/**
	 * 
	 * @param entityType
	 * @param level
	 */
	public Harmball(EntityType<? extends Harmball> entityType, World level) {
		super(entityType, level);
	}

	/**
	 * 
	 * @param owner
	 * @param directionX
	 * @param directionY
	 * @param directionZ
	 */
	public void init(LivingEntity owner, double directionX, double directionY, double directionZ) {
		this.refreshPositionAndAngles(owner.getX(), owner.getY(), owner.getZ(), owner.getYaw(), owner.getPitch());
		double d0 = Math.sqrt(directionX * directionX + directionY * directionY + directionZ * directionZ);
		if (d0 != 0.0D) {
			this.powerX = directionX / d0 * 0.1D;
			this.powerY = directionY / d0 * 0.1D;
			this.powerZ = directionZ / d0 * 0.1D;
		}
		this.setOwner(owner);
		// todo is this needed?
		this.setRotation(owner.getYaw(), owner.getPitch());
	}

	public void setItem(ItemStack stack) {
		if (!stack.isOf(Registration.HARMBALL_ITEM) || stack.hasNbt()) {
			this.getDataTracker().set(DATA_ITEM_STACK, Util.make(stack.copy(), (itemStack) -> {
				itemStack.setCount(1);
			}));
		}
	}

	protected ItemStack getItem() {
		return this.getDataTracker().get(ITEM);
	}

	@Override
	public boolean isOnFire() {
		return false;
	}

	@Override
	protected void onHit(HitResult hitResult) {
		super.onHit(hitResult);
		if (!this.level.isClientSide) {
			this.discard();
		}
	}

	@Override
	protected void onHitEntity(EntityHitResult hitResult) {
		super.onHitEntity(hitResult);
		if (!this.level.isClientSide) {
			Entity target = hitResult.getEntity();
			Entity ownerEntity = this.getOwner();
			DamageSource damageSource = new IndirectEntityDamageSource("harmball", this, ownerEntity).setProjectile();
			target.hurt(damageSource, Config.Spells.HARMBALL.damage.get());
			if (target instanceof LivingEntity) {
				this.doEnchantDamageEffects((LivingEntity)ownerEntity, target);
			}
		}
	}



	protected ItemStack getItemRaw() {
		return this.getEntityData().get(DATA_ITEM_STACK);
	}

	@Override
	protected ParticleOptions getTrailParticle() {
		return ParticleTypes.SMOKE;
	}

	@Override
	public ItemStack getItem() {
		ItemStack stack = this.getItemRaw();
		return stack.isEmpty() ? new ItemStack(Registration.HARMBALL_ITEM.get()) : stack;
	}

	@Override
	protected void defineSynchedData() {
		this.getEntityData().define(DATA_ITEM_STACK, ItemStack.EMPTY);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		ItemStack itemstack = this.getItemRaw();
		if (!itemstack.isEmpty()) {
			tag.put("Item", itemstack.save(new CompoundTag()));
		}

	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		ItemStack itemStack = ItemStack.of(tag.getCompound("Item"));
		this.setItem(itemStack);
	}
}
