package underscore.com.vlex.util;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Sachith Senarathne on 8/7/16.
 */
public class VLexSingleton {

    private static VLexSingleton requestInstance;
    private RequestQueue requestQueue;
    private static Context requestContext;


    private VLexSingleton(Context context){

        requestContext = context;
        requestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue(){

        if(requestQueue == null){

            requestQueue = Volley.newRequestQueue(requestContext.getApplicationContext());
        }

        return requestQueue;
    }

    public static synchronized VLexSingleton getInstance(Context context){

        if(requestInstance == null){
            requestInstance = new VLexSingleton(context);
        }

        return requestInstance;
    }

    public<T> void addToRequestQueue(Request<T> request){

        requestQueue.add(request);
    }
}
