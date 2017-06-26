package com.darwindeveloper.mrteacher;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.darwindeveloper.mrteacher.adapters.EstudiantesAdapter;
import com.darwindeveloper.mrteacher.base_de_datos.DataBaseManager;
import com.darwindeveloper.mrteacher.tablas.Estudiante;
import com.darwindeveloper.mrteacher.utils.Extras;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class EstudiantesActivity extends BaseActivity implements EstudiantesAdapter.OnEstudiantesClickListener {

    public static final int CARRERA = 1, ASIGNATURA = 2;

    public static final String OPCION = "mrteacher.estudiantes.opcion";
    public static final String CARRERA_ID = "mrteacher.estudiantes.carrera_id";
    public static final String CARRERA_NOMBRE = "mrteacher.estudiantes.carrera_nombre";
    public static final String ASIGNATURA_ID = "mrteacher.estudiantes.asignatura_id";
    public static final String ASIGNATURA_NOMBRE = "mrteacher.estudiantes.asignatura_nombre";

    private String sql, carrera_id, carrera_nombre, asignatura_id, asignatura_nombre;
    private int opcion = 1;

    private Toolbar toolbar;
    private ArrayList<Estudiante> estudiantes = new ArrayList<>();
    private int seleccionados = 0;
    private RecyclerView recyclerView;
    private EstudiantesAdapter adapter;
    private TextView textViewToolbar;

    private SearchView searchView;

    private LoadData loadData;
    private boolean searchEmpty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estudiantes);


        Intent intent = getIntent();
        carrera_id = intent.getStringExtra(CARRERA_ID);
        carrera_nombre = intent.getStringExtra(CARRERA_NOMBRE);

        opcion = intent.getIntExtra(OPCION, 1);
        if (opcion == 2) {
            asignatura_id = intent.getStringExtra(ASIGNATURA_ID);
            asignatura_nombre = intent.getStringExtra(ASIGNATURA_NOMBRE);

            sql = "SELECT TE.* FROM " + DataBaseManager.T_ESTUDIANTES +
                    " as TE where TE.estudiante_id IN ( SELECT TEM.estudiante_id FROM "
                    + DataBaseManager.T_ESTUDIANTES_MATERIAS + " as TEM WHERE TEM.materia_id=" + asignatura_id + ")";
        } else {
            sql = "select TE.* from " + DataBaseManager.T_ESTUDIANTES + " as TE where TE.carrera_id=" + carrera_id;
        }


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        textViewToolbar = (TextView) findViewById(R.id.textViewToolbar);
        searchView = (SearchView) findViewById(R.id.searchView);


        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    textViewToolbar.setVisibility(View.GONE);
                } else {
                    if (searchEmpty) {
                        textViewToolbar.setVisibility(View.VISIBLE);
                    }
                }
            }
        });


        if (asignatura_nombre != null) {
            textViewToolbar.setText(carrera_nombre + ": " + asignatura_nombre);
        } else {
            textViewToolbar.setText(carrera_nombre);
        }


        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new EstudiantesAdapter(context, estudiantes);
        adapter.setOnEstudiantesClickListener(this);
        recyclerView.setAdapter(adapter);


        loadData = new LoadData(sql + " order by TE.apellidos");
        loadData.execute();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                searchEmpty = newText.length() == 0;


                if (loadData.getStatus() != AsyncTask.Status.FINISHED) {
                    loadData.cancel(true);
                    loadData = null;
                }
                loadData = new LoadData(sql + " and (TE.apellidos like'%" + newText + "%' or" +
                        " TE.nombres like'%" + newText + "%' or " +
                        " TE.estudiante_id like'%" + newText + "%' ) order by TE.apellidos ");
                loadData.execute();

                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onEstudianteItemClick(Estudiante estudiante, int position) {
        if (adapter.isShowCheckboxes()) {
            boolean selected = estudiante.isSelected();
            if (!selected) {
                seleccionados++;
                estudiantes.get(position).setSelected(true);
            } else {
                seleccionados--;
                estudiantes.get(position).setSelected(false);
            }

            adapter.notifyItemChanged(position);
            adapter.notifyDataSetChanged();
            textViewToolbar.setText("seleccionados " + seleccionados);
        }

    }

    private int buscarEstudiante(ArrayList<Estudiante> estudiantes, Estudiante estudiante) {
        int position = -1;
        for (int i = 0; i < estudiantes.size(); i++) {
            if (estudiante.getId().equals(estudiantes.get(i).getId())) {
                position = i;
                break;
            }
        }
        return position;
    }


    @Override
    public void onEstudianteItemLongClick(Estudiante estudiante, int position) {

        if (!adapter.isShowCheckboxes()) {
            adapter.setShowCheckboxes(true);

            onEstudianteItemClick(estudiante, position);

            adapter.notifyItemRangeChanged(0, estudiantes.size() - 1);
            adapter.notifyDataSetChanged();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            textViewToolbar.setText("seleccionados " + seleccionados);

            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.menu_estudiantes_eliminar);
        }


    }

    @Override
    public void onEstudianteItemClickMore(View view, final Estudiante estudiante, final int position) {
        PopupMenu popupMenu = new PopupMenu(context, view);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.popup_editar:
                        editarEstudiante(estudiante, position);

                        break;
                    case R.id.popup_eliminar:
                        AlertDialog.Builder builder = extras.createDialog(
                                R.drawable.ic_delete_forever_black_24dp,
                                "Confirmacion requerida",
                                "¿Eliminar " + estudiante.getApellidos() + " " + estudiante.getNombres() + "\n" + estudiante.getId() + "?",
                                true);

                        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                int result = dataBaseManager.deleteEstudiante(estudiante.getId());
                                if (result > 0) {
                                    estudiantes.remove(position);
                                    adapter.notifyItemRemoved(position);
                                    adapter.notifyDataSetChanged();
                                    if (estudiante.isSelected()) {
                                        seleccionados--;
                                        if (adapter.isShowCheckboxes())
                                            textViewToolbar.setText("seleccionados " + seleccionados);
                                    }
                                    Toast.makeText(context, "ELIMINADO", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show();
                                }

                                dialog.dismiss();

                            }
                        });
                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                        break;
                }
                return false;
            }
        });

        popupMenu.inflate(R.menu.menu_estudiante);

        showPopupIcon(popupMenu);

        popupMenu.show();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_estudiantes_carrera, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                adapter.setShowCheckboxes(false);
                adapter.notifyItemRangeChanged(0, estudiantes.size() - 1);
                adapter.notifyDataSetChanged();


                toolbar.getMenu().clear();
                toolbar.inflateMenu(R.menu.menu_estudiantes_carrera);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);

                if (asignatura_nombre != null) {
                    textViewToolbar.setText(carrera_nombre + ": " + asignatura_nombre);
                } else {
                    textViewToolbar.setText(carrera_nombre);
                }


                break;

            case R.id.action_nuevo:
                nuevoEstudiante();
                break;


            case R.id.action_eliminar:
                if (seleccionados > 0) {
                    AlertDialog.Builder builder = extras.createDialog(R.drawable.ic_delete_forever_black_24dp,
                            "Confirmación requerida", "¿Eliminar " + seleccionados + " seleccionados?", false);

                    builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            toolbar.getMenu().clear();
                            toolbar.inflateMenu(R.menu.menu_estudiantes_carrera);
                            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                            new DeleteStudents().execute();
                        }
                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }
                break;
        }
        return true;
    }

    /**
     * carga los estudiantes en la base de datos segun una sentencia sql
     */
    private class LoadData extends AsyncTask<Void, Void, Void> {
        private String sql;

        public LoadData(String sql) {
            this.sql = sql;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            seleccionados = 0;
            if (adapter.isShowCheckboxes()) {
                textViewToolbar.setText("seleccionados " + 0);
            }

            int tam = estudiantes.size();
            if (tam > 0) {
                estudiantes.clear();
                adapter.notifyItemRangeRemoved(0, tam - 1);
                adapter.notifyDataSetChanged();

            }

        }

        @Override
        protected Void doInBackground(Void... params) {
            estudiantes.addAll(dataBaseManager.selectEstudiantes(sql));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (estudiantes.size() > 0) {
                adapter.notifyItemRangeInserted(0, estudiantes.size() - 1);
                adapter.notifyDataSetChanged();
            } else {
                mostrarSnackBar("No se encontrarón estudiantes", Color.parseColor("#ff0000"));
            }
        }
    }

    /**
     * agrega un nuevo estudiante a la base de datos
     */
    private void nuevoEstudiante() {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialog_nuevo_estudiante, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setIcon(R.drawable.ic_launcher);
        alertDialogBuilder.setTitle("Nuevo Estudiante");

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText editTextID = (EditText) promptsView.findViewById(R.id.editTextID);
        final EditText editTextApellidos = (EditText) promptsView.findViewById(R.id.editTextApellidos);
        final EditText editTextNombres = (EditText) promptsView.findViewById(R.id.editTextNombres);
        final EditText editTextEmail = (EditText) promptsView.findViewById(R.id.editTextEmail);
        final EditText editTextTelefono = (EditText) promptsView.findViewById(R.id.editTextTelefono);
        final EditText editTextObs = (EditText) promptsView.findViewById(R.id.editTextDescripcion);

        Button btn_guardar = (Button) promptsView.findViewById(R.id.buttonG);
        Button btn_cancelar = (Button) promptsView.findViewById(R.id.buttonC);

        final Dialog dialog = alertDialogBuilder.create();

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editTextID.getText().toString();
                String apellidos = editTextApellidos.getText().toString();
                String nombres = editTextNombres.getText().toString();
                String email = editTextEmail.getText().toString();
                String telefono = editTextTelefono.getText().toString();
                String obs = editTextObs.getText().toString();

                if (id.trim().length() < 8 || apellidos.trim().length() < 3 || nombres.trim().length() < 3) {
                    Toast.makeText(context, "Ingrese un numero de identificación, apellidos y nombres validos", Toast.LENGTH_LONG).show();
                } else {

                    String last_id = dataBaseManager.insertEstudiante(id, nombres, apellidos, email, telefono, obs, carrera_id);
                    if (!last_id.equals("-1")) {
                        Estudiante tmp = new Estudiante(id, nombres, apellidos, email, telefono, obs, carrera_id);
                        estudiantes.add(tmp);
                        adapter.notifyItemInserted(estudiantes.size() - 1);
                        adapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(estudiantes.size() - 1);
                        dialog.dismiss();
                        mostrarSnackBar("Estudiante Creado", Color.parseColor("#0099aa"));

                    } else {
                        Toast.makeText(context, "ERROR No se pudo crear el dato", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();


    }


    /**
     * edita un estudiante en la base de datos
     *
     * @param estudiante
     * @param position
     */
    private void editarEstudiante(final Estudiante estudiante, final int position) {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialog_nuevo_estudiante, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setIcon(R.drawable.ic_launcher);
        alertDialogBuilder.setTitle("Editar Estudiante");

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText editTextID = (EditText) promptsView.findViewById(R.id.editTextID);
        final EditText editTextApellidos = (EditText) promptsView.findViewById(R.id.editTextApellidos);
        final EditText editTextNombres = (EditText) promptsView.findViewById(R.id.editTextNombres);
        final EditText editTextEmail = (EditText) promptsView.findViewById(R.id.editTextEmail);
        final EditText editTextTelefono = (EditText) promptsView.findViewById(R.id.editTextTelefono);
        final EditText editTextObs = (EditText) promptsView.findViewById(R.id.editTextDescripcion);

        editTextID.setText(estudiante.getId());
        editTextApellidos.setText(estudiante.getApellidos());
        editTextNombres.setText(estudiante.getNombres());
        editTextEmail.setText(estudiante.getEmail());
        editTextTelefono.setText(estudiante.getTelefono());
        editTextObs.setText(estudiante.getDescripcion());

        Button btn_guardar = (Button) promptsView.findViewById(R.id.buttonG);
        Button btn_cancelar = (Button) promptsView.findViewById(R.id.buttonC);

        final Dialog dialog = alertDialogBuilder.create();

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editTextID.getText().toString();
                String apellidos = editTextApellidos.getText().toString();
                String nombres = editTextNombres.getText().toString();
                String email = editTextEmail.getText().toString();
                String telefono = editTextTelefono.getText().toString();
                String obs = editTextObs.getText().toString();

                if (id.trim().length() < 8 || apellidos.trim().length() < 3 || nombres.trim().length() < 3) {
                    Toast.makeText(context, "Ingrese un número de identificación, apellidos y nombres validos", Toast.LENGTH_LONG).show();
                } else {

                    int result = dataBaseManager.updateEstudiante(estudiante.getId(), id, nombres, apellidos, email, telefono, obs, carrera_id);
                    if (result > 0) {
                        Estudiante tmp = new Estudiante(id, nombres, apellidos, email, telefono, obs, carrera_id);
                        estudiante.setApellidos(apellidos);
                        estudiante.setId(id);
                        estudiante.setNombres(nombres);
                        estudiante.setEmail(email);
                        estudiante.setTelefono(telefono);
                        estudiante.setDescripcion(obs);
                        adapter.notifyItemChanged(position);
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                        mostrarSnackBar("Estudiante Actualizado", Color.parseColor("#0099aa"));

                    } else {
                        Toast.makeText(context, "ERROR No se pudo actualizar el dato", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();


    }


    /**
     * para mostrar los iconos en los menus de cada estudiante
     *
     * @param popupMenu
     */
    private void showPopupIcon(PopupMenu popupMenu) {
        // Force icons to show
        Object menuHelper;
        Class[] argTypes;
        try {
            Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popupMenu);
            argTypes = new Class[]{boolean.class};
            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);

        } catch (Exception e) {
            // Possible exceptions are NoSuchMethodError and NoSuchFieldError
            //
            // In either case, an exception indicates something is wrong with the reflection code, or the
            // structure of the PopupMenu class or its dependencies has changed.
            //
            // These exceptions should never happen since we're shipping the AppCompat library in our own apk,
            // but in the case that they do, we simply can't force icons to display, so log the error and
            // show the menu normally.

            Log.w("popup error", "error forcing menu icons to show", e);
            popupMenu.show();
            return;
        }
    }


    /**
     * elimina todos los estudiantes seleccionados
     */
    private class DeleteStudents extends AsyncTask<Void, Integer, Void> {

        ProgressDialog progress;
        private int num = 0;
        private ArrayList<Estudiante> eliminados = new ArrayList<>();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = ProgressDialog.show(context, "Eliminado Estudiantes", "Espere...", false);
            progress.setMax(seleccionados);
            progress.setProgress(0);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progress.setProgress(values[0]);
        }

        @Override
        protected Void doInBackground(Void... params) {
            int pos = 0;
            for (Estudiante estudinate : estudiantes) {
                if (estudinate.isSelected()) {
                    int result = dataBaseManager.deleteEstudiante(estudinate.getId());
                    if (result > 0)
                        eliminados.add(estudinate);
                    num++;
                    publishProgress(num);
                }
                pos++;
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            seleccionados = 0;
            adapter.setShowCheckboxes(false);
            estudiantes.removeAll(eliminados);

            adapter.notifyDataSetChanged();

            progress.dismiss();

            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.menu_estudiantes_carrera);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            if (asignatura_nombre != null) {
                textViewToolbar.setText(carrera_nombre + ": " + asignatura_nombre);
            } else {
                textViewToolbar.setText(carrera_nombre);
            }


        }
    }

}
