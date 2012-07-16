package com.hmi.smartphotosharing;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.gson.Gson;
import com.hmi.json.FetchJSON;
import com.hmi.json.OnDownloadListener;
import com.hmi.json.Photo;
import com.hmi.json.PopularResponse;

public class PhotoDetailActivity extends SherlockFragmentActivity implements OnDownloadListener {

	private ImageView imgView;
	
	private long id;
	private DrawableManager dm;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_detail);

        imgView = (ImageView) findViewById(R.id.picture);
        
		new FetchJSON(this).execute(getResources().getString(R.string.photo_detail_url));

        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case R.id.share:

        		String uri = getResources().getString(R.string.photo_detail_url);
        		
        		Intent intent = new Intent(this,SharePhotoActivity.class);
				intent.setType("image/jpeg");

				// Add the Uri of the current photo as extra value
				intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(uri));
				
				// Create and start the chooser
				startActivity(intent);
				return true;
	        default:
	        	return super.onOptionsItemSelected(item);
        }
    }

	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
    	MenuInflater inflater = getSupportMenuInflater();
	    inflater.inflate(R.menu.photo_menu, menu);
	    return true;
	}

	@Override
	public void parseJson(String json, int code) {
		Gson gson = new Gson();
		PopularResponse pr = gson.fromJson(json, PopularResponse.class);
		
		// TODO single photo in json response
		Photo p = pr.msg.get(0);
		
		String uri = p.location + p.name;
		        
        dm = new DrawableManager(this);
        dm.fetchDrawableOnThread(uri, imgView);
        
		// Update the 'Taken by' text
        TextView by = (TextView)findViewById(R.id.photo_detail_name);
        String byTxt = getResources().getString(R.string.photo_detail_name);
        by.setText(String.format(byTxt, p.rname));

        // Update the timestamp
        TextView date = (TextView)findViewById(R.id.photo_detail_date);
        // Convert Unix timestamp to Date
        Date time = new Date(Long.parseLong(p.time)*1000);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String datum = sdf.format(time);
        date.setText(datum);
        
        // Update the group text
        TextView group = (TextView)findViewById(R.id.photo_detail_group);
        String groupTxt = getResources().getString(R.string.photo_detail_group);
        group.setText(String.format(groupTxt, p.gid));
        
        
	}	


}