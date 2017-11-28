/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.patil.jobsearch.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.util.adapter.TextWatcherAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.patil.jobsearch.Activity.Auth.LoginActivity;
import com.patil.jobsearch.Activity.View.ViewProfileActivity;
import com.patil.jobsearch.Activity.View.ViewRecruiterReqActivity;
import com.patil.jobsearch.Adapters.SearchAdapter;
import com.patil.jobsearch.Class.CircleImageView;
import com.patil.jobsearch.Class.Functions;
import com.patil.jobsearch.Config;
import com.patil.jobsearch.ConfigStateCity;
import com.patil.jobsearch.Items.RecruiterItem;
import com.patil.jobsearch.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.patil.jobsearch.Activity.Auth.LoginActivity.sharedPreferencesDatabase;

public class RecruiterActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static Activity activity;
    protected DatabaseReference mRef;
    private RecyclerView common_recyclerView;
    private CircleImageView circleImageView_header_drawer;
    private TextView tv_name_header_drawer, tv_roll_header_drawer, tv_email_header_drawer;
    private LinearLayout ll_nav_header;
    private List<RecruiterItem> recruiterItems = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;
    private ProgressBar pb_error_recyclerView;
    private TextView tv_error_recyclerView;
    private com.patil.jobsearch.Adapters.Custom.RecruiterAdapter recruiterAdapter;
    private FloatingSearchView floating_search_view_recruiter;
    private FirebaseAuth mAuth;
    private ArrayList<String> titles = new ArrayList<>();
    private SearchAdapter searchAdapter;
    private FloatingActionButton fab_recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_recruiter);
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
        activity = RecruiterActivity.this;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        common_recyclerView = (RecyclerView) findViewById(R.id.common_recyclerView);
        pb_error_recyclerView = (ProgressBar) findViewById(R.id.pb_error_recyclerView);
        tv_error_recyclerView = (TextView) findViewById(R.id.tv_error_recyclerView);
        fab_recyclerView = (FloatingActionButton) findViewById(R.id.fab_recyclerView);

        floating_search_view_recruiter = (FloatingSearchView) findViewById(R.id.floating_search_view_recruiter);
        floating_search_view_recruiter.setSearchHint("Search Recruiter..");
        floating_search_view_recruiter.attachNavigationDrawerToMenuButton(drawer);
        floating_search_view_recruiter.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_filter) {
                    aleartDialogFilter();
                }
            }
        });

        fab_recyclerView.setVisibility(View.GONE);
        floating_search_view_recruiter.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                recruiterAdapter.getFilter().filter(newQuery);
            }
        });
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
        tv_roll_header_drawer = (TextView) header.findViewById(R.id.tv_roll_header_drawer);
        tv_email_header_drawer = (TextView) header.findViewById(R.id.tv_email_header_drawer);
        String img = sharedPreferencesDatabase.getData(Config.LoginImg);
        String name = sharedPreferencesDatabase.getData(Config.LoginName);
        String email = sharedPreferencesDatabase.getData(Config.LoginEmail);
        String roll = sharedPreferencesDatabase.getData(Config.LoginRoll);
        tv_name_header_drawer.setText(name);
        if (!TextUtils.isEmpty(img)) {
            Picasso.with(RecruiterActivity.this).load(img).into(circleImageView_header_drawer);
        }
        if (!TextUtils.isEmpty(name)) {
            tv_name_header_drawer.setText(name);
        }
        if (!TextUtils.isEmpty(email)) {
            tv_email_header_drawer.setText(email);
        }
        if (!TextUtils.isEmpty(roll)) {
            tv_roll_header_drawer.setText(roll.toUpperCase());
        }
        attachRecyclerViewAdapter();

    }


    public void attachDataCompany(final TextView tv_result_filter_recruiter_dialog, final String s_city, final String s_experiance, final String s_skill) {
        pb_error_recyclerView.setVisibility(View.VISIBLE);
        Query query = mRef.child(Config.Recruiters);

        if (!TextUtils.isEmpty(s_city)) {
            query.orderByChild("city").equalTo(s_city);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recruiterItems.clear();
                for (final DataSnapshot child : dataSnapshot.getChildren()) {
                    String id = (String) child.child("id").getValue();
                    String uid = (String) child.child("uid").getValue();
                    String company_name = (String) child.child("company_name").getValue();
                    String city = (String) child.child("city").getValue();
                    String job_title = (String) child.child("job_title").getValue();
                    String required_skills = (String) child.child("required_skills").getValue();
                    String required_experience = (String) child.child("required_experience").getValue();
                    String eligibility = (String) child.child("eligibility").getValue();

                    String final_city = city;
                    String final_experience = required_experience;
                    String final_skill= required_skills;

                    if (TextUtils.isEmpty(s_city)) {
                        city = "";
                    }
                    if (TextUtils.isEmpty(s_experiance)) {

                        required_experience = "";
                    }

                    if (TextUtils.isEmpty(s_skill)) {
                        required_skills = "";
                    }

                    if (TextUtils.equals(s_city, city) && TextUtils.equals(s_experiance, required_experience) && TextUtils.equals(s_skill, required_skills)) {
                        recruiterItems.add(new RecruiterItem(id, uid, company_name, final_city, job_title, final_skill, final_experience, eligibility));
                    }

                }
                pb_error_recyclerView.setVisibility(View.GONE);
                recruiterAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
//            addFragment(new HomeFragment());
        } else if (id == R.id.nav_req_recruiter) {
            startActivity(new Intent(RecruiterActivity.this, ViewRecruiterReqActivity.class));
        } else if (id == R.id.nav_notification) {
            startActivity(new Intent(RecruiterActivity.this, NotificationActivity.class));
        } else if (id == R.id.nav_requests) {
//            addFragment(new RequestsFragment());
        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            if (mAuth.getCurrentUser() == null) {
                sharedPreferencesDatabase.removeData();
                Intent i = new Intent(RecruiterActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_settings) {
            Intent i = new Intent(RecruiterActivity.this, SettingsActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void addFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_drawer, fragment);
            fragmentTransaction.commit();
        }
    }

    private void attachRecyclerViewAdapter() {
        mLayoutManager = new LinearLayoutManager(RecruiterActivity.this, LinearLayoutManager.VERTICAL, true);
        mLayoutManager.setStackFromEnd(true);
        common_recyclerView.setNestedScrollingEnabled(false);
        common_recyclerView.setHasFixedSize(false);
        common_recyclerView.setLayoutManager(mLayoutManager);
        String login_roll = sharedPreferencesDatabase.getData("login_roll");
        recruiterAdapter = new com.patil.jobsearch.Adapters.Custom.RecruiterAdapter(RecruiterActivity.this,
                0, recruiterItems, null, null);
        common_recyclerView.setAdapter(recruiterAdapter);
        attachData("", "");
    }

    private void attachData(String s_qurey_address, final String s_qurey_skill) {
        pb_error_recyclerView.setVisibility(View.VISIBLE);
        final String login_name = sharedPreferencesDatabase.getData("login_name");
        Query query = mRef;
        if (TextUtils.isEmpty(s_qurey_address)) {
            query = mRef.child(Config.Recruiters).orderByChild("eligibility").equalTo("Yes");
        } else {
            query = mRef.child(Config.Recruiters).orderByChild("address").equalTo(s_qurey_address);
        }

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recruiterItems.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
//                    id, company_name, city, organization, job_title, required_skills, required_experience, eligibility
                    String id = (String) child.child("id").getValue();
                    String uid = (String) child.child("uid").getValue();
                    String company_name = (String) child.child("company_name").getValue();
                    String city = (String) child.child("city").getValue();
                    String organization = (String) child.child("organization").getValue();
                    String job_title = (String) child.child("job_title").getValue();
                    String required_skills = (String) child.child("required_skills").getValue();
                    String required_experience = (String) child.child("required_experience").getValue();
                    String eligibility = (String) child.child("eligibility").getValue();

                    RecruiterItem recruiterItem = new RecruiterItem(id, uid, company_name, city, job_title,
                            required_skills, required_experience, eligibility);

                    if (!TextUtils.isEmpty(s_qurey_skill)) {
                        if (TextUtils.equals(s_qurey_skill, required_skills)) {
                            recruiterItems.add(recruiterItem);
                        }
                    } else {
                        recruiterItems.add(recruiterItem);
                    }


                }

                recruiterAdapter.notifyDataSetChanged();
                if (recruiterAdapter.getItemCount() == 0) {
                    tv_error_recyclerView.setVisibility(View.VISIBLE);
                } else {
                    tv_error_recyclerView.setVisibility(View.GONE);
                }

                pb_error_recyclerView.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recruiter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_filter) {
            Toast.makeText(this, "Filter", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void aleartDialogFilter() {
        final AlertDialog alertDialog = new AlertDialog.Builder(RecruiterActivity.this).create();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = inflater.inflate(R.layout.dialog_filter_recruiter, null);
        alertDialog.setView(v);
        alertDialog.setCancelable(true);
        alertDialog.show();
        final EditText et_city_filrer_recruiter_dialog = (EditText) v.findViewById(R.id.et_city_filrer_recruiter_dialog);
        final EditText et_experience_filrer_recruiter_dialog = (EditText) v.findViewById(R.id.et_experience_filrer_recruiter_dialog);
        final EditText et_skill_filrer_recruiter_dialog = (EditText) v.findViewById(R.id.et_skill_filrer_recruiter_dialog);
        final TextView tv_result_filter_recruiter_dialog = (TextView) v.findViewById(R.id.tv_result_filter_recruiter_dialog);
        Button btn_cancel_filrer_recruiter_dialog = (Button) v.findViewById(R.id.btn_cancel_filrer_recruiter_dialog);
        Button btn_apply_filrer_recruiter_dialog = (Button) v.findViewById(R.id.btn_apply_filrer_recruiter_dialog);
        et_city_filrer_recruiter_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDialog(Config.STATS, et_city_filrer_recruiter_dialog);
            }
        });
        et_experience_filrer_recruiter_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDialog(Config.EXPERIENCE, et_experience_filrer_recruiter_dialog);
            }
        });
        et_skill_filrer_recruiter_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDialog(Config.SKILLS, et_skill_filrer_recruiter_dialog);
            }
        });

        btn_cancel_filrer_recruiter_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btn_apply_filrer_recruiter_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachDataCompany(tv_result_filter_recruiter_dialog, et_city_filrer_recruiter_dialog.getText().toString(), et_experience_filrer_recruiter_dialog.getText().toString(), et_skill_filrer_recruiter_dialog.getText().toString());
                alertDialog.dismiss();
            }
        });

    }

    public void searchDialog(String tag, final EditText editText) {
        final AlertDialog alertDialog = new AlertDialog.Builder(RecruiterActivity.this).create();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = inflater.inflate(R.layout.dialog_search, null);
        alertDialog.setView(v);
        alertDialog.setCancelable(true);
        alertDialog.show();

        final RecyclerView recyclerView_search_dialog = (RecyclerView) v.findViewById(R.id.recyclerView_search_dialog);
        final LinearLayout ll_spinner_state_search_dialog = (LinearLayout) v.findViewById(R.id.ll_spinner_state_search_dialog);
        final Spinner spinner_state_search_dialog = (Spinner) v.findViewById(R.id.spinner_state_search_dialog);
        final ProgressBar pb_search_dialog = (ProgressBar) v.findViewById(R.id.pb_search_dialog);
        final TextView tv_error_search_dialog = (TextView) v.findViewById(R.id.tv_error_search_dialog);
        final EditText et_search_dialog = (EditText) v.findViewById(R.id.et_search_dialog);
        ImageView iv_clear_search_dialog = (ImageView) v.findViewById(R.id.iv_clear_search_dialog);
        pb_search_dialog.setVisibility(View.VISIBLE);

        LinearLayoutManager mLayoutManager_search = new LinearLayoutManager(RecruiterActivity.this);
        mLayoutManager_search.setReverseLayout(false);
        mLayoutManager_search.setStackFromEnd(false);
        recyclerView_search_dialog.setNestedScrollingEnabled(false);
        recyclerView_search_dialog.setHasFixedSize(false);
        recyclerView_search_dialog.setLayoutManager(mLayoutManager_search);
        et_search_dialog.requestFocus();
        iv_clear_search_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(et_search_dialog.getText().toString())) {
                    alertDialog.dismiss();
                } else {
                    et_search_dialog.setText("");
                }

            }
        });
        titles.clear();
        if (titles != null) {
            searchAdapter = new SearchAdapter(RecruiterActivity.this, titles, editText, null, null, null, null, null, alertDialog);
            recyclerView_search_dialog.setAdapter(searchAdapter);
        }

        if (!TextUtils.isEmpty(tag)) {
            if (TextUtils.equals(tag, Config.STATS)) {
                ArrayList<String> stringList = new ArrayList<String>(Arrays.asList(ConfigStateCity.stats));
//                Collections.sort(stringList, Collections.reverseOrder());
                titles = stringList;
                pb_search_dialog.setVisibility(View.GONE);
                ll_spinner_state_search_dialog.setVisibility(View.VISIBLE);
                ArrayAdapter arrayAdapter = new ArrayAdapter(RecruiterActivity.this, R.layout.item_spinner, stringList);
                spinner_state_search_dialog.setAdapter(arrayAdapter);
                spinner_state_search_dialog.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String item = parent.getItemAtPosition(position).toString();
//                        if (TextUtils.equals(item, ConfigStateCity.stats[position])) {
                        Collections.sort(Functions.cheackState(position));
                        searchAdapter = new SearchAdapter(RecruiterActivity.this, Functions.cheackState(position), editText, null, ConfigStateCity.stats[position], null, null, null, alertDialog);
                        searchAdapter.notifyDataSetChanged();
                        recyclerView_search_dialog.setAdapter(searchAdapter);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            } else if (TextUtils.equals(tag, Config.EXPERIENCE)) {
                ArrayList<String> stringList = new ArrayList<String>(Arrays.asList(Config.experiences));
                titles = stringList;
                searchAdapter = new SearchAdapter(RecruiterActivity.this, titles, editText, null, null, null, null, null, alertDialog);
                searchAdapter.notifyDataSetChanged();
                recyclerView_search_dialog.setAdapter(searchAdapter);
                pb_search_dialog.setVisibility(View.GONE);
            } else {
                mRef.child(tag).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            titles.add(child.getValue(String.class));
                        }
                        if (titles != null) {
                            if (titles.size() == 0) {
                                tv_error_search_dialog.setVisibility(View.VISIBLE);
                            } else {
                                tv_error_search_dialog.setVisibility(View.GONE);
                            }
                            searchAdapter.notifyDataSetChanged();
                        }
                        pb_search_dialog.setVisibility(View.GONE);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        pb_search_dialog.setVisibility(View.GONE);
                    }
                });
            }
        }
        et_search_dialog.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                super.beforeTextChanged(s, start, count, after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                searchAdapter.getFilter().filter(s);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String img = sharedPreferencesDatabase.getData(Config.LoginImg);
        String name = sharedPreferencesDatabase.getData(Config.LoginName);
        String email = sharedPreferencesDatabase.getData(Config.LoginEmail);
        tv_name_header_drawer.setText(name);
        if (!TextUtils.isEmpty(img)) {
            Picasso.with(RecruiterActivity.this).load(img).into(circleImageView_header_drawer);
        }
        if (!TextUtils.isEmpty(name)) {
            tv_name_header_drawer.setText(name);
        }
        if (!TextUtils.isEmpty(email)) {
            tv_email_header_drawer.setText(email);
        }
    }
}
