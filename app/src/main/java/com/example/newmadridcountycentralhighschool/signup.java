package com.example.newmadridcountycentralhighschool;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class signup extends AppCompatActivity {
    private RadioButton stu,parent,ass,dr;


    ImageView userimage;
    Button signupButton;
    ProgressBar signupProgress;
    static int PReqCode = 1;
    static int REQUESCODE= 1;
    Uri pickedimageUri ;
    private FirebaseAuth mAuth;



    private EditText username,userid,useremail,userpassword,userconpassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        username=findViewById(R.id.edite_name);
        useremail=findViewById(R.id.edite_email);
        userpassword=findViewById(R.id.edite_password);
        userconpassword=findViewById(R.id.edite_confirmpassword);


        //signup_contents

        signupButton = findViewById(R.id.signup_button);
        signupProgress = findViewById(R.id.signup_progressBar);
        signupProgress.setVisibility(View.INVISIBLE);

        mAuth=FirebaseAuth.getInstance();





        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupProgress.setVisibility(View.VISIBLE);
                signupButton.setVisibility(View.INVISIBLE);
                final String name = username.getText().toString();
                final String email= useremail.getText().toString();
                final String password = userpassword.getText().toString();
                final String conpassword = userconpassword.getText().toString();



                if (name.isEmpty() ||email.isEmpty() || password.isEmpty() || conpassword.isEmpty())
                {
                    showMessage("Please Verify All Fields");
                    signupButton.setVisibility(View.VISIBLE);
                    signupProgress.setVisibility(View.INVISIBLE);
                }
                else
                {
                    updateUi();
                    CreateUserAccount(name,email,password);


                }
            }
        });

        userimage =findViewById(R.id.user_image);
        userimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= 22)
                {
                    checkAndRequestForPermission();
                }
                else
                {
                    openGallery();
                }
            }
        });
    }

    private void CreateUserAccount(final String name, String email, String password)
    {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    showMessage("Account Created");
                    updateUserInfo(name,pickedimageUri,mAuth.getCurrentUser());

                }
                else
                {
                    showMessage("Account Creation failed"+ Objects.requireNonNull(task.getException()).getMessage());
                    signupButton.setVisibility(View.VISIBLE);
                    signupProgress.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    private void updateUserInfo(final String name, Uri pickedimageUri, final FirebaseUser currentUser)
    {

        StorageReference mstorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFiledPath =mstorage.child(Objects.requireNonNull(pickedimageUri.getLastPathSegment()));
        imageFiledPath.putFile(pickedimageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                imageFiledPath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri)
                    {
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(name).setPhotoUri(uri).build();
                        currentUser.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if(task.isSuccessful())
                                {
                                    updateUi();


                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private void updateUi()
    {
        Intent homeActivity=new Intent(getApplicationContext(),CheckoutActivity.class);
        startActivity(homeActivity);
        finish();
    }

    private void showMessage(String message)
    {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }


    private void openGallery()
    {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);


    }

    private void checkAndRequestForPermission()
    {
        if (ContextCompat.checkSelfPermission(signup.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(signup.this,Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                Toast.makeText(signup.this,"Please accept for required permission",Toast.LENGTH_SHORT).show();
            }
            else
            {
                ActivityCompat.requestPermissions(signup.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PReqCode);
            }
        }
        else
        {
            openGallery();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data !=null)
        {
            pickedimageUri = data.getData();
            userimage.setImageURI(pickedimageUri);


        }
    }
}
