#!/bin/bash

echo "Temporary script to patch 'tomtom-indigo' in developer portal url's to 'tomtom-digital-cockpit'."
echo "Also patches content of .md .plantuml .svg files."
echo "See Jira ticket IVI-8700."

die() {
    echo "Error: $*"
    exit 1
}

test -d .git -a -d docs/portal/src || \
    die "Must run from root directory of an ivi-example-app checkout."
git clean -fdx
set -e

echo ""
echo "Patch main URL."
for file in $(find . -name '*.md' | sort)
do
    sed -i 's#(/tomtom-indigo/#(/tomtom-digital-cockpit/#' "$file"
    sed -i 's#://developer.tomtom.com/tomtom-indigo/#://developer.tomtom.com/tomtom-digital-cockpit/#' "$file"
    sed -i 's#href="/tomtom-indigo/#href="/tomtom-digital-cockpit/#' "$file"
    sed -i 's#href: "/tomtom-indigo/#href: "/tomtom-digital-cockpit/#' "$file"
    sed -i 's#url: "/tomtom-indigo/#url: "/tomtom-digital-cockpit/#' "$file"
done
git add . && git commit -m "Patch main URL 'tomtom-indigo' -> 'tomtom-digital-cockpit'."

echo ""
echo "Patch assets URLs."
for file in $(find . -name '*.md' | sort)
do
    sed -i 's#://developer.tomtom.com/assets/downloads/tomtom-indigo/#://developer.tomtom.com/assets/downloads/tomtom-digital-cockpit/#' "$file"
done
git add . && git commit -m "Patch assets URL 'tomtom-indigo' -> 'tomtom-digital-cockpit'."

echo ""
echo "Patch GitHub URLs."
for file in $(find . -name '*.md' | sort)
do
    sed -i 's#://github.com/tomtom-international/tomtom-indigo-sdk-examples#://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples#' "$file"
    sed -i 's#git@github.com:tomtom-international/tomtom-indigo-sdk-examples#:git@github.com:tomtom-international/tomtom-digital-cockpit-sdk-examples#' "$file"
done
git add . && git commit -m "Patch assets URL 'tomtom-indigo-sdk-examples' -> 'tomtom-digital-cockpit-sdk-examples'."

echo ""
echo "Rename directories."
for dir in $(find . -name '*tomtom-indigo*' -type d | sort)
do
    dir_old=$dir
    dir_new=$(echo "$dir_old" | sed 's#tomtom-indigo#tomtom-digital-cockpit#')
    echo "Directory rename: $dir_old -> $dir_new"
    git mv "$dir_old" "$dir_new"

    link_old=$(echo "$dir_old" | awk -F '/' '{ print $NF }')
    link_new=$(echo "$dir_new" | awk -F '/' '{ print $NF }')
    echo "Link update: $link_old -> $link_new"
    find . -name '*.md' | xargs sed -i "s#/${link_old}/#/${link_new}/#"

    echo "Yaml update: $link_old -> $link_new"
    find . -name 'navigation.yml' -o -name 'top-navigation.yml' | xargs sed -i "s#/${link_old}/#/${link_new}/#"
done
git add . && git commit -m "Rename directories 'tomtom-indigo' -> 'tomtom-digital-cockpit'."

echo ""
echo "Rename markdown files."
for file in $(find . -name '*tomtom-indigo*.md' | sort)
do
    dir=$(dirname "$file" | sed "s#^./##")
    file_old=$(basename "$file")
    file_new=$(basename "$file_old" | sed "s#tomtom-indigo#tomtom-digital-cockpit#")
    path_old="$dir/$file_old"
    path_new="$dir/$file_new"
    echo "File rename: $path_old -> $path_new"
    git mv "$path_old" "$path_new"

    link_old=$(echo "$path_old" | sed "s#\.md\$##")
    link_new=$(echo "$path_new" | sed "s#\.md\$##")
    echo "Link update: $link_old -> $link_new"
    find . -name '*.md' | xargs sed -i "s#/${link_old}#/${link_new}#"

    link_old=$(echo "$file_old" | sed "s#\.md\$##")
    link_new=$(echo "$file_new" | sed "s#\.md\$##")
    echo "Yaml update: $link_old -> $link_new"
    find . -name 'navigation.yml' -o -name 'top-navigation.yml' | xargs sed -i "s#/${link_old}#/${link_new}#"
done
git add . && git commit -m "Rename markdown files 'tomtom-indigo' -> 'tomtom-digital-cockpit'."

# echo ""
# echo "Rename image files."
# for extension in "plantuml" "svg" "png"
# do
#     for file in $(find . -name "*tomtom-indigo*.$extension" | sort)
#     do
#         dir=$(dirname "$file" | sed "s#^./##")
#         file_old=$(basename "$file")
#         file_new=$(basename "$file_old" | sed "s#tomtom-indigo#tomtom-digital-cockpit#")
#         path_old="$dir/$file_old"
#         path_new="$dir/$file_new"
#         echo "File rename: $path_old -> $path_new"
#         git mv "$path_old" "$path_new"
# 
#         link_old=$(echo "$path_old" | sed "s#\.${extension}\$##" | awk -F '/' '{ print $NF }')
#         link_new=$(echo "$path_new" | sed "s#\.${extension}\$##" | awk -F '/' '{ print $NF }')
#         echo "Link update: $link_old -> $link_new"
#         find . -name '*.md' | xargs sed -i "s#/${link_old}#/${link_new}#"
#     done
# done
# git add . && git commit -m "Rename image files 'tomtom-indigo' -> 'tomtom-digital-cockpit'."

echo ""
echo "Patch 'navigation.yml' files."
for file in $(find . -name 'navigation.yml' -o -name 'top-navigation.yml' | sort)
do
    echo "File: $file"
    sed -i "s#url: /tomtom-indigo/#url: /tomtom-digital-cockpit/#" "$file"
    sed -i "s#^group: tomtom-indigo\$#group: tomtom-digital-cockpit#" "$file"
    sed -i "s#IndiGO#Digital Cockpit#" "$file"
done
git add . && git commit -m "Patch 'navigation.yml' files 'tomtom-indigo' -> 'tomtom-digital-cockpit'."

echo ""
echo "Patch markdown files."
for file in $(find . -name '*.md' | sort)
do
    echo "File: $file"
    sed -i "s#IndiGO#Digital Cockpit#g" "$file"
    sed -i "s#-tomtom-indigo#-tomtom-digital-cockpit#g" "$file"  # Reference to page section.
    sed -i "s#://aaos.blob.core.windows.net/indigo-automotive/#://aaos.blob.core.windows.net/tomtom-digital-cockpit/#" "$file"
    sed -i "s#indigodependencies.versioncatalog.gradle.kts#ividependencies.versioncatalog.gradle.kts#" "$file"
    sed -i "s#<TOMTOM-INDIGO-VERSION>#<TOMTOM-DIGITAL-COCKPIT-VERSION>#" "$file"
done
git add . && git commit -m "Patch markdown file content 'IndiGO' -> 'Digital Cockpit'."

echo ""
echo "Patch example code."
for file in $(find . -name '*.kt' -o -name '*.xml' | sort)
do
    echo "File: $file"
    sed -i "s#TomTom IndiGO#TomTom Digital Cockpit#g" "$file"
    sed -i "s#-tomtom-indigo#-tomtom-digital-cockpit#g" "$file"  # Reference to page section.
    sed -i "s#://developer.tomtom.com/tomtom-indigo/#://developer.tomtom.com/tomtom-digital-cockpit/#g" "$file"  # Reference to page section.
done
git add . && git commit -m "Patch markdown file content 'IndiGO' -> 'Digital Cockpit'."

# echo ""
# echo "Patch plantuml files."
# for file in $(find . -name '*.plantuml' | sort)
# do
#     echo "File: $file"
#     sed -i "s#IndiGO#Digital Cockpit#g" "$file"
#     sed -i "s#Indigo#DigitalCockpit#g" "$file"
#     plantuml -tsvg "$file"
# done
# git add . && git commit -m "Patch plantuml file content 'IndiGO' -> 'Digital Cockpit'."

# echo ""
# echo "Patch SVG files."
# for file in $(find . -name '*.svg' | sort)
# do
#     if [ -f "`dirname $file`/`basename $file .svg`.plantuml" ]
#     then
#         echo "Skipped: $file  because the .plantuml file is available."
#     else
#         echo "File: $file"
#         sed -i "s#IndiGO#Digital Cockpit#g" "$file"
#     fi
# done
# git add . && git commit -m "Patch SVG file content 'IndiGO' -> 'Digital Cockpit'."

echo ""
echo "Rename TTIVI_xx_API placeholders."
for file in $(find . -name '*.md' | sort)
do
    echo "File: $file"
    sed -i "s#](TTIVI_INDIGO_API)#](TTIVI_PLATFORM_API)#g" "$file"
    sed -i "s#](TTIVI_INDIGO_GRADLEPLUGINS_API)#](TTIVI_GRADLEPUGINS_API)#g" "$file"
    sed -i "s#](TTIVI_INDIGO_COMMS_API)#](TTIVI_COMMS_API)#g" "$file"
done
for file in "docs/portal/scripts/api_link_generator.py"
do
    echo "File: $file"
    sed -i 's#"TTIVI_INDIGO_API"#"TTIVI_PLATFORM_API"#g' "$file"
    sed -i 's#INDIGO_PLACEHOLDER#PLATFORM_API_PLACEHOLDER#g' "$file"

    sed -i 's#"TTIVI_INDIGO_GRADLEPLUGINS_API"#"TTIVI_GRADLEPUGINS_API"#g' "$file"
    sed -i 's#INDIGO_GRADLEPLUGINS_PLACEHOLDER#GRADLEPUGINS_API_PLACEHOLDER#g' "$file"

    sed -i 's#"TTIVI_INDIGO_COMMS_API"#"TTIVI_COMMS_API"#g' "$file"
    sed -i 's#INDIGO_COMMS_PLACEHOLDER#COMMS_API_PLACEHOLDER#g' "$file"

    sed -i 's#ANDROID_TOOLS_PLACEHOLDER#ANDROID_TOOLS_API_PLACEHOLDER#g' "$file"
done
git add . && git commit -m "Rename TTIVI_xx_API placeholders."

echo ""
echo "Patch 'api_link_generator.py' script."
file="docs/portal/scripts/api_link_generator.py"
echo "File: $file"
# Patch S3 URLs.
sed -i 's#INDIGO_S3_BASE_URL#S3_BASE_URL#' "$file"
sed -i 's#"https://developer.tomtom.com/assets/downloads/tomtom-indigo"#"https://developer.tomtom.com/assets/downloads/tomtom-digital-cockpit"#' "$file"
sed -i 's#/tomtom-indigo-api/#/platform-api/#g' "$file"
sed -i 's#/tomtom-indigo-gradleplugins-api/#/gradleplugins-api/#g' "$file"
sed -i 's#/tomtom-indigo-comms-sdk-api/#/comms-sdk-api/#g' "$file"
# Rename download directories.
sed -i 's#{DOWNLOAD_DIR}/indigo_{#{DOWNLOAD_DIR}/platform_{#' "$file"
sed -i 's#{DOWNLOAD_DIR}/indigo_gradleplugins_{#{DOWNLOAD_DIR}/gradleplugins_{#' "$file"
sed -i 's#{DOWNLOAD_DIR}/indigo_comms_{#{DOWNLOAD_DIR}/comms_{#' "$file"
# Rename variables.
#   Line 253
sed -i 's#indigo_version#platform_version#g' "$file"
sed -i 's#indigo_gradleplugins_version#gradleplugins_version#g' "$file"
sed -i 's#indigo_comms_version#comms_version#g' "$file"
#   Line 265
sed -i 's#indigo_base_url#platform_s3_url#g' "$file"
sed -i 's#indigo_gradleplugins_base_url#gradleplugins_s3_url#g' "$file"
sed -i 's#indigo_comms_base_url#comms_s3_url#g' "$file"
#   Line 271
sed -i 's#indigo_artifactory_url#platform_artifactory_url#g' "$file"
sed -i 's#indigo_gradleplugins_artifactory_url#gradleplugins_artifactory_url#g' "$file"
sed -i 's#indigo_comms_artifactory_url#comms_artifactory_url#g' "$file"
#   Line 277
sed -i 's#indigo_download_dir#platform_download_dir#g' "$file"
sed -i 's#indigo_gradleplugins_download_dir#gradleplugins_download_dir#g' "$file"
sed -i 's#indigo_comms_download_dir#comms_download_dir#g' "$file"
#   Line 295
sed -i 's#indigo_map#platform_map#g' "$file"
sed -i 's#indigo_gradleplugins_map#gradleplugins_map#g' "$file"
sed -i 's#indigo_comms_map#comms_download_dir#g' "$file"
# Patch comments.
#   Line 247
sed -i 's#\] IndiGO version.#\] IVI Platform version.#' "$file"
sed -i 's#\] IndiGO Comms SDK version.#\] IVI Comms SDK version.#' "$file"
#   Line 259
sed -i 's#IndiGO Platform version {#IVI Platform version {#' "$file"
sed -i 's#IndiGO Gradle Plugins version {#IVI Gradle Plugins version {#' "$file"
sed -i 's#IndiGO Comms SDK version {#IVI Comms SDK version {#' "$file"
# Commit to Git.
git add "$file" && git commit -m "Patch 'api_link_generator.py' script."

echo ""
echo "Patch 'api_releases_generator.py' script."
file="docs/portal/scripts/api_releases_generator.py"
echo "File: $file"
# Patch S3 URLs.
sed -i 's#INDIGO_S3_BASE_URL#S3_BASE_URL#' "$file"
sed -i 's#"https://developer.tomtom.com/assets/downloads/tomtom-indigo"#"https://developer.tomtom.com/assets/downloads/tomtom-digital-cockpit"#' "$file"
sed -i 's#INDIGO_BASE_URL#PLATFORM_BASE_URL#' "$file"
sed -i 's#INDIGO_GRADLEPLUGINS_BASE_URL#GRADLEPLUGINS_BASE_URL#' "$file"
sed -i 's#INDIGO_COMMS_BASE_URL#COMMS_BASE_URL#' "$file"
sed -i 's#}/tomtom-indigo-api#}/platform-api#' "$file"
sed -i 's#}/tomtom-indigo-gradleplugins-api#}/gradleplugins-api#g' "$file"
sed -i 's#}/tomtom-indigo-comms-sdk-api#}/comms-sdk-api#g' "$file"
# Patch Artifactory URL.
sed -i 's#/ivi/releases-data/tomtom-indigo-sdk/releases.json#/ivi/releases-data/tomtom-digital-cockpit-sdk/releases.json#' "$file"
# Patch GitHub URL.
sed -i 's#://github.com/tomtom-international/tomtom-indigo-sdk-examples/#://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/#' "$file"
# Rename variables.
#   Line 205
sed -i 's#indigo_version#platform_version#g' "$file"
# Patch strings.
#   Line 158
sed -i 's#TomTom IndiGO SDK#TomTom Digital Cockpit SDK#' "$file"
sed -i 's#tomtom-indigo-sdk-#tomtom-digital-cockpit-sdk-#g' "$file"
#   Line 212
sed -i 's#TomTom IndiGO platform#TomTom Digital Cockpit platform#' "$file"
sed -i 's#TomTom IndiGO Gradle plugins#TomTom Digital Cockpit Gradle plugins#' "$file"
sed -i 's#TomTom IndiGO Comms SDK#TomTom Digital Cockpit Comms SDK#' "$file"
# Patch comments.
#   Line 123
sed -i 's#IndiGO S3 bucket#Digital Cockpit S3 bucket#' "$file"
sed -i 's#\] IndiGO Comms SDK version.#\] Comms SDK version.#' "$file"
# Commit to Git.
git add "$file" && git commit -m "Patch 'api_releases_generator.py' script."

echo ""
echo "Patch 'portal_generator.py' script."
file="docs/portal/scripts/portal_generator.py"
echo "File: $file"
# Patch S3 URLs.
sed -i 's#"https://developer.tomtom.com/assets/downloads/tomtom-indigo"#"https://developer.tomtom.com/assets/downloads/tomtom-digital-cockpit"#' "$file"
sed -i 's#/tomtom-indigo-api/#/platform-api/#g' "$file"
sed -i 's#/tomtom-indigo-gradleplugins-api/#/gradleplugins-api/#g' "$file"
sed -i 's#/tomtom-indigo-comms-sdk-api/#/comms-sdk-api/#g' "$file"
# Patch comments.
#   Line 21
sed -i "s#<indigo-version>#<ivi-platform-version>#g" "$file"
sed -i "s#<indigo-comms-version>#<ivi-comms-version>#g" "$file"
#   Line 38
sed -i "s#](TTIVI_INDIGO_API)#](TTIVI_PLATFORM_API)#g" "$file"
sed -i "s#/downloads/tomtom-indigo/#/downloads/tomtom-digital-cockpit/#" "$file"
#   Line 62
sed -i 's#\] IndiGO version.#\] IVI Platform version.#' "$file"
sed -i 's#\] IndiGO Comms SDK version.#\] IVI Comms SDK version.#' "$file"
# Commit to Git.
git add "$file" && git commit -m "Patch 'portal_generator.py' script."

echo ""
echo "Patch 'url_validator.py' script."
file="docs/portal/scripts/url_validator.py"
echo "File: $file"
# Patch regex for internal URLs.
sed -i 's#()/tomtom-indigo/#()/tomtom-digital-cockpit/#' "$file"
sed -i 's#()tomtom-indigo/#()tomtom-digital-cockpit/#' "$file"
# Patch GitHub URL.
sed -i 's#TomTom IndiGO#TomTom Digital Cockpit#' "$file"
sed -i 's#REGEX_INDIGO_GITHUB_URL#REGEX_DIGITAL_COCKPIT_GITHUB_URL#g' "$file"
sed -i 's#://github.com/tomtom-international/tomtom-indigo-sdk-examples/#://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/#' "$file"
# Commit to Git.
git add "$file" && git commit -m "Patch 'api_link_generator.py' script."

# echo ""
# echo "Rename build files."
# mv build-logic/indigodependencies.versioncatalog.gradle.kts build-logic/ividependencies.versioncatalog.gradle.kts
# mv buildSrc/tasks/indigoPlatformUpdate.gradle.kts buildSrc/tasks/iviPlatformUpdate.gradle.kts
# git add . && git commit -m "Rename build files."

# echo ""
# echo "Patch build files."
# for file in $(find . -name settings.gradle.kts)
# do
#     echo "File: $file"
#     sed -i "s#build-logic/indigodependencies.versioncatalog.gradle.kts#build-logic/ividependencies.versioncatalog.gradle.kts#" "$file"
# done
# filelist="build-logic/libraries.versions.toml"
# filelist+=" $(find . -name '*.gradle.kts' | sort)"
# filelist+=" $(find docs/portal/src -name '*.md' | sort)"
# filelist+=" $(find . -name 'README.md' | sort)"
# for file in $filelist
# do
#     echo "File: $file"
#     sed -i "s#TomTom IndiGO#TomTom Digital Cockpit#g" "$file"
#     sed -i "s#indigoPlatform#iviPlatform#g" "$file"
#     sed -i "s#indigoVersion#platformVersion#g" "$file"
#     sed -i "s#indigoAppsuite#iviAppsuite#g" "$file"
#     sed -i "s#indigoDependencies#iviDependencies#g" "$file"
#     sed -i "s#indigo repository#tomtom-digital-cockpit repository#g" "$file"
#     sed -i "s#getIndigoPlatformVersionFromTomlFile#getPlatformVersionFromTomlFile#g" "$file"
# done
# git add . && git commit -m "Patch build files."

# echo ""
# echo "Patch platform update files."
# sourceFile=$(find buildSrc/src -name 'IndigoUpdateHelper.kt')
# filelist=".azure-templates/indigo-update.yml"
# filelist+=" azure-pipelines-update.yml"
# filelist+=" buildSrc/tasks/iviPlatformUpdate.gradle.kts"
# filelist+=" $sourceFile"
# for file in $filelist
# do
#     echo "File: $file"
#     sed -i "s#generateIndigoLibrariesVersionFile#generatePlatformLibrariesVersionFile#g" "$file"
#     sed -i "s#indigoPlatform#iviPlatform#g" "$file"
#     sed -i "s#IndigoUpdateHelper#PlatformUpdateHelper#g" "$file"
#     sed -i "s#indigoUpdateHelper#updateHelper#g" "$file"
#     sed -i "s#latestIndigoVersion#latestPlatformVersion#g" "$file"
#     sed -i "s#newIndigoVersion#newPlatformVersion#g" "$file"
#     sed -i "s#TomTom IndiGO version#TomTom Digital Cockput platform version#g" "$file"
#     sed -i "s#currentVersion IndiGO version#currentVersion platform version#g" "$file"
# done
# mv "$sourceFile" "$(dirname $sourceFile)/PlatformUpdateHelper.kt"
# git add . && git commit -m "Patch platform update files and class IndigoUpdateHelper."

echo ""
echo "OK"
