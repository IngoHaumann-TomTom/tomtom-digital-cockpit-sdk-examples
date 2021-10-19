
We use [Jekyll](https://jekyllrb.com/), which is a 
[Ruby Gem](https://en.wikipedia.org/wiki/RubyGems), to generate static HTML files from Markdown 
files to populate the developer portal with. 

## Introduction

The documentation in the `/src` folder covers 3 sections on the developer portal:
- Documentation
- API Reference
- Releases

These sections are updated on the developer portal by uploading the generated HTML files for each 
section separately. 

The order of menu items and navigation bars on the developer portal are 
automatically generated from the file names and directory structure of the uploaded files:

- The digits in file and folder names determine their order in the navigation (sub)menu.
- The directory structure directly reflects how the levels of pages are organized. Nested files will 
  be nested pages.
  
## Getting started

To use Jekyll, you first need to have Ruby packages installed on your system. After installing
the Ruby essentials, you can install Jekyll with the `gem` command, which is Ruby's package 
manager. As a final step you will have to update dependencies with 
[Bundler](https://medium.com/never-hop-on-the-bandwagon/gemfile-and-gemfile-lock-in-ruby-65adc918b856).

First install the Ruby packages:

```bash
sudo apt-get install ruby-full
```

Then install Jekyll and Bundler:

```bash
sudo gem install jekyll bundler
```

Now run Bundler to update the environment and dependencies:

```bash
bundler install
```

__Note:__ Bundler may require you to run this command as `sudo`.

If all dependencies are met, you will get the following confirmation:

```bash
Bundle complete! 2 Gemfile dependencies, 28 gems now installed.
Use `bundle info [gemname]` to see where a bundled gem is installed.
```

You are now ready to use Jekyll. For more information on install options and operating systems, 
consult [the Jekyll installation documentation](https://jekyllrb.com/docs/installation/).

## Commands

To generate the HTML files:

```bash
./gradlew portal
```

The HTML files are generated in the `build/html` directory, under their respective 
section folders. A few notes on the generated files:

- The generated files will have no styling, so they will be rendered as plain HTML when previewed. 
  The styling will get added automatically to the pages when uploaded to the developer portal.
- The generated HTML pages lack any form of navigation when previewed, as there is no index page 
  that lists the site's content. This also will be added by the developer portal.
  
