package com.hmi.smartphotosharing.groups;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hmi.smartphotosharing.Login;
import com.hmi.smartphotosharing.NavBarListActivity;
import com.hmi.smartphotosharing.ProfileActivity;
import com.hmi.smartphotosharing.R;
import com.hmi.smartphotosharing.json.FetchJSON;
import com.hmi.smartphotosharing.json.Group;
import com.hmi.smartphotosharing.json.GroupListResponse;
import com.hmi.smartphotosharing.json.OnDownloadListener;
import com.hmi.smartphotosharing.json.User;
import com.hmi.smartphotosharing.json.UserResponse;
import com.hmi.smartphotosharing.util.Sorter;
import com.hmi.smartphotosharing.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class GroupsActivity extends NavBarListActivity implements OnDownloadListener {
	
    public static final int CREATE_GROUP = 4;

    private static final int CODE_PROFILE = 1;
    private static final int CODE_GROUPS = 2;
        
	private ImageLoader imageLoader;
	    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.groups);
        super.onCreate(savedInstanceState);
                        
        imageLoader = ImageLoader.getInstance();

        DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showStubImage(R.drawable.ic_launcher)
            .showImageForEmptyUri(R.drawable.ic_launcher)
            .cacheInMemory()
            .cacheOnDisc()
            	.build();

        // Set the config for the ImageLoader
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
        .threadPoolSize(5)
        .threadPriority(Thread.MIN_PRIORITY+2)
        .defaultDisplayImageOptions(options)
        .build();

        // Init ImageLoader
        imageLoader.init(config);
        
        // Show selection in nav bar
        ImageView home = (ImageView) findViewById(R.id.home);
        home.setImageResource(R.drawable.ic_menu_home_selected);
        
        // Load data
        loadData(true, true);
        
        
    }

    public void onClickAdd(View view) {
    	Intent intent = new Intent(this, GroupCreateActivity.class);
    	startActivity(intent);
    }
    
    public void onClickJoin(View view) {
    	Intent intent = new Intent(this, GroupJoinActivity.class);
    	startActivity(intent);
    }
    
    public void onClickProfile(View view) {
    	Intent intent = new Intent(this, ProfileActivity.class);
    	startActivity(intent);
    }
    	
    @Override
    public void onResume() {
    	super.onResume();
      
    	// Refresh groups list
    	loadData(true, true);
    }  
    
	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.group_menu, menu);
	    return true;
	}	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {		
        switch (item.getItemId()) {

	        case R.id.refresh:
	        	loadData(false,true);
		    	return true;
	        default:
	        	return super.onOptionsItemSelected(item);
        }
    }	
	
	private void loadData(boolean profile, boolean groups) {
		
		SharedPreferences settings = getSharedPreferences(Login.SESSION_PREFS, MODE_PRIVATE);
		String hash = settings.getString(Login.SESSION_HASH, null);

		if (profile) {
	        String profileUrl = String.format(Util.getUrl(this,R.string.profile_http),hash);		
	        new FetchJSON(this,CODE_PROFILE).execute(profileUrl);
		}

		if (groups) {
			String groupsUrl = String.format(Util.getUrl(this,R.string.groups_http),hash);
			new FetchJSON(this,CODE_GROUPS).execute(groupsUrl);
		}

	}
		
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_GROUP && resultCode == Activity.RESULT_OK) {
            loadData(false,true);
        }
    }	

	/**
	 * Checks whether there is a network connection available
	 * @return true if the device is connected to a network
	 */
	public boolean hasNetwork() {
		// Gets the URL from the UI's text field.
        ConnectivityManager connMgr = (ConnectivityManager) 
            getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        
        return networkInfo != null && networkInfo.isConnected();
	}
		
	/**
	 * This method converts the GroupList object to an array of Group objects and sets the list adapter.
	 * @param result
	 */
	@Override
	public void parseJson(String result, int code) {
		
		switch (code) {
		case CODE_GROUPS:
			parseGroups(result);
			break;
			
		case CODE_PROFILE:
			parseProfile(result);
			break;
			
		default:
		}
	}

	private void parseProfile(String result) {
		Gson gson = new Gson();
		UserResponse response = gson.fromJson(result, UserResponse.class);
		User user = response.getObject();
		if (user != null) {
			// Set the user name
			TextView name = (TextView) findViewById(R.id.groups_name);
			name.setText(user.getName());
			
			// Set the user name
			TextView stats = (TextView) findViewById(R.id.stats);
			
			stats.setText(String.format(this.getResources().getString(R.string.profile_stats), user.groups, user.photos));
			
			// Set the user icon
			ImageView pic = (ImageView) findViewById(R.id.groups_icon);
			//dm.fetchDrawableOnThread(userPic, pic);
			imageLoader.displayImage(user.thumb, pic);
		}
	}

	private void parseGroups(String result) {
		
		Gson gson = new Gson();
		GroupListResponse response = gson.fromJson(result, GroupListResponse.class);
		
		if (response != null) {
			List <Group> group_list = response.getObject();
			
			if (group_list == null) {
				ListView listView = getListView();
				TextView emptyView = (TextView) listView.getEmptyView();
				emptyView.setGravity(Gravity.CENTER_HORIZONTAL);
				emptyView.setText(getResources().getString(R.string.groups_empty));
			} else {

				// Sort the group on newest
				GroupAdapter adapter = new GroupAdapter(
						this, 
						R.layout.group_item, 
						group_list,
						imageLoader
					);
				adapter.sort(Sorter.GROUP_SORTER_UPDATES);
				setListAdapter(adapter);	
			}
		}
	}
 
}