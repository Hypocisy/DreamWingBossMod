package com.kumoe.dream_wing_mod.register;

import com.kumoe.dream_wing_mod.DreamWingBossMod;
import com.kumoe.dream_wing_mod.effects.DummyEffect;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMobEffects {

	public static final DeferredRegister<MobEffect> MOB_EFFECT_DEFERRED_REGISTER = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, DreamWingBossMod.MODID);

	public static final DeferredHolder<MobEffect, DummyEffect> DUMMY_EFFECT = MOB_EFFECT_DEFERRED_REGISTER.register("dummy", DummyEffect::new);

	public static void register(IEventBus bus) {
		MOB_EFFECT_DEFERRED_REGISTER.register(bus);
	}
}
