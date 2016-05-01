package views;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.anderson.expressdelivery.R;

import controllers.AnnouncementController;
import controllers.UserAuthController;
import models.User;
import services.IResult;
import services.IResultUser;
import views.adapters.AnnouncementAdapter;
import views.adapters.RecyclerItemClickListener;
import models.Announcement;

import java.util.List;

public class MainActivity extends GenericActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mRecyclerView;
    private AnnouncementAdapter mAdapter;
    private boolean mLayoutGrid;
    private List<Announcement> mList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mLayoutGrid = false;
        new RemoteDataTask().execute();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                Context contexto = getApplicationContext();
                Intent intent = new Intent(contexto, AnnouncementDetailActivity.class);
                Announcement announcement = mList.get(position);
                intent.putExtra("description", announcement.getDescription());
                intent.putExtra("endereco", announcement.getEndereco());
                intent.putExtra("picture", announcement.getPicture());
                intent.putExtra("telefone", announcement.getTelefone());
                intent.putExtra("tittle", announcement.getTitle());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Context contexto = getApplicationContext();
                String texto = "LONGO";
                int duracao = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(contexto, texto, duracao);
                toast.show();
            }
        }));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        new RemoteDataTask().execute();
    }

    ProgressDialog mProgressDialog;

    private int limit = 2;

    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            AnnouncementController.getAll(limit, new IResult<Announcement>() {
                @Override
                public void onSuccess(List<Announcement> list) {
                    mLayoutGrid = false;
                    mList = list;

                }

                @Override
                public void onSuccess(Announcement obj) {

                }

                @Override
                public void onError(String msg) {

                }
            });
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAdapter = new AnnouncementAdapter(mList);
            mRecyclerView.setAdapter(mAdapter);

            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                }
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        Toast.makeText(getBaseContext(), "PEGOU", Toast.LENGTH_LONG).show();
                        new LoadMoreDataTask().execute();
                    }

                }
            });

        }
    }

    private class LoadMoreDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {

            AnnouncementController.getAll(limit += 1, new IResult<Announcement>() {
                @Override
                public void onSuccess(List<Announcement> list) {
                    mLayoutGrid = false;
                    mList = list;

                }

                @Override
                public void onSuccess(Announcement obj) {

                }

                @Override
                public void onError(String msg) {

                }
            });


            return null;
        }


        @TargetApi(Build.VERSION_CODES.M)
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAdapter = new AnnouncementAdapter(mList);
            mRecyclerView.setAdapter(mAdapter);

        }
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
        getMenuInflater().inflate(R.menu.main, menu);
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

        if (id == R.id.nav_manage_cadastro_anuncio) {
            redirect(this, AnnouncementRegisterActivity.class);
        } else if (id == R.id.nav_manage_cadastro_usuario) {
            redirect(this, UserRegisterActivity.class);
        } else if (id == R.id.nav_manage_logout) {
            UserAuthController.getCurrentUser(new IResultUser<User>() {
                @Override
                public void onSuccess(User obj) {
                    UserAuthController.logOut(obj, new IResultUser<User>() {
                        @Override
                        public void onSuccess(User obj) {
                            redirect(MainActivity.this, UserLoginActivity.class);
                        }

                        @Override
                        public void onError(String msg) {
                            showToastMessage(MainActivity.this, msg);
                        }
                    });
                }

                @Override
                public void onError(String msg) {
                    showToastMessage(MainActivity.this, msg);
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
