package uk.ac.aber.chh57.helloworld;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;
import android.content.Context;
import android.location.Location;
import android.view.View.OnClickListener;
import android.location.LocationListener;
import android.location.LocationManager;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.Intent;


public class MainActivity extends Activity  {

    File plantFile,sceneFile;
    protected LocationManager locationManager;
    protected Button plantPhotoBtn,scenePhotoBtn, SaveRecordBtn;
    private static final long update_distance = 5; //the distance change before an update(in meters)
    private static final long update_time = 1000 ; //time between updates (in milliseconds)
    public static String userData = "sharedData";
    SharedPreferences runtimeData;
    Button saveBtn;
    EditText txtName, txtPhone, txtEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // *********************** pre-tab creation *******************************************************
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        runtimeData = getSharedPreferences(userData, 0);
        // The below function call finds the user details from the shared preferences, if they exist
        checkPrefs();

        // The below assigns the fields in the gradle to the program code
        txtName = (EditText) findViewById(R.id.nametxt);
        txtPhone = (EditText) findViewById(R.id.phonetxt);
        txtEmail = (EditText) findViewById(R.id.emailtxt);

        // and assigns the tab host to control which tabs exist
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        // it then initiates the tabs
        tabHost.setup();
        // ************************************************************************************************

        // *************************** the below creates the three tabs *****************************************
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
        // *******************************************************************************************************


        // *************************** Save Record METHOD ***************************************************************
        Intent intent=new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        sendBroadcast(intent);
        SaveRecordBtn = (Button) findViewById(R.id.btnSave);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                update_time,
                update_distance,
                new GPSListener()
        );

        SaveRecordBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showCurrentLocation();
            }
        });
        SharedPreferences.Editor editor = runtimeData.edit();

        // *************************************************************************************************


        // **************************************************************************************************
        // The following is for actions based on text changing within the WHOLE app- this main class doesn't see tabs; it only views the ID's.
        txtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
               saveBtn.setEnabled((!txtName.getText().toString().trim().isEmpty()) && (!txtPhone.getText().toString().trim().isEmpty()) && (!txtEmail.getText().toString().trim().isEmpty()));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        // **************************************************************************************************

        // ***************************** Saving user settings *********************************************
        saveBtn = (Button) findViewById(R.id.btnSaveSettings);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = txtName.getText().toString();
                String phoneNumber = txtPhone.getText().toString();
                String email = txtEmail.getText().toString();


                editor.putString("username", userName);
                editor.putString("phoneNumber", phoneNumber);
                editor.putString("email", email);
                editor.commit();
                Toast.makeText(getApplicationContext(), "Data stored for " + runtimeData.getString("username", "Not Found") + " successfully.", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), "Details for " + txtName.getText().toString() + " have been saved.", Toast.LENGTH_SHORT).show();
            }
        });
        // ******************************************************************************************************

        // ******************* Saving Record ****************************************************************


        // ******************* Photo storage button control **************************************************
        plantPhotoBtn = (Button) findViewById(R.id.btnPlant);
        scenePhotoBtn = (Button) findViewById(R.id.btnScene);

        plantPhotoBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //Get the name from the setPhotoFile() function
                plantFile = setPhotoFile(0);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, plantFile);
                startActivityForResult(intent, 0);

            }
        });
        scenePhotoBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //Get the name from the setPhotoFile() function
                sceneFile = setPhotoFile(1);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, sceneFile);
                startActivityForResult(intent, 0);

            }
        });
        // ******************************************************************************************************
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
            // Image captured and saved successfully
                Toast.makeText(this, "Image saved to:\n" +
                        data.getData(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
            // User cancelled the photo
                Toast.makeText(this, "Image not saved", Toast.LENGTH_LONG).show();
            } else {
            // Photo failed, inform user
                Toast.makeText(this, "Image failed to saved", Toast.LENGTH_LONG).show();
            }
        }
    }

    private File setPhotoFile(int x){
        String type = null;
        if (x==0){
        //if it's plant
            type = "Species";
        }else if (x==1){
        //if it's scene
            type = "Scene";
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File( "IMG_" + type + timeStamp + ".jpg");//directory.getPath() + File.separator +
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
        else {
            Toast.makeText(MainActivity.this, "Could not find GPS location",
                    Toast.LENGTH_LONG).show();
        }
    }

    protected void checkPrefs(){
        txtName = (EditText) findViewById(R.id.nametxt);
        txtPhone = (EditText) findViewById(R.id.phonetxt);
        txtEmail = (EditText) findViewById(R.id.emailtxt);
        runtimeData = getSharedPreferences(userData, Context.MODE_PRIVATE);
        String existingUsername = runtimeData.getString("username", null);
        String existingPhone = runtimeData.getString("phoneNumber", null);
        String existingEmail = runtimeData.getString("email", null);
        if (existingUsername != null)  {
            txtName.setText(existingUsername);
            txtPhone.setText(existingPhone);
            txtEmail.setText(existingEmail);
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
