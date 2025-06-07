package com.kumoe.dream_wing_mod.entity;

import com.kumoe.dream_wing_mod.DreamWingBossMod;
import com.kumoe.dream_wing_mod.projectiles.DreamWingFireballEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

public class DreamWingFireballRenderer<T extends DreamWingFireballEntity> extends EntityRenderer<T> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(DreamWingBossMod.MODID, "textures/item/dream_wing_fireball.png");
	private final ItemRenderer itemRenderer;
	private final float scale;
	private final boolean fullBright;

	public DreamWingFireballRenderer(EntityRendererProvider.Context context, float scale, boolean fullBright) {
		super(context);
		this.itemRenderer = context.getItemRenderer();
		this.scale = scale;
		this.fullBright = fullBright;
	}

	public DreamWingFireballRenderer(EntityRendererProvider.Context context) {
		this(context, 1.0F, false);
	}

	protected int getBlockLightLevel(T entity, BlockPos pos) {
		return this.fullBright ? 15 : super.getBlockLightLevel(entity, pos);
	}

	@Override
	@ParametersAreNonnullByDefault
	public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		if (entity.tickCount >= 2 || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(entity) < 12.25)) {
			poseStack.pushPose();
			poseStack.scale(this.scale, this.scale, this.scale);
			poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
			this.itemRenderer.renderStatic(((ItemSupplier) entity).getItem(), ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), entity.getId());
			poseStack.popPose();
			super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
		}

	}

	public @NotNull ResourceLocation getTextureLocation(DreamWingFireballEntity entity) {
		return TEXTURE;
	}
}
