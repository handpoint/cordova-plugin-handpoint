#!/bin/bash
npm install
node release.js -r $1 -c $2
rm -rf node_modules
