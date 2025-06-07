package com.kumoe.dream_wing_mod.projectiles;

import com.kumoe.dream_wing_mod.register.ModEntities;
import com.kumoe.dream_wing_mod.register.ModItems;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class DreamWingFireballEntity extends AbstractHurtingProjectile implements ItemSupplier {

	public DreamWingFireballEntity(Level level) {
		super(ModEntities.BALL.get(), 0, 0, 0, new Vec3(1, 1, 1), level);
		this.setNoGravity(true);
		this.setDeltaMovement(this.getDeltaMovement().normalize().scale(1.35));
	}

	public DreamWingFireballEntity(EntityType<DreamWingFireballEntity> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	protected void onHitEntity(EntityHitResult result) {
		result.getEntity().hurt(this.damageSources().source(DamageTypes.MAGIC, getOwner(), this), 8.0F);
		this.discard();
	}

	@Override
	public @NotNull ItemStack getItem() {
		return ModItems.DREAMING_FIREBALL.get().getDefaultInstance();
	}
}
