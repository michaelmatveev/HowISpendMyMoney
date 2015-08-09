package com.michaelmatveev.howispendmymoney;

import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SpinnerAdapter;

public class MainActivity extends ActionBarActivity {
	
	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle drawerToggle;
	private ListView drawerList;
	private String[] navItems;
	private ActionBar actionBar;
	private SpinnerAdapter spinnerAdapter;
	private Fragment newFragment;
	private int selectedFragmentId = 2;
	private int selectedPeriod;
	private TotalFragment totalFragment;
	
	private static final int FRAGMENT_TRANSACTION = 0;
	private static final int FRAGMENT_TAGS = 1;
	private static final int FRAGMENT_CLOUD = 2;

	private class NavigationListener implements OnNavigationListener {
		
		@Override
		public boolean onNavigationItemSelected(int position, long id) {
			selectedPeriod = position;
			for(PeriodFilter pf : new PeriodFilter[] { (PeriodFilter)newFragment , (PeriodFilter)totalFragment} ) {
				if(pf == null) {			
					Log.e("onNavigationItemSelected", "pf should not be null");
				} 
				else {
					pf.setPeriod(position);
				}
			}
			return true;
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK) {
			for(PeriodFilter pf : new PeriodFilter[] { (PeriodFilter)newFragment , (PeriodFilter)totalFragment} ) {
				if(pf == null) {			
					Log.e("onNavigationItemSelected", "pf should not be null");
				} 
				else {
					pf.setPeriod(selectedPeriod);
				}
			}			
		}
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {

		@Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
			totalFragment = (TotalFragment)fragmentManager.findFragmentById(R.id.bottom);
			
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			selectedFragmentId = position;
			switch (position) {
			case FRAGMENT_TRANSACTION:
				actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
				newFragment = new TransactionFragment();
				fragmentManager.beginTransaction()
		          //.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
		          .show(totalFragment)
		          .commit();
				break;
			case FRAGMENT_TAGS:
				actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
				newFragment = new TagsFragment();
				fragmentManager.beginTransaction()
		          //.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
		          .hide(totalFragment)
		          .commit();
				break;
			case FRAGMENT_CLOUD:
				actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
				newFragment = new CloudFragment();
				fragmentManager.beginTransaction()
		          //.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
		          .show(totalFragment)
		          .commit();
				
				break;
			default:
				startSettingsActivity();
				return;
			}

			transaction.replace(R.id.container, newFragment).commit();
			//transaction.addToBackStack(null);
			//transaction.commit();
			
			drawerList.setItemChecked(position, true);
			getSupportActionBar().setTitle(navItems[position]);
	        drawerLayout.closeDrawer(drawerList);
	    }
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	private void startSettingsActivity() {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);		
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = this.getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerList = (ListView)findViewById(R.id.left_drawer);
        
        navItems = getResources().getStringArray(R.array.navigation_items);
        drawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, navItems));
        
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
                ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                actionBar.setTitle(getTitle());
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                actionBar.setTitle(R.string.drawer_title);
            }
        };

        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(drawerToggle);
        
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        
        
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.view_periods, android.R.layout.simple_spinner_dropdown_item);
        actionBar.setListNavigationCallbacks(spinnerAdapter, new NavigationListener());
        
        
        
       /* 
        if(savedInstanceState != null) {
        	selectedFragmentId = savedInstanceState.getInt("SELECTED_FRAGMENT");
        	selectedPeriod = savedInstanceState.getInt("SELECTED_PERIOD");    	
        }
  
        // Open cloud by default
        drawerList.performItemClick(drawerList, selectedFragmentId, 0);
    	if(selectedFragmentId != 1) {
    		actionBar.setSelectedNavigationItem(selectedPeriod);
    	}*/

/*
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.bottom, new TotalFragment());
		
		transaction.commit();    		
  */      
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
     
    	drawerList.performItemClick(drawerList, selectedFragmentId, 0);
    	if(selectedFragmentId != 1) {
    		actionBar.setSelectedNavigationItem(selectedPeriod);
    	}

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
          return true;
        }
                
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putInt("SELECTED_FRAGMENT", selectedFragmentId);
    	outState.putInt("SELECTED_PERIOD", selectedPeriod);
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	selectedFragmentId = savedInstanceState.getInt("SELECTED_FRAGMENT");
    	selectedPeriod = savedInstanceState.getInt("SELECTED_PERIOD");    	
    }
    
}
