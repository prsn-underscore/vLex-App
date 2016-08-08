package underscore.com.vlex.util;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Created by Sachith Senarathne on 8/7/16.
 */
public class TextSpeaker implements TextToSpeech.OnInitListener
{
    TextToSpeech tts;
    String sentence;

    public TextSpeaker(Context c, String line1)
    {
        tts = new TextToSpeech(c, this);
        sentence = line1;
    }

    @Override
    public void onInit(int status) {
        if(status != TextToSpeech.ERROR){
            tts.setLanguage(Locale.US);
            tts.speak(sentence, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}
