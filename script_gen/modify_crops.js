// BotanyPots faster growth
// Copy BotanyPots data folder to this directory

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
const lines = [];
for (const file of files) {
    const json = require(file);
    if (json.type !== "botanypots:crop") continue;
    var mod = "botanypots";
    const foundMod = json.conditions?.find(x => x.type === "forge:mod_loaded");
    if (foundMod) mod = foundMod.modid;
    var filename = file.replace(/^.*[\\\/]/, '');
    var name = filename.replace(".json", "");
    lines.push(`val ${mod}_${name} = crops.getCrop("${mod}:crops/${name}");`);
    lines.push(`${mod}_${name}.clearDrops();`);
    lines.push(`${mod}_${name}.addCategory("dirt");`);
    lines.push(`${mod}_${name}.addDrop(<item:mysticalagriculture:${name}_essence>, 0.5, 4);`);
    lines.push(`${mod}_${name}.addDrop(<item:mysticalagriculture:${name}_seeds>, 0.2);`);
    lines.push(`${mod}_${name}.addDrop(<item:mysticalagriculture:fertilized_essence>, 0.01, 1);`);
    console.log(`${filename} can now be grown with dirt`);
}
fs.writeFileSync("./botany_crops.zs", lines.join("\n"));