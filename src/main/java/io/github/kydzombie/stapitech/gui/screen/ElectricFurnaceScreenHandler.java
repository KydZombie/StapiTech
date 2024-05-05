package io.github.kydzombie.stapitech.gui.screen;

import io.github.kydzombie.stapitech.block.entity.ElectricFurnaceBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_633;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.FurnaceOutputSlot;
import net.minecraft.screen.slot.Slot;

import java.util.List;

public class ElectricFurnaceScreenHandler extends ScreenHandler {
    private final ElectricFurnaceBlockEntity blockEntity;

    private int cachedCookTime = 0;
    private int cachedEnergy = 0;

    public ElectricFurnaceScreenHandler(PlayerEntity player, ElectricFurnaceBlockEntity blockEntity) {
        this.blockEntity = blockEntity;

        addSlot(new Slot(blockEntity, 0, 56, 34));
        addSlot(new FurnaceOutputSlot(player, blockEntity, 1, 116, 35));

        int i;
        for(i = 0; i < 3; ++i) {
            for(int column = 0; column < 9; ++column) {
                addSlot(new Slot(player.inventory, column + i * 9 + 9, 8 + column * 18, 84 + i * 18));
            }
        }

        for(i = 0; i < 9; ++i) {
            addSlot(new Slot(player.inventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public void addListener(class_633 listener) {
        super.addListener(listener);
        listener.method_2099(this, 0, blockEntity.cookTime);
        listener.method_2099(this, 1, blockEntity.getEnergy());
    }

    @Override
    public void method_2075() {
        super.method_2075();

        for (var listener : (List<class_633>) listeners) {
            if (cachedCookTime != blockEntity.cookTime) {
                listener.method_2099(this, 0, blockEntity.cookTime);
            }

            if (cachedEnergy != blockEntity.getEnergy()) {
                listener.method_2099(this, 1, blockEntity.getEnergy());
            }
        }

        cachedCookTime = blockEntity.cookTime;
        cachedEnergy = blockEntity.getEnergy();
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void method_2077(int id, int val) {
        switch (id) {
            case 0:
                blockEntity.cookTime = val;
                break;
            case 1:
                blockEntity.setEnergy(val);
                break;
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return blockEntity.canPlayerUse(player);
    }
}
