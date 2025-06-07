package com.kumoe.dream_wing_mod.entity.geo;

import com.kumoe.dream_wing_mod.DreamWingBossMod;
import com.kumoe.dream_wing_mod.entity.DreamWingBossEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DreamWingBossModel extends GeoModel<DreamWingBossEntity> {
    @Override
    public ResourceLocation getModelResource(DreamWingBossEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DreamWingBossMod.MODID, "geo/dream_wing.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(DreamWingBossEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DreamWingBossMod.MODID, "textures/entity/dream_wing.png");
    }

    @Override
    public ResourceLocation getAnimationResource(DreamWingBossEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DreamWingBossMod.MODID, "animations/dream_wing.animation.json");
    }
}
