package io.github.kydzombie.stapitech.api.energy;

import net.modificationstation.stationapi.api.util.math.Direction;

public record EnergyConnection(HasEnergyIO machine, Direction side) {}
