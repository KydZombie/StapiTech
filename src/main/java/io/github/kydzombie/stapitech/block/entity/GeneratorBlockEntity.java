package io.github.kydzombie.stapitech.block.entity;

import io.github.kydzombie.stapitech.api.energy.*;
import io.github.kydzombie.stapitech.block.MachineBlock;
import io.github.kydzombie.stapitech.block.OrientableMachineBlock;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.recipe.FuelRegistry;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;

public class GeneratorBlockEntity extends BlockEntity implements SimpleInventory, HasEnergy, HasEnergyIO, HasEnergyConnections {
    @Getter @Setter private ItemStack[] inventory = new ItemStack[2];
    @Getter @Setter private int energy = 0;

    private static final int BASE_FUEL_CHARGE_PER_TICK = 10;
    private static final int MAX_AMOUNT_SENT_PER_TICK = 100;
    private static final int MAX_AMOUNT_SENT_PER_TICK_PER_MACHINE = 20;

    EnumMap<Direction, List<EnergyConnection>> energyConnections;
    private boolean connectionsDirty = true;

    public int burnTime = 0;
    public int fuelTime = 0;

    @Override
    public String getName() {
        return "Generator";
    }

    @Override
    public int getMaxEnergy() {
        return 64000;
    }

    @Override
    public EnumSet<EnergyConnectionType> getConnectionType(Direction side) {
        var state = world.getBlockState(x, y, z);
        return side != state.get(OrientableMachineBlock.FACING_PROPERTY) ?
                EnumSet.of(EnergyConnectionType.OUTPUT) : EnumSet.noneOf(EnergyConnectionType.class);
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        readEnergyNbt(nbt);
        readInventoryNbt(nbt);
        burnTime = nbt.getShort("burn_time");
        fuelTime = FuelRegistry.getFuelTime(inventory[1]);
    }

    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        writeEnergyNbt(nbt);
        writeInventoryNbt(nbt);
        nbt.putShort("burn_time", (short)burnTime);
    }

    @Environment(EnvType.CLIENT)
    public boolean isBurning() {
        return burnTime > 0;
    }

    @Environment(EnvType.CLIENT)
    public int getFuelProgress(int textureWidth) {
        if (this.fuelTime == 0) {
            this.fuelTime = 200;
        }

        return this.burnTime * textureWidth / this.fuelTime;
    }

    @Override
    public void tick() {
        if (world.isRemote) return;

        if (burnTime > 0) {
            chargeEnergy(BASE_FUEL_CHARGE_PER_TICK);
            burnTime--;
        } else if (getEnergy() != getMaxEnergy()) {
            fuelTime = FuelRegistry.getFuelTime(inventory[0]);
            burnTime = fuelTime;
            if (fuelTime > 0) {
                inventory[0].count--;
                if (inventory[0].count <= 0) {
                    inventory[0] = null;
                }
                MachineBlock.setOn(world, x, y, z, true);
            } else {
                MachineBlock.setOn(world, x, y, z, false);
            }
        } else {
            MachineBlock.setOn(world, x, y, z, false);
        }

        if (energyConnections == null || connectionsDirty) {
            var directions = new ArrayList<>(List.of(Direction.values()));
            var state = world.getBlockState(x, y, z);
            directions.remove(state.get(OrientableMachineBlock.FACING_PROPERTY));
            energyConnections = EnergyUtils.findUniqueMachineConnections(
                    world,
                    x,
                    y,
                    z,
                    directions.toArray(Direction[]::new),
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
