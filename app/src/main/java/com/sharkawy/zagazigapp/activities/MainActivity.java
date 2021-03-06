package com.sharkawy.zagazigapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sharkawy.zagazigapp.R;
import com.sharkawy.zagazigapp.RecyclerItemClickListener;
import com.sharkawy.zagazigapp.utilities.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Spinner s1;
    Button btn1;
    EditText search_input ;
    TabLayout tabLayout ;
    ProgressDialog pDialog ;
    private static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        s1=(Spinner)findViewById(R.id.spinner);
         search_input = (EditText) findViewById(R.id.etsearch);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage(getResources().getString(R.string.msg_loading));

        String [] arr ={"القومية ","شارع المحافظة","مفارق المنصورة","فلل الجامعة","حي الزهور","المنتزة","شارع البحر","المحطة","شارع مديرالامن","عمر افندي","عمارة الاوقاف","حي ثاني","شارع الغشام"};
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, arr);
        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        final TabLayout.Tab favorites = tabLayout.newTab();
        final TabLayout.Tab offers = tabLayout.newTab();
        final TabLayout.Tab job = tabLayout.newTab();
        final TabLayout.Tab courses = tabLayout.newTab();

        favorites.setText("Favorites");
        offers.setText("Offers");
        job.setText("Job");
        courses.setText("Courses");

        tabLayout.addTab(favorites, 0);
        tabLayout.addTab(offers, 1);
        tabLayout.addTab(job, 2);
        tabLayout.addTab(courses,3);

        s1.setAdapter(dataAdapter);
        btn1=(Button)findViewById(R.id.btn1);
        Animation animation = new TranslateAnimation(0, 500,0, 0);
        animation.setDuration(1000);
        btn1.startAnimation(animation);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,CategoryActivity.class);
                startActivity(i);
            }
        });


        search_input.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    String URL ="http://www.mashaly.net/zag.php?filter="+s1.getSelectedItem().toString();
                    Toast.makeText(MainActivity.this,URL, Toast.LENGTH_SHORT).show();
                    makeJsonObjectRequest();
                    Intent i = new Intent(MainActivity.this,SearchResultActivity.class);
                    startActivity(i);
                    return true;
                }
                return false;
            }
        });

    }
    private void makeJsonObjectRequest() {
        showpDialog();
        String URL ="http://www.mashaly.net/zag.php?filter="+s1.getSelectedItem().toString().replace(" ","%20");
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                URL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                // Parsing json object response
                hidepDialog();
//                Toast.makeText(MainActivity.this,response.toString(), Toast.LENGTH_LONG).show();
                try {
                    if(response.getString("message").toString().equals("success")){

                        Toast.makeText(MainActivity.this,response.toString(), Toast.LENGTH_LONG).show();

                    }else {
                        Toast.makeText(getApplicationContext(),
                                "Something went wrong..!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                hidepDialog();

                String responseBody = null;
                JSONObject jsonObject=null ;
                try {

                    responseBody = new String( error.networkResponse.data, "utf-8" );
                    jsonObject = new JSONObject( responseBody );

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,"Connection Error.",Toast.LENGTH_LONG).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);

    }
    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }
    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
