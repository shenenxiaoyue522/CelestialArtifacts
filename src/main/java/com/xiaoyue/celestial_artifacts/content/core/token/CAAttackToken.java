package com.xiaoyue.celestial_artifacts.content.core.token;

import com.xiaoyue.celestial_artifacts.content.core.feature.IFeature;
import dev.xkmc.l2damagetracker.contents.attack.CreateSourceEvent;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2damagetracker.contents.damage.DamageTypeRoot;
import dev.xkmc.l2damagetracker.init.data.L2DamageTypes;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public interface CAAttackToken extends IFeature {

	static boolean chance(LivingEntity player, double chance) {
		return player.getRandom().nextDouble() < chance;
	}

	static DamageSource getSource(DamageData data) {
		return data.getSource();
	}

	static boolean isArrow(DamageData data) {
		return getSource(data).is(DamageTypeTags.IS_PROJECTILE);
	}

	static boolean isMelee(DamageData data) {
		if (data.getAttacker() == null) return false;
		if (data.getAttacker() == data.getTarget()) return false;
		if (data.getSource().getDirectEntity() != data.getAttacker()) return false;
		var ans = data.getSource().typeHolder().unwrapKey().map(DamageTypeRoot::of);
		return ans.isPresent() && (ans.get() == L2DamageTypes.PLAYER_ATTACK || ans.get() == L2DamageTypes.MOB_ATTACK);
	}

	default void onPlayerDamagedFinal(Player player, DamageData.DefenceMax cache) {

	}

	default void onPlayerAttackTarget(Player player, DamageData.Attack cache) {

	}

	default void onPlayerHurtTarget(Player player, DamageData.Offence cache) {

	}

	default void onPlayerDamageTarget(Player player, DamageData.Defence cache) {

	}

	default boolean onPlayerAttacked(Player player, DamageData.Attack cache) {
		return false;
	}

	default void onPlayerHurt(Player player, DamageData.Offence cache) {

	}

	default void onPlayerDamaged(Player player, DamageData.Defence cache) {

	}

	default void onPlayerDamageTargetFinal(Player player, DamageData.DefenceMax cache) {

	}

	default void onCreateSource(Player player, CreateSourceEvent event) {

	}

	default void onPlayerKill(Player player, LivingDeathEvent event) {

	}

}
