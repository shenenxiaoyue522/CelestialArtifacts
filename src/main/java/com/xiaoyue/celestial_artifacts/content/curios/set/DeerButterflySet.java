package com.xiaoyue.celestial_artifacts.content.curios.set;

import com.xiaoyue.celestial_artifacts.content.core.modular.TextFacet;
import com.xiaoyue.celestial_artifacts.content.core.token.BaseTickingToken;
import com.xiaoyue.celestial_artifacts.content.core.token.CAAttackToken;
import com.xiaoyue.celestial_artifacts.data.CALang;
import com.xiaoyue.celestial_artifacts.data.CAModConfig;
import com.xiaoyue.celestial_artifacts.register.CAItems;
import com.xiaoyue.celestial_core.data.CCDamageTypes;
import dev.xkmc.l2core.events.SchedulerHandler;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SerialClass
public class DeerButterflySet extends BaseTickingToken implements CAAttackToken {

    private static float dmgMul() {
        return CAModConfig.SERVER.set.deerButterFlyDmgMul.get().floatValue();
    }

    @Override
    protected void removeImpl(Player player) {

    }

    @Override
    protected void tickImpl(Player player) {
    }

    @Override
    public void onPlayerDamageTargetFinal(Player player, DamageData.DefenceMax cache) {
        List<DamageSource> types = List.of(CCDamageTypes.magic(player), CCDamageTypes.abyss(player));
        if (types.stream().noneMatch(source -> source.type().equals(cache.getSource().type()))) {
            SchedulerHandler.schedule(() -> {
                LivingEntity target = cache.getTarget();
                target.hurt(types.get(player.getRandom().nextInt(types.size())), cache.getDamageFinal() * dmgMul());
            });
        }
    }

    @Override
    public void addText(@Nullable Level level, List<Component> list) {
        list.add(TextFacet.set(level, CAItems.DeerButterflySet()));
        list.add(TextFacet.wrap(CALang.Sets.DEER_BUTTERFLY_0.get()));
        list.add(TextFacet.inner(CALang.Sets.DEER_BUTTERFLY_1.get(TextFacet.perc(dmgMul()))));
    }
}
