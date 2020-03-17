package ca.juliencomtois.imagematch;

import java.util.ArrayList;
import java.util.Arrays;
import android.app.AlertDialog;
import java.util.Collections;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Assignment Image Matcher The main activity class which will take care of the
 * game play.
 * 
 * @author Julien Comtois
 * @author Joey Campanelli
 * @author Frank Birikundavyi
 * 
 * @version 1.0
 *
 */
public class MainActivity extends Activity {

	private boolean hasClicked = false;
	private ImageButton currentBtn;
	ArrayList<Integer> imageIds;
	private SharedPreferences prefs;
	private int missesCtr;
	private int matchesCtr;
	private int totalMissesCtr;
	private int totalMatchesCtr;
	private int dupeImg;

	// Restores the previous state of the game or starts a new one.
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		prefs = getPreferences(MODE_PRIVATE);
		totalMissesCtr = prefs.getInt("totalmisses", 0);
		totalMatchesCtr = prefs.getInt("totalmatches", 0);
		if (savedInstanceState != null) {
			ArrayList<ImageButton> imageButtons = getImageButtons();
			ArrayList<Integer> currentImgs = savedInstanceState.getIntegerArrayList("currentimgs");
			boolean[] imgClickable = savedInstanceState.getBooleanArray("imgclickable");
			// Restore the state of all the image buttons
			for (int i = 0; i < 9; i++) {
				imageButtons.get(i).setImageResource(currentImgs.get(i));
				imageButtons.get(i).setTag(currentImgs.get(i));
				imageButtons.get(i).setClickable(imgClickable[i]);
			}
			missesCtr = savedInstanceState.getInt("missesCtr");
			matchesCtr = savedInstanceState.getInt("matchesCtr");
			totalMissesCtr = savedInstanceState.getInt("totalMissesCtr");
			totalMatchesCtr = savedInstanceState.getInt("totalMatchesCtr");
			hasClicked = savedInstanceState.getBoolean("hasClicked");
			dupeImg = savedInstanceState.getInt("dupeImg");

			super.onRestoreInstanceState(savedInstanceState);
		} else {
			// Start a new game
			scramble(null);
		}
		displayCounts();
	}

	@Override
	public void onStop() {
		super.onStop();
		saveSharedPrefs();
	}

	// Save the counters of the game to storage.
	private void saveSharedPrefs() {
		prefs = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt("totalmisses", totalMissesCtr);
		editor.putInt("totalmatches", totalMatchesCtr);
		editor.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// Save the state of the game
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putIntegerArrayList("imageids", imageIds);
		savedInstanceState.putInt("missesCtr", missesCtr);
		savedInstanceState.putInt("matchesCtr", matchesCtr);
		savedInstanceState.putInt("totalMissesCtr", totalMissesCtr);
		savedInstanceState.putInt("totalMatchesCtr", totalMatchesCtr);
		savedInstanceState.putBoolean("hasClicked", hasClicked);
		savedInstanceState.putInt("dupeImg", dupeImg);

		ArrayList<ImageButton> imageButtons = getImageButtons();

		ArrayList<Integer> currentImgs = new ArrayList<Integer>();
		//Saves the id's of each image into the array
		for (int i = 0; i < 9; i++) {
			currentImgs.add((Integer) imageButtons.get(i).getTag());
		}
		savedInstanceState.putIntegerArrayList("currentimgs", currentImgs);

		boolean[] imgClickable = new boolean[9];
		//Saves whether each button is clickable or not
		for (int i = 0; i < 9; i++) {
			imgClickable[i] = imageButtons.get(i).isClickable();
		}
		savedInstanceState.putBooleanArray("imgclickable", imgClickable);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Launches the about activity for the description of the game.
	 * 
	 * @param view
	 *            current view
	 */
	public void about(View view) {
		Intent aboutActivity = new Intent(getApplicationContext(), AboutActivity.class);
		startActivity(aboutActivity);
	}

	/**
	 * Rotates the screen.
	 * 
	 * @param view
	 *            current view
	 */
	public void rotate(View view) {
		if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		else
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	/**
	 * Scrambles the game board.
	 * 
	 * @param view
	 *            current view
	 */
	public void scramble(View view) {
		ArrayList<ImageButton> imageButtons = getImageButtons();
		// Set all buttons to clickable
		for (int i = 0; i < 9; i++)
			imageButtons.get(i).setClickable(true);
		// Get id's of all the images
		imageIds = new ArrayList<Integer>(Arrays.asList(R.drawable.apple, R.drawable.banana, R.drawable.blueberry,
				R.drawable.grape, R.drawable.lemon, R.drawable.orange, R.drawable.pear, R.drawable.strawberry));
		// Randomly pick one of the 8 images to be the double
		int rand = (int) (Math.random() * 8);
		imageIds.add(imageIds.get(rand));
		dupeImg = imageIds.get(rand);
		// Shuffle the board so the tiles are in different places each time
		Collections.shuffle(imageIds);
		// Put all the images into image buttons to populate the board
		for (int i = 0; i < 9; i++) {
			imageButtons.get(i).setImageResource(imageIds.get(i));
			imageButtons.get(i).setTag(imageIds.get(i));
		}
		hasClicked = false;
	}

	/**
	 * Resets counters of the current lifecycle.
	 * 
	 * @param view
	 *            current view
	 */
	public void zero(View view) {
		missesCtr = 0;
		matchesCtr = 0;
		saveSharedPrefs();
		displayCounts();
	}

	private ArrayList<ImageButton> getImageButtons() {
		ArrayList<ImageButton> imageButtons = new ArrayList<ImageButton>();
		imageButtons.add((ImageButton) findViewById(R.id.imgbtn1));
		imageButtons.add((ImageButton) findViewById(R.id.imgbtn2));
		imageButtons.add((ImageButton) findViewById(R.id.imgbtn3));
		imageButtons.add((ImageButton) findViewById(R.id.imgbtn4));
		imageButtons.add((ImageButton) findViewById(R.id.imgbtn5));
		imageButtons.add((ImageButton) findViewById(R.id.imgbtn6));
		imageButtons.add((ImageButton) findViewById(R.id.imgbtn7));
		imageButtons.add((ImageButton) findViewById(R.id.imgbtn8));
		imageButtons.add((ImageButton) findViewById(R.id.imgbtn9));
		return imageButtons;
	}

	/**
	 * Checks to see if the pair clicked is a match.
	 * 
	 * @param view
	 *            current view
	 */
	public void checkMatch(View view) {
		currentBtn = (ImageButton) findViewById(view.getId());
		int currentImg = (Integer) currentBtn.getTag();
		if (currentImg == dupeImg) {
			currentBtn.setImageResource(R.drawable.placeholder);
			currentBtn.setTag(R.drawable.placeholder);
			if (hasClicked) {
				pairFound();
				matchesCtr++;
				totalMatchesCtr++;
			} else {
				hasClicked = true;
				Toast toast = Toast.makeText(this, R.string.dupefound, Toast.LENGTH_SHORT);
				toast.show();
			}
		} else {
			missesCtr++;
			totalMissesCtr++;
			Toast toast = Toast.makeText(this, R.string.losetoast, Toast.LENGTH_SHORT);
			toast.show();
		}
		currentBtn.setClickable(false);
		displayCounts();
	}

	private void pairFound() {
		// Show winning dialog to user
		Builder dialog = new AlertDialog.Builder(MainActivity.this);
		dialog.setTitle(R.string.windialogtitle);
		dialog.setMessage(R.string.windialog);
		dialog.setPositiveButton(R.string.winpositivebtn, null);
		dialog.show();
		hasClicked = false;
		ArrayList<ImageButton> imageButtons = getImageButtons();
		// Set all buttons to be un-clickable
		for (int i = 0; i < 9; i++)
			imageButtons.get(i).setClickable(false);
	}

	// Display / Update the counters
	private void displayCounts() {
		TextView textView1 = (TextView) findViewById(R.id.misses_ctr);
		textView1.setText(getString(R.string.missesctr) + missesCtr);
		textView1 = (TextView) findViewById(R.id.matches_ctr);
		textView1.setText(getString(R.string.matchesctr) + matchesCtr);
		textView1 = (TextView) findViewById(R.id.total_misses_ctr);
		textView1.setText(getString(R.string.totalmissesctr) + totalMissesCtr);
		textView1 = (TextView) findViewById(R.id.total_matches_ctr);
		textView1.setText(getString(R.string.totalmatchesctr) + totalMatchesCtr);
	}
}
