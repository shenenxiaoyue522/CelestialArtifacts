package com.xiaoyue.celestial_artifacts.content.core.token;

import com.xiaoyue.celestial_artifacts.CelestialArtifacts;
import com.xiaoyue.celestial_artifacts.content.core.modular.AttrFacet;
import com.xiaoyue.celestial_invoker.invoker.tooltip.TooltipEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public record AttrAdder(ResourceLocation name, Supplier<Holder<Attribute>> attr, AttributeModifier.Operation op, DoubleSupplier value)
		implements ISubToken, IAttrAdder {

	public static AttrAdder of(String name, Supplier<Holder<Attribute>> id, AttributeModifier.Operation op, double value) {
		String key = id.get().getRegisteredName().split(":")[1].replace('.', '_');
		return new AttrAdder(CelestialArtifacts.loc(name).withSuffix("_" + key), id, op, () -> value);
	}

	public static AttrAdder of(String name, Supplier<Holder<Attribute>> id, AttributeModifier.Operation op, DoubleSupplier value) {
		String key = id.get().getRegisteredName().split(":")[1].replace('.', '_');
		return new AttrAdder(CelestialArtifacts.loc(name).withSuffix("_" + key), id, op, value);
	}

	public void tickImpl(Player player) {
		addAttr(player);
	}

	public void addAttr(LivingEntity player) {
		if (player.level().isClientSide()) return;
		double val = value.getAsDouble();
		var ins = player.getAttribute(attr.get());
		if (ins == null) return;
		var mod = ins.getModifier(name);
		if (mod == null || mod.operation() != op || mod.amount() != val) {
			ins.removeModifier(name);
			ins.addTransientModifier(new AttributeModifier(name, val, op));
		}
	}

	public void removeImpl(Player player) {
		if (player.level().isClientSide()) return;
		var ins = player.getAttribute(attr.get());
		if (ins == null) return;
		ins.removeModifier(name);
	}

	public MutableComponent getTooltip() {
		MutableComponent base = Component.literal(value.getAsDouble() < 0 ? "-" : "+");
		return base.append(attr.get().value().toComponent(new AttributeModifier(name, value.getAsDouble(), op), TooltipFlag.NORMAL)
				.withStyle(ChatFormatting.BLUE)).withStyle(ChatFormatting.BLUE);
	}

	public MutableComponent getText(double val) {
		return attr.get().value().toComponent(new AttributeModifier(name, val, op), TooltipFlag.NORMAL).copy().withStyle(ChatFormatting.BLUE);
	}
}
