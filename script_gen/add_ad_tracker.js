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
const paths = [];
for (const file of files) {
    const splitted = file.split("/");
    const path = splitted[splitted.length - 2] + "/" + splitted[splitted.length - 1].replace(".json", "");
    paths.push("\"mysticalagriculture:" + path + "\"");
}
fs.writeFileSync("./array.json", paths.join(", "));