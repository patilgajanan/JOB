/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.patil.jobsearch.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.patil.jobsearch.Activity.Auth.LoginActivity;
import com.patil.jobsearch.Activity.View.ViewProfileActivity;
import com.patil.jobsearch.Class.CircleImageView;
import com.patil.jobsearch.Config;
import com.patil.jobsearch.R;

public class RecruiterActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView_recruiter;
    private CircleImageView circleImageView_header_drawer;
    private TextView tv_name_header_drawer, tv_email_header_drawer;
    private LinearLayout ll_nav_header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView_recruiter = (RecyclerView) findViewById(R.id.recyclerView_recruiter);
        FloatingSearchView floating_search_view_recruiter = (FloatingSearchView) findViewById(R.id.floating_search_view_recruiter);
        floating_search_view_recruiter.attachNavigationDrawerToMenuButton(drawer);

        final View header = navigationView.getHeaderView(0);
        ll_nav_header = (LinearLayout) header.findViewById(R.id.ll_nav_header);
        ll_nav_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RecruiterActivity.this, ViewProfileActivity.class));
            }
        });
        circleImageView_header_drawer = (CircleImageView) header.findViewById(R.id.circleImageView_header_drawer);

        tv_name_header_drawer = (TextView) header.findViewById(R.id.tv_name_header_drawer);
        tv_email_header_drawer = (TextView) header.findViewById(R.id.tv_email_header_drawer);
        String name = LoginActivity.sharedPreferencesDatabase.getData(Config.LoginName);
        String email = LoginActivity.sharedPreferencesDatabase.getData(Config.LoginEmail);
        String phone = LoginActivity.sharedPreferencesDatabase.getData(Config.LoginMobile);
        tv_name_header_drawer.setText(name);

        if (!TextUtils.isEmpty(email)) {
            tv_email_header_drawer.setText(email);
        } else if (!TextUtils.isEmpty(phone)){
            tv_email_header_drawer.setText(phone);
        }



    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}
