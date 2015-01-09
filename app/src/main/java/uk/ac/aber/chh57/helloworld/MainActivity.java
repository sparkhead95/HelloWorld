package uk.ac.aber.chh57.helloworld;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;


public class MainActivity extends Activity {

    Button saveBtn;
    EditText txtName, txtPhone, txtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // The class doesn't check through tabs to find a value; it checks for ID matches.
        txtName = (EditText) findViewById(R.id.nametxt);
        txtPhone = (EditText) findViewById(R.id.phonetxt);
        txtEmail = (EditText) findViewById(R.id.emailtxt);
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);

        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("usrSettings");
        tabSpec.setContent(R.id.tabSettings);
        tabSpec.setIndicator("Settings");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("addRecord");
        tabSpec.setContent(R.id.tabAddRecord);
        tabSpec.setIndicator("Add Record");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("listofRecords");
        tabSpec.setContent(R.id.tabListofRecords);
        tabSpec.setIndicator("List of Records");
        tabHost.addTab(tabSpec);

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

        saveBtn = (Button) findViewById(R.id.btnShowMsg);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Details Saved.", Toast.LENGTH_SHORT).show();
                //Contact newContact = new Contact(txtName, txtEmail, txtPhone);
            }
        });



    }

    //private class ContactAdapter extends



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
