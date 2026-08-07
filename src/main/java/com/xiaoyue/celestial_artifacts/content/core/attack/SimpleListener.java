package com.xiaoyue.celestial_artifacts.content.core.attack;

import com.xiaoyue.celestial_artifacts.CelestialArtifacts;
import com.xiaoyue.celestial_artifacts.content.core.modular.AttrTextFacet;
import com.xiaoyue.celestial_artifacts.content.core.modular.IFacet;
import com.xiaoyue.celestial_artifacts.content.core.modular.TextFacet;
import com.xiaoyue.celestial_artifacts.content.core.token.CAAttackToken;
import com.xiaoyue.celestial_artifacts.data.CALang;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.DoubleSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class SimpleListener {

	public static IFacet hurtBonus(Supplier<MutableComponent> text, HurtBonusPredicate condition, DoubleSupplier bonus, String item) {
		return new HurtBonus(text, condition, bonus, item);
	}

	public static IFacet protect(Supplier<MutableComponent> text, ProtectPredicate condition, DoubleSupplier bonus, String item) {
		return new Protection(text, condition, bonus, item);
	}

	public static IFacet protectType(CALang.DamageTypes text, DoubleSupplier bonus, String item) {
		return new ProtectionType(text::get, text::pred, bonus, item);
	}

	public static IFacet avoidType(CALang.DamageTypes text, DoubleSupplier chance) {
		return new AvoidType(text::get, text::pred, chance);
	}

	public static IFacet negateType(CALang.DamageTypes text) {
		return new AvoidType(text::get, text::pred, null);
	}

	public interface HurtBonusPredicate {

		boolean test(Player player, LivingEntity target, DamageData.Offence cache);

	}

	public interface ProtectPredicate {

		boolean test(Player player, LivingEntity attacker, DamageData.Defence cache);

	}

	record HurtBonus(Supplier<MutableComponent> text, HurtBonusPredicate condition, DoubleSupplier bonus, String item)
			implements TextFacet, CAAttackToken {

		@Override
		public void addText(@Nullable Level level, List<Component> list) {
			list.add(TextFacet.wrap(text.get()));
			list.add(TextFacet.inner(CALang.Modular.HURT_BONUS.get(TextFacet.perc(bonus.getAsDouble())).withStyle(ChatFormatting.GRAY)));
		}

		@Override
		public void onPlayerHurtTarget(Player player, DamageData.Offence data) {
			if (condition.test(player, data.getTarget(), data)) {
				data.addHurtModifier(DamageModifier.multTotal((float) (1 + bonus.getAsDouble()), CelestialArtifacts.loc(item).withSuffix("_attack_bonus")));
			}
		}
	}

	record Protection(Supplier<MutableComponent> text, ProtectPredicate condition, DoubleSupplier bonus, String item)
			implements TextFacet, CAAttackToken {

		@Override
		public void addText(@Nullable Level level, List<Component> list) {
			list.add(TextFacet.wrap(text.get()));
			list.add(TextFacet.inner(CALang.Modular.PROTECT.get(TextFacet.perc(bonus.getAsDouble())).withStyle(ChatFormatting.GRAY)));
		}

		@Override
		public void onPlayerDamaged(Player player, DamageData.Defence data) {
			if (condition.test(player, data.getTarget(), data)) {
				data.addDealtModifier(DamageModifier.multTotal((float) (1 - bonus.getAsDouble()), CelestialArtifacts.loc(item).withSuffix("_protection")));
			}
		}
	}

	record ProtectionType(Supplier<MutableComponent> text, Predicate<DamageSource> condition, DoubleSupplier bonus, String item)
			implements AttrTextFacet, CAAttackToken {

		@Override
		public void addAttrText(List<Component> list) {
			MutableComponent bonus = Component.literal(bonus().getAsDouble() * 100 + "%").withStyle(ChatFormatting.BLUE);
			list.add(CALang.Modular.PROTECT_TYPE.get(this.text.get(), bonus).withStyle(ChatFormatting.BLUE));
		}

		@Override
		public void onPlayerDamaged(Player player, DamageData.Defence cache) {
			if (condition.test(CAAttackToken.getSource(cache))) {
				cache.addDealtModifier(DamageModifier.multTotal((float) (1 - bonus.getAsDouble()), CelestialArtifacts.loc(item).withSuffix("_protection_type")));
			}
		}

	}

	record AvoidType(Supplier<MutableComponent> text, Predicate<DamageSource> condition, @Nullable DoubleSupplier chance)
			implements TextFacet, CAAttackToken {

		@Override
		public void addText(@Nullable Level level, List<Component> list) {
			if (chance == null) {
				list.add(TextFacet.wrap(CALang.Modular.NEGATE_TYPE.get(text.get()).withStyle(ChatFormatting.GRAY)));
			} else {
				list.add(TextFacet.wrap(CALang.Modular.DODGE_TYPE.get(TextFacet.perc(chance.getAsDouble()), text.get())
						.withStyle(ChatFormatting.GRAY)));
			}
		}

		@Override
		public boolean onPlayerAttacked(Player player, DamageData.Attack cache) {
			return condition.test(CAAttackToken.getSource(cache)) &&
					(chance == null || CAAttackToken.chance(player, chance.getAsDouble()));
		}

	}

}
