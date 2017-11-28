package com.patil.jobsearch.Activity.Auth;

import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.patil.jobsearch.Class.CircleImageView;
import com.patil.jobsearch.Class.CropingOption;
import com.patil.jobsearch.Class.CropingOptionAdapter;
import com.patil.jobsearch.Class.Functions;
import com.patil.jobsearch.Config;
import com.patil.jobsearch.Database.SharedPreferencesDatabase;
import com.patil.jobsearch.Items.UserItem;
import com.patil.jobsearch.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.patil.jobsearch.Activity.Auth.LoginActivity.sharedPreferencesDatabase;

public class SignUpActivity extends AppCompatActivity {
    private final static int REQUEST_PERMISSION_REQ_CODE = 34;
    private static final int CAMERA_CODE = 101, GALLERY_CODE = 201, CROPING_CODE = 301;
    private static final int REQUEST_CODE_CHOOSE = 23;
    boolean doubleBackToExitPressedOnce = false;
    String img_download_url = "";
    private Button btn_sign_up;
    private ImageView iv_mobile_verified_sign_up, iv_email_verified_sign_up;
    private CircleImageView circleImageView_sign_up;
    private EditText et_full_name_sign_up, et_mobile_sign_up, et_email_sign_up, et_password_sign_up, et_confirm_password_sign_up;
    private ProgressBar pb_sign_up;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference mFirebaseDatabaseReference;
    private Uri mImageCaptureUri;
    private File outPutFile = null;
    private FloatingActionButton fab_iv_edit_sign_up;
    private DatabaseReference mRef;
    private SharedPreferencesDatabase sharedPreferencesDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mRef = FirebaseDatabase.getInstance().getReference();
        outPutFile = new File(android.os.Environment.getExternalStorageDirectory(), ".temp.jpg");
        auth = FirebaseAuth.getInstance();
        if (database == null) {
            database = FirebaseDatabase.getInstance();
        }
        sharedPreferencesDatabase = new SharedPreferencesDatabase(SignUpActivity.this);
        sharedPreferencesDatabase.createDatabase();
        mFirebaseDatabaseReference = database.getReference();
        mFirebaseDatabaseReference.keepSynced(true);

        circleImageView_sign_up = (CircleImageView) findViewById(R.id.circleImageView_sign_up);
        iv_mobile_verified_sign_up = (ImageView) findViewById(R.id.iv_mobile_verified_sign_up);
        iv_email_verified_sign_up = (ImageView) findViewById(R.id.iv_email_verified_sign_up);
        et_full_name_sign_up = (EditText) findViewById(R.id.et_full_name_sign_up);
        et_mobile_sign_up = (EditText) findViewById(R.id.et_mobile_sign_up);
        et_email_sign_up = (EditText) findViewById(R.id.et_email_sign_up);
        et_password_sign_up = (EditText) findViewById(R.id.et_password_sign_up);
        et_confirm_password_sign_up = (EditText) findViewById(R.id.et_confirm_password_sign_up);
        pb_sign_up = (ProgressBar) findViewById(R.id.pb_sign_up);
        btn_sign_up = (Button) findViewById(R.id.btn_sign_up);
        fab_iv_edit_sign_up = (FloatingActionButton) findViewById(R.id.fab_iv_edit_sign_up);
        String name = LoginActivity.sharedPreferencesDatabase.getData(Config.LoginName);
        String email = LoginActivity.sharedPreferencesDatabase.getData(Config.LoginEmail);
        String img = LoginActivity.sharedPreferencesDatabase.getData(Config.LoginImg);

        if (!TextUtils.isEmpty(img)) {
            Picasso.with(SignUpActivity.this).load(img).into(circleImageView_sign_up);
        }
        et_mobile_sign_up.setText(LoginActivity.sharedPreferencesDatabase.getData(Config.LoginMobile));
        if (TextUtils.isEmpty(LoginActivity.sharedPreferencesDatabase.getData(Config.LoginPhoneVerified))) {
            iv_mobile_verified_sign_up.setVisibility(View.GONE);
        } else {
            iv_mobile_verified_sign_up.setVisibility(View.VISIBLE);
            et_mobile_sign_up.setFocusable(false);
            et_mobile_sign_up.setClickable(false);
            et_mobile_sign_up.setLongClickable(false);
        }

        et_full_name_sign_up.setText(name);
        if (TextUtils.isEmpty(LoginActivity.sharedPreferencesDatabase.getData(Config.LoginPhoneVerified))) {
            iv_email_verified_sign_up.setVisibility(View.GONE);
        } else {
            et_email_sign_up.setText(email);
            iv_email_verified_sign_up.setVisibility(View.VISIBLE);
            et_email_sign_up.setFocusable(false);
            et_email_sign_up.setClickable(false);
            et_email_sign_up.setLongClickable(false);

        }

        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s_et_full_name_sign_up = et_full_name_sign_up.getText().toString();
                String s_et_email_sign_up = et_email_sign_up.getText().toString();
                String s_et_password_sign_up = et_password_sign_up.getText().toString();
                String s_et_confirm_password_sign_up = et_confirm_password_sign_up.getText().toString();

                if (TextUtils.isEmpty(s_et_full_name_sign_up)) {
                    Toast.makeText(SignUpActivity.this, "Please enter Full Name", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(s_et_email_sign_up)) {
                    Toast.makeText(SignUpActivity.this, "Please enter Email", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(s_et_password_sign_up)) {
                    Toast.makeText(SignUpActivity.this, "Please enter Password", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(s_et_confirm_password_sign_up)) {
                    Toast.makeText(SignUpActivity.this, "Please enter Confirm Password", Toast.LENGTH_SHORT).show();
                } else if (s_et_confirm_password_sign_up.length() < 6) {
                    Toast.makeText(SignUpActivity.this, "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                } else if (!TextUtils.equals(s_et_password_sign_up, s_et_confirm_password_sign_up)) {
                    Toast.makeText(SignUpActivity.this, "Password not match!", Toast.LENGTH_SHORT).show();
                } else {
                    Functions.viewProgress(true, btn_sign_up, pb_sign_up);
                    String login_device_token = sharedPreferencesDatabase.getData(Config.LoginDeviceToken);
                    getSignUp(s_et_full_name_sign_up, s_et_email_sign_up, "", s_et_confirm_password_sign_up, "",login_device_token, "");

                }
            }
        });
        circleImageView_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImageOption();
            }
        });
        fab_iv_edit_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageOption();
            }
        });
    }

    public void getSignUp(final String name, final String email, final String mobile, final String password, final String roll,final String device_token, final String query) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    if (password.length() < 6) {
                        Toast.makeText(SignUpActivity.this, "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Authentication failed, check your email and password or sign up", Toast.LENGTH_LONG).show();
                    }
                    Functions.viewProgress(false, btn_sign_up, pb_sign_up);
                } else {
                    uploadImage(Functions.getUID(), Functions.getUID(), name, email, mobile, password, roll,device_token, query);
                }
            }
        });
    }

    public void uploadImage(String path_name, String id, final String name, final String email, final String mobile, final String password, final String roll,final String device_token, final String query) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://job-search-4ff12.appspot.com/").child(Config.PROFILE).child(path_name);
        if (outPutFile != null) {
            UploadTask uploadTask = storageRef.putFile(Uri.fromFile(outPutFile));
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Functions.viewProgress(false, btn_sign_up, pb_sign_up);
                    Toast.makeText(SignUpActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    img_download_url = downloadUrl.toString();
                    sharedPreferencesDatabase.addData(Config.LoginImg, img_download_url);
                    addData(btn_sign_up, pb_sign_up, Functions.getUID(), img_download_url, name, email, mobile, password, roll,device_token, query);
                }
            });
        }
    }

    public void addData(final Button btn, final ProgressBar pb, String id, String img, final String name, final String email, final String mobile,
                        String password, String roll,String device_token, String query) {
        Functions.viewProgress(true, btn, pb);
        UserItem userItem = new UserItem(id, img, name, email, mobile, password, roll,device_token, query);
        mRef.child(Config.USERS).child(Functions.getUID()).setValue(userItem).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(SignUpActivity.this, "Done", Toast.LENGTH_SHORT).show();
                Functions.viewProgress(false, btn, pb);
                sharedPreferencesDatabase.addData(Config.LoginName, name);
                sharedPreferencesDatabase.addData(Config.LoginEmail, email);
                sharedPreferencesDatabase.addData(Config.LoginImg, mobile);
                startActivity(new Intent(SignUpActivity.this, PhoneActivity.class));
                finish();
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Functions.viewProgress(false, btn, pb);
                Toast.makeText(SignUpActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void selectImageOption() {
        final CharSequence[] items = {"Capture Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Capture Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp1.jpg");
                    mImageCaptureUri = Uri.fromFile(f);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                    startActivityForResult(intent, CAMERA_CODE);

                } else if (items[item].equals("Choose from Gallery")) {
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, GALLERY_CODE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK && null != data) {
            mImageCaptureUri = data.getData();
            System.out.println("Gallery Image URI : " + mImageCaptureUri);
            CropingIMG();

        } else if (requestCode == CAMERA_CODE && resultCode == Activity.RESULT_OK) {

            System.out.println("Camera Image URI : " + mImageCaptureUri);
            CropingIMG();
        } else if (requestCode == CROPING_CODE) {
            try {
                if (outPutFile.exists()) {
                    Picasso.with(SignUpActivity.this).load(outPutFile).skipMemoryCache().into(circleImageView_sign_up, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), "Error while save image", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void CropingIMG() {
        final ArrayList<CropingOption> cropOptions = new ArrayList<CropingOption>();
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        int size = list.size();
        if (size == 0) {
            Toast.makeText(this, "Cann't find image croping app", Toast.LENGTH_SHORT).show();
            return;
        } else {
            intent.setData(mImageCaptureUri);
            intent.putExtra("outputX", 512);
            intent.putExtra("outputY", 512);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outPutFile));

            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = (ResolveInfo) list.get(0);

                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROPING_CODE);
            } else {
                for (ResolveInfo res : list) {
                    final CropingOption co = new CropingOption();
                    co.title = getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon = getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);
                    co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    cropOptions.add(co);
                }

                CropingOptionAdapter adapter = new CropingOptionAdapter(getApplicationContext(), cropOptions);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose Croping App");
                builder.setCancelable(false);
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        startActivityForResult(cropOptions.get(item).appIntent, CROPING_CODE);
                    }
                });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        if (mImageCaptureUri != null) {
                            getContentResolver().delete(mImageCaptureUri, null, null);
                            mImageCaptureUri = null;
                        }
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
