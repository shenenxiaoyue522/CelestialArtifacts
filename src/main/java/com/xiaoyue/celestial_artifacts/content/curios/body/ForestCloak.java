package com.xiaoyue.celestial_artifacts.content.curios.body;

import com.xiaoyue.celestial_artifacts.content.core.feature.JumpFeature;
import com.xiaoyue.celestial_artifacts.content.core.modular.AttrFacet;
import com.xiaoyue.celestial_artifacts.content.core.modular.AttrTextFacet;
import com.xiaoyue.celestial_artifacts.content.core.modular.SingleLineText;
import com.xiaoyue.celestial_artifacts.content.core.modular.TextFacet;
import com.xiaoyue.celestial_artifacts.content.core.token.CAAttackToken;
import com.xiaoyue.celestial_artifacts.data.CALang;
import com.xiaoyue.celestial_artifacts.data.CAModConfig;
import com.xiaoyue.celestial_core.events.api.LivingJumpEvent;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class ForestCloak implements SingleLineText, AttrTextFacet, CAAttackToken, JumpFeature {

	private static double getDodgeChance() {
		return CAModConfig.SERVER.body.forestCloakDodgeChance.get();
	}

	private static float getPower() {
		return CAModConfig.SERVER.body.forestCloakJumpPower.get().floatValue();
	}

	private static CALang.DamageTypes type() {
		return CALang.DamageTypes.PROJECTILE;
	}

	@Override
	public MutableComponent getLine() {
		return CALang.Modular.DODGE_TYPE.get(TextFacet.perc(getDodgeChance()),
				type().get()).withStyle(ChatFormatting.GRAY);
	}

	@Override
	public void addAttrText(List<Component> list) {
		list.add(AttrFacet.simpleMult(CALang.Modular.JUMP_POWER.get(), getPower()));
	}

	@Override
	public void onJump(Player player, LivingJumpEvent event) {
		event.setJumpPower(event.getJumpPower() * (1 + getPower()));
	}

	@Override
	public boolean onPlayerAttacked(Player player, DamageData.Attack cache) {
		if (CAAttackToken.isArrow(cache)) {
			return CAAttackToken.chance(player, getDodgeChance());
		}
		return false;
	}
}
