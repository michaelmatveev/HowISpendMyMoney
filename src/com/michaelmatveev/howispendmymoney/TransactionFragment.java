package com.michaelmatveev.howispendmymoney;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.SearchView;
import android.text.format.DateFormat;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TransactionFragment extends ListFragment
implements PeriodFilter {
	
	private SimpleCursorAdapter adapter;
	private SQLiteHelper dbHelper;
	private String filter;
	private Date period;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		filter = "";
		super.onCreate(savedInstanceState);
     
		dbHelper = new SQLiteHelper(this.getActivity());
		
	    String[] from = new String[] { "_id", "AmountAndTitle", "TagsList", "Date" };

	    int[] to = new int[] { 0,R.id.transaction_amount, R.id.transaction_tags, R.id.transaction_date };
	    Cursor cur = dbHelper.getAllTransactionsCursor(filter, period);
	    adapter = new SimpleCursorAdapter(this.getActivity(), R.layout.transaction_list_item, cur, from, to, 0);
	    // преобразовать дату в строку
	    adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
			
			@Override
			public boolean setViewValue(View view, Cursor cursor, int column) {
				
				if(column == 3) {
					TextView tv = (TextView)view;
					String dateStr = cursor.getString(cursor.getColumnIndex("Date"));
					Timestamp time = Timestamp.valueOf(dateStr);
					tv.setText(TimeIntervals.monthDayFormatter.format(time));
			        return true;  
				}
				return false;
			}
		});
	    	    
	    setListAdapter(adapter);
	 }
	
	 
	 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.setHasOptionsMenu(true);
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    ListView lv = getListView();
	    registerForContextMenu(lv);
	    lv.setOnItemClickListener(new ListView.OnItemClickListener() {
	    	@Override
	    	public void onItemClick(AdapterView<?> adapterView, View view, int index, long arg) {
	    	  Cursor c = adapter.getCursor();
	  	      c.moveToPosition(index);	  	      
	        	Intent intent = makeIntent(c.getInt(0), c.getString(1), c.getFloat(2), Timestamp.valueOf(c.getString(3)),
	            		c.getString(5), c.getString(6));
	        	updateTransactionById(intent);
	    	}
		});
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		Cursor c = adapter.getCursor();
		c.moveToPosition(info.position);
		menu.setHeaderTitle("Options for " + c.getString(1));
		menu.add(1, 1, 1, "Edit");
		menu.add(1, 2, 1, "Split");
		menu.add(1, 3, 2, "Delete");
	}
	
	
	@SuppressLint("NewApi") 
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    // Inflate the menu items for use in the action bar
		
	    inflater.inflate(R.menu.tags, menu);
  
	    MenuItem searchItem = menu.findItem(R.id.action_search_tag);
	    SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
	    searchView.setQueryHint(getResources().getText(R.string.enter_tag));
	    
	    int currentapiVersion = android.os.Build.VERSION.SDK_INT;
	    
	    if (currentapiVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
	    	searchItem.setOnActionExpandListener(new OnActionExpandListener() {

	            @Override
	            public boolean onMenuItemActionCollapse(MenuItem item) {
					filter = "";
					refresh();
	                return true; // Return true to collapse action view
	            }

	            @Override
	            public boolean onMenuItemActionExpand(MenuItem item)
	            {
	                return true;
	            }
	        });
	    } 
	    else {
	        // do something for phones running an SDK before froyo
	        searchView.setOnCloseListener(new SearchView.OnCloseListener()
	        {

	            @Override
	            public boolean onClose()
	            {
					filter = "";
					refresh();
					return false;
	            }
	        });
	    }
	    
	    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener( ) {
	    	@Override
	    	public boolean onQueryTextChange(String newText) {
	    		return true;
	    	}

	    	@Override
	    	public boolean onQueryTextSubmit(String query) {
	    		filter = query;
	    		refresh();
	    		return true;
	    	}
	    	
	    });
	    
		super.onCreateOptionsMenu(menu, inflater);
		
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.action_add_new_tag:
				addNewTransaction();
				return true;
			default: 
				return super.onOptionsItemSelected(item);
		}		
	}
	
/*	private void createTagsDialog(int[] selectedTags, final View v) {
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
		new AlertDialog.Builder(this.getActivity())
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
*/	
	private void addNewTransaction() {
		Intent intent = new Intent(getActivity(), TransactionEditActivity.class);
		intent.putExtra(TransactionEditActivity.OPEN_MODE, TransactionEditActivity.Mode.ADD);
		intent.putExtra(TransactionEditActivity.TITLE, getResources().getString(R.string.action_new_transaction));
		startActivityForResult(intent, 2);
	}


	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
	    Cursor c = adapter.getCursor();
	    c.moveToPosition(info.position);
	    int id = c.getInt(0);
	    Intent intent = null;
	    switch (item.getItemId()) {
	        case 1: // update
	        	intent = makeIntent(id, c.getString(1), c.getFloat(2), Timestamp.valueOf(c.getString(3)),
	            		c.getString(5), c.getString(6));
	        	updateTransactionById(intent);
	            return true;
	        case 2: // split
	        	intent = makeIntent(id, c.getString(1), c.getFloat(2), Timestamp.valueOf(c.getString(3)),
	            		c.getString(5), c.getString(6));
	        	splitTransaction(intent);
		        return true;
	        case 3: // delete
	        	deleteTransactionById(id);
	            return true;
	        default:
	            return super.onContextItemSelected(item);
	      }
	  }
	  
	  
	private void refresh() {
	    Cursor newCursor = dbHelper.getAllTransactionsCursor(filter, period);
	    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD) {
	  	  adapter.swapCursor(newCursor);
	    } else {
	  	   adapter.changeCursor(newCursor);
	    }
	    adapter.notifyDataSetChanged();  
	}
	
	private Intent makeIntent(final int id, final String initText, final float initAmount, Timestamp initDate, String tagNames, String tagIds) {
		Intent intent = new Intent(getActivity(), TransactionEditActivity.class);
		
		intent.putExtra(TransactionEditActivity.ID, id);		
		intent.putExtra(TransactionEditActivity.DESCRIPTION, initText);
		intent.putExtra(TransactionEditActivity.AMOUNT, initAmount);
		intent.putExtra(TransactionEditActivity.TAGS_TEXT, tagNames);
		intent.putExtra(TransactionEditActivity.TAGS_IDS, tagIds);
		Calendar calendar = Calendar.getInstance();  
	    calendar.setTime(initDate);
	    intent.putExtra(TransactionEditActivity.YEAR, calendar.get(Calendar.YEAR));
	    intent.putExtra(TransactionEditActivity.MONTH, calendar.get(Calendar.MONTH));
	    intent.putExtra(TransactionEditActivity.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));	    
		return intent;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Activity.RESULT_OK) {
			refresh();
		}
	}
	
	private void deleteTransactionById(int id) {
        dbHelper.deleteTransaction(id);
        refresh();
 	}
	
	public void splitTransaction(Intent intent) {
		intent.putExtra(TransactionEditActivity.OPEN_MODE, TransactionEditActivity.Mode.SPLIT);
		intent.putExtra(TransactionEditActivity.TITLE, getResources().getString(R.string.action_split_transaction));
		startActivityForResult(intent, 1); // refresh list after receiving result		
	}
	
	private void updateTransactionById(Intent intent) {	  
		intent.putExtra(TransactionEditActivity.OPEN_MODE, TransactionEditActivity.Mode.UPDATE);
		intent.putExtra(TransactionEditActivity.TITLE, getResources().getString(R.string.action_edit_transaction));
		startActivityForResult(intent, 1);	// refresh list after receiving result	

		//private String tagListAsText;


		  
		  /*
		  LayoutInflater factory = LayoutInflater.from(this.getActivity());
		  final View transactionDialogView = factory.inflate(
		            R.layout.transaction_dialog, null);
		  
		  //final EditText description = (EditText)transactionDialogView.findViewById(R.id.editTextDescription);
		  //description.setText(initText);
		  
		  final EditText amount = (EditText)transactionDialogView.findViewById(R.id.editTextAmount);
		  amount.setText(String.valueOf(initAmount));
		  
		  final Button tagsButton = (Button)transactionDialogView.findViewById(R.id.buttonTags);
		  tagsButton.setText(tagNames);
		  tagsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
					// show dialog for tags selection
					createTagsDialog(tagsToUpdate, v);
				}
			});
			
		  final DatePicker picker = (DatePicker)transactionDialogView.findViewById(R.id.datePicker);
		  final Calendar calendar = Calendar.getInstance();  
	      calendar.setTime(initDate);
		  picker.init(calendar.get(Calendar.YEAR),
				  calendar.get(Calendar.MONTH),
				  calendar.get(Calendar.DAY_OF_MONTH), null);
				  
		  final AlertDialog dialog = new AlertDialog.Builder(this.getActivity())
		  .setTitle("Edit transaction")		  
		  .setView(transactionDialogView)
		  .setPositiveButton(R.string.ok, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Workaround to check input values and prevent dialog closing
				// http://stackoverflow.com/questions/2620444/how-to-prevent-a-dialog-from-closing-when-a-button-is-clicked
			}
		  })
		  .setNegativeButton(R.string.cancel, null)
		  .create();
		  
		  dialog.show();

		  dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				float newAmount;
				try {
					newAmount = Float.parseFloat(amount.getText().toString());
				}
				catch(NumberFormatException nfe) {					
					Toast.makeText(getActivity(), "Enter a valid amount", Toast.LENGTH_SHORT)
					.show();
					return;
				}
				String newDescription = "";//description.getText().toString();				
				calendar.set(picker.getYear(), picker.getMonth(), picker.getDayOfMonth());
				Timestamp newDate = new Timestamp(calendar.getTimeInMillis());
				dbHelper.updateTransaction(id, newDescription, newAmount, newDate, tagsToUpdate);
				dialog.dismiss();
				Toast.makeText(getActivity(), "Transcation updated", Toast.LENGTH_SHORT)
					.show();
				refresh();
			}
		});
*/		  
	}
	  
	public void setPeriod(int periodId) {
		Date start = TimeIntervals.getPeriod(periodId).first;
		if(period != start) {
			period = start;
			refresh();
		}
		
	}
}


