const fs = require("fs");
const advJson = {
    parent: "skyfarm:farming/insanium_farmland",
    conditions: [
        {
            type: "forge:mod_loaded",
            modid: "mysticalagriculture"
        }
    ],
    display: {
        title: {
            translate: "advancement.farming.ma_seeds.title"
        },
        description: {
            translate: "advancement.farming.ma_seeds.description"
        },
        icon: {
            item: "mysticalagriculture:air_essence"
        },
        frame: "challenge",
        show_toast: true,
        announce_to_chat: true,
        hidden: false
    },
    criteria: {
    },
    requirements: [
    ],
    rewards: {
        function: "skyfarm:add_point"
    }
}

// MA seeds
for (const file of fs.readdirSync("infusion")) {
    if (!file.endsWith(".json")) continue;
    const json = require("./infusion/" + file);
    if (json.type !== "mysticalagriculture:infusion") continue;
    const result = json.result.item;
    advJson.criteria[file.replace(".json", "")] = {
        trigger: "minecraft:inventory_changed",
        conditions: {
            items: [
                {
                    item: result
                }
            ]
        }
    };
    advJson.requirements.push([file.replace(".json", "")]);
}

// IAP seeds
// Could've extracted from the jar, but let's not do that
const seeds = [
    // Botania
    "element",
    "gaia_spirit",
    "livingrock",
    "livingwood",
    "manasteel",
    "terrasteel",
    // Industrial Foregoing
    "pink_slime",
    "plastic",
    // Mekanism
    "bronze",
    "copper",
    "osmium",
    "refined_glowstone",
    "refined_obsidian",
    "salt",
    "steel",
    "sulfur",
    "tin",
    // Powah
    "uraninite"
];

for (const seed of seeds) {
    advJson.criteria[seed] = {
        trigger: "minecraft:inventory_changed",
        conditions: {
            items: [
                {
                    item: `mysticalagriculture:${seed}_seeds`
                }
            ]
        }
    };
    advJson.requirements.push([seed]);
}

fs.writeFileSync("./ma_seeds.json", JSON.stringify(advJson, null, 2));