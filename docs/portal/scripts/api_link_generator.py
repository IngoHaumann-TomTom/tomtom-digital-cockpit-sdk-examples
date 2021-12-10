#!/usr/bin/python3

# Copyright © 2020 TomTom NV. All rights reserved.
#
# This software is the proprietary copyright of TomTom NV and its subsidiaries and may be
# used for internal evaluation purposes or commercial use strictly subject to separate
# license agreement between you and TomTom NV. If you are the licensee, you are only permitted
# to use this software in accordance with the terms of your license agreement. If you are
# not the licensee, you are not authorized to use this software in any manner and should
# immediately return or destroy it.

import re
import os
import json
from pathlib import Path
from urllib.request import urlopen

TARGET_FILETYPE = "*.md"

# The API Reference versions used by the API link generating script. These are not coupled to the
# versions used in the Example App (Versions.kt). This grants us more control over when to update
# the documentation and helps us avoid breaking changes, which disables us from making exports.
INDIGO_VERSION = "1.0.2904"
INDIGO_GRADLEPLUGINS_VERSION = INDIGO_VERSION
INDIGO_COMMS_VERSION = "0.1.92"
ANDROID_TOOLS_VERSION = "0.0.282"

INDIGO_BASE_URL = f"https://developer.tomtom.com/assets/downloads/indigo/indigo-api/{INDIGO_VERSION}"
INDIGO_GRADLEPLUGINS_BASE_URL = f"https://developer.tomtom.com/assets/downloads/indigo/indigo-gradleplugins-api/{INDIGO_GRADLEPLUGINS_VERSION}"
INDIGO_COMMS_BASE_URL = f"https://developer.tomtom.com/assets/downloads/indigo/indigo-comms-api/{INDIGO_COMMS_VERSION}"
ANDROID_TOOLS_BASE_URL = f"https://developer.tomtom.com/assets/downloads/indigo/android-tools-api/{ANDROID_TOOLS_VERSION}"
JSON_POSTFIX_URL = "scripts/navigation-pane.json"

# The placeholders in the Markdown files that will be replaced by an API Reference URL.
INDIGO_PLACEHOLDER = "TTIVI_INDIGO_API"
INDIGO_GRADLEPLUGINS_PLACEHOLDER = "TTIVI_INDIGO_GRADLEPLUGINS_API"
INDIGO_COMMS_PLACEHOLDER = "TTIVI_INDIGO_COMMS_API"
ANDROID_TOOLS_PLACEHOLDER = "TTIVI_ANDROID_TOOLS_API"

# Regex patterns to find API-links: [api-element](placeholder).
REGEX_INDIGO_PLACEHOLDER = f"\[.*?\]\({INDIGO_PLACEHOLDER}\)"
REGEX_INDIGO_GRADLEPLUGINS_PLACEHOLDER = f"\[.*?\]\({INDIGO_GRADLEPLUGINS_PLACEHOLDER}\)"
REGEX_INDIGO_COMMS_PLACEHOLDER = f"\[.*?\]\({INDIGO_COMMS_PLACEHOLDER}\)"
REGEX_ANDROID_TOOLS_PLACEHOLDER = f"\[.*?\]\({ANDROID_TOOLS_PLACEHOLDER}\)"

# Regex pattern to retrieve the API element without brackets.
REGEX_API_ELEMENT = "(?<=\[).*(?=\])"

# Regex pattern to retrieve all placeholders.
REGEX_GENERIC_PLACEHOLDER = "(?<=\]\()TTIVI_.*?(?=\))"

def is_valid_placeholder(match):
    '''
    Checks whether 'match' has correct placeholder syntax. Returns a boolean.
    '''
    return match == INDIGO_PLACEHOLDER or \
        match == INDIGO_GRADLEPLUGINS_PLACEHOLDER or \
        match == INDIGO_COMMS_PLACEHOLDER or \
        match == ANDROID_TOOLS_PLACEHOLDER

def get_json_map(json_url):
    '''
    Retrieves a JSON object that contains all pages of an API Reference.
    Maps the 'name' and 'location' fields of the JSON object to 'api_element' and 'url' key-value
    pairs and returns the map.

    Parameters
    -----------
    json_url : str
        The URL to the JSON file to retrieve.

    Returns
    -----------
    map
        A map of 'api_element' and 'url' key-value pairs.
    '''
    raw_json = json.loads(urlopen(json_url).read())
    if not raw_json:
        raise ConnectionError(f"JSON cannot be retrieved from {json_url}.")
    map = {}
    for item in raw_json:
        map[item["name"]] = item["location"]
    return map

def url_lookup(api_reference_map, regex_match, path, errors):
    '''
    Returns the URL postfix for a matched API placeholder.
    Retrieves an 'api_element' from 'regex_match' and uses that as a lookup to find the
    corresponding 'url' in the 'api_reference_map'.
    Raises an Exception when the `api_element` cannot be found in `api_reference_map`.

    Parameters
    -----------
    api_reference_map : map
        A map containing 'api_element' and 'url' key-value pairs.
    regex_match : str
        A string containing the full Markdown link, including the API element and placeholder.
    path : str
        A string containing the path of the file in which the link was found. For logging purposes.
    errors : list
        A list containing error messages for logging purposes. Any new error(s) will be appended to this list.

    Returns
    -----------
    str
        The URL postfix of the matched API element.
    '''
    api_element = (re.search(REGEX_API_ELEMENT, regex_match)).group(0)

    if api_element == None:
        raise SyntaxError(f"API element cannot be captured from regex match {regex_match} in {path}.")

    # Trim api_element when surrounded by backticks.
    if api_element[0] == '`' and api_element[-1] == '`':
        api_element = api_element[1:-1]

    # Trim api_element when prepended by '@'.
    if api_element[0] == '@':
        api_element = api_element[1:]

    url = api_reference_map.get(api_element)
    if not url:
        errors.append(f"API element '{api_element}' in {path} cannot be found in the API Reference map.")
        return ""

    return url

def validate_placeholders(target_dir):
    '''
    Validates whether the placeholder syntax is correctly applied.
    Raises an Exception when an incorrect placeholder is found.

    Parameters
    -----------
    target_dir : str
        The files within this directory will be checked for placeholder syntax.
    '''
    errors = []
    for path in Path(target_dir).rglob(TARGET_FILETYPE):
        with open(path, 'r+', encoding="utf-8") as file:
            content = file.read()
            for match in re.findall(REGEX_GENERIC_PLACEHOLDER, content):
                if not is_valid_placeholder(match):
                    errors.append(f"{match} in file {path}")
    if len(errors):
        raise SyntaxError("Encountered {} error(s):\n{}".format(len(errors), '\n'.join(errors)))

def api_link_generator(target_dir):
    '''
    Replaces all API placeholders with the corresponding API Reference URLs.

    Parameters
    -----------
    target_dir : str
        The files within this directory will be altered, replacing the placeholders with URLs.
    '''
    validate_placeholders(target_dir)

    indigo_map = get_json_map(os.path.join(INDIGO_BASE_URL, JSON_POSTFIX_URL))
    indigo_gradleplugins_map = get_json_map(os.path.join(INDIGO_GRADLEPLUGINS_BASE_URL, JSON_POSTFIX_URL))
    indigo_comms_map = get_json_map(os.path.join(INDIGO_COMMS_BASE_URL, JSON_POSTFIX_URL))
    android_tools_map = get_json_map(os.path.join(ANDROID_TOOLS_BASE_URL, JSON_POSTFIX_URL))

    errors = []
    for path in Path(target_dir).rglob(TARGET_FILETYPE):
        with open(path, 'r+', encoding="utf-8") as file:
            content = file.read()

            # Replace TTIVI_ placeholders.
            for match in re.findall(REGEX_INDIGO_PLACEHOLDER, content):
                content = content.replace(INDIGO_PLACEHOLDER, \
                    os.path.join(INDIGO_BASE_URL, url_lookup(indigo_map, match, path, errors)), 1)
            for match in re.findall(REGEX_INDIGO_GRADLEPLUGINS_PLACEHOLDER, content):
                content = content.replace(INDIGO_GRADLEPLUGINS_PLACEHOLDER, \
                    os.path.join(INDIGO_GRADLEPLUGINS_BASE_URL, url_lookup(indigo_gradleplugins_map, match, path, errors)), 1)
            for match in re.findall(REGEX_INDIGO_COMMS_PLACEHOLDER, content):
                content = content.replace(INDIGO_COMMS_PLACEHOLDER, \
                    os.path.join(INDIGO_COMMS_BASE_URL, url_lookup(indigo_comms_map, match, path, errors)), 1)
            for match in re.findall(REGEX_ANDROID_TOOLS_PLACEHOLDER, content):
                content = content.replace(ANDROID_TOOLS_PLACEHOLDER, \
                    os.path.join(ANDROID_TOOLS_BASE_URL, url_lookup(android_tools_map, match, path, errors)), 1)
            file.seek(0)
            file.write(content)
            file.truncate()

    if len(errors):
        print("Encountered {} error(s):\n{}".format(len(errors), '\n'.join(errors)))
        raise KeyError("API link(s) could not be generated. Make sure you use the correct API Reference versions in api_link_generator.py")
