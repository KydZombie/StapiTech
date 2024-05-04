package io.github.kydzombie.stapitech.block.entity;

import io.github.kydzombie.stapitech.api.energy.HasEnergy;
import io.github.kydzombie.stapitech.api.energy.HasEnergyIO;
import io.github.kydzombie.stapitech.block.MachineBlock;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

public abstract class ProcessingMachineEntity extends BlockEntity implements SimpleInventory, HasEnergy, HasEnergyIO {
    @Getter @Setter protected ItemStack[] inventory;
    @Getter @Setter protected int energy = 0;

    public int cookTime = 0;

    public ProcessingMachineEntity(int inventoryLength) {
        inventory = new ItemStack[inventoryLength];
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        readEnergyNbt(nbt);
        readInventoryNbt(nbt);

        cookTime = nbt.getShort("cook_time");
    }

    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        writeEnergyNbt(nbt);
        writeInventoryNbt(nbt);

        nbt.putShort("cook_time", (short)cookTime);
    }

    @Environment(EnvType.CLIENT)
    public int getCookProgress(int textureWidth) {
        return cookTime * textureWidth / 200;
    }

    protected record MachineOutput(ItemStack stack, int energyUsage, int recipeCookTime) {}


    protected abstract @Nullable MachineOutput getOutput();


    @Override
    public void tick() {
        if (!world.isRemote) {
            var output = getOutput();
            if (output == null) {
                cookTime = 0;
                MachineBlock.setOn(world, x, y, z, false);
                return;
            }

            if (energy >= output.energyUsage) {
                MachineBlock.setOn(world, x, y, z, true);
                cookTime++;
                consumeEnergy(output.energyUsage);
            } else {
                cookTime = 0;
            }

            if (cookTime >= output.recipeCookTime) {
                inventory[0].count--;
                if (inventory[0].count <= 0) {
                    inventory[0] = null;
                }
                cookTime = 0;
                if (inventory[1] == null) {
                    inventory[1] = output.stack.copy();
                } else {
                    inventory[1].count += output.stack.count;
                }
                markDirty();
            }
        }
    }
}


