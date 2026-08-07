package com.xiaoyue.celestial_artifacts.events;

import com.xiaoyue.celestial_artifacts.content.core.feature.FeatureType;
import com.xiaoyue.celestial_artifacts.content.core.modular.CurioCacheCap;
import com.xiaoyue.celestial_artifacts.register.CAEffects;
import com.xiaoyue.celestial_core.events.api.LivingJumpEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;

import static com.xiaoyue.celestial_artifacts.CelestialArtifacts.MODID;

@EventBusSubscriber(modid = MODID)
public class CAGeneralEventHandler {

	@SubscribeEvent
	public static void onLivingJump(LivingJumpEvent event) {
		if (event.getEntity() instanceof Player player) {
			for (var e : CurioCacheCap.HOLDER.getOrCreate(player).getFeature(player, FeatureType.JUMP)) {
				e.onJump(player, event);
			}
		}
	}

	@SubscribeEvent
	public static void onLivingDeath(LivingDeathEvent event) {
		DamageSource source = event.getSource();
		Entity attacker = source.getEntity();
		if (attacker instanceof Player player) {
			CAAttackListener.fireEvent(player, t -> t.onPlayerKill(player, event));
		}
	}

	@SubscribeEvent
	public static void onPlayerPickupXp(PlayerXpEvent.PickupXp event) {
		Player player = event.getEntity();
		double factor = 1;
		for (var e : CurioCacheCap.HOLDER.getOrCreate(player).getFeature(player, FeatureType.EXP)) {
			factor += e.getXpBonus(player);
		}
		event.getOrb().value = (int) (event.getOrb().value * factor);
	}

	@SubscribeEvent
	public static void onPlayerBreak(PlayerEvent.BreakSpeed event) {
		Player player = event.getEntity();
		double factor = 1;
		for (var e : CurioCacheCap.HOLDER.getOrCreate(player).getFeature(player, FeatureType.MINING)) {
			factor *= e.getBreakFactor(player);
		}
		event.setNewSpeed((float) (event.getOriginalSpeed() * factor));
	}


	@SubscribeEvent
	public static void onPlayerHeal(LivingHealEvent event) {
		if (event.getEntity().hasEffect(CAEffects.ENFEEBLED_LACERATION.holder())) {
			event.setAmount(event.getAmount() * 0.2f);
		}
		if (event.getEntity() instanceof Player player) {
			for (var e : CurioCacheCap.HOLDER.getOrCreate(player).getFeature(player, FeatureType.HEAL))
				e.onPlayerHeal(player, event);
		}
	}

	@SubscribeEvent
	public static void onPlayerBlocked(LivingShieldBlockEvent event) {
		if (event.getEntity() instanceof Player player) {
			for (var e : CurioCacheCap.HOLDER.getOrCreate(player).getFeature(player, FeatureType.SHIELD))
				e.onPlayerBlocked(player, event);
		}
	}

}
