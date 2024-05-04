package io.github.kydzombie.stapitech.block;

import io.github.kydzombie.stapitech.StapiTech;
import io.github.kydzombie.stapitech.api.energy.EnergyUtils;
import io.github.kydzombie.stapitech.api.energy.HasEnergyIO;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.state.StateManager;
import net.modificationstation.stationapi.api.state.property.BooleanProperty;
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity;
import net.modificationstation.stationapi.api.util.Identifier;

import java.util.Random;

abstract public class MachineBlock extends TemplateBlockWithEntity {
    private static boolean BEING_REPLACED = false;
    public static final BooleanProperty ON_PROPERTY = BooleanProperty.of("on");
    private static final Random random = new Random();

    public MachineBlock(Identifier identifier, Material material) {
        super(identifier, material);
        setDefaultState(getDefaultState().with(ON_PROPERTY, false));
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(ON_PROPERTY);
    }

    public static void setOn(World world, int x, int y, int z, boolean on) {
        var curr = world.getBlockState(x, y, z);
        if (curr.get(ON_PROPERTY) != on) {
            var blockEntity = world.getBlockEntity(x, y, z);
            BEING_REPLACED = true;
            StapiTech.LOGGER.info("Would have turned {} ({}, {}, {}) {}.",
                    blockEntity.getClass().getName(), x, y, z, on ? "on" : "off");
//            world.setBlockStateWithNotify(x, y, z, curr.with(ON_PROPERTY, on));
//            world.method_157(x, y, z, blockEntity);
            BEING_REPLACED = false;
        }
    }

    @Override
    public void onBlockPlaced(World world, int x, int y, int z, BlockState replacedState) {
        if (BEING_REPLACED) return;
        super.onBlockPlaced(world, x, y, z, replacedState);

        var blockEntity = world.getBlockEntity(x, y, z);
        if (blockEntity instanceof HasEnergyIO) {
            EnergyUtils.notifyRelevantConnections(world, x, y, z);
        }
    }

    @Override
    public void onBreak(World world, int x, int y, int z) {
        if (BEING_REPLACED) {
            super.onBreak(world, x, y, z);
            return;
        }

        var blockEntity = world.getBlockEntity(x, y, z);
        if (blockEntity instanceof Inventory inventory) {
            for (int slot = 0; slot < inventory.size(); ++slot) {
                ItemStack stack = inventory.getStack(slot);
                if (stack == null) continue;

                float var8 = random.nextFloat() * 0.8F + 0.1F;
                float var9 = random.nextFloat() * 0.8F + 0.1F;
                float var10 = random.nextFloat() * 0.8F + 0.1F;

                while (stack.count > 0) {
                    int var11 = random.nextInt(21) + 10;
                    if (var11 > stack.count) {
                        var11 = stack.count;
                    }

                    stack.count -= var11;
                    ItemEntity itemEntity = new ItemEntity(world, (float) x + var8, (float) y + var9, (float) z + var10, new ItemStack(stack.itemId, var11, stack.getDamage()));
                    float var13 = 0.05F;
                    itemEntity.velocityX = (float) random.nextGaussian() * var13;
                    itemEntity.velocityY = (float) random.nextGaussian() * var13 + 0.2F;
                    itemEntity.velocityZ = (float) random.nextGaussian() * var13;
                    world.method_210(itemEntity);
                }
            }
        }
        if (blockEntity instanceof HasEnergyIO) {
            EnergyUtils.notifyRelevantConnections(world, x, y, z);
        }
        super.onBreak(world, x, y, z);
    }
}
