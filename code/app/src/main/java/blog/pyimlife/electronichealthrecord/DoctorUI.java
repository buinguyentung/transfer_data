package blog.pyimlife.electronichealthrecord;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class DoctorUI extends AppCompatActivity {

    private static final String TAG = "DoctorUI";

    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    // Access a Cloud Firestore instance from Activity
    FirebaseFirestore myFirestoreRef;
    CollectionReference myColRef;

    private final String PATIENTS_COLLECTION = "patients-collection";
    private String doctorEmail = "";
    private String doctorId = "";

    TextView tvDoctorId;
    ListView lvPatientList;
    ArrayList<Patient> listPatient;
    ArrayList<String> listKey;
    DoctorPatientAdapter adapterPatient;

    Button btnAddPatient;
    private final String patIdBegin = "patid0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_ui);

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
        doctorEmail = mUser.getEmail();
        doctorId = doctorEmail.substring(0, doctorEmail.indexOf("@"));
        toastMessage("DoctorId: " + mUser.getUid() + "; email: " + doctorEmail + "; id: " + doctorId);

        // Init firestore reference
        myFirestoreRef = FirebaseFirestore.getInstance();
        myColRef = myFirestoreRef.collection(PATIENTS_COLLECTION);

        tvDoctorId = (TextView) findViewById(R.id.tvDoctorId);
        tvDoctorId.setText(doctorId);

        // Init the patient data source
        listPatient = new ArrayList<>();
        listKey = new ArrayList<String>();
        adapterPatient = new DoctorPatientAdapter(this, listPatient);
        // Attach the adapter to a ListView
        lvPatientList = (ListView) findViewById(R.id.lvPatientList);
        lvPatientList.setAdapter(adapterPatient);
        //lvPatientList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        lvPatientList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // To the patient's page
                
            }
        });

        // Add new patient
        btnAddPatient = (Button) findViewById(R.id.btnAddPatient);
        btnAddPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewPatientDialog();
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
                //Log.d(TAG, "Listening Data in DoctorUI.");
                adapterPatient.clear();
                //listPatient.clear();
                listKey.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Patient patient = document.toObject(Patient.class);
                    adapterPatient.add(patient);
                    //Log.d(TAG, "====== patient name: " + patient.name + " size: " + listPatient.size());
                    listKey.add(document.getId());
                }
                adapterPatient.notifyDataSetChanged();
                toastMessage("List size: " + adapterPatient.getCount());
            }
        });
    }

    public void showNewPatientDialog() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        View view = layoutInflaterAndroid.inflate(R.layout.new_patient_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(context);
        alertDialogBuilderUserInput.setView(view);

        final EditText edtNameDialog = view.findViewById(R.id.edtNameDialog);
        final EditText edtIdDialog= view.findViewById(R.id.edtIdDialog);
        final EditText edtAgeDialog = view.findViewById(R.id.edtAgeDialog);

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(edtNameDialog.getText().toString()) ||
                        TextUtils.isEmpty(edtIdDialog.getText().toString()) ||
                        TextUtils.isEmpty(edtAgeDialog.getText().toString())) {
                    Toast.makeText(context, "Enter Patient's info!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                Patient p = new Patient();
                p.setName(edtNameDialog.getText().toString());
                p.setId(edtIdDialog.getText().toString());
                p.setAge(Integer.parseInt(edtAgeDialog.getText().toString()));
                p.setPatid(patIdBegin + String.valueOf(adapterPatient.getCount()+1));

                createPatient(p);
            }
        });
    }

    public void createPatient(Patient p) {
        // DocumentId
        String documentId = p.getPatid();
        myColRef.document(documentId).set(p);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        signOut();
        if (item.getItemId()==android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Signout Firebase authentication
     */
    private void signOut() {
        Log.d(TAG, "signedOut::fromDoctorUI");
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
