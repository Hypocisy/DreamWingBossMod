package com.kumoe.dream_wing_mod.register;

import com.kumoe.dream_wing_mod.DreamWingBossMod;
import com.kumoe.dream_wing_mod.entity.DreamWingBossEntity;
import com.kumoe.dream_wing_mod.projectiles.DreamWingFireballEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {

	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, DreamWingBossMod.MODID);

	public static final Supplier<EntityType<DreamWingBossEntity>> DREAM_WING = ENTITIES.register(
			"dream_wing",
			// The entity type, created using a builder.
			() -> EntityType.Builder.of(
							// An EntityType.EntityFactory<T>, where T is the entity class used - MyEntity in this case.
							// You can think of it as a BiFunction<EntityType<T>, Level, T>.
							// This is commonly a reference to the entity constructor.
							DreamWingBossEntity::new,
							// The MobCategory our entity uses. This is mainly relevant for spawning.
							// See below for more information.
							MobCategory.MONSTER
					)
					// The width and height, in blocks. The width is used in both horizontal directions.
					// This also means that non-square footprints are not supported. Default is 0.6f and 1.8f.
					.sized(1.0f, 2.0f)
					// A multiplicative factor (scalar) used by mobs that spawn in varying sizes.
					// In vanilla, these are only slimes and magma cubes, both of which use 4.0f.
					.spawnDimensionsScale(4.0f)
					// The range in which the entity is kept loaded by the client, in chunks.
					// Vanilla values for this vary, but it's often something around 8 or 10. Defaults to 5.
					// Be aware that if this is greater than the client's chunk view distance,
					// then that chunk view distance is effectively used here instead.
					.clientTrackingRange(8)
					// How often update packets are sent for this entity, in once every x ticks. This is set to higher values
					// for entities that have predictable movement patterns, for example projectiles. Defaults to 3.
					.updateInterval(10)
					// Build the entity type using a resource key. The second parameter should be the same as the entity id.
					.build("dream_wing")
	);
	public static final Supplier<EntityType<DreamWingFireballEntity>> BALL = ENTITIES.register(
			"dream_wing_fireball",
			// The entity type, created using a builder.
			() -> EntityType.Builder.<DreamWingFireballEntity>of(DreamWingFireballEntity::new,
							// The MobCategory our entity uses. This is mainly relevant for spawning.
							// See below for more information.
							MobCategory.MONSTER
					)
					// The width and height, in blocks. The width is used in both horizontal directions.
					// This also means that non-square footprints are not supported. Default is 0.6f and 1.8f.
					.sized(0.5f, 0.5f)
					.fireImmune()
					.noSummon()
					.clientTrackingRange(4) // 追踪范围（单位：区块）
					.updateInterval(10) // 每隔多少 tick 同步实体状态
					// Build the entity type using a resource key. The second parameter should be the same as the entity id.
					.build("dream_wing_fireball")
	);

	public static void register(IEventBus bus) {
		ENTITIES.register(bus);
	}

}
