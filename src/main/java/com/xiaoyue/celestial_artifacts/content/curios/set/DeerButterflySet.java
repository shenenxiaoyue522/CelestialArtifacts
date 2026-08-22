package com.xiaoyue.celestial_artifacts.content.curios.set;

import com.xiaoyue.celestial_artifacts.content.core.modular.TextFacet;
import com.xiaoyue.celestial_artifacts.content.core.token.BaseTickingToken;
import com.xiaoyue.celestial_artifacts.content.core.token.CAAttackToken;
import com.xiaoyue.celestial_artifacts.data.CALang;
import com.xiaoyue.celestial_artifacts.data.CAModConfig;
import com.xiaoyue.celestial_artifacts.register.CAItems;
import com.xiaoyue.celestial_core.data.CCDamageTypes;
import com.xiaoyue.celestial_invoker.content.common.helper.CooldownHelper;
import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2library.init.events.GeneralEventHandler;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SerialClass
public class DeerButterflySet extends BaseTickingToken implements CAAttackToken {
    public static final String COOLDOWN_ID = "celestial_artifacts:deer_butterfly_set";

    private static float dmgMul() {
        return CAModConfig.COMMON.set.deerButterFlyDmgMul.get().floatValue();
    }

    @Override
    protected void removeImpl(Player player) {

    }

    @Override
    protected void tickImpl(Player player) {
    }

    @Override
    public void onPlayerDamageTargetFinal(Player player, AttackCache cache) {
        List<DamageSource> types = List.of(CCDamageTypes.magic(player), CCDamageTypes.abyss(player));
        if (types.stream().noneMatch(source -> {
            LivingDamageEvent event = cache.getLivingDamageEvent();
            return source.type().equals(event.getSource().type());
        }) && CooldownHelper.isCooldownReady(player, COOLDOWN_ID)) {
            GeneralEventHandler.schedule(() -> {
                LivingEntity target = cache.getAttackTarget();
                target.hurt(types.get(player.getRandom().nextInt(types.size())), cache.getDamageDealt() * dmgMul());
                CooldownHelper.setCooldown(player, COOLDOWN_ID, 10);
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
