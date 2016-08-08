package underscore.com.vlex.util;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;

import java.util.Locale;


public class Card {
    private String line1;
    private String line2;

    public Card(String line1, String line2) {
        this.line1 = line1;
        this.line2 = line2;
    }

    View.OnClickListener listener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Log.d("OK", line1);
            TextSpeaker ttsUtil = new TextSpeaker(v.getContext(),line1);
            ttsUtil.tts.speak(line1,TextToSpeech.QUEUE_FLUSH,null);
        }
    };

    public String getLine1() {
        return line1;
    }

    public String getLine2() {
        return line2;
    }
}
