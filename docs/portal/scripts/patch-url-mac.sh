

echo "Patch main URL."
for file in $(find . -name '*.mdx' | sort)
do
    gsed -i 's#/tomtom-digital-cockpit/developers/getting-started/#/tomtom-digital-cockpit/getting-started/#' "$file"
done