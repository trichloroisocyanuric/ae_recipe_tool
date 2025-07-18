package com.example.ae_recipe_tool.mixin;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.core.network.ServerboundPacket;
import appeng.core.network.serverbound.InventoryActionPacket;
import appeng.helpers.InventoryAction;
import appeng.integration.modules.itemlists.EncodingHelper;
import appeng.menu.me.items.PatternEncodingTermMenu;
import appeng.menu.slot.FakeSlot;
import com.example.ae_recipe_tool.SharedRecipeHolder;
import com.example.ae_recipe_tool.kubejs.ModEvents;
import com.example.ae_recipe_tool.kubejs.RecipeTransferClientEvent;
import com.google.common.math.LongMath;
import com.llamalad7.mixinextras.sugar.Local;
import dev.latvian.mods.kubejs.event.EventResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mixin(EncodingHelper.class)
abstract public class EncodingHelperMixin {
    @Shadow
    private static void addOrMerge(List<GenericStack> stacks, GenericStack newStack) {
    }

    @Shadow
    private static GenericStack findBestIngredient(Map<AEKey, Integer> ingredientPriorities,
                                                   List<GenericStack> possibleIngredients) {
        return null;
    }

    @Inject(
            method = "encodeProcessingRecipe(Lappeng/menu/me/items/PatternEncodingTermMenu;Ljava/util/List;Ljava/util/List;)V",
            at = @At(value = "INVOKE",
                    target = "Lappeng/integration/modules/itemlists/EncodingHelper;encodeBestMatchingStacksIntoSlots(Ljava/util/List;Ljava/util/Map;[Lappeng/menu/slot/FakeSlot;)V",
                    ordinal = 0,
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private static void onEncodeProcessingRecipe(PatternEncodingTermMenu menu, List<List<GenericStack>> genericIngredients,
                                                 List<GenericStack> genericResults, CallbackInfo ci,
                                                 @Local Map<AEKey, Integer> ingredientPriorities
    ) {
        RecipeHolder<?> holder = SharedRecipeHolder.ref.get();
        if (holder != null) {
            SharedRecipeHolder.ref.set(null);
            if (ModEvents.AE_RECIPE_TRANSFER.hasListeners()) {
                EventResult result = ModEvents.AE_RECIPE_TRANSFER.post(
                        new RecipeTransferClientEvent(holder, genericIngredients, genericResults) {
                            @Override
                            public void initSortedSlots(boolean merge) {
                                if (inited) return;
                                inited = true;

                                for (var genericIngredient : rawInputs) {
                                    if (!genericIngredient.isEmpty()) {
                                        GenericStack bestIngredient = findBestIngredient(ingredientPriorities, genericIngredient);
                                        if (merge) {
                                            addOrMerge(inputs, bestIngredient);
                                        } else {
                                            aerecipetool$addNotMerge(inputs, bestIngredient);
                                        }
                                    }
                                }

                                for (var output : rawOutputs) {
                                    if (merge) {
                                        addOrMerge(outputs, output);
                                    } else {
                                        aerecipetool$addNotMerge(outputs, output);
                                    }
                                }
                            }

                            @Override
                            protected void setSlots() {
                                aerecipetool$setSlots(menu.getProcessingInputSlots(), inputs);
                                aerecipetool$setSlots(menu.getProcessingOutputSlots(), outputs);
                            }

                            @Override
                            public GenericStack ingredientToGenericStack(Ingredient ingredient) {
                                return findBestIngredient(ingredientPriorities,
                                        Arrays.stream(ingredient.getItems()).map(GenericStack::fromItemStack).toList()
                                );
                            }
                        });

                if (result.pass()) {
                    ci.cancel();
                }
            }
        }
    }

    @Unique
    private static void aerecipetool$addNotMerge(List<GenericStack> stacks, GenericStack newStack) {
        if (!stacks.isEmpty()) {
            var lastStack = stacks.getLast();
            if (Objects.equals(lastStack.what(), newStack.what())) {
                long existingAmount = lastStack.amount();
                long newAmount = LongMath.saturatedAdd(existingAmount, newStack.amount());
                stacks.set(stacks.size() - 1, new GenericStack(newStack.what(), newAmount));

                long overflow = newStack.amount() - (newAmount - existingAmount);
                if (overflow > 0) {
                    stacks.add(new GenericStack(newStack.what(), overflow));
                }
                return;
            }
        }
        stacks.add(newStack);
    }

    @Unique
    private static void aerecipetool$setSlots(FakeSlot[] slots, List<GenericStack> stacks) {
        for (int i = 0; i < slots.length; i++) {
            var slot = slots[i];
            var stack = (i < stacks.size()) ? GenericStack.wrapInItemStack(stacks.get(i))
                    : ItemStack.EMPTY;
            ServerboundPacket message = new InventoryActionPacket(
                    InventoryAction.SET_FILTER, slot.index, stack);
            PacketDistributor.sendToServer(message);
        }
    }
}
