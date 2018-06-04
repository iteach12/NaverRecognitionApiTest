package com.sihwan.iteach12.naverrecognitionapitest;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final int ITEM_SIZE = 10;
    Toolbar toolbar;
    FloatingActionButton fab;
    DrawerLayout drawer;
    NavigationView navigationView;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;

    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        auth = FirebaseAuth.getInstance();
        initView();


    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle;
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();



        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);



        List<Item> items = new ArrayList<>();
        Item[] item = new Item[ITEM_SIZE];
        item[0] = new Item("발음연습", "쉽고 즐겁게 발음을 연습해 보세요");
        item[1] = new Item("순위보기", "나의 순위를 확인하세요.");
        item[2] = new Item("가방열기", "나의 학습 상황을 점검해 보세요.");
        item[3] = new Item("오늘의 문제", "친구들과 함께 오락을 즐기면서 연습해 보세요.");
        item[4] = new Item("설정하기", "연습문제를 풀어서 실력을 늘려봅시다.");
        item[5] = new Item("연습문제2", "연습문제를 풀어서 실력을 늘려봅시다.");
        item[6] = new Item("연습문제3", "실력을 늘려봅시다.");
        item[7] = new Item("연습문제4", "굿굿굿이예요 굿굿굿");
        item[8] = new Item("연습문제5", "친구들과 함께 오락을 즐기면서 연습해 보세요.");
        item[9] = new Item("연습문제6", "친구들과 함께 오락을 즐기면서 연습해 보세요.");


        for (int i = 0; i < ITEM_SIZE; i++) {
            items.add(item[i]);
        }

        recyclerView.setAdapter(new RecyclerAdapter(getApplicationContext(), items, R.layout.activity_main));
        recyclerView.addItemDecoration(new RecyclerViewDecoration(40));


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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

            auth.signOut();
            LoginManager.getInstance().logOut();
            finish();
            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(myIntent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
