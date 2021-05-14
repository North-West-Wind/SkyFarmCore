const fs = require("fs");

const files = fs.readdirSync(".");
for (const file of files) {
    if (!file.endsWith(".json")) continue;
    const json = require(__dirname + "/" + file);
    json.conditions = [
        {
            type: "forge:mod_loaded",
            modid: "resourcefulbees"
        }
    ];
    fs.writeFileSync(`${file}`, JSON.stringify(json, null, 2));
}