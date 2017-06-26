package com.darwindeveloper.mrteacher.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.darwindeveloper.mrteacher.R;
import com.darwindeveloper.mrteacher.tablas.Asignatura;

import java.util.ArrayList;

/**
 * Created by Darwin Morocho on 15/3/2017.
 */

public class AsignaturasAdapter extends RecyclerView.Adapter<AsignaturasAdapter.AViewHolder> {

    private Context context;
    private ArrayList<Asignatura> asignaturas;

    public AsignaturasAdapter(Context context, ArrayList<Asignatura> asignaturas) {
        this.context = context;
        this.asignaturas = asignaturas;
    }

    @Override
    public AViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_asignatura, parent, false);
        return new AViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AViewHolder holder, int position) {

        Asignatura asignatura = asignaturas.get(position);
        holder.nombre.setText(asignatura.getNombre());
        holder.estudiantes.setText("Estudiantes: " + asignatura.getNumerp_estudiantes());

    }

    @Override
    public int getItemCount() {
        return asignaturas.size();
    }

    public class AViewHolder extends RecyclerView.ViewHolder {

        LinearLayout item_content;
        TextView nombre, estudiantes;

        public AViewHolder(View itemView) {
            super(itemView);
            item_content = (LinearLayout) itemView.findViewById(R.id.item_content);
            nombre = (TextView) itemView.findViewById(R.id.textViewName);
            estudiantes = (TextView) itemView.findViewById(R.id.textViewNum);
        }
    }

}
