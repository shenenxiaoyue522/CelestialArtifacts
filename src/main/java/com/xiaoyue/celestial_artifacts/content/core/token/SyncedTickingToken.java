package com.xiaoyue.celestial_artifacts.content.core.token;

import dev.xkmc.l2core.capability.conditionals.NetworkSensitiveToken;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public abstract class SyncedTickingToken<T extends SyncedTickingToken<T>> extends BaseTickingToken implements NetworkSensitiveToken<T> {

    @Override
    public void onSync(@Nullable T old, Player player) {
        update();
    }
}
