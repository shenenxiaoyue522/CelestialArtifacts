package com.xiaoyue.celestial_artifacts.events;

import com.xiaoyue.celestial_artifacts.content.core.feature.FeatureType;
import com.xiaoyue.celestial_artifacts.content.core.modular.CurioCacheCap;
import com.xiaoyue.celestial_artifacts.content.core.token.CAAttackToken;
import com.xiaoyue.celestial_artifacts.content.curios.curse.CatastropheScroll;
import dev.xkmc.l2damagetracker.contents.attack.AttackListener;
import dev.xkmc.l2damagetracker.contents.attack.CreateSourceEvent;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2damagetracker.init.data.L2DamageTypes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class CAAttackListener implements AttackListener {

	public static void fireEvent(Player player, Consumer<CAAttackToken> cons) {
		for (var token : CurioCacheCap.HOLDER.getOrCreate(player).getFeature(player, FeatureType.ATK)) {
			cons.accept(token);
		}
	}

	public static boolean fireEventCancellable(Player player, Predicate<CAAttackToken> cons) {
		for (var token : CurioCacheCap.HOLDER.getOrCreate(player).getFeature(player, FeatureType.ATK)) {
			if (cons.test(token)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onCreateSource(CreateSourceEvent event) {
		if (event.getResult() != null) {
			if (event.getResult().toRoot() == L2DamageTypes.PLAYER_ATTACK) {
				if (event.getAttacker() instanceof Player player) {
					fireEvent(player, t -> t.onCreateSource(player, event));
				}
			}
		}
	}

	@Override
	public void onDamageFinalized(DamageData.DefenceMax data) {
		if (data.getTarget() instanceof Player player) {
			fireEvent(player, t -> t.onPlayerDamagedFinal(player, data));
		}
		if (data.getAttacker() instanceof Player player) {
			fireEvent(player, t -> t.onPlayerDamageTargetFinal(player, data));
		}
	}

	@Override
	public boolean onAttack(DamageData.Attack data) {
		if (data.getTarget() instanceof Player player) {
			if (fireEventCancellable(player, t -> t.onPlayerAttacked(player, data))) {
				return true;
			}
		}
		if (data.getAttacker() instanceof Player player) {
			fireEvent(player, t -> t.onPlayerAttackTarget(player, data));
		}
        return false;
    }

	@Override
	public void onHurt(DamageData.Offence data) {
		if (data.getAttacker() instanceof Player player) {
			fireEvent(player, t -> t.onPlayerHurtTarget(player, data));
			if (data.getTarget() instanceof Mob mob) {
				if (mob.targetSelector.getAvailableGoals().isEmpty()) {
					CatastropheScroll.Curses.DESIRE.trigger(player);
				}
			}
		}
		if (data.getTarget() instanceof Player player) {
			fireEvent(player, t -> t.onPlayerHurt(player, data));
			CatastropheScroll.Curses.LIFE.trigger(player);
		}
	}

	@Override
	public void onDamage(DamageData.Defence data) {
		if (data.getAttacker() instanceof Player player) {
			fireEvent(player, t -> t.onPlayerDamageTarget(player, data));
		}
		if (data.getTarget() instanceof Player player) {
			fireEvent(player, t -> t.onPlayerDamaged(player, data));
		}
	}
}
