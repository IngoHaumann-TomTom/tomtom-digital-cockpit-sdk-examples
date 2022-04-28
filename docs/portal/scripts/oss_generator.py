#!/usr/bin/env python3

'''
This script takes a BlackDuck compliance report in JSON format and renders it in a human readable
format, like a list or a table in MarkDown.
'''

import os
import sys
import argparse
import json
from types import SimpleNamespace

class Writer:
    '''Writes lines of text.'''

    def __enter__(self):
        '''So we can use 'with' and close any resources properly.'''
        pass

    def __exit__(self, *args):
        '''So we can use 'with' and close any resource properly.'''
        pass

    def write(self, line):
        '''Write the given line to the output verbatim.'''
        print("ERROR: Do not use abstract base class to produce output.")
        exit(1)

    def newline(self):
        '''Write a newline character.'''
        self.write("\n")

    def writeln(self, line):
        '''Write the given line, followed by a newline, to the output.'''
        self.write(line)
        self.newline()

class StdoutWriter(Writer):
    '''Writes the output to stdout of this script.'''

    def write(self, line):
        print(line, end = '')

class FileWriter(Writer):
    '''Write the output to a given file. The contents will be overwritten.'''

    def __init__(self, path):
        self.path = path

    def __enter__(self):
        self.file = open(self.path, "w")

    def __exit__(self, *args):
        self.file.flush()
        self.file.close()

    def write(self, line):
        self.file.write(line)

class OutputData:
    '''Contains all the data that can be used to create the output.'''

    def __init__(self, input):
        self.metadata = input.metadata
        self.project = input.project
        self.version = self.project.version
        self.title = self.project.name
        
class Formatter:
    '''Output formatter for the license information.'''

    def __init__(self, writer):
        self.writer = writer
        
    def begin(self, project, metadata, version=None):
        '''Called once, to render a document header.'''
        pass

    def begin(self, data):
        '''Called once, to render a document header.'''
        pass

    def write(self, component):
        '''Called for each component with all its known versions.'''
        print("ERROR: Do not use abstract base class to produce output.")
        exit(1)

    def end(self):
        '''Called once, to render a document footer.'''
        pass

    def headers(self, metadata, joinstring="\n"):
        '''Returns all headers in the metadata as a single string.'''
        return joinstring.join(metadata.headers)

class MarkDownFormatter(Formatter):
    def __init__(self, writer):
        super().__init__(writer)

    def begin(self, data):
        self.writer.writeln(f"# OSS report for TomTom _{data.title}_ version {data.version}\n")
        self.writer.writeln(f"{self.headers(data.metadata)}\n")
        if hasattr(data.metadata, 'disclaimer'):
            self.writer.writeln(f"> _{data.metadata.disclaimer}_\n")

class SimpleMarkDownFormatter(MarkDownFormatter):
    '''A simple line by line MarkDown output formatter.'''

    def __init__(self, writer):
        super().__init__(writer)

    def write(self, component):
        is_plural = len(component.versions) > 1
        self.writer.writeln(f"\"[{component.name}]({component.url})\" "
            + f"version{'s' if is_plural else ''} "
            + f"{', '.join(component.versions)} {'are' if is_plural else 'is'} "
            + f"licensed under {component.license_name}\n")

class TableMarkDownFormatter(MarkDownFormatter):
    '''
        A MarkDown table output formatter. It has three columns:
        component name, version, and license.
    '''

    def __init__(self, writer):
        super().__init__(writer)

    def begin(self, data):
        super().begin(data)
        self.writer.writeln('| Component | Version | License |')
        self.writer.writeln('| :-------- | :------ | :------ |')

    def write(self, component):
        self.writer.writeln(f"| [{component.name}]({component.url}) "
            + f"| {', '.join(component.versions)}"
            + f" | {component.license_name} |")

class FullMarkDownFormatter(MarkDownFormatter):
    '''A full MarkDown report output formatter. This is the official user facing report.'''

    def __init__(self, writer):
        super().__init__(writer)

    def begin(self, data):
        super().begin(data)

    def write(self, component):
        self.writer.writeln(f"## {component.name}\n")
        self.writer.writeln(f"```plaintext")
        self.writer.writeln(f"Project version: {', '.join(component.versions)}")
        self.writer.writeln(f"Project Home Page: {component.url}\n")
        if not component.licenses_conjunctive:
            self.writer.writeln(f"Licensed under one of the licenses below.\n")
        for license in component.licenses.values():
            self.writer.writeln(f"{license.text}\n")
        self.writer.writeln(f"```\n")

class CsvFormatter(Formatter):
    '''
        A Comma Separated Values (CSV) output formatter.
        It has four columns: component name, version, license, and URL.
    '''

    def __init__(self, writer):
        super().__init__(writer)

    def begin(self, _data):
        self.writer.writeln('"Component", "Version", "License", "URL"')
    
    def write(self, component):
        self.writer.writeln(f"\"{self.escape(component.name)}\", "
            + f"\"{self.escape(', '.join(component.versions))}\", "
            + f"\"{self.escape(component.license_name)}\", \"{self.escape(component.url)}\"")

    def escape(self, string):
        return string.replace('"', '\\"')

class HtmlFormatter(Formatter):
    '''
        An HTML table output formatter.
    '''

    def __init__(self, writer):
        super().__init__(writer)

    def begin(self, data):
        header_divider = "</p><p>"
        title = f"OSS report for TomTom {data.title} version {data.version}"
        self.writer.writeln("<!DOCTYPE html>\n<html lang=\"en\">"
            "<head><meta charset=\"utf-8\"/>"
            f"<title>{title}</title>"
            "<style>"
                "table th { text-align: left; }"
                "table { margin: 1em; border-collapse: collapse; width: 95%; }"
                "th { padding: 6pt; }"
                "td { padding: 6pt; }"
            "</style>"
            "</head>"
            "<body>"
            f"<h1>{title}</h1>"
            f"<p>{self.headers(data.metadata, header_divider)}</p>")
        if hasattr(data.metadata, 'disclaimer'):
            self.writer.writeln(f"<p><em>{data.metadata.disclaimer}</em></p>")
        self.writer.writeln("<table border=\"1px\"><thead>"
            "<tr><th>Component</th><th>Version</th><th>License</th></tr>"
            "</thead><tbody>")
    
    def write(self, component):
        self.writer.writeln(f"<tr><td><a href=\"{component.url}\">{component.name}</a></td>"
            f"<td>{', '.join(component.versions)}</td>"
            f"<td>{component.license_name}</td></tr>")

    def end(self):
        self.writer.writeln("</tbody></table></body></html>")

class Component:
    '''Simple container to collect all versions of a component.'''

    def __init__(self, json):
        self.name = json.name
        self.url = json.url
        self.license_name = json.license.display
        self.licenses = {}
        self.licenses_conjunctive = True
        self.versions = []

def create_components(data):
    '''Converts the given JSON data into a map of component names to their Component instance.'''

    # Map a map of license IDs to license information.
    licenses = {}
    for license in data.licenses:
        licenses[license.id] = license

    # Map component names to a map of version numbers to license ID's.
    components = {}
    for item in data.components:
        if item.name not in components:
            # Create a new component and add to the map.
            components[item.name] = Component(item)
        component = components[item.name]

        # Add missing licenses.
        if item.license.type == "LICENSE":
            # Single license.
            id = item.license.license_data_id
            component.licenses[id] = licenses[id]
        else:
            # Multiple licenses.
            if item.license.type == "DISJUNCTIVE":
                component.licenses_conjunctive = False
            for license in item.license.licenses:
                id = license.license_data_id
                component.licenses[id] = licenses[id]

        # Add potential new version.
        versions = components[component.name].versions
        if item.version not in versions:
            versions.append(item.version)

    return components

def write_output(formatter, data, components):
    '''Output the license information of each component.'''

    formatter.begin(data)

    for name, component in components.items():
        # Try to output in TASL format: Title, Author, Source, License.
        # Since we do not have the Author information, we'll have to leave that out. That
        # information is made available, however, by following the source URL.
        formatter.write(component)

    formatter.end()

def main(args):
    try:
        # Load the JSON data.
        content = open(args.blackduck_report).read()
    except FileNotFoundError as err:
        print(err)
        exit(1)
    input = json.loads(content, object_hook = lambda d: SimpleNamespace(**d))
    components = create_components(input)

    data = OutputData(input)
    if args.version:
        data.version = args.version
    if args.project_name:
        data.title = args.project_name

    version = args.version

    writer = FileWriter(args.output) if args.output else StdoutWriter()

    with writer:
        # Generate the requested output.
        if args.markdown_list:
            write_output(SimpleMarkDownFormatter(writer), data, components)
        if args.markdown_table:
            write_output(TableMarkDownFormatter(writer), data, components)
        if args.markdown_full:
            write_output(FullMarkDownFormatter(writer), data, components)
        if args.web:
            write_output(HtmlFormatter(writer), data, components)
        if args.csv:
            write_output(CsvFormatter(writer), data, components)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description = "Generate a license report based on the JSON file generated by BlackDuck.",
        usage = f"{os.path.basename(sys.argv[0])} [options] <blackduck_report>")
    parser.add_argument("blackduck_report", help="JSON file generated by BlackDuck.")
    parser.add_argument('-l', '--markdown-list', default = False, action = 'store_true',
        help = "Output a simple list in MarkDown format.")
    parser.add_argument('-t', '--markdown-table', default = False, action = 'store_true',
        help = "Output a table in MarkDown format.")
    parser.add_argument('-f', '--markdown-full', default = False, action = 'store_true',
        help = "Output the official full report in MarkDown format.")
    parser.add_argument('-w', '--web', default = False, action = 'store_true',
        help = "Output a table in HTML format.")
    parser.add_argument('-c', '--csv', default = False, action = 'store_true',
        help = "Output a table in Comma Separated Values (CSV) format. "
            + "Can also be imported into a spreadsheet.")
    parser.add_argument('-v', '--version', type = str, default = None,
        help = "Version to use in the output. "
            + "Defaults to the project version as stated in the JSON file.")
    parser.add_argument('-p', '--project-name', type = str, default = None,
        help = "Project name to use in the output. "
            + "Defaults to the project name as stated in the JSON file.")
    parser.add_argument('-o', '--output', type = str, default = None,
        help = "Output to the given file instead of stdout. Target will be overwritten.")
    if len(sys.argv) == 1:
        # No arguments given, so show usage information.
        parser.print_help()
        exit(1)
    args = parser.parse_args()
    main(args)
