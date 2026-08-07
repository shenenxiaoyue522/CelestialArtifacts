package com.xiaoyue.celestial_artifacts;

import com.xiaoyue.celestial_artifacts.register.CAKeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(modid = CelestialArtifacts.MODID, value = Dist.CLIENT)
public class CArtifactsClient {

	@SubscribeEvent
	public static void keyRegister(RegisterKeyMappingsEvent event) {
		event.register(CAKeyMapping.ABILITY_KEY);
	}
}
