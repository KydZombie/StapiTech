package io.github.kydzombie.stapitech.event.init.client;

import io.github.kydzombie.stapitech.StapiTech;
import io.github.kydzombie.stapitech.block.entity.BatteryBlockEntity;
import io.github.kydzombie.stapitech.block.entity.ElectricFurnaceBlockEntity;
import io.github.kydzombie.stapitech.block.entity.GeneratorBlockEntity;
import io.github.kydzombie.stapitech.block.entity.GrinderBlockEntity;
import io.github.kydzombie.stapitech.gui.screen.ingame.BatteryBlockScreen;
import io.github.kydzombie.stapitech.gui.screen.ingame.ElectricFurnaceScreen;
import io.github.kydzombie.stapitech.gui.screen.ingame.GeneratorScreen;
import io.github.kydzombie.stapitech.gui.screen.ingame.GrinderScreen;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.modificationstation.stationapi.api.event.registry.GuiHandlerRegistryEvent;
import uk.co.benjiweber.expressions.tuple.BiTuple;

public class StapiTechGui {
    @EventListener
    private void registerGuiHandlers(GuiHandlerRegistryEvent event) {
        event.registry.registerValueNoMessage(StapiTech.NAMESPACE.id("generator"), BiTuple.of(this::openGenerator, GeneratorBlockEntity::new));
        event.registry.registerValueNoMessage(StapiTech.NAMESPACE.id("electric_furnace"), BiTuple.of(this::openElectricFurnace, ElectricFurnaceBlockEntity::new));
        event.registry.registerValueNoMessage(StapiTech.NAMESPACE.id("grinder"), BiTuple.of(this::openGrinder, GrinderBlockEntity::new));
        event.registry.registerValueNoMessage(StapiTech.NAMESPACE.id("battery_block"), BiTuple.of(this::openBatteryBlock, BatteryBlockEntity::new));
    }

    private Screen openGenerator(PlayerEntity player, Inventory inventory) {
        return new GeneratorScreen(player, (GeneratorBlockEntity) inventory);
    }

    private Screen openElectricFurnace(PlayerEntity player, Inventory inventory) {
        return new ElectricFurnaceScreen(player, (ElectricFurnaceBlockEntity) inventory);
    }

    private Screen openGrinder(PlayerEntity player, Inventory inventory) {
        return new GrinderScreen(player, (GrinderBlockEntity) inventory);
    }

    private Screen openBatteryBlock(PlayerEntity player, Inventory inventory) {
        return new BatteryBlockScreen(player, (BatteryBlockEntity) inventory);
    }
}
