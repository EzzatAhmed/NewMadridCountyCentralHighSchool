package com.example.newmadridcountycentralhighschool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class login extends Activity {

    private EditText usermail,userpassword;
    private Button loginButton;
    private ProgressBar loginProgress;
    private FirebaseAuth mAuth;

    private TextView contacts,about_us;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);


        usermail=findViewById(R.id.mail);
        userpassword=findViewById(R.id.edite_password);
        loginButton = findViewById(R.id.login_button);
        loginProgress = findViewById(R.id.login_progresbar);
        mAuth=FirebaseAuth.getInstance();
        //home=new Intent(this, com.example.new_desgin.home.class);

        loginProgress.setVisibility(View.INVISIBLE);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginProgress.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.INVISIBLE);

                final String mail=usermail.getText().toString();
                final String password=userpassword.getText().toString();

                if (mail.isEmpty() || password.isEmpty())
                {
                    showMessage("Please Verify All Fields");
                }
                else
                {
                    signIn(mail,password);
                }
            }
        });

        contacts=(TextView)findViewById(R.id.contacts);
        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_contacts();
            }
        });

        about_us=(TextView)findViewById(R.id.About_us);
        about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_about_us();
            }
        });

        TextView signup = findViewById(R.id.signupid);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup_acc();
            }
        });
    }

    public void signup_acc()
    {
        Intent signup=new Intent(this ,signup.class);
        startActivity(signup);
    }


    public void open_contacts()
    {
        Intent contacts=new Intent(this ,contacts.class);
        startActivity(contacts);
    }


    public void open_about_us()
    {
        Intent about_us=new Intent(this ,about_us.class);
        startActivity(about_us);
    }

    private void signIn(String mail,String password)
    {
        mAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    loginProgress.setVisibility(View.INVISIBLE);
                    loginButton.setVisibility(View.VISIBLE);
                    updateUI();
                }
                else
                    showMessage(Objects.requireNonNull(task.getException()).getMessage());
                loginProgress.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void updateUI()
    {
        Intent home=new Intent(this ,CheckoutActivity.class);
        startActivity(home);
        this.finish();
    }

    private void showMessage(String message)
    {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user= mAuth.getCurrentUser();

        if (user !=null)
        {
            updateUI();
        }
    }

}