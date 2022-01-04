package com.example.warehouse_mobile_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private Button itemWare;
    private Button itemCust;
    public static String ITEM_CUSTOMER_ID = "Choose customer";
    public static String ITEM_WAREHOUSE_ID = "Choose warehouse";
    public static String ITEM_QUANTITY = "Quantity: ";
    public static String ITEM_DESCRIPTION = "Description: ";
    public static String ITEM_NAME = "Name:";
    public static boolean WAREHOUSE_OK;
    public static boolean CUSTOMER_OK;

    private RequestQueue requestQueue;
    private String url = "https://warehouse-is.azurewebsites.net/api/itemapi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);

        Intent intent = getIntent();
        Boolean from = intent.getBooleanExtra("IS_FROM_MAINMENU", true);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        itemName = (EditText) findViewById(R.id.itemName);
        itemDesc = (EditText) findViewById(R.id.itemDesc);
        itemQuan = (EditText) findViewById(R.id.itemQantity);
        itemWare = (Button) findViewById(R.id.chooseWarehouse);
        itemCust = (Button) findViewById(R.id.chooseCustomer);

        if(!from) {
            itemName.setText(intent.getStringExtra("ITEM_NAME"));
            itemDesc.setText(intent.getStringExtra("ITEM_DESCRIPTION"));
            itemQuan.setText(intent.getStringExtra("ITEM_QUANTITY"));
            itemWare.setText(intent.getStringExtra("ITEM_WAREHOUSE_ID"));
            itemCust.setText(intent.getStringExtra("ITEM_CUSTOMER_ID"));
            if (intent.getBooleanExtra("WAREHOUSE_OK", false)) {
                itemWare.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.appGreen));
                WAREHOUSE_OK = true;
            }
            if (intent.getBooleanExtra("CUSTOMER_OK", false)) {
                itemCust.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.appGreen));
                CUSTOMER_OK = true;
            }
        }
    }

    public void addItemFin(View view) {
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("name", itemName.getText().toString().split(" ")[1]);
            jsonBody.put("description", itemDesc.getText().toString().split(" ", 2)[1]);
            jsonBody.put("quantity", Integer.parseInt(itemQuan.getText().toString().split(" ")[1]));
            jsonBody.put("warehouseID", Integer.parseInt(itemWare.getText().toString().split(" ")[2]));
            jsonBody.put("customerID", Integer.parseInt(itemCust.getText().toString().split(" ")[2]));

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

    public void openWarehouseSelection(View view) {

        String quantity = itemQuan.getText().toString();
        String name = itemName.getText().toString();
        String description = itemDesc.getText().toString();
        String warehouse = itemWare.getText().toString();
        String customer = itemCust.getText().toString();

        Intent myIntent = new Intent(this, WarehouseActivity.class);
        myIntent.putExtra("ITEM_CUSTOMER_ID", customer);
        myIntent.putExtra("ITEM_NAME", name);
        myIntent.putExtra("ITEM_DESCRIPTION", description);
        myIntent.putExtra("ITEM_QUANTITY", quantity);
        myIntent.putExtra("ITEM_WAREHOUSE_ID", warehouse);
        myIntent.putExtra("WAREHOUSE_OK", WAREHOUSE_OK);
        myIntent.putExtra("CUSTOMER_OK", CUSTOMER_OK);
        startActivity(myIntent);
    }

    public void openCustomerSelection(View view) {

        String quantity = itemQuan.getText().toString();
        String name = itemName.getText().toString();
        String description = itemDesc.getText().toString();
        String warehouse = itemWare.getText().toString();
        String customer = itemCust.getText().toString();

        Intent myIntent = new Intent(this, CustomerActivity.class);
        myIntent.putExtra("ITEM_CUSTOMER_ID", customer);
        myIntent.putExtra("ITEM_NAME", name);
        myIntent.putExtra("ITEM_DESCRIPTION", description);
        myIntent.putExtra("ITEM_QUANTITY", quantity);
        myIntent.putExtra("ITEM_WAREHOUSE_ID", warehouse);
        myIntent.putExtra("WAREHOUSE_OK", WAREHOUSE_OK);
        myIntent.putExtra("CUSTOMER_OK", CUSTOMER_OK);
        startActivity(myIntent);
    }

    public void backMain(View view) {
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}