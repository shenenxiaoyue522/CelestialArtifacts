package com.xiaoyue.celestial_artifacts.content.core.modular;

import com.xiaoyue.celestial_artifacts.utils.CurioUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class BaseCurio extends Item implements ICurioItem {

	public BaseCurio(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public boolean canEquip(SlotContext slotContext, ItemStack stack) {
		if (slotContext.entity() instanceof Player player) {
			return !CurioUtils.hasCurio(player, this);
		}
		return false;
	}

}
