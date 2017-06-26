package com.darwindeveloper.mrteacher.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.darwindeveloper.mrteacher.tablas.Asignatura;

import java.util.ArrayList;

/**
 * Created by Darwin Morocho on 18/3/2017.
 */

public class AsignaturaSpinnerAdapter extends ArrayAdapter<Asignatura> {
    private Context context;
    private ArrayList<Asignatura> asignaturas;

    public AsignaturaSpinnerAdapter(@NonNull Context context, @LayoutRes int resource, ArrayList<Asignatura> asignaturas) {
        super(context, resource, asignaturas);
        this.context = context;
        this.asignaturas = asignaturas;
    }


    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Asignatura asignatura = asignaturas.get(position);

        LayoutInflater inflater = LayoutInflater.from(context);
        View item = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        TextView nombre_asignatura = (TextView) item.findViewById(android.R.id.text1);
        nombre_asignatura.setText(asignatura.getNombre());

        return item;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Asignatura asignatura = asignaturas.get(position);

        View item = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        TextView nombre_asignatura = (TextView) item.findViewById(android.R.id.text1);
        nombre_asignatura.setText(asignatura.getNombre());

        return item;
    }


}
