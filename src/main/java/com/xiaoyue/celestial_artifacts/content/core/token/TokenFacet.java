package com.xiaoyue.celestial_artifacts.content.core.token;

import com.xiaoyue.celestial_artifacts.CelestialArtifacts;
import com.xiaoyue.celestial_artifacts.content.core.modular.TextFacet;
import com.xiaoyue.celestial_artifacts.content.core.modular.TickFacet;
import dev.xkmc.l2core.capability.conditionals.TokenKey;
import dev.xkmc.l2core.init.L2LibReg;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public record TokenFacet<T extends BaseTickingToken>(String id, Supplier<T> sup)
		implements TickFacet, TextFacet {

	@Override
	public void addText(@Nullable Level level, List<Component> list) {
		var token = ClientTokenHelper.get(getKey(), level);
		if (token != null) token.addText(level, list);
		else sup.get().addTextNoData(list);
	}

	@Override
	public void tick(LivingEntity entity, ItemStack stack) {
		if (entity instanceof Player player) {
			Wrappers.run(() -> L2LibReg.CONDITIONAL.type().getOrCreate(player).getOrCreateData(getKey(), sup).update());
		}
	}

	@Nullable
	public T get(LivingEntity entity) {
		if (entity instanceof Player player) {
			return Wrappers.get(() -> L2LibReg.CONDITIONAL.type().getOrCreate(player).getData(getKey()));
		}
		return null;
	}

	public T getData(TokenFacet<T> self) {
		return sup.get();
	}

	public TokenKey<T> getKey() {
		return TokenKey.of(CelestialArtifacts.loc(id));
	}

}
