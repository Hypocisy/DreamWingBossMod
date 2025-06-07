package com.kumoe.dream_wing_mod;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(modid = DreamWingBossMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
	private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
	static final ModConfigSpec SPEC = BUILDER.build();

	private static final ModConfigSpec.IntValue SUMMON_FREQUENCY = BUILDER
			.comment("DreamWing召唤怪物的冷却时间(ticks)")
			.defineInRange("summon_frequency", 2000, 20, Integer.MAX_VALUE);
	private static final ModConfigSpec.IntValue MAX_SUMMON_LIMIT = BUILDER
			.comment("DreamWing召唤从属的最大限制")
			.defineInRange("max_summon_limit", 20, 1, Integer.MAX_VALUE);
	private static final ModConfigSpec.IntValue BASIC_SUMMON_COUNT = BUILDER
			.comment("DreamWing召唤从属的基础数量")
			.defineInRange("basic_summon_count", 3, 1, Integer.MAX_VALUE);
	private static final ModConfigSpec.IntValue RANDOM_SUMMON_COUNT = BUILDER
			.comment("DreamWing召唤从属的随机数量(0-this var)")
			.defineInRange("random_summon_count", 3, 1, Integer.MAX_VALUE);
	// a list of strings that are treated as resource locations for items
	private static final ModConfigSpec.ConfigValue<List<? extends String>> ENTIYT_STRINGS = BUILDER
			.comment("当DreamWing召唤时的一个随机召唤列表.")
			.defineListAllowEmpty("items", List.of("minecraft:zombie", "minecraft:"), () -> "minecraft:zombie", Config::validateItemName);
	public static int summon_frequency;
	public static int max_summon_limit;
	public static List<EntityType<?>> ENTITYS;
	public static int basicSummonCount;
	public static int randomSummonCount;

	private static boolean validateItemName(final Object obj) {
		return obj instanceof String entityName && BuiltInRegistries.ENTITY_TYPE.containsKey(ResourceLocation.parse(entityName));
	}

	@SubscribeEvent
	static void onLoad(final ModConfigEvent event) {
		summon_frequency = SUMMON_FREQUENCY.get();
		max_summon_limit = MAX_SUMMON_LIMIT.get();
		basicSummonCount = BASIC_SUMMON_COUNT.get();
		randomSummonCount = RANDOM_SUMMON_COUNT.get();

		// convert the list of strings into a set of items
		ENTITYS = ENTIYT_STRINGS.get().stream()
				.map(entityName -> BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(entityName)))
				.collect(Collectors.toList());
	}
}
