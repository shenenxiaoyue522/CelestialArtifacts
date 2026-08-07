package com.xiaoyue.celestial_artifacts.content.curios.ring;

import com.xiaoyue.celestial_artifacts.content.core.modular.SingleLineText;
import com.xiaoyue.celestial_artifacts.content.core.modular.TextFacet;
import com.xiaoyue.celestial_artifacts.content.core.token.CAAttackToken;
import com.xiaoyue.celestial_artifacts.data.CALang;
import com.xiaoyue.celestial_artifacts.data.CAModConfig;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

public class NetherFire implements SingleLineText, CAAttackToken {

	private static int fireTimeFactor() {
		return CAModConfig.SERVER.ring.netherFireRingFireTime.get();
	}

	@Override
	public MutableComponent getLine() {
		return CALang.Ring.NETHER_FIRE.get(TextFacet.num(fireTimeFactor()));
	}

	@Override
	public void onPlayerAttackTarget(Player player, DamageData.Attack cache) {
		cache.getTarget().setRemainingFireTicks(fireTimeFactor() * 20);
	}

}
