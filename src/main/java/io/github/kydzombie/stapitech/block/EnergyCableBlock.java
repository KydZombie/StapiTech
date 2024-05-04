package io.github.kydzombie.stapitech.block;

import io.github.kydzombie.stapitech.api.energy.EnergyUtils;
import io.github.kydzombie.stapitech.api.energy.HasEnergyIO;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.state.StateManager;
import net.modificationstation.stationapi.api.state.property.BooleanProperty;
import net.modificationstation.stationapi.api.template.block.TemplateBlock;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.api.world.BlockStateView;

import java.util.ArrayList;

public class EnergyCableBlock extends TemplateBlock implements HasEnergyIO {
    private static final BooleanProperty NORTH = BooleanProperty.of("north");
    private static final BooleanProperty SOUTH = BooleanProperty.of("south");
    private static final BooleanProperty EAST = BooleanProperty.of("east");
    private static final BooleanProperty WEST = BooleanProperty.of("west");
    private static final BooleanProperty UP = BooleanProperty.of("up");
    private static final BooleanProperty DOWN = BooleanProperty.of("down");

    private static final float CABLE_WIDTH = 0.4f;

    private static final float MIN_SIZE = .5f - CABLE_WIDTH / 2;
    private static final float MAX_SIZE = .5f + CABLE_WIDTH / 2;

    public EnergyCableBlock(Identifier identifier, Material material) {
        super(identifier, material);
        setTranslationKey(identifier);
        setHardness(0.8f);
        setDefaultState(
                getDefaultState()
                        .with(NORTH, false)
                        .with(SOUTH, false)
                        .with(EAST, false)
                        .with(WEST, false)
                        .with(UP, false)
                        .with(DOWN, false)
        );
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }

    public static Direction[] getConnections(BlockState blockState) {
        var directions = new ArrayList<Direction>();
        if (blockState.get(NORTH)) directions.add(Direction.NORTH);
        if (blockState.get(SOUTH)) directions.add(Direction.SOUTH);
        if (blockState.get(EAST)) directions.add(Direction.EAST);
        if (blockState.get(WEST)) directions.add(Direction.WEST);
        if (blockState.get(UP)) directions.add(Direction.UP);
        if (blockState.get(DOWN)) directions.add(Direction.DOWN);
        return directions.toArray(Direction[]::new);
    }

    public static Direction[] getConnections(World world, int x, int y, int z) {
        return getConnections(world.getBlockState(x, y, z));
    }

    private boolean checkConnection(World world, int x, int y, int z, Direction side) {
        if (world.getBlockState(x, y, z).getBlock() instanceof EnergyCableBlock) {
            return true;
        } else if (world.getBlockEntity(x, y, z) instanceof HasEnergyIO blockEntity) {
            return !blockEntity.getConnectionType(side.getOpposite()).isEmpty();
        }
        return false;
    }

    @Override
    public void neighborUpdate(World world, int x, int y, int z, int id) {
        world.setBlockState(
                x, y, z, world.getBlockState(x, y, z)
                        .with(NORTH, checkConnection(world, x - 1, y, z, Direction.NORTH))
                        .with(SOUTH, checkConnection(world, x + 1, y, z, Direction.SOUTH))
                        .with(EAST, checkConnection(world, x, y, z - 1, Direction.EAST))
                        .with(WEST, checkConnection(world, x, y, z + 1, Direction.WEST))
                        .with(UP, checkConnection(world, x, y + 1, z, Direction.UP))
                        .with(DOWN, checkConnection(world, x, y - 1, z, Direction.DOWN))
        );
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        var world = context.getWorld();
        var pos = context.getBlockPos();
        var x = pos.x;
        var y = pos.y;
        var z = pos.z;
        return getDefaultState()
                .with(NORTH, checkConnection(world, x - 1, y, z, Direction.NORTH))
                .with(SOUTH, checkConnection(world, x + 1, y, z, Direction.SOUTH))
                .with(EAST, checkConnection(world, x, y, z - 1, Direction.EAST))
                .with(WEST, checkConnection(world, x, y, z + 1, Direction.WEST))
                .with(UP, checkConnection(world, x, y + 1, z, Direction.UP))
                .with(DOWN, checkConnection(world, x, y - 1, z, Direction.DOWN));
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    protected Box getOutline(BlockState blockState, int x, int y, int z) {
        var maxX = blockState.get(SOUTH) ? 1f : MAX_SIZE;
        var minX = blockState.get(NORTH) ? 0f : MIN_SIZE;
        var maxY = blockState.get(UP) ? 1f : MAX_SIZE;
        var minY = blockState.get(DOWN) ? 0f : MIN_SIZE;
        var maxZ = blockState.get(WEST) ? 1f : MAX_SIZE;
        var minZ = blockState.get(EAST) ? 0f : MIN_SIZE;
        return Box.createCached(
                x + minX,
                y + minY,
                z + minZ,
                x + maxX,
                y + maxY,
                z + maxZ
        );
    }

    @Override
    public Box getCollisionShape(World world, int x, int y, int z) {
        var blockState = world.getBlockState(x, y, z);
        if (blockState.getBlock() instanceof EnergyCableBlock) {
            return getOutline(blockState, x, y, z);
        } else {
            return super.getCollisionShape(world, x, y, z);
        }
    }

    @Override
    public Box getBoundingBox(World world, int x, int y, int z) {
        return getOutline(world.getBlockState(x, y, z), x, y, z);
    }

    @Override
    public void updateBoundingBox(BlockView blockView, int x, int y, int z) {
        var blockState = ((BlockStateView) blockView).getBlockState(x, y, z);
        var maxX = blockState.get(SOUTH) ? 1f : MAX_SIZE;
        var minX = blockState.get(NORTH) ? 0f : MIN_SIZE;
        var maxY = blockState.get(UP) ? 1f : MAX_SIZE;
        var minY = blockState.get(DOWN) ? 0f : MIN_SIZE;
        var maxZ = blockState.get(WEST) ? 1f : MAX_SIZE;
        var minZ = blockState.get(EAST) ? 0f : MIN_SIZE;
        setBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public void onBreak(World world, int x, int y, int z) {
        EnergyUtils.notifyRelevantConnections(world, x, y, z);
        super.onBreak(world, x, y, z);
    }

    @Override
    public void onPlaced(World world, int x, int y, int z) {
        EnergyUtils.notifyRelevantConnections(world, x, y, z);
        super.onPlaced(world, x, y, z);
    }
}
