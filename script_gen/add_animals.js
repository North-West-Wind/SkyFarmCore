// Put in data/skyfarm/advancements/farming

const fs = require("fs");
const json = require("./animals.json");
const TYPES = [
    "seeds",
    "anemonemal",
    "spores",
    "magnemone"
];
json.requirements = [];

function addAnimal(mod, name, type = 0) {
    json.criteria[name] = {
        trigger: "minecraft:inventory_changed",
        conditions: {
            items: [{
                item: "animalcrops:" + TYPES[type],
                nbt: `{entity:"${mod}:${name}"}`
            }]
        }
    }
    json.requirements.push([name]);
}

const animalCrops = [
    "minecraft:bee",
    "minecraft:cat",
    "minecraft:chicken",
    "minecraft:cow",
    "minecraft:donkey",
    "minecraft:fox",
    "minecraft:horse",
    "minecraft:llama",
    "minecraft:mooshroom",
    "minecraft:ocelot",
    "minecraft:panda",
    "minecraft:parrot",
    "minecraft:pig",
    "minecraft:polar_bear",
    "minecraft:rabbit",
    "minecraft:sheep",
    "minecraft:villager",
    "minecraft:wolf",
    "minecraft:bat",
    "minecraft:cave_spider",
    "minecraft:creeper",
    "minecraft:evoker",
    "minecraft:husk",
    "minecraft:illusioner",
    "minecraft:phantom",
    "minecraft:pillager",
    "minecraft:ravager",
    "minecraft:skeleton",
    "minecraft:slime",
    "minecraft:spider",
    "minecraft:stray",
    "minecraft:vindicator",
    "minecraft:wandering_trader",
    "minecraft:witch",
    "minecraft:zombie",
    "tconstruct:sky_slime"
];
const anemonemals = [
    "minecraft:cod",
    "minecraft:dolphin",
    "minecraft:pufferfish",
    "minecraft:salmon",
    "minecraft:squid",
    "minecraft:tropical_fish",
    "minecraft:turtle",
    "minecraft:drowned",
    "minecraft:elder_guardian",
    "minecraft:guardian"
];
const shrooms = [
    "minecraft:hoglin",
    "minecraft:piglin",
    "minecraft:ghast",
    "minecraft:blaze",
    "minecraft:wither_skeleton"
];
const magnemones = [
    "minecraft:strider",
    "minecraft:magma_cube"
];

for (const crop of animalCrops) {
    const [mod, name] = crop.split(":");
    addAnimal(mod, name, 0);
}
for (const crop of anemonemals) {
    const [mod, name] = crop.split(":");
    addAnimal(mod, name, 1);
}
for (const crop of shrooms) {
    const [mod, name] = crop.split(":");
    addAnimal(mod, name, 2);
}
for (const crop of magnemones) {
    const [mod, name] = crop.split(":");
    addAnimal(mod, name, 3);
}

fs.writeFileSync("animals.json", JSON.stringify(json, null, 2));