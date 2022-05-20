package com.airy.wowcalligraphy.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airy.wowcalligraphy.R;
import com.airy.wowcalligraphy.util.CalligraphyTexts;

public class CalligraphyAdapter extends RecyclerView.Adapter<CalligraphyHolder> {
    private final AdapterClickListener listener;
    private final CalligraphyTexts[] list;

    public CalligraphyAdapter(AdapterClickListener listener, CalligraphyTexts[] list) {
        this.listener = listener;
        this.list = list;
    }

    public interface AdapterClickListener{
        void onItemClickListener(String id);
    }

    @NonNull
    @Override
    public CalligraphyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calligraphy_text, parent, false);
        return new CalligraphyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalligraphyHolder holder, int position) {
        int adapterPosition = holder.getAdapterPosition();
        CalligraphyTexts calligraphyText = list[adapterPosition];
        holder.text.setText(calligraphyText.getText());
        holder.writer.setText(calligraphyText.getWriter());
        holder.title.setText("《"+calligraphyText.getTitle()+"》");
        holder.itemView.setOnClickListener(v -> listener.onItemClickListener(calligraphyText.name()));
    }

    @Override
    public int getItemCount() {
        return list.length;
    }
}

class CalligraphyHolder extends RecyclerView.ViewHolder{
    TextView text;
    TextView writer;
    TextView title;
    public CalligraphyHolder(@NonNull View itemView) {
        super(itemView);
        text = itemView.findViewById(R.id.item_text);
        writer = itemView.findViewById(R.id.item_writer);
        title = itemView.findViewById(R.id.item_title);
    }
}
