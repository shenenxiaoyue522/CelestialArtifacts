package com.xiaoyue.celestial_artifacts.register;

import com.xiaoyue.celestial_artifacts.content.core.feature.FeatureType;
import com.xiaoyue.celestial_artifacts.content.core.modular.CurioCacheCap;
import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import net.minecraft.world.entity.player.Player;

@SerialClass
public record CAbilityPacket() implements SerialPacketBase<CAbilityPacket> {

	@Override
	public void handle(Player player) {
		for (var e : CurioCacheCap.HOLDER.getOrCreate(player).getFeature(player, FeatureType.SKILL)) {
			e.trigger(player);
		}
	}
}