package io.github.kydzombie.stapitech.gui.screen.ingame;

import io.github.kydzombie.stapitech.block.entity.ElectricFurnaceBlockEntity;
import io.github.kydzombie.stapitech.gui.screen.ElectricFurnaceScreenHandler;
import net.minecraft.entity.player.PlayerEntity;

public class ElectricFurnaceScreen extends ProcessingMachineScreen {
    private final ElectricFurnaceBlockEntity blockEntity;

    public ElectricFurnaceScreen(PlayerEntity player, ElectricFurnaceBlockEntity electricFurnaceBlockEntity) {
        super(
                new ElectricFurnaceScreenHandler(player, electricFurnaceBlockEntity),
                "Electric Furnace",
                "assets/stapitech/gui/electric_furnace.png"
        );
        this.blockEntity = electricFurnaceBlockEntity;
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
