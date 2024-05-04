package io.github.kydzombie.stapitech.event.init;

import io.github.kydzombie.stapitech.StapiTech;
import io.github.kydzombie.stapitech.api.recipe.GrinderRecipe;
import io.github.kydzombie.stapitech.api.recipe.GrinderRecipeRegistry;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.StationAPI;
import net.modificationstation.stationapi.api.event.recipe.RecipeRegisterEvent;
import net.modificationstation.stationapi.api.event.registry.AfterBlockAndItemRegisterEvent;

public class StapiTechRecipes {
    @EventListener
    private void callCustomRecipeRegistries(AfterBlockAndItemRegisterEvent event) {
        StationAPI.EVENT_BUS.post(RecipeRegisterEvent.builder().recipeId(StapiTech.NAMESPACE.id("grinder")).build());
    }

    @EventListener
    private void registerRecipes(RecipeRegisterEvent event) {
        if (event.recipeId == StapiTech.NAMESPACE.id("grinder")) {
            GrinderRecipeRegistry.addRecipe(new GrinderRecipe(new ItemStack(Block.COBBLESTONE), new ItemStack(Block.GRAVEL)));
        }
    }
}
