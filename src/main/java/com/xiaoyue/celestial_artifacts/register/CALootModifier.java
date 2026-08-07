package com.xiaoyue.celestial_artifacts.register;

import com.xiaoyue.celestial_artifacts.content.loot.EnabledCondition;
import com.xiaoyue.celestial_artifacts.content.loot.FishingCondition;
import com.xiaoyue.celestial_artifacts.content.loot.HasCurioCondition;
import com.xiaoyue.celestial_artifacts.content.loot.PlayerStatCondition;
import com.xiaoyue.celestial_core.CelestialCore;
import com.xiaoyue.celestial_invoker.content.common.registrar.NeoForgeRegister;
import dev.xkmc.l2serial.serialization.codec.MapCodecAdaptor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.function.Supplier;

public class CALootModifier {

	public static final NeoForgeRegister<LootItemConditionType> CONDITION = CelestialCore.EXTRA.neoforgeRegister(BuiltInRegistries.LOOT_CONDITION_TYPE);

	public static final Supplier<LootItemConditionType> HAS_CURIO, PLAYER_STAT, ENABLED, FISHING;

	static {
		HAS_CURIO = condition("has_curio", HasCurioCondition.class);
		ENABLED = condition("enabled", EnabledCondition.class);
		PLAYER_STAT = condition("player_stat", PlayerStatCondition.class);
		FISHING = condition("fishing", FishingCondition.class);
	}

	private static <T extends LootItemCondition> Supplier<LootItemConditionType> condition(String id, Class<T> codec) {
		return CONDITION.object(id, rl -> new LootItemConditionType(MapCodecAdaptor.of(codec)));
	}

	public static void register() {

	}

}
