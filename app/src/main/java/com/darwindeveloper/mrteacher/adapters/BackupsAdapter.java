package com.darwindeveloper.mrteacher.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.darwindeveloper.mrteacher.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Darwin Morocho on 16/3/2017.
 */

public class BackupsAdapter extends RecyclerView.Adapter<BackupsAdapter.BViewHolder> {


    private Context context;
    private ArrayList<File> files;

    public BackupsAdapter(Context context, ArrayList<File> files) {
        this.context = context;
        this.files = files;
    }

    @Override
    public BViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_backup, parent, false);
        return new BViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BViewHolder holder, int position) {
        File file = files.get(position);
        holder.nombre.setText(file.getName());

        Date date = new Date(file.lastModified());
        holder.fecha.setText(date.toString());
        int file_size = Integer.parseInt(String.valueOf(file.length() / 1024));

        if (file_size >= 1024) {
            holder.tam.setText((file_size / 1024) + " MB");
        } else {
            holder.tam.setText(file_size + " KB");
        }


    }

    @Override
    public int getItemCount() {
        return files.size();
    }


    public class BViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, fecha, tam;
        LinearLayout content;

        public BViewHolder(View itemView) {
            super(itemView);
            content = (LinearLayout) itemView.findViewById(R.id.item_content);
            nombre = (TextView) itemView.findViewById(R.id.textViewName);
            fecha = (TextView) itemView.findViewById(R.id.textViewDate);
            tam = (TextView) itemView.findViewById(R.id.textViewSize);
        }
    }
}
