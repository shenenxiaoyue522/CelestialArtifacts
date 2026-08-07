package com.xiaoyue.celestial_artifacts.content.curios.head;

import com.xiaoyue.celestial_artifacts.content.core.feature.SkillFeature;
import com.xiaoyue.celestial_artifacts.content.core.modular.MultiLineText;
import com.xiaoyue.celestial_artifacts.content.core.modular.TextFacet;
import com.xiaoyue.celestial_artifacts.data.CALang;
import com.xiaoyue.celestial_artifacts.data.CAModConfig;
import com.xiaoyue.celestial_artifacts.register.CAItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SeaGodCrown implements MultiLineText, SkillFeature {

	private static int cd() {
		return CAModConfig.SERVER.head.seaGodCrownCoolDown.get();
	}

	@Override
	public void trigger(Player player) {
		if (player.level() instanceof ServerLevel serverLevel && !serverLevel.isRaining()) {
			if (!player.getCooldowns().isOnCooldown(CAItems.SEA_GOD_CROWN.get())) {
				serverLevel.setWeatherParameters(0, 20000, true, true);
				player.getCooldowns().addCooldown(CAItems.SEA_GOD_CROWN.get(), cd() * 20);
			}
		}
	}

	@Override
	public void addText(@Nullable Level level, List<Component> list) {
		list.add(CALang.Modular.SKILL.get().withStyle(ChatFormatting.YELLOW));
		list.add(TextFacet.wrap(CALang.Modular.SKILL_CD.get(TextFacet.num(cd()))));
		list.add(TextFacet.wrap(CALang.Head.SEA_GOD_CROWN.get()));
	}

}
