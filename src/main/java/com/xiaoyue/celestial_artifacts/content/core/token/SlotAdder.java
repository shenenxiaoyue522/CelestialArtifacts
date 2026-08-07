package com.xiaoyue.celestial_artifacts.content.core.token;

import com.xiaoyue.celestial_artifacts.CelestialArtifacts;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import top.theillusivec4.curios.api.CuriosApi;

import static net.minecraft.world.item.component.ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT;

public record SlotAdder(ResourceLocation name, String slotId, int slotCount) implements ISubToken, IAttrAdder {

	public static SlotAdder of(String name, String id, int count) {
		return new SlotAdder(CelestialArtifacts.loc(name + "_" + id + "_bonus"), id, count);
	}

	public void tickImpl(Player player) {
		if (player.level().isClientSide()) return;
		var opt = CuriosApi.getCuriosInventory(player)
				.flatMap(x -> x.getStacksHandler(slotId));
		if (opt.isEmpty()) return;
		var old = opt.get().getModifiers().get(name);
		if (old == null || old.amount() != slotCount) {
			opt.get().removeModifier(name);
			opt.get().addPermanentModifier(new AttributeModifier(name,
					slotCount, AttributeModifier.Operation.ADD_VALUE));
		}
	}

	public void removeImpl(Player player) {
		if (player.level().isClientSide()) return;
		var opt = CuriosApi.getCuriosInventory(player).flatMap(x -> x.getStacksHandler(slotId));
		if (opt.isEmpty()) return;
		opt.get().removeModifier(name);
	}

	public MutableComponent getTooltip() {
		return Component.translatable("attribute.modifier.plus." + AttributeModifier.Operation.ADD_VALUE.id(),
				ATTRIBUTE_MODIFIER_FORMAT.format(slotCount),
				Component.translatable("curios.identifier." + slotId));
	}

}
