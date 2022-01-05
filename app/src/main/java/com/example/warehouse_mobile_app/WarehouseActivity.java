package com.example.warehouse_mobile_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WarehouseActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private EditText warehouseID;
    private TextView warehouses;
    private Button confirmID;
    private String url = "https://warehouse-is.azurewebsites.net/api/warehouseapi/";
    private String itemName;
    private String description;
    private String  quantity;
    private String customer;
    private boolean wh_ok;
    private boolean cs_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse);

        Intent intent = getIntent();
        boolean fromMain = intent.getBooleanExtra("IS_FROM_MAINMENU", false);
        boolean toItems = intent.getBooleanExtra("IS_TO_ITEMS", false);
        itemName = intent.getStringExtra("ITEM_NAME");
        description = intent.getStringExtra("ITEM_DESCRIPTION");
        quantity = intent.getStringExtra("ITEM_QUANTITY");
        customer = intent.getStringExtra("ITEM_CUSTOMER_ID");
        wh_ok = intent.getBooleanExtra("WAREHOUSE_OK", false);
        cs_ok = intent.getBooleanExtra("CUSTOMER_OK", false);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        warehouseID = (EditText) findViewById(R.id.customereID);
        warehouses = (TextView) findViewById(R.id.customersTextView);
        confirmID = (Button) findViewById(R.id.confirmCustomer);

        if(fromMain) {
            warehouseID.setVisibility(View.INVISIBLE);
            confirmID.setVisibility(View.INVISIBLE);
        }

        if(toItems) {
            confirmID.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    goToItems();
                }
            });
        }
        else {
            confirmID.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    selectWarehouse();
                }
            });
        }

        showWarehouses();
    }

    public  void showWarehouses(){
        JsonArrayRequest request = new JsonArrayRequest(url, jsonArrayListener, errorListener) {
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

    private Response.Listener<JSONArray> jsonArrayListener = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response){
            ArrayList<String> data = new ArrayList<>();

            for (int i = 0; i < response.length(); i++){
                try {
                    JSONObject object =response.getJSONObject(i);
                    int id = object.getInt("id");
                    String address = object.getString("address");
                    String city = object.getString("city");
                    String country = object.getString("country");

                    data.add(id + " - " + address + ", " + city + ", " + country);

                } catch (JSONException e){
                    e.printStackTrace();
                    return;

                }
            }

            for (String row: data){
                String currentText = warehouses.getText().toString();
                warehouses.setText(currentText + "\n\n" + row);
            }

        }

    };

    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("REST error", error.getMessage());
        }
    };

    public void selectWarehouse() {
        String id = warehouseID.getText().toString();
        Intent myIntent = new Intent(this, AddNewItemActivity.class);
        myIntent.putExtra("ITEM_CUSTOMER_ID", customer);
        myIntent.putExtra("ITEM_NAME", itemName);
        myIntent.putExtra("ITEM_DESCRIPTION", description);
        myIntent.putExtra("ITEM_QUANTITY", quantity);
        myIntent.putExtra("ITEM_WAREHOUSE_ID", id);
        myIntent.putExtra("IS_FROM_MAINMENU", false);
        myIntent.putExtra("WAREHOUSE_OK",true);
        myIntent.putExtra("CUSTOMER_OK", cs_ok);
        startActivity(myIntent);
    }

    public void backMain(View view) {
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void goToItems() {
        String id = warehouseID.getText().toString();
        if (id.split(" ").length == 2) {
            Intent myIntent = new Intent(this, AllItemsActivity.class);
            myIntent.putExtra("SHOW_ALL_ITEMS", true);
            startActivity(myIntent);
        } else {
            Intent myIntent = new Intent(this, AllItemsActivity.class);
            myIntent.putExtra("ITEM_WAREHOUSE_ID", id);
            startActivity(myIntent);
        }
    }
}