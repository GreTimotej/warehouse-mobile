package com.example.warehouse_mobile_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class AddNewItemActivity extends AppCompatActivity {

    private EditText itemName;
    private EditText itemDesc;
    private EditText itemQuan;
    private EditText itemWare;
    private EditText itemCust;

    private RequestQueue requestQueue;
    private String url = "https://warehouse-is.azurewebsites.net/api/itemapi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        itemName = (EditText) findViewById(R.id.itemName);
        itemDesc = (EditText) findViewById(R.id.itemDesc);
        itemQuan = (EditText) findViewById(R.id.itemQantity);
        itemWare = (EditText) findViewById(R.id.itemWarehouse);
        itemCust = (EditText) findViewById(R.id.itemCustomer);
    }

    public void addItemFin(View view) {
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("name", itemName.getText().toString());
            jsonBody.put("description", itemDesc.getText().toString());
            jsonBody.put("quantity", Integer.parseInt(itemQuan.getText().toString()));
            jsonBody.put("warehouseID", Integer.parseInt(itemWare.getText().toString()));
            jsonBody.put("customerID", Integer.parseInt(itemCust.getText().toString()));

            final String mRequestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
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
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }

            };

            requestQueue.add(stringRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Toast.makeText(this, "Added item with name: " + itemName.getText().toString(), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
}