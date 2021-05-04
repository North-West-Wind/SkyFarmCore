const fs = require("fs");
const path = require("path");
function deepReaddir(dir) {
    var results = [];
    const list = fs.readdirSync(dir);
    var i = 0;
    function next() {
        var file = list[i++];
        if (!file) return results;
        file = path.resolve(dir, file);
        const stat = fs.statSync(file);
        if (stat && stat.isDirectory()) {
            const res = deepReaddir(file);
            results = results.concat(res);
            return next();
        } else {
            results.push(file);
            return next();
        }
    };
    return next();
}
const files = deepReaddir(".").filter(file => file.endsWith(".json"));

for (const file of files) {
    const json = require(file);
    if (!json.flower) continue;
    var flower;
    if (json.flower.startsWith("tag:")) flower = { tag: json.flower.replace("tag:", "") };
    else if (json.flower.toLowerCase() === "all") flower = { tag: "minecraft:flowers" };
    else if (json.flower.toLowerCase() === "tall") flower = { tag: "minecraft:tall_flowers" };
    else if (json.flower.toLowerCase() === "small") flower = { tag: "minecraft:small_flowers" };
    else flower = { item: json.flower };
    var filename = file.replace(/^.*[\\\/]/, '');
    const name = filename.replace(".json", "").toLowerCase();
    const recipe = {
        type: "minecraft:crafting_shapeless",
        ingredients: [
            flower,
            {
                tag: "minecraft:flowers"
            }
        ],
        result: {
            item: "skyfarm:mutation_pollen",
            nbt: `{Type:${name}}`,
            count: 1
        }
    };
    fs.writeFileSync(`${name}.json`, JSON.stringify(recipe, null, 2));
}