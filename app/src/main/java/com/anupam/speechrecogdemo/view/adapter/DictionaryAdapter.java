package com.anupam.speechrecogdemo.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anupam.speechrecogdemo.R;
import com.anupam.speechrecogdemo.model.SpeechTextmodel;

import java.util.List;

public class DictionaryAdapter extends RecyclerView.Adapter<setViewHolder> {
    private Context context;
    String choosenValue;

    List<SpeechTextmodel> dataitems;

    public DictionaryAdapter(Context ctx, List<SpeechTextmodel> dataitems, String choosenValue) {
        this.context = ctx;
        this.dataitems = dataitems;
        this.choosenValue = choosenValue;
    }

    @NonNull
    @Override
    public setViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dictionary_text_list, viewGroup, false);
        return new setViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull setViewHolder holder, int position) {
        if (dataitems.get(holder.getAdapterPosition()).getSpeechText().equals(choosenValue)) {
            holder.itemView.setBackgroundColor(Color.parseColor("#c5e1a5"));
        }
        holder.txtSpeech.setText(dataitems.get(holder.getAdapterPosition()).getSpeechText());
        holder.txtFrequent.setText(dataitems.get(holder.getAdapterPosition()).getFrequent());
    }

    @Override
    public int getItemCount() {
        return dataitems.size();
    }
}


class setViewHolder extends RecyclerView.ViewHolder {
    TextView txtSpeech;
    TextView txtFrequent;

    setViewHolder(View itemView) {
        super(itemView);
        txtSpeech = itemView.findViewById(R.id.speech_text);
        txtFrequent = itemView.findViewById(R.id.frequent);
    }
}