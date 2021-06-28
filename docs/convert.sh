#!/usr/bin/env sh

die()
{
    echo $*
    exit 1
}

which pandoc || die "Unable to run pandoc. It is required to convert Markdown to HTML files."

test $# -eq 1 || die "This script should only be run by the Gradle task. Usage: ./gradlew docs"
output_dir=$1
mkdir -p "$output_dir" || die "Failed to create output directory: $output_dir."

files=`ls *.md`
test -n "$files" || die "Cannot find input files of type '.md'"

for input_file in $files
do
  output_file=$output_dir/${input_file%.md}".html"
  pandoc -s -o "$output_file" "$input_file" || die "Pandoc failed on input file: $input_file."
done

