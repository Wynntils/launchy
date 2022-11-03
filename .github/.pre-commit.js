const fs = require("fs");

exports.preCommit = (props) => {
    const replace = (path, searchValue, replaceValue) => {
        let content = fs.readFileSync(path, "utf-8");
        if (content.match(searchValue)) {
            fs.writeFileSync(path, content.replace(searchValue, replaceValue));
            console.log(`"${path}" changed`);
        }
    };

    // replace only the version string example:
    // version=1.1.4-alpha.1
    replace("./gradle.properties", /version=\d+\.\d+\.\d+(-\w+\.\d+)?/, `version=${props.version}`);
    // Regex provided by Github Copilot
};
