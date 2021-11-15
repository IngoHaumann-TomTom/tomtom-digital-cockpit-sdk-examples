#!/usr/bin/python3

# Copyright © 2020 TomTom NV. All rights reserved.
#
# This software is the proprietary copyright of TomTom NV and its subsidiaries and may be
# used for internal evaluation purposes or commercial use strictly subject to separate
# license agreement between you and TomTom NV. If you are the licensee, you are only permitted
# to use this software in accordance with the terms of your license agreement. If you are
# not the licensee, you are not authorized to use this software in any manner and should
# immediately return or destroy it.

# This script populates the API Reference pages with the available, hosted versions of API
# References. It requires the AWS Command Line Interface (awscli) tool to be installed, and the
# credentials to the TomTom S3 bucket configured. This script is only called by the portal
# generator when creating an export to be uploaded to the Developer Portal.

import re
import os
from subprocess import PIPE, run

TARGET_FILETYPE = "*.md"

# API Reference URIs for the S3 server.
INDIGO_S3_URI = "s3://devportal-bucket/downloads/indigo/indigo-api/"
INDIGO_GRADLEPLUGINS_S3_URI = "s3://devportal-bucket/downloads/indigo/indigo-gradleplugins-api/"
INDIGO_COMMS_S3_URI = "s3://devportal-bucket/downloads/indigo/indigo-comms-api/"
ANDROID_TOOLS_S3_URI = "s3://devportal-bucket/downloads/indigo/android-tools-api/"

# Base urls for the API References hosted on S3.
INDIGO_BASE_URL = "https://developer.tomtom.com/assets/downloads/indigo/indigo-api"
INDIGO_GRADLEPLUGINS_BASE_URL = "https://developer.tomtom.com/assets/downloads/indigo/indigo-gradleplugins-api"
INDIGO_COMMS_BASE_URL = "https://developer.tomtom.com/assets/downloads/indigo/indigo-comms-api"
ANDROID_TOOLS_BASE_URL = "https://developer.tomtom.com/assets/downloads/indigo/android-tools-api"

# Markdown files that will get populated with the available API Reference versions.
INDIGO_FILE = "1. indigo-api-reference.md"
INDIGO_GRADLEPLUGINS_FILE = "2. indigo-gradle-plugins-api-reference.md"
INDIGO_COMMS_FILE = "3. indigo-comms-api-reference.md"
ANDROID_TOOLS_FILE = "4. tomtom-android-tools-api-reference.md"

REGEX_VERSION = "[0-9].[0-9].[0-9]{2,4}"

# Placeholders that will be replaced by API Reference links.
VERSIONS_LATEST_PLACEHOLDER = "TTIVI_API_LATEST"
# TODO(IVI-5672): Include older versions of the API References.
# VERSIONS_OLDER_PLACEHOLDER = "TTIVI_API_OLDER"

def get_index_url(base_url, version):
    '''
    Returns a joined string consisting of 'base_url', 'version' and "index.html".
    '''
    return os.path.join(base_url, version, "index.html")

def get_versions(s3_uri):
    '''
    Queries the IndiGO S3 bucket specified by 's3_uri' for its content, and then retrieves the
    available version numbers from those results. Returns the versions as a list.

    Returns
    -----------
    list
        Returns a list of API Reference versions.
        The list of versions is reverse-sorted on version number (newest to oldest).
    '''

    # Query the S3 server to retrieve directory contents from bucket.
    command = ['aws', 's3', 'ls', s3_uri]
    result = run(command, stdout=PIPE, universal_newlines=True)

    # Retrieve version numbers from the received content and sort them from newest to oldest.
    versions = []
    for match in re.findall(REGEX_VERSION, result.stdout):
        versions.append(match)
    # TODO(IVI-5700): Improve sorting algorithm when including older versions.
    #   Text based sorting will cause sorting errors when 1.0.10000 follows 1.0.9999.
    versions.sort(reverse=True)

    return versions

def populate_file(path, versions, base_url):
    '''
    Replaces placeholders within a file specified by 'path' with a list of links to available
    API Reference versions, specified by 'list'.

    Parameters
    -----------
    path : str
        Path to the file in which the placeholders will be replaced.
    versions : list
        List of version numbers, sorted from newest to oldest.
    base_url : str
        The base URL used in constructing links to the specified versions.
    '''
    latest = "- [{}]({})".format(versions[0], get_index_url(base_url, versions[0]))
    versions.pop(0)
    print(f"Populating {path} with API Reference versions.")

    # TODO(IVI-5672): Include older versions of the API References.
#     older = []
#     while versions:
#         older.append("- [{}]({})".format(versions[0], get_index_url(base_url, versions[0])))
#         versions.pop(0)

    with open(path, 'r+', encoding="utf-8") as file:
        content = file.read()
        content = content.replace(VERSIONS_LATEST_PLACEHOLDER, latest)
        # TODO(IVI-5672): Include older versions of the API References.
        # content = content.replace(VERSIONS_OLDER_PLACEHOLDER, "\n".join(older))
        file.seek(0)
        file.write(content)
        file.truncate()

def populate_versions(target_dir):
    '''
    Populates the API Reference pages within target_dir with the available API Reference versions
    that are hosted on the IndiGO S3 server.

    Parameters
    -----------
    target_dir : str
        The files within this directory will be populated with links to API Reference versions.

    '''
    populate_file(os.path.join(target_dir, INDIGO_FILE),
        get_versions(INDIGO_S3_URI), INDIGO_BASE_URL
    )
    populate_file(os.path.join(target_dir, INDIGO_GRADLEPLUGINS_FILE),
        get_versions(INDIGO_GRADLEPLUGINS_S3_URI), INDIGO_GRADLEPLUGINS_BASE_URL
    )
    populate_file(os.path.join(target_dir, INDIGO_COMMS_FILE),
        get_versions(INDIGO_COMMS_S3_URI), INDIGO_COMMS_BASE_URL
    )
    populate_file(os.path.join(target_dir, ANDROID_TOOLS_FILE),
        get_versions(ANDROID_TOOLS_S3_URI), ANDROID_TOOLS_BASE_URL
    )


