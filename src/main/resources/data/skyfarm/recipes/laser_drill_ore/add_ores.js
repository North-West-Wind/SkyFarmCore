// Wrote this script and realize industrial foregoing already implemented them.

const fs = require("fs");

function addOre(mineral, lens, min, max, weight, whitelist = {}, blacklist = {}) {
    const json = {
        "type": "forge:conditional",
        "recipes": [
            {
                "conditions": [
                    {
                        "value": {
                            "tag": `forge:ores/${mineral}`,
                            "type": "forge:tag_empty"
                        },
                        "type": "forge:not"
                    },
                    {
                        "type": "forge:mod_loaded",
                        "modid": "industrialforegoing"
                    }
                ],
                "recipe": {
                    "output": {
                        "tag": `forge:ores/${mineral}`
                    },
                    "rarity": [
                        {
                            whitelist,
                            blacklist,
                            "depth_min": min,
                            "depth_max": max,
                            weight
                        }
                    ],
                    "pointer": 0,
                    "catalyst": {
                        "item": `industrialforegoing:laser_lens${lens}`
                    },
                    "type": "industrialforegoing:laser_drill_ore"
                }
            }
        ]
    };
    fs.writeFileSync(`./${mineral}.json`, JSON.stringify(json, null, 2));
}