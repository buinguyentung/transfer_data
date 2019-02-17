package blog.pyimlife.electronichealthrecord;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DoctorPatientAdapter extends ArrayAdapter<Patient> {

    Context context;
    ArrayList<Patient> patientList;

    // @param earthquakes is the list of earthquakes, which is the data source of the adapter
    public DoctorPatientAdapter(Context context, ArrayList<Patient> patientList) {
        super(context, 0, patientList);
        this.context = context;
        this.patientList = patientList;
    }

    // Refresh the patient list
    public void refreshPatientList(ArrayList<Patient> patientList) {
        this.patientList.clear();
        this.patientList.addAll(patientList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return patientList.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_patient_item, parent, false);
        }
        Patient currentPatient = getItem(position);
        // Name, Age, Id, PatId
        TextView tvName = (TextView) listItemView.findViewById(R.id.tvName);
        TextView tvAge  = (TextView) listItemView.findViewById(R.id.tvAge);
        TextView tvId   = (TextView) listItemView.findViewById(R.id.tvId);
        TextView tvPatid = (TextView) listItemView.findViewById(R.id.tvPatid);

        tvName.setText(currentPatient.getName());
        tvAge.setText(String.valueOf(currentPatient.getAge()));
        tvId.setText(currentPatient.getId());
        tvPatid.setText(currentPatient.getPatid());

        return listItemView;
    }
}
