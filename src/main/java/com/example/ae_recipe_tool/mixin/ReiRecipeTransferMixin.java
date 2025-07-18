package com.example.ae_recipe_tool.mixin;

import appeng.integration.modules.rei.transfer.EncodePatternTransferHandler;
import appeng.menu.me.items.PatternEncodingTermMenu;
import com.example.ae_recipe_tool.SharedRecipeHolder;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandler;
import me.shedaniel.rei.api.common.display.Display;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EncodePatternTransferHandler.class)
abstract public class ReiRecipeTransferMixin<T extends PatternEncodingTermMenu> {
    @Inject(
            method = "transferRecipe(Lappeng/menu/me/items/PatternEncodingTermMenu;Lnet/minecraft/world/item/crafting/RecipeHolder;Lme/shedaniel/rei/api/common/display/Display;Z)Lme/shedaniel/rei/api/client/registry/transfer/TransferHandler$Result;",
            at = @At("HEAD")
    )
    public void captureHolder(T menu, RecipeHolder<?> holder, Display display, boolean doTransfer, CallbackInfoReturnable<TransferHandler.Result> cir) {
        if (doTransfer && holder != null) {
            SharedRecipeHolder.ref.set(holder);
        }
    }
}
