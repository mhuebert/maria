#!/usr/bin/env bash
mkdir resources/public/css;
node_modules/stylus/bin/stylus src/css/maria.styl -o resources/public/css/maria.css $1