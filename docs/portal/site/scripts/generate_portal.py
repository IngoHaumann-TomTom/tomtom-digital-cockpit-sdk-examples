#!/usr/bin/python3

# Copyright © 2020 TomTom NV. All rights reserved.
#
# This software is the proprietary copyright of TomTom NV and its subsidiaries and may be
# used for internal evaluation purposes or commercial use strictly subject to separate
# license agreement between you and TomTom NV. If you are the licensee, you are only permitted
# to use this software in accordance with the terms of your license agreement. If you are
# not the licensee, you are not authorized to use this software in any manner and should
# immediately return or destroy it.

# This script generates the files that get uploaded to the Developer Portal.
# It also allows us to check and generate URLs within the Developer Portal pages, this makes the
# content on the Developer Portal easier to maintain.
# Guidelines on adding links to the Developer Portal pages:
# https://confluence.tomtomgroup.com/display/SSAUTO/Writing+Documentation+for+the+Developer+Portal
# This script is not used on its own, but by running `./gradlew portal` from the repository's root.
#
# There are multiple steps to this script:
#   - Intermediate Markdown files are created from the Markdown files in the 'src' directory.
#   - API Reference placeholders get replaced by API Reference URLs in the intermediate files.
#   - The intermediate files get converted to HTML, using Jekyll.
#   - The HTML files are zipped per Developer Portal subsection and moved to the build/portal
#     directory at the repository's root, ready to be uploaded.
#
# An example of replacing an API Reference placeholder by an API Reference URL:
#   [OverlayPanel](TTIVI_INDIGO_API)
# becomes
#   [OverlayPanel](https://developer.tomtom.com/assets/downloads/indigo/indigo-api/latest/api_framework_frontend/com.tomtom.ivi.api.framework.frontend.panels/-overlay-panel/index.html)

import shutil
import os
import json
import re
import sys
from os.path import basename
from urllib.request import urlopen
from pathlib import Path
from zipfile import ZipFile

TARGET_DIR = sys.argv[1]
SOURCE_DIR = "src"
INTERMEDIATE_DIR = "build/intermediate"
HTML_DIR = "build/html"
TARGET_FILETYPE = "*.md"
INPUT_CHECK_FILE = "documentation/2. developing/index.md"

# TODO(IVI-5408): Replace latest by SDK Release versions.
INDIGO_BASE_URL = "https://developer.tomtom.com/assets/downloads/indigo/indigo-api/latest"
INDIGO_GRADLEPLUGINS_BASE_URL = "https://developer.tomtom.com/assets/downloads/indigo/indigo-gradleplugins-api/latest"
ANDROID_TOOLS_BASE_URL = "https://developer.tomtom.com/assets/downloads/indigo/android-tools-api/latest"
JSON_POSTFIX_URL = "scripts/navigation-pane.json"

# The placeholders in the Markdown files will get replaced by an API Reference URL.
INDIGO_PLACEHOLDER = "TTIVI_INDIGO_API"
INDIGO_GRADLEPLUGINS_PLACEHOLDER = "TTIVI_INDIGO_GRADLEPLUGINS_API"
ANDROID_TOOLS_PLACEHOLDER = "TTIVI_ANDROID_TOOLS_API"

# TODO(IVI-5445): Add support for API elements within backticks.
# Regex pattern to retrieve the API element without brackets.
REGEX_API_ELEMENT = "(?<=\[).*(?=\])"

# Regex patterns to retrieve full API-links: [api-element](placeholder).
REGEX_INDIGO_PLACEHOLDER = f"\[.*?\]\({INDIGO_PLACEHOLDER}\)"
REGEX_INDIGO_GRADLEPLUGINS_PLACEHOLDER = f"\[.*?\]\({INDIGO_GRADLEPLUGINS_PLACEHOLDER}\)"
REGEX_ANDROID_TOOLS_PLACEHOLDER = f"\[.*?\]\({ANDROID_TOOLS_PLACEHOLDER}\)"

def input_validation():
    if len(sys.argv) != 2:
        raise Exception(f"Usage: {sys.argv[0]} <target-directory>.")
    if not os.path.exists(os.path.join(SOURCE_DIR, INPUT_CHECK_FILE)):
        raise Exception("Script was run from incorrect working directory.")

def clean_old_files():
    '''Clean old build files.'''
    os.system("jekyll clean")
    if os.path.exists(INTERMEDIATE_DIR):
        shutil.rmtree(INTERMEDIATE_DIR)

def create_intermediate_files():
    '''Create intermediate files.'''
    shutil.copytree(SOURCE_DIR, INTERMEDIATE_DIR)

def get_json_map(json_url):
    '''
    Retrieve the API Reference JSON from `json_url`.
    Process the JSON and return a map of `name` and `location` pairs to be used as API Reference lookup.
    '''
    print(f"Retrieving JSON data from {json_url}.")
    raw_json = json.loads(urlopen(json_url).read())
    if not raw_json:
        raise Exception(f"JSON can't be retrieved from {json_url}.")
    map = {}
    for item in raw_json:
        map[item["name"]] = item["location"]
    return map

def url_lookup(api_reference_map, regex_match, path):
    '''
    Return a URL postfix for the 'api_element' in 'regex_match'.
    Use 'api_reference_map' as a lookup to find the URL postfix.
    Parameter 'path' is used only for error logging.
    Raise Exception when lookup fails.
    '''
    api_element = (re.search(REGEX_API_ELEMENT, regex_match)).group(0)
    url = api_reference_map.get(api_element)
    if not url:
        raise Exception(f"API element '{api_element}' in file {path} can't be found in the API Reference map.")
    print(f"Generating URL for {api_element} in file {path}.")
    return url

def process_placeholders():
    '''Replace placeholders with API Reference URLs.'''
    file_paths = Path(INTERMEDIATE_DIR).rglob(TARGET_FILETYPE)
    indigo_map = get_json_map(os.path.join(INDIGO_BASE_URL, JSON_POSTFIX_URL))
    indigo_gradleplugins_map = get_json_map(os.path.join(INDIGO_GRADLEPLUGINS_BASE_URL, JSON_POSTFIX_URL))
    android_tools_map = get_json_map(os.path.join(ANDROID_TOOLS_BASE_URL, JSON_POSTFIX_URL))
    for path in file_paths:
        with open(path, 'r+') as file:
            content = file.read()
            # Replace Indigo API Reference placeholders.
            for match in re.findall(REGEX_INDIGO_PLACEHOLDER, content):
                content = content.replace(INDIGO_PLACEHOLDER, \
                    os.path.join(INDIGO_BASE_URL, url_lookup(indigo_map, match, path)), 1)
            # Replace Indigo Gradleplugins API Reference placeholders.
            for match in re.findall(REGEX_INDIGO_GRADLEPLUGINS_PLACEHOLDER, content):
                content = content.replace(INDIGO_GRADLEPLUGINS_PLACEHOLDER, \
                    os.path.join(INDIGO_GRADLEPLUGINS_BASE_URL, url_lookup(indigo_gradleplugins_map, match, path)), 1)
            # Replace TomTom Android Tools API Reference placeholders.
            for match in re.findall(REGEX_ANDROID_TOOLS_PLACEHOLDER, content):
                content = content.replace(ANDROID_TOOLS_PLACEHOLDER, \
                    os.path.join(ANDROID_TOOLS_BASE_URL, url_lookup(android_tools_map, match, path)), 1)
            file.seek(0)
            file.write(content)
            file.truncate()

def zip_files(directory):
    '''Zip utility function.'''
    source_dir = os.path.join(HTML_DIR, directory)
    old_dir = os.getcwd()
    dest_zip = os.path.join(TARGET_DIR, (directory + ".zip"))
    print (f"Zipping Developer Portal files in {os.path.join(old_dir, source_dir)}.")
    os.chdir(source_dir)
    with ZipFile(dest_zip, "w") as zip:
        for directory, subfolders, filenames in os.walk('./'):
            for filename in filenames:
                zip.write(os.path.join(directory, filename))
    os.chdir(old_dir)

def build_zip():
    '''Convert and zip Developer Portal files.'''
    ret = os.system("jekyll build")
    if not (ret == 0):
        raise Exception("Jekyll build failed.")
    os.makedirs(TARGET_DIR, exist_ok=True)
    zip_files("documentation")
    zip_files("api-reference")
    zip_files("releases")

input_validation()
clean_old_files()
create_intermediate_files()
process_placeholders()
build_zip()



