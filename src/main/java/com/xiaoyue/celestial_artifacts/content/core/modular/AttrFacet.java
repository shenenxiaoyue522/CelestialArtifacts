package com.xiaoyue.celestial_artifacts.content.core.modular;

import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import static net.minecraft.world.item.component.ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT;

public record AttrFacet(Supplier<Holder<Attribute>> attr, DoubleSupplier val,
						AttributeModifier.Operation op) implements IFacet {

	public static AttrFacet add(Supplier<Holder<Attribute>> attr, DoubleSupplier val) {
		return new AttrFacet(attr, val, AttributeModifier.Operation.ADD_VALUE);
	}

	public static AttrFacet multBase(Supplier<Holder<Attribute>> attr, DoubleSupplier val) {
		return new AttrFacet(attr, val, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
	}

	public static AttrFacet multTotal(Supplier<Holder<Attribute>> attr, DoubleSupplier val) {
		return new AttrFacet(attr, val, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
	}

	public void modify(ResourceLocation id, Multimap<Holder<Attribute>, AttributeModifier> ans) {
		ans.put(attr.get(), new AttributeModifier(id, val.getAsDouble(), op));
	}

	public static MutableComponent simpleAdd(Component text, int val) {
		MutableComponent base = Component.literal(val < 0 ? "-" : "+");
		base.append(ATTRIBUTE_MODIFIER_FORMAT.format(Math.abs(val)));
		base.append(" ");
		base.append(text);
		return base.withStyle(val < 0 ? ChatFormatting.RED : ChatFormatting.BLUE);
	}

	public static MutableComponent simpleMult(Component text, double val) {
		MutableComponent base = Component.literal(val < 0 ? "-" : "+");
		base.append(ATTRIBUTE_MODIFIER_FORMAT.format(Math.abs(val * 100)));
		base.append("% ");
		base.append(text);
		return base.withStyle(val < 0 ? ChatFormatting.RED : ChatFormatting.BLUE);
	}

	public static MutableComponent textMult(Component text, double val) {
		MutableComponent base = Component.literal(val < 0 ? "-" : "+");
		base.append(ATTRIBUTE_MODIFIER_FORMAT.format(Math.abs(val * 100)));
		base.append("%");
		base = Component.empty().append(base.withStyle(ChatFormatting.AQUA)).append(" ");
		base.append(text);
		return base.withStyle(ChatFormatting.GRAY);
	}

}
