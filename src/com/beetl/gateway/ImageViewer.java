package com.beetl.gateway;

import gateway.com.beetl.R;
import android.app.*;
import android.graphics.Bitmap;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

public class ImageViewer extends Activity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Go fullscreen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		//If android is ready
		if (Build.VERSION.SDK_INT >= 14)
			//Remove the navigation bar for the view
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		
		//Load the activity XML
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_viewer);
		
		//Create a variable for the image shown
		ImageView image = (ImageView) findViewById(R.id.imageView);
		
		//Set the image view to have the latest image data, but scaled
		image.setImageBitmap(ImageEditor.scaleBitmap(ImageEditor.currentBitmap, 1920));
		
		//Set the image press detection
		image.setOnClickListener(this);
	}
	
	/**
	 * Stops the current activity with animations if applicable.
	 */
	private void stopThis() {
		//If the Android version is lollipop
		if (Build.VERSION.SDK_INT >= 21) {
			//Close the activity with animations
			finishAfterTransition();
		//Otherwise
		} else {
			//Just close the activity
			finish();
		}
	}

	@Override
	public void onClick(View v) {
		stopThis();
	}
}