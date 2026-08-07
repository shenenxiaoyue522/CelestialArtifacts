package com.xiaoyue.celestial_artifacts.content.items.tool;

import com.xiaoyue.celestial_core.register.CCItems;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public class EarthTierUtils implements Tier {

	@Override
	public int getUses() {
		return 12456;
	}

	@Override
	public float getSpeed() {
		return 20;
	}

	@Override
	public float getAttackDamageBonus() {
		return 10;
	}

	@Override
	public TagKey<Block> getIncorrectBlocksForDrops() {
		return BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
	}

	@Override
	public int getEnchantmentValue() {
		return 30;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return Ingredient.of(CCItems.EARTH_CORE.get());
	}
}
