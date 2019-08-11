package com.cyxtera.carlostorres.loginapp.view;

import android.os.Bundle;
import android.widget.Toast;

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

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListActivity extends AppCompatActivity implements InfoLocationAdapter.ClickListener, Controller.ListCallbackListener {

    @BindView(R.id.list)
    RecyclerView recyclerView;

    @BindView(R.id.swipe)
    SwipeRefreshLayout swipeRefreshLayout;

    private List<InfoLocation> infoLocations = new ArrayList<>();
    private InfoLocationAdapter infoLocationAdapter = new InfoLocationAdapter(infoLocations, this);
    private Controller controller;
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);

        controller = new Controller(getApplicationContext(), this);
        controller.setListCallbacks(ListActivity.this);
        configViews();
        currentUser = getIntent().getStringExtra("email");
        controller.getListOfInfoLocations(currentUser);
    }

    private void configViews() {
        recyclerView.setLayoutManager(new LinearLayoutManager(ListActivity.this));
        recyclerView.setAdapter(infoLocationAdapter);

        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.colorPrimaryDark));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                controller.getListOfInfoLocations(currentUser);
            }
        });
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void clicked(final InfoLocation infoLocation) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ListActivity.this, "Informacion: " + infoLocation.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onListProgress(List<InfoLocation> infoLocations) {
        infoLocationAdapter.addAllInfoLocations(infoLocations);
    }

    @Override
    public void onListComplete() {
        swipeRefreshLayout.setRefreshing(false);
    }
}
