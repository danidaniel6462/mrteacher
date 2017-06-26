package com.darwindeveloper.mrteacher.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.darwindeveloper.mrteacher.CarreraActivity;
import com.darwindeveloper.mrteacher.EstudiantesActivity;
import com.darwindeveloper.mrteacher.R;
import com.darwindeveloper.mrteacher.adapters.CarrerasAdapter;
import com.darwindeveloper.mrteacher.base_de_datos.DataBaseManager;
import com.darwindeveloper.mrteacher.tablas.Carrera;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Darwin Morocho on 12/3/2017.
 */

public class CarrerasDialogFragment extends DialogFragment implements CarrerasAdapter.OnCarreraClickListener {

    //para obtener datos desde la actividad dashboard
    public static final String U_ID = "mrteacher.u_id";
    public static final String U_N = "mrteacher.u_n";


    private Context context;
    private View rootView;
    private ArrayList<Carrera> carreras = new ArrayList<>();
    private RecyclerView recyclerViewCarreras;
    private ImageView noData;
    private Button btn_nuevo, btn_cerrar;
    private CarrerasAdapter carrerasAdapter;
    private DataBaseManager dataBaseManager;

    private String universidad_id, universidad_nombre;

    static CarrerasDialogFragment newInstance(String universidad_id, String universidad_nombre) {
        CarrerasDialogFragment f = new CarrerasDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString(U_ID, universidad_id);
        args.putString(U_N, universidad_nombre);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        try {
            dataBaseManager = new DataBaseManager(context);
        } catch (IOException e) {
            e.printStackTrace();
        }

        universidad_id = getArguments().getString(U_ID);
        universidad_nombre = getArguments().getString(U_N);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        rootView = LayoutInflater.from(context).inflate(R.layout.dialog_fragment_carreras, container, false);

        recyclerViewCarreras = (RecyclerView) rootView.findViewById(R.id.recyclerviewCarreras);
        TextView textViewUniversidad = (TextView) rootView.findViewById(R.id.textViewUniversidad);
        noData = (ImageView) rootView.findViewById(R.id.imageViewNoData);
        carrerasAdapter = new CarrerasAdapter(context, carreras);
        recyclerViewCarreras.setLayoutManager(new LinearLayoutManager(context));
        carrerasAdapter.setOnCarreraClickListener(this);
        recyclerViewCarreras.setAdapter(carrerasAdapter);
        textViewUniversidad.setText(universidad_nombre);

        btn_nuevo = (Button) rootView.findViewById(R.id.btn_nuevo);
        btn_cerrar = (Button) rootView.findViewById(R.id.btn_cerrar);


        btn_nuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nuevaCarrera();
            }
        });

        btn_cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        new LoadData().execute();

        return rootView;
    }

    @Override
    public void onCarreraClickName(final Carrera carrera, int position) {
        Intent intent = new Intent(context, CarreraActivity.class);
        intent.putExtra(CarreraActivity.CARRERA_ID, carrera.getId());
        intent.putExtra(CarreraActivity.CARRERA_NOMBRE, carrera.getNombre());
        startActivity(intent);
    }

    @Override
    public void onCarreraClickMenu(Carrera carrera, int position) {

    }

    @Override
    public void onCarreraClickStudents(Carrera carrera, int position) {
        Intent intent = new Intent(context, EstudiantesActivity.class);
        intent.putExtra(EstudiantesActivity.OPCION, EstudiantesActivity.CARRERA);
        intent.putExtra(EstudiantesActivity.CARRERA_ID, carrera.getId());
        intent.putExtra(EstudiantesActivity.CARRERA_NOMBRE, carrera.getNombre());
        startActivity(intent);
    }


    private class LoadData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            carreras.clear();
            recyclerViewCarreras.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            String sql = "select * from " + DataBaseManager.T_CARRERAS + " where institucion_id=" + universidad_id;
            carreras.addAll(dataBaseManager.selectCarreras(sql));
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (carreras.size() > 0) {
                recyclerViewCarreras.setVisibility(View.VISIBLE);
                noData.setVisibility(View.GONE);
                carrerasAdapter.notifyItemRangeInserted(0, carreras.size() - 1);
                carrerasAdapter.notifyDataSetChanged();
            }


        }
    }


    private void nuevaCarrera() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Nueva Carrera " + universidad_nombre);
        alertDialog.setCancelable(false);

        final EditText input = new EditText(context);
        input.setHint("Ejemplo: Ingeniería en Computación Gráfica");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("Guardar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String new_name = input.getText().toString();
                        if (new_name.trim().length() > 3) {
                            String id = dataBaseManager.insertCarrera(new_name, universidad_id);
                            if (!id.equals("-1")) {
                                Carrera tmp = new Carrera(id, new_name, universidad_id);
                                carreras.add(tmp);
                                noData.setVisibility(View.GONE);
                                recyclerViewCarreras.setVisibility(View.VISIBLE);
                                carrerasAdapter.notifyItemInserted(carreras.size() - 1);
                                carrerasAdapter.notifyDataSetChanged();
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
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.dialog_slide_animation;
    }
}
