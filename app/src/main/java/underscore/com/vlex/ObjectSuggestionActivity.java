package underscore.com.vlex;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.devspark.appmsg.AppMsg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import underscore.com.vlex.util.VLexSingleton;
import underscore.com.vlex.util.VLexUtil;

public class ObjectSuggestionActivity extends AppCompatActivity {

    private RadioGroup radioWordGroup;
    private  String selectedWordId = null;
    private String path = null;
    TextToSpeech t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objec_suggestiont);

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });

        Bundle extras = getIntent().getExtras();
        String imagePath = null;
        if (extras != null) {
            imagePath = extras.getString("path");
            path = imagePath;
            Log.d("Image path" , imagePath);
        }

        File imgFile = new File(imagePath);

        if (imgFile.exists()) {

            Uri imageUri = Uri.fromFile(imgFile);
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            Bitmap rotatedImage = null;

            try {
                rotatedImage = rotateImageIfRequired(myBitmap, getApplicationContext(), imageUri);
                retrieveSuggestedWords(rotatedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }


           ImageView capturedPhotoImageView = (ImageView) findViewById(R.id.iVselectedImage);
           capturedPhotoImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
           capturedPhotoImageView.setImageBitmap(rotatedImage);
           capturedPhotoImageView.setBackgroundColor(Color.parseColor("#212121"));



        }else {
            Log.e("Image" , "Image not found");
        }

        radioWordGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioWordGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.radioButton) {

                    TextView wordOne = (TextView) findViewById(R.id.textView);
                    selectedWordId = wordOne.getText().toString();
                } else if (checkedId == R.id.radioButton2) {
                    TextView wordOTwo = (TextView) findViewById(R.id.textView2);
                    selectedWordId = wordOTwo.getText().toString();
                } else if (checkedId == R.id.radioButton3) {
                    TextView wordThree = (TextView) findViewById(R.id.textView3);
                    selectedWordId = wordThree.getText().toString();
                } else if (checkedId == R.id.radioButton4) {
                    TextView wordFour = (TextView) findViewById(R.id.textView4);
                    selectedWordId = wordFour.getText().toString();
                } else if (checkedId == R.id.radioButton5) {
                    TextView wordFive = (TextView) findViewById(R.id.textView5);
                    selectedWordId = wordFive.getText().toString();

                }

            }
        });

        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.nextFloatingButton);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                if (!selectedWordId.equals(null) && !selectedWordId.equals("")) {
                    Intent intent = new Intent(ObjectSuggestionActivity.this, TagSuggestionActivity.class);
                    intent.putExtra("word",selectedWordId);
                    intent.putExtra("path",path);
                    startActivity(intent);
                } else {
                    AppMsg.makeText(ObjectSuggestionActivity.this ,"Please select a word (Por favor, selecione uma palavra)", AppMsg.STYLE_CONFIRM).show();
                }
            }
        });

        Button button = (Button)findViewById(R.id.button);
        Button button2 = (Button)findViewById(R.id.button2);
        Button button3 = (Button)findViewById(R.id.button3);
        Button button4 = (Button)findViewById(R.id.button4);
        Button button5 = (Button)findViewById(R.id.button5);
        // Register the onClick listener with the implementation above
        button.setOnClickListener(ttsOnClickListner);
        button2.setOnClickListener(ttsOnClickListner);
        button3.setOnClickListener(ttsOnClickListner);
        button4.setOnClickListener(ttsOnClickListner);
        button5.setOnClickListener(ttsOnClickListner);

        // Create an anonymous implementation of OnClickListener

    }

    private View.OnClickListener ttsOnClickListner = new View.OnClickListener() {
        public void onClick(View v) {
            TextView word1 = (TextView) findViewById(R.id.textView);
            TextView word2 = (TextView) findViewById(R.id.textView2);
            TextView word3 = (TextView) findViewById(R.id.textView3);
            TextView word4 = (TextView) findViewById(R.id.textView4);
            TextView word5 = (TextView) findViewById(R.id.textView5);
            switch(v.getId()){
                case R.id.button:
                    speakWord(word1.getText().toString());
                    break;
                case R.id.button2:
                    speakWord(word2.getText().toString());
                    break;
                case R.id.button3:
                    speakWord(word3.getText().toString());
                    break;
                case R.id.button4:
                    speakWord(word4.getText().toString());
                    break;
                case R.id.button5:
                    speakWord(word5.getText().toString());
                    break;
            }
        }
};

    private void speakWord(String s) {

        String[] parts = s.split(" ");
        String trimmedWord = parts[0];

        t1.speak(trimmedWord, TextToSpeech.QUEUE_FLUSH, null);
    }

    private void retrieveSuggestedWords(Bitmap imgBitMapFile) {

        JSONObject requestBody = new JSONObject();
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imgBitMapFile.compress(Bitmap.CompressFormat.JPEG, 30, baos);
            byte[] b = baos.toByteArray();
            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            Log.d("Encoded", "index=" + encodedImage);
                requestBody.accumulate("image" , encodedImage);
                sendImageRecogRequest(requestBody);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendImageRecogRequest(JSONObject requestBody) {

            String url = null;
            try {
                url = VLexUtil.getProperty("baseUrl",getApplicationContext())+"classifications";

            } catch (IOException e) {
                e.printStackTrace();
            }


        JsonArrayRequest imageWordRequest = new JsonArrayRequest(Request.Method.POST, url, requestBody,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("Res Image" , response.toString());

                        if (!response.equals(null)) {
                            try {
                                TextView wordOne = (TextView) findViewById(R.id.textView);
                                wordOne.setText(response.getString(0));
                                TextView wordOTwo = (TextView) findViewById(R.id.textView2);
                                wordOTwo.setText(response.getString(1));
                                TextView wordThree = (TextView) findViewById(R.id.textView3);
                                wordThree.setText(response.getString(2));
                                TextView wordFour = (TextView) findViewById(R.id.textView4);
                                wordFour.setText(response.getString(3));
                                TextView wordFive = (TextView) findViewById(R.id.textView5);
                                wordFive.setText(response.getString(4));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            AppMsg.makeText(ObjectSuggestionActivity.this ,"No suggested words available", AppMsg.STYLE_CONFIRM).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

                imageWordRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            VLexSingleton.getInstance(ObjectSuggestionActivity.this).addToRequestQueue(imageWordRequest);
    }


    public static Bitmap rotateImageIfRequired(Bitmap img, Context context, Uri selectedImage) throws IOException {

        if (selectedImage.getScheme().equals("content")) {
            String[] projection = { MediaStore.Images.ImageColumns.ORIENTATION };
            Cursor c = context.getContentResolver().query(selectedImage, projection, null, null, null);
            if (c.moveToFirst()) {
                final int rotation = c.getInt(0);
                c.close();
                return rotateImage(img, rotation);
            }
            return img;
        } else {
            ExifInterface ei = new ExifInterface(selectedImage.getPath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Log.d("orientation: ", String.valueOf(orientation));

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(img, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(img, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(img, 270);
                default:
                    //return rotateImage(img, 90);
                    return img;
            }
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        return rotatedImg;
    }
}
