package it.unical.mat.dlvfit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.lang.reflect.Array;
import java.util.ArrayList;

import it.unical.mat.dlvfit.contentprovider.InputDataUtil;
import it.unical.mat.dlvfit.contentprovider.OptimizeUtil;
import it.unical.mat.dlvfit.contentprovider.SQLiteDBManager;
import it.unical.mat.dlvfit.menu_fragments.AddActivity;
import it.unical.mat.dlvfit.menu_fragments.ElaborateWorkout;
import it.unical.mat.dlvfit.menu_fragments.StartWorkout;
import it.unical.mat.dlvfit.menu_fragments.UserDataFragment;

/**
 * Created by Brain At Work on 13/05/2015.
 */

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {
    public static int restoreFragment = 0;
    private static String TAG = MainActivity.class.getSimpleName();
    public final static String FRAGMENT_INTENT_KEY= "FRAG_KEY";
    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private SQLiteDBManager dbManager;
    private AlertDialog alertDialog;
    private boolean showAlert = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbManager = new SQLiteDBManager(getApplicationContext());

        Bundle extras = getIntent().getExtras();

        if( savedInstanceState != null){
            showAlert = false;
        }

        if(dbManager.retrieveInputData().size() == 0 && showAlert && extras == null){
            alertDialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.app_name)
                    .setMessage(R.string.input_data_alert)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    })
                    .setIcon(R.mipmap.ic_launcher)
                    .show();
        }

        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        if( extras != null){
            restoreFragment = extras.getInt(FRAGMENT_INTENT_KEY);
            Log.i(TAG, "Restore fragment from intent");
        }
        // display the first navigation drawer view on app launch
        displayView(restoreFragment);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    public void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        restoreFragment = position;
        switch (position) {
            case 0:
                fragment = new UserDataFragment();
                title = getString(R.string.title_user_data);
                break;
            case 1:
                fragment = new StartWorkout();
                title = getString(R.string.title_start_workout);
                break;
            case 2:
                fragment = new ElaborateWorkout();
                title = getString(R.string.title_elaborate_workout);
                break;
            case 3:
                fragment = new AddActivity();
                title = getString(R.string.title_add_activity);
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }
}