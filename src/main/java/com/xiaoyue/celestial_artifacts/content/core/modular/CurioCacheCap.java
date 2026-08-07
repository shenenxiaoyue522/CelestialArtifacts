package com.xiaoyue.celestial_artifacts.content.core.modular;

import com.xiaoyue.celestial_artifacts.CelestialArtifacts;
import com.xiaoyue.celestial_artifacts.content.core.feature.FeatureMap;
import com.xiaoyue.celestial_artifacts.content.core.feature.FeatureType;
import com.xiaoyue.celestial_artifacts.content.core.feature.IFeature;
import dev.xkmc.l2core.capability.player.PlayerCapabilityHolder;
import dev.xkmc.l2core.capability.player.PlayerCapabilityNetworkHandler;
import dev.xkmc.l2core.capability.player.PlayerCapabilityTemplate;
import dev.xkmc.l2core.init.L2LibReg;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.HashMap;
import java.util.Map;

@SerialClass
public class CurioCacheCap extends PlayerCapabilityTemplate<CurioCacheCap> {

	public static final PlayerCapabilityHolder<CurioCacheCap> HOLDER = new PlayerCapabilityHolder<>(CelestialArtifacts.loc("curio_data"),
			CurioCacheCap.class, CurioCacheCap::new, PlayerCapabilityNetworkHandler::new);

	private final Map<Item, ItemStack> map = new HashMap<>();
	private final FeatureMap features = new FeatureMap();
	private long lastTime = -1;

	private void refresh(LivingEntity entity) {
		if (!(entity instanceof Player player)) return;
		if (player.level().getGameTime() != lastTime) {
			lastTime = player.level().getGameTime();
			map.clear();
			features.clear();
			var opt = CuriosApi.getCuriosInventory(player);
			if (opt.isPresent()) {
				for (var e : opt.get().getCurios().values()) {
					for (int i = 0; i < e.getStacks().getSlots(); i++) {
						ItemStack stack = e.getStacks().getStackInSlot(i);
						map.put(stack.getItem(), stack);
						if (stack.getItem() instanceof ModularCurio modular) {
							features.addAll(modular.features());
						}
					}
				}
			}
			for (var e : L2LibReg.CONDITIONAL.type().getOrCreate(player).data.values()) {
				if (e instanceof IFacet t) {
					features.add(t);
				}
			}
		}
	}

	public ItemStack get(LivingEntity entity, Item item) {
		refresh(entity);
		return map.getOrDefault(item, ItemStack.EMPTY);
	}

	public boolean has(LivingEntity entity, Item... items) {
		refresh(entity);
		for (var e : items) {
			if (!map.containsKey(e))
				return false;
		}
		return true;
	}

	public static void register() {

	}

	public <T extends IFeature> Iterable<T> getFeature(LivingEntity entity, FeatureType<T> type) {
		refresh(entity);
		return features.get(type);
	}
}
