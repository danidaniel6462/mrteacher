package com.darwindeveloper.mrteacher;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.darwindeveloper.mrteacher.adapters.BackupsAdapter;
import com.darwindeveloper.mrteacher.base_de_datos.DataBaseHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class LocalBackupsActivity extends BaseActivity {

    private static final  int REQUEST_CODE=111;
    String path;
    private ArrayList<File> myList = new ArrayList<>();

    private RecyclerView recyclerView;
    private BackupsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_backups);
        path = Environment.getExternalStorageDirectory() + "/MrTeacher/backups/";

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerviewBackups);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new BackupsAdapter(context, myList);
        recyclerView.setAdapter(adapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }else {
                checkFolder();
                new LoadFiles().execute();
            }
        }else{
            checkFolder();
            new LoadFiles().execute();
        }








    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v("P60","Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
            checkFolder();


            new LoadFiles().execute();
        }else{
            Toast.makeText(context, "ERROR permiso denegado", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    private void checkFolder() {
        File f = new File(path);
        if (!f.exists())
            f.mkdirs();
    }


    private void loadBackupFiles() {
        File file = new File(path);
        File list[] = file.listFiles();
        for (int i = 0; i < list.length; i++) {
            myList.add(list[i]);
        }
    }


    private void nuevoBackup() {
        final String db_path = "/data/data/" + getApplicationContext().getPackageName() + "/databases/" + DataBaseHelper.DB_NAME;


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Nuevo Backup ");
        alertDialog.setCancelable(false);

        final EditText input = new EditText(context);
        input.setHint("Ejemplo: Respaldo-2017-2018");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("Crear",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String name = input.getText().toString();
                        if (name.trim().length() > 3) {
                            try {
                                File dbFile = new File(db_path);
                                FileInputStream fis = new FileInputStream(dbFile);

                                String outFileName = path + name + ".db";

                                // Open the empty db as the output stream
                                OutputStream output = new FileOutputStream(outFileName);

                                // Transfer bytes from the inputfile to the outputfile
                                byte[] buffer = new byte[1024];
                                int length;
                                while ((length = fis.read(buffer)) > 0) {
                                    output.write(buffer, 0, length);
                                }

                                // Close the streams
                                output.flush();
                                output.close();
                                fis.close();


                                mostrarSnackBar("Backup Creado", Color.parseColor("#0099aa"));

                                new LoadFiles().execute();
                            } catch (Exception e) {
                                Toast.makeText(context, "ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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


    private class LoadFiles extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "Cargando Backups", "por favor espere ...", true);
            progressDialog.show();
            int tam = myList.size();
            if (tam > 0) {
                myList.clear();
                adapter.notifyItemRangeRemoved(0, tam - 1);
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            loadBackupFiles();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if (myList.size() > 0) {
                adapter.notifyItemRangeInserted(0, myList.size() - 1);
                adapter.notifyDataSetChanged();
            } else {
                mostrarSnackBar("No se encontrarón backups", Color.parseColor("#ff0000"));
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_local_backups, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;

            case R.id.backup_nuevo:
                nuevoBackup();
                break;
        }
        return true;
    }
}
