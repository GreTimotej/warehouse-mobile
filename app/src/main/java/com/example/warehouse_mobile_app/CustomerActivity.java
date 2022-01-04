package com.example.warehouse_mobile_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CustomerActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private EditText customerID;
    private TextView customers;
    private Button confirmID;
    private String url = "https://warehouse-is.azurewebsites.net/api/customerapi/";
    private String itemName;
    private String description;
    private String quantity;
    private String warehouse;
    private boolean wh_ok;
    private boolean cs_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        Intent intent = getIntent();
        boolean fromMain = intent.getBooleanExtra("IS_FROM_MAINMENU", false);
        itemName = intent.getStringExtra("ITEM_NAME");
        description = intent.getStringExtra("ITEM_DESCRIPTION");
        quantity = intent.getStringExtra("ITEM_QUANTITY");
        warehouse = intent.getStringExtra("ITEM_WAREHOUSE_ID");
        wh_ok = intent.getBooleanExtra("WAREHOUSE_OK", false);
        cs_ok = intent.getBooleanExtra("CUSTOMER_OK", false);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        customerID = (EditText) findViewById(R.id.customereID);
        customers = (TextView) findViewById(R.id.customersTextView);
        confirmID = (Button) findViewById(R.id.confirmCustomer);

        if(fromMain) {
            customerID.setVisibility(View.INVISIBLE);
            confirmID.setVisibility(View.INVISIBLE);
        }

        showCustomers();
    }

    public  void showCustomers(){
        JsonArrayRequest request = new JsonArrayRequest(url, jsonArrayListener, errorListener);
        requestQueue.add(request);
    }

    private Response.Listener<JSONArray> jsonArrayListener = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response){
            ArrayList<String> data = new ArrayList<>();

            for (int i = 0; i < response.length(); i++){
                try {
                    JSONObject object =response.getJSONObject(i);
                    int id = object.getInt("id");
                    String fName = object.getString("firstName");
                    String lName = object.getString("lastName");
                    String address = object.getString("address");
                    String city = object.getString("city");
                    String country = object.getString("country");

                    data.add(id + " - " + fName + ", " + lName + ", " + address + ", " + city + ", " + country);

                } catch (JSONException e){
                    e.printStackTrace();
                    return;

                }
            }

            for (String row: data){
                String currentText = customers.getText().toString();
                customers.setText(currentText + "\n\n" + row);
            }

        }

    };

    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("REST error", error.getMessage());
        }
    };

    public void selectCustomer(View view) {
        String id = customerID.getText().toString();
        Intent myIntent = new Intent(this, AddNewItemActivity.class);
        myIntent.putExtra("ITEM_CUSTOMER_ID", id);
        myIntent.putExtra("ITEM_NAME", itemName);
        myIntent.putExtra("ITEM_DESCRIPTION", description);
        myIntent.putExtra("ITEM_QUANTITY", quantity);
        myIntent.putExtra("ITEM_WAREHOUSE_ID", warehouse);
        myIntent.putExtra("IS_FROM_MAINMENU", false);
        myIntent.putExtra("CUSTOMER_OK", true);
        myIntent.putExtra("WAREHOUSE_OK", wh_ok);
        startActivity(myIntent);
    }

    public void backMain(View view) {
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}