package com.darwindeveloper.mrteacher;


import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.darwindeveloper.horizontalscrollmenulibrary.custom_views.HorizontalScrollMenuView;
import com.darwindeveloper.mrteacher.base_de_datos.DataBaseManager;
import com.darwindeveloper.mrteacher.fragments.EventosDashboardFragment;
import com.darwindeveloper.mrteacher.fragments.UniversidadesFragment;
import com.darwindeveloper.mrteacher.login.LoginActivity;
import com.darwindeveloper.mrteacher.tablas.Asignatura;
import com.darwindeveloper.mrteacher.tablas.Carrera;
import com.darwindeveloper.mrteacher.tablas.Evento;
import com.darwindeveloper.wcviewpagerindicatorlibrary.WCViewPagerIndicator;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {


    private ArrayList<Evento> eventos_proximos = new ArrayList<>();


    private ProgressDialog progressDialog;
    private WCViewPagerIndicator wcviewpager;
    private ViewPager main_viewPager;
    private String nombre_usuario, email_usuario, login_usuario;
    private Toolbar toolbar;
    private HorizontalScrollMenuView hmenu;
    private CardView cardViewNoEvents;
    private WCViewPagerAdapter pagerAdapter;


    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show();
                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        wcviewpager = (WCViewPagerIndicator) findViewById(R.id.wcviewpager);
        main_viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        cardViewNoEvents = (CardView) findViewById(R.id.carViewNoEvents);


        init();


        initCollapse();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_local_backups) {
            Intent intent = new Intent(context, LocalBackupsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_exit) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("¿Cerrar sesión?");
            builder.setCancelable(false);
            builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    progressDialog = ProgressDialog.show(context, "Cerrando sesión", "Espere ...", true);
                    progressDialog.show();
                    if (login_usuario.equals("google")) {
                        signOutGoogle();
                    } else {
                        signOutFacebook();
                    }
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.create().show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void initCollapse() {

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int monnth = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);


        final RelativeLayout content_collapse = (RelativeLayout) findViewById(R.id.content_collapse);
        TextView textViewToolbar = (TextView) findViewById(R.id.textViewToolbar);
        textViewToolbar.setText("Mr. Teacher  " + day + "/" + (monnth + 1) + "/" + year);


        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        collapsingToolbarLayout.setTitle("");


        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            int scrollRange = -1;
            boolean animate_out = false, animate_in = false;//variables para controlar que la animacion solo se muestre una sola vez mientra se hace scroll

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                collapsingToolbarLayout.setTitle("");
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }


                if (scrollRange + verticalOffset < 70) {
                    if (!animate_out) {
                        animate_out = true;
                        Animation anim = AnimationUtils.loadAnimation(context, R.anim.fade_out);
                        content_collapse.startAnimation(anim);
                        content_collapse.setVisibility(View.INVISIBLE);
                        animate_in = false;
                    }
                } else {
                    if (!animate_in) {
                        animate_in = true;
                        Animation anim = AnimationUtils.loadAnimation(context, R.anim.fade_in);
                        content_collapse.startAnimation(anim);
                        content_collapse.setVisibility(View.VISIBLE);
                        animate_out = false;
                    }

                }


            }


        });


        NestedScrollView scrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);
        scrollView.setFillViewport(true);


        new LoadEventosProximos().execute();
    }


    /**
     * adaptador para el viewpager de eventos
     */
    private class WCViewPagerAdapter extends FragmentStatePagerAdapter {


        public WCViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return the Fragment associated with a specified position.
         *
         * @param position
         */
        @Override
        public Fragment getItem(int position) {
            if (eventos_proximos.size() > 0) {
                Fragment fragment = new EventosDashboardFragment();

                Evento evento = eventos_proximos.get(position);


                String nombre_carrera, nombre_materia = "-1";

                Carrera tmpc = dataBaseManager.getCarrera(evento.getCarrera_id());
                nombre_carrera = tmpc.getNombre();

                if (!evento.getMateria_id().equals("-1")) {
                    Asignatura tmp = dataBaseManager.getAsignatura(evento.getMateria_id());
                    nombre_materia = tmp.getNombre();
                }

                Bundle bundle = new Bundle();
                bundle.putString(EventosDashboardFragment.EVENTO_ID, evento.getId());
                bundle.putString(EventosDashboardFragment.EVENTO_TITULO, evento.getNombre());
                bundle.putString(EventosDashboardFragment.EVENTO_DESCRIPCION, evento.getObservaciones());
                bundle.putString(EventosDashboardFragment.EVENTO_FECHA_CREACION, evento.getFecha_creacion());
                bundle.putString(EventosDashboardFragment.EVENTO_FECHA_EVENTO, evento.getFecha());
                bundle.putString(EventosDashboardFragment.EVENTO_CARRERA_ID, evento.getCarrera_id());
                bundle.putString(EventosDashboardFragment.EVENTO_CARRERA_NOMBRE, nombre_carrera);
                bundle.putString(EventosDashboardFragment.EVENTO_MATERIA_ID, evento.getMateria_id());
                bundle.putString(EventosDashboardFragment.EVENTO_MATERIA_NOMBRE, nombre_materia);

                fragment.setArguments(bundle);
                return fragment;
            }

            return null;
        }

        /**
         * Return the number of views available.
         */
        @Override
        public int getCount() {
            return eventos_proximos.size();
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }


    }


    /**
     * adaptador para el viewpager de abajo
     */
    private class MViewPagerAdapter extends FragmentPagerAdapter {

        public MViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return the Fragment associated with a specified position.
         *
         * @param position
         */
        @Override
        public Fragment getItem(int position) {

            Fragment fragment = new UniversidadesFragment();


            return fragment;
        }

        /**
         * Return the number of views available.
         */
        @Override
        public int getCount() {
            return 5;
        }
    }


    private View headerView;

    private void init() {

        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        drawer.setScrimColor(getResources().getColor(android.R.color.transparent));
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                coordinatorLayout.setTranslationX(slideOffset * drawerView.getWidth());
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });


        hmenu = (HorizontalScrollMenuView) findViewById(R.id.hmenu);
        hmenu.addItem("Universidades\nEscuelas", R.drawable.school, true);
        hmenu.addItem("Carreras\nCursos", R.drawable.graduate);
        hmenu.addItem("Tareas\nTrabajos", R.drawable.homework);
        hmenu.addItem("Notas\nApuntes", R.drawable.notepad);
        hmenu.addItem("Eventos\nRecordato.", R.drawable.calendar);

        hmenu.showItems();
        hmenu.setOnHSMenuClickListener(new HorizontalScrollMenuView.OnHSMenuClickListener() {
            @Override
            public void onHSMClick(com.darwindeveloper.horizontalscrollmenulibrary.extras.MenuItem menuItem, int position) {
                main_viewPager.setCurrentItem(position);
            }
        });


        MViewPagerAdapter mViewPagerAdapter = new MViewPagerAdapter(getSupportFragmentManager());
        main_viewPager.setAdapter(mViewPagerAdapter);
        main_viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                hmenu.setItemSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        nombre_usuario = preferences.getString(getString(R.string.usuario_nombre), "none");
        email_usuario = preferences.getString(getString(R.string.usuario_email), "none");
        login_usuario = preferences.getString(getString(R.string.usuario_login), "none");
        String url_imagen = preferences.getString(getString(R.string.usuario_foto), "none");

        headerView = navigationView.getHeaderView(0);

        TextView textViewUserName = (TextView) headerView.findViewById(R.id.username);
        TextView textViewUserEmail = (TextView) headerView.findViewById(R.id.email);

        textViewUserName.setText(nombre_usuario);
        textViewUserEmail.setText(email_usuario);


        if (checkInternetConnection()) {
            if (url_imagen.contains("http")) {
                new LoadImage(url_imagen).execute();
            }
        }

    }

    //carga la imagen de perfil como una taerea asincrota
    private class LoadImage extends AsyncTask<Void, Void, Void> {

        private Bitmap bitmap;
        private String url_imagen;

        LoadImage(String url_imagen) {
            this.url_imagen = url_imagen;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {//descargamos la imagen de perfil del usuario
                bitmap = Picasso.with(context).load(url_imagen).get();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            CircleImageView circleImageView = (CircleImageView) headerView.findViewById(R.id.profile_image);
            circleImageView.setImageBitmap(bitmap);

        }
    }


    private void signOutFacebook() {
        LoginManager.getInstance().logOut();
        loginActivity();
    }

    // [START signOut]
    private void signOutGoogle() {


        if (mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            // [START_EXCLUDE]
                            loginActivity();
                            // [END_EXCLUDE]
                        }
                    });
        }


    }
    // [END signOut]


    private void loginActivity() {
        if (progressDialog != null)
            progressDialog.dismiss();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        startActivity(new Intent(context, LoginActivity.class));
        finish();

    }


    private class LoadEventosProximos extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            eventos_proximos.clear();

        }


        @Override
        protected Void doInBackground(Void... params) {
            eventos_proximos.addAll(dataBaseManager.selectEventos("select * from " + DataBaseManager.T_EVENTOS + " order by fecha limit 10"));
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (eventos_proximos.size() > 0) {
                cardViewNoEvents.setVisibility(View.GONE);
                wcviewpager.setVisibility(View.VISIBLE);

                if (pagerAdapter == null) {
                    pagerAdapter = new WCViewPagerAdapter(getSupportFragmentManager());
                    wcviewpager.setAdapter(pagerAdapter);
                    wcviewpager.getViewPager().addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                        }

                        @Override
                        public void onPageSelected(int position) {
                            wcviewpager.setSelectedindicator(position);
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });
                } else {
                    pagerAdapter.notifyDataSetChanged();
                    wcviewpager.updateIndicators(eventos_proximos.size());
                    wcviewpager.getViewPager().setCurrentItem(0);
                }

            } else {
                cardViewNoEvents.setVisibility(View.VISIBLE);
                wcviewpager.setVisibility(View.GONE);
            }
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        new LoadEventosProximos().execute();
    }
}
