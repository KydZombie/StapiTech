package io.github.kydzombie.stapitech.api.recipe;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GrinderRecipeRegistry {
    private static final List<GrinderRecipe> RECIPES = new ArrayList<>();

    public static void addRecipe(GrinderRecipe recipe) {
        RECIPES.add(recipe);
    }


    public static @Nullable ItemStack getOutput(@Nullable ItemStack input) {
        if (input == null) return null;
        return RECIPES.stream().filter(recipe -> recipe.input().isItemEqual(input)).findFirst().map(GrinderRecipe::output).orElse(null);
    }

    public static List<GrinderRecipe> getRecipes() {
        return RECIPES;
    }
}
