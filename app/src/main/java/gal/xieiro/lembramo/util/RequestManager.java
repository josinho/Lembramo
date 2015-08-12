package gal.xieiro.lembramo.util;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class RequestManager {
    private static RequestQueue mQueue = null;

    protected RequestManager() {
        //sin instancias
    }

    public static void init(Context context) {
        if (mQueue == null)
            mQueue = Volley.newRequestQueue(context);
    }

    public static RequestQueue getRequestQueue() {
        if (mQueue != null) {
            return mQueue;
        } else {
            throw new IllegalStateException("Not initialized");
        }
    }
}
