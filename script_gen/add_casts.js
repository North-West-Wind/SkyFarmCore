// Put in data/skyfarm/advancements/other

const fs = require("fs");
const json = require("./casts.json");

function addCast(cast) {
    json.criteria[cast] = {
        trigger: "minecraft:inventory_changed",
        conditions: {
            items: [
                {
                    item: `tconstruct:${cast}_cast`
                }
            ]
        }
    };
    json.requirements.push([cast]);
}

addCast("ingot");
addCast("nugget");
addCast("gem");
addCast("rod");
addCast("repair_kit");
addCast("plate");
addCast("gear");
addCast("coin");
addCast("pickaxe_head");
addCast("small_axe_head");
addCast("kama_head");
addCast("sword_blade");
addCast("hammer_head");
addCast("broad_blade");
addCast("broad_axe_head");
addCast("tool_binding");
addCast("large_plate");
addCast("tool_handle");
addCast("tough_handle");

fs.writeFileSync("./casts.json", JSON.stringify(json, null, 2));