package com.cyxtera.carlostorres.loginapp.model.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.cyxtera.carlostorres.loginapp.R;
import com.cyxtera.carlostorres.loginapp.model.pojo.InfoLocation;

import java.util.List;

public class InfoLocationAdapter extends RecyclerView.Adapter<InfoLocationAdapter.Holder>
{
    private List<InfoLocation> infoLocations;
    private ClickListener clickListener;

    public InfoLocationAdapter(List<InfoLocation> infoLocations, ClickListener clickListener)
    {
        this.clickListener = clickListener;
        this.infoLocations = infoLocations;
    }

    public void addAllInfoLocations(List<InfoLocation> infoLocations)
    {
        this.infoLocations.clear();
        this.infoLocations.addAll(infoLocations);
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
        return new Holder(row);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position)
    {
        final InfoLocation currentInfoLocation = infoLocations.get(position);
        holder.mName.setText(currentInfoLocation.getTime());
        holder.mdate.setText(currentInfoLocation.getLat().toString());
        holder.mEpisode.setText(currentInfoLocation.getLng().toString());

        holder.mItem.setOnClickListener(new View.OnClickListener() {
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

    class Holder extends RecyclerView.ViewHolder
    {
        private TextView mName, mdate, mEpisode;
        private ConstraintLayout mItem;

        public Holder(View itemView)
        {
            super(itemView);
            mName = itemView.findViewById(R.id.name);
            mdate = itemView.findViewById(R.id.date);
            mEpisode = itemView.findViewById(R.id.episode);
            mItem = itemView.findViewById(R.id.layout);
        }
    }

    public interface ClickListener {
        void clicked(InfoLocation infoLocation);
    }
}


