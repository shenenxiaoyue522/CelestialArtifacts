package com.xiaoyue.celestial_artifacts.content.curios.charm;

import com.xiaoyue.celestial_artifacts.content.core.modular.TextFacet;
import com.xiaoyue.celestial_artifacts.content.core.token.BaseTickingToken;
import com.xiaoyue.celestial_artifacts.content.core.token.CAAttackToken;
import com.xiaoyue.celestial_artifacts.data.CALang;
import com.xiaoyue.celestial_artifacts.data.CAModConfig;
import com.xiaoyue.celestial_artifacts.register.CAEffects;
import com.xiaoyue.celestial_artifacts.utils.CurioUtils;
import com.xiaoyue.celestial_core.utils.EntityUtils;
import dev.xkmc.l2damagetracker.contents.attack.AttackCache;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.l2library.init.events.GeneralEventHandler;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SerialClass
public class DeerInscribedAmulet extends BaseTickingToken implements CAAttackToken {

    private static float bonus() {
        return CAModConfig.COMMON.charm.deerInscribedAmuletBlessBonus.get().floatValue();
    }

    private static int time() {
        return CAModConfig.COMMON.charm.deerInscribedAmuletStrengthInterval.get();
    }

    @SerialClass.SerialField
    private boolean strength;

    @Override
    protected void removeImpl(Player player) {
        strength = false;
    }

    @Override
    protected void tickImpl(Player player) {
        if (player.tickCount % time() * 20 == 0) {
            strength = true;
        }
    }

    @Override
    public void addText(@Nullable Level level, List<Component> list) {
        list.add(TextFacet.wrap(CALang.Charm.DEER_INSCRIBED_AMULET_1.get(TextFacet.num(time()), TextFacet.eff(CAEffects.ENFEEBLED_LACERATION.get()))));
        list.add(TextFacet.wrap(CALang.Charm.DEER_INSCRIBED_AMULET_2.get(TextFacet.perc(bonus()), TextFacet.perc(bonus()))));
    }

    @Override
    public void onPlayerHurtTarget(Player player, AttackCache cache) {
        LivingEntity target = cache.getAttackTarget();
        if (strength) {
            GeneralEventHandler.schedule(() -> {
                EntityUtils.addEct(target, CAEffects.ENFEEBLED_LACERATION.get(), 100);
                strength = false;
            });
        }
        if (CurioUtils.isCsOn(player)) {
            int mulBonus = CurioUtils.getCurseAmount(player) == 0 ? 8 : 8 - CurioUtils.getCurseAmount(player);
            cache.addHurtModifier(DamageModifier.multBase(mulBonus * bonus()));
        }
    }
}
