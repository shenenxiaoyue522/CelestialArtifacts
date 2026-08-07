package com.xiaoyue.celestial_artifacts.content.curios.charm;

import com.xiaoyue.celestial_artifacts.content.core.effect.EffectFacet;
import com.xiaoyue.celestial_artifacts.content.core.modular.MultiLineText;
import com.xiaoyue.celestial_artifacts.content.core.modular.TextFacet;
import com.xiaoyue.celestial_artifacts.content.core.modular.TotemFacet;
import com.xiaoyue.celestial_artifacts.content.core.token.CAAttackToken;
import com.xiaoyue.celestial_artifacts.data.CALang;
import com.xiaoyue.celestial_artifacts.data.CAModConfig;
import com.xiaoyue.celestial_core.data.CCDamageTypes;
import com.xiaoyue.celestial_core.register.CCEffects;
import dev.xkmc.l2core.events.SchedulerHandler;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2damagetracker.contents.curios.TotemHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SoulBox implements MultiLineText, CAAttackToken, TotemFacet {

	private static int cooldown() {
		return CAModConfig.SERVER.charm.soulBoxCooldown.get();
	}

	private static double damageFactor() {
		return CAModConfig.SERVER.charm.soulBoxReflect.get();
	}

	private static double effectChanceFactor() {
		return CAModConfig.SERVER.charm.soulBoxShatterChance.get();
	}

	private static int durHigh() {
		return CAModConfig.SERVER.charm.soulBoxShatterHighDuration.get();
	}

	private static int ampHigh() {
		return CAModConfig.SERVER.charm.soulBoxShatterHighLevel.get();
	}

	private static int durLow() {
		return CAModConfig.SERVER.charm.soulBoxShatterLowDuration.get();
	}

	private static int ampLow() {
		return CAModConfig.SERVER.charm.soulBoxShatterLowLevel.get();
	}

	private static MobEffectInstance effHigh() {
		return new MobEffectInstance(CCEffects.SOUL_SHATTER.holder(), durHigh() * 20, ampHigh());
	}

	private static MobEffectInstance effLow() {
		return new MobEffectInstance(CCEffects.SOUL_SHATTER.holder(), durLow() * 20, ampLow());
	}

	@Override
	public void addText(@Nullable Level level, List<Component> list) {
		list.add(TextFacet.wrap(CALang.Charm.SOUL_BOX_0.get(TextFacet.perc(effectChanceFactor()), EffectFacet.getDesc(effLow()))));
		list.add(TextFacet.wrap(CALang.Charm.SOUL_BOX_1.get(TextFacet.num(cooldown()))));
		list.add(TextFacet.inner(CALang.Charm.SOUL_BOX_2.get(EffectFacet.getDesc(effHigh()))));
		list.add(TextFacet.inner(CALang.Charm.SOUL_BOX_3.get(TextFacet.perc(damageFactor()))));
	}

	@Override
	public void trigger(Player player, ItemStack stack, TotemHelper.TotemSlot slot, DamageSource source) {
		TotemFacet.super.trigger(player, stack, slot, source);
		if (source.getEntity() instanceof LivingEntity le) {
			le.addEffect(effHigh());
			SchedulerHandler.schedule(() -> le.hurt(CCDamageTypes.abyss(player), (float) (player.getMaxHealth() * damageFactor())));
		}
		player.getCooldowns().addCooldown(stack.getItem(), cooldown() * 20);
	}

	@Override
	public void onPlayerDamaged(Player player, DamageData.Defence cache) {
		if (cache.getAttacker() != null) {
			cache.getAttacker().addEffect(effLow());
		}
	}

}
