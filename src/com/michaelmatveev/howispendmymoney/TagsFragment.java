package com.michaelmatveev.howispendmymoney;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class TagsFragment extends ListFragment
implements PeriodFilter {
	
	private SimpleCursorAdapter adapter;
	private SQLiteHelper dbHelper;
	private String filter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		filter = "";
		super.onCreate(savedInstanceState);
     
		dbHelper = new SQLiteHelper(this.getActivity());
	    String[] from = new String[] { Tag.KEY_ID, Tag.COLUMN_NAME };
	    int[] to = new int[] { 0, android.R.id.text1 };
	    
	    Cursor cur = dbHelper.getAllTagsCursor(filter);
	    adapter = new SimpleCursorAdapter(this.getActivity(), R.layout.simple_list_item_1, cur, from, to, 0);
	    	    
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
	      menu.add(1, 2, 2, "Delete");
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
	    } else {
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
				addNewTag();
				return true;
			default: 
				return super.onOptionsItemSelected(item);	
		}		
	}


	private void addNewTag() {
		  final EditText input = new EditText(this.getActivity());
		  input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
		 
		  new AlertDialog.Builder(this.getActivity())
		  .setTitle("New tag")
		  .setView(input)
		  .setPositiveButton("OK", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String newTagTitle = input.getText().toString();
				if(dbHelper.isTagExist(newTagTitle)) {
					Toast.makeText(getActivity(), "Tag \"" +  newTagTitle + "\" already exists", Toast.LENGTH_SHORT)
					.show();
				}
				else {
					dbHelper.addTag(new Tag(newTagTitle));
					Toast.makeText(getActivity(), "New tag \"" +  newTagTitle + "\" has been created", Toast.LENGTH_SHORT)
					.show();
					refresh();
				}
				
			}
		  })
		  .setNegativeButton("Cancel", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		  })
		  .show();
		
		
	}


	@Override
	  public boolean onContextItemSelected(MenuItem item) {
	      AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
	      Cursor c = adapter.getCursor();
	      c.moveToPosition(info.position);
	      int id = c.getInt(0);
	      switch (item.getItemId()) {
	          case 1:
	              updateTagTitleById(id, c.getString(1));
	              return true;
	          case 2:
	        	  deleteTagById(id);
	              return true;
	          default:
	              return super.onContextItemSelected(item);
	      }
	  }
	  
	  private void deleteTagById(int id) {
          dbHelper.deleteTag(id);
          refresh();
 	  }
	  
	  private void refresh() {
	         Cursor newCursor = dbHelper.getAllTagsCursor(filter);
	          if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD) {
	        	    adapter.swapCursor(newCursor);
	        	} else {
	        	    adapter.changeCursor(newCursor);
	        	}
	          adapter.notifyDataSetChanged();  
	  }
	  
	  private void updateTagTitleById(final int id, final String initText) {
		  final EditText input = new EditText(this.getActivity());
		  input.setText(initText);
		  new AlertDialog.Builder(this.getActivity())
		  .setTitle("Change title")
		  .setView(input)
		  .setPositiveButton("OK", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {

				String newTagTitle = input.getText().toString();
				if(dbHelper.isTagExist(newTagTitle)) {
					Toast.makeText(getActivity(), "Unable to rename \"" +  initText + "\" to \"" + newTagTitle + "\" since proposed tag already exists", Toast.LENGTH_SHORT)
					.show();
				}
				else {
					dbHelper.updateTagText(id, newTagTitle);
					Toast.makeText(getActivity(), "Tag \"" +  initText + "\" has been renamed to \"" + newTagTitle + "\"", Toast.LENGTH_SHORT)
					.show();
					refresh();
				}	
			}
		  })
		  .setNegativeButton("Cancel", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		  })
		  .show();
	  }


	@Override
	public void setPeriod(int periodId) {
		// Do nothing
		
	}
}


