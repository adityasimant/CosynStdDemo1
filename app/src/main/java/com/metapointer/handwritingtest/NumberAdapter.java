package com.metapointer.handwritingtest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NumberAdapter extends RecyclerView.Adapter<NumberAdapter.NumberViewHolder> {

    private List<Integer> numbers;
    private OnNumberClickListener listener;
    private int selectedPosition = 0; // To keep track of the selected position
    private Context context;

    public interface OnNumberClickListener {
        void onNumberClick(int number);
    }

    public NumberAdapter(Context context, List<Integer> numbers, OnNumberClickListener listener) {
        this.context = context;
        this.numbers = numbers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NumberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_number, parent, false);
        return new NumberViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull NumberViewHolder holder, int position) {
        holder.tvNumber.setText(String.valueOf(numbers.get(position)));

        if (position == selectedPosition) {
            holder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.pagebackground));
            holder.tvNumber.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            holder.tvNumber.setTextColor(R.color.pageNumberBg);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNumberClick(numbers.get(position));
                setSelectedPosition(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return numbers.size();
    }

    public void setSelectedPosition(int position) {
        int previousPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(previousPosition);
        notifyItemChanged(selectedPosition);
    }

    public static class NumberViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumber;

        public NumberViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tvNumber);
        }
    }
}