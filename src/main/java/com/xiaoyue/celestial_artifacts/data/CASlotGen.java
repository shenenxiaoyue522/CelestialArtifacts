package com.xiaoyue.celestial_artifacts.data;

import com.xiaoyue.celestial_artifacts.CelestialArtifacts;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import top.theillusivec4.curios.api.CuriosDataProvider;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import static com.xiaoyue.celestial_artifacts.CelestialArtifacts.MODID;

public class CASlotGen extends CuriosDataProvider {

	public CASlotGen(DataGenerator generator, CompletableFuture<HolderLookup.Provider> pvd, ExistingFileHelper helper) {
		super(MODID, generator.getPackOutput(), helper, pvd);
	}

	@Override
	public void generate(HolderLookup.Provider registries, ExistingFileHelper fileHelper) {
		for (Type type : Type.values()) {
			int order = 20 + type.ordinal();
			if (type == Type.CATASTROPHE) order = -5;
			createSlot(type.id()).order(order).icon(CelestialArtifacts.loc("slot/empty_" + type.id() + "_slot")).size(type.slot);
		}
		createEntities("player_vanilla").addPlayer().addSlots("back", "body", "bracelet", "charm", "head", "necklace", "ring");
		createEntities("player_celestial").addPlayer().addSlots(Type.SCROLL.id(), Type.PENDANT.id(), Type.HEART.id(), Type.CATASTROPHE.id(), Type.ETCHING.id());
	}

	public enum Type {
		SCROLL(1),
		PENDANT(1),
		HEART(1),
		CATASTROPHE(1),
		ETCHING(0);

		private final int slot;

		Type(int slot) {
			this.slot = slot;
		}
		public String id() {
			return name().toLowerCase(Locale.ROOT);
		}

		public int getSlot() {
			return slot;
		}
	}
}
