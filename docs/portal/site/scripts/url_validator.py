#!/usr/bin/python3

# Copyright © 2020 TomTom NV. All rights reserved.
#
# This software is the proprietary copyright of TomTom NV and its subsidiaries and may be
# used for internal evaluation purposes or commercial use strictly subject to separate
# license agreement between you and TomTom NV. If you are the licensee, you are only permitted
# to use this software in accordance with the terms of your license agreement. If you are
# not the licensee, you are not authorized to use this software in any manner and should
# immediately return or destroy it.

import requests
import re
from pathlib import Path

TARGET_FILETYPE = "*.md"

# Regex pattern to retrieve URLs.
REGEX_URL = "(?<=\()http[^)]+(?=\))"

# Regex pattern to retrieve code-blocks.
REGEX_CODE = "```(.*?)```"

# Error placeholder for HTTP Requests.
HTTP_ERROR = 1000

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
    for path in Path(target_dir).rglob(TARGET_FILETYPE):
        with open(path, 'r+', encoding="utf-8") as file:
            content = file.read()
            # Exclude code blocks from checked URLs.
            content = re.sub(REGEX_CODE, "", content, flags=re.DOTALL)
            for match in re.findall(REGEX_URL, content):
                try:
                    status = requests.head(match).status_code
                except:
                    status = HTTP_ERROR
                finally:
                    # Check for client and server error responses.
                    if status >= 400 or status == 204:
                        errors.append(f"{match} in {path}")
    if len(errors):
        raise ConnectionError("Encountered {} error(s):\n{}".format(len(errors), '\n'.join(errors)))
