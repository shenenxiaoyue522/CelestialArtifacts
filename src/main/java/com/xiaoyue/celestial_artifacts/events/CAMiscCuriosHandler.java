package com.xiaoyue.celestial_artifacts.events;

import com.xiaoyue.celestial_artifacts.content.curios.charm.CursedTotem;
import com.xiaoyue.celestial_artifacts.content.curios.charm.GluttonyBadge;
import com.xiaoyue.celestial_artifacts.content.curios.charm.SacrificialObject;
import com.xiaoyue.celestial_artifacts.content.curios.curse.CatastropheScroll;
import com.xiaoyue.celestial_artifacts.data.CAModConfig;
import com.xiaoyue.celestial_artifacts.data.CATagGen;
import com.xiaoyue.celestial_artifacts.register.CAItems;
import com.xiaoyue.celestial_artifacts.utils.CurioUtils;
import com.xiaoyue.celestial_core.events.api.DamageItemEvent;
import com.xiaoyue.celestial_core.utils.ItemUtils;
import com.xiaoyue.celestial_invoker.content.generic.shared.CompoundData;
import dev.xkmc.l2core.base.effects.EffectBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

import java.util.List;

import static com.xiaoyue.celestial_artifacts.CelestialArtifacts.MODID;

@EventBusSubscriber(modid = MODID)
public class CAMiscCuriosHandler {

	public static final String COPPER_REF = "copper_reinforced";
	public static final String AMETHYST_REF = "amethyst_reinforced";

	@SubscribeEvent
	public static void anvilRecipe(AnvilUpdateEvent event) {
		ItemStack left = event.getLeft().copy();
		ItemStack right = event.getRight();
		if (!left.isDamageableItem()) return;
		if (right.is(CAItems.COPPER_REINFORCE_PLATE.get())) {
			CompoundData.update(left, tag -> tag.putBoolean(COPPER_REF, true));
			ItemUtils.defaultAnvilOutput(event, left);
		}
		if (right.is(CAItems.AMETHYST_REINFORCE_PLATE.get())) {
			CompoundData.update(left, tag -> tag.putBoolean(AMETHYST_REF, true));
			ItemUtils.defaultAnvilOutput(event, left);
		}
	}

	@SubscribeEvent
	public static void onItemDamage(DamageItemEvent event) {
		ItemStack stack = event.getStack();
		CompoundData data = CompoundData.getOrCreate(stack);
		if (event.getEntity() == null) return;
		if (data.isEmpty()) return;
		if (data.tag().getBoolean(COPPER_REF)) {
			Double chance = CAModConfig.SERVER.misc.copperReinforceChance.get();
			event.setAmount(ItemUtils.calculateRef(event.getEntity().getRandom(), event.getAmount(), chance));
		}
		if (data.tag().getBoolean(AMETHYST_REF)) {
			event.setAmount(Math.min(event.getAmount(), CAModConfig.SERVER.misc.amethystReinforceEffect.get()));
		}
	}

	@SubscribeEvent
	public static void playerTick(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		Level level = player.level();
		if (level.isClientSide()) return;
		ItemStack stack = player.getMainHandItem();
		if (stack.isDamageableItem() && stack.getMaxDamage() >= CatastropheScroll.getOriginTrigger()) {
			CatastropheScroll.Curses.ORIGIN.trigger(player);
		}
		for (var e : player.getArmorSlots()) {
			if (e.isEnchanted()) {
				CatastropheScroll.Curses.CHAOS.trigger(player);
				break;
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onEnderManAnger(EnderManAngerEvent event) {
		if (event.getPlayer() instanceof ServerPlayer sp) {
			var ender = event.getEntity();
			Vec3 vec3 = sp.getViewVector(1.0F).normalize();
			Vec3 vec31 = new Vec3(ender.getX() - sp.getX(), ender.getEyeY() - sp.getEyeY(), ender.getZ() - sp.getZ());
			double d0 = vec31.length();
			vec31 = vec31.normalize();
			double d1 = vec3.dot(vec31);
			if (d1 > (double) 1.0F - 0.025 / d0 && sp.hasLineOfSight(ender))
				CatastropheScroll.Curses.END.trigger(sp);
		}
	}

	@SubscribeEvent
	public static void onEquipEtching(CurioChangeEvent event) {
		if (event.getEntity() instanceof ServerPlayer sp && event.getTo().is(CATagGen.ETCHINGS)) {
			CatastropheScroll.Curses.TRUTH.trigger(sp);
		}
	}

	@SubscribeEvent
	public static void onArrowHit(ProjectileImpactEvent event) {
		Projectile projectile = event.getProjectile();
		Entity entity = event.getEntity();
		if (projectile instanceof AbstractArrow arrow) {
			if (arrow.getOwner() instanceof Player player) {
				if (CurioUtils.hasCurio(player, CAItems.FLAME_ARROW_BAG.get())) {
					entity.setRemainingFireTicks(CAModConfig.SERVER.back.flameArrowBagTime.get());
				}
			}
		}
	}

	@SubscribeEvent
	public static void onAddedEffect(MobEffectEvent.Added event) {
		MobEffectInstance instance = event.getEffectInstance();
		if (!(event.getEntity() instanceof Player player)) return;
		if (instance.getEffect().value().isBeneficial())
			CatastropheScroll.Curses.NIHILITY.trigger(player);
		if (instance.getEffect().value().getCategory() != MobEffectCategory.HARMFUL) return;
		if (!CatastropheScroll.Curses.NIHILITY.cursing(player)) return;
		double factor = CatastropheScroll.getNihilityCurse();
		new EffectBuilder(instance).setDuration((int) (instance.getDuration() * (1 + factor)));
	}

	@SubscribeEvent
	public static void onFinishItemUse(LivingEntityUseItemEvent.Finish event) {
		ItemStack itemStack = event.getItem();
		LivingEntity entity = event.getEntity();
		if (entity instanceof Player player) {
			if (CurioUtils.hasCurio(player, CAItems.GLUTTONY_BADGE.get())) {
				if (itemStack.getUseAnimation() == UseAnim.EAT) {
					player.addEffect(GluttonyBadge.effAtk());
					player.addEffect(GluttonyBadge.effReg());
				}
			}
		}
	}

	@SubscribeEvent
	public static void onStarItemUse(LivingEntityUseItemEvent.Tick event) {
		ItemStack itemStack = event.getItem();
		LivingEntity entity = event.getEntity();
		if (entity instanceof Player player) {
			if (CurioUtils.hasCurio(player, CAItems.SPIRIT_BRACELET.get())) {
				if (CurioUtils.isRangeUseAnim(itemStack.getUseAnimation())) {
					event.setDuration(event.getDuration() - 1);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onUseTotem(LivingUseTotemEvent event) {
		LivingEntity entity = event.getEntity();
		Entity attacker = event.getSource().getEntity();
		if (entity instanceof Player player) {
			if (CurioUtils.hasCurio(player, CAItems.CURSED_TOTEM.get())) {
				if (attacker instanceof LivingEntity livingEntity) {
					livingEntity.addEffect(CursedTotem.eff());
				}
			}
		}
	}

	@SubscribeEvent
	public static void onChangeLevel(PlayerEvent.PlayerChangedDimensionEvent event) {
		Player entity = event.getEntity();
		if (CurioUtils.hasCurio(entity, CAItems.TRAVELER_SCROLL.get())) {
			entity.addEffect(CAModConfig.SERVER.scroll.travelerScrollSpeedEffect());
			entity.addEffect(CAModConfig.SERVER.scroll.travelerScrollRegenEffect());
		}
	}

	@SubscribeEvent
	public static void onLivingDeath(LivingDeathEvent event) {
		if (event.getEntity() instanceof Player player) {
			if (CurioUtils.hasCurio(player, CAItems.SACRIFICIAL_OBJECT.get())) {
				SacrificialObject.onPlayerDeath(player);
			}
		}
	}

	public static void onEnchTable(@Nullable Slot slot, List<EnchantmentInstance> original) {
		if (slot != null && slot.container instanceof Inventory inv) {
			Player player = inv.player;
			int lv = CAModConfig.SERVER.pendant.chaoticPendantEnchantLevel.get();
			if (CurioUtils.hasCurio(player, CAItems.CHAOTIC_PENDANT.get())) {
				for (int i = 0; i < original.size(); i++) {
					var ins = original.get(i);
					int ilv = Math.max(ins.level, Math.min(ins.enchantment.value().getMaxLevel(), ins.level + lv));
					original.set(i, new EnchantmentInstance(ins.enchantment, ilv));
				}
			}
		}
	}
}
