package com.xiaoyue.celestial_artifacts.content.core.feature;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;

public interface ShieldingFeature extends IFeature {

	void onPlayerBlocked(Player player, LivingShieldBlockEvent event);

}
