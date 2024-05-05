package io.github.kydzombie.stapitech.gui.screen;

import io.github.kydzombie.stapitech.block.entity.BatteryBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_633;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import java.util.List;

public class BatteryBlockScreenHandler extends ScreenHandler {
    private final BatteryBlockEntity blockEntity;

    private int cachedEnergy = 0;

    public BatteryBlockScreenHandler(PlayerEntity player, BatteryBlockEntity blockEntity) {
        this.blockEntity = blockEntity;

        addSlot(new Slot(blockEntity, 0, 80, 53));
        addSlot(new Slot(blockEntity, 1, 80, 17));

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
        listener.method_2099(this, 0, blockEntity.getEnergy());
    }

    @Override
    public void method_2075() {
        super.method_2075();

        for (var listener : (List<class_633>) listeners) {
            if (cachedEnergy != blockEntity.getEnergy()) {
                listener.method_2099(this, 0, blockEntity.getEnergy());
            }
        }

        cachedEnergy = blockEntity.getEnergy();
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void method_2077(int id, int val) {
        switch (id) {
            case 0:
                blockEntity.setEnergy(val);
                break;
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return blockEntity.canPlayerUse(player);
    }
}
