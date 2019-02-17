package blog.pyimlife.electronichealthrecord;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Context context;
    private FirebaseAuth mAuth;
    //private FirebaseAuth.AuthStateListener mAuthListener;

    public EditText mEmail, mPassword;
    public Button btnLogIn;


    private final String emailAddress = "@ehr.vn";
    private final String patientFormat = "patid";
    Boolean isPatient = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        context = this;
        mAuth = FirebaseAuth.getInstance();
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user  = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    Log.d(TAG, "signInWithEmail:success");
//                    updateUI(user);
//                } else {
//                    Log.d(TAG, "signOut:success");
//                }
//            }
//        };

        mEmail = (EditText) findViewById(R.id.edtUsername);
        mPassword = (EditText) findViewById(R.id.edtPassword);
        btnLogIn = (Button) findViewById(R.id.btnLogin);

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().toLowerCase();
                String password = mPassword.getText().toString();

                if (!email.equals("") && !password.equals("")) {
                    email = email + emailAddress;
                    signIn(email, password);
                } else {
                    toastMessage("Please fill in email and password!");
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        //mAuth.addAuthStateListener(mAuthListener);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (mAuthListener != null) {
//            mAuth.removeAuthStateListener(mAuthListener);
//        }
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //toastMessage("signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithEmail:failure", task.getException());
                            toastMessage("Authentication failed.");
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        // Start new Activity
        // Doctor or Patient?
        Intent intent;
        String email = user.getEmail().toLowerCase();
        if (email.contains(patientFormat)) {
            intent = new Intent(context, PatientInfo.class);
        } else {
            intent = new Intent(context, DoctorUI.class);
        }
        startActivity(intent);
    }

    /**
     * Toast a message
     * @param message
     */
    private void toastMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
