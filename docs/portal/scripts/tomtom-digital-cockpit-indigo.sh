#!/bin/bash

echo "Temporary script to patch 'TomTom IndiGO' in platform KDoc 'TomTom Digital Cockpit'."
echo "Also patches content of .md .xml gradle.properties files."
echo "See Jira tickets IVI-8580 and IVI-8700."

die() {
    echo "Error: $*"
    exit 1
}

test -d .git -a -d platform/systemui/plugin/frontendextension/debugtab || \
    die "Must run from root directory of an indigo checkout."
git clean -fdx
set -e

echo ""
echo "Patch build files to Digital Cockpit."
filenames="$(find . -name 'build.gradle.kts' | sort)"
filenames+=" $(find . -name 'gradle.properties' | sort)"
filenames+=" $(find . -name 'libraries.versions.toml')"
                             
for file in $filenames
do
    sed -i 's#TomTom IndiGO#TomTom Digital Cockpit#g' "$file"
done
git add . && git commit --no-verify -m "Patch build files 'TomTom IndiGO' -> 'TomTom Digital Cockpit'."

echo ""
echo "Patch KDoc to Digital Cockpit."
for file in $(find . -name '*.kt' -o -name '*.kts' | sort)
do
    sed -i 's#TomTom IndiGO#TomTom Digital Cockpit#g' "$file"
done
git add . && git commit --no-verify -m "Patch KDoc 'TomTom IndiGO' -> 'TomTom Digital Cockpit'."

echo ""
echo "Patch markdown files to Digital Cockpit."
for file in $(find . -name '*.md' | sort)
do
    sed -i 's#TomTom IndiGO#TomTom Digital Cockpit#g' "$file"
done
git add . && git commit --no-verify -m "Patch markdown files 'TomTom IndiGO' -> 'TomTom Digital Cockpit'."

echo ""
echo "Check for line length etc. after text replacement."
./gradlew ktlintcheck

echo ""
echo "OK"
