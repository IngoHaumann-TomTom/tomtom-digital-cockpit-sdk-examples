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
# Gradle task 'portal_export' calls the script as 'python3 -B <script-name> export'.
# The script generates API Reference URLs from placeholders and it validates URLs in the content.
# It connects to the S3 server to retrieve all available API Reference versions and adds those to
# the API Reference pages. It then converts the generated Markdown files to HTML, using Jekyll.
#
# Gradle task 'portal_check' calls the script as 'python3 -B <script-name>'.
# The script generates API Reference URLs from placeholders and it validates URLs in the content.
#
# There are multiple steps to this script:
#   - New Markdown files are created in TARGET_DIR from the Markdown files in the SOURCE_DIR directory.
#   - API Reference placeholders get replaced by API Reference URLs in the TARGET_DIR files.
#   - The TARGET_DIR Markdown files get checked for broken links.
#   - Optional: the TARGET_DIR Markdown files get converted to HTML, using Jekyll.
#
# An example of replacing an API Reference placeholder by an API Reference URL:
#   [OverlayPanel](TTIVI_INDIGO_API)
# becomes
#   [OverlayPanel](https://developer.tomtom.com/assets/downloads/indigo/indigo-api/latest/api_framework_frontend/com.tomtom.ivi.api.framework.frontend.panels/-overlay-panel/index.html)

import shutil
import os
import sys
from api_link_generator import api_link_generator
from url_validator import url_validator
from versions_generator import populate_versions

SOURCE_DIR = "src"
TARGET_DIR = "build/intermediate"
API_REFERENCE_DIR = "api-reference"
INPUT_CHECK_FILE = "documentation/3. development/index.md"

def verify_working_directory():
    '''Verifies whether script is run from correct working directory.'''
    if not os.path.exists(os.path.join(SOURCE_DIR, INPUT_CHECK_FILE)):
        raise EnvironmentError("Script was run from incorrect working directory.")

def is_export():
    '''
    Returns a boolean to indicate whether this script is run with the optional argument "export".
    '''
    return len(sys.argv) == 2 and sys.argv[1] == "export"

def clean_old_files():
    '''Clean old build files.'''
    if is_export():
        os.system("jekyll clean")
    if os.path.exists(TARGET_DIR):
        shutil.rmtree(TARGET_DIR)

def create_intermediate_files():
    '''Create intermediate files.'''
    shutil.copytree(SOURCE_DIR, TARGET_DIR)

def build_jekyll():
    '''Convert Developer Portal files.'''
    ret = os.system("jekyll build")
    if not ret == 0:
        raise RuntimeError("Jekyll build failed.")

verify_working_directory()
clean_old_files()
create_intermediate_files()
api_link_generator(TARGET_DIR)
if is_export():
    populate_versions(os.path.join(TARGET_DIR, API_REFERENCE_DIR))
url_validator(TARGET_DIR)
if is_export():
    build_jekyll()



