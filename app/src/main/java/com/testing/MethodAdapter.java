package com.testing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MethodAdapter extends RecyclerView.Adapter<MethodAdapter.MethodViewHolder> {

    private Context context;
    private List<DietMethod> dietMethods;
    private OnMethodClickListener onMethodClickListener;

    public interface OnMethodClickListener {
        void onMethodClick(DietMethod dietMethod);
    }

    public MethodAdapter(Context context, List<DietMethod> dietMethods, OnMethodClickListener onMethodClickListener) {
        this.context = context;
        this.dietMethods = dietMethods;
        this.onMethodClickListener = onMethodClickListener;
    }

    @NonNull
    @Override
    public MethodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_method, parent, false);
        return new MethodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MethodViewHolder holder, int position) {
        DietMethod dietMethod = dietMethods.get(position);
        holder.tvMethodName.setText(dietMethod.getName());
        holder.tvMethodDescription.setText(dietMethod.getDescription());

        // Check if imgURL is not empty and load the image using Picasso
        if (dietMethod.getImageUrl() != null && !dietMethod.getImageUrl().isEmpty()) {
            Picasso.get().load(dietMethod.getImageUrl()).into(holder.imgBackground);
        } else {
            // Load a default image if imgURL is empty
            Picasso.get().load(R.drawable.thumbfragdiary).into(holder.imgBackground);
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMethodClickListener.onMethodClick(dietMethod);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dietMethods.size();
    }

    static class MethodViewHolder extends RecyclerView.ViewHolder {
        TextView tvMethodName, tvMethodDescription;
        ImageView imgBackground;
        CardView cardView;

        MethodViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMethodName = itemView.findViewById(R.id.tvMethodName);
            tvMethodDescription = itemView.findViewById(R.id.tvMethodDescription);
            imgBackground = itemView.findViewById(R.id.imgBackground);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
