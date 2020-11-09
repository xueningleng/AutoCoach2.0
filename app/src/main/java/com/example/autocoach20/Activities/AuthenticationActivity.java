package com.example.autocoach20.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.autocoach20.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class AuthenticationActivity extends AppCompatActivity{
    private static final String TAG = null;
    private static final int MY_REQUEST_CODE = 1234;

    List<AuthUI.IdpConfig> providers;
    public FirebaseAuth mAuth;

    //UI fields
    EditText userEmail;
    EditText userPassword;
    Button btn_signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        Intent intent = getIntent();


        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build());
        showSignInOptions();

        mAuth = FirebaseAuth.getInstance();

        userEmail = (EditText)findViewById(R.id.editTextTextPersonName);
        userEmail.getText().toString();

        userPassword = (EditText)findViewById(R.id.editTextTextPassword);
        userPassword.getText().toString();

    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    private void showSignInOptions (){

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.logobw)
                        .build(),
                MY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        //setContentView(R.layout.activity_firebase_ui);


        if (requestCode == MY_REQUEST_CODE){
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK){
                //Get User
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                //Show email on Toast
                Toast.makeText(this, ""+user.getEmail(), Toast.LENGTH_SHORT).show();

                //Here we are switching to the other screen once being logged in
                //Intent startAutoCoach = new Intent(AuthenticationActivity.this, StartAutoCoachActivity.class);
                finish();
                //startActivity(startAutoCoach);

                //set Button signOut
                //btn_sign_out.setEnabled(true);
            }

            else {
                Toast.makeText(this, ""+response.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void updateUI(@Nullable FirebaseUser user) {
        // No-op
    }
    private void signIn (String email, String password){

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            //Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                            //        Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });

    }
    private void createAccount (String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            //Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                            //        Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
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

    public void getGoogleCredentials() {
        String googleIdToken = "";
        // [START auth_google_cred]
        AuthCredential credential = GoogleAuthProvider.getCredential(googleIdToken, null);
        // [END auth_google_cred]
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
