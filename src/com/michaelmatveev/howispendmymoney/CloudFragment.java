package com.michaelmatveev.howispendmymoney;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.michaelmatveev.wordcloud.AMath;
import com.michaelmatveev.wordcloud.Anglers;
import com.michaelmatveev.wordcloud.Colorers;
import com.michaelmatveev.wordcloud.Placers;
import com.michaelmatveev.wordcloud.Sizers;
import com.michaelmatveev.wordcloud.Word;
import com.michaelmatveev.wordcloud.WordCloud;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.Media;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class CloudFragment extends Fragment 
implements PeriodFilter {
	private SQLiteHelper dbHelper;
	private WordCloud wordCloud;
	private WordCloudView wordCloudView;
	private Date period;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		dbHelper = new SQLiteHelper(this.getActivity());
		createWordCloud();
	}
	
	//@Override
	//public void onResume() {
		//super.onResume();
		//refresh();
		//createWordCloud();
	//}
	
	/*
	public boolean onTouchEvent(MotionEvent event) {
	    int eventaction = event.getAction();

	    switch (eventaction) {
	        case MotionEvent.ACTION_DOWN: 
	            // finger touches the screen
	            break;

	        case MotionEvent.ACTION_MOVE:
	            // finger moves on the screen
	            break;

	        case MotionEvent.ACTION_UP:   
	            // finger leaves the screen
	            break;
	    }

	    // tell the system that we handled the event and no further processing is required
	    return true; 
	}*/
	
	private void createWordCloud() {
		wordCloud = new WordCloud(getWords())
		//.withCustomCanvas(pg)
		//.fromTextFile(textFilePath())
		//.fromWords(alphabet())
		//.upperCase()
		//.lowerCase()
		//.excludeNumbers()		
		.withFonts(randomTypeface())			
		.withColorer(Colorers.twoHuesRandomSats())
		.withAngler(Anglers.mostlyHoriz())
		.withPlacer(Placers.horizLine())
		.withSizer(Sizers.byWeight(10, 60));
		//.withSizer(Sizers.byRank(5, 30))
		//.withNudger(new RandomWordNudger())
		//.minPathSize(15)
		//.maxAttemptsToPlaceWord(10)						

wordCloud.maxNumberOfWordsToDraw(50);	
		
	}
	
	private Word[] getWords() {
		float weight = 0;
		float amount = 0;
		float min = Float.MAX_VALUE;;
		float max = 0;
		
		List<Pair<String, Float>> entries = new ArrayList<Pair<String, Float>>();
		Cursor c = dbHelper.getEntriesAndTags(period);
		c.moveToFirst();
		while(!c.isAfterLast()) {			
			amount = c.getFloat(1);
			
			if(amount != 0) {
				entries.add(new Pair<String, Float>(c.getString(0), amount));				
				if(min > amount)  {
					min = amount;
				}
				if(max < amount) {
					max = amount;
				}
			}
			c.moveToNext();
		}
		c.close();
		int i = 0;
		Word[] result = new Word[entries.size()];
		for(Pair<String, Float> en: entries) {
			weight = AMath.norm(en.second, min, max);
			Word w = new Word(en.first, weight);
			w.setProperty("Amount", en.second);
			result[i++] = w;
		}
	
		return result;		
	}
	
	private AssetManager am;
	private Typeface randomTypeface() {
		
		ArrayList<Typeface> typeFaces = new ArrayList<Typeface>();
		am = this.getActivity().getAssets();
		typeFaces.add(Typeface.createFromAsset(am, "fonts/AEbook.ttf"));
		typeFaces.add(Typeface.createFromAsset(am, "fonts/Days.otf"));
		typeFaces.add(Typeface.createFromAsset(am, "fonts/georgia-ebook.ttf"));
		typeFaces.add(Typeface.createFromAsset(am, "fonts/georgia.ttf"));
		typeFaces.add(Typeface.createFromAsset(am, "fonts/orbitron-black.otf"));	
		typeFaces.add(Typeface.createFromAsset(am, "fonts/orbitron-bold.otf"));	
		typeFaces.add(Typeface.createFromAsset(am, "fonts/orbitron-light.otf"));	
		typeFaces.add(Typeface.createFromAsset(am, "fonts/orbitron-medium.otf"));
		typeFaces.add(Typeface.createFromAsset(am, "fonts/trebuc.ttf"));

		Typeface x = typeFaces.get(new Random().nextInt(typeFaces.size()));
		
		return x;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.setHasOptionsMenu(true);
		wordCloudView = new WordCloudView(this.getActivity());

		wordCloudView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Word[] words = wordCloud.getWordsAt(event.getX(), event.getY(), 10f);
				if(words.length == 1) {
					showWordInfo(words[0]);
				}
				else if(words.length > 1) {
					showMenu(words);
				}
				return false;
			}
		});

		return wordCloudView;
	}
	
	private void showWordInfo(Word w) {
		String message = String.format("%s %.2f", w.word, w.getProperty("Amount"));
		Toast t = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
		t.setGravity(Gravity.CENTER, 0, 0);
		t.show();
	}
	
	private void showMenu(final Word[] words) {
		FragmentActivity activity = getActivity();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        //LayoutInflater inflater = getActivity().getLayoutInflater();
        //View convertView = (View)inflater.inflate(R.layout.custom, null);
        ArrayList<String> names = new ArrayList<String>(words.length);
        for(int i = 0; i < words.length; i++) {
        	names.add(i, words[i].word);
        }
        ListView lv = new ListView(activity);
        alertDialog.setView(lv);
        final AlertDialog alert = alertDialog.create();
        ArrayAdapter<String> adapter = 
        		new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, names.toArray(new String[words.length]));
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int index, long l) {
            	alert.dismiss();
            	showWordInfo(words[index]);
            }
        });
        alert.show();
	}
	
	private class WordCloudView extends View {
		
		private Rect rectangle = new Rect();
		
		@SuppressLint("NewApi") 
		public WordCloudView(Context context){
			super(context);
			int currentapiVersion = android.os.Build.VERSION.SDK_INT;
			if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
				setLayerType(View.LAYER_TYPE_SOFTWARE, null);
			}
			//invalidate();
		}
/*
	       @Override 
	        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
	            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	            int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
	            int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
	            this.setMeasuredDimension(parentWidth, parentHeight);
	        }
	*/       
		@Override 
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			
			Window window = getActivity().getWindow();
			window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
			int statusBarHeight = 0;//rectangle.top;
			
			wordCloud.drawAll(canvas, statusBarHeight);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    inflater.inflate(R.menu.cloud, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.action_add_new:
				startTransactionEditActivity();
				return true;
			case R.id.action_refresh:
				refresh();
				return true;
			case R.id.action_share_cloud:
				shareCloud();
				return true;
			default: 
				return super.onOptionsItemSelected(item);	
		}		
	}

	private boolean startTransactionEditActivity() {
		Intent intent = new Intent(getActivity(), TransactionEditActivity.class);
		intent.putExtra(TransactionEditActivity.OPEN_MODE, TransactionEditActivity.Mode.ADD);
		intent.putExtra(TransactionEditActivity.TITLE, getResources().getString(R.string.action_new_transaction));
		startActivityForResult(intent, 1);
		return true;
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Activity.RESULT_OK) {
			this.refresh();
		}
	}
	
	private void refresh() {
		createWordCloud();
		wordCloudView.invalidate();	
	}
	
	private void shareCloud() {

		View view = getView();
		Bitmap cloud = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(cloud);
		view.draw(c);
		
		Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("image/png");

		ContentValues values = new ContentValues();
		values.put(Images.Media.TITLE, "How I spend my money");
		values.put(Images.Media.MIME_TYPE, "image/png");
		Uri uri = getActivity().getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);


		OutputStream outstream;
		try {
		    outstream = getActivity().getContentResolver().openOutputStream(uri);
		    cloud.compress(Bitmap.CompressFormat.PNG, 100, outstream);
		    outstream.close();
		} catch (Exception e) {
		    System.err.println(e.toString());
		}

		share.putExtra(Intent.EXTRA_STREAM, uri);
		startActivity(Intent.createChooser(share, "Share Image"));
	}

	
	public void setPeriod(int periodId) {
		Date start = TimeIntervals.getPeriod(periodId).first;
		if(period != start) {
			period = start;
			refresh();
		}
	}
}
