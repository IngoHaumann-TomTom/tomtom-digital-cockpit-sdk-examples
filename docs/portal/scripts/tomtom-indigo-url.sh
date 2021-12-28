#!/bin/bash

echo "Temporary script to patch 'indigo' in developer portal url's to 'tomtom-indigo'."
echo "See Jira ticket IVI-6184."

die() {
    echo "Error: $*"
    exit 1
}

pwd | grep -e '/docs/portal/src$' > /dev/null || die "Must run from docs/portal/src directory."
set -e

echo ""
echo "Patching main URL."
for file in $(find . -name "*.md" | sort)
do
    sed -i 's#(/indigo/#(/tomtom-indigo/#' "$file"
    sed -i 's#://developer.tomtom.com/indigo/#://developer.tomtom.com/tomtom-indigo/#' "$file"
    sed -i 's#href="/indigo/#href="/tomtom-indigo/#' "$file"
    # Untested: sed -i 's#tomtom.com/products/indigo#/tomtom.com/products/tomtom-indigo#' "$file"
done
git add . && git commit -m "Patching main URL to 'tomtom-indigo'."

echo ""
echo "Renaming directories."
for dir in $(find . -name "*indigo*" -type d ! -name "*tomtom-indigo*" | sort)
do
    dir_old=$dir
    dir_new=$(echo "$dir_old" | sed 's#indigo#tomtom-indigo#')
    echo "Directory rename: $dir_old -> $dir_new"
    git mv "$dir_old" "$dir_new"

    link_old=$(echo "$dir_old" | awk -F '/' '{ print $NF }')
    link_new=$(echo "$dir_new" | awk -F '/' '{ print $NF }')
    echo "Link update: $link_old -> $link_new"
    find . -name "*.md" | xargs sed -i "s#/${link_old}/#/${link_new}/#"

    echo "Yaml update: $link_old -> $link_new"
    find . -name 'navigation.yml' | xargs sed -i "s#/${link_old}/#/${link_new}/#"
done
git add . && git commit -m "Renaming directories 'indigo' -> 'tomtom-indigo'."

echo ""
echo "Renaming markdown files."
for file in $(find . -name "*indigo*.md" ! -name "*tomtom-indigo*" | sort)
do
    dir=$(dirname "$file" | sed "s#^./##")
    file_old=$(basename "$file")
    file_new=$(basename "$file_old" | sed "s#indigo#tomtom-indigo#")
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
    find . -name 'navigation.yml' | xargs sed -i "s#/${link_old}#/${link_new}#"
done
git add . && git commit -m "Renaming markdown files 'indigo' -> 'tomtom-indigo'."

echo ""
echo "Renaming image files."
for extension in "plantuml" "svg" "png"
do
    for file in $(find . -name "*indigo*.$extension" ! -name "*tomtom-indigo*" | sort)
    do
        dir=$(dirname "$file" | sed "s#^./##")
        file_old=$(basename "$file")
        file_new=$(basename "$file_old" | sed "s#indigo#tomtom-indigo#")
        path_old="$dir/$file_old"
        path_new="$dir/$file_new"
        echo "File rename: $path_old -> $path_new"
        git mv "$path_old" "$path_new"

        link_old=$(echo "$path_old" | sed "s#\.${extension}\$##" | awk -F '/' '{ print $NF }')
        link_new=$(echo "$path_new" | sed "s#\.${extension}\$##" | awk -F '/' '{ print $NF }')
        echo "Link update: $link_old -> $link_new"
        find . -name '*.md' | xargs sed -i "s#/${link_old}#/${link_new}#"
    done
done
git add . && git commit -m "Renaming image files 'indigo' -> 'tomtom-indigo'."

echo ""
echo "Patching 'navigation.yml' files."
for file in $(find . -name "navigation.yml" | sort)
do
    echo "File: $file"
    sed -i "s#^group: indigo\$#group: tomtom-indigo#" "$file"
done
git add . && git commit -m "Patching 'navigation.yml' files 'indigo' -> 'tomtom-indigo'."

