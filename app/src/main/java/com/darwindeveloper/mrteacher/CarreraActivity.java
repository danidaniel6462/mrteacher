package com.darwindeveloper.mrteacher;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.darwindeveloper.horizontalscrollmenulibrary.custom_views.HorizontalScrollMenuView;
import com.darwindeveloper.mrteacher.base_de_datos.DataBaseManager;
import com.darwindeveloper.mrteacher.fragments.AsignaturasFragment;
import com.darwindeveloper.mrteacher.fragments.BottomSheetNuevoEvento;
import com.darwindeveloper.mrteacher.fragments.EventosCarrerafragment;
import com.darwindeveloper.mrteacher.fragments.EventosDashboardFragment;
import com.darwindeveloper.mrteacher.tablas.Asignatura;
import com.darwindeveloper.mrteacher.tablas.Carrera;
import com.darwindeveloper.mrteacher.tablas.Evento;
import com.darwindeveloper.wcviewpagerindicatorlibrary.WCViewPagerIndicator;
import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;

import java.util.ArrayList;

public class CarreraActivity extends BaseActivity implements BottomSheetNuevoEvento.OnEventChangeListener, EventosCarrerafragment.OnEventosCarreraChangeListener {

    private ArrayList<Evento> eventos_proximos = new ArrayList<>();
    //para obtener informacion desde otra actividad
    public static final String CARRERA_ID = "mrteacher.asignaturas.carrera_id";
    public static final String CARRERA_NOMBRE = "mrteacher.asignaturas.carrera_nombre";

    private String carrera_id, carrera_nombre;

    private WCViewPagerIndicator wcviewpager;
    private Toolbar toolbar;
    private ViewPager main_viewpager;
    private HorizontalScrollMenuView hmenu;
    private CardView cardViewNoEvents;
    private Fragment fragment;
    private WCViewPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrera);


        Intent intent = getIntent();
        carrera_id = intent.getStringExtra(CARRERA_ID);
        carrera_nombre = intent.getStringExtra(CARRERA_NOMBRE);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        wcviewpager = (WCViewPagerIndicator) findViewById(R.id.wcviewpager);
        main_viewpager = (ViewPager) findViewById(R.id.main_viewpager);
        cardViewNoEvents = (CardView) findViewById(R.id.carViewNoEvents);

        hmenu = (HorizontalScrollMenuView) findViewById(R.id.hmenu);


        init();


        MViewPagerAdapter pagerAdapter = new MViewPagerAdapter(getSupportFragmentManager());
        main_viewpager.setAdapter(pagerAdapter);

        main_viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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


        hmenu = (HorizontalScrollMenuView) findViewById(R.id.hmenu);
        hmenu.addItem("Asignaturas\nMaterias", R.drawable.painting_palette, true);
        hmenu.addItem("Notas\nApuntes", R.drawable.notepad);
        hmenu.addItem("Eventos\nRecordato.", R.drawable.calendar);

        hmenu.showItems();

        hmenu.setOnHSMenuClickListener(new HorizontalScrollMenuView.OnHSMenuClickListener() {
            @Override
            public void onHSMClick(com.darwindeveloper.horizontalscrollmenulibrary.extras.MenuItem menuItem, int position) {
                main_viewpager.setCurrentItem(position);

            }
        });

        NestedScrollView scrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);
        scrollView.setFillViewport(true);


    }


    private void init() {
        final RelativeLayout content_collapse = (RelativeLayout) findViewById(R.id.content_collapse);
        TextView textViewToolbar = (TextView) findViewById(R.id.textViewToolbar);
        textViewToolbar.setText(carrera_nombre);


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

    @Override
    public void onEventChange() {
        new LoadEventosProximos().execute();
        if (main_viewpager.getCurrentItem() == 2) {
            ((EventosCarrerafragment) fragment).reloadEvents();
        }
    }

    @Override
    public void onEventosCarreraChange() {
        new LoadEventosProximos().execute();
    }


    private class WCViewPagerAdapter extends FragmentStatePagerAdapter {

        // This holds all the currently displayable views, in order from left to right.
        private ArrayList<View> views = new ArrayList<View>();


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


        /**
         * Return the number of views available.
         */
        @Override
        public int getCount() {
            return eventos_proximos.size();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.swipeback_stack_to_front,
                R.anim.swipeback_stack_right_out);
    }


    private class MViewPagerAdapter extends FragmentPagerAdapter {

        private String tabTitles[] = new String[]{"Asignaturas", "Notas/Apuntes", "Eventos"};

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


            switch (position) {
                case 0:
                    fragment = new AsignaturasFragment();
                    Bundle args = new Bundle();
                    args.putString(AsignaturasFragment.CARRERA_ID, carrera_id);
                    args.putString(AsignaturasFragment.CARRERA_NOMBRE, carrera_nombre);
                    fragment.setArguments(args);
                    break;
                case 2:
                    fragment = new EventosCarrerafragment();
                    args = new Bundle();
                    args.putString(EventosCarrerafragment.CARRERA_ID, carrera_id);
                    args.putString(EventosCarrerafragment.CARRERA_NOMBRE, carrera_nombre);
                    fragment.setArguments(args);
                    break;
                default:
                    fragment = new AsignaturasFragment();
                    args = new Bundle();
                    args.putString(AsignaturasFragment.CARRERA_ID, carrera_id);
                    args.putString(AsignaturasFragment.CARRERA_NOMBRE, carrera_nombre);
                    fragment.setArguments(args);
                    break;
            }

            return fragment;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        /**
         * Return the number of views available.
         */
        @Override
        public int getCount() {
            return 3;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }

        return true;
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

                if(pagerAdapter==null){
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
                }else{
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

}
