package com.example.autocoach20.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.autocoach20.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class SignUpActivity extends AppCompatActivity{
    private static final String TAG = null;
    private static final int MY_REQUEST_CODE = 1234;

    List<AuthUI.IdpConfig> providers;
    public FirebaseAuth mAuth;

    //UI fields
    EditText userEmail;
    EditText userPassword;
    Button btn_signIn, homeBtn;
    TextView resultHint;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Intent intent = getIntent();
        initializeUI();
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build());

        mAuth = FirebaseAuth.getInstance();

        btn_signIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                createAccount();
            }
        });
        homeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){finish();}
        });
    }


    private void initializeUI(){
        userEmail = (EditText)findViewById(R.id.uEmail);
        userPassword = (EditText)findViewById(R.id.uPword);
        btn_signIn = findViewById(R.id.signUpButton);
        homeBtn = findViewById(R.id.returnButton);
        resultHint = findViewById(R.id.result);

    }


    private void updateUI(@Nullable FirebaseUser user) {
        // No-op
    }

    private void createAccount (){
        String email, password;
        email = userEmail.getText().toString();
        password = userPassword.getText().toString();
        if (TextUtils.isEmpty(email)){
            Toast.makeText(getApplicationContext(), "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_LONG).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            resultHint.setText("Registration Successful");
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(getApplicationContext(), "Registration successful", Toast.LENGTH_LONG).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                            startActivity(intent);
                        } else {
                            resultHint.setText("Registration Failed");
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });

    }

    public FirebaseUser getCurrentUser (){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
        }

        return user;

    }

    public void reauthWithLink(String email, String emailLink) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // [START auth_reauth_with_link]
        // Construct the email link credential from the current URL.
        AuthCredential credential =
                EmailAuthProvider.getCredentialWithLink(email, emailLink);

        // Re-authenticate the user with this credential.
        auth.getCurrentUser().reauthenticateAndRetrieveData(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // User is now successfully reauthenticated
                        } else {
                            Log.e((String) TAG, "Error reauthenticating", task.getException());
                        }
                    }
                });
        // [END auth_reauth_with_link]
    }

    public void differentiateLink(String email) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // [START auth_differentiate_link]
        auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()) {
                            SignInMethodQueryResult result = task.getResult();
                            List<String> signInMethods = result.getSignInMethods();
                            if (signInMethods.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)) {
                                // User can sign in with email/password
                            } else if (signInMethods.contains(EmailAuthProvider.EMAIL_LINK_SIGN_IN_METHOD)) {
                                // User can sign in with email/link
                            }
                        } else {
                            Log.e((String) TAG, "Error getting sign in methods for user", task.getException());
                        }
                    }
                });
        // [END auth_differentiate_link]
    }



    public void getEmailCredentials() {
        String email = "";
        String password = "";
        // [START auth_email_cred]
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        // [END auth_email_cred]
    }

    public void signOut() {
        // [START auth_sign_out]
        FirebaseAuth.getInstance().signOut();
        // [END auth_sign_out]
    }

}
