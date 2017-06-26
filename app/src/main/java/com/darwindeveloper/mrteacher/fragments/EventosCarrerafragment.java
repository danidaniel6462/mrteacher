package com.darwindeveloper.mrteacher.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.darwindeveloper.mrteacher.R;
import com.darwindeveloper.mrteacher.adapters.AsignaturaSpinnerAdapter;
import com.darwindeveloper.mrteacher.adapters.AsignaturasAdapter;
import com.darwindeveloper.mrteacher.adapters.EventosAdapter;
import com.darwindeveloper.mrteacher.base_de_datos.DataBaseManager;
import com.darwindeveloper.mrteacher.tablas.Asignatura;
import com.darwindeveloper.mrteacher.tablas.Evento;
import com.darwindeveloper.mrteacher.utils.Extras;

import java.io.IOException;
import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Darwin Morocho on 16/3/2017.
 */

public class EventosCarrerafragment extends Fragment implements EventosAdapter.OnEventClickListener {

    private Extras extras;


    public static final String CARRERA_ID = "mrteacher.eventos.carrera.carrera_id";
    public static final String CARRERA_NOMBRE = "mrteacher.eventos.carrera.carrera_nombre";


    private Context context;
    private DataBaseManager dataBaseManager;
    private RecyclerView recyclerViewEventos;
    private EventosAdapter eventosAdapter;
    private ArrayList<Evento> eventos = new ArrayList<>();
    private ArrayList<Asignatura> materias = new ArrayList<>();
    private Spinner spinner;


    private String carrera_id, carrera_nombre, materia_id, materia_nombre;
    private String sql;


    private LoadEventos loadEventos;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();
        try {
            dataBaseManager = new DataBaseManager(context);
        } catch (IOException e) {
            e.printStackTrace();
        }

        extras = new Extras(context);


        Bundle args = getArguments();

        carrera_id = args.getString(CARRERA_ID);
        carrera_nombre = args.getString(CARRERA_NOMBRE);


    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = LayoutInflater.from(context).inflate(R.layout.fragment_eventos_carrera, container, false);
        recyclerViewEventos = (RecyclerView) rootView.findViewById(R.id.recyclerviewEventos);
        spinner = (Spinner) rootView.findViewById(R.id.spinner);
        recyclerViewEventos.setLayoutManager(new LinearLayoutManager(context));
        eventosAdapter = new EventosAdapter(context, eventos);
        eventosAdapter.setOnEventClickListener(this);
        recyclerViewEventos.setAdapter(eventosAdapter);

        Button btn_nuevo = (Button) rootView.findViewById(R.id.btn_nuevo);


        AsignaturaSpinnerAdapter adapter = new AsignaturaSpinnerAdapter(context, android.R.layout.simple_spinner_dropdown_item, materias);
        spinner.setAdapter(adapter);

        btn_nuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString(BottomSheetNuevoEvento.CARRERA_ID, carrera_id);
                args.putString(BottomSheetNuevoEvento.CARRERA_NOMBRE, carrera_nombre);
                args.putString(BottomSheetNuevoEvento.MATERIA_ID, materia_id);
                args.putString(BottomSheetNuevoEvento.MATERIA_NOMBRE, materia_nombre);

                BottomSheetDialogFragment bottomSheet = BottomSheetNuevoEvento.newInstance(args);


                bottomSheet.show(getActivity().getSupportFragmentManager(), "BSDialog");
            }
        });

        new LoadMaterias().execute();


        return rootView;
    }

    @Override
    public void onEventWatch(Evento evento, int position) {

    }

    @Override
    public void onEventEdit(Evento evento, int position) {

    }

    @Override
    public void onEventDelete(final Evento evento, final int position) {
        AlertDialog.Builder builder = extras.createDialog(R.drawable.ic_delete_forever_black_24dp, "¿Eliminar evento?",evento.getNombre(), false);
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int result = dataBaseManager.deleteEvento(evento.getId());
                if (result > 0) {
                    dialog.dismiss();
                    eventos.remove(position);
                    eventosAdapter.notifyItemRemoved(position);
                    eventosAdapter.notifyItemRangeChanged(position, eventosAdapter.getItemCount());
                    Toast.makeText(context, "Evento eliminado", Toast.LENGTH_SHORT).show();

                    onEventosCarreraChangeListener.onEventosCarreraChange();
                }
            }
        });
        builder.create().show();
    }

    @Override
    public void onEventCalendar(Evento evento) {

    }


    private class LoadMaterias extends AsyncTask<Void, Void, Void> implements AdapterView.OnItemSelectedListener {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            materias.clear();
            materias.add(new Asignatura("-1", "Todos los eventos", carrera_id, 0));
        }

        @Override
        protected Void doInBackground(Void... params) {

            materias.addAll(dataBaseManager.selectAsignaturas("select * from " + DataBaseManager.T_MATERIAS + " where carrera_id=" + carrera_id));

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            AsignaturaSpinnerAdapter adapter = new AsignaturaSpinnerAdapter(context, android.R.layout.simple_spinner_item, materias);
            //adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);
        }


        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) {
                sql = "select * from " + DataBaseManager.T_EVENTOS + " where carrera_id=" + carrera_id + " order by fecha";
            } else {
                sql = "select * from " + DataBaseManager.T_EVENTOS +
                        " where carrera_id=" + carrera_id + " and materia_id=" + materias.get(position).getId() + " order by fecha";
                ;
                materia_id = materias.get(position).getId();
                materia_nombre = materias.get(position).getNombre();
            }

            if (loadEventos != null) {
                loadEventos.cancel(true);
                loadEventos = null;
            }

            loadEventos = new LoadEventos();
            loadEventos.execute();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }


    private class LoadEventos extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = ProgressDialog.show(context, "Cargando", "Por favoe espere...", true);
            progress.show();
            int n = eventos.size();
            if (n > 0) {
                eventos.clear();
                eventosAdapter.notifyItemRangeRemoved(0, n - 1);
                eventosAdapter.notifyDataSetChanged();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            eventos.addAll(dataBaseManager.selectEventos(sql));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progress.dismiss();
            int n = eventos.size();
            if (n > 0) {
                eventosAdapter.notifyItemRangeInserted(0, n - 1);
                eventosAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(context, "No se encontrarón Eventos", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void reloadEvents() {
        if (loadEventos != null) {
            loadEventos.cancel(true);
            loadEventos = null;
        }

        loadEventos = new LoadEventos();
        loadEventos.execute();
    }



    public  interface OnEventosCarreraChangeListener{
        void onEventosCarreraChange();
    }


    private OnEventosCarreraChangeListener onEventosCarreraChangeListener;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onEventosCarreraChangeListener = (OnEventosCarreraChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement");
        }
    }
}
