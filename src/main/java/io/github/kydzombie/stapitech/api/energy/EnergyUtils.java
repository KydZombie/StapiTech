package io.github.kydzombie.stapitech.api.energy;

import io.github.kydzombie.stapitech.block.EnergyCableBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.*;

public class EnergyUtils {
    public static void notifyRelevantListeners(World world, int x, int y, int z) {
        var localConnections = findAllMachineConnections(world, x, y, z, EnumSet.allOf(EnergyConnectionType.class));
        for (var localConnection : localConnections) {
            if (localConnection.machine() instanceof HasEnergyConnections blockWithConnections) {
                blockWithConnections.markConnectionsDirty();
            }
        }
    }

    public static List<EnergyConnection> findUniqueMachineConnections(World world, int x, int y, int z, EnumSet<EnergyConnectionType> connectionTypes) {
        var unique = new ArrayList<EnergyConnection>();
        var connections = findAllMachineConnections(world, x, y, z, connectionTypes);
        for (EnergyConnection connection : connections) {
            if (unique.stream().noneMatch(uniqueConnection -> connection.machine() == uniqueConnection.machine())) {
                unique.add(connection);
            }
        }
        return unique;
    }

    public static List<EnergyConnection> findAllMachineConnections(World world, int x, int y, int z, EnumSet<EnergyConnectionType> connectionTypes) {
        var connections = new ArrayList<EnergyConnection>();
        var cablesToCheck = new LinkedList<BlockPos>() {{
            for (var direction : Direction.values()) {
                add(DirectionalBlockPos.getFromDirection(x, y, z, direction).toBlockPos());
            }
        }};
        var cablesChecked = new ArrayList<BlockPos>();
        while (!cablesToCheck.isEmpty()) {
            var check = cablesToCheck.pop();
            var cableState = world.getBlockState(check.x, check.y, check.z);
            if (!(cableState.getBlock() instanceof EnergyCableBlock)) {
                cablesChecked.add(check);
                continue;
            }
            for (var direction : EnergyCableBlock.getConnections(cableState)) {
                var newCheck = DirectionalBlockPos.getFromDirection(check.x, check.y, check.z, direction).toBlockPos();
                if (newCheck.x == x && newCheck.y == y && newCheck.z == z) continue;
                if (cablesChecked.contains(newCheck)) continue;
                var foundState = world.getBlockState(newCheck);
                if (foundState.getBlock() instanceof EnergyCableBlock) {
                    cablesToCheck.addFirst(newCheck);
                } else if (world.getBlockEntity(newCheck.x, newCheck.y, newCheck.z) instanceof HasEnergyIO machine &&
                        !Collections.disjoint(machine.getConnectionType(direction.getOpposite()),connectionTypes)) {
                    connections.add(new EnergyConnection(machine, direction.getOpposite()));
                }
            }
            cablesChecked.add(check);
        }

        var adjacent = getAdjacentMachines(world, x, y, z, connectionTypes);

        connections.removeAll(adjacent);
        connections.addAll(adjacent);

        return connections;
    }

    public static List<EnergyConnection> getAdjacentMachines(World world, int x, int y, int z, EnumSet<EnergyConnectionType> connectionTypes) {
        var connections = new ArrayList<EnergyConnection>();

        for (var direction : Direction.values()) {
            var check = DirectionalBlockPos.getFromDirection(x, y, z, direction);
            if (world.getBlockEntity(check.x, check.y, check.z) instanceof HasEnergyIO machine &&
                    !Collections.disjoint(machine.getConnectionType(direction.getOpposite()),connectionTypes)) {
                connections.add(new EnergyConnection(machine, direction.getOpposite()));
            }
        }

        return connections;
    }

    private record DirectionalBlockPos(int x, int y, int z, Direction side) {
        private static DirectionalBlockPos getFromDirection(int x, int y, int z, Direction direction) {
            return new DirectionalBlockPos(
                    x + direction.getOffsetX(),
                    y + direction.getOffsetY(),
                    z + direction.getOffsetZ(),
                    direction);
        }

        private BlockPos toBlockPos() {
            return new BlockPos(x, y, z);
        }
    }
}
