package com.cyxtera.carlostorres.loginapp.model.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cyxtera.carlostorres.loginapp.R;
import com.cyxtera.carlostorres.loginapp.model.pojo.InfoLocation;

import java.util.List;

public class InfoLocationAdapter extends RecyclerView.Adapter<InfoLocationAdapter.Holder> {
    private List<InfoLocation> infoLocations;
    private ClickListener clickListener;
    private Context context;

    public InfoLocationAdapter(List<InfoLocation> infoLocations, ClickListener clickListener) {
        this.clickListener = clickListener;
        this.infoLocations = infoLocations;
    }

    public void addAllInfoLocations(List<InfoLocation> infoLocations) {
        this.infoLocations.clear();
        this.infoLocations.addAll(infoLocations);
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
        context = parent.getContext();
        return new Holder(row);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        final InfoLocation currentInfoLocation = infoLocations.get(position);
        holder.date.setText(currentInfoLocation.getTime());
        holder.location.setText(currentInfoLocation.getLat().toString() + "," + currentInfoLocation.getLng().toString());
        holder.status.setText(currentInfoLocation.getStatus());
        if (currentInfoLocation.getStatus().equals("Exitoso")) {
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.green));
        } else {
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.red));
        }

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.clicked(currentInfoLocation);
            }
        });
    }

    @Override
    public int getItemCount() {
        return infoLocations.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private TextView date, status, location;
        private ConstraintLayout item;

        public Holder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            status = itemView.findViewById(R.id.status);
            location = itemView.findViewById(R.id.location);
            item = itemView.findViewById(R.id.layout);
        }
    }

    public interface ClickListener {
        void clicked(InfoLocation infoLocation);
    }
}


