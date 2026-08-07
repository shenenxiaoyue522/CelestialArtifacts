package com.xiaoyue.celestial_artifacts.content.curios.bracelet;

import com.xiaoyue.celestial_artifacts.content.core.modular.TextFacet;
import com.xiaoyue.celestial_artifacts.content.core.token.BaseTickingToken;
import com.xiaoyue.celestial_artifacts.content.core.token.CAAttackToken;
import com.xiaoyue.celestial_artifacts.data.CALang;
import com.xiaoyue.celestial_artifacts.data.CAModConfig;
import com.xiaoyue.celestial_artifacts.register.CAItems;
import com.xiaoyue.celestial_core.register.CCEffects;
import dev.xkmc.l2core.base.effects.EffectUtil;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SerialClass
public class HiddenBracelet extends BaseTickingToken implements CAAttackToken {

	@SerialField
	private int cooldown = 0;

	private static int dur() {
		return CAModConfig.SERVER.bracelet.hiddenBraceletInterval.get();
	}

	private static Holder<MobEffect> eff() {
		return CCEffects.HIDDEN.holder();
	}

	private static MobEffectInstance ins() {
		return new MobEffectInstance(eff(), 40, 0, true, false);
	}

	private static double atk() {
		return CAModConfig.SERVER.bracelet.hiddenBraceletDamageBoost.get();
	}

	@Override
	public void addText(@Nullable Level level, List<Component> list) {
		list.add(TextFacet.wrap(CALang.Bracelet.HIDDEN_0.get(TextFacet.eff(eff()), TextFacet.num(dur()), TextFacet.eff(eff()))));
		list.add(TextFacet.wrap(CALang.Bracelet.HIDDEN_1.get(TextFacet.eff(eff()), TextFacet.perc(atk()))));
	}

	@Override
	protected void removeImpl(Player player) {
		player.removeEffect(eff());
	}

	@Override
	protected void tickImpl(Player player) {
		if (!player.hasEffect(eff())) {
			if (cooldown <= 0) {
				cooldown = dur() * 20;
			} else {
				cooldown--;
				if (cooldown <= 0) {
					player.addEffect(ins());
				}
			}
		} else {
			EffectUtil.refreshEffect(player, ins(), player);
		}
	}

	@Override
	public void onPlayerHurtTarget(Player player, DamageData.Offence cache) {
		if (player.hasEffect(CCEffects.HIDDEN.holder())) {
			cache.addHurtModifier(DamageModifier.multTotal(1 + (float) atk(), CAItems.HIDDEN_BRACELET.getId()));
		}
	}

}
