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
import time
from pathlib import Path

TARGET_FILETYPE = "*.md"

# Developer Portal base URL.
PORTAL_BASE_URL = "https://developer.tomtom.com"

# Regex pattern to retrieve external URLs.
REGEX_EXTERNAL_URL = "(?<=\()http[^)]+(?=\))"

# Regex pattern to retrieve internal Developer Portal URLs.
REGEX_INTERNAL_URL = "(?<=\()/tomtom-digital-cockpit/.*?(?=\))"
REGEX_INTERNAL_URL_NO_SLASH = "(?<=\()tomtom-digital-cockpit/.*?(?=\))"

# Regex pattern to retrieve API Reference URLs hosted on S3.
REGEX_S3_URL = "https://developer.tomtom.com/assets/.*?"

# Regex pattern to retrieve Digital Cockpit GitHub URLs.
REGEX_GITHUB_URL = "\w+://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/.*?"

# Regex pattern to retrieve restricted Nexus URLs.
REGEX_NEXUS_URL = "\w+://repo.tomtom.com/.*?"

# Regex pattern to retrieve code-blocks.
REGEX_CODE = "```(.*?)```"

def is_url_available(url, backoff_seconds = 10):
    '''
    Sends a HTTP request to URL and returns the HTTP response code.

    Parameters
    -----------
    url : str
        The URL to send a HTTP request.
    backoff_seconds : int
        The number of seconds to wait before retrying after receiving status 429 retry-after.

    Returns
    -----------
    status : int
        The HTTP response code.
    '''
    response = requests.head(url)
    status = response.status_code

    if status == 429:  # retry-after
        if "Retry-After" in response.headers:
            retry_after = int(response.headers["Retry-After"])
            sleep_seconds = max(retry_after, 10)
            sleep_seconds = min(sleep_seconds, 60)
            print(f"Status {status}: Retry-after {retry_after} sleep {sleep_seconds} before retrying {url}")
            time.sleep(sleep_seconds)
        else:
            print(f"Status {status}: Sleep {backoff_seconds} before retrying {url}")
            time.sleep(backoff_seconds)
        return is_url_available(url, min(2 * backoff_seconds, 60))

    return status

def check_external_url(content, warnings, errors, path, is_export):
    '''
    Checks the external URLs found in the Markdown file in 'path' by sending HTTP requests
    and checking the HTTP status code returned.

    Parameters
    -----------
    content : str
        The file's content.
    warnings : list
        A list containing warning messages for logging purposes. Any new warning(s) will be appended to this list.
    errors : list
        A list containing error messages for logging purposes. Any new error(s) will be appended to this list.
    path : str
        The path of the file being checked. For logging purposes.
    is_export : boolean
        Indicates whether the script is run with the optional argument "export".
    '''
    for line_number, line in enumerate(content.splitlines(), 1):
        for external_url in re.findall(REGEX_EXTERNAL_URL, line, re.IGNORECASE):

            # Skip validation of S3 URLs when script is not run as export.
            if not is_export and re.fullmatch(REGEX_S3_URL, external_url, re.IGNORECASE) != None:
                continue

            # Skip validation of TomTom Nexus URLs as access is restricted.
            if re.fullmatch(REGEX_NEXUS_URL, external_url, re.IGNORECASE) != None:
                continue

            status = is_url_available(external_url)

            # Check for client and server error responses.
            if status >= 400 or status == 204:
                if re.fullmatch(REGEX_GITHUB_URL, external_url, re.IGNORECASE) != None:
                    warnings.append(f"{external_url} in {path}:{line_number} (status {status})")
                else:
                    errors.append(f"{external_url} in {path}:{line_number} (status {status})")

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

    for line_number, line in enumerate(content.splitlines(), 1):
        for match in re.findall(REGEX_INTERNAL_URL, line, re.IGNORECASE):
            internal_url = os.path.join(PORTAL_BASE_URL, match[1:])
            status = is_url_available(internal_url)

            # Check for client and server error responses.
            if status > 200:
                warnings.append(f"{internal_url} in {path}:{line_number} (status {status})")

def validate_internal_url_syntax(target_dir):
    '''
    Checks whether a forward slash is included in internal URLs. Throws an Exception with a list of
    URLs with omitted forward slashes.

    Parameters
    -----------
    target_dir : str
        The files within this directory will be checked for URL syntax.

    '''
    errors = []
    for path in Path(target_dir).rglob(TARGET_FILETYPE):
        with open(path, 'r+', encoding="utf-8") as file:
            content = file.read()

            # Exclude code blocks from checked URLs.
            content = re.sub(REGEX_CODE, "", content, re.DOTALL, re.IGNORECASE)

            for line_number, line in enumerate(content.splitlines(), 1):
                for match in re.findall(REGEX_INTERNAL_URL_NO_SLASH, line, re.IGNORECASE):
                    errors.append(f"{match} in file {path}:{line_number}")

    if len(errors):
        raise SyntaxError("Encountered {} syntax error(s) in internal URLs:\n{}".format(len(errors), '\n'.join(errors)))

def validate_urls(target_dir, is_export):
    '''
    Validates the URLs found in Markdown files within a target directory by sending HTTP requests
    and checking the HTTP status code returned.

    Parameters
    -----------
    target_dir : str
        The files within this directory will be checked for URL validity.
    is_export : boolean
        Indicates whether the script is run with the optional argument "export".

    '''
    validate_internal_url_syntax(target_dir)

    errors = []
    warnings = []
    for path in Path(target_dir).rglob(TARGET_FILETYPE):
        with open(path, 'r+', encoding="utf-8") as file:
            content = file.read()

            # Exclude code blocks from checked URLs.
            content = re.sub(REGEX_CODE, "", content, re.DOTALL, re.IGNORECASE)

            check_external_url(content, warnings, errors, path, is_export)
            check_internal_url(content, warnings, path)

    if len(warnings):
        print("Warning: Encountered {} broken internal URL(s):\n{}".format(len(warnings), '\n'.join(warnings)))

    if len(errors):
        raise ConnectionError("Encountered {} broken external URL(s):\n{}".format(len(errors), '\n'.join(errors)))