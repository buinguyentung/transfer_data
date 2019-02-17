package blog.pyimlife.electronichealthrecord;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PatientInfo extends AppCompatActivity {

    private static final String TAG = "PatientInfo";

    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore myFirestoreRef;
    CollectionReference myColRef;

    private final String PATIENTS_COLLECTION = "patients-collection";
    private String patEmail = "";
    private String patId = "";

    TextView tvPatient;
    ListView lvItem;
    ArrayList<String> listPatient;
    ArrayList<String> listKey;
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);

        if (getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        context = this;

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        // Print User's info
        if (mUser == null) {
            toastMessage("User is null!");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        patEmail = mUser.getEmail();
        patId = patEmail.substring(0, patEmail.indexOf("@"));
        toastMessage("UserId: " + mUser.getUid() + "; email: " + mUser.getEmail() + "; id: " + patId);

        myFirestoreRef = FirebaseFirestore.getInstance();
        myColRef = myFirestoreRef.collection(PATIENTS_COLLECTION);

        tvPatient = (TextView) findViewById(R.id.tvPatient);
        tvPatient.setText(patId);

        lvItem = (ListView) findViewById(R.id.lvHistory);
        listPatient = new ArrayList<String>();
        listKey = new ArrayList<String>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, listPatient);
        lvItem.setAdapter(adapter);
        lvItem.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        lvItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Listen to multiple documents in a collection
        myColRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d(TAG, "Listen failed.", e);
                    return;
                }
                //Log.d(TAG, "Listening Data.");
                adapter.clear();
                listPatient.clear();
                listKey.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Patient patient = document.toObject(Patient.class);
                    listPatient.add(patient.name + " - age: " + patient.age + " - id: " + patient.id);
                    listKey.add(document.getId());
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        signOut();
        if (item.getItemId()==android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        Log.d(TAG, "signedOut::firebase");
        mAuth.signOut();
    }

    /**
     * Toast a message
     * @param message
     */
    private void toastMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
