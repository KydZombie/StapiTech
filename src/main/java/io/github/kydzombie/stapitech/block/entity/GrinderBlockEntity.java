package io.github.kydzombie.stapitech.block.entity;

import io.github.kydzombie.stapitech.api.recipe.GrinderRecipeRegistry;
import org.jetbrains.annotations.Nullable;

public class GrinderBlockEntity extends ProcessingMachineEntity {
    public static final int ENERGY_USE_PER_TICK = 12;
    public static final int RECIPE_COOK_TIME = 200;

    public GrinderBlockEntity() {
        super(2);
    }

    @Override
    public String getName() {
        return "Grinder";
    }

    @Override
    public int getMaxEnergy() {
        return 32000;
    }

    @Override
    protected @Nullable MachineOutput getOutput() {
        var output = GrinderRecipeRegistry.getOutput(inventory[0]);
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
