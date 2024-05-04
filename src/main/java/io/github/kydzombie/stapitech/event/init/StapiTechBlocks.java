package io.github.kydzombie.stapitech.event.init;

import io.github.kydzombie.stapitech.StapiTech;
import io.github.kydzombie.stapitech.block.*;
import io.github.kydzombie.stapitech.block.entity.BatteryBlockEntity;
import io.github.kydzombie.stapitech.block.entity.ElectricFurnaceBlockEntity;
import io.github.kydzombie.stapitech.block.entity.GeneratorBlockEntity;
import io.github.kydzombie.stapitech.block.entity.GrinderBlockEntity;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.block.Material;
import net.modificationstation.stationapi.api.event.block.entity.BlockEntityRegisterEvent;
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent;

public class StapiTechBlocks {
    public static GeneratorBlock generator;
    public static ElectricFurnaceBlock electricFurnace;
    public static GrinderBlock grinder;

    public static BatteryBlock batteryBlock;

    public static EnergyCableBlock energyCable;

    @EventListener
    private void registerBlocks(BlockRegistryEvent event) {
        StapiTech.LOGGER.info("Registering blocks...");
        generator = new GeneratorBlock(StapiTech.NAMESPACE.id("generator"), Material.METAL);
        electricFurnace = new ElectricFurnaceBlock(StapiTech.NAMESPACE.id("electric_furnace"), Material.METAL);
        grinder = new GrinderBlock(StapiTech.NAMESPACE.id("grinder"), Material.METAL);

        batteryBlock = new BatteryBlock(StapiTech.NAMESPACE.id("battery_block"), Material.METAL);

        energyCable = new EnergyCableBlock(StapiTech.NAMESPACE.id("energy_cable"), Material.METAL);
    }

    @EventListener
    private void registerBlockEntities(BlockEntityRegisterEvent event) {
        StapiTech.LOGGER.info("Registering block entities...");
        event.register(GeneratorBlockEntity.class, StapiTech.NAMESPACE.id("generator").toString());
        event.register(ElectricFurnaceBlockEntity.class, StapiTech.NAMESPACE.id("electric_furnace").toString());
        event.register(GrinderBlockEntity.class, StapiTech.NAMESPACE.id("grinder").toString());
        event.register(BatteryBlockEntity.class, StapiTech.NAMESPACE.id("battery_block").toString());
    }
}
