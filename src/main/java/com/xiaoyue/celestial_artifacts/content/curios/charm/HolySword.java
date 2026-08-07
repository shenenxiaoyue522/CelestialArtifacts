package com.xiaoyue.celestial_artifacts.content.curios.charm;

import com.xiaoyue.celestial_artifacts.content.core.modular.MultiLineText;
import com.xiaoyue.celestial_artifacts.content.core.modular.TextFacet;
import com.xiaoyue.celestial_artifacts.content.core.token.CAAttackToken;
import com.xiaoyue.celestial_artifacts.data.CALang;
import com.xiaoyue.celestial_artifacts.data.CAModConfig;
import com.xiaoyue.celestial_artifacts.register.CAItems;
import dev.xkmc.l2core.events.SchedulerHandler;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HolySword implements MultiLineText, CAAttackToken {

	private static double atk() {
		return CAModConfig.SERVER.charm.holySwordLostLifeAddDamage.get();
	}

	private static double reflect() {
		return CAModConfig.SERVER.charm.holySwordReflect.get();
	}

	public static float lossLifeAdd(Player player) {
		return (float) (((player.getMaxHealth() - player.getHealth()) * atk()));
	}

	@Override
	public void addText(@Nullable Level level, List<Component> list) {
		list.add(TextFacet.wrap(CALang.Charm.HOLY_SWORD_1.get(TextFacet.perc(reflect()))));
		list.add(TextFacet.wrap(CALang.Charm.HOLY_SWORD_2.get()));
		list.add(TextFacet.wrap(CALang.Charm.HOLY_SWORD_3.get(TextFacet.perc(atk()))));
	}

	@Override
	public void onPlayerHurtTarget(Player player, DamageData.Offence cache) {
		float min = HolySword.lossLifeAdd(player);
		cache.addHurtModifier(DamageModifier.multTotal(1 + min, CAItems.HOLY_SWORD.getId()));
	}

	@Override
	public void onPlayerDamageTargetFinal(Player player, DamageData.DefenceMax cache) {
		if (cache.getTarget().getType().is(EntityTypeTags.UNDEAD)) {
			player.heal(cache.getDamageFinal());
		}
	}

	@Override
	public void onPlayerDamagedFinal(Player player, DamageData.DefenceMax cache) {
		if (cache.getAttacker() != null)
			SchedulerHandler.schedule(() -> cache.getAttacker().hurt(player.damageSources().playerAttack(player),
					cache.getDamageFinal() * (float) reflect()));
	}

}
