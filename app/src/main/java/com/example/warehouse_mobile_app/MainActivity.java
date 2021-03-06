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
    public static Boolean IS_FROM_MAINMENU = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void addItem(View view) {
        Intent intent = new Intent(this, AddNewItemActivity.class);
        intent.putExtra("IS_FROM_MAINMENU", true);
        startActivity(intent);
    }

    public void scanItem(View view) {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }

    public void openWarehouse(View view) {
        Intent intent = new Intent(this, WarehouseActivity.class);
        intent.putExtra("IS_FROM_MAINMENU", true);
        startActivity(intent);
    }

    public void openCustomer(View view) {
        Intent intent = new Intent(this, CustomerActivity.class);
        intent.putExtra("IS_FROM_MAINMENU", true);
        startActivity(intent);
    }

    public void openItems(View view) {
        Intent intent = new Intent(this, WarehouseActivity.class);
        intent.putExtra("IS_TO_ITEMS", true);
        startActivity(intent);
    }
}