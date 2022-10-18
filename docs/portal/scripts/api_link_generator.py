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
import tarfile
import shutil
from pathlib import Path
import requests

TARGET_FILETYPE = "*.md"
DOWNLOAD_DIR = "build/downloads"
IGNORE = ["scripts",  "images", "styles", "package-list"]

# The placeholders in the Markdown files that will be replaced by an API Reference URL.
PLATFORM_PLACEHOLDER = "TTIVI_PLATFORM_API"
GRADLEPLUGINS_PLACEHOLDER = "TTIVI_GRADLEPUGINS_API"
COMMS_PLACEHOLDER = "TTIVI_COMMS_API"
ANDROID_TOOLS_PLACEHOLDER = "TTIVI_ANDROID_TOOLS_API"

# Regex patterns to find API-links: [api-element](placeholder).
REGEX_PLATFORM_PLACEHOLDER = f"\[.*?\]\({PLATFORM_PLACEHOLDER}\)"
REGEX_GRADLEPLUGINS_PLACEHOLDER = f"\[.*?\]\({GRADLEPLUGINS_PLACEHOLDER}\)"
REGEX_COMMS_PLACEHOLDER = f"\[.*?\]\({COMMS_PLACEHOLDER}\)"
REGEX_ANDROID_TOOLS_PLACEHOLDER = f"\[.*?\]\({ANDROID_TOOLS_PLACEHOLDER}\)"

S3_BASE_URL = "https://developer.tomtom.com/assets/downloads/tomtom-digital-cockpit"
ARTIFACTORY_BASE_URL = "https://artifactory.navkit-pipeline.tt3.com/artifactory"

# Regex pattern to retrieve the API element without brackets.
REGEX_API_ELEMENT = "(?<=\[).*(?=\])"

# Regex pattern to retrieve all placeholders.
REGEX_GENERIC_PLACEHOLDER = "(?<=\]\()TTIVI_.*?(?=\))"

def is_valid_placeholder(match):
    '''
    Checks whether 'match' has correct placeholder syntax. Returns a boolean.

    Parameters
    -----------
    match : str
        The match to validate.

    Returns
    -----------
    bool
        True if valid placeholder syntax.

    '''
    return match == PLATFORM_PLACEHOLDER or \
        match == GRADLEPLUGINS_PLACEHOLDER or \
        match == COMMS_PLACEHOLDER or \
        match == ANDROID_TOOLS_PLACEHOLDER

def download_api_ref(artifactory_url, target_dir):
    '''
    Downloads API Reference from Artifactory to specified target_dir.

    Parameters
    -----------
    artifactory_url : str
        The URL to the API Reference tarball on Artifactory which contains the JSON file.
    target_dir : str
        The directory to download the API Reference tarball to.
    '''
    download_target = os.path.join(target_dir, "api-reference.tar.gz")
    os.makedirs(target_dir)

    # Download and extract API Reference from Artifactory.
    response = requests.get(artifactory_url)
    if not response.ok:
        raise ConnectionError(f"API Reference cannot be retrieved from {url} (status {status}.")
    with open(download_target, "wb") as file:
        file.write(response.content)
    with tarfile.open(download_target, 'r') as archive:
        archive.extractall(target_dir)

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
    if not api_element:
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
        raise SyntaxError("Encountered {} syntax error(s):\n{}".format(len(errors), '\n'.join(errors)))

def mutate_name(property_name):
    '''
    Transforms property names to PascalCase, removing dashes.

    Parameters
    -----------
    property_name : str
        Property name to transform

    Returns
    -----------
    str
        Property name in PascalCase without dashes.
    '''
    capitalized = [word.capitalize() for word in property_name.split("-")]
    return "".join(capitalized)

def get_subdirectories(target_dir):
    '''
    Returns all the subdirectories of target_dir and ignores directories specified by 'IGNORE'.

    Parameters
    -----------
    target_dir : str
        Target directory to return the subdirectories from.

    Returns
    -----------
    list
        List of subdirectories in 'target_dir'
    '''
    sub_dirs = os.listdir(target_dir)
    sub_dirs = [dir for dir in sub_dirs \
        if dir not in IGNORE \
        if os.path.isdir(os.path.join(target_dir, dir))]
    return sub_dirs

def make_index_file(*args):
    '''
    Creates URLs to index files.

    Parameters
    -----------
    *args : str
        Variable number of paths.

    Returns
    -----------
    str
        Concatenated string of paths + "index.html"
    '''
    return os.path.join(*args, "index.html")

def create_index(target_dir):
    '''
    Indexes an API Reference and returns a map of API elements and URLs.

    Parameters
    -----------
    target_dir : str
        The path to a downloaded API Reference.

    Returns
    -----------
    map
        A map of API elements and URL key-value pairs.
    '''
    map = {}

    modules = get_subdirectories(target_dir)
    for module in modules:
        map[module] = make_index_file(module)

        module_path = os.path.join(target_dir, module)
        packages = get_subdirectories(module_path)
        for package in packages:
            map[package] = make_index_file(module, package)

            package_path = os.path.join(module_path, package)
            properties = get_subdirectories(package_path)
            for property in properties:
                map[mutate_name(property)] = make_index_file(module, package, property)
                # TODO(IVI-5714): Add support class functions

    return map

def generate_api_links(target_dir, versions):
    '''
    Replaces all API placeholders with the corresponding API Reference URLs.

    Parameters
    -----------
    target_dir : str
        The files within this directory will be altered, replacing the placeholders with URLs.
    versions : list
        [0] IVI platform version.
        [1] IVI Comms SDK version.
        [2] TomTom Android Tools version.
    '''

    assert (len(versions) == 3), "Invalid number of versions."
    platform_version = versions[0]
    gradleplugins_version = platform_version
    comms_version = versions[1]
    android_tools_version = versions[2]

    print("Using API reference versions:")
    print(f"    IVI Platform version {platform_version}")
    print(f"    IVI Gradle Plugins version {gradleplugins_version}")
    print(f"    IVI Comms SDK version {comms_version}")
    print(f"    TomTom Android Tools version {android_tools_version}")

    # The base URLs of the hosted API References on the S3 bucket.
    platform_base_url = f"{S3_BASE_URL}/platform-api/{platform_version}"
    gradleplugins_base_url = f"{S3_BASE_URL}/gradleplugins-api/{gradleplugins_version}"
    comms_base_url = f"{S3_BASE_URL}/comms-sdk-api/{comms_version}"
    android_tools_base_url = f"{S3_BASE_URL}/tomtom-android-tools-api/{android_tools_version}"

    # The URLs of the API Reference on Artifactory.
    platform_artifactory_url = f"{ARTIFACTORY_BASE_URL}/ivi-maven/com/tomtom/ivi/api-reference-docs/{platform_version}/api-reference-docs-{platform_version}.tar.gz"
    gradleplugins_artifactory_url = f"{ARTIFACTORY_BASE_URL}/ivi-maven/com/tomtom/ivi/platform/gradle/api-reference-docs/{gradleplugins_version}/api-reference-docs-{gradleplugins_version}.tar.gz"
    comms_artifactory_url = f"{ARTIFACTORY_BASE_URL}/ivi-maven/com/tomtom/ivi/sdk/communications/api-reference-docs/{comms_version}/api-reference-docs-{comms_version}.tar.gz"
    android_tools_artifactory_url = f"{ARTIFACTORY_BASE_URL}/nav-maven-release/com/tomtom/tools/android/api-reference-docs/{android_tools_version}/api-reference-docs-{android_tools_version}.tar.gz"

    # The directories where the downloaded API References will be saved.
    platform_download_dir = f"{DOWNLOAD_DIR}/platform_{platform_version}"
    gradleplugins_download_dir = f"{DOWNLOAD_DIR}/gradleplugins_{gradleplugins_version}"
    comms_download_dir = f"{DOWNLOAD_DIR}/comms_{comms_version}"
    android_tools_download_dir = f"{DOWNLOAD_DIR}/android_tools_{android_tools_version}"

    validate_placeholders(target_dir)

    # Clean previous API Reference downloads.
    if os.path.exists(DOWNLOAD_DIR):
        shutil.rmtree(DOWNLOAD_DIR)

    # Download API References stored on Artifactory.
    download_api_ref(platform_artifactory_url, platform_download_dir)
    download_api_ref(gradleplugins_artifactory_url, gradleplugins_download_dir)
    download_api_ref(comms_artifactory_url, comms_download_dir)
    download_api_ref(android_tools_artifactory_url, android_tools_download_dir)

    # Create look-up maps by indexing API References.
    platform_map = create_index(platform_download_dir)
    gradleplugins_map = create_index(gradleplugins_download_dir)
    comms_map = create_index(comms_download_dir)
    android_tools_map = create_index(android_tools_download_dir)

    errors = []
    for path in Path(target_dir).rglob(TARGET_FILETYPE):
        with open(path, 'r+', encoding="utf-8") as file:
            content = file.read()

            # Replace TTIVI_ placeholders in documentation.
            for match in re.findall(REGEX_PLATFORM_PLACEHOLDER, content):
                content = content.replace(PLATFORM_PLACEHOLDER, \
                    os.path.join(platform_base_url, url_lookup(platform_map, match, path, errors)), 1)
            for match in re.findall(REGEX_GRADLEPLUGINS_PLACEHOLDER, content):
                content = content.replace(GRADLEPLUGINS_PLACEHOLDER, \
                    os.path.join(gradleplugins_base_url, url_lookup(gradleplugins_map, match, path, errors)), 1)
            for match in re.findall(REGEX_COMMS_PLACEHOLDER, content):
                content = content.replace(COMMS_PLACEHOLDER, \
                    os.path.join(comms_base_url, url_lookup(comms_map, match, path, errors)), 1)
            for match in re.findall(REGEX_ANDROID_TOOLS_PLACEHOLDER, content):
                content = content.replace(ANDROID_TOOLS_PLACEHOLDER, \
                    os.path.join(android_tools_base_url, url_lookup(android_tools_map, match, path, errors)), 1)
            file.seek(0)
            file.write(content)
            file.truncate()

    if len(errors):
        print("Encountered {} error(s):\n{}".format(len(errors), '\n'.join(errors)))
        raise KeyError("API link(s) could not be generated. Make sure the API symbols match the API Reference versions specified in libraries.versions.toml")
