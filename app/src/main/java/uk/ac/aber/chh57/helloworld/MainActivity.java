package uk.ac.aber.chh57.helloworld;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.location.Location;
import android.view.View.OnClickListener;
import android.location.LocationListener;
import android.location.LocationManager;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.Intent;

public class MainActivity extends Activity  {

    File plantFile,sceneFile;
    protected LocationManager locationManager;
    protected Button plantPhotoBtn,scenePhotoBtn, SaveRecordBtn;
    private static final long update_distance = 5; //the distance change before an update(in meters)
    private static final long update_time = 1000 ; //time between updates (in milliseconds)
    private static int saveClicked = 0;
    public static String userData = "sharedData";
    SharedPreferences userSettingsData, recordData;
    Button saveBtn, updateReserveBtn;
    EditText txtName, txtPhone, txtEmail, speciesTxt, commentsTxt;
    Spinner abundanceTxt, siteSpinner;
    TextView siteDetailsLbl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // *********************** pre-tab creation *******************************************************
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userSettingsData = getSharedPreferences(userData, 0);
        recordData = getSharedPreferences(userData, 0);
        // The below function call finds the user details from the shared preferences, if they exist
        checkPrefs();
        // The below assigns the fields in the gradle to the program code
        saveClicked = 0;
        saveBtn = (Button) findViewById(R.id.btnSaveSettings);
        updateReserveBtn = (Button) findViewById(R.id.btnUpdateReserves);
        txtName = (EditText) findViewById(R.id.nametxt);
        txtPhone = (EditText) findViewById(R.id.phonetxt);
        txtEmail = (EditText) findViewById(R.id.emailtxt);
        speciesTxt = (EditText) findViewById(R.id.txtSpecies);
        commentsTxt = (EditText) findViewById(R.id.txtComments);
        abundanceTxt = (Spinner) findViewById(R.id.txtAbundance);
        siteSpinner = (Spinner) findViewById(R.id.txtSite);
        siteDetailsLbl = (TextView) findViewById(R.id.lblSiteDetails);
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

        // ************************** Populate abundance drop down list *****************************************
        Spinner dropdown = (Spinner)findViewById(R.id.txtAbundance);
        String[] abundance = new String[]{"Dominant", "Abundant", "Frequent", "Occasional", "Rare"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, abundance);
        dropdown.setAdapter(adapter);
        // **********************************************************************************************************

        // ************************ Update Reserves button ************************************************************
        updateReserveBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getReserves();
            }
        });
        // *************************************************************************************************************

        // *************************** Update reserves spinner ******************************************************************
        Spinner dropdownReserves = (Spinner)findViewById(R.id.txtSite);
        String[] reserves = new String[]{"Aberbargoed Grasslands", "Allt Rhyd y Groes", "Allt y Benglog", "Anglesey Fens", "Bardsey Island"};
        ArrayAdapter<String> adapterReserves = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, reserves);
        dropdownReserves.setAdapter(adapterReserves);
        // *************************************************************************************************************

        // *************************** Save Record method ***************************************************************
        saveRecord();
        // *************************************************************************************************

        // **************************** Validation *********************************************************
        validation();
        // ***********************************************************************************************


        // ***************************** Saving user settings *********************************************
        saveUserSettings();
        // ******************************************************************************************************

        // ******************* Photo storage button control **************************************************
        storePhoto();
        // ******************************************************************************************************
    }

    protected void storePhoto(){
        plantPhotoBtn = (Button) findViewById(R.id.btnPlant);
        scenePhotoBtn = (Button) findViewById(R.id.btnScene);

        plantPhotoBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //Get the name from the setPhotoFile() function
                try {
                    plantFile = setPhotoFile(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(plantFile));
                startActivityForResult(intent, 0);

            }
        });
        scenePhotoBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //Get the name from the setPhotoFile() function
                try {
                    sceneFile = setPhotoFile(1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT,  Uri.fromFile(sceneFile));
                startActivityForResult(intent, 0);

            }
        });

    }


    protected void getReserves(){
        Toast.makeText(this, "Could not retrieve reserves.", Toast.LENGTH_LONG).show();
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

    protected void saveUserSettings(){
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = txtName.getText().toString();
                String phoneNumber = txtPhone.getText().toString();
                String email = txtEmail.getText().toString();
                SharedPreferences.Editor editor = userSettingsData.edit();
                editor.putString("username", userName);
                editor.putString("phoneNumber", phoneNumber);
                editor.putString("email", email);
                editor.commit();
                Toast.makeText(getApplicationContext(), "Data stored for " + userSettingsData.getString("username", "Not Found") + " successfully.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void validation(){
        txtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (validateUser() == true){
                    saveBtn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (validateUser() == true){
                    saveBtn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        txtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (validateUser() == true){
                    saveBtn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    protected void saveRecord(){
        Intent intent=new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        //sendBroadcast(intent);
        SaveRecordBtn = (Button) findViewById(R.id.btnSave);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                update_time,
                update_distance,
                new GPSListener()
        );
        speciesTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                SaveRecordBtn.setEnabled(!((!speciesTxt.getText().toString().trim().isEmpty()) && (!commentsTxt.getText().toString().trim().isEmpty()) && (!abundanceTxt.toString().trim().isEmpty())));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        SaveRecordBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                saveClicked = saveClicked + 1;
                // GPS
                showCurrentLocation();
                // -------------------
                // Shared prefs
                String species = speciesTxt.getText().toString();
                String comments = commentsTxt.getText().toString();
                String abundance = abundanceTxt.toString();
                SharedPreferences.Editor editor = recordData.edit();
                editor.putString("species", species);
                editor.putString("comments", comments);
                editor.putString("abundance", abundance);
                editor.commit();
                // ------------------
                // OO
                try {

                    Record i = new Record();
                    i.setPlantName(species);
                    i.setComments(comments);
                    i.setAbundance(abundance);
                    i.setReserveViewed(siteSpinner.toString());
                    i.setPlantPhotoPath(plantFile.getPath().toString());
                    i.setScenePhotoPath(sceneFile.getPath().toString());
                }
                catch (Exception e){
                    System.out.println("No plant/scene found.");
                }
                // ----------------------
                Toast.makeText(getApplicationContext(), "Data stored for " + userSettingsData.getString("species", "Not Found") + " successfully.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private File setPhotoFile(int x) throws IOException {
        String type = null;
        if (x==0){
            //if it's plant
            type = "Species";
        }else if (x==1){
            //if it's scene
            type = "Scene";
        }
        //Make a time stamp and add the type e.g. scene or species
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = type + timeStamp;
        //Find the default storage directory
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName, ".jpg",storageDir);

        return image;
    }

    protected boolean validateUser(){
        if (!((txtName.getText().toString().isEmpty()) && ((txtPhone.getText().toString().isEmpty())) && ((txtEmail.getText().toString().isEmpty())))){
            return true;
        }
        else return false;
    }


    protected void showCurrentLocation(){
        LocationManager gpsService = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = gpsService
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
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
        userSettingsData = getSharedPreferences(userData, Context.MODE_PRIVATE);
        String existingUsername = userSettingsData.getString("username", null);
        String existingPhone = userSettingsData.getString("phoneNumber", null);
        String existingEmail = userSettingsData.getString("email", null);
        if (existingUsername != null)  {
            txtName.setText(existingUsername);
            txtPhone.setText(existingPhone);
            txtEmail.setText(existingEmail);
        }
    }



    private class GPSListener implements LocationListener {

        public void onLocationChanged(Location location) {
//            String message = String.format(
//                    "GPS Location saved",
//                    location.getLongitude(), location.getLatitude()
//            );
//            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
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
