package com.xiaoyue.celestial_artifacts.content.items.item;

import com.xiaoyue.celestial_artifacts.data.CALang;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

import java.util.List;

public class PurifiedPowder extends Item {
	public PurifiedPowder() {
		super(new Properties().rarity(Rarity.UNCOMMON));
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag tooltipFlag) {
		list.add(CALang.Tooltip.PURIFIED.get().withStyle(ChatFormatting.GRAY));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		InteractionHand off = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
		ItemStack other = player.getItemInHand(off);
		if (other.isEnchanted()) {
			if (!level.isClientSide()) {
				ItemEnchantments map = other.getTagEnchantments();
				ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(map);
				mutable.removeIf(ench -> ench.is(EnchantmentTags.CURSE));
				EnchantmentHelper.setEnchantments(other, mutable.toImmutable());
				stack.shrink(1);
			}
			return InteractionResultHolder.success(stack);
		}
		return InteractionResultHolder.pass(stack);
	}

}
