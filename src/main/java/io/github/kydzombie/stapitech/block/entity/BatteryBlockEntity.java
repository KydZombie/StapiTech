package io.github.kydzombie.stapitech.block.entity;

import io.github.kydzombie.stapitech.api.energy.*;
import io.github.kydzombie.stapitech.block.BatteryBlock;
import io.github.kydzombie.stapitech.block.OrientableMachineBlock;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.*;

public class BatteryBlockEntity extends BlockEntity implements SimpleInventory, HasEnergy, HasEnergyIO, HasEnergyConnections {
    @Getter @Setter private ItemStack[] inventory = new ItemStack[2];
    @Getter @Setter private int energy = 0;

    EnumMap<Direction, List<EnergyConnection>> energyConnections;

    private boolean connectionsDirty = true;

    private static final int MAX_AMOUNT_SENT_PER_TICK = 400;
    private static final int MAX_AMOUNT_SENT_PER_TICK_PER_MACHINE = 40;

    @Override
    public int getMaxEnergy() {
        return 32000;
    }

    public EnumMap<Direction, BatteryBlock.BatteryConnectionType> getSideConfig() {
        var state = world.getBlockState(x, y, z);
        var map = new EnumMap<Direction, BatteryBlock.BatteryConnectionType>(Direction.class);
        for (var side : BatteryBlock.DIRECTION_PROPERTIES.keySet()) {
            var setting = state.get(BatteryBlock.DIRECTION_PROPERTIES.get(side));
            map.put(side, setting);
        }
        return map;
    }

    @Override
    public EnumSet<EnergyConnectionType> getConnectionType(Direction side) {
        var sideConfig = getSideConfig();
        return switch (sideConfig.get(side)) {
            case INPUT -> EnumSet.of(EnergyConnectionType.INPUT);
            case OUTPUT -> EnumSet.of(EnergyConnectionType.OUTPUT);
            case MIXED -> EnumSet.of(EnergyConnectionType.INPUT, EnergyConnectionType.OUTPUT);
            case NONE -> EnumSet.noneOf(EnergyConnectionType.class);
        };
    }

    @Override
    public String getName() {
        return "Battery";
    }

    @Override
    public void tick() {
        if (world.isRemote) return;

        if (energyConnections == null || connectionsDirty) {
            var directions = getSideConfig();
            energyConnections = EnergyUtils.findUniqueMachineConnections(
                    world,
                    x,
                    y,
                    z,
                    Arrays.stream(Direction.values())
                            .filter(direction -> directions.get(direction) != BatteryBlock.BatteryConnectionType.NONE)
                            .toArray(Direction[]::new),
                    EnumSet.of(EnergyConnectionType.INPUT)
            );
            connectionsDirty = false;
        }

        var sent = 0;

        for (var direction : energyConnections.keySet()) {
            for (EnergyConnection connection : energyConnections.get(direction)) {
                var machine = connection.machine();
                sent += sendEnergyToSide(
                        machine,
                        connection.side(),
                        Math.min(
                                energy,
                                Math.min(
                                        MAX_AMOUNT_SENT_PER_TICK_PER_MACHINE,
                                        MAX_AMOUNT_SENT_PER_TICK - sent
                                )
                        )
                );
                if (sent >= MAX_AMOUNT_SENT_PER_TICK) return;
            }
        }
    }

    @Override
    public void markConnectionsDirty() {
        connectionsDirty = true;
    }
}
