const fs = require("fs");

function addColor(color) {
    const json = {
        type: "minecraft:crafting_shaped",
        conditions: [
            {
                type: "forge:mod_loaded",
                modid: "iceandfire"
            }
        ],
        pattern: [
            " # ",
            "#E#",
            " # "
        ],
        key: {
            "#": {
                item: "iceandfire:dragonscale_" + color
            },
            "E": {
                item: "minecraft:egg"
            }
        },
        "result": {
          "item": "iceandfire:dragonegg_"+color,
          "count": 1
        }
    };
    fs.writeFileSync(color+".json", JSON.stringify(json, null, 4));
}

addColor("red");
addColor("green");
addColor("bronze");
addColor("gray");
addColor("blue");
addColor("white");
addColor("sapphire");
addColor("silver");
addColor("electric");
addColor("amythest");
addColor("copper");
addColor("black");