#!/usr/bin/env sh
# Copyright © 2020 TomTom NV. All rights reserved.
#
# This software is the proprietary copyright of TomTom NV and its subsidiaries and may be
# used for internal evaluation purposes or commercial use strictly subject to separate
# license agreement between you and TomTom NV. If you are the licensee, you are only permitted
# to use this software in accordance with the terms of your license agreement. If you are
# not the licensee, you are not authorized to use this software in any manner and should
# immediately return or destroy it.

die()
{
    echo $*
    exit 1
}

which pandoc || die "Cannot run pandoc. It is required to convert Markdown to HTML files."

test $# -eq 1 || die "This script must be used by Gradle only. Usage: ./gradlew docs"
output_dir=$1
echo "Output directory: $output_dir"

rm -rf "$output_dir" || die "Cannot remove old documentation: $output_dir."
mkdir "$output_dir" || die "Cannot create output directory: $output_dir."

input_files=`ls *.md`
test -n "$input_files" || die "Cannot find input files of type '.md'"

for in_file in $input_files
do
  out_file=$output_dir/${in_file%.md}".html"
  pandoc -s --number-sections -c style.css -o "$out_file" "$in_file" || die "Pandoc failed on input file: $in_file."
done

cp style.css $output_dir/style.css || die "Cannot copy stylesheets."
mkdir $output_dir/images || die "Cannot create output directory: $output_dir/images."
cp images/* $output_dir/images || die "Cannot copy images directory."
