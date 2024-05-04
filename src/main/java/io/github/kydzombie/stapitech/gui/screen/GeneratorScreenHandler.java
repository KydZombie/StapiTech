package io.github.kydzombie.stapitech.gui.screen;

import io.github.kydzombie.stapitech.block.entity.GeneratorBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class GeneratorScreenHandler extends ScreenHandler {
    private final GeneratorBlockEntity blockEntity;

    public GeneratorScreenHandler(PlayerEntity player, GeneratorBlockEntity blockEntity) {
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
    public boolean canUse(PlayerEntity player) {
        return blockEntity.canPlayerUse(player);
    }
}
