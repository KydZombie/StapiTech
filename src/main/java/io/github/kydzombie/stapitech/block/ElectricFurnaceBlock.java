package io.github.kydzombie.stapitech.block;

import io.github.kydzombie.stapitech.StapiTech;
import io.github.kydzombie.stapitech.block.entity.ElectricFurnaceBlockEntity;
import io.github.kydzombie.stapitech.gui.screen.ElectricFurnaceScreenHandler;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.util.Identifier;

public class ElectricFurnaceBlock extends OrientableMachineBlock {
    public ElectricFurnaceBlock(Identifier identifier, Material material) {
        super(identifier, material);
        setTranslationKey(identifier);
        setHardness(3.5f);
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new ElectricFurnaceBlockEntity();
    }

    @Override
    public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
        var blockEntity = (ElectricFurnaceBlockEntity) world.getBlockEntity(x, y, z);
        GuiHelper.openGUI(
                player,
                StapiTech.NAMESPACE.id("electric_furnace"),
                blockEntity,
                new ElectricFurnaceScreenHandler(player, blockEntity)
        );
        return true;
    }
}
