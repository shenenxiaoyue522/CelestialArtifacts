package com.xiaoyue.celestial_artifacts.content.curios.charm;

import com.xiaoyue.celestial_artifacts.content.core.modular.MultiLineText;
import com.xiaoyue.celestial_artifacts.content.core.modular.TextFacet;
import com.xiaoyue.celestial_artifacts.content.core.modular.TickFacet;
import com.xiaoyue.celestial_artifacts.content.core.modular.TotemFacet;
import com.xiaoyue.celestial_artifacts.content.core.token.CAAttackToken;
import com.xiaoyue.celestial_artifacts.data.CALang;
import com.xiaoyue.celestial_artifacts.data.CAModConfig;
import com.xiaoyue.celestial_artifacts.register.CAItems;
import com.xiaoyue.celestial_core.utils.EntityUtils;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.l2damagetracker.contents.curios.TotemHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HolyTalisman implements MultiLineText, TickFacet, CAAttackToken, TotemFacet {

	private static int interval() {
		return CAModConfig.SERVER.charm.holyTalismanWeakenInterval.get();
	}

	private static int duration() {
		return CAModConfig.SERVER.charm.holyTalismanEffectDuration.get();
	}

	private static double prot() {
		return CAModConfig.SERVER.charm.holyTalismanProtection.get();
	}

	private static double protUndead() {
		return CAModConfig.SERVER.charm.holyTalismanProtectionUndead.get();
	}

	private static int cooldown() {
		return CAModConfig.SERVER.charm.holyTalismanCooldown.get();
	}

	@Override
	public void addText(@Nullable Level level, List<Component> list) {
		list.add(TextFacet.wrap(CALang.Charm.HOLY_TALISMAN_1.get(TextFacet.num(interval()), TextFacet.num(duration()))));
		list.add(TextFacet.wrap(CALang.Charm.HOLY_TALISMAN_2.get(TextFacet.perc(prot()), TextFacet.perc(protUndead()))));
		list.add(TextFacet.wrap(CALang.Charm.HOLY_TALISMAN_3.get(TextFacet.num(cooldown()))));
	}

	@Override
	public void tick(LivingEntity entity, ItemStack stack) {
		if (entity.tickCount % (interval() * 20) == 0) {
			List<LivingEntity> entities = EntityUtils.getExceptForCentralEntity(entity, 8, 2, LivingEntity::isAlive);
			int size = entities.size();
			for (LivingEntity list : entities) {
				if (list instanceof Monster monster) {
					EntityUtils.addEct(monster, MobEffects.WEAKNESS, size * duration() * 20, 0);
				}
			}
		}
	}

	@Override
	public void onPlayerDamaged(Player player, DamageData.Defence cache) {
		var e = cache.getAttacker();
		if (e != null) {
			float factor = (float) (e.getType().is(EntityTypeTags.UNDEAD) ? 1 - protUndead() : 1 - prot());
			cache.addDealtModifier(DamageModifier.multTotal(factor, CAItems.HOLY_TALISMAN.getId()));
		}
	}

	@Override
	public void trigger(Player player, ItemStack holded, TotemHelper.TotemSlot second, DamageSource source) {
		TotemFacet.super.trigger(player, holded, second, source);
		player.setAbsorptionAmount(player.getMaxHealth());
		player.getCooldowns().addCooldown(holded.getItem(), cooldown() * 20);
	}

}
