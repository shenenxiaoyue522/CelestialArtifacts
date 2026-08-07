package com.xiaoyue.celestial_artifacts.content.curios.charm;

import com.xiaoyue.celestial_artifacts.content.core.modular.MultiLineText;
import com.xiaoyue.celestial_artifacts.content.core.modular.TextFacet;
import com.xiaoyue.celestial_artifacts.content.core.modular.TickFacet;
import com.xiaoyue.celestial_artifacts.content.core.token.CAAttackToken;
import com.xiaoyue.celestial_artifacts.data.CALang;
import com.xiaoyue.celestial_artifacts.data.CAModConfig;
import com.xiaoyue.celestial_artifacts.register.CAItems;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ItemAbilities;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CursedProtector implements MultiLineText, TickFacet, CAAttackToken {

	private static double threshold() {
		return CAModConfig.SERVER.charm.cursedProtectorThreshold.get();
	}

	private static double reduction() {
		return CAModConfig.SERVER.charm.cursedProtectorReduction.get();
	}

	@Override
	public void addText(@Nullable Level level, List<Component> list) {
		list.add(TextFacet.wrap(CALang.Charm.CURSED_PROTECTOR_0.get().withStyle(ChatFormatting.GRAY)));
		list.add(TextFacet.wrap(CALang.Charm.CURSED_PROTECTOR_1.get(
				TextFacet.perc(threshold()), TextFacet.perc(reduction())
		).withStyle(ChatFormatting.GRAY)));
	}

	@Override
	public void tick(LivingEntity entity, ItemStack stack) {
		if (!(entity instanceof Player player)) return;
		var item = player.getOffhandItem();
		if (item.canPerformAction(ItemAbilities.SHIELD_BLOCK)) {
			if (player.getCooldowns().isOnCooldown(item.getItem())) {
				player.getCooldowns().removeCooldown(item.getItem());
			}
		}
	}

	@Override
	public void onPlayerDamaged(Player player, DamageData.Defence cache) {
		cache.addDealtModifier(DamageModifier.nonlinearMiddle(200, val -> parse(player, val), CAItems.CURSED_PROTECTOR.getId()));
	}

	private float parse(Player player, float val) {
		if (val > player.getHealth() * threshold()) {
			return val * (float) (1 - reduction());
		}
		return val;
	}

}
