package com.xiaoyue.celestial_artifacts.register;

import com.xiaoyue.celestial_artifacts.CelestialArtifacts;
import com.xiaoyue.celestial_artifacts.content.core.modular.CurioCacheCap;
import com.xiaoyue.celestial_invoker.content.common.registrar.NeoForgeRegister;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class CAObjects {

    public static final NeoForgeRegister<AttachmentType<?>> TYPE = CelestialArtifacts.EXTRA.neoforgeRegister(NeoForgeRegistries.ATTACHMENT_TYPES);

    public static final Supplier<AttachmentType<CurioCacheCap>> CURIO_DATA = TYPE.object("curio_data", CurioCacheCap.HOLDER::type);

    public static void register() {
    }
}
