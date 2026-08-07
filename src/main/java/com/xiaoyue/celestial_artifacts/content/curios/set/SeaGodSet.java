package com.xiaoyue.celestial_artifacts.content.curios.set;

import com.xiaoyue.celestial_artifacts.CelestialArtifacts;
import com.xiaoyue.celestial_artifacts.content.core.modular.TextFacet;
import com.xiaoyue.celestial_artifacts.content.core.token.AttrAdder;
import com.xiaoyue.celestial_artifacts.content.core.token.BaseTickingToken;
import com.xiaoyue.celestial_artifacts.content.core.token.CAAttackToken;
import com.xiaoyue.celestial_artifacts.data.CALang;
import com.xiaoyue.celestial_artifacts.data.CAModConfig;
import com.xiaoyue.celestial_artifacts.register.CAItems;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SerialClass
public class SeaGodSet extends BaseTickingToken implements CAAttackToken {

	private static final String NAME = "sea_god_set";
	private static final AttrAdder ATK = AttrAdder.of(NAME, () -> Attributes.ATTACK_DAMAGE, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, SeaGodSet::getTridentMelee);
	private static final AttrAdder BOW = AttrAdder.of(NAME, L2DamageTracker.BOW_STRENGTH::holder, AttributeModifier.Operation.ADD_VALUE, SeaGodSet::getTridentThrow);

	private static CALang.DamageTypes type() {
		return CALang.DamageTypes.WATER_MOB;
	}

	private static double getProtect() {
		return CAModConfig.SERVER.set.seaGodProtect.get();
	}

	private static double getTridentMelee() {
		return CAModConfig.SERVER.set.seaGodMelee.get();
	}

	private static double getTridentThrow() {
		return CAModConfig.SERVER.set.seaGodThrow.get();
	}

	@Override
	protected void removeImpl(Player player) {
		ATK.removeImpl(player);
		BOW.removeImpl(player);
	}

	@Override
	protected void tickImpl(Player player) {
		if (player.getMainHandItem().is(ItemTags.TRIDENT_ENCHANTABLE)) {
			ATK.tickImpl(player);
			BOW.tickImpl(player);
		} else {
			ATK.removeImpl(player);
			BOW.removeImpl(player);
		}
	}

	@Override
	public void addText(@Nullable Level level, List<Component> list) {
		list.add(TextFacet.set(level, CAItems.seaGodSet()));
		list.add(TextFacet.wrap(CALang.Modular.PROTECT_TYPE.get(
				type().get(), TextFacet.perc(getProtect())
		).withStyle(ChatFormatting.GRAY)));
		list.add(TextFacet.wrap(CALang.Sets.SEA_GOD.get().withStyle(ChatFormatting.GRAY)));
		list.add(TextFacet.inner(ATK.getTooltip()));
		list.add(TextFacet.inner(BOW.getTooltip()));
	}

	@Override
	public void onPlayerDamaged(Player player, DamageData.Defence cache) {
		if (type().pred(CAAttackToken.getSource(cache))) {
			cache.addDealtModifier(DamageModifier.multTotal(1 - (float) getProtect(), CelestialArtifacts.loc("sea_god_set")));
		}
	}

}
