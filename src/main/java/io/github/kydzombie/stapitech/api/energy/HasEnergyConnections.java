package io.github.kydzombie.stapitech.api.energy;

import java.util.List;

public interface HasEnergyConnections {
    List<EnergyConnection> getEnergyConnections();

    void markConnectionsDirty();
}
