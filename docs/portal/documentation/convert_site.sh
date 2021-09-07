#!/usr/bin/env bash

die()
{
    echo $*
    exit 1
}

# Set to true, to enable our own stylesheets on the site.
use_local_css=false

which pandoc || die "Cannot run pandoc. It is required to convert Markdown to HTML files."

output_dir="../../../build/html"
rm -rf "$output_dir" || die "Cannot remove old documentation: $output_dir."
mkdir -p "$output_dir" || die "Cannot create output directory: $output_dir."
pushd "$output_dir" >/dev/null 2>&1
output_dir=`pwd`
popd >/dev/null 2>&1
echo "Output directory: $output_dir"

folders=`find . ! -path . -type d`    # Excludes the current folder

links_index=0
for folder in $folders
do
  pushd "$folder" >/dev/null 2>&1
  folder="${folder:2}"    # remove leading './' 
  echo "Scanning: '$folder'"
  slashes="${folder//[^\/]}"
  num_subfolders=${#slashes}
  num_subfolders=$((num_subfolders+1))
  leaf_folder=`basename $folder`
  input_files=`ls *.md 2>/dev/null`
  for in_file in $input_files
    do
      echo "  Converting: $in_file"
      file_wo_ext=`basename $in_file .md`
      # If the MD file has the same name as the folder it's inside, then output it to index.html
      if [ "$file_wo_ext" == "$leaf_folder" ]; then
        out_file="$output_dir/$folder/index.html"
        link="$folder/index.html"
      else
        out_file=$output_dir/$folder/${in_file%.md}".html"
        link="$folder/${in_file%.md}.html"
      fi 
      mkdir -p $output_dir/$folder || die "Can't create $output_dir/$folder"
      echo "  to: $out_file"
      if $use_local_css ; then
        css_path=""
        for j in $(seq 1 $num_subfolders); do
          css_path+="../"
        done
        pandoc -s -c "$css_path"style.css -o "$out_file" "$in_file" || die "Pandoc failed on input file: $in_file."
      else
        pandoc -s -o "$out_file" "$in_file" || die "Pandoc failed on input file: $in_file."
      fi
      links[links_index]=$link
      links_index=$((links_index+1))
    done
  popd >/dev/null 2>&1
done

if $use_local_css ; then
  stylesheet_link="<link rel="stylesheet" href="style.css" />"
else
  stylesheet_link=""
fi

index_file="$output_dir"/"index.html"
echo "Writing index file: $index_file"
cat << EOF > $index_file
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html
        PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <title>IndiGO Developer Portal</title>
  $stylesheet_link
</head>
<body>
<H1>IndiGO Developer Portal</H1>
EOF

for j in $(seq 0 ${#links[@]}); do
  hlink=${links[j]}
  echo "<p><a href=\"$hlink\">$hlink</a></p>" >> $index_file
done

cat << EOF >> $index_file
</body>
</html>
EOF

if $use_local_css ; then
  cp ../style.css $output_dir/style.css || die "Cannot copy stylesheets."
fi
