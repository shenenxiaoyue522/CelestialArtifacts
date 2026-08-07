package com.xiaoyue.celestial_artifacts.content.core.feature;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;

public interface HealFeature extends IFeature {

	void onPlayerHeal(Player player, LivingHealEvent event);

}
