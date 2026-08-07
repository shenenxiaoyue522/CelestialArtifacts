package com.xiaoyue.celestial_artifacts.content.core.modular;

import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.function.IntSupplier;

public record SlotFacet(String slot, IntSupplier count) implements IFacet {

	public static SlotFacet of(String slot, int amount) {
		return new SlotFacet(slot, () -> amount);
	}

	public static SlotFacet of(String slot, IntSupplier amount) {
		return new SlotFacet(slot, amount);
	}

	public void modify(ResourceLocation id, Multimap<Holder<Attribute>, AttributeModifier> ans) {
		CuriosApi.addSlotModifier(ans, slot, id, count.getAsInt(), AttributeModifier.Operation.ADD_VALUE);
	}

}
