package com.cyxtera.carlostorres.loginapp.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cyxtera.carlostorres.loginapp.R;
import com.cyxtera.carlostorres.loginapp.controller.remote.Controller;
import com.cyxtera.carlostorres.loginapp.model.adapters.InfoLocationAdapter;
import com.cyxtera.carlostorres.loginapp.model.pojo.InfoLocation;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity implements InfoLocationAdapter.ClickListener, Controller.CallbackListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<InfoLocation> infoLocations = new ArrayList<>();
    private InfoLocationAdapter infoLocationAdapter = new InfoLocationAdapter(infoLocations, this);
    private Controller controller;
    private InfoLocation currentInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        controller = new Controller(getApplicationContext(), this);
        controller.setCallbacks(ListActivity.this);
        configViews();
        currentInfo = (InfoLocation) getIntent().getSerializableExtra("info");
        controller.startFetching(currentInfo);
    }

    private void configViews() {
        RecyclerView recyclerView = this.findViewById(R.id.list);
        swipeRefreshLayout = this.findViewById(R.id.swipe);

        recyclerView.setLayoutManager(new LinearLayoutManager(ListActivity.this));
        recyclerView.setAdapter(infoLocationAdapter);

        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.colorPrimaryDark));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                controller.startFetching(currentInfo);
            }
        });
    }

    @Override
    public void clicked(InfoLocation infoLocation) {
        Intent i = new Intent(this, ListActivity.class);
        i.putExtra("infoLocation", infoLocation);
        startActivity(i);
    }

    @Override
    public void onFetchProgress(InfoLocation location) {

    }

    @Override
    public void onFetchComplete() {

    }
}
