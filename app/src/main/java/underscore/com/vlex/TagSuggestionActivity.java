package underscore.com.vlex;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.devspark.appmsg.AppMsg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import eu.fiskur.chipcloud.ChipCloud;
import eu.fiskur.chipcloud.ChipListener;
import underscore.com.vlex.util.VLexUtil;

public class TagSuggestionActivity extends AppCompatActivity {

    String trimmedTag = null;
    String imagePath = null;
    String selectedWord = null;
    private JSONObject sentenceRequest;
    private JSONArray sentenceArray;
    String url = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_suggestion);
        Bundle extras = getIntent().getExtras();
         // add no image view
        if (extras != null) {
            imagePath = extras.getString("path");
            String[] parts = extras.getString("word").split(" ");
            selectedWord = parts[0];
        }


        try {
            url = VLexUtil.getProperty("baseUrl", getApplicationContext()) + "sentences";

        } catch (IOException e) {
            e.printStackTrace();
        }

        final String[] tags = {
                "bring (trazer)",
                "buy (comprar)",
                "take (leve)",
                "catch (pegar)",
                "throw (jogue)",
                "repair (reparação)",
                "show (mostrar)",
                "brake (pausa)",
                "get (obter)",
                "use (usar)",
                "eat (comer)",
                "drink (beber)",
                "see (ver)",
                "find (encotrar)",
                "give (dar)",
                "pay for (pagar)",
                "send (enviar)",
                "build (construir)",
                "cut (cortar)"
        };

        ChipCloud chipCloud = (ChipCloud) findViewById(R.id.chip_cloud);
        new ChipCloud.Configure()
                .chipCloud(chipCloud)
                .selectTransitionMS(500)
                .deselectTransitionMS(250)
                .labels(tags)
                .mode(ChipCloud.Mode.SINGLE)
                .chipListener(new ChipListener() {
                    @Override
                    public void chipSelected(int index) {
                        String cloudTag = tags[index];
                        String[] parts = cloudTag.split(" ");
                        trimmedTag = parts[0];

                    }
                    @Override
                    public void chipDeselected(int index) {

                    }
                })
                .build();

        Button register_btn = (Button) findViewById(R.id.buttonSubmitTags);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView textViewTag = (TextView) findViewById(R.id.input_tagName);
                String tagWord = textViewTag.getText().toString();
                if (!tagWord.equals(null) && !tagWord.equals("")) {
                    Intent intent = new Intent(TagSuggestionActivity.this, SentenceActivity.class);

                    getSentenceReq("I",selectedWord,tagWord);
                    new HttpRequestTask().execute();

                    intent.putExtra("tag",tagWord);
                    intent.putExtra("word", selectedWord);
                    intent.putExtra("path", imagePath);
                    intent.putExtra("sentences", sentenceArray.toString());
                    startActivity(intent);
                } else if(!trimmedTag.equals(null)){
                    Intent intent = new Intent(TagSuggestionActivity.this, SentenceActivity.class);

                    getSentenceReq("I",selectedWord,trimmedTag);
                    new HttpRequestTask().execute();

                    intent.putExtra("tag", trimmedTag);
                    intent.putExtra("word", selectedWord);
                    intent.putExtra("path", imagePath);
                    intent.putExtra("sentences", sentenceArray.toString());
                    startActivity(intent);
                } else {
                    AppMsg.makeText(TagSuggestionActivity.this ,"Please select a tag", AppMsg.STYLE_CONFIRM).show();
                }
            }
        });
    }

    private void getSentenceReq(String sub,String obj,String verb)
    {
        try {
            sentenceRequest=new JSONObject();
            sentenceRequest.put("subject",sub);
            sentenceRequest.put("object",obj);
            sentenceRequest.put("verb",verb);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void... voids) {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<String>(sentenceRequest.toString(),headers);
            String answer = restTemplate.postForObject(url, entity, String.class);
            return answer;
        }

        @Override
        protected void onPostExecute(String sentence) {
            try {
                sentenceArray=new JSONArray(sentence);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
