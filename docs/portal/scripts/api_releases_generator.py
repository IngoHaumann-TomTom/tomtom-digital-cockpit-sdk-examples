#!/usr/bin/python3

# Copyright © 2020 TomTom NV. All rights reserved.
#
# This software is the proprietary copyright of TomTom NV and its subsidiaries and may be
# used for internal evaluation purposes or commercial use strictly subject to separate
# license agreement between you and TomTom NV. If you are the licensee, you are only permitted
# to use this software in accordance with the terms of your license agreement. If you are
# not the licensee, you are not authorized to use this software in any manner and should
# immediately return or destroy it.

import json
import os
import datetime
from urllib.request import urlopen
from collections import deque
from enum import Enum

# Paths to Markdown files to generate.
API_REFERENCE_FILE = "api-reference/api-reference.mdx"
RECENT_RELEASES_FILE = "releases/releases.mdx"
OLDER_RELEASES_FILE = "releases/older-releases.mdx"

# Base URLs for the API References hosted on new Digital Cockpit S3 bucket.
S3_BASE_URL = "https://developer.tomtom.com/assets/downloads/tomtom-digital-cockpit"
PLATFORM_BASE_URL = f"{S3_BASE_URL}/platform-api"
GRADLEPLUGINS_BASE_URL = f"{S3_BASE_URL}/gradleplugins-api"
COMMS_BASE_URL = f"{S3_BASE_URL}/comms-sdk-api"
ANDROID_TOOLS_BASE_URL = f"{S3_BASE_URL}/tomtom-android-tools-api"

# JSON library fields.
PLATFORM_JSON = "indigoPlatform"
GRADLEPLUGINS_JSON = PLATFORM_JSON
COMMS_JSON = "iviCommunicationsSdk"
ANDROID_TOOLS_JSON = "tomtomAndroidTools"

# Example App Sources version first introduced on GitHub
GITHUB_INTRODUCTION_VERSION = 2049

# API References for later versions are pushed to Digital Cockpit S3 bucket. 
DIGITAL_COCKPIT_S3_INTRODUCTION = 2049

# Internal Artifactory URL to releases.json file.
RELEASES_JSON_URL = "https://artifactory.navkit-pipeline.tt3.com/artifactory/ivi-maven/com/tomtom/ivi/releases-data/tomtom-indigo-sdk/releases.json"

# Base URL for GitHub releases.
RELEASES_GITHUB_BASE_URL = "https://github.com/tomtom-international/tomtom-digital-cockpit-sdk-examples/tree"

# Placeholder to be replaced by a list of paired API References.
API_PLACEHOLDER = "TTIVI_API_ANCHOR"

# Placeholder to be replaced by a list of releases.
RECENT_RELEASES_PLACEHOLDER = "TTIVI_RECENT_RELEASES_ANCHOR"
OLDER_RELEASES_PLACEHOLDER = "TTIVI_OLDER_RELEASES_ANCHOR"

RECENT_AMOUNT = 12

class Accordion_style(Enum):
    FIRST_OPEN = 1
    ALL_OPEN = 2
    ALL_CLOSED = 3

def get_releases_dict():
    '''
    Retrieves the 'releases.json' file from TomTom's internal Artifactory and parses the content.
    Throws a ConnectionError when the file cannot be retrieved.

    Returns
    -------
    releases_dict : dict
        A dictionary with release versions as keys, and release data as values.
    '''
    releases_json = json.loads(urlopen(RELEASES_JSON_URL).read())
    if not releases_json:
        raise ConnectionError(f"Releases data cannot be retrieved from {RELEASES_JSON_URL}.")

    releases = [release for release in releases_json["releases"]]
    releases_dict = {release:releases_json["releases"][release] for release in releases}
    return releases_dict

def get_date(date):
    '''
    Parses the 'date' value from the releases JSON file and returns it as human-readable string.

    Parameters
    -----------
    date : str
        The date string retrieved from the releases JSON file.

    Returns
    -------
    str
        Release date in human-readable format.
    '''
    date_obj = datetime.datetime.strptime(date, '%m/%d/%Y, %H:%M:%S')
    return date_obj.strftime("%d %b %Y, %H:%M")

def get_opened(is_open):
    '''
    Returns "isOpened" if 'is_open' is True, otherwise returns an empty string.

    Parameters
    -----------
    is_open : bool
        Boolean that indicates whether Accordion component needs to be set to 'isOpened'.

    Returns
    -------
    str
        Returns 'isOpened' or an empty string.
    '''
    return "isOpened" if is_open else ""

def get_api_link(api_version, library_url, library_name):
    '''
    Generates an HTML link to an API Reference specified by 'api_version', 'library_url', and
    'library_name'.

    Parameters
    -----------
    api_version : str
        The version number of an API Reference.
    library_url : str
        The base URL of an API Reference hosted on the IndiGO S3 bucket.
    library_name : str
        The string describing the library, will be visible on the Accordion component.

    Returns
    -------
    str
        An HTML '<a href=>' link to an API Reference.
    '''
    return f"\n<a href=\"{library_url}/{api_version}/index.html\">"\
        f"{library_name} - version {api_version}</a>"

def get_release_link(release_version):
    '''
    Generates an HTML link to an SDK release on GitHub specified by 'release_version'.

    Parameters
    -----------
    release_version : str
        The version number of an SDK Release.

    Returns
    -------
    str
        An HTML link to an SDK release on GitHub.
    '''

    # Retrieve Example App Sources version number.
    example_app_sources_version = int(release_version[-4:])

    # Return empty string if release version is not available on GitHub.
    if example_app_sources_version < GITHUB_INTRODUCTION_VERSION:
        return ""
    
    return f"\n<a href=\"{RELEASES_GITHUB_BASE_URL}/{release_version}\">"\
            f"TomTom Digital Cockpit SDK - version {release_version}</a>\n"

def get_release_notes(tickets):
    '''
    Generates an HTML list of release notes for a single SDK release.

    Parameters
    -----------
    tickets : dict
        The 'tickets' field from the releases JSON file for a single SDK release.

    Returns
    -------
    str
        An HTML list of release notes for a single SDK release.
    '''
    release_notes = []

    release_notes.append("<div><ul>")
    for ticket in list(tickets.keys()):
        release_notes.extend(["<li>", "<br/>".join(tickets[ticket]['release notes']), "</li>"])
    release_notes.append("</ul></div>")
    return "\n".join(release_notes)

def construct_api(releases_dict, release_version, is_open):
    '''
    Generates a single custom Accordion element (for a single SDK release) for the API Reference
    section.

    Parameters
    -----------
    releases_dict : dict
        A dictionary with release versions as keys, and release data as values.
    release_version : str
        The version number of an SDK Release.
    is_open : bool
        Boolean that indicates whether Accordion component needs to be set to 'isOpened'.

    Returns
    -------
    accordion : str
        An HTML '<Accordion>' element of a single SDK release with linked API References.
    '''
    platform_version = releases_dict[release_version]['versions'][PLATFORM_JSON]
    gradleplugins_version = releases_dict[release_version]['versions'][GRADLEPLUGINS_JSON]
    comms_version = releases_dict[release_version]['versions'][COMMS_JSON]
    android_tools_version = releases_dict[release_version]['versions'][ANDROID_TOOLS_JSON]
    date = releases_dict[release_version]['date']

    accordion = f"<Accordion label=\"Release {release_version} - {get_date(date)}\" {get_opened(is_open)}>"\
        f"{get_api_link(platform_version, PLATFORM_BASE_URL, 'TomTom Digital Cockpit platform')}"\
        f"{get_api_link(gradleplugins_version, GRADLEPLUGINS_BASE_URL, 'TomTom Digital Cockpit Gradle plugins')}"\
        f"{get_api_link(comms_version, COMMS_BASE_URL, 'TomTom Digital Cockpit Comms SDK')}"\
        f"{get_api_link(android_tools_version, ANDROID_TOOLS_BASE_URL, 'TomTom Android Tools')}"\
        "\n</Accordion>\n"
    return accordion    

def construct_release(releases_dict, release_version, is_open):
    '''
    Generates a single custom Accordion element (for a single SDK release) for the Releases section.

    Parameters
    -----------
    releases_dict : dict
        A dictionary with release versions as keys, and release data as values.
    release_version : str
        The version number of an SDK Release.
    is_open : bool
        Boolean that indicates whether Accordion component needs to be set to 'isOpened'.

    Returns
    -------
    accordion : str
        An HTML '<Accordion>' element of a single SDK release with links to the SDK release tarball
        on Nexus and release notes.
    '''
    date = releases_dict[release_version]['date']
    tickets = releases_dict[release_version]['tickets']

    accordion = f"<Accordion label=\"Release {release_version} - {get_date(date)}\" {get_opened(is_open)}>"\
        f"{get_release_link(release_version)}"\
        f"<b>Release notes</b>\n"\
        f"{get_release_notes(tickets)}"\
        "\n\n</Accordion>\n"
    return accordion

def get_accordions(construct_function, releases_dict, accordion_style):
    '''
    Higher-order function to generate the full list of SDK releases.

    Parameters
    -----------
    construct_function : function
        The construct_function passed to this higher-order function will be used to construct the
        individual Accordion components.
    releases_dict : dict
        A dictionary with release versions as keys, and release data as values.
    accordion_style : Accordion_style
        Enum specifying which accordion entries should be open and which ones closed.

    Returns
    -------
    str
        A string containing a list of HTML '<Accordion>' elements of all SDK releases.
    '''
    accordions = deque()

    if accordion_style == Accordion_style.ALL_OPEN:
        for release in releases_dict:
            # Append left to accordions container so the order becomes chronologic.
            accordions.appendleft(construct_function(releases_dict, release, is_open=True))
    else:
        if accordion_style == Accordion_style.ALL_CLOSED:
            for release in releases_dict:
                # Append left to accordions container so the order becomes chronologic.
                accordions.appendleft(construct_function(releases_dict, release, is_open=False))
        else:
            for release in releases_dict:
                # Append left to accordions container so the order becomes chronologic.
                accordions.appendleft(construct_function(releases_dict, release, is_open=False))
            # set 'isOpened' on current Accordion item.
            accordions.popleft()
            current_release = (list(releases_dict.keys())[-1])
            accordions.appendleft(construct_function(releases_dict, current_release, is_open=True))

    return "\n".join(accordions)

def generate_file(file_path, placeholder, construct_function, releases_dict, accordion_style):
    '''
    Transforms the content of an intermediate file specified by 'file_path', replacing 'placeholder'
    by the full list of SDK releases (as Accordion components).

    Parameters
    -----------
    file_path : str
        Path to intermediate file that will be edited.
    placeholder : str
        Placeholder that will be used as anchor to place new content.
    construct_function : function
        The function that will be used to construct the individual Accordion components.
    releases_dict : dict
        A dictionary with release versions as keys, and release data as values.
    accordion_style : Accordion_style
        Enum specifying which accordion entries should be open and which ones closed.
    '''
    with open(file_path, 'r+', encoding="utf-8") as file:
        content = file.read()
        content = content.replace(placeholder, \
            get_accordions(construct_function, releases_dict, accordion_style))
        file.seek(0)
        file.write(content)
        file.truncate()

def split_dict_releases(releases_dict):
    '''
    Splits 'releases_dict' into 'recent_releases_dict' which holds the 12 most recent releases and
    'older_releases_dict' which holds all other releases.

    Parameters
    -----------
    releases_dict : dict
        A dictionary with release versions as keys, and release data as values.
    '''
    recent_releases_dict = dict(list(releases_dict.items())[(len(releases_dict)-RECENT_AMOUNT):])
    older_releases_dict = dict(list(releases_dict.items())[:(len(releases_dict)-RECENT_AMOUNT):])

    return older_releases_dict, recent_releases_dict

def get_dict_api_references(releases_dict):
    '''
    Returns a dictionary which holds the releases for which the API
    References have been published to the Digital Cockpit S3 bucket.

    Parameters
    -----------
    releases_dict : dict
        A dictionary with release versions as keys, and release data as values.
    '''

    api_dict = {key:value for (key, value) in releases_dict.items() \
        if int(key[-4:]) >= DIGITAL_COCKPIT_S3_INTRODUCTION}

    return api_dict

def generate_api_releases_sections(target_dir):
    '''
    Generates the API Reference and Releases section for a Dev Portal export.

    Parameters
    -----------
    target_dir : str
        Path to directory containing the export files.
    '''
    releases_dict = get_releases_dict()
    older_releases_dict, recent_releases_dict = split_dict_releases(releases_dict)
    api_dict = get_dict_api_references(releases_dict)

    # Generate API Reference section.
    api_reference_path = os.path.join(target_dir, API_REFERENCE_FILE)
    generate_file(api_reference_path, API_PLACEHOLDER, \
        construct_api, api_dict, Accordion_style.FIRST_OPEN)
        
    # Generate recent Releases section.
    recent_releases_path = os.path.join(target_dir, RECENT_RELEASES_FILE)
    generate_file(recent_releases_path, RECENT_RELEASES_PLACEHOLDER, \
        construct_release, recent_releases_dict, Accordion_style.ALL_OPEN)

    # Generate older Releases section.
    older_releases_path = os.path.join(target_dir, OLDER_RELEASES_FILE)
    generate_file(older_releases_path, OLDER_RELEASES_PLACEHOLDER, \
        construct_release, older_releases_dict, Accordion_style.ALL_CLOSED)


