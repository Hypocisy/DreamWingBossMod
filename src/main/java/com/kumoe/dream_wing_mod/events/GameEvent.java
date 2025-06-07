package com.kumoe.dream_wing_mod.events;

import com.kumoe.dream_wing_mod.DreamWingBossMod;
import com.kumoe.dream_wing_mod.entity.DreamWingBossEntity;
import com.kumoe.dream_wing_mod.entity.DreamWingBossRenderer;
import com.kumoe.dream_wing_mod.entity.DreamWingFireballRenderer;
import com.kumoe.dream_wing_mod.register.ModEntities;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(modid = DreamWingBossMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class GameEvent {

	@SubscribeEvent
	public static void createDefaultAttributes(EntityAttributeCreationEvent event) {
		event.put(
				// Your entity type.
				ModEntities.DREAM_WING.get(),
				// An AttributeSupplier. This is typically created by calling LivingEntity#createLivingAttributes,
				// setting your values on it, and calling #build. You can also create the AttributeSupplier from scratch
				// if you want, see the source of LivingEntity#createLivingAttributes for an example.
				DreamWingBossEntity.createAttributes()
		);
	}

	@SubscribeEvent
	public static void onClientInit(final FMLClientSetupEvent event) {
		EntityRenderers.register(ModEntities.DREAM_WING.get(), DreamWingBossRenderer::new);
		EntityRenderers.register(ModEntities.BALL.get(), DreamWingFireballRenderer::new);
	}
}
