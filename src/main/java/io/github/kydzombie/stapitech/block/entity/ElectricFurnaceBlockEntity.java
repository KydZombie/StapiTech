package io.github.kydzombie.stapitech.block.entity;

import net.modificationstation.stationapi.api.recipe.SmeltingRegistry;
import org.jetbrains.annotations.Nullable;

public class ElectricFurnaceBlockEntity extends ProcessingMachineEntity {
    public static final int ENERGY_USE_PER_TICK = 8;
    public static final int RECIPE_COOK_TIME = 200;

    public ElectricFurnaceBlockEntity() {
        super(2);
    }

    @Override
    public String getName() {
        return "Electric Furnace";
    }

    @Override
    public int getMaxEnergy() {
        return 32000;
    }

    @Override
    protected @Nullable MachineOutput getOutput() {
        if (inventory[0] == null) {
            return null;
        }
        var output = SmeltingRegistry.getResultFor(inventory[0]);
        if (output == null) {
            return null;
        } else if (inventory[1] != null) {
            if (!output.isItemEqual(inventory[1]) || output.count + inventory[1].count > output.getMaxCount()) {
                return null;
            }
        }
        return new MachineOutput(output, ENERGY_USE_PER_TICK, RECIPE_COOK_TIME);
    }
}
