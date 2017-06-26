package com.darwindeveloper.mrteacher.base_de_datos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.darwindeveloper.mrteacher.tablas.Asignatura;
import com.darwindeveloper.mrteacher.tablas.Carrera;
import com.darwindeveloper.mrteacher.tablas.Estudiante;
import com.darwindeveloper.mrteacher.tablas.Evento;
import com.darwindeveloper.mrteacher.tablas.InstitucionEducativa;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * CRUD de toda la base de datos
 * Created by Darwin Morocho on 7/3/2017.
 */

public class DataBaseManager {

    private SQLiteDatabase db;
    private DataBaseHelper dbHelper;
    /*Nombre de las tablas*/
    public static final String T_INSTITUCIONES_EDUCATIVAS = "instituciones";
    public static final String T_CARRERAS = "carreras";
    public static final String T_ESTUDIANTES = "estudiantes";
    public static final String T_MATERIAS = "materias";
    public static final String T_ESTUDIANTES_MATERIAS = "estudiantes_materias";
    public static final String T_EVENTOS = "eventos";


    /**
     * constructor de la clase
     *
     * @param context contexto de la Actividad
     * @throws IOException posible error en tiempode ejecucion
     */
    public DataBaseManager(Context context) throws IOException {
        //creamos la connecion con la base de datos
        dbHelper = new DataBaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }


    /**
     * termina la connecion con la base de datos
     */
    public synchronized void cerrarConecion() {
        dbHelper.close();
    }


    /**
     * @param SELECT_SQL sentecia sql para recuperar los datos de la tabla de instictuciones educativas
     * @return una coleccion con las instituciones recuperadas
     */
    public ArrayList<InstitucionEducativa> selectInstitucionesEducativas(String SELECT_SQL) {

        ArrayList<InstitucionEducativa> instituciones = new ArrayList<>();

        Cursor cursor = db.rawQuery(SELECT_SQL, null);

        if (cursor.getCount() > 0) {//si hay datos recuperados por la sentencia SQL
            cursor.moveToFirst();
            do {
                String id = cursor.getString(cursor.getColumnIndex("institucion_id"));
                String nombre = cursor.getString(cursor.getColumnIndex("nombre"));
                String siglas = cursor.getString(cursor.getColumnIndex("siglas"));
                String observaciones = cursor.getString(cursor.getColumnIndex("descripcion"));
                instituciones.add(new InstitucionEducativa(id, nombre, siglas, observaciones));

            } while (cursor.moveToNext());
        }
        cursor.close();

        return instituciones;

    }


    /**
     * agrega una nueva institucion educativa a la base de datos
     *
     * @param nombre
     * @param siglas
     * @param observaciones
     * @return id de la ultima fila ingresada (retonnara -1 si hubo un error)
     */
    public String insertInstitucionEducativa(String nombre, String siglas, String observaciones) {

        String id = "-1";
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("siglas", siglas);
        values.put("descripcion", observaciones);
        id = db.insert(T_INSTITUCIONES_EDUCATIVAS, null, values) + "";
        return id;
    }


    /**
     * actuliza una institucion educativa
     *
     * @param id
     * @param nombre
     * @param siglas
     * @return numero de filas afectadas
     */
    public int updateInstitucionEducativa(String id, String nombre, String siglas) {
        // New value for one column
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("siglas", siglas);

// Which row to update, based on the title
        String selection = "institucion_id = ?";
        String[] selectionArgs = {id};

        return db.update(
                T_INSTITUCIONES_EDUCATIVAS,
                values,
                selection,
                selectionArgs);
    }

    /**
     * elimina unainstitucion educativa por su id
     *
     * @param id
     * @return retorna 1 si se booro con extido la fila en la base de datos
     */
    public int deleteInstitucionEducativa(String id) {
        return db.delete(T_INSTITUCIONES_EDUCATIVAS, "institucion_id = ?", new String[]{id});
    }

    /**
     * @param SELECT_SQL sentecia sql para recuperar los datos de la tabla de carreras
     * @return una coleccion con las carreras recuperadas
     */
    public ArrayList<Carrera> selectCarreras(String SELECT_SQL) {

        ArrayList<Carrera> carreras = new ArrayList<>();

        Cursor cursor = db.rawQuery(SELECT_SQL, null);

        if (cursor.getCount() > 0) {//si hay datos recuperados por la sentencia SQL
            cursor.moveToFirst();
            do {
                String id = cursor.getString(cursor.getColumnIndex("carrera_id"));
                String nombre = cursor.getString(cursor.getColumnIndex("nombre"));
                String u_id = cursor.getString(cursor.getColumnIndex("institucion_id"));
                carreras.add(new Carrera(id, nombre, u_id));

            } while (cursor.moveToNext());
        }
        cursor.close();

        return carreras;

    }


    public Carrera getCarrera(String carrera_id) {

        String sql = "select * from " + T_CARRERAS + " where carrera_id=" + carrera_id;

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.getCount() > 0) {//si hay datos recuperados por la sentencia SQL
            cursor.moveToFirst();
            String nombre = cursor.getString(cursor.getColumnIndex("nombre"));
            String u_id = cursor.getString(cursor.getColumnIndex("institucion_id"));
            return new Carrera(carrera_id, nombre, u_id);
        }
        cursor.close();

        return null;

    }


    /**
     * agrega una nueva institucion educativa a la base de datos
     *
     * @param nombre
     * @param institucion_id
     * @return id de la ultima fila ingresada (retonnara -1 si hubo un error)
     */
    public String insertCarrera(String nombre, String institucion_id) {
        String id = "-1";
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("institucion_id", institucion_id);
        id = db.insert(T_CARRERAS, null, values) + "";
        return id;
    }


    public int updateCarrera(String id, String nombre) {
        // New value for one column
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);

// Which row to update, based on the title
        String selection = "carrera_id = ?";
        String[] selectionArgs = {id};

        return db.update(
                T_CARRERAS,
                values,
                selection,
                selectionArgs);
    }


    /**
     * retorna una lista de todas las asignaturas obtenidas segun una sentencia select
     *
     * @param sql sql select
     * @return lista de asignaturas
     */
    public ArrayList<Asignatura> selectAsignaturas(String sql) {
        ArrayList<Asignatura> asignaturas = new ArrayList<>();

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.getCount() > 0) {//si hay asignaturas recuperadas
            cursor.moveToFirst();

            do {

                String id = cursor.getString(cursor.getColumnIndex("materia_id"));
                String nombre = cursor.getString(cursor.getColumnIndex("nombre"));
                String carrera_id = cursor.getString(cursor.getColumnIndex("carrera_id"));

                String sql_estudiantes_materia_materias = "select * from " + T_ESTUDIANTES_MATERIAS + " where materia_id=" + id;
                Cursor cursor2 = db.rawQuery(sql_estudiantes_materia_materias, null);
                int numEstuiantes = cursor2.getCount();
                cursor2.close();


                Asignatura asignatura = new Asignatura(id, nombre, carrera_id, numEstuiantes);
                asignaturas.add(asignatura);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return asignaturas;
    }


    public Asignatura getAsignatura(String asignatura_id) {
        String sql = "select * from " + T_MATERIAS + " where materia_id=" + asignatura_id;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String nombre = cursor.getString(cursor.getColumnIndex("nombre"));
            String carrera_id = cursor.getString(cursor.getColumnIndex("carrera_id"));

            String sql_estudiantes_materia_materias = "select * from " + T_ESTUDIANTES_MATERIAS + " where materia_id=" + asignatura_id;
            Cursor cursor2 = db.rawQuery(sql_estudiantes_materia_materias, null);
            int numEstuiantes = cursor2.getCount();
            cursor2.close();

            return new Asignatura(asignatura_id, nombre, carrera_id, numEstuiantes);

        }
        return null;
    }


    /**
     * agrega una nueva asignatura a la base de datos
     *
     * @param nombre
     * @param carrera_id
     * @return id de la ultima fila ingresada (retornara -1 si hubo un error)
     */
    public String insertAsignatura(String nombre, String carrera_id) {
        String id = "-1";
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("carrera_id", carrera_id);
        id = db.insert(T_MATERIAS, null, values) + "";
        return id;
    }


    public int updateAsignatura(String id, String nombre) {
        // New value for one column
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);

// Which row to update, based on the title
        String selection = "materia_id = ?";
        String[] selectionArgs = {id};

        return db.update(
                T_INSTITUCIONES_EDUCATIVAS,
                values,
                selection,
                selectionArgs);
    }


    /**
     * elimina una materia de la base de datos segun su id
     *
     * @param id
     * @return retornara el numero de elementos eliminados
     */
    public int deleteAsignatura(String id) {
        return db.delete(T_MATERIAS, "materia_id=?", new String[]{id});
    }


    /**
     * retorna un alista de estudiantes segun una sentencia sql
     *
     * @param sql
     * @return
     */
    public ArrayList<Estudiante> selectEstudiantes(String sql) {
        ArrayList<Estudiante> estudiantes = new ArrayList<>();

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                String id = cursor.getString(cursor.getColumnIndex("estudiante_id"));
                String nombres = cursor.getString(cursor.getColumnIndex("nombres"));
                String apellidos = cursor.getString(cursor.getColumnIndex("apellidos"));
                String email = cursor.getString(cursor.getColumnIndex("email"));
                String telefono = cursor.getString(cursor.getColumnIndex("telefono"));
                String descripcion = cursor.getString(cursor.getColumnIndex("descripcion"));
                String carrera_id = cursor.getString(cursor.getColumnIndex("carrera_id"));

                Estudiante estudiante = new Estudiante(id, nombres, apellidos, email, telefono, descripcion, carrera_id);
                estudiantes.add(estudiante);

            } while (cursor.moveToNext());
        }
        cursor.close();

        return estudiantes;
    }


    /**
     * ingresa un nuevo estudiante a la base de datos
     *
     * @param estudiante_id numero de identificacion (cedula o pasaporte)
     * @param nombres       obligatorios
     * @param apellidos     obligatorios
     * @param email
     * @param telefono
     * @param descripcion
     * @param carrera_id    carrera a la que pertenecce
     * @return
     */
    public String insertEstudiante(String estudiante_id, String nombres, String apellidos, String email,
                                   String telefono, String descripcion, String carrera_id) {
        String id = "-1";
        ContentValues values = new ContentValues();
        values.put("estudiante_id", estudiante_id);
        values.put("nombres", nombres);
        values.put("apellidos", apellidos);
        values.put("email", email);
        values.put("telefono", telefono);
        values.put("descripcion", descripcion);
        values.put("carrera_id", carrera_id);
        id = db.insert(T_ESTUDIANTES, null, values) + "";
        return id;
    }


    /**
     * ingresa un nuevo estudiante a la base de datos
     *
     * @param estudiante_id numero de identificacion (cedula o pasaporte)
     * @param nombres       obligatorios
     * @param apellidos     obligatorios
     * @param email
     * @param telefono
     * @param descripcion
     * @param carrera_id    carrera a la que pertenecce
     * @return
     */
    public String insertEstudianteAsignatura(String estudiante_id, String nombres, String apellidos, String email,
                                             String telefono, String descripcion, String carrera_id, String materia_id) {
        String id = "-1";
        ContentValues values = new ContentValues();
        values.put("estudiante_id", estudiante_id);
        values.put("nombres", nombres);
        values.put("apellidos", apellidos);
        values.put("email", email);
        values.put("telefono", telefono);
        values.put("descripcion", descripcion);
        values.put("carrera_id", carrera_id);
        id = db.insert(T_ESTUDIANTES, null, values) + "";

        if (!id.equals("-1")) {//si el estudiante se agrego a la carrera
            ContentValues values2 = new ContentValues();
            values2.put("estudiante_id", estudiante_id);
            values2.put("materia_id", materia_id);
            db.insert(T_ESTUDIANTES_MATERIAS, null, values2);
        } else {
            return "-1";
        }


        return id;
    }

    public int updateEstudiante(String id, String estudiante_id, String nombres, String apellidos, String email,
                                String telefono, String descripcion, String carrera_id) {
        // New value for one column
        ContentValues values = new ContentValues();
        values.put("estudiante_id", estudiante_id);
        values.put("nombres", nombres);
        values.put("apellidos", apellidos);
        values.put("email", email);
        values.put("telefono", telefono);
        values.put("descripcion", descripcion);
        values.put("carrera_id", carrera_id);

// Which row to update, based on the title
        String selection = "estudiante_id = ?";
        String[] selectionArgs = {id};

        return db.update(
                T_ESTUDIANTES,
                values,
                selection,
                selectionArgs);
    }

    /**
     * elimina un estudiante de la base de datos segun su id
     *
     * @param id
     * @return retornara el numero de elementos eliminados
     */
    public int deleteEstudiante(String id) {
        return db.delete(T_ESTUDIANTES, "estudiante_id=?", new String[]{id});
    }


    /**
     * retorna una lista de todos los eventos segun un sql dado
     *
     * @param sql
     * @return
     */
    public ArrayList<Evento> selectEventos(String sql) {
        ArrayList<Evento> eventos = new ArrayList<>();

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                String id = cursor.getString(cursor.getColumnIndex("evento_id"));
                String nombre = cursor.getString(cursor.getColumnIndex("nombre"));
                String fecha_creacion = cursor.getString(cursor.getColumnIndex("fecha_creacion"));
                String fecha = cursor.getString(cursor.getColumnIndex("fecha"));
                String observaciones = cursor.getString(cursor.getColumnIndex("observaciones"));
                String alarma_id = cursor.getString(cursor.getColumnIndex("alarma_id"));
                String carrera_id = cursor.getString(cursor.getColumnIndex("carrera_id"));
                String materia_id = cursor.getString(cursor.getColumnIndex("materia_id"));

                Evento ev = new Evento(id, nombre, fecha_creacion, fecha, observaciones, alarma_id, carrera_id, materia_id);
                eventos.add(ev);

            } while (cursor.moveToNext());
        }
        cursor.close();

        return eventos;
    }


    /**
     * elimina un evento segun su id
     *
     * @param id
     * @return nuemro de elementos eliminados
     */
    public int deleteEvento(String id) {
        return db.delete(T_EVENTOS, "evento_id=?", new String[]{id});
    }


    /**
     * crea un nuevo evento
     *
     * @param titulo
     * @param fecha
     * @param descripcion
     * @param alarma_id
     * @param carrera_id
     * @param materia_id
     * @return null si no se creo el evento
     */
    public Evento insertEvento(String titulo, String fecha_creacion, String fecha, String descripcion, String alarma_id, String carrera_id, String materia_id) {
        String id = "-1";

        ContentValues values = new ContentValues();
        values.put("nombre", titulo);
        values.put("observaciones", descripcion);
        values.put("fecha_creacion", fecha_creacion);
        values.put("fecha", fecha);
        values.put("alarma_id", alarma_id);
        values.put("carrera_id", carrera_id);
        values.put("materia_id", materia_id);
        id = db.insert(T_EVENTOS, null, values) + "";

        if (!id.equals("-1")) {
            return new Evento(id, titulo, fecha_creacion, fecha, descripcion, alarma_id, carrera_id, materia_id);
        } else {
            return null;
        }

    }


}
