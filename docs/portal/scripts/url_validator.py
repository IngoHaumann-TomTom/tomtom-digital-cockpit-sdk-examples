#!/usr/bin/python3

# Copyright © 2020 TomTom NV. All rights reserved.
#
# This software is the proprietary copyright of TomTom NV and its subsidiaries and may be
# used for internal evaluation purposes or commercial use strictly subject to separate
# license agreement between you and TomTom NV. If you are the licensee, you are only permitted
# to use this software in accordance with the terms of your license agreement. If you are
# not the licensee, you are not authorized to use this software in any manner and should
# immediately return or destroy it.

# This script validates internal URLs (checked against pages on the live Portal) and external URLs 
# that are included in our Developer Portal Markdown content. 
# 
# External URLs that return a invalid HTTP response status will raise an Exception and will fail 
# the script. Internal URLs that are invalid only produce warnings in the standard output. This 
# allows us to add links to pages that are yet to be published.

import os
import requests
import re
from pathlib import Path

TARGET_FILETYPE = "*.md"

# Developer Portal base URL.
PORTAL_BASE_URL = "https://developer.tomtom.com"

# Regex pattern to retrieve URLs.
REGEX_EXTERNAL_URL = "(?<=\()http[^)]+(?=\))"
REGEX_INTERNAL_URL = "(?<=\()/indigo/.*?(?=\))"

# Regex pattern to retrieve code-blocks.
REGEX_CODE = "```(.*?)```"

# Error placeholder for HTTP Requests.
HTTP_ERROR = 1000

def is_url_available(url):
    '''
    Sends a HTTP request to URL and returns the HTTP response code.
    If HTTP request fails, HTTP_ERROR is returned.

    Parameters
    -----------
    url : str
        The URL to send a HTTP request.

    Returns
    -----------
    status : int
        The HTTP response code.
    '''
    try:
        status = requests.head(url).status_code
    except:
        status = HTTP_ERROR
    return status

def check_external_url(content, errors, path):
    '''
    Checks the external URLs found in the Markdown file in 'path' by sending HTTP requests
    and checking the HTTP status code returned.

    Parameters
    -----------
    content : str
        The file's content.
    errors : list
        A list containing error messages for logging purposes. Any new error(s) will be appended to this list.
    path : str
        The path of the file being checked. For logging purposes.
    '''
    for external_url in re.findall(REGEX_EXTERNAL_URL, content):
        status = is_url_available(external_url)

        # Check for client and server error responses.
        if status >= 400 or status == 204:
            errors.append(f"{external_url} in {path}")

def check_internal_url(content, warnings, path):
    '''
    Checks the internal URLs found in the Markdown file in 'path' by sending HTTP requests
    and checking the HTTP status code returned.

    Parameters
    -----------
    content : str
        The file's content.
    warnings : list
        A list containing warning messages for logging purposes. Any new warning(s) will be appended to this list.
    path : str
        The path of the file being checked. For logging purposes.
    '''
    for match in re.findall(REGEX_INTERNAL_URL, content):
        internal_url = os.path.join(PORTAL_BASE_URL, match[1:])
        status = is_url_available(internal_url)

        # Check for client and server error responses.
        if status > 200:
            warnings.append(f"{internal_url} in {path}")

def url_validator(target_dir):
    '''
    Validates the URLs found in Markdown files within a target directory by sending HTTP requests
    and checking the HTTP status code returned.

    Parameters
    -----------
    target_dir : str
        The files within this directory will be checked for URL validity.

    '''
    errors = []
    warnings = []
    for path in Path(target_dir).rglob(TARGET_FILETYPE):
        with open(path, 'r+', encoding="utf-8") as file:
            content = file.read()

            # Exclude code blocks from checked URLs.
            content = re.sub(REGEX_CODE, "", content, flags=re.DOTALL)
            
            check_external_url(content, errors, path)
            check_internal_url(content, warnings, path)

    if len(warnings):
        print("Encountered {} broken internal URL(s):\n{}".format(len(warnings), '\n'.join(warnings)))

    if len(errors):
        raise ConnectionError("Encountered {} broken external URL(s):\n{}".format(len(errors), '\n'.join(errors)))