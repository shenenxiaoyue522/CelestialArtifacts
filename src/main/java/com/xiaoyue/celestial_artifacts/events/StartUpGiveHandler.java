package com.xiaoyue.celestial_artifacts.events;

import com.xiaoyue.celestial_artifacts.data.CAModConfig;
import com.xiaoyue.celestial_artifacts.register.CAItems;
import com.xiaoyue.celestial_artifacts.utils.CurioUtils;
import com.xiaoyue.celestial_core.content.generic.PlayerFlagData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import static com.xiaoyue.celestial_artifacts.CelestialArtifacts.MODID;

@EventBusSubscriber(modid = MODID)
public class StartUpGiveHandler {

	@SubscribeEvent
	public static void tickPlayer(PlayerTickEvent.Post event) {
		var player = event.getEntity();
		if (player.level().isClientSide()) return;
		PlayerFlagData data = PlayerFlagData.HOLDER.getOrCreate(player);
		if (CAModConfig.SERVER.misc.giveItemsOnStart.get() && !data.hasFlag("hello_world")) {
			data.addFlag("hello_world");
			CAItems.HEIRLOOM_NECKLACE.get().enableMap(e -> player.addItem(e.getDefaultInstance()));
			if (!CAModConfig.SERVER.misc.catastropheScrollEquipOnStart.get())
				CAItems.CATASTROPHE_SCROLL.get().enableMap(e -> player.addItem(e.getDefaultInstance()));
		}
		if (CAModConfig.SERVER.misc.catastropheScrollEquipOnStart.get() &&
				CAItems.CATASTROPHE_SCROLL.get().enableConfig().get()) {
			if (!data.hasFlag("cs") && !CurioUtils.isCsOn(player)) {
				var opt = CuriosApi.getCuriosInventory(player)
						.flatMap(e -> e.getStacksHandler("catastrophe"))
						.map(ICurioStacksHandler::getStacks);
				if (opt.isPresent() && opt.get().getSlots() > 0 && opt.get().getStackInSlot(0).isEmpty()) {
					opt.get().setStackInSlot(0, CAItems.CATASTROPHE_SCROLL.asStack());
					data.addFlag("cs");
				}
			}
		}
	}
}
