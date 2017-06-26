package com.darwindeveloper.mrteacher.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.darwindeveloper.mrteacher.R;
import com.darwindeveloper.mrteacher.adapters.InstitucionEducativaAdapter;
import com.darwindeveloper.mrteacher.base_de_datos.DataBaseManager;
import com.darwindeveloper.mrteacher.tablas.InstitucionEducativa;
import com.darwindeveloper.mrteacher.utils.Extras;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Darwin Morocho
 * @version 1.0.0
 *          https://darwindeveloper.com
 */
public class UniversidadesFragment extends Fragment implements
        InstitucionEducativaAdapter.OnIEClickListener {

    private Extras extras;

    private View rootView;
    private ArrayList<InstitucionEducativa> instituciones = new ArrayList<>();
    private ImageView noData;
    private RecyclerView recyclerView;
    private InstitucionEducativaAdapter adapter;

    private DataBaseManager dataBaseManager;
    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        extras = new Extras(context);
        try {
            dataBaseManager = new DataBaseManager(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_universidad, container, false);
        noData = (ImageView) rootView.findViewById(R.id.imageViewNoData);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewUniversidades);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        adapter = new InstitucionEducativaAdapter(context, instituciones);
        adapter.setOnIEClickListener(this);
        recyclerView.setAdapter(adapter);

        new LoadData().execute();

        final FloatingActionButton floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.dialog_nueva_universidad, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                alertDialogBuilder.setIcon(R.drawable.ic_launcher);
                alertDialogBuilder.setTitle("Nueva Institución Educativa");

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText nombreInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);

                final EditText siglasInput = (EditText) promptsView
                        .findViewById(R.id.editTextSiglas);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Guardar",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        // edit text

                                        String nombre = nombreInput.getText().toString();
                                        String siglas = siglasInput.getText().toString();

                                        if (siglas.trim().length() > 0 && nombre.trim().length() > 3) {
                                            nuevaUniversidad(nombre, siglas);
                                        } else {
                                            Toast.makeText(context, "ingrese un nombre y siglas valido", Toast.LENGTH_LONG).show();
                                        }

                                    }
                                })
                        .setNegativeButton("Cancelar",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }
        });


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    floatingActionButton.show();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0 || dy < 0 && floatingActionButton.isShown()) {
                    floatingActionButton.hide();
                }

            }
        });

        return rootView;
    }

    @Override
    public void onIEClick(InstitucionEducativa institucionEducativa, int position) {


        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        //ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = CarrerasDialogFragment.newInstance(institucionEducativa.getId(), institucionEducativa.getNombre());
        newFragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CustomDialog);
        newFragment.show(ft, "dialog");

    }

    @Override
    public void onIELongClick(final InstitucionEducativa institucionEducativa, final int position) {
        final AlertDialog.Builder builder = extras.createDialog(R.drawable.ic_launcher, "Eliga una opción", institucionEducativa.getNombre(), true);
        builder.setNegativeButton("Editar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editarUniversidad(institucionEducativa, position);
            }
        }).setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder builder2 = extras.createDialog(R.drawable.ic_launcher, "Confirmación requerida", "¿Eliminar " + institucionEducativa.getNombre() + "?", true);
                builder2.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int result = dataBaseManager.deleteInstitucionEducativa(institucionEducativa.getId());
                        if (result == 1) {
                            instituciones.remove(position);
                            adapter.notifyItemRemoved(position);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(context, "ELIMINADO", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "ERROR AL BORRAR", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).create().show();
            }
        });

        builder.create().show();
    }


    private class LoadData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            instituciones.clear();
            recyclerView.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            String sql = "select * from " + DataBaseManager.T_INSTITUCIONES_EDUCATIVAS;
            instituciones.addAll(dataBaseManager.selectInstitucionesEducativas(sql));
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (instituciones.size() > 0) {
                recyclerView.setVisibility(View.VISIBLE);
                noData.setVisibility(View.GONE);
                adapter.notifyItemRangeInserted(0, instituciones.size() - 1);
                adapter.notifyDataSetChanged();
            }
        }
    }


    private void nuevaUniversidad(String nombre, String siglas) {
        String id = dataBaseManager.insertInstitucionEducativa(nombre, siglas, "");
        if (!id.equals("-1")) {
            InstitucionEducativa tmp = new InstitucionEducativa(id, nombre, siglas, "");
            instituciones.add(tmp);
            adapter.notifyItemInserted(instituciones.size() - 1);
            adapter.notifyDataSetChanged();
            Toast.makeText(context, "GUARDADO", Toast.LENGTH_SHORT).show();
            noData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show();
        }
    }


    private void editarUniversidad(final InstitucionEducativa institucion, final int position) {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialog_nueva_universidad, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setIcon(R.drawable.ic_launcher);
        alertDialogBuilder.setTitle("Editar Institución Educativa");

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText nombreInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        nombreInput.setText(institucion.getNombre());

        final EditText siglasInput = (EditText) promptsView
                .findViewById(R.id.editTextSiglas);

        siglasInput.setText(institucion.getSiglas());

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Guardar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text

                                String nombre = nombreInput.getText().toString();
                                String siglas = siglasInput.getText().toString();

                                if (siglas.trim().length() > 0 && nombre.trim().length() > 3) {
                                    int result = dataBaseManager.updateInstitucionEducativa(institucion.getId(), nombre, siglas);
                                    if (result > 0) {
                                        instituciones.get(position).setNombre(nombre);
                                        instituciones.get(position).setSiglas(siglas);
                                        adapter.notifyItemChanged(position);
                                        adapter.notifyDataSetChanged();
                                        Toast.makeText(context, "ACTUALIZADO", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "ERROR AL ACTUALIZAR", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(context, "ingrese un nombre y siglas valido", Toast.LENGTH_LONG).show();
                                }

                            }
                        })
                .setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }



}
