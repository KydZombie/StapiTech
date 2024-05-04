package io.github.kydzombie.stapitech.block;

import io.github.kydzombie.stapitech.StapiTech;
import io.github.kydzombie.stapitech.block.entity.GeneratorBlockEntity;
import io.github.kydzombie.stapitech.gui.screen.GeneratorScreenHandler;
import net.danygames2014.uniwrench.item.WrenchBase;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.util.Identifier;

public class GeneratorBlock extends OrientableMachineBlock {
    public GeneratorBlock(Identifier identifier, Material material) {
        super(identifier, material);
        setTranslationKey(identifier);
        setHardness(3.5f);
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new GeneratorBlockEntity();
    }

    @Override
    public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
        if (player.getHand() != null && player.getHand().getItem() instanceof WrenchBase) {
            return super.onUse(world, x, y, z, player);
        }
        var blockEntity = (GeneratorBlockEntity) world.getBlockEntity(x, y, z);
        GuiHelper.openGUI(
                player,
                StapiTech.NAMESPACE.id("generator"),
                blockEntity,
                new GeneratorScreenHandler(player, blockEntity)
        );
        return true;
    }
}
