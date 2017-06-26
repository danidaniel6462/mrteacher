package com.darwindeveloper.mrteacher.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.darwindeveloper.mrteacher.R;
import com.darwindeveloper.mrteacher.adapters.AsignaturasAdapter;
import com.darwindeveloper.mrteacher.base_de_datos.DataBaseManager;
import com.darwindeveloper.mrteacher.tablas.Asignatura;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Darwin Morocho on 15/3/2017.
 */

public class AsignaturasFragment extends Fragment {


    public static final String CARRERA_ID = "mrteacher.fragments.asignaturas.carrera_id";
    public static final String CARRERA_NOMBRE = "mrteacher.fragments.asignaturas.carrera_nombre";

    private String carrera_id, carrera_nombre;
    private DataBaseManager dataBaseManager;


    private Context context;
    private RecyclerView recyclerViewAsignaturas;
    private AsignaturasAdapter adapter;
    private ArrayList<Asignatura> asignaturas = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        try {
            dataBaseManager = new DataBaseManager(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = LayoutInflater.from(context).inflate(R.layout.fragment_asignaturas, container, false);
        recyclerViewAsignaturas = (RecyclerView) rootView.findViewById(R.id.recyclerview);


        adapter = new AsignaturasAdapter(context, asignaturas);


        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Portrait Mode
            recyclerViewAsignaturas.setLayoutManager(new GridLayoutManager(context,2));
            recyclerViewAsignaturas.setAdapter(adapter);
        } else {
            // Landscape Mode
            recyclerViewAsignaturas.setLayoutManager(new GridLayoutManager(context,3));
            recyclerViewAsignaturas.setAdapter(adapter);
        }


        final FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nuevaAsignatura();
            }
        });


        recyclerViewAsignaturas.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fab.show();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0 || dy < 0 && fab.isShown()) {
                    fab.hide();
                }

            }
        });

        carrera_id = getArguments().getString(CARRERA_ID);
        carrera_nombre = getArguments().getString(CARRERA_NOMBRE);

        String sql = "select * from " + DataBaseManager.T_MATERIAS + " where carrera_id = " + carrera_id;

        new LoadData(sql).execute();
        return rootView;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerViewAsignaturas.setLayoutManager(new GridLayoutManager(context,3));
            recyclerViewAsignaturas.setAdapter(adapter);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerViewAsignaturas.setLayoutManager(new GridLayoutManager(context,2));
            recyclerViewAsignaturas.setAdapter(adapter);
        }

    }


    private class LoadData extends AsyncTask<Void, Void, Void> {
        private String sql;

        public LoadData(String sql) {
            this.sql = sql;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            asignaturas.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            asignaturas.addAll(dataBaseManager.selectAsignaturas(sql));

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (asignaturas.size() > 0) {
                adapter.notifyItemRangeInserted(0, asignaturas.size() - 1);
                adapter.notifyDataSetChanged();
            }
        }
    }


    private void nuevaAsignatura() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Nueva Asignatura " + carrera_nombre);
        alertDialog.setCancelable(false);

        final EditText input = new EditText(context);
        input.setHint("Ejemplo: Programación 2");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("Guardar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String name = input.getText().toString();
                        if (name.trim().length() > 3) {
                            String id = dataBaseManager.insertAsignatura(name, carrera_id);
                            if (!id.equals("-1")) {
                                Asignatura asignatura = new Asignatura(id, name, carrera_id, 0);
                                asignaturas.add(asignatura);
                                adapter.notifyItemInserted(asignaturas.size() - 1);
                                adapter.notifyDataSetChanged();

                                Toast.makeText(context, "GUARDADO", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "No se pudo crear el dato", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(context, "Nombre no valido", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        alertDialog.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }

}
