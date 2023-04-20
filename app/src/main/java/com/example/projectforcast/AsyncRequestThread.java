package com.example.projectforcast;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class AsyncRequestThread extends Thread {
    private static RequestQueue mRequestQueue;
    public JSONObject responseJson;
    private static final Object lock = new Object();
    private final String mURL= "https://cth42.net/php/forecast/demo.php";
    private JSONObject mArgument;
    private Context context;

    public AsyncRequestThread(Context context){
        this.context = context;
    }
    private RequestQueue getRequestQueue(){
        if(mRequestQueue==null){
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return mRequestQueue;
    }
    private<T> void addToRequestQueue(Request<T> request){
        getRequestQueue().add(request);
    }

    public void updatePhpArgumentsAndRunThread (String IDIdentify, String IDValue){
        mArgument = new JSONObject();
        try {
            if (IDIdentify.contains("|")) {
                String[] Identify = IDIdentify.split("\\|");
                String[] Value = IDValue.split("\\|");
                for (int x = 0; x < Identify.length; x++) {
                    System.out.println(Value[x]);
                    if(Value[x].equals(" ")){
                        mArgument.put(Identify[x], null );
                    }
                    else {
                        mArgument.put(Identify[x], Value[x].replace("\\","-").replace("/","-").replace("--","-"));
                    }
                }
            }
            else {
                mArgument.put(IDIdentify, IDValue);
            }
            System.out.println("Async Request Arguments: "+ mArgument + " \n"+mURL);
            if (Looper.myLooper()==null) {
                Looper.prepare();
            }
            this.start();
            System.out.println("Started: Current Thread Count: "+ Thread.activeCount());
        }
        catch (JSONException e ) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, mURL, mArgument, response -> {
                    System.out.println("Response: "+ response);
                    synchronized (lock) {
                        responseJson = response;
                        lock.notify();
                    }
        }, volleyError -> Log.e("Message", "problem occurred, volley error: " + volleyError.getMessage()));
        this.addToRequestQueue(jsonObjectRequest);
        System.out.println("Async Lock Locked");
        synchronized (lock) {
            while (responseJson == null) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Async Lock Unlocked");
        if(responseJson.has("Success") ){
            try {
                if(responseJson.getBoolean("Success")){
                    Toast.makeText(context, "Data Added", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(context, "Data Not Added", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Toast.makeText(context, "Runtime Error", Toast.LENGTH_SHORT).show();
                throw new RuntimeException(e);
            }

        }
        else if(responseJson.has("SowList")){
            // TODO: Set spinners in second fragment
            
        }
        else{
            Toast.makeText(context, "PHP Error", Toast.LENGTH_SHORT).show();
        }

        responseJson = null;
    }
}
