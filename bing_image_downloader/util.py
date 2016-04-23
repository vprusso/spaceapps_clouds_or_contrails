# Utility functions for processing downloaded content from search, i.e.
# renaming and resizing.

import os
import sys

from PIL import Image
from resizeimage import resizeimage

def rename_files(path, new_file_name):
    os.chdir(path)
    filenames = os.listdir(path)
    count = 1
    for filename in filenames:
        os.rename(filename, new_file_name + "_" + str(count) + "_" + ".jpg")
        count += 1

def resize_files(path, x_dim, y_dim):
    os.chdir(path)
    filenames = os.listdir(path)

    for filename in filenames:
        im = Image.open(filename)

        if  im.size[0] > x_dim and im.size[1] > y_dim:
            with open(filename, 'r+b') as f:
                with Image.open(f) as image:
                    cover = resizeimage.resize_cover(image,[x_dim,y_dim])
                    cover.save(filename, image.format)


        elif im.size[0] > x_dim and im.size[1] < y_dim:
            with open(filename, 'r+b') as f:
                with Image.open(f) as image:
                    cover = resizeimage.resize_cover(image,[x_dim, im.size[1]])
                    cover.save(filename, image.format)

        elif im.size[0] < x_dim and im.size[1] > y_dim:
            with open(filename, 'r+b') as f:
                with Image.open(f) as image:
                    cover = resizeimage.resize_cover(image,[im.size[0], y_dim])
                    cover.save(filename, image.format)


if os.path.isdir(os.getcwd() + "\clouds") and \
    os.path.isdir(os.getcwd() + "\contrails"):

    cloud_dir = os.getcwd() + "\clouds"
    contrail_dir = os.getcwd() + "\contrails"

    print("Renaming cloud images...")
    rename_files(cloud_dir,"clouds")
    print("Renaming contrail image...")
    rename_files(contrail_dir,"contrails")

    print("Resizing cloud images")
    resize_files(cloud_dir,320,320)
    print("Resizing contrail images")
    resize_files(contrail_dir,320,320)