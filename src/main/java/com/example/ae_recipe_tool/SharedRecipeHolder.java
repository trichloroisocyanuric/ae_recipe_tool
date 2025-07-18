package com.example.ae_recipe_tool;

import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.concurrent.atomic.AtomicReference;

public class SharedRecipeHolder {
    public static AtomicReference<RecipeHolder<?>> ref = new AtomicReference<>(null);
}
