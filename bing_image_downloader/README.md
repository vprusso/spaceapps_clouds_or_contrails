bing_image_downloader
==========================
*Bulk Bing Image Downloader (BBID)* is downloader which:
- downloads full-size images from bing image search results
- is multithreaded
- is crossplatform
- bypasses bing API
- has option to disable adult content filtering
- is written in python 3.
- uses SSL connection

### Usage
```
chmod +x bbid.py
./bbid.py [-h] [-s SEARCH_STRING] [-f SEARCH_FILE] [-o OUTPUT] [--filter] [--no-filter]
```
### Example
`./bbid.py -s earth`

# How to use for Clouds or Contrails project
==========================

## Download images of clouds and contrails by:
`Py -3 bbid.py -s contrails`
`Py -3 bbid.py -s clouds`

For resizing or renaming images once downloaded run ``util.py''

