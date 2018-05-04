package com.example.aadityasuri.awaam;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aadityasuri.awaam.Adapters.TabsPagerAdapter;
import com.example.aadityasuri.awaam.Fragemnets.CategoryFragment;
import com.example.aadityasuri.awaam.Fragemnets.PopularFragment;
import com.example.aadityasuri.awaam.Fragemnets.RecentFragment;

import java.util.HashMap;

public class ElectionClass extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TabsPagerAdapter mTabsPagerAdapter;
    private ViewPager mViewPager;
    private DrawerLayout mDrawerLayout;
    private TextView usernameMenu;
    HashMap<String, String> userdetail;
    private ActionBarDrawerToggle mActionBarToggle;
    SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        userdetail = session.getUserDetails();
        final String username = userdetail.get(SessionManager.KEY_EMAIL);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_menu);
        usernameMenu = (TextView) findViewById(R.id.user_name);
        mActionBarToggle = new ActionBarDrawerToggle(ElectionClass.this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mActionBarToggle);
        mActionBarToggle.syncState();
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        usernameMenu.setText("Welcome " + username);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(android.graphics.Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(mViewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mViewPager);
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        Menu trial = mNavigationView.getMenu();
        trial.findItem(R.id.edit_election).setVisible(false);
        trial.findItem(R.id.delete_election).setVisible(false);
        //        if (userdetail.get(SessionManager.KEY_ROLE).equals("0")||userdetail.get(SessionManager.KEY_ROLE).equals("1")) {
//            Menu trial=mNavigationView.getMenu();
//            trial.findItem(R.id.create_election).setVisible(false);
//            trial.findItem(R.id.edit_election).setVisible(false);
//            trial.findItem(R.id.delete_election).setVisible(false);
//            trial.findItem(R.id.verify_user).setVisible(false);
//            trial.findItem(R.id.create_election).setVisible(false);
//        }
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.log_out:
                        session.logoutUser();
                        break;
                    case R.id.create_election:
                        Intent createElection = new Intent(ElectionClass.this, CreateElection.class);
                        startActivity(createElection);
                        break;
                    case R.id.verify_user:
                        Intent verifyUser = new Intent(ElectionClass.this, VerifyUser.class);
                        startActivity(verifyUser);
                        break;
                    case R.id.user_stats:
                        Intent userStats = new Intent(ElectionClass.this, UserStats.class);
                        startActivity(userStats);
                        break;
                }

                return true;
            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mActionBarToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }


    public void setupViewPager(ViewPager viewPager) {
        TabsPagerAdapter adapter = new TabsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new RecentFragment(), "RECENT");
        adapter.addFragment(new PopularFragment(), "POPULAR");
        adapter.addFragment(new CategoryFragment(), "CATEGORY");
        viewPager.setAdapter(adapter);
    }

    private Boolean exit = false;

    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Intent a = new Intent(Intent.ACTION_MAIN);
                    a.addCategory(Intent.CATEGORY_HOME);
                    a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(a);
                }
            }, 1000);
        }
    }
}
