/*
create_classifier.js:
   Connect to Watson application and upload two zipped folders
   labelled as ``clouds`` and ``contrails``. Each folder consists
   of images of either clouds or contrails scaled to 320 x 320 
   pixels.
*/ 
var watson = require('watson-developer-cloud');
var fs = require('fs');

var visual_recognition = watson.visual_recognition({
  username: '1c6ece7f-9e3f-4aa9-88f6-ac96a639bb03',
  password: 'YLI1qiyefpTb',
  version: 'v2-beta',
  version_date: '2015-12-02'
});

var params = {
	name: 'contrails_classifier',
	positive_examples: fs.createReadStream('./contrails.zip'),
	negative_examples: fs.createReadStream('./clouds.zip')
};

visual_recognition.createClassifier(params, 
	function(err, response) {
   	 if (err)
      		console.log(err);
    	 else
   		console.log(JSON.stringify(response, null, 2));
});