package zw.co.hariplay.hariplay.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import java.util.concurrent.TimeUnit;

import zw.co.hariplay.hariplay.Home.HomeActivity;
import zw.co.hariplay.hariplay.R;
import zw.co.hariplay.hariplay.Utils.FirebaseMethods;
import zw.co.hariplay.hariplay.models.User;

/**
 * Created by User on 6/19/2017.
 */

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final String USER_NAME = "username";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Context mContext;
    private ProgressBar mProgressBar;
    private EditText mEmail, mPassword;
    private TextView mPleaseWait;
    IntlPhoneInput phoneInputView;

    private String phoneNumber;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mPleaseWait = (TextView) findViewById(R.id.pleaseWait);
        mEmail = (EditText) findViewById(R.id.input_email);
        mPassword = (EditText) findViewById(R.id.input_password);
        mContext = LoginActivity.this;
        phoneInputView = (IntlPhoneInput) findViewById(R.id.my_phone_input);

        Log.d(TAG, "onCreate: started.");

        mPleaseWait.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);

        setupFirebaseAuth();
        init();

    }

    private boolean isStringNull(String string){
        Log.d(TAG, "isStringNull: checking string if null.");

        if(string.equals("")){
            return true;
        }
        else{
            return false;
        }
    }

    private void saveUserInfo(FirebaseUser user){

        Toast.makeText(this,user.getUid(),Toast.LENGTH_SHORT).show();

        //get the user object
        FirebaseDatabase.getInstance().getReference("users/"+user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Toast.makeText(LoginActivity.this,"Data has been collected",Toast.LENGTH_LONG).show();

                SharedPreferences prefs = getSharedPreferences("UserDetails",
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(USER_NAME, dataSnapshot.child(USER_NAME).getValue(String.class));
                editor.putString("user_id",  dataSnapshot.child("user_id").getValue(String.class));
                editor.putString("email",dataSnapshot.child("email").getValue(String.class));
                editor.commit();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this,databaseError.getDetails(),Toast.LENGTH_LONG).show();
            }
        });
    }

     /*
    ------------------------------------ Firebase ---------------------------------------------
     */

     private void init(){

         //initialize the button for logging in
         Button btnLogin = (Button) findViewById(R.id.btn_login);
         btnLogin.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Log.d(TAG, "onClick: attempting to log in.");

                 String email = mEmail.getText().toString();
                 String password = mPassword.getText().toString();

                 if (phoneInputView.isValid()){
                     mProgressBar.setVisibility(View.VISIBLE);
                     mPleaseWait.setVisibility(View.VISIBLE);
                     myLogin();
                     mProgressBar.setVisibility(View.GONE);
                     mPleaseWait.setVisibility(View.GONE);
                 }else{
                     Toast.makeText(LoginActivity.this,"Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                 }
                 /*if(isStringNull(email) && isStringNull(password)){
                     Toast.makeText(mContext, "You must fill out all the fields", Toast.LENGTH_SHORT).show();
                 }else{
                     mProgressBar.setVisibility(View.VISIBLE);
                     mPleaseWait.setVisibility(View.VISIBLE);

                     mAuth.signInWithEmailAndPassword(email, password)
                             .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                 @Override
                                 public void onComplete(@NonNull Task<AuthResult> task) {
                                     Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                                     FirebaseUser user = mAuth.getCurrentUser();

                                     // If sign in fails, display a message to the user. If sign in succeeds
                                     // the auth state listener will be notified and logic to handle the
                                     // signed in user can be handled in the listener.
                                     if (!task.isSuccessful()) {
                                         Log.w(TAG, "signInWithEmail:failed", task.getException());

                                         Toast.makeText(LoginActivity.this, getString(R.string.auth_failed),
                                                 Toast.LENGTH_SHORT).show();
                                         mProgressBar.setVisibility(View.GONE);
                                         mPleaseWait.setVisibility(View.GONE);
                                     }
                                     else{
                                         try{
                                             if(user.isEmailVerified()){
                                                 Log.d(TAG, "onComplete: success. email is verified.");
                                                 Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                 startActivity(intent);
                                             }else{
                                                 Toast.makeText(mContext, "Email is not verified \n check your email inbox.", Toast.LENGTH_SHORT).show();
                                                 mProgressBar.setVisibility(View.GONE);
                                                 mPleaseWait.setVisibility(View.GONE);
                                                 mAuth.signOut();
                                             }
                                         }catch (NullPointerException e){
                                             Log.e(TAG, "onComplete: NullPointerException: " + e.getMessage() );
                                         }
                                     }

                                     // ...
                                 }
                             });
                 }*/

             }
         });

         TextView linkSignUp = (TextView) findViewById(R.id.link_signup);
         linkSignUp.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Log.d(TAG, "onClick: navigating to register screen");
                 Intent intent = new Intent(LoginActivity.this,ConfirmNumberActivity.class);
                 startActivity(intent);
             }
         });

         /*
         If the user is logged in then navigate to HomeActivity and call 'finish()'
          */
         if(mAuth.getCurrentUser() != null){
             Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
             startActivity(intent);
             finish();
         }
     }

     private void myLogin(){
         if(phoneInputView.isValid()) {
             phoneNumber = phoneInputView.getNumber();

             PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, LoginActivity.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                 @Override
                 public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                     signInWithPhoneAuthCredential(phoneAuthCredential);
                 }

                 @Override
                 public void onVerificationFailed(FirebaseException e) {
                     Toast.makeText(mContext,"Error : " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();

                 }
             });
         }else{
             Toast.makeText(LoginActivity.this,"phone number entered is invalid", Toast.LENGTH_SHORT).show();
         }
     }
    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    public void signInWithPhoneAuthCredential(PhoneAuthCredential credential){
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithCredentials:onComplete:" + task.isSuccessful());

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Toast.makeText(mContext, R.string.auth_failed,
                            Toast.LENGTH_SHORT).show();

                }
                else if(task.isSuccessful()){
                    //send verificaton email
                    //sendVerificationEmail();

                    saveUserInfo(mAuth.getCurrentUser());

                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    Log.d(TAG, "onComplete: Authstate changed: " + mAuth.getCurrentUser().getUid());
                }
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
























