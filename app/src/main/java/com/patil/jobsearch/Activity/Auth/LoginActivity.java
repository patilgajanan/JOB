package com.patil.jobsearch.Activity.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.patil.jobsearch.Activity.Admin.AdminActivity;
import com.patil.jobsearch.Activity.RecruiterActivity;
import com.patil.jobsearch.Activity.SeekerActivity;
import com.patil.jobsearch.Class.Functions;
import com.patil.jobsearch.Config;
import com.patil.jobsearch.Database.SharedPreferencesDatabase;
import com.patil.jobsearch.Items.UserItem;
import com.patil.jobsearch.R;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    public static SharedPreferencesDatabase sharedPreferencesDatabase;
    protected DatabaseReference mRef;
    private Button btn_sign_up_login, btn_login;
    private ProgressBar pb_sign_up_login, pb_login;
    private FirebaseAuth mAuth;
    private int RC_GOOGLE_LOGIN = 1;
    private GoogleApiClient mGoogleapiclient;
    private EditText et_email_login, et_password_login;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FacebookSdk.sdkInitialize(LoginActivity.this);
        auth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
        setContentView(R.layout.activity_login);
        sharedPreferencesDatabase = new SharedPreferencesDatabase(LoginActivity.this);
        sharedPreferencesDatabase.createDatabase();

        String device_token = sharedPreferencesDatabase.getData(Config.LoginDeviceToken);
        device_token = FirebaseInstanceId.getInstance().getToken();

        sharedPreferencesDatabase.addData(Config.LoginDeviceToken, device_token);
//        callbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            if (!TextUtils.isEmpty(sharedPreferencesDatabase.getData(Config.LoginId)) &&
                    !TextUtils.isEmpty(sharedPreferencesDatabase.getData(Config.LoginName)) &&
                    !TextUtils.isEmpty(sharedPreferencesDatabase.getData(Config.LoginEmail)) &&
                    !TextUtils.isEmpty(sharedPreferencesDatabase.getData(Config.LoginMobile)) &&
                    !TextUtils.isEmpty(sharedPreferencesDatabase.getData(Config.LoginRoll))) {

                if (TextUtils.equals(sharedPreferencesDatabase.getData(Config.LoginRoll), Config.Seekers)) {
                    startActivity(new Intent(LoginActivity.this, RecruiterActivity.class));
                    finish();
                } else if (TextUtils.equals(sharedPreferencesDatabase.getData(Config.LoginRoll), Config.Recruiters)) {
                    startActivity(new Intent(LoginActivity.this, SeekerActivity.class));
                    finish();
                }
            } else {
                updateUI(mAuth.getCurrentUser());
            }
        }

        et_email_login = (EditText) findViewById(R.id.et_email_login);
        et_password_login = (EditText) findViewById(R.id.et_password_login);

        btn_sign_up_login = (Button) findViewById(R.id.btn_sign_up_login);
        btn_login = (Button) findViewById(R.id.btn_login);
        pb_login = (ProgressBar) findViewById(R.id.pb_login);
        SignInButton loginButtongmail = (SignInButton) findViewById(R.id.button_theoffertime_login_screen_google);
        loginButtongmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        btn_sign_up_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = et_email_login.getText().toString();
                final String password = et_password_login.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                } else {
                    Functions.viewProgress(true, btn_login, pb_login);
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        // there was an error
                                        if (password.length() < 6) {
                                            Toast.makeText(LoginActivity.this, "Password too short, enter minimum 6 characters!", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Authentication failed, check your email and password or sign up", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        updateUI(mAuth.getCurrentUser());
//                                        Intent intent = new Intent(LoginActivity.this, SplashScreenActivity.class);
//                                        startActivity(intent);
//                                        finish();
                                    }
                                    Functions.viewProgress(true, btn_login, pb_login);

                                }
                            });
                }
                //startActivity(new Intent(LoginActivity.this, YourSelfActivity.class));
            }
        });

//        LoginButton loginButton = (LoginButton) findViewById(R.id.login_facebook);
//        loginButton.setReadPermissions("email", "public_profile");
//        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                handleFacebookAccessTokens(loginResult.getAccessToken());
//            }
//
//            @Override
//            public void onCancel() {
//
//                // [START_EXCLUDE]
//                updateUI(null);
//                // [END_EXCLUDE]
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//
//                // [START_EXCLUDE]
//                updateUI(null);
//                // [END_EXCLUDE]
//            }
//        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_api_client_id))
                .requestEmail()
                .build();
        mGoogleapiclient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleapiclient);
        startActivityForResult(signInIntent, RC_GOOGLE_LOGIN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_LOGIN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(LoginActivity.this, "Google Sign In failed", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                        } else {
                            task.getException().getMessage();
                            String s = ((FirebaseAuthException) task.getException()).getErrorCode();
                            Toast.makeText(LoginActivity.this, s,
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

//    private void handleFacebookAccessTokens(AccessToken token) {
//        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
//                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                            finish();
//                        } else {
//                            Toast.makeText(LoginActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
//                        }
//                    }
//                });
//    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String user_name = user.getDisplayName();
            String user_img = "";
            if (TextUtils.isEmpty(user_name)) {
                user_name = sharedPreferencesDatabase.getData(Config.LoginName);
            }
            if (user.getPhotoUrl() != null) {
                user_img = user.getPhotoUrl().toString();
                sharedPreferencesDatabase.addData(Config.LoginImg, user_img);
            } else {
                user_img = sharedPreferencesDatabase.getData(Config.LoginImg);
            }
            final String user_email = user.getEmail();
            sharedPreferencesDatabase.addData(Config.LoginId, Functions.getUID());
            sharedPreferencesDatabase.addData(Config.LoginName, user_name);
            sharedPreferencesDatabase.addData(Config.LoginEmail, user_email);

            final String finalUser_img = user_img;
            final String finalUser_name = user_name;
            if (!TextUtils.equals(user_email, Config.ADMIN_EMAIL)) {
                mRef.child(Config.USERS).child(Functions.getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String id = (String) dataSnapshot.child("id").getValue();
                        String img = (String) dataSnapshot.child("img").getValue();
                        String name = (String) dataSnapshot.child("name").getValue();
                        String email = (String) dataSnapshot.child("email").getValue();
                        String mobile = (String) dataSnapshot.child("mobile").getValue();
                        String password = (String) dataSnapshot.child("password").getValue();
                        String roll = (String) dataSnapshot.child("roll").getValue();
                        String device_token = (String) dataSnapshot.child("device_token").getValue();
                        String query = (String) dataSnapshot.child("query").getValue();
                        Functions.viewProgress(true, btn_login, pb_login);
                        if (TextUtils.isEmpty(img) && TextUtils.isEmpty(name) && TextUtils.isEmpty(email)) {
                            String uid = Functions.getUID();
                            device_token = sharedPreferencesDatabase.getData(Config.LoginDeviceToken);
                            UserItem userItem = new UserItem(uid, finalUser_img, finalUser_name, user_email, mobile, password, roll, device_token, query);
                            mRef.child(Config.USERS).child(uid).setValue(userItem);
                            if (TextUtils.isEmpty(mobile)) {
                                Intent i = new Intent(LoginActivity.this, com.patil.jobsearch.Activity.PhoneActivity.class);
                                i.putExtra("i_id", id);
                                i.putExtra("i_img", img);
                                i.putExtra("i_name", name);
                                i.putExtra("i_email", email);
                                i.putExtra("i_mobile", mobile);
                                i.putExtra("i_roll", roll);
                                i.putExtra("i_device_token", device_token);
                                i.putExtra("i_query", query);
                                startActivity(i);
                                finish();
                            } else {
                                if (TextUtils.equals(roll, Config.Seekers)) {
                                    startActivity(new Intent(LoginActivity.this, RecruiterActivity.class));
                                    finish();
                                } else if (TextUtils.equals(roll, Config.Recruiters)) {
                                    startActivity(new Intent(LoginActivity.this, SeekerActivity.class));
                                    finish();
                                }
                            }

                        } else {
                            device_token = sharedPreferencesDatabase.getData(Config.LoginDeviceToken);
                            UserItem userItem = new UserItem(id, img, name, email, mobile, password, roll, device_token, query);
                            mRef.child(Config.USERS).child(Functions.getUID()).setValue(userItem);
                            if (TextUtils.isEmpty(mobile)) {
                                Intent i = new Intent(LoginActivity.this, com.patil.jobsearch.Activity.PhoneActivity.class);
                                i.putExtra("i_id", id);
                                i.putExtra("i_img", img);
                                i.putExtra("i_name", name);
                                i.putExtra("i_email", email);
                                i.putExtra("i_mobile", mobile);
                                i.putExtra("i_roll", roll);
                                i.putExtra("i_device_token", device_token);
                                i.putExtra("i_query", query);
                                startActivity(i);
                                finish();
                            } else if (TextUtils.isEmpty(roll)) {
                                Intent i = new Intent(LoginActivity.this, com.patil.jobsearch.Activity.YourSelfActivity.class);
                                i.putExtra("i_id", id);
                                i.putExtra("i_img", img);
                                i.putExtra("i_name", name);
                                i.putExtra("i_email", email);
                                i.putExtra("i_mobile", mobile);
                                i.putExtra("i_roll", roll);
                                i.putExtra("i_device_token", device_token);
                                i.putExtra("i_query", query);
                                startActivity(i);
                                finish();
                            } else {
                                if (TextUtils.equals(roll, Config.Seekers)) {
                                    startActivity(new Intent(LoginActivity.this, RecruiterActivity.class));
                                    finish();
                                } else if (TextUtils.equals(roll, Config.Recruiters)) {
                                    startActivity(new Intent(LoginActivity.this, SeekerActivity.class));
                                    finish();
                                }

                                if (!TextUtils.isEmpty(id)) {
                                    sharedPreferencesDatabase.addData(Config.LoginId, id);
                                }
                                if (!TextUtils.isEmpty(img)) {
                                    sharedPreferencesDatabase.addData(Config.LoginImg, img);
                                }
                                if (!TextUtils.isEmpty(name)) {
                                    sharedPreferencesDatabase.addData(Config.LoginName, name);
                                }
                                if (!TextUtils.isEmpty(email)) {
                                    sharedPreferencesDatabase.addData(Config.LoginEmail, email);
                                }
                                if (!TextUtils.isEmpty(mobile)) {
                                    sharedPreferencesDatabase.addData(Config.LoginMobile, mobile);
                                }
                                if (!TextUtils.isEmpty(password)) {
                                    sharedPreferencesDatabase.addData(Config.LoginPassword, password);
                                }
                                if (!TextUtils.isEmpty(roll)) {
                                    sharedPreferencesDatabase.addData(Config.LoginRoll, roll);
                                }
                                if (!TextUtils.isEmpty(device_token)) {
                                    sharedPreferencesDatabase.addData(Config.LoginDeviceToken, device_token);
                                }
                                if (!TextUtils.isEmpty(query)) {
                                    sharedPreferencesDatabase.addData(Config.LoginQuery, query);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                finish();
            }

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
