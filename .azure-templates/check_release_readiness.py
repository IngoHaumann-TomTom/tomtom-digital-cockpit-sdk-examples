import argparse
import requests
import json
import os

is_running_on_ci = os.getenv("TF_BUILD")
release_readiness_base_url = "https://ivi-release-readiness.prod.devsup.az.tt3.com"

def get_latest_released_version():
    session = requests.Session()
    response = session.get(
        url=f"{release_readiness_base_url}/sdk/released",
        timeout=60,
    )

    print_response(response)
    if not response.ok:
        raise Exception("Could not get latest released version.")

    response_body = response.json()
    set_ci_variable("LatestReleasedIviVersion", response_body.get("latest_ivi"))

def check_release_readiness(examples_version):
    session = requests.Session()
    response = session.get(
        url=f"{release_readiness_base_url}/sdk/{examples_version}",
        timeout=60,
    )

    print_response(response)

    if not response.ok:
        set_ci_variable("ReleaseCandidateFound", False)
        return

    response_body = response.json()
    sdk_status = response_body["status"]

    if not sdk_status == "READY_FOR_RELEASE":
        set_ci_variable("ReleaseCandidateFound", False)
        return

    versions = get_product_versions(response_body)
    for name, version in versions.items():
        set_ci_variable(f"ReleaseCandidate{name.capitalize()}Version", version)

    set_ci_variable("ReleaseCandidateFound", True)


def get_product_versions(response_body):
    components = response_body.get("components", [])
    versions = {component["product"]: component["version"] for component in components}
    versions["sdk"] = response_body["version"]
    return versions


def set_ci_variable(name, value):
    print(f"{name}={value}")
    if is_running_on_ci:
        print(f"##vso[task.setvariable variable={name};isOutput=true]{value}")


def print_response(response):
    message = (
        json.dumps(response.json(), indent=2)
        if response.headers.get("Content-type") == "application/json"
        else response.text
    )
    print(message)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--examples_version",
        dest="examples_version",
        required=True,
        help="Version of Examples app to check. Can be set to 'latest'.",
    )
    parser.add_argument(
        "--debug",
        dest="debug",
        action="store_true",
        help="[Optional] For testing purposes."
    )

    args = parser.parse_args()
    if args.examples_version[-1] == "!":
        message = "Also including succeeded with issues builds."
        print(f"##vso[task.logissue type=warning;]{message}")

    if args.debug:
        debug_set_vars()
    else:
        print_service_version()
        check_release_readiness(args.examples_version)
        get_latest_released_version()

def debug_set_vars():
    set_ci_variable("ReleaseCandidateFound", True)
    set_ci_variable(f"ReleaseCandidateIviVersion", "1.0.4522")
    set_ci_variable(f"ReleaseCandidateExamplesVersion", "1.0.2220")
    set_ci_variable(f"ReleaseCandidateSdkVersion", "1.0.4522-2220")
    set_ci_variable("LatestReleasedIviVersion", "1.0.4520")

def print_service_version():
    session = requests.Session()
    response = session.get(
        url=f"{release_readiness_base_url}/version",
        timeout=60,
    )

    print_response(response)


if __name__ == "__main__":
    main()
