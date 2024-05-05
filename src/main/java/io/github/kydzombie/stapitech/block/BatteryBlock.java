package io.github.kydzombie.stapitech.block;

import io.github.kydzombie.stapitech.StapiTech;
import io.github.kydzombie.stapitech.api.energy.EnergyUtils;
import io.github.kydzombie.stapitech.block.entity.BatteryBlockEntity;
import io.github.kydzombie.stapitech.gui.screen.BatteryBlockScreenHandler;
import net.danygames2014.uniwrench.api.WrenchMode;
import net.danygames2014.uniwrench.api.Wrenchable;
import net.danygames2014.uniwrench.item.WrenchBase;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.gui.screen.container.GuiHelper;
import net.modificationstation.stationapi.api.item.ItemPlacementContext;
import net.modificationstation.stationapi.api.state.StateManager;
import net.modificationstation.stationapi.api.state.property.EnumProperty;
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.StringIdentifiable;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.EnumMap;
import java.util.Random;

public class BatteryBlock extends TemplateBlockWithEntity implements Wrenchable {
    public static final EnumMap<Direction, EnumProperty<BatteryConnectionType>> DIRECTION_PROPERTIES = new EnumMap<>(Direction.class) {{
        put(Direction.NORTH, EnumProperty.of("connection_north", BatteryConnectionType.class));
        put(Direction.SOUTH, EnumProperty.of("connection_south", BatteryConnectionType.class));
        put(Direction.EAST, EnumProperty.of("connection_east", BatteryConnectionType.class));
        put(Direction.WEST, EnumProperty.of("connection_west", BatteryConnectionType.class));
        put(Direction.UP, EnumProperty.of("connection_up", BatteryConnectionType.class));
        put(Direction.DOWN, EnumProperty.of("connection_down", BatteryConnectionType.class));
    }};
    private static boolean BEING_REPLACED = false;

    private static final Random random = new Random();

    public BatteryBlock(Identifier identifier, Material material) {
        super(identifier, material);
        setTranslationKey(identifier);
        setHardness(3.5f);
        var state = getDefaultState();
        for (var prop : DIRECTION_PROPERTIES.values()) {
            state = state.with(prop, BatteryConnectionType.NONE);
        }
        setDefaultState(state);
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        for (var prop : DIRECTION_PROPERTIES.values()) {
            builder.add(prop);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return getDefaultState()
                .with(DIRECTION_PROPERTIES.get(context.getPlayerLookDirection().getOpposite()), BatteryConnectionType.INPUT)
                .with(DIRECTION_PROPERTIES.get(context.getPlayerLookDirection()), BatteryConnectionType.OUTPUT);
    }

    @Override
    protected BlockEntity createBlockEntity() {
        return new BatteryBlockEntity();
    }

    @Override
    public void onBlockPlaced(World world, int x, int y, int z, BlockState replacedState) {
        if (BEING_REPLACED) return;
        super.onBlockPlaced(world, x, y, z, replacedState);
        EnergyUtils.notifyRelevantConnections(world, x, y, z);
    }

    @Override
    public void onBreak(World world, int x, int y, int z) {
        if (BEING_REPLACED) {
            return;
        }
        super.onBreak(world, x, y, z);
        EnergyUtils.notifyRelevantConnections(world, x, y, z);
    }

    @Override
    public boolean onUse(World world, int x, int y, int z, PlayerEntity player) {
        if (player.getHand() != null && player.getHand().getItem() instanceof WrenchBase) {
            return super.onUse(world, x, y, z, player);
        }
        var blockEntity = (BatteryBlockEntity) world.getBlockEntity(x, y, z);
        GuiHelper.openGUI(
                player,
                StapiTech.NAMESPACE.id("battery_block"),
                blockEntity,
                new BatteryBlockScreenHandler(player, blockEntity)
        );
        return true;
    }

    @Override
    public void wrenchRightClick(ItemStack stack, PlayerEntity player, boolean isSneaking, World world, int x, int y, int z, int side, WrenchMode wrenchMode) {
        if (wrenchMode != WrenchMode.MODE_WRENCH) return;
        var state = world.getBlockState(x, y, z);
        var stateSide = DIRECTION_PROPERTIES.get(Direction.values()[side]);
        var value = isSneaking ? state.get(stateSide).getNext() : state.get(stateSide).getPrevious();

        var blockEntity = world.getBlockEntity(x, y, z);
        world.setBlockStateWithNotify(x, y, z, state.with(stateSide, value));
        blockEntity.cancelRemoval();
        world.method_157(x, y, z, blockEntity);

        EnergyUtils.notifyRelevantConnections(world, x, y, z);
    }

    public enum BatteryConnectionType implements StringIdentifiable {
        INPUT,
        OUTPUT,
        MIXED,
        NONE;

        private static final BatteryConnectionType[] vals = values();

        public BatteryConnectionType getNext() {
            return vals[(ordinal() - 1  + vals.length) % vals.length];
        }
        public BatteryConnectionType getPrevious() {
            return vals[(this.ordinal() + 1) % vals.length];
        }

        @Override
        public String asString() {
            return switch (this) {
                case INPUT -> "input";
                case OUTPUT -> "output";
                case MIXED -> "mixed";
                case NONE -> "none";
            };
        }
    }
}
