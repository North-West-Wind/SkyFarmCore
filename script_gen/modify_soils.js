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
    if (json.type !== "botanypots:soil") continue;
    var mod = "botanypots";
    const foundMod = json.conditions?.find(x => x.type === "forge:mod_loaded");
    if (foundMod) mod = foundMod.modid;
    if (json.growthModifier !== undefined) {
        const old = json.growthModifier;
        json.growthModifier += 0.5;
        if (json.growthModifier == 0) json.growthModifier += 0.05;
        if (json.growthModifier > 0) json.growthModifier *= 2.5;
        else json.growthModifier *= 0.3;
        json.growthModifier = Math.round((json.growthModifier + Number.EPSILON) * 100) / 100;
        var filename = file.replace(/^.*[\\\/]/, '');
        lines.push(`soils.getSoil("${mod}:soils/${filename.replace(".json", "")}").setGrowthModifier(${json.growthModifier});`);
        console.log(`${filename} growthModifier increased by ${json.growthModifier - old}`);
    }
}
fs.writeFileSync("./botany_soils.zs", lines.join("\n"));