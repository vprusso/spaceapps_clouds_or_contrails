package android.captainhampton.com.spaceapps;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserLocationActivity extends AppCompatActivity implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener,
        View.OnClickListener {

    //Define a request code to send to Google Play services
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private String mCurrentPhotoPath, imgPhotoPath;
    private ImageButton ibCloseBtn;
    private ImageView ivImageToUpload;
    private String imageFileName;
    private TextView tvChances;
    //private static final String SERVER_ADDRESS = "http://162.243.248.12:8081/?image=http://vprusso-spaceapps-beta.site88.net/pictures/";
    //private static final String SERVER_ADDRESS = "http://vprusso-spaceapps-beta.site88.net/";
    private static final String SERVER_POST_ADDRESS = "http://vprusso-spaceapps-beta.site88.net/SavePicture.php";
    private static final String SERVER_GET_ADDRESS = "http://162.243.248.12:8081/?image=http://vprusso-spaceapps-beta.site88.net/pictures/";
    public void setupVariables() {
        ivImageToUpload = (ImageView) findViewById(R.id.ivImageToUpload);
        ibCloseBtn = (ImageButton) findViewById(R.id.ibCloseBtn);
        tvChances = (TextView) findViewById(R.id.tvChances);
        //bUploadImage = (Button) findViewById(R.id.bUploadImage);
        //tvUserLatitude = (TextView) findViewById(R.id.tvUserLatitude);
        //tvUserLongitude = (TextView) findViewById(R.id.tvUserLongitude);

        //ivImageToUpload.setOnClickListener(this);
        //bUploadImage.setOnClickListener(this);
        //tvUserLongitude.setOnClickListener(this);
        //tvUserLatitude.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location);
        setupVariables();


        ibCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        dispatchTakePictureIntent();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Now lets connect to the API
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(this.getClass().getSimpleName(), "onPause()");

        //Disconnect from API onPause()
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }


    }

    private File createImageFile() throws IOException {
        imageFileName = "IMG_" + Double.toString(currentLatitude) + "_" + Double.toString(currentLongitude);
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        Log.i("Main", mCurrentPhotoPath);
        imgPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d("Main", "Error occurred while creating the file ");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * If connected get lat and long
     *
     */
    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } else {
            //If everything went fine lets get latitude and longitude
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();

            Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
            //tvUserLatitude.setText(Double.toString(currentLatitude));
            //tvUserLongitude.setText(Double.toString(currentLongitude));
        }
    }


    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
            /*
             * Google Play services can resolve some errors it detects.
             * If the error has a resolution, try sending an Intent to
             * start a Google Play services activity that can resolve
             * error.
             */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                    /*
                     * Thrown if Google Play services canceled the original
                     * PendingIntent
                     */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
                /*
                 * If no resolution is available, display a dialog to the
                 * user with the error.
                 */
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    /**
     * If locationChanges change lat and long
     *
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        /*switch(v.getId()) {
            case R.id.ivImageToUpload:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                break;
            case R.id.bUploadImage:
                Bitmap image = ((BitmapDrawable) ivImageToUpload.getDrawable()).getBitmap();
                new UploadImage(image, Double.toString(currentLatitude) + "_" + Double.toString(currentLongitude)).execute();
                //new UploadImage(image, uploadImageName.getText().toString()).execute();
                break;
        }*/
    }

    /*private String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        try {
            while((line = bufferedReader.readLine()) != null){
                result += line;
            }
        } catch (Exception e){
            Toast.makeText(this, "InputStream Conversion Exception", Toast.LENGTH_SHORT).show();
        }

        inputStream.close();
        return result;


        HttpResponse response; // some response object
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
        StringBuilder builder = new StringBuilder();
        for (String line = null; (line = reader.readLine()) != null;) {
            builder.append(line).append("\n");
        }
        JSONTokener tokener = new JSONTokener(builder.toString());
        try{

        } catch (Exception e) {
            JSONArray finalResult = new JSONArray(tokener);
        }
        //return null;


    }

    public String makeRequest (String URL){
        InputStream iStream = null;
        String response = "";
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(URL);
        try {
            HttpResponse httpResponse = httpclient.execute(httpget);
            iStream = httpResponse.getEntity().getContent();

            if(iStream != null)
                response = convertInputStreamToString(iStream);
            //JSONObject myObject = new JSONObject(
            //Log.d("HTTP_RESPONSE)", String.valueOf(httpResponse));//);

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return response;
    }

    private class GetData extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... params){
            String server_response = makeRequest(SERVER_ADDRESS + imageFileName + ".jpg");
            Log.d("SERVER_RESPONSE", server_response);
            return null;
        }
    }*/

    private class UploadImage extends AsyncTask<Void, Void, Void> {

        Bitmap image;
        String name;

        public UploadImage(Bitmap image, String name) {
            this.image = image;
            this.name = name;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            OkHttpClient client = new OkHttpClient();
            Log.e("xxx", encodedImage);
            /*HashMap<String, String> dataToSend2 = new HashMap<>();
            dataToSend2.put("image", encodedImage);
            dataToSend2.put("name", name);*/
                RequestBody formBody = new FormBody.Builder()
                        .add("image", encodedImage)
                        .add("name", name == null ? "duh" : name)
                        .build();
            Boolean exit = false;
            for (int i = 0; i < 5 && !exit; i++){
                Request post_request = new Request.Builder()
                        .url(SERVER_POST_ADDRESS)
                        .post(formBody)
                        .build();

                Response post_response = null;
                try {
                    post_response = client.newCall(post_request).execute();
                    String post_respStr = post_response.body().string();
                    Log.d("POST_RESPONSE", post_respStr);
                    exit = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            Request get_request = new Request.Builder()
                    .url(SERVER_GET_ADDRESS + imageFileName + ".JPG")
                    //.url("http://162.243.248.12:8081/?image=http://vprusso-spaceapps-beta.site88.net/pictures/IMG_43.4509684_-80.4977896.JPG")
                    .get()
                    .build();


            Log.d("GET_URL", SERVER_GET_ADDRESS + imageFileName + ".JPG");
            Response get_response = null;
            try {
                get_response = client.newCall(get_request).execute();
                String get_respStr = get_response.body().string();
                Log.d("GET_RESPONSE", get_respStr);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //performPostCall(SERVER_ADDRESS + "SavePicture.php", dataToSend2);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();

            //new GetData().execute();
        }
    }

    /*public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    //Create a file Uri for saving an image or video
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    //Create a File for saving an image or video
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                //return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                //Toast.makeText(this, "Image saved to:\n" +
                        //data.getData(), Toast.LENGTH_LONG).show();

                Bitmap imgBitMap = BitmapFactory.decodeFile(imgPhotoPath);
                Log.d("ERROR", imgPhotoPath);
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
            try{
                Bitmap displayImg = Bitmap.createBitmap(imgBitMap, 0, 0, imgBitMap.getWidth()/2, imgBitMap.getHeight());
                ivImageToUpload.setImageBitmap(displayImg);
                Bitmap image = ((BitmapDrawable) ivImageToUpload.getDrawable()).getBitmap();
                // make sure the file is just called "contrail_clip.jpg" for every picture
                new UploadImage(image, imageFileName).execute();
                //new UploadImage(image, uploadImageName.getText().toString()).execute();
            } catch (NullPointerException e){
                Log.e("Bitmap_Error", "Bitmap Null Pointer");
                dispatchTakePictureIntent();
            }

        } else if (resultCode == RESULT_CANCELED) {
            // User cancelled the image capture
        } else {
            // Image capture failed, advise user
        }
        /*if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            ivImageToUpload.setImageURI(selectedImage);
        }*/
    }
    // http://stackoverflow.com/questions/29536233/deprecated-http-classes-android-lollipop-5-1
    public String performPostCall(String requestURL, HashMap<String, String> postDataParams) {

        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

}
