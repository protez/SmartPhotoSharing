package com.hmi.smartphotosharing;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.hmi.smartphotosharing.json.Photo;
import com.hmi.smartphotosharing.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * Base Adapter subclass creates Gallery view
 * - provides method for adding new images from user selection
 * - provides method to return bitmaps from array
 *
 */
public class MyGalleryAdapter extends BaseAdapter {


	private Context context;
	private ImageLoader imageLoader;
	private List<Photo> data;
	
	//use the default gallery background image
    int defaultItemBackground;
    
    //placeholder bitmap for empty spaces in gallery
    Bitmap placeholder;

    //constructor
    public MyGalleryAdapter(Context c, List<Photo> list, ImageLoader im) {
        context = c;
        this.data = list;
        this.imageLoader = im;
        
        // Add a dummy item if there is only 1 photo in the gallery
        // This is a fix to make the gallery start from the left
        if (data != null && data.size() == 1) {
        	Photo p = new Photo();
        	p.iid = "0";
        	p.thumb = Util.DUMMY_PHOTO;
        	p.dummy = true;
        	data.add(p);
        }
    }

    @Override
    public int getCount() {
    	if (data == null) {
    		return 0;
    	} else {
    		return data.size();
    	}
    }

    @Override
    public Photo getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    //get view specifies layout and display options for each thumbnail in the gallery
    public View getView(int position, View convertView, ViewGroup parent) {

        Photo photo = data.get(position);
    	
        View v = convertView;
        ViewHolder holder;
        
        if(v == null) {
	    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        v = inflater.inflate(R.layout.simple_image, null);
	        //specify the bitmap at this position in the array

            holder = new ViewHolder();
            holder.imgIcon = (ImageView)v.findViewById(R.id.image1);
            v.setTag(holder);
        } else {
            // view already exists, get the holder instance from the view
            holder = (ViewHolder) v.getTag();
        }

        //set layout options
        holder.imgIcon.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));

        imageLoader.displayImage(photo.thumb, holder.imgIcon);
        
        holder.imgIcon.setPadding(2,2,2,2);
        if (getItem(position).isNew)
        	holder.imgIcon.setBackgroundColor(0xFFFF0000);
        else
        	holder.imgIcon.setBackgroundColor(NO_SELECTION);
         
        // Hide the dummy item from the gallery
        if (photo.dummy) {
        	holder.imgIcon.setVisibility(ImageView.INVISIBLE);
        } else {
        	holder.imgIcon.setVisibility(ImageView.VISIBLE);
        }
        
        return v;
    	
        /*ImageView imageView = new ImageView(context);
        //specify the bitmap at this position in the array
        
        Photo photo = data.get(position);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setLayoutParams(new Gallery.LayoutParams(45, 45));
        imageLoader.displayImage(photo.thumb, imageView);
        
        imageView.setPadding(2,2,2,2);
        
        if (getItem(position).isNew)
        	imageView.setBackgroundColor(0xFFFF0000);
                
        return imageView;*/
    }
    
    static class ViewHolder {
        ImageView imgIcon;
    }


}