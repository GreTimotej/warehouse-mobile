package com.example.warehouse_mobile_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.util.Log;
import android.view.View;
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

public class MainActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private TextView warehouses;
    private EditText id;
    private String url = "https://warehouse-is.azurewebsites.net/api/";
    private int currentWarehouse = 0;
    public static final String EXTRA_NUMBER = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        id = (EditText) findViewById(R.id.warehouseInput);
        warehouses = (TextView) findViewById(R.id.warehouseList);
        showWarehouses();
    }

    public void selectWarehouse(View view) {
        if (view != null) {
            currentWarehouse = Integer.parseInt(id.getText().toString().split(" ")[2]);
            warehouses.setText("Current warehouse: " + currentWarehouse);
            Intent intent = new Intent(this, ScanActivity.class);
            getIntent().putExtra(EXTRA_NUMBER, currentWarehouse);
            startActivity(intent);
        }
    }

    public  void showWarehouses() {
        JsonArrayRequest request = new JsonArrayRequest(url + "warehouseapi", jsonArrayListener, errorListener);
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

            //warehouses.setText("");

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


}