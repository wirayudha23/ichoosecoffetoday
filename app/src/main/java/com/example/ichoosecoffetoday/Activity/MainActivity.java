package com.example.ichoosecoffetoday.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ichoosecoffetoday.R;

public class MainActivity extends AppCompatActivity {

    private RecyclerView.Adapter adapterPopular;

    private RecyclerView recyclerViewPopular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initRecyclerView();
    }

    private void initRecyclerView() {
    }
}
