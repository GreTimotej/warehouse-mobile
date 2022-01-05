package com.example.warehouse_mobile_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ItemActivity extends AppCompatActivity {

    String itemUrl;
    private RequestQueue requestQueue;
    private TextView idText;
    private TextView nameText;
    private TextView descText;
    private TextView quantityText;
    private TextView activeText;
    private TextView status;
    private Button export;

    private int itemID;
    private String name;
    private String description;
    private int quantity;
    private Boolean active;
    private int warehouseID;
    private int customerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        Intent intent = getIntent();

        itemUrl = "https://warehouse-is.azurewebsites.net/api/itemapi/" + intent.getStringExtra(ScanActivity.SCANNED_ID);
        //itemUrl = "https://warehouse-is.azurewebsites.net/api/itemapi/16";

        idText = findViewById(R.id.idTextView);
        nameText = findViewById(R.id.nameTextView);
        descText = findViewById(R.id.descriptionTextView);
        quantityText = findViewById(R.id.quantityTextView);
        activeText = findViewById(R.id.activeTextView);
        status = findViewById(R.id.textView);
        status.setText(itemUrl);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        export = findViewById(R.id.button);

        showItem();
    }

    public void scan(View view) {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }

    public void mainMenu(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public  void showItem() {
        JsonObjectRequest request = new JsonObjectRequest(itemUrl, jsonObjectListener, errorListener) {
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("ApiKey", "123kuhaneSalame123");
                return params;
            }
        };
        requestQueue.add(request);
    }

    private Response.Listener<JSONObject> jsonObjectListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response){
            try {
                idText.setText(idText.getText().toString() + response.getInt("id"));
                nameText.setText(nameText.getText().toString() + response.getString("name"));
                descText.setText(descText.getText().toString() + response.getString("description"));
                quantityText.setText(quantityText.getText().toString() + response.getInt("quantity"));

                if(response.getBoolean("active")) {
                    activeText.setText(activeText.getText().toString() + "In the current warehouse");
                }
                else {
                    activeText.setText(activeText.getText().toString() + "Already exported");
                }

                itemID = response.getInt("id");
                name = response.getString("name");
                description = response.getString("description");
                quantity = response.getInt("quantity");
                active = response.getBoolean("active");
                warehouseID = response.getInt("warehouseID");
                customerID = response.getInt("customerID");

                if(!active) {
                    export.setVisibility(View.INVISIBLE);
                }

            } catch (JSONException e){
                e.printStackTrace();
                return;
            }
        }
    };

    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("REST error", error.getMessage());
        }
    };

    public void exportItem(View view){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("itemID", itemID);
            jsonBody.put("warehouseID", warehouseID);
            jsonBody.put("customerID", customerID);
            jsonBody.put("out", currentDateandTime.replace(" ", "T"));

            final String mRequestBody = jsonBody.toString();

            Log.d("JSON object: ", mRequestBody);

            status.setText(mRequestBody);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://warehouse-is.azurewebsites.net/api/evidenceapi", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("LOG_VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("LOG_VOLLEY", error.toString());
                }
            }
            ) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        return null;
                    }
                }
                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        status.setText(responseString);
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }

                @Override
                public Map<String,String> getHeaders() throws AuthFailureError
                {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("ApiKey", "123kuhaneSalame123");
                    return params;
                }

            };

            requestQueue.add(stringRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        changeItemActivitiy();

        Toast.makeText(this, "Succesfully exported item with ID: " + itemID, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);

    }

    public void changeItemActivitiy(){
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("id", itemID);
            jsonBody.put("name", name);
            jsonBody.put("description", description);
            jsonBody.put("quantity", quantity);
            jsonBody.put("active", false);
            jsonBody.put("warehouseID", warehouseID);
            jsonBody.put("customerID", customerID);

            final String mRequestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.PUT, itemUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("LOG_VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("LOG_VOLLEY", error.toString());
                }
            }
            ) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        return null;
                    }
                }
                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        status.setText(responseString);
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }

                @Override
                public Map<String,String> getHeaders() throws AuthFailureError
                {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("ApiKey", "123kuhaneSalame123");
                    return params;
                }

            };

            requestQueue.add(stringRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}