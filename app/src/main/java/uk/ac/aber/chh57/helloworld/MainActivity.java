package uk.ac.aber.chh57.helloworld;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.location.Location;
import android.view.View.OnClickListener;
import android.location.LocationListener;
import android.location.LocationManager;
import java.util.ArrayList;
import java.util.List;



public class MainActivity extends Activity {

    protected LocationManager locationManager;
    protected Button GPSBtn;
    private static final long update_distance = 5; //the distance change before an update(in meters)
    private static final long update_time = 1000 ; //time between updates (in milliseconds)
    public static String userData = "sharedData";
    SharedPreferences runtimeData;

    Button saveBtn;
    EditText txtName, txtPhone, txtEmail;
    List<Contact> Contacts = new ArrayList<Contact>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        runtimeData = getSharedPreferences(userData, 0);
        checkPrefs();

        // The class doesn't check through tabs to find a value; it checks for ID matches.
        txtName = (EditText) findViewById(R.id.nametxt);
        txtPhone = (EditText) findViewById(R.id.phonetxt);
        txtEmail = (EditText) findViewById(R.id.emailtxt);
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);

        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("usrSettings");
        tabSpec.setContent(R.id.Settings);
        tabSpec.setIndicator("Settings");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("addRecord");
        tabSpec.setContent(R.id.Record);
        tabSpec.setIndicator("Add Record");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("listofRecords");
        tabSpec.setContent(R.id.Records);
        tabSpec.setIndicator("List of Records");
        tabHost.addTab(tabSpec);

        GPSBtn = (Button) findViewById(R.id.btnSave);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                update_time,
                update_distance,
                new GPSListener()
        );

        GPSBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showCurrentLocation();
            }
        });

        // The following is for actions based on text changing within the WHOLE app- this main class doesn't see tabs; it only views the ID's.
        txtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
               saveBtn.setEnabled(!txtName.getText().toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        saveBtn = (Button) findViewById(R.id.btnSaveSettings);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveContact(txtName.getText().toString(), txtPhone.getText().toString(), txtEmail.getText().toString());
                String userName = txtName.getText().toString();
                String phoneNumber = txtPhone.getText().toString();
                String email = txtEmail.getText().toString();
                SharedPreferences.Editor editor = runtimeData.edit();
                editor.putString("username", userName);
                editor.putString("phoneNumber", phoneNumber);
                editor.putString("email", email);
                editor.commit();
                Toast.makeText(getApplicationContext(), "Details for " + txtName.getText().toString() + " have been saved.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void showCurrentLocation(){

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {
            String message = String.format(
                    "Saved Location \n Longitude: %1$s \n Latitude: %2$s",
                    location.getLongitude(), location.getLatitude()
            );
            Toast.makeText(MainActivity.this, message,
                    Toast.LENGTH_LONG).show();
        }
    }

    protected void checkPrefs(){
        runtimeData = getSharedPreferences(userData, 0);
        if (runtimeData.getString("username", "NotFound") == "NotFound"){

        }
        else {
            txtName.setText(runtimeData.getString("username", "Not Found"));
            txtPhone.setText(runtimeData.getString("phoneNumber", "Not Found"));
            txtEmail.setText(runtimeData.getString("email", "Not Found"));
        }


    }

    private class GPSListener implements LocationListener {

        public void onLocationChanged(Location location) {
            String message = String.format(
                    "GPS Location saved",
                    location.getLongitude(), location.getLatitude()
            );
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
        }

        public void onStatusChanged(String s, int i, Bundle b) {
            Toast.makeText(MainActivity.this, "Provider status changed",
                    Toast.LENGTH_LONG).show();
        }

        public void onProviderDisabled(String s) {
            Toast.makeText(MainActivity.this,
                    "Provider disabled by the user. GPS turned off",
                    Toast.LENGTH_LONG).show();
        }

        public void onProviderEnabled(String s) {
            Toast.makeText(MainActivity.this,
                    "Provider enabled by the user. GPS turned on",
                    Toast.LENGTH_LONG).show();
        }

    }

    private void saveContact(String name, String email, String phone){
        Contacts.add(new Contact(name, email, phone));
    }


        private class ContactListAdapter extends ArrayAdapter<Contact> {
        public ContactListAdapter(){
            super (MainActivity.this, R.layout.listview_item, Contacts);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent){
            if (view == null){
                view = getLayoutInflater().inflate(R.layout.listview_item, parent, false);
            }
            Contact thisContact = Contacts.get(position);

            TextView name = (TextView) view.findViewById(R.id.contactName);
            name.setText(thisContact.getName());
            TextView phone = (TextView) view.findViewById(R.id.Phone);
            phone.setText(thisContact.getPhone());
            TextView email = (TextView) view.findViewById(R.id.Email);
            email.setText(thisContact.getEmail());
            return view;
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
