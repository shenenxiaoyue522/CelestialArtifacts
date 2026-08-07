package com.xiaoyue.celestial_artifacts.content.curios.necklace;

import com.xiaoyue.celestial_artifacts.content.core.modular.MultiLineText;
import com.xiaoyue.celestial_artifacts.content.core.modular.TextFacet;
import com.xiaoyue.celestial_artifacts.content.core.token.CAAttackToken;
import com.xiaoyue.celestial_artifacts.data.CALang;
import com.xiaoyue.celestial_artifacts.data.CAModConfig;
import com.xiaoyue.celestial_core.data.CCDamageTypes;
import com.xiaoyue.celestial_core.utils.EntityUtils;
import dev.xkmc.l2core.events.SchedulerHandler;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LockOfAbyss implements MultiLineText, CAAttackToken {

	private double extraDamageFactor() {
		return CAModConfig.SERVER.necklace.lockOfAbyssExtraDamage.get();
	}

	private int duration() {
		return CAModConfig.SERVER.necklace.lockOfAbyssDuration.get();
	}

	private int threshold() {
		return CAModConfig.SERVER.necklace.lockOfAbyssThreshold.get();
	}

	@Override
	public void addText(@Nullable Level level, List<Component> list) {
		list.add(TextFacet.wrap(CALang.Necklace.LOCK_OF_ABYSS_1.get(TextFacet.num(duration())).withStyle(ChatFormatting.GRAY)));
		list.add(TextFacet.wrap(CALang.Necklace.LOCK_OF_ABYSS_2.get().withStyle(ChatFormatting.GRAY)));
		list.add(TextFacet.wrap(CALang.Necklace.LOCK_OF_ABYSS_3.get(TextFacet.num(threshold())).withStyle(ChatFormatting.GRAY)));
		list.add(TextFacet.wrap(CALang.Necklace.LOCK_OF_ABYSS_4.get(TextFacet.perc(extraDamageFactor())).withStyle(ChatFormatting.GRAY)));
	}

	@Override
	public void onPlayerDamageTargetFinal(Player player, DamageData.DefenceMax cache) {
		if (CAAttackToken.getSource(cache).is(DamageTypeTags.BYPASSES_COOLDOWN)) {
			return;
		}
		var entity = cache.getTarget();
		var ins = entity.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
		if (ins != null) {
			int amplifier = ins.getAmplifier();
			if (amplifier >= threshold() - 1) {
				entity.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
				var dmg = cache.getDamageFinal() * extraDamageFactor();
				SchedulerHandler.schedule(() -> entity.hurt(CCDamageTypes.abyss(player), (float) dmg));
			} else {
				EntityUtils.addEct(entity, MobEffects.MOVEMENT_SLOWDOWN, duration() * 20, amplifier + 1);
			}
		} else {
			EntityUtils.addEct(entity, MobEffects.MOVEMENT_SLOWDOWN, duration() * 20, 0);
		}
	}
}
