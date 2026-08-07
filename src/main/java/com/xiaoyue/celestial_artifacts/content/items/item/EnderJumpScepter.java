package com.xiaoyue.celestial_artifacts.content.items.item;

import com.xiaoyue.celestial_artifacts.content.core.modular.TextFacet;
import com.xiaoyue.celestial_artifacts.data.CALang;
import com.xiaoyue.celestial_core.data.CCLangData;
import com.xiaoyue.celestial_invoker.content.generic.shared.CompoundData;
import com.xiaoyue.celestial_invoker.invoker.tooltip.TooltipEntry;
import dev.xkmc.l2core.util.TeleportTool;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;

public class EnderJumpScepter extends Item {

	public static final String CHARGING = "ender_jump_scepter_charging";
	public static final String DIM_POS = "ender_jump_scepter_dim";
	public static final String X_POS = "ender_jump_scepter_x";
	public static final String Y_POS = "ender_jump_scepter_y";
	public static final String Z_POS = "ender_jump_scepter_z";

	public EnderJumpScepter() {
		super(new Properties().stacksTo(1).rarity(Rarity.EPIC));
	}

	public ResourceKey<Level> getLevelFromTag(ItemStack stack) {
		CompoundData data = CompoundData.getOrCreate(stack);
		String dim = data.tag().getString(DIM_POS);
		return ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(dim));
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity target, InteractionHand pUsedHand) {
		MinecraftServer server = pPlayer.level().getServer();
		CompoundData data = CompoundData.getOrCreate(pStack);
		if (!data.isEmpty() && server != null) {
			int charging = data.tag().getInt(CHARGING);
			ServerLevel level = server.getLevel(getLevelFromTag(pStack));
			if (charging > 0 && level != null) {
				TeleportTool.performTeleport(target, level, data.tag().getDouble(X_POS), data.tag().getDouble(Y_POS), data.tag().getDouble(Z_POS), target.getYRot(), target.getXRot());
				data.update(tag -> tag.putInt(CHARGING, charging - 1));
				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.PASS;
	}

	@Override
	public InteractionResult useOn(UseOnContext pContext) {
		ItemStack stack = pContext.getItemInHand();
		CompoundData.update(stack, tag -> {
			tag.putDouble(X_POS, pContext.getClickedPos().getX());
			tag.putDouble(Y_POS, pContext.getClickedPos().getY());
			tag.putDouble(Z_POS, pContext.getClickedPos().getZ());
			tag.putString(DIM_POS, pContext.getLevel().dimension().location().toString());
		});
		return InteractionResult.SUCCESS;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag tooltipFlag) {
		CompoundTag tag = CompoundData.getOrCreate(stack).tag();
		list.add(CALang.Tooltip.ENDER_JUMP_SCEPTER.get());
		list.add(CALang.Tooltip.SCEPTER_CHARGING.get(TextFacet.item(Items.ENDER_PEARL)));
		list.add(CALang.Tooltip.CURRENT_CHARGING.get(CCLangData.num(tag.getInt(CHARGING))));
		list.add(CALang.Tooltip.CURRENT_LEVEL.get(Component.translatable(tag.getString(DIM_POS)).withStyle(ChatFormatting.AQUA)));
		list.add(CALang.Tooltip.CURRENT_POS.get(TooltipEntry.num(tag.getDouble(X_POS)), TooltipEntry.num(tag.getDouble(Y_POS)), TooltipEntry.num(tag.getDouble(Z_POS))));
	}

	@Override
	public boolean overrideOtherStackedOnMe(ItemStack pStack, ItemStack pOther, Slot pSlot, ClickAction pAction, Player pPlayer, SlotAccess pAccess) {
		if (pAction.equals(ClickAction.SECONDARY)) {
			if (pOther.is(Items.ENDER_PEARL)) {
				pOther.shrink(1);
				CompoundData.update(pStack, tag -> {
					int charging = tag.getInt(EnderJumpScepter.CHARGING);
					tag.putInt(EnderJumpScepter.CHARGING, charging + 1);
				});
				return true;
			}
		}
		return false;
	}
}
