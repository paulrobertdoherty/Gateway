package com.beetl.gateway;

import gateway.com.beetl.R;

import java.io.*;

import android.net.*;
import android.os.*;
import android.provider.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.util.Log;
import android.view.*;
import android.view.View.*;
import android.widget.*;

public class Main extends Activity implements OnClickListener {
	private static final int fromCameraPicture = 0, fromGalleryPicture = 1;
	public static Bitmap imageData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Load the activity xml
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Create variables for the buttons
		Button takePicture = (Button) findViewById(R.id.take_picture);
		Button editPicture = (Button) findViewById(R.id.edit_picture);
		
		//Set the variables click listener to this class.
		takePicture.setOnClickListener(this);
		editPicture.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	
	/**
	 * Opens the Android camera application and returns a picture.
	 */
	private void callCameraApp() {
		//If the device does not have a camera
		if (!this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			//Say so to the user
			Toast.makeText(this, "The device doesn't have a camera.", Toast.LENGTH_LONG).show();
			
			//Stop the method, since it would just fail
			return;
		}
		
		//Create an intent to access the camera app
		Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		
		//Create a new temporary file to store the image
		File image;
		
		try {
			//Set the image file to a temporary file
			image = createTemporaryFile("temp", ".png");
			
			//Make sure the image takes up no space
			image.delete();
		//If something goes wrong
		} catch (IOException e) {
			//Log the error
			Log.e("Gateway", e.toString());
			
			//Explain what happened
			Toast.makeText(this, "Something is wrong with the SD card, so we couldn't take a picture.", Toast.LENGTH_LONG).show();
			
			//Stop the method, since the camera app can't start now
			return;
		}
		
		//Set the image uri as the temp image directory
		iUri = Uri.fromFile(image);
		
		//Set the image directory as the place to put the image
		intent.putExtra(MediaStore.EXTRA_OUTPUT, iUri);
		
		//Then access the camera app
		startActivityForResult(intent, fromCameraPicture);
	}
	
	/**
	 * The temp image directory.
	 */
	private Uri iUri = null;
	
	/**
	 * Returns a temporary file from a prefix and a suffix.
	 * @param prefix
	 * @param suffix
	 * @return Temporary file
	 * @throws IOException
	 */
	private File createTemporaryFile(String prefix, String suffix) throws IOException {
		//Create a file to access the SD card
		File tempDirectory= Environment.getExternalStorageDirectory();
		
		//Add a temp folder to the storage directory
	    tempDirectory=new File(tempDirectory.getAbsolutePath() + "/.temp/");
	    
	    //If the directory does not exist
	    if (!tempDirectory.exists()) {
	    	//Make it
	        tempDirectory.mkdir();
	    }
	    
	    //Return a temporary file in the directory
	    return File.createTempFile(prefix, suffix, tempDirectory);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//Do normal stuff
		super.onActivityResult(requestCode, resultCode, data);
		
		//If the activity returns a photo
		if (resultCode == RESULT_OK) {
			//Set the image uri if from the gallery
			if (requestCode == fromGalleryPicture) {
				iUri = data.getData();
			}
			
			//Notify the content resolver that the image Uri will be accessed
			this.getContentResolver().notifyChange(iUri, null);
			
			//Create a content resolver variable
		    ContentResolver contentResolver = this.getContentResolver();
		    
		    //And a temporary bitmap
		    Bitmap tempBit;
		    
		    try {
		    	//Set the temporary bitmap 
		    	tempBit = MediaStore.Images.Media.getBitmap(contentResolver, iUri);
		        
		        //Set the image data to a mutable, and smaller, version of the bitmap image
		        imageData = tempBit.copy(tempBit.getConfig(), true);
		        
		        //If the image was taken with the camera
		        if (requestCode == fromCameraPicture) {
		        	//Delete the image
			        new File(iUri.getPath()).delete();
		        }
		    //If something goes wrong
		    } catch (IOException e) {
		    	//Say what went wrong
		    	Log.e("Gateway", e.toString());
		    	
		    	//Explain what happened
		        Toast.makeText(this, "The app was unable to access the image.", Toast.LENGTH_LONG).show();
		        
		        //Stop the method.  The app can't continue any more.
		        return;
		    }
			
			//Create a new intent to start the image editor
			Intent intent = new Intent(this, ImageEditor.class);
			
			//And then start it
			startActivity(intent);
		//Say to press the check box, since they probably didn't mean to close
		} else if (resultCode == RESULT_CANCELED) {
			//But only if the image was taken from a camera
			if (requestCode == fromCameraPicture) {
				Toast.makeText(this, "You probably didn't mean to close that.", Toast.LENGTH_LONG).show();
				Toast.makeText(this, "Next time, press the check mark in the top "
						+ "right corner of the screen.", Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onClick(View v) {
		//Do something different for each button
		switch(v.getId()) {
			//If the "Take Picture" button is pressed
			case R.id.take_picture:
				//Call the camera app to receive pictures
				callCameraApp();
				break;
				
			//If the "Edit Picture" button is pressed
			case R.id.edit_picture:
				//Call the gallery app to receive pictures
				callGalleryApp();
				break;
		}
	}

	/**
	 * Calls the gallery app to load an image.
	 */
	private void callGalleryApp() {
		//Create an intent to access the gallery
		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		
		//Then actually start the intent
		startActivityForResult(intent, fromGalleryPicture);
	}
}