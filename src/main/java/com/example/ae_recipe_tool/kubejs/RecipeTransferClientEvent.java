package com.example.ae_recipe_tool.kubejs;

import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import dev.latvian.mods.kubejs.client.ClientKubeEvent;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public abstract class RecipeTransferClientEvent implements ClientKubeEvent {
    public final RecipeHolder<?> holder;
    public final List<List<GenericStack>> rawInputs;
    public final List<GenericStack> rawOutputs;
    public final List<GenericStack> inputs;
    public final List<GenericStack> outputs;

    protected boolean inited = false;

    protected RecipeTransferClientEvent(RecipeHolder<?> holder, List<List<GenericStack>> inputs, List<GenericStack> outputs) {
        this.holder = holder;
        this.rawInputs = inputs;
        this.rawOutputs = outputs;
        this.inputs = new ArrayList<>(inputs.size());
        this.outputs = new ArrayList<>(outputs.size());
    }

    public void initSortedSlots() {
        initSortedSlots(true);
    }

    abstract public void initSortedSlots(boolean merge);

    abstract public GenericStack ingredientToGenericStack(Ingredient ingredient);

    @HideFromJS
    abstract protected void setSlots();

    public GenericStack itemToGenericStack(ItemStack stack) {
        return GenericStack.fromItemStack(stack);
    }

    public GenericStack fluidToGenericStack(FluidStack stack) {
        return GenericStack.fromFluidStack(stack);
    }

    public boolean isItem(GenericStack stack) {
        return stack.what().getType() == AEKeyType.items();
    }

    public ResourceLocation getRecipeId() {
        return holder.id();
    }

    public String getRecipeTypeName() {
        String name = holder.value().getType().toString();
        return name.contains(":") ? name : holder.id().getNamespace() + ':' + name;
    }

    @Override
    public void afterPosted(EventResult result) {
        if (!inited) initSortedSlots(true);
        setSlots();
    }
}
