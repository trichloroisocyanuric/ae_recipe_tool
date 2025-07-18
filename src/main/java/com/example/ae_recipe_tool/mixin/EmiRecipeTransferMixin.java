package com.example.ae_recipe_tool.mixin;

import appeng.integration.modules.emi.EmiEncodePatternHandler;
import appeng.menu.me.items.PatternEncodingTermMenu;
import com.example.ae_recipe_tool.SharedRecipeHolder;
import dev.emi.emi.api.recipe.EmiRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EmiEncodePatternHandler.class)
abstract public class EmiRecipeTransferMixin<T extends PatternEncodingTermMenu> {
    @Inject(
            method = "transferRecipe(Lappeng/menu/me/items/PatternEncodingTermMenu;Lnet/minecraft/world/item/crafting/RecipeHolder;Ldev/emi/emi/api/recipe/EmiRecipe;Z)Lappeng/integration/modules/emi/AbstractRecipeHandler$Result;",
            at = @At("HEAD")
    )
    public void captureHolder(T menu, RecipeHolder<?> holder, EmiRecipe emiRecipe, boolean doTransfer, CallbackInfoReturnable<?> cir) {
        if (doTransfer && holder != null) {
            SharedRecipeHolder.ref.set(holder);
        }
    }
}
