
AE Recipe Tool
=======

When transferring recipe in pattern terminal processing mode (by pressing `+` of emi/rei), the recipe can be modified using kubejs.

`kubejs/client_scripts/example.js`: 
```js
ClientEvents.aeRecipeTransfer((event) => {
    const recipeName = event.getRecipeTypeName();
    console.log(`recipe name: ${recipeName}`);

    switch (recipeName) {
        case "minecraft:smelting":
            event.initSortedSlots();
            console.log("modify smelting...");
            let inputs = event.inputs;
            for (let index = 0; index < inputs.length; index++) {
                let item = inputs[index];
                if (event.isItem(item)) inputs[index] = event.itemToGenericStack(item.what().toStack(item.amount() * 8));

            }
            let outputs = event.outputs;
            for (let index = 0; index < outputs.length; index++) {
                let item = outputs[index];
                if (event.isItem(item)) outputs[index] = event.itemToGenericStack(item.what().toStack(item.amount() * 8));
            }
            inputs.add(event.itemToGenericStack(Item.of("minecraft:coal")));
            break;
        case "botania:petal_apothecary":
            event.initSortedSlots();
            console.log("remove water...");
            event.inputs.removeIf((i) => !event.isItem(i));
            break;
        case "botania:runic_altar":
            event.initSortedSlots();
            console.log("add runic...");
            let catalysts = event.holder.value().catalysts;
            if (!catalysts.isEmpty()) {
                let lastItem = event.inputs.removeLast();
                for (let index = 0; index < catalysts.length; index++) {
                    let item = event.ingredientToGenericStack(catalysts[index]);
                    event.inputs.add(item);
                    event.outputs.add(item);
                }
                event.inputs.add(lastItem);
            }
            break;
        case "create:mechanical_crafting":
            console.log("do not to merge items");
            event.initSortedSlots(false);
            break;
        default:
            break;
    }
});
```