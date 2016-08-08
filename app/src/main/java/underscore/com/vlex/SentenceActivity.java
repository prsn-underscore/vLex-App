package underscore.com.vlex;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.devspark.appmsg.AppMsg;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import underscore.com.vlex.util.Card;
import underscore.com.vlex.util.CardArrayAdapter;
import underscore.com.vlex.util.VLexSingleton;
import underscore.com.vlex.util.VLexUtil;

public class SentenceActivity extends AppCompatActivity {

    private CardArrayAdapter cardArrayAdapter;
    private ListView listView;

    String trimmedTag = null;
    String imagePath = null;
    String selectedWord = null;
    JSONArray jsonArraySentences = null;
    TextToSpeech t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentence);

        Bundle extras = getIntent().getExtras();
        // add no image view
        if (extras != null) {
            imagePath = extras.getString("path");
            selectedWord= extras.getString("word");
            trimmedTag= extras.getString("tag");

            Button objectText = (Button) findViewById(R.id.objectNameBtn);
            objectText.setText(""+selectedWord);
            String sarraystring = extras.getString("sentences");
            try {
                jsonArraySentences = new JSONArray(sarraystring);
            } catch (JSONException e) {
                e.printStackTrace();
                AppMsg.makeText(SentenceActivity.this ,"Error in converting sentences", AppMsg.STYLE_INFO).show();
            }
        }

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                }
            }
        });

        Bitmap myBitmap = BitmapFactory.decodeFile(imagePath);
        ImageView capturedPhotoImageView = (ImageView) findViewById(R.id.iVselectedImage);
        capturedPhotoImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        capturedPhotoImageView.setImageBitmap(myBitmap);

        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.saveFloatingButton);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                AppMsg.makeText(SentenceActivity.this ,"Your sentences are saved", AppMsg.STYLE_INFO).show();
            }
        });

        listView = (ListView) findViewById(R.id.listViewSentences);
        listView.addHeaderView(new View(this));
        listView.addFooterView(new View(this));
        listView.setDivider(null);
        listView.setDividerHeight(0);

        cardArrayAdapter = new CardArrayAdapter(getApplicationContext(), R.layout.list_item_card);

        if (jsonArraySentences != null) {
            for (int i = 0; i < jsonArraySentences.length(); i++) {

                try {

                    String sentence = jsonArraySentences.getJSONObject(i).get("sentence").toString();
                    String translation = jsonArraySentences.getJSONObject(i).get("translation").toString();
                    Card card = new Card(sentence, translation);
                    cardArrayAdapter.add(card);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            listView.setAdapter(cardArrayAdapter);
        }else {
            AppMsg.makeText(SentenceActivity.this ,"No sentences available", AppMsg.STYLE_CONFIRM).show();
        }

    }
}
