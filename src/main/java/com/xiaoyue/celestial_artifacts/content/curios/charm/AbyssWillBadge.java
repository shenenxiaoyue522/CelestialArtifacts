package com.xiaoyue.celestial_artifacts.content.curios.charm;

import com.xiaoyue.celestial_artifacts.content.core.feature.SkillFeature;
import com.xiaoyue.celestial_artifacts.content.core.modular.TextFacet;
import com.xiaoyue.celestial_artifacts.content.core.token.CAAttackToken;
import com.xiaoyue.celestial_artifacts.content.core.token.SyncedTickingToken;
import com.xiaoyue.celestial_artifacts.content.core.token.TokenFacet;
import com.xiaoyue.celestial_artifacts.data.CALang;
import com.xiaoyue.celestial_artifacts.data.CAModConfig;
import com.xiaoyue.celestial_artifacts.register.CAItems;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SerialClass
public class AbyssWillBadge extends SyncedTickingToken<AbyssWillBadge> implements CAAttackToken, SkillFeature {

	public static final TokenFacet<AbyssWillBadge> TOKEN = new TokenFacet<>("abyss_will_badge", AbyssWillBadge::new);

	@SerialField
	public int abyss_will_badge_add;

	@SerialField
	public float abyss_will_badge_damage;

	private static int initial() {
		return CAModConfig.SERVER.charm.abyssWillBadgeInitialLevel.get();
	}

	private static int duration() {
		return CAModConfig.SERVER.charm.abyssWillBadgeDuration.get();
	}

	private static double remainHP() {
		return CAModConfig.SERVER.charm.abyssWillBadgeRemainingHealth.get();
	}

	private static double pa() {
		return CAModConfig.SERVER.charm.abyssWillBadgeChanceSmall.get();
	}

	private static double pb() {
		return CAModConfig.SERVER.charm.abyssWillBadgeChanceLarge.get();
	}

	private static double atkA() {
		return CAModConfig.SERVER.charm.abyssWillBadgeBonusSmall.get();
	}

	private static double atkB() {
		return CAModConfig.SERVER.charm.abyssWillBadgeBonusLarge.get();
	}

	private static double dmgA() {
		return CAModConfig.SERVER.charm.abyssWillBadgePenaltySmall.get();
	}

	private static double dmgB() {
		return CAModConfig.SERVER.charm.abyssWillBadgePenaltyLarge.get();
	}

	private static int interval() {
		return CAModConfig.SERVER.charm.abyssWillBadgeGrowInterval.get();
	}

	private static int limit() {
		return CAModConfig.SERVER.charm.abyssWillBadgeLimit.get();
	}

	private static double atkBonus() {
		return CAModConfig.SERVER.charm.abyssWillBadgeDamageBonus.get();
	}

	private static double hurtPenalty() {
		return CAModConfig.SERVER.charm.abyssWillBadgeDamagePenalty.get();
	}

	@Override
	public void addText(@Nullable Level level, List<Component> list) {
		list.add(TextFacet.wrap(CALang.Charm.ABYSS_WILL_BADGE_3.get(TextFacet.perc(pa()), TextFacet.perc(atkA()),
				TextFacet.perc(pb()), TextFacet.perc(atkB()))));
		list.add(TextFacet.wrap(CALang.Charm.ABYSS_WILL_BADGE_4.get(TextFacet.perc(pa()), TextFacet.perc(dmgB()),
				TextFacet.perc(pb()), TextFacet.perc(dmgA()))));
		list.add(TextFacet.wrap(CALang.Charm.ABYSS_WILL_BADGE_5.get(TextFacet.num(interval()), TextFacet.num(limit()))));
		list.add(TextFacet.wrap(CALang.Charm.ABYSS_WILL_BADGE_6.get(TextFacet.perc(atkBonus()), TextFacet.perc(hurtPenalty()))));
		list.add(TextFacet.wrap(CALang.Charm.ABYSS_WILL_BADGE_7.get(TextFacet.num(abyss_will_badge_add)).withStyle(ChatFormatting.DARK_PURPLE)));
		list.add(CALang.Modular.SKILL.get().withStyle(ChatFormatting.YELLOW));
		list.add(TextFacet.wrap(CALang.Modular.SKILL_CD.get(TextFacet.num(duration()))));
		list.add(TextFacet.wrap(CALang.Charm.ABYSS_WILL_BADGE_1.get(TextFacet.num(initial()), TextFacet.num(duration()))));
		list.add(TextFacet.wrap(CALang.Charm.ABYSS_WILL_BADGE_2.get(TextFacet.perc(remainHP()))));
	}

	@Override
	public void trigger(Player player) {
		var item = CAItems.ABYSS_WILL_BADGE.get();
		if (!player.getCooldowns().isOnCooldown(item) && player instanceof ServerPlayer serverPlayer) {
			abyss_will_badge_add = initial();
			sync(TOKEN.getKey(), this, serverPlayer);
			player.getCooldowns().addCooldown(item, duration() * 20);
			player.level().playSound(null, player.getOnPos(), SoundEvents.END_PORTAL_SPAWN, SoundSource.PLAYERS, 1f, 1f);
		}
	}

	@Override
	protected void tickImpl(Player player) {
		var item = CAItems.ABYSS_WILL_BADGE.get();
		if (player.tickCount % (interval() * 20) == 0) {
			if (abyss_will_badge_add < limit()) {
				abyss_will_badge_add++;
			}
		}
		if (!player.getCooldowns().isOnCooldown(item)) {
			if (abyss_will_badge_add > limit() + 1) {
				abyss_will_badge_add = 0;
			}
			if (abyss_will_badge_damage != 0) {
				if (!player.level().isClientSide())
					player.setHealth((float) (player.getHealth() * remainHP()));
				abyss_will_badge_damage = 0;
			}
		}
	}

	@Override
	public void onPlayerHurtTarget(Player player, DamageData.Offence cache) {
		double random = player.getRandom().nextDouble();
		float bonus;
		if (pb() > random) {
			bonus = (float) atkA();
		} else if (pb() < random && pb() + pa() > random) {
			bonus = (float) atkB();
		} else {
			bonus = 1;
		}
		if (abyss_will_badge_add > 0) {
			bonus *= (float) (1 + (abyss_will_badge_add * atkBonus()));
		}
		cache.addHurtModifier(DamageModifier.multTotal(bonus, CAItems.ABYSS_WILL_BADGE.getId().withSuffix("_attack_bonus")));
	}

	@Override
	public void onPlayerDamageTargetFinal(Player player, DamageData.DefenceMax cache) {
		var item = CAItems.ABYSS_WILL_BADGE.get();
		if (player.getCooldowns().isOnCooldown(item)) {
			abyss_will_badge_damage += cache.getDamageFinal();
			sync(TOKEN.getKey(), this, (ServerPlayer) player);
		}
	}

	@Override
	public void onPlayerDamaged(Player player, DamageData.Defence cache) {
		double random = player.getRandom().nextDouble();
		float bonus;
		if (pa() > random) {
			bonus = (float) dmgB();
		} else if (pb() < random && pa() + pb() > random) {
			bonus = (float) dmgA();
		} else {
			bonus = 1;
		}
		if (abyss_will_badge_add > 0) {
			bonus *= (float) (1 + abyss_will_badge_add * hurtPenalty());
		}
		cache.addDealtModifier(DamageModifier.multTotal(bonus, CAItems.ABYSS_WILL_BADGE.getId().withSuffix("_damage_bonus")));
	}

	@Override
	protected void removeImpl(Player player) {

	}
}
