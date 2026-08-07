package com.xiaoyue.celestial_artifacts.content.items.tool;

import com.xiaoyue.celestial_artifacts.register.CAItems;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class EarthHoe extends HoeItem {
	public EarthHoe() {
		super(new EarthTierUtils(), new Properties());
	}

	@Override
	public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
		ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
		builder.add(Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(CAItems.EARTH_HOE.getId().withSuffix("block_reach_bonus"),
				4, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
		builder.add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(CAItems.EARTH_HOE.getId().withSuffix("entity_reach_bonus"),
				4, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
		return builder.build();
	}
}
