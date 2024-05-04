package io.github.kydzombie.stapitech.block;

import io.github.kydzombie.stapitech.StapiTech;
import io.github.kydzombie.stapitech.block.entity.GrinderBlockEntity;
import io.github.kydzombie.stapitech.gui.screen.GrinderScreenHandler;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.util.Identifier;

public class GrinderBlock extends OrientableMachineBlock {
    public GrinderBlock(Identifier identifier, Material material) {
        super(identifier, material);
        setTranslationKey(identifier);
        setHardness(3.5f);
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new GrinderBlockEntity();
    }

    @Override
    public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
        var blockEntity = (GrinderBlockEntity) world.getBlockEntity(x, y, z);
        GuiHelper.openGUI(
                player,
                StapiTech.NAMESPACE.id("grinder"),
                blockEntity,
                new GrinderScreenHandler(player, blockEntity)
        );
        return true;
    }
}
