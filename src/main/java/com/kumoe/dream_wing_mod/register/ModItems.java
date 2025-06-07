package com.kumoe.dream_wing_mod.register;

import com.kumoe.dream_wing_mod.DreamWingBossMod;
import com.kumoe.dream_wing_mod.item.DreamingFireball;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

	private static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.create(BuiltInRegistries.ITEM, DreamWingBossMod.MODID);
	public static final DeferredHolder<Item, DreamingFireball> DREAMING_FIREBALL = ITEM_REGISTER.register("dream_wing_fireball", DreamingFireball::new);

	public static void register(IEventBus bus) {
		ITEM_REGISTER.register(bus);
	}
}
