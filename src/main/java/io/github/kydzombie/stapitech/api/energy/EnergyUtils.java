package io.github.kydzombie.stapitech.api.energy;

import io.github.kydzombie.stapitech.block.EnergyCableBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.*;

public class EnergyUtils {
    public static void notifyRelevantConnections(World world, int x, int y, int z) {
        var connections = findAllMachineConnections(world, x, y, z, Direction.values(), EnumSet.allOf(EnergyConnectionType.class));
        for (var direction : connections.keySet()) {
            for (var connection : connections.get(direction)) {
                if (connection.machine() instanceof HasEnergyConnections blockWithConnections) {
                    blockWithConnections.markConnectionsDirty();
                }
            }
        }
    }

    public static List<EnergyConnection> findUniqueMachineConnections(World world, int x, int y, int z, Direction direction, EnumSet<EnergyConnectionType> connectionTypes) {
        var unique = new ArrayList<EnergyConnection>();
        var connections = findAllMachineConnections(world, x, y, z, direction, connectionTypes);
        for (EnergyConnection connection : connections) {
            if (unique.stream().noneMatch(uniqueConnection -> connection.machine() == uniqueConnection.machine())) {
                unique.add(connection);
            }
        }
        return unique;
    }

    public static EnumMap<Direction, List<EnergyConnection>> findUniqueMachineConnections(World world, int x, int y, int z, Direction[] directions, EnumSet<EnergyConnectionType> connectionTypes) {
        var map = new EnumMap<Direction, List<EnergyConnection>>(Direction.class);
        for (var direction : directions) {
            map.put(direction, findUniqueMachineConnections(world, x, y, z, direction, connectionTypes));
        }
        return map;
    }

    public static List<EnergyConnection> findAllMachineConnections(World world, int x, int y, int z, Direction startDirection, EnumSet<EnergyConnectionType> connectionTypes) {
        var connections = new ArrayList<EnergyConnection>();
        var cablesToCheck = new LinkedList<BlockPos>() {{
            add(DirectionalBlockPos.getFromDirection(x, y, z, startDirection).toBlockPos());
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

        var adjPos = DirectionalBlockPos.getFromDirection(x, y, z, startDirection);
        if (world.getBlockEntity(adjPos.x, adjPos.y, adjPos.z) instanceof HasEnergyIO machine &&
                !Collections.disjoint(machine.getConnectionType(adjPos.side.getOpposite()),connectionTypes)) {
            var connection = new EnergyConnection(machine, adjPos.side.getOpposite());
            if (!connections.contains(connection)) {
                connections.add(connection);
            }
        }

        return connections;
    }

    public static EnumMap<Direction, List<EnergyConnection>> findAllMachineConnections(World world, int x, int y, int z, Direction[] directions, EnumSet<EnergyConnectionType> connectionTypes) {
        var map = new EnumMap<Direction, List<EnergyConnection>>(Direction.class);
        for (var direction : directions) {
            map.put(direction, findAllMachineConnections(world, x, y, z, direction, connectionTypes));
        }
        return map;
    }

//    public static List<EnergyConnection> getAdjacentMachines(World world, int x, int y, int z, EnumSet<EnergyConnectionType> connectionTypes) {
//        var connections = new ArrayList<EnergyConnection>();
//
//        for (var direction : Direction.values()) {
//            var check = DirectionalBlockPos.getFromDirection(x, y, z, direction);
//            if (world.getBlockEntity(check.x, check.y, check.z) instanceof HasEnergyIO machine &&
//                    !Collections.disjoint(machine.getConnectionType(direction.getOpposite()),connectionTypes)) {
//                connections.add(new EnergyConnection(machine, direction.getOpposite()));
//            }
//        }
//
//        return connections;
//    }

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
