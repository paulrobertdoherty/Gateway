package com.beetl.gateway;

import gateway.com.beetl.R;

import java.io.*;
import java.util.*;
import com.beetl.gateway.filters.helpers.Coordinate;
import com.beetl.gateway.filters.helpers.Filter;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.AdapterView.*;

public class ImageEditor extends Activity implements OnClickListener, OnItemClickListener{
	/**
	 * The ImageView used to display the image on the screen.
	 */
	private ImageView imageView = null;
	
	/**
	 * The ListView used to display the filters.
	 */
	private ListView filters = null;
	
	/**
	 * A String array storing the names used for the filter ListView.
	 */
	private String[] filterNames = new String[]{
			"Inverse", "Flip Image Horizontally", "Neon", "Equalize Colors", "Bulge", "Blur"
	};
	
	/**
	 * A String array storing the class names of the filters.
	 */
	private String[] filterClassNames = new String[] {
		"SmartNegative", "MirrorHorizontal", "ExaggerateColors", "ColorCorrection", "Bulge", "Blur"
	};
	
	/**
	 * A variable representing whether or not the image has been filtered.
	 */
	private static boolean imageChanged = false;
	
	/**
	 * A variable used to store the current working bitmap.
	 */
	public static Bitmap currentBitmap = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Load the activity XML
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_editor);
		
		//Initialize the variable for the imageView variable, used to display the current image
		imageView = (ImageView) findViewById(R.id.image);
		
		//Set the on click listener to check if the image is pressed
		imageView.setOnClickListener(this);
				
		//Initialize the variable for the filters variable, used to list the filters for an image
		filters = (ListView) findViewById(R.id.filters);
		
		//Put all the filter names on to the filter list view
		filters.setAdapter(
				new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1,
				Arrays.asList(filterNames)));
		
		filters.setOnItemClickListener(this);
		//If we are first starting the activity
		if (!imageChanged) {
			//Set the current bitmap to the picture taken
			currentBitmap = Main.imageData;
			
			imageChanged = true;
		}
		
		//Set the image view to have the latest image data, but scaled
		imageView.setImageBitmap(scaleBitmap(currentBitmap, 1080));
		
		//Set the image height to half the screen's height
		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);
		imageView.requestLayout();
		imageView.getLayoutParams().height = size.y / 2;
	}

	/**
	 * Changes the newly filtered image to the other images
	 * @param image
	 * @param imageChanged
	 */
	private void filterImage(Bitmap image) {
		//Set the image view to have the latest image data, but scaled down
		imageView.setImageBitmap(scaleBitmap(image, 1080));
		
		//Set the current bitmap to the latest bitmap
		currentBitmap = image;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		try {
			//Make a Class<?> variable for the filter selected
			Class<?> currentClass = Class.forName("com.beetl.gateway.filters." + filterClassNames[position]);
			
			//Change the "currentClass" variable to a Filter variable
			com.beetl.gateway.filters.helpers.Filter currentFilter = (Filter) currentClass.newInstance();
			
			//Set the image data to the unfiltered bitmap
			Main.imageData = currentBitmap;
			
			//Creates a new task for filtering the image, then filters it
			new Load().execute(currentFilter);
		//If anything failed, explain to the user what happened
		} catch (ClassNotFoundException e) {
			Toast.makeText(this, "That's strange.  We couldn't find the \"" + filterNames[position] + "\" filter.", Toast.LENGTH_LONG).show();
		} catch (InstantiationException e) {
			Toast.makeText(this, "Huh.  The filter \"" + filterNames[position] + "\" doesn't seem to be an actual filter.", Toast.LENGTH_LONG).show();
		} catch (IllegalAccessException e) {
			Toast.makeText(this, "Hmm.  We couldn't access the filter \"" + filterNames[position] + "\"", Toast.LENGTH_LONG).show();
		}
	}
	
	/**
	 * A dialogue for a progress bar for the filter, while actually filtering the image as well.
	 * @author Paul Doherty
	 *
	 */
	public class Load extends AsyncTask<Filter, Integer, Bitmap>{
		private ProgressDialog dialog = null;
		
		@Override
		protected void onPreExecute() {
			//Create a progress bar
			dialog = new ProgressDialog(ImageEditor.this);
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.setMax(100);
			dialog.show();
		}
		
		@Override
		protected Bitmap doInBackground(Filter... filter) {
			//Create a variable to return
			Bitmap returnBitmap = currentBitmap.copy(currentBitmap.getConfig(), true);
			
			//If the method was called from the filter list
			if (filter[0] != null) {
				
				//Get the total amount of pixels that need filtering
				float totalPix = returnBitmap.getWidth() * returnBitmap.getHeight() * filter[0].getNumberOfPasses();
				
				//A number that represents the total incrementation of the progress bar, as long as it is below 1
				float addUp = 0;
				
				//Call the between passes method
				filter[0].betweenPasses(-1);
				
				for (int i = 0; i < filter[0].getNumberOfPasses(); i++) {
					//Do for each individual pixel
					for (int x = 0; x < returnBitmap.getWidth(); x++) {
						for (int y = 0; y < returnBitmap.getHeight(); y++) {
							//Get the other pixels needed for the filter
							int[] otherColors = getColorsFromCoordinates(currentBitmap, filter[0].getNeededCoordinates(x, y, returnBitmap.getWidth(), returnBitmap.getHeight()));
							
							//Set the current image bitmap to whatever the filter puts out
							returnBitmap.setPixel(x, y, filter[0].filterPixel(returnBitmap.getPixel(x, y), otherColors, i));
							
							//Add whatever small number to addUp
							addUp += 100.0f / totalPix;
							
							//If addUp is large enough
							if (addUp > 1) {
								//Update the progress bar
								publishProgress((int)(addUp));
								
								//Fix addUp
								addUp -= (int)addUp;
							}
						}
					}
					
					//Call the between passes method
					filter[0].betweenPasses(i);
				}
			}
			
			//Return the bitmap
			return returnBitmap;
		}
		
		@Override
		protected void onProgressUpdate(Integer... progress) {
			//Increment progress dialog
			dialog.incrementProgressBy(progress[0]);
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			//Do normal stuff
			super.onPostExecute(result);
			
			//Stop the dialog
			dialog.dismiss();
			
			//Changes the image to the new filtered image
			filterImage(result);
		}
	}
	
	/**
	 * Gets the colors needed for the filter from the given coordinates.
	 * @param image
	 * @param coordinates
	 * @return Needed colors
	 */
	private int[] getColorsFromCoordinates(Bitmap image, Coordinate[] coordinates) {
		//If coordinates were actually provided
		if (coordinates != null) {
			//Creates a set of values to return
			int[] returnValues = new int[coordinates.length];
			
			//For each coordinate
			for (int i = 0; i < coordinates.length; i++) {
				//Get the current coordinate
				Coordinate coordinate = coordinates[i];
				
				//Set the current return value to the color of the current pixel
				returnValues[i] = image.getPixel(coordinate.getX(), coordinate.getY());
			}
			
			//Then return the values
			return returnValues;
		}
		
		//If no coordinates were provided, don't provide any colors
		return null;
	}

	@Override
	public void onClick(View v) {
		//Create an intent to launch the image viewer
		Intent intent = new Intent(this, ImageViewer.class);
		
		//If the Android version is less than lollipop
		if (Build.VERSION.SDK_INT < 21) {
			//Launch the activity normally
			startActivity(intent);
		//Otherwise
		} else {
			//Make sure the image animates from one activity to another
			ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, imageView, getString(R.string.transition_image));
			
			//Finally start the activity
			startActivity(intent, options.toBundle());
		}
	}
	
	@Override
	public void onBackPressed() {
		//Show a dialog asking whether or not the user wants to save
		showSaveDialog();
	}
	
	/**
	 * Shows a dialog asking whether or not the image should be saved (or shared) or not.  If yes, it saves or shares the image.
	 */
	private void showSaveDialog() {
		//Create a dialogue for the user to choose whether or not to save
		new AlertDialog.Builder(this)
			.setCancelable(true)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle("Save Image?")
			.setMessage("Do you want to save the image before you leave?")
			.setPositiveButton("Save", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//Save the image
					try {
						saveImage(currentBitmap, false);
					} catch (IOException e1) {
						//Say something went wrong
						Toast.makeText(ImageEditor.this, "We couldn't save the image.", Toast.LENGTH_LONG).show();
					}
				}
			})
			//If the user decides not to save, just return to normal
			.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
			@Override
				public void onClick(DialogInterface dialog, int which) {
					imageChanged = false;
				
					//Close the activity
				    finish();
				}
			})
			.show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.editor, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		//For each individual item
		switch (item.getItemId()) {
			//For the undo item
			case R.id.undo:
				//Undo the image
				filterImage(Main.imageData);
				break;
		
			//For the save item
			case R.id.save:
				//Save the image
				try {
					saveImage(currentBitmap, false);
				} catch (IOException e1) {
					//Say something went wrong
					Toast.makeText(this, "We couldn't save the image.", Toast.LENGTH_LONG).show();
				}
				break;
					
			//For the share item
			case R.id.share:
				//Create an intent to launch the application for sharing
				Intent shareIntent = new Intent(Intent.ACTION_SEND);
					
				//Set the intent type as anything
				shareIntent.setType("image/png");
					
				//Finish sharing
				try {
					finishSharing(shareIntent);
				} catch (IOException e) {
					//If something went wrong, tell the user
					Toast.makeText(this, "We couldn't share the image.", Toast.LENGTH_LONG).show();
				}
				break;
		}
		return false;
	}
	
	/**
	 * Finish the sharing process.
	 * @param shareIntent
	 * @throws IOException
	 */
	private void finishSharing(Intent shareIntent) throws IOException {
		//Saves the image as well as puts the image into the intent
		shareIntent.putExtra(Intent.EXTRA_STREAM, saveImage(currentBitmap, true));
		
		//Start the activity used for sharing
		startActivity(Intent.createChooser(shareIntent, "Choose an app for sharing"));
	}
	
	/**
	 * Saves the current image.
	 * @param currentBitmap
	 * @param share
	 * @return Uri image
	 * @throws IOException
	 */
	private Uri saveImage(Bitmap currentBitmap, boolean share) throws IOException {
		//Set the name of the file as a practically random number
		String returnValueName = Long.toString(System.currentTimeMillis());
		
		//Create a file to return
		File image = new File(Environment.getExternalStorageDirectory(), returnValueName + ".png");
		
		//Create a FileOutputStream to create the image
		FileOutputStream fileOutputStream = new FileOutputStream(image);
		
		//Create a new ByteArrayOutputStream to convert the bitmap to a byte array
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		
		//Add the bitmap data to the stream
		currentBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		
		//Create the image by writing the bitmap bytes to the file output stream
		fileOutputStream.write(stream.toByteArray());
		
		//Close the file output stream
		fileOutputStream.close();
		
		//Show the image to the gallery and picture application
		MediaStore.Images.Media.insertImage(getContentResolver(), image.getPath(), returnValueName, "An image created in Gateway.");
		
		//If the method was not called from finishSharing
		if (!share)
			//Say the saving is done
			Toast.makeText(this, "Done saving!", Toast.LENGTH_LONG).show();
		
		//Then return the image as a Uri
		return Uri.fromFile(image);
	}

	/**
	 * Scales a bitmap down to where the biggest dimension is at least larger than the given max dimension.
	 * @param bitmap
	 * @param max
	 * @return Scaled bitmap
	 */
	public static Bitmap scaleBitmap(Bitmap bitmap, int max) {
		//The largest side of the bitmap
		float largest = 0;
		
		//If the width is larger than the height
		if (bitmap.getWidth() > bitmap.getHeight()) {
			//Set the largest side to the width
			largest = bitmap.getWidth();
		} else {
			//Otherwise set it as the height
			largest = bitmap.getHeight();
		}
		
		//The largest to max ratio
		float lToMR = max / largest;
		
		//If not scaling down, don't scale
		if (lToMR >= 1) {
			return bitmap;
		}
		
		//Otherwise, scale
		return Bitmap.createScaledBitmap(bitmap, (int)(bitmap.getWidth() * lToMR), (int)(bitmap.getHeight() * lToMR), false);
	}
}