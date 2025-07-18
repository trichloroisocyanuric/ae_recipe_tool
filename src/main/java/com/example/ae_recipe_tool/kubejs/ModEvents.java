package com.example.ae_recipe_tool.kubejs;

import dev.latvian.mods.kubejs.bindings.event.ClientEvents;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

public interface ModEvents {
    EventGroup GROUP = ClientEvents.GROUP;

    EventHandler AE_RECIPE_TRANSFER = GROUP.client("aeRecipeTransfer", () -> RecipeTransferClientEvent.class);
}
