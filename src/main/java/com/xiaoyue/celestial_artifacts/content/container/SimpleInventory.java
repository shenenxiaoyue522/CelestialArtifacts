package com.xiaoyue.celestial_artifacts.content.container;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public class SimpleInventory extends SimpleContainer {
    private final ItemStack stack;

    public SimpleInventory(ItemStack stack, int expectedSize) {
        super(expectedSize);
        this.stack = stack;
        var contents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        contents.copyInto(getItems());
    }

    @Override
    public boolean stillValid(Player player) {
        return !stack.isEmpty();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        ItemContainerContents contents = ItemContainerContents.fromItems(getItems());
        if (contents.equals(ItemContainerContents.EMPTY)) {
            stack.remove(DataComponents.CONTAINER);
        } else {
            stack.set(DataComponents.CONTAINER, contents);
        }
    }
}
