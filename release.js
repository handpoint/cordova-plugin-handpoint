#!/usr/bin/env node

var fs = require('fs'),
    xml2js = require('xml2js'),
    commandLineArgs = require('command-line-args'),
    Git = require('nodegit'),
    semverSort = require('semver-sort'),
    semver = require('semver'),
    exec = require('child_process').exec;

const RELEASE_VALUES = ['major', 'premajor', 'minor', 'preminor', 'patch', 'prepatch', 'prerelease'];

/**
 * Command options definition
 */
var optionDefinitions = [{
    name: 'current',
    alias: 'c',
    type: String,
    defaultOption: ''
}, {
    name: 'release',
    alias: 'r',
    type: String,
    defaultOption: 'minor'
}];

/**
 * Check if current working tree contains pending changes
 * @param {*} repository 
 */
var checkPendingChanges = function (repository) {
    return new Promise(function (resolve, reject) {
        repository.getStatus({}).then(function (arrayStatusFile) {
            if (!arrayStatusFile || arrayStatusFile.length === 0) {
                resolve(false);
            } else {
                resolve(true);
            }
        });
    });
};

/**
 * Write version in package.json file 
 * @param {*} version 
 */
var setPackageJsonVersion = function (version) {
    return new Promise(function (resolve, reject) {
        // Read file
        var file = require('./package.json');
        file.version = version;
        fs.writeFile('./package.json', JSON.stringify(file, true, 2), function (err) {
            if (err) {
                console.error('Error writing version to package.json file: ' + err);
                reject();
            } else {
                resolve();
            }
        });
    });
};

var setPluginVersion = function (version) {
    return new Promise(function (resolve, reject) {
        let parser = new xml2js.Parser();
        fs.readFile('./plugin.xml', function (err, data) {
            parser.parseString(data, function (err, result) {
                if (err) {
                    console.log(err);
                    reject();
                } else {
                    result.plugin.$.version = version;
                    // create a new builder object and then convert our json back to xml
                    let builder = new xml2js.Builder({
                        'pretty': true,
                        'indent': '    '
                    });
                    let xml = builder.buildObject(result);
                    fs.writeFile('./plugin.xml', xml, function (err, data) {
                        if (err) {
                            console.log(err);
                            reject();
                        } else {
                            console.log("plugin.xml successfully updated");
                            resolve();
                        }
                    });
                }
            });
        });
    });
};

var createVersion = function (version, repository) {
    // Increment version in package.json
    setPackageJsonVersion(version).then(function () {
        // Increment version in plugin.xml
        setPluginVersion(version).then(function () {

        })
    });
};

/**
 * Increment last git tag with release type specified in command line
 * @param {*} repository 
 * @param {*} tags 
 */
var incrementVersion = function (repository) {
    var version = '0.0.0';
    var nextVersion;

    // Get command line options and execute the proper action
    var options = commandLineArgs(optionDefinitions);

    if (options.current) {
        version = options.current;
    }

    if (RELEASE_VALUES.indexOf(options.release) === -1) {
        console.error('Error: wrong value for release parameter. Valid values are: ' + JSON.stringify(RELEASE_VALUES));
    } else {
        if (options.release.startsWith('pre')) {
            nextVersion = semver.inc(version, options.release, 'RC');
        } else {
            nextVersion = semver.inc(version, options.release);
        }

        createVersion(nextVersion, repository);
    }
};

// ------------------------ MAIN ---------------------
// Get current repo
Git.Repository.open("./").then(function (repository) {
    // Check there are no pending changes to commit
    checkPendingChanges(repository).then(function (pendingChanges) {
        if (pendingChanges) {
            console.error("Error: the working tree contains pending changes. Commit or stash your changes and try again")
        } else {
            // Increment version
            incrementVersion(repository);
        }
    });
});
