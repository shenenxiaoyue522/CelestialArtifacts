package com.xiaoyue.celestial_artifacts.content.effect;

import com.xiaoyue.celestial_artifacts.CelestialArtifacts;
import com.xiaoyue.celestial_core.content.generic.CelestialEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class EnfeebledLaceration extends CelestialEffect {
    public EnfeebledLaceration() {
        super(MobEffectCategory.HARMFUL, 0xffae0007);
        ResourceLocation id = CelestialArtifacts.loc("enfeebled_laceration");
        addAttributeModifier(Attributes.ATTACK_DAMAGE, id.withSuffix("attack_bonus"), -0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        addAttributeModifier(Attributes.MOVEMENT_SPEED, id.withSuffix("speed_bonus"), -0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
}
