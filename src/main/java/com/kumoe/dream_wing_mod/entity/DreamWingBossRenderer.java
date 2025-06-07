package com.kumoe.dream_wing_mod.entity;

import com.kumoe.dream_wing_mod.entity.geo.DreamWingBossModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DreamWingBossRenderer extends GeoEntityRenderer<DreamWingBossEntity> {
    public DreamWingBossRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DreamWingBossModel());
        this.shadowRadius = 0.5f;
    }
}
