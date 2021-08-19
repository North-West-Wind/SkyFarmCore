var missing_seeds = [
    "apatite",
    "zombified_piglin",
    "ravager",
    "piglin",
    "gaia_spirit",
    "piglin_brute",
    "tinkers_bronze",
    "slimesteel",
    "pig_iron",
    "steeleaf",
    "ironwood",
    "cobalt",
    "rose_gold",
    "fluorite",
    "knightmetal",
    "fiery_ingot",
    "manyullyn",
    "queens_slime",
    "hepatizon",
    "marble",
    "limestone",
    "basalt",
    "livingwood",
    "livingrock",
    "ruby",
    "sapphire",
    "elementium"
];
for (const seed of missing_seeds) {
    console.log(`val ${seed} = crops.create("mysticalagriculture:crops/${seed}", <item:mysticalagriculture:${seed}_seeds>, <blockstate:mysticalagriculture:${seed}_crop>, 1200, "dirt");`);
    console.log(`${seed}.addDrop(<item:mysticalagriculture:${seed}_essence>, 0.5, 4);`);
    console.log(`${seed}.addDrop(<item:mysticalagriculture:${seed}_seeds>, 0.2);`);
    console.log(`${seed}.addDrop(<item:mysticalagriculture:fertilized_essence>, 0.01, 1);`);
}