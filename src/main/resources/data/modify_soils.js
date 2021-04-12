// BotanyPots faster growth

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
var i = 0;
for (const file of files) {
    const json = require(file);
    /*if (json.growthModifier === undefined) continue;
    json.growthModifier -= 0.2;
    if (json.growthModifier > 0) json.growthModifier *= 0.6;
    else json.growthModifier *= 1.5;
    json.growthModifier = Math.round((json.growthModifier + Number.EPSILON) * 100) / 100;
    fs.writeFileSync(file, JSON.stringify(json));
    console.log(`${file.split("\\")[file.split("\\").length - 1]}'s new growth modifier: ${json.growthModifier}`);*/
    if (json.type !== "botanypots:soil") continue;
    json.condition = [
        {
            "type": "forge:mod_loaded",
            "modid": "botanypots"
        }
    ];
    if (json.growthModifier !== undefined) {
        json.growthModifier += 0.2;
        if (json.growthModifier > 0) json.growthModifier *= 1.5;
        else json.growthModifier *= 0.6;
        json.growthModifier = Math.round((json.growthModifier + Number.EPSILON) * 100) / 100;
        fs.writeFileSync(file, JSON.stringify(json));
        console.log(`${file.split("\\")[file.split("\\").length - 1]}'s new growth modifier: ${json.growthModifier}`);
    }
    fs.writeFileSync(file, JSON.stringify(json, null, 4));
    i++;
}
console.log("Modified " + i + " files");