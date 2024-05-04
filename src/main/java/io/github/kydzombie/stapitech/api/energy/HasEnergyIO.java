package io.github.kydzombie.stapitech.api.energy;

import io.github.kydzombie.stapitech.block.OrientableMachineBlock;
import net.minecraft.block.entity.BlockEntity;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.EnumSet;

public interface HasEnergyIO {
    default EnumSet<EnergyConnectionType> getConnectionType(Direction side) {
        if (this instanceof BlockEntity blockEntity) {
            var state = blockEntity.world.getBlockState(blockEntity.x, blockEntity.y, blockEntity.z);
            if (state.getProperties().contains(OrientableMachineBlock.FACING_PROPERTY)) {
                return side != state.get(OrientableMachineBlock.FACING_PROPERTY) ?
                        EnumSet.of(EnergyConnectionType.INPUT) : EnumSet.noneOf(EnergyConnectionType.class);
            }
        }
        return EnumSet.of(EnergyConnectionType.INPUT);
    }

    default EnumSet<EnergyConnectionType> getConnectionType(BlockEntity source, Direction side) {
        return getConnectionType(side);
    }

    /**
     * @param target What to send the energy to.
     * @param side The side of the target block that power is being sent to.
     * @param amount The amount of energy you are trying to send.
     * @return The amount of energy that was actually sent.
     */
    default int sendEnergyToSide(HasEnergyIO target, Direction side, int amount) {
        var connectionType = (this instanceof BlockEntity) ? target.getConnectionType((BlockEntity) this, side) : target.getConnectionType(side);
        if (connectionType.contains(EnergyConnectionType.INPUT) && this instanceof HasEnergy energyStorage && target instanceof HasEnergy targetEnergyStorage) {
            var sent = Math.min(Math.min(targetEnergyStorage.getEnergyRemainingSpace(), amount), energyStorage.getEnergy());
            energyStorage.setEnergy(energyStorage.getEnergy() - sent);
            targetEnergyStorage.setEnergy(targetEnergyStorage.getEnergy() + sent);
            return sent;
        } else {
            return 0;
        }
    }
}
