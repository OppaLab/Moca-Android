package com.oppalab.moca.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.oppalab.moca.R;

import java.util.List;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {

    Context context;
    List<Integer> colorList;
    ColorSelectedListener colorSelectedListener;

    public ColorAdapter(Context context, List<Integer> colorList, ColorSelectedListener colorSelectedListener) {
        this.context = context;
        this.colorList = colorList;
        this.colorSelectedListener = colorSelectedListener;
    }

    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.color_item, parent, false);
        return new ColorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
        holder.cardView.setCardBackgroundColor(colorList.get(position));
    }

    @Override
    public int getItemCount() {
        return colorList.size();
    }

    public class ColorViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        public ColorViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.color_section);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    colorSelectedListener.onColorSelectedListener(colorList.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface ColorSelectedListener {
        void onColorSelectedListener(int color);
    }
}
