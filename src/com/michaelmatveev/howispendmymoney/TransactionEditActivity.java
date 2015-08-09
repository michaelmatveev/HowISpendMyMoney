package com.michaelmatveev.howispendmymoney;

import java.sql.Timestamp;
import java.util.Calendar;
//import java.util.Date;




import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class TransactionEditActivity extends ActionBarActivity {
	
	public class AddTransactionTask extends AsyncTask<Void, Long, String > {
		
		//private SQLiteHelper helper;
		private String newDescription;
		private float newAmount;
		private Timestamp newDate;
		//private int[] tagsToUpdate;
		
		public AddTransactionTask(String desc, float amount, Timestamp date) {
			newDescription = desc;
			newAmount = amount;
			newDate = date;
		}
		
		@Override
		protected String doInBackground(Void... args) {
			dbHelper.addTransaction(newDescription, newAmount, newDate, tagsToUpdate);
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			navigateUp("Transcation added");
		}
	}

	public void navigateUp(String message) {
		//NavUtils.navigateUpFromSameTask(this);
		Toast.makeText(this, message, Toast.LENGTH_SHORT)
			.show();
		setResult(RESULT_OK, null);
		finish();
	}
	
	public enum Mode {
		ADD,
		UPDATE,
		SPLIT
	}
	
	private Mode currentMode;
	public static final String TITLE = "Title";
	public static final String OPEN_MODE = "Mode";
	public static final String ID = "Id";
	public static final String DESCRIPTION = "Description";
	public static final String AMOUNT = "Amount";
	public static final String TAGS_TEXT = "TagsText";
	public static final String TAGS_IDS = "TagsId";	
	public static final String YEAR = "Year";
	public static final String MONTH = "Month";
	public static final String DAY_OF_MONTH = "DayOfMonth";
	
	private int id;
	private float amount;
	private String description;
	private int[] tagsToUpdate;
	private String tagListAsText;
	
	private SQLiteHelper dbHelper;
	static final Calendar calendar = Calendar.getInstance();
	//static private Date period;
	EditText descriptionView;
	EditText amountView;
	Button tagsView;
	Button dateView;

	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_editor);
        Intent intent = getIntent(); 
        setTitle(intent.getStringExtra(TITLE));
        currentMode = (Mode)intent.getSerializableExtra(OPEN_MODE);
        id = intent.getIntExtra(ID, -1);
        
        dbHelper = new SQLiteHelper(this);
        
        tagListAsText = getIntent().getStringExtra(TAGS_TEXT);
        if(tagListAsText == null) {
        	tagListAsText = getResources().getString(R.string.tap_here_to_select_tags);
        }
        
        String tagIds = getIntent().getStringExtra(TAGS_IDS);		  
		if(tagIds == null || tagIds.equals("")) {
			tagsToUpdate = new int[] {};
		}
		else {
			String[] ids = tagIds.split(", ");
			tagsToUpdate = new int[ids.length];
			int i = 0;
			for(String tid : ids) {
				tagsToUpdate[i++] = Integer.valueOf(tid);
			}
		}
        
		amount = getIntent().getFloatExtra(AMOUNT, 0);
        description = getIntent().getStringExtra(DESCRIPTION);
        
        if(currentMode == Mode.UPDATE || currentMode == Mode.SPLIT) {
        	calendar.set(Calendar.YEAR, getIntent().getIntExtra(YEAR, -1));
        	calendar.set(Calendar.MONTH, getIntent().getIntExtra(MONTH, -1));
        	calendar.set(Calendar.DAY_OF_MONTH, getIntent().getIntExtra(DAY_OF_MONTH, -1));
        }
        
		descriptionView = (EditText)findViewById(R.id.editTextDescription);
		descriptionView.setText(description);
		
		amountView = (EditText)findViewById(R.id.editTextAmount);
		if(currentMode == Mode.UPDATE) {
			amountView.setText(String.valueOf(amount));
		}
		else if (currentMode == Mode.SPLIT){
			String f = getResources().getString(R.string.action_new_transaction_amount_hint);
			amountView.setHint(String.format(f, amount));
		}
		else if (currentMode == Mode.ADD) {
			amountView.setHint(getResources().getString(R.string.action_new_transaction_amount_hint));
		}
		
		tagsView = (Button)findViewById(R.id.buttonTags);
		tagsView.setText(tagListAsText);
		dateView = (Button)findViewById(R.id.buttonDate);  

		//period = calendar.getTime();
		//dateView.setText(SQLiteHelper.datesFormatter.format(period));
		dateView.setText(SQLiteHelper.datesFormatter.format(calendar.getTime()));
		
		tagsView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				createTagsDialog(tagsToUpdate, v);			
			}
		});
		
		dateView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showTimePickerDialog(v);
			}
		});
	}
	
    public void showTimePickerDialog(View v) {
    	DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.dateView = dateView;
        newFragment.show(getSupportFragmentManager(), getResources().getString(R.string.date_of_transaction));
    }
    
    public static class DatePickerFragment extends DialogFragment
    	implements DatePickerDialog.OnDateSetListener {
    	public Button dateView;  	
    	
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            //final Calendar c = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, monthOfYear);
			calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			//period = calendar.getTime();
			//dateView.setText(SQLiteHelper.datesFormatter.format(period));
			dateView.setText(SQLiteHelper.datesFormatter.format(calendar.getTime()));
		}
    }
      
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.transaction_editor, menu);
	    return super.onCreateOptionsMenu(menu); 
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_cancel:
			//NavUtils.navigateUpFromSameTask(this);
			finish(); // just return back, no need to apply any changes on the parent activity
			return true;
		case R.id.action_save:
			if(currentMode == Mode.ADD) {
				addNewTransaction();
			}
			else if(currentMode == Mode.UPDATE) {
				updateTransaction();
			}
			else if(currentMode == Mode.SPLIT) {
				splitTransaction();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}		
	}	

	private void createTagsDialog(int[] selectedTags, final View v) {
		Cursor c = dbHelper.getAllTagsCursor("");
		final String[] items = new String[c.getCount()];
		final int[] tagIds = new int[c.getCount()];
		final boolean [] checkedItems = new boolean[c.getCount()];
		int i = 0;		
		c.moveToFirst();
		while(!c.isAfterLast()) {
			items[i] = c.getString(1); // Name
			int tagId = c.getInt(0); // _id
			tagIds[i] = tagId;
			for(int t: selectedTags) {
				if(t == tagId) {
					checkedItems[i] = true;
				}
			}
			i++;
			c.moveToNext();
		}
		new AlertDialog.Builder(this)
			.setTitle("Transaction tags")
			.setCancelable(false)
			.setMultiChoiceItems(items, checkedItems, new OnMultiChoiceClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					checkedItems[which] = isChecked;
				}
			})
			.setPositiveButton("OK", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					tagsToUpdate = new int[checkedItems.length];
					int j = 0;
					int k = 0;
					tagListAsText = "";
					for(boolean c: checkedItems) {
						if(c) {
							tagsToUpdate[k++] = tagIds[j];
							tagListAsText += items[j] + ", "; 
						}
						j++;
					}
					if(tagListAsText != "") {
						tagListAsText = tagListAsText.substring(0, tagListAsText.length() - 2);
					}
					((Button)v).setText(tagListAsText);
				}
			})
			.setNegativeButton("Cancel", null)
			.show();
	}

	private boolean addNewTransaction() {
		String newDescription = descriptionView.getText().toString();
		float newAmount;
		try {
			newAmount = Float.parseFloat(amountView.getText().toString());
		}
		catch(NumberFormatException nfe) {					
			Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT)
			.show();
			return false;
		}
		
		Timestamp newDate = new Timestamp(calendar.getTimeInMillis());
		
		//Timestamp newDate = new Timestamp(period.getTime());
		//dbHelper.addTransaction(newDescription, newAmount, newDate, tagsToUpdate);
		new AddTransactionTask(newDescription, newAmount, newDate)
			.execute();
		//Toast.makeText(this, "Transcation added", Toast.LENGTH_SHORT)
		//	.show();
		return true;
	}

	private void updateTransaction() {
		String newDescription = descriptionView.getText().toString();
		float newAmount;
		try {
			newAmount = Float.parseFloat(amountView.getText().toString());
		}
		catch(NumberFormatException nfe) {					
			Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT)
			.show();
			return;
		}
		Timestamp newDate = new Timestamp(calendar.getTimeInMillis());

		dbHelper.updateTransaction(id, newDescription, newAmount, newDate, tagsToUpdate);
		Toast.makeText(this, "Transcation updated", Toast.LENGTH_SHORT)
			.show();

		setResult(RESULT_OK);
		finish();
	}
	
	private void splitTransaction() {
		String newDescription = descriptionView.getText().toString();
		float newAmount;
		try {
			newAmount = Float.parseFloat(amountView.getText().toString());
			if(newAmount >= amount) {
				Toast.makeText(this, "You should inter a les amount", Toast.LENGTH_SHORT)
				.show();				
				return;
			}
		}
		catch(NumberFormatException nfe) {					
			Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT)
			.show();
			return;
		}

		Timestamp newDate = new Timestamp(calendar.getTimeInMillis());

		dbHelper.updateChangeAmount(id, amount - newAmount);
		dbHelper.addTransaction(newDescription, newAmount, newDate, tagsToUpdate);
		Toast.makeText(this, "Transcation splitter", Toast.LENGTH_SHORT)
			.show();

		setResult(RESULT_OK);
		finish();		

	}

	
}
