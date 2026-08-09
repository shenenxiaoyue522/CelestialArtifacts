package com.xiaoyue.celestial_artifacts.content.items.item;

import com.xiaoyue.celestial_artifacts.data.CALang;
import com.xiaoyue.celestial_artifacts.data.CATagGen;
import com.xiaoyue.celestial_artifacts.utils.CurioUtils;
import com.xiaoyue.celestial_core.register.CCEffects;
import com.xiaoyue.celestial_core.utils.EntityUtils;
import com.xiaoyue.celestial_invoker.invoker.tooltip.TooltipEntry;
import dev.xkmc.l2damagetracker.contents.curios.L2Totem;
import dev.xkmc.l2damagetracker.contents.curios.TotemUseToClient;
import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.function.Consumer;

public class GaiaTotem extends Item implements L2Totem {
	public GaiaTotem() {
		super(new Properties().stacksTo(1).fireResistant());
	}

	@Override
	public void trigger(LivingEntity self, ItemStack holded, Consumer<ItemStack> second) {
		L2DamageTracker.PACKET_HANDLER.toTrackingPlayers(TotemUseToClient.of(self, holded), self);
		holded.shrink(1);
		self.setHealth(1f);
		self.removeAllEffects();
		EntityUtils.addEct(self, CCEffects.UNYIELDING.holder(), 100);
	}

	@Override
	public boolean allow(LivingEntity self, ItemStack stack, DamageSource source) {
		if (!stack.is(CATagGen.REQUIRE_CURSE)) return true;
		if (self instanceof Player player) {
			return CurioUtils.isCsOn(player);
		}
		return false;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag tooltipFlag) {
		if (stack.is(CATagGen.REQUIRE_CURSE)) {
			list.add(CALang.Modular.curse());
		}
		list.add(CALang.Tooltip.GAIA_TOTEM.get(TooltipEntry.eff(CCEffects.UNYIELDING.get())).withStyle(ChatFormatting.GRAY));
	}
}
