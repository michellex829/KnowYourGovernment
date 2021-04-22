package com.michelleweixu.knowyourgovernment;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

public class OfficialAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private static final String TAG = "StockAdapter";
    public List<Official> officialList;
    private MainActivity mainAct;

    OfficialAdapter(List<Official> offList, MainActivity ma) {
        this.officialList = offList;
        mainAct = ma;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW MyViewHolder");

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.official_entry, parent, false);

        itemView.setOnClickListener(mainAct);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: FILLING VIEW HOLDER Stock    " + position);

        Official off = officialList.get(position);

        holder.office.setText(off.office);
        holder.name.setText(off.name);
        holder.party.setText("(" + off.party + ")");
    }

    @Override
    public int getItemCount() {
        return officialList.size();
    }
}