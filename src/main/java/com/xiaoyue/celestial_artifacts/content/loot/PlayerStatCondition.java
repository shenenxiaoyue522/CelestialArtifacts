package com.xiaoyue.celestial_artifacts.content.loot;

import com.xiaoyue.celestial_artifacts.register.CALootModifier;
import com.xiaoyue.celestial_core.content.loot.IntConfigValue;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.neoforge.common.CommonHooks;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.function.BiFunction;

@SerialClass
public class PlayerStatCondition implements LootItemCondition {

	@SerialField
	public Type type;
	@SerialField
	public String count;

	@Deprecated
	public PlayerStatCondition() {

	}

	public PlayerStatCondition(Type type, IntConfigValue count) {
		this.type = type;
		this.count = count.toData();
	}

	@Override
	public LootItemConditionType getType() {
		return CALootModifier.PLAYER_STAT.get();
	}

	@Override
	public boolean test(LootContext ctx) {
		if (!ctx.hasParam(LootContextParams.LAST_DAMAGE_PLAYER)) return false;
		var player = ctx.getParam(LootContextParams.LAST_DAMAGE_PLAYER);
		return type.get(player, ctx) >= IntConfigValue.of(count).get();
	}

	public enum Type {
		LOOT((player, ctx) -> {
			HolderLookup.RegistryLookup<Enchantment> lookup = CommonHooks.resolveLookup(Registries.ENCHANTMENT);
			if (lookup != null) {
				var looting = lookup.get(Enchantments.LOOTING);
				var inv = CuriosApi.getCuriosInventory(player);
				int curioLooting = inv.map(handler -> handler.getLootingLevel(ctx)).orElse(0);
				int enchLooting = looting.map(e -> EnchantmentHelper.getEnchantmentLevel(e.getDelegate(), player)).orElse(0);
				return curioLooting + enchLooting;
			}
            return 0;
        }),
		REPUTATION((player, ctx) -> ctx.hasParam(LootContextParams.THIS_ENTITY) &&
				ctx.getParam(LootContextParams.THIS_ENTITY) instanceof Villager vil ?
				vil.getPlayerReputation(player) : 0);

		private final BiFunction<Player, LootContext, Integer> func;

		Type(BiFunction<Player, LootContext, Integer> func) {
			this.func = func;
		}

		public int get(Player player, LootContext ctx) {
			return func.apply(player, ctx);
		}
	}

}
