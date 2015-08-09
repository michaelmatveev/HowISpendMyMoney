package com.michaelmatveev.howispendmymoney;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity {

	private Preference exportDb;
	private Preference importDb;

	@SuppressLint("NewApi") 
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		exportDb = getPreferenceScreen().findPreference("Export");
		importDb = getPreferenceScreen().findPreference("Import");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
			getActionBar().setDisplayShowTitleEnabled(true);
			getActionBar().setTitle("Settings");
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		if(preference == exportDb) {
			exportDb();
			return true;
		}
		else if(preference == importDb) {
			importDb();
			return true;
		}
		
		// If the user has clicked on a preference screen, set up the action bar
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (preference instanceof PreferenceScreen) {
				initializeActionBar((PreferenceScreen) preference);
			}
		}
		
		return false;
	}
	
	@SuppressLint("NewApi")
	public static void initializeActionBar(PreferenceScreen preferenceScreen) {
	   final Dialog dialog = preferenceScreen.getDialog();

	   if (dialog != null) {
	   // Inialize the action bar
	   dialog.getActionBar().setDisplayHomeAsUpEnabled(true);

	   // Apply custom home button area click listener to close the PreferenceScreen because PreferenceScreens are dialogs which swallow
       // events instead of passing to the activity
       // Related Issue: https://code.google.com/p/android/issues/detail?id=4611
        View homeBtn = dialog.findViewById(android.R.id.home);

        if (homeBtn != null) {
        	View.OnClickListener dismissDialogClickListener = new View.OnClickListener() {
        		@Override
        		public void onClick(View v) {
        			dialog.dismiss();
        		}
           };

		            // Prepare yourselves for some hacky programming
		            ViewParent homeBtnContainer = homeBtn.getParent();

		            // The home button is an ImageView inside a FrameLayout
		            if (homeBtnContainer instanceof FrameLayout) {
		                ViewGroup containerParent = (ViewGroup) homeBtnContainer.getParent();

		                if (containerParent instanceof LinearLayout) {
		                    // This view also contains the title text, set the whole view as clickable
		                    ((LinearLayout) containerParent).setOnClickListener(dismissDialogClickListener);
		                } else {
		                    // Just set it on the home button
		                    ((FrameLayout) homeBtnContainer).setOnClickListener(dismissDialogClickListener);
		                }
		            } else {
		                // The 'If all else fails' default case
		                homeBtn.setOnClickListener(dismissDialogClickListener);
		       }
		    }    
		}
	}
	   
	private void importDb() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
                if (sd.canWrite()) {
                    String currentDBPath = "//data//" + "com.michaelmatveev.howispendmymoney"
                            + "//databases//" + SQLiteHelper.DATABASE_NAME;
                //String backupDBPath = "<backup db filename>"; // From SD directory.
                //String currentDBPath = SQLiteHelper.DATABASE_NAME;
                String backupDBPath = SQLiteHelper.DATABASE_NAME;

                File backupDB = new File(data, currentDBPath);
                File currentDB = new File(sd, backupDBPath);

            FileChannel src = new FileInputStream(currentDB).getChannel();
            FileChannel dst = new FileOutputStream(backupDB).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
            Toast.makeText(getApplicationContext(), "Import Successful!",
                    Toast.LENGTH_SHORT).show();

        }
    } catch (Exception e) {

        Toast.makeText(getApplicationContext(), "Import Failed!", Toast.LENGTH_SHORT)
                .show();

    }
}

private void exportDb() {
    try {
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();

        if (sd.canWrite()) {
            String currentDBPath = "//data//" + "com.michaelmatveev.howispendmymoney"
                    + "//databases//" + SQLiteHelper.DATABASE_NAME;
//            String backupDBPath = "<destination>";
            //String currentDBPath = SQLiteHelper.DATABASE_NAME;
            String backupDBPath = SQLiteHelper.DATABASE_NAME;

        	File currentDB = new File(data, currentDBPath);
            File backupDB = new File(sd, backupDBPath);

            FileChannel src = new FileInputStream(currentDB).getChannel();
            FileChannel dst = new FileOutputStream(backupDB).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
            Toast.makeText(getApplicationContext(), "Backup Successful!",
                    Toast.LENGTH_SHORT).show();

        }
    } catch (Exception e) {

        Toast.makeText(getApplicationContext(), "Backup Failed!", Toast.LENGTH_SHORT)
                .show();

    }
}
}
