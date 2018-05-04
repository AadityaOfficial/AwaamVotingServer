package com.example.aadityasuri.awaam;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class FacialRecognition extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String mCurrentPhotoPath;
    SessionManager session;
    HashMap<String, String> userdetail;
    ImageView mImageView;
    String facialRecUrl = "https://api.kairos.com/recognize";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        userdetail = session.getUserDetails();
        final RequestQueue recogniseQueue = Volley.newRequestQueue(FacialRecognition.this);
        final String username = userdetail.get(SessionManager.KEY_EMAIL);
        File f1 = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), username + ".jpg");
        f1.delete();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facial_recognition);
        Button camera = (Button) findViewById(R.id.camera_button);
        Button preview = (Button) findViewById(R.id.preview);
        Button submit = (Button) findViewById(R.id.submit_button);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent(username);

            }
        });
        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    File f = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), username + ".jpg");
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                    ImageView img = (ImageView) findViewById(R.id.camera_thumbnail);
                    img.setImageBitmap(b);
                    img.setRotation(-90);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog loading = ProgressDialog.show(FacialRecognition.this, "Uploading...", "Please wait...", false, false);
                String encoded = "";
                Map<String, String> jsonParams = new HashMap<String, String>();

                try {
                    File f = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), username + ".jpg");
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                    b=RotateBitmap(b,-90);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    b.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    encoded = Base64.encodeToString(byteArray,Base64.NO_CLOSE);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                File path=getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File file = new File(path, "my-file-name.txt");
                FileOutputStream stream = null;
                try {
                    stream = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    stream.write(encoded.getBytes());
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                jsonParams.put("image",encoded);
                jsonParams.put("gallery_name", "awaamgallery");
                JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, facialRecUrl, new JSONObject(jsonParams),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                loading.dismiss();
                                Log.e("Response : ", String.valueOf(response));
                                try {
                                    JSONArray images=response.getJSONArray("images");
                                    JSONArray requiredValues=images.getJSONObject(0).getJSONArray("candidates");
                                    String recievedUser=requiredValues.getJSONObject(0).getString("subject_id");
                                    if(recievedUser.equals(username)){
                                        Toast.makeText(getApplicationContext(), "Welcome "+username, Toast.LENGTH_LONG).show();
                                        Intent electionIntent=new Intent(FacialRecognition.this,ElectionClass.class);
                                        startActivity(electionIntent);
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(), "Facial Identity Mismatch", Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }





                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if (error == null || error.networkResponse == null) {
                                    Log.e("Error network", "Connect error");
                                    return;

                                }
                                Toast.makeText(getApplicationContext(), "Facial Identity Mismatch", Toast.LENGTH_LONG).show();

                                String body = "";
                                final String statusCode = String.valueOf(error.networkResponse.statusCode);
                                //get status code here
                                //get response body and parse with appropriate encoding
                                try {
                                    body = new String(error.networkResponse.data, "UTF-8");
                                    Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_LONG).show();
                                } catch (UnsupportedEncodingException e) {
                                    // exception
                                }
                                if (loading != null && loading.isShowing()) {
                                    loading.dismiss();

                                }
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("app_id", "5ad2e0b7");
                        headers.put("app_key", "b06a0045ce9c6dcd239c882179bf36a2");
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }
                };
                recogniseQueue.add(postRequest);

            }
        });


    }

    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent(String username) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {


            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(username);
            } catch (IOException ex) {
                // Error occurred while creating the File

            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }
        }
    }

    private File createImageFile(String username) throws IOException {
        // Create an image file name

        String imageFileName = username;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir, imageFileName + ".jpg"         /* suffix */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

}
