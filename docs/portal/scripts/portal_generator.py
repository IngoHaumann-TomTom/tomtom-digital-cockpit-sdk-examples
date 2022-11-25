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
# Gradle task 'portal_export' calls the script as 'python3 -B <script-name> <indigo-version> 
# <indigo-comms-version> <android-tools-version> <target-dir> export'.
# The script generates API Reference URLs from placeholders and it validates URLs in the content.
# It connects to the S3 server to retrieve all available API Reference versions and adds those to
# the API Reference pages.
#
# Gradle task 'portal_check' calls the script as 'python3 -B <script-name> <indigo-version> 
# <indigo-comms-version> <android-tools-version> <target-dir>'.
# The script generates API Reference URLs from placeholders and it validates URLs in the content.
#
# There are multiple steps to this script:
#   - New Markdown files are created in <target-dir> from the Markdown files in the SOURCE_DIR
#     directory.
#   - API Reference placeholders get replaced by API Reference URLs in the <target_dir> files.
#   - The <target_dir> Markdown files get checked for broken links.
#
# An example of replacing an API Reference placeholder by an API Reference URL:
#   [OverlayPanel](TTIVI_INDIGO_API)
# becomes
#   [OverlayPanel](https://developer.tomtom.com/assets/downloads/tomtom-indigo/tomtom-indigo-api/x.y.z/platform_frontend_api_common_frontend/com.tomtom.ivi.platform.frontend.api.common.frontend.panels/-overlay-panel/index.html)


import shutil
import os
import sys
from api_link_generator import generate_api_links
from url_validator import validate_urls
from api_releases_generator import generate_api_releases_sections

SOURCE_DIR = "src"
TARGET_FILETYPE = "*.mdx"
DOCUMENTATION_DIR = "documentation"
INPUT_CHECK_FILE = "documentation/development/frontend-plugins.mdx"

def parse_parameters():
    '''
    Parse command-line parameters and store relevant info.

    Returns
    -------
    versions : list
        [0] IndiGO version.
        [1] IndiGO Comms SDK version.
        [2] TomTom Android Tools version.
    target_dir : string
        The directory where the portal files must be generated.
    is_export : boolean
        Indicates whether the script is run with the optional argument "export"
    '''
    argc = len(sys.argv)
    assert (argc >= 5 and argc <= 6), "Invalid number of parameters."

    versions = []
    versions.append(sys.argv[1])
    versions.append(sys.argv[2])
    versions.append(sys.argv[3])

    target_dir = sys.argv[4]

    is_export = False
    if argc == 6:
        assert (sys.argv[5] == "export"), "Unexpected 5th parameter."
        is_export = True

    # TODO(IVI-6612) Create PortalConfig object
    return versions, target_dir, is_export

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
# TODO(IVI-6612) Create PortalConfig object
versions, target_dir, is_export = parse_parameters()
verify_working_directory()
clean_old_files(target_dir)

# Generate and verify the portal content.
create_intermediate_files(target_dir)
generate_api_links(target_dir, versions)
generate_api_releases_sections(target_dir)
validate_urls(target_dir, is_export)

