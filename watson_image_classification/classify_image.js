/*
classify_image.js: 
   Connect to the Bluemix interface to classify a test image
   against a classifier trained from ``create_classifier``. 
   contrails_classifier_1232401869
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
	images_file: fs.createReadStream('./contrail_clip.jpg'),
	classifier_ids: fs.readFileSync('./classifierlist.json')
};

visual_recognition.classify(params, 
	function(err, response) {
   	 if (err)
      		console.log(err);
    	 else
   		console.log(JSON.stringify(response, null, 2));
});