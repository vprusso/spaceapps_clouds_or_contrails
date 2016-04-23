watson_image_classification
==========================
*Uses the Watson Visual Recognition service to create a cloud / contrail
classifier, and then test whether or not a given image is a contrail or not.

### Usage
To train the classifier:
```
node create_classifier.js
```

To classify some image in the working directory:
```
node classify_image.js
```
Running this will spit out some JSON that gives the probability
with which the image is a contrail. 