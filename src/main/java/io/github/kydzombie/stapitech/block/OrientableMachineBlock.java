package io.github.kydzombie.stapitech.block;

import io.github.kydzombie.stapitech.api.energy.EnergyUtils;
import net.danygames2014.uniwrench.api.WrenchMode;
import net.danygames2014.uniwrench.api.Wrenchable;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.state.StateManager;
import net.modificationstation.stationapi.api.state.property.EnumProperty;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;

abstract public class OrientableMachineBlock extends MachineBlock implements Wrenchable {
    public static final EnumProperty<Direction> FACING_PROPERTY = EnumProperty.of("facing", Direction.class);

    public OrientableMachineBlock(Identifier identifier, Material material) {
        super(identifier, material);
        setDefaultState(getDefaultState().with(FACING_PROPERTY, Direction.NORTH));
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING_PROPERTY);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return getDefaultState().with(FACING_PROPERTY, context.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public void wrenchRightClick(ItemStack stack, PlayerEntity player, boolean isSneaking, World world, int x, int y, int z, int side, WrenchMode wrenchMode) {
        if (wrenchMode != WrenchMode.MODE_ROTATE || side == 0 || side == 1) return;
        world.setBlockStateWithNotify(x, y, z, world.getBlockState(x, y, z).with(FACING_PROPERTY, Direction.values()[side]));
        EnergyUtils.notifyRelevantConnections(world, x, y, z);
    }
}
