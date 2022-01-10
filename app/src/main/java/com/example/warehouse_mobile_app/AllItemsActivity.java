package com.example.warehouse_mobile_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class AllItemsActivity extends AppCompatActivity {

    String warehouse;
    private String url = "https://warehouse-is.azurewebsites.net/api/itemapi/";
    private RequestQueue requestQueue;
    TextView items;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_items);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        items = (TextView) findViewById(R.id.customersTextView);

        intent = getIntent();

        warehouse = intent.getStringExtra("ITEM_WAREHOUSE_ID");

        showItems();
    }

    public void backMain(View view) {
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public  void showItems(){
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
                    String name = object.getString("name");
                    String description = object.getString("description");
                    int quantity = object.getInt("quantity");
                    int ware = object.getInt("warehouseID");
                    boolean act = object.getBoolean("active");

                    if(!intent.getBooleanExtra("SHOW_ALL_ITEMS", false)) {
                        if (ware == Integer.parseInt(warehouse.split(" ")[2]) && act) {
                            data.add("Name: "+name + "\nDescription:" + description + "\nQuantity: " + quantity);
                        }
                    } else {
                        data.add("Name: "+name + "\nDescription:" + description + "\nQuantity: " + quantity);
                    }

                } catch (JSONException e){
                    e.printStackTrace();
                    return;

                }
            }

            for (String row: data){
                String currentText = items.getText().toString();
                items.setText(currentText + "\n\n" + row);
            }

        }

    };

    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("REST error", error.getMessage());
        }
    };
}