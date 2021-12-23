---
title: Installing TomTom IndiGO SDK
---

Download the latest version of the TomTom IndiGO SDK from
[Nexus](https://repo.tomtom.com/#browse/browse:ivi:com%2Ftomtom%2Findigo%2Ftomtom-indigo-sdk).

- When you get an error `Path "browse/browse"  not found`, then click `Sign in` at the upper-right
  of the screen and supply the
  [repository credentials](/tomtom-indigo/documentation/getting-started/introduction)
  that you received from TomTom.

After logging in to Nexus you can find the SDK versions under `com/tomtom/indigo/tomtom-indigo-sdk`:

- Find the latest version of the `tomtom-indigo-sdk`.
- Click on the `+` icon left of this version to open its folder.
- Click on the `tomtom-indigo-sdk-<version>.tar.gz` file to see its details in the panel on the
  right.
- Click the link at `Path` in the panel on the right to start downloading the SDK.

Extract the downloaded file and you will find the example app source code inside it. Then follow the
instructions in the next steps to setup an environment in which to run TomTom IndiGO.

__Windows Notes:__

- Use [WinRAR](https://www.win-rar.com/predownload.html?&L=0) to extract the downloaded file.
- Extract to a folder with a short name, for example `C:\Indigo`.
- The TomTom IndiGO SDK contains some files with a long path name, especially in the `docs` folder, 
  but the path length on Windows is restricted to 260 characters. See
  [this article in Microsoft Developer](https://docs.microsoft.com/en-us/windows/win32/fileio/maximum-file-path-limitation)
  if you wish to use longer path names.

__Next step:__
[Accessing the Artifact repository](/tomtom-indigo/documentation/getting-started/accessing-the-artifact-repository).
