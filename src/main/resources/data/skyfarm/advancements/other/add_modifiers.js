const fs = require("fs");
const json = require("./modifiers.json");

function addModifier(modifier) {
    json.criteria[modifier] = {
        trigger: "minecraft:inventory_changed",
        conditions: {
            items: [
                {
                    tag: "tconstruct:modifiable",
                    nbt: `{tic_modifiers:[{name:"tconstruct:${modifier}"}]}`
                }
            ]
        }
    };
    json.requirements.push([modifier]);
}

addModifier("antiaquatic");
addModifier("autosmelt");
addModifier("bane_of_arthropods");
addModifier("beheading");
addModifier("blasting");
addModifier("cooling");
addModifier("diamond");
addModifier("draconic");
addModifier("emerald");
addModifier("expanded");
addModifier("experienced");
addModifier("fiery");
addModifier("gilded");
addModifier("harmonious");
addModifier("haste");
addModifier("hydraulic");
addModifier("knockback");
addModifier("lightspeed");
addModifier("luck");
addModifier("magnetic");
addModifier("necrotic");
addModifier("netherite");
addModifier("overslime");
addModifier("reach");
addModifier("recapitated");
addModifier("reinforced");
addModifier("resurrected");
addModifier("sharpness");
addModifier("silky");
addModifier("smite");
addModifier("soulbound");
addModifier("worldbound");
addModifier("writable");

fs.writeFileSync("./modifiers.json", JSON.stringify(json, null, 2));