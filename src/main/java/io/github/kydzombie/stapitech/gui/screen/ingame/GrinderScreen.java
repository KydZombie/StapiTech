package io.github.kydzombie.stapitech.gui.screen.ingame;

import io.github.kydzombie.stapitech.block.entity.GrinderBlockEntity;
import io.github.kydzombie.stapitech.gui.screen.GrinderScreenHandler;
import net.minecraft.entity.player.PlayerEntity;

public class GrinderScreen extends ProcessingMachineScreen {
    private final GrinderBlockEntity blockEntity;

    public GrinderScreen(PlayerEntity player, GrinderBlockEntity blockEntity) {
        super(new GrinderScreenHandler(player, blockEntity), "Grinder", "assets/stapitech/gui/grinder.png");
        this.blockEntity = blockEntity;
    }


    @Override
    protected int getEnergy() {
        return blockEntity.getEnergy();
    }

    @Override
    protected int getMaxEnergy() {
        return blockEntity.getMaxEnergy();
    }

    @Override
    protected int getCookProgress() {
        return blockEntity.getCookProgress(24);
    }
}
