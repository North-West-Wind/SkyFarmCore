const fs = require("fs");
const TYPES = [
    "seeds",
    "anemonemal",
    "spores",
    "magnemone"
];
const original = [
    {tag:"forge:seeds"},
    {item:"minecraft:lily_pad"},
    {tag:"skyfarm:mushrooms"},
    {item:"minecraft:magma_cream"}
]

function addAnimal(entity, ingredient, type = 0, extra = undefined) {
    const json = {
        type: "minecraft:crafting_shapeless",
        ingredients: [original[type]],
        result: {
            item: "animalcrops:" + TYPES[type],
            nbt: `{entity:"${entity}"}`,
            count: 1
        }
    };
    json.ingredients.push(ingredient);
    json.ingredients.push(extra ? extra : ingredient);
    fs.writeFileSync(`${entity.split(":")[1]}.json`, JSON.stringify(json, null, 2));
}

addAnimal("minecraft:bee", {item: "minecraft:honeycomb"});
addAnimal("minecraft:cat", {tag: "minecraft:fishes"});
addAnimal("minecraft:chicken", {item: "minecraft:feather"});
addAnimal("minecraft:cow", {item: "minecraft:leather"});
addAnimal("minecraft:donkey", {item: "minecraft:leather"}, 0, {tag: "forge:chests/wooden"});
addAnimal("minecraft:fox", {item: "minecraft:sweet_berries"});
addAnimal("minecraft:horse", {item: "minecraft:leather"}, 0, {item: "minecraft:saddle"});
addAnimal("minecraft:llama", {item: "minecraft:leather"}, 0, {tag: "minecraft:wool"});
addAnimal("minecraft:mooshroom", {item: "minecraft:leather"}, 0, {tag: "skyfarm:mushrooms"});
addAnimal("minecraft:ocelot", {tag: "minecraft:fishes"}, 0, {tag:"forge:crops/cocoabeans"});
addAnimal("minecraft:panda", {item: "minecraft:bamboo"});
addAnimal("minecraft:parrot", {tag: "forge:crops/cocoabeans"});
addAnimal("minecraft:pig", {tag: "forge:crops/carrots"}, 0, {tag: "forge:crops/potato"});
addAnimal("minecraft:polar_bear", {item: "minecraft:salmon"}, 0, {tag: "forge:ices/snowball"});
addAnimal("minecraft:rabbit", {tag: "forge:crops/carrot"});
addAnimal("minecraft:sheep", {tag: "minecraft:wool"});
addAnimal("minecraft:villager", {tag: "forge:gems/emerald"});
addAnimal("minecraft:wolf", {tag: "forge:bones"}, 0, {item: "minecraft:leather"});
addAnimal("minecraft:bat", {tag: "forge:dyes/black"}, 0, {item: "minecraft:rabbit_hide"});
addAnimal("minecraft:cave_spider", {item: "minecraft:spider_eye"}, 0, {item: "minecraft:poisonous_potato"});
addAnimal("minecraft:creeper", {tag: "forge:gunpowder"});
addAnimal("minecraft:evoker", {item: "minecraft:totem_of_undying"}, 0, {tag: "forge:gems/emerald"});
addAnimal("minecraft:husk", {item: "minecraft:rotten_flesh"}, 0, {tag: "forge:sand"});
addAnimal("minecraft:illusioner", {item: "minecraft:bow"}, 0, {tag: "forge:gems/emerald"});
addAnimal("minecraft:phantom", {item: "minecraft:phantom_membrane"});
addAnimal("minecraft:pillager", {item: "minecraft:crossbow"}, 0, {tag: "forge:gems/emerald"});
addAnimal("minecraft:ravager", {item: "minecraft:saddle"}, 0, {tag: "forge:gems/emerald"});
addAnimal("minecraft:skeleton", {tag: "forge:bones"});
addAnimal("minecraft:slime", {tag: "forge:slimeball/green"});
addAnimal("minecraft:spider", {item: "minecraft:spider_eye"});
addAnimal("minecraft:stray", {tag: "forge:bones"}, 0, {tag: "forge:ices/snowball"});
addAnimal("minecraft:pillager", {item: "minecraft:iron_axe"}, 0, {tag: "forge:gems/emerald"});
addAnimal("minecraft:wandering_trader", {item: "minecraft:lead"}, 0, {tag: "forge:gems/emerald"});
addAnimal("minecraft:witch", {tag: "forge:dusts/redstone"}, 0, {tag: "forge:dusts/glowstone"});
addAnimal("minecraft:zombie", {item: "minecraft:rotten_flesh"});
addAnimal("tconstruct:sky_slime", {tag: "forge:slimeball/sky"});

addAnimal("minecraft:cod", {item:"minecraft:cod"}, 1);
addAnimal("minecraft:dolphin", {tag:"minecraft:fishes"}, 1, {item:"minecraft:map"});
addAnimal("minecraft:pufferfish", {item:"minecraft:pufferfish"}, 1);
addAnimal("minecraft:salmon", {item:"minecraft:salmon"}, 1);
addAnimal("minecraft:squid", {item:"minecraft:ink_sac"}, 1);
addAnimal("minecraft:tropical_fish", {item: "minecraft:tropical_fish"}, 1);
addAnimal("minecraft:turtle", {item:"minecraft:scute"}, 1);
addAnimal("minecraft:drowned", {item:"minecraft:rotten_flesh"}, 1);
addAnimal("minecraft:elder_guardian", {item:"minecraft:prismarine_crystals"}, 1);
addAnimal("minecraft:guardian", {item:"minecraft:prismarine_shard"}, 1);

addAnimal("minecraft:hoglin", {item:"minecraft:rotten_flesh"}, 2, {tag: "forge:rawpork"});
addAnimal("minecraft:piglin", {item:"minecraft:rotten_flesh"}, 2, {tag: "forge:ingots/gold"});
addAnimal("minecraft:ghast", {tag:"forge:gunpowder"}, 2, {item: "minecraft:fire_charge"});
addAnimal("minecraft:blaze", {tag:"forge:rods/blaze"}, 2);
addAnimal("minecraft:wither_skeleton", {tag:"forge:obsidian"}, 2, {tag:"forge:bones"});

addAnimal("minecraft:strider", {tag:"forge:string"}, 3);
addAnimal("minecraft:magma_cube", {item:"minecraft:magma_cream"}, 3);