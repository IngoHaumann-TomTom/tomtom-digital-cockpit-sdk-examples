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
# It checks and generates URLs within the Developer Portal pages to ease maintenance
# of the Developer Portal content.
#
# Guidelines on writing documentation for the Developer Portal pages:
# https://confluence.tomtomgroup.com/display/SSAUTO/Writing+Documentation+for+the+Developer+Portal
#
# This script is intended to be called from Gradle tasks, not as a separate tool.
#
# Gradle task 'portal_export' calls the script as 'python3 -B <script-name> <target-dir> export'.
# The script generates API Reference URLs from placeholders and it validates URLs in the content.
# It connects to the S3 server to retrieve all available API Reference versions and adds those to
# the API Reference pages.
#
# Gradle task 'portal_check' calls the script as 'python3 -B <script-name> <target-dir>'.
# The script generates API Reference URLs from placeholders and it validates URLs in the content.
#
# There are multiple steps to this script:
#   - New Markdown files are created in <target-dir> from the Markdown files in the SOURCE_DIR
#     directory.
#   - API Reference placeholders get replaced by API Reference URLs in the TARGET_DIR files.
#   - The TARGET_DIR Markdown files get checked for broken links.
#
# An example of replacing an API Reference placeholder by an API Reference URL:
#   [OverlayPanel](TTIVI_INDIGO_API)
# becomes
#   [OverlayPanel](https://developer.tomtom.com/assets/downloads/indigo/indigo-api/latest/platform_frontend_api_common_frontend/com.tomtom.ivi.platform.frontend.api.common.frontend.panels/-overlay-panel/index.html)

import shutil
import os
import sys
from api_link_generator import api_link_generator
from url_validator import url_validator
from versions_generator import populate_versions

SOURCE_DIR = "src"
TARGET_FILETYPE = "*.md"
API_REFERENCE_DIR = "api-reference"
DOCUMENTATION_DIR = "documentation"
INPUT_CHECK_FILE = "documentation/development/frontend-plugins.md"

def parse_parameters():
    '''
    Parse command-line parameters and store relevant info.

    Returns
    -------
    target_dir : string
        The directory where the portal files must be generated.
    is_export : boolean
        Indicates whether the script is run with the optional argument "export"
    '''
    argc = len(sys.argv)
    assert (1 <= argc and argc <= 3), "Illegal number of parameters."

    target_dir = sys.argv[1]

    is_export = False
    if argc >= 3:
        assert (sys.argv[2] == "export"), "Unexpected 2nd parameter."
        is_export = True
    
    return target_dir, is_export

def verify_working_directory():
    '''Verifies whether script is run from correct working directory.'''
    if not os.path.exists(os.path.join(SOURCE_DIR, INPUT_CHECK_FILE)):
        raise EnvironmentError("Script must be run from the 'docs/portal' directory.")

def clean_old_files(target_dir):
    '''
    Clean old build files.

    Parameters
    -----------
    target_dir : str
        The directory to be cleaned.
    '''
    if os.path.exists(target_dir):
        shutil.rmtree(target_dir)

def create_intermediate_files(target_dir):
    '''
    Create intermediate files.

    Parameters
    -----------
    target_dir : str
        The directory in which to create the intermediate files.
    '''
    shutil.copytree(SOURCE_DIR, target_dir)

# Input validation and other preparation.
target_dir, is_export = parse_parameters()
verify_working_directory()
clean_old_files(target_dir)

# Generate and verify the portal content.
create_intermediate_files(target_dir)
api_link_generator(target_dir)
if is_export:
    populate_versions(os.path.join(target_dir, API_REFERENCE_DIR))
url_validator(os.path.join(target_dir, DOCUMENTATION_DIR))
