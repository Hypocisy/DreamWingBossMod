package com.kumoe.dream_wing_mod;

import com.kumoe.dream_wing_mod.register.ModEntities;
import com.kumoe.dream_wing_mod.register.ModItems;
import com.kumoe.dream_wing_mod.register.ModMobEffects;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(DreamWingBossMod.MODID)
public class DreamWingBossMod {

	public static final String MODID = "dream_wing_boss_mod";
	// Directly reference a slf4j logger
	private static final Logger LOGGER = LogUtils.getLogger();

	public DreamWingBossMod(IEventBus modEventBus, ModContainer modContainer) {
		ModMobEffects.register(modEventBus);
		ModEntities.register(modEventBus);
		ModItems.register(modEventBus);

		// Register our mod's ModConfigSpec so that FML can create and load the config file for us
		modContainer.registerConfig(ModConfig.Type.STARTUP, Config.SPEC);
	}

}
