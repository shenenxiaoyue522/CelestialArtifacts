package com.xiaoyue.celestial_artifacts.content.items.item;

import com.xiaoyue.celestial_artifacts.content.container.PotionsBagMenu;
import com.xiaoyue.celestial_artifacts.content.container.SimpleInventory;
import com.xiaoyue.celestial_artifacts.data.CALang;
import com.xiaoyue.celestial_artifacts.register.CAMenus;
import com.xiaoyue.celestial_core.data.CCDataMapGen;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PotionsBag extends Item {
	public PotionsBag() {
		super(new Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
	}

	public static boolean isPotion(ItemStack stack) {
		return stack.getItem() instanceof PotionItem || stack.is(CCDataMapGen.IS_POTION);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
		ItemStack stack = pPlayer.getItemInHand(pUsedHand);
		if (!pLevel.isClientSide() && pPlayer instanceof ServerPlayer serverPlayer) {
			if (serverPlayer.isCrouching()) {
				this.onShiftUse(serverPlayer, stack);
			} else {
				this.openSimpleMenu(serverPlayer, stack);
			}
			return InteractionResultHolder.success(stack);
		}
		return InteractionResultHolder.fail(stack);
	}

	public @NotNull SimpleInventory getSimpleInv(ItemStack stack) {
		return new SimpleInventory(stack, 27) {
			@Override
			public boolean canPlaceItem(int pIndex, ItemStack pStack) {
				return PotionsBag.isPotion(pStack);
			}
		};
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag tooltipFlag) {
		list.add(CALang.Tooltip.CAN_STORAGE_POTION.get().withStyle(ChatFormatting.GRAY));
		list.add(CALang.Tooltip.POTIONS_BAG_INFO.get().withStyle(ChatFormatting.GRAY));
	}

	public void openSimpleMenu(ServerPlayer serverPlayer, ItemStack stack) {
		serverPlayer.openMenu(new SimpleMenuProvider((id, inv, player) ->
				new PotionsBagMenu(CAMenus.POTIONS_BAG_MENU.get(), id, inv, stack),
				stack.getHoverName())
		);
	}

	public void onShiftUse(ServerPlayer player, ItemStack stack) {
		SimpleInventory inv = this.getSimpleInv(stack);
		if (inv.isEmpty()) return;
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack invItem = inv.getItem(i);
			if (invItem.isEmpty() || !(invItem.getItem() instanceof PotionItem)) continue;
			ItemStack copy = invItem.copy();
			PotionContents contents = copy.get(DataComponents.POTION_CONTENTS);
            if (contents != null) {
				contents.getAllEffects().forEach(ins -> {
					if (!player.hasEffect(ins.getEffect())) {
						player.addEffect(ins);
						copy.shrink(1);
					}
				});
			}
			inv.setItem(i, copy);
		}
	}
}
