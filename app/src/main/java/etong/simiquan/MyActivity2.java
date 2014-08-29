package etong.simiquan;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.nineoldandroids.animation.ObjectAnimator;

public class MyActivity2 extends Activity {

	private View contentView;

	private RelativeLayout relayout;
	
	private EditText editText;
	
	private ListView listView;

	private float density;
	int offsetY;
	int offsetHeight;
	
	int imageId;
	
	private String animType;

	private List<String> strs = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		density = getResources().getDisplayMetrics().density;
		contentView = LayoutInflater.from(this).inflate(
				R.layout.activity_my_activity2, null);
		Intent intent = getIntent();
		offsetHeight = intent.getIntExtra("offsetHeight", 0);
		offsetY = intent.getIntExtra("offsetY", 0);
		animType = intent.getStringExtra("animType");
		imageId = intent.getIntExtra("imageId", R.drawable.tu2);

		setContentView(contentView);

		relayout = (RelativeLayout) findViewById(R.id.headview);

		relayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				doEndAnim();
			}
		});
		ImageView imageView = (ImageView) LayoutInflater.from(this).inflate(R.layout.list_item,null);
		imageView.setImageResource(imageId);
		editText = (EditText) findViewById(R.id.editText);

		strs.add("ABCDEFGHIJKLMN");
		strs.add("ABCDEFGHIJKLMN");
		strs.add("ABCDEFGHIJKLMN");
		strs.add("ABCDEFGHIJKLMN");
		strs.add("ABCDEFGHIJKLMN");
		strs.add("ABCDEFGHIJKLMN");
		strs.add("ABCDEFGHIJKLMN");
		strs.add("ABCDEFGHIJKLMN");
		strs.add("ABCDEFGHIJKLMN");
		strs.add("ABCDEFGHIJKLMN");
		strs.add("ABCDEFGHIJKLMN");

		listView = (ListView) findViewById(R.id.listView);

		listView.setAdapter(new ArrayAdapter<String>(this,
				
				R.layout.comment_item, strs));

        listView.addHeaderView(imageView);

		doStartAnim();

	}
	
	public int getStatuBarHeight(){
		Class<?> c = null;
	    Object obj = null;
	    Field field = null;
	    int x = 0,sbar = 0;
	    try{
	            c = Class.forName("com.android.internal.R$dimen");
	            obj = c.newInstance();
	            field = c.getField("status_bar_height");
	            x = Integer.parseInt(field.get(obj).toString());
	            sbar = getResources().getDimensionPixelSize(x);
	    }catch(Exception e){
	            e.printStackTrace();
	    }
	    return sbar;
	}
	
	public int getContentViewHeight(){
		WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		int screenHeight = wm.getDefaultDisplay().getHeight();
		return screenHeight - getStatuBarHeight()+8;
	}
	
	

	private void doStartAnim() {
		Animation anim = new TranslateAnimation(0, 0, offsetY-getStatuBarHeight()+4, 0);
		anim.setDuration(200);
		contentView.setAnimation(anim);
		Log.i("etong", "type: "+animType);
		if(animType.equals("fade")){
			Animation fadeAnim= AnimationUtils.loadAnimation(this, R.anim.abc_fade_in);
			fadeAnim.setDuration(200);
			listView.startAnimation(fadeAnim);
		}else if(animType.equals("zoom")){
			ViewWrapper viewWrapper = new ViewWrapper(contentView);
			ObjectAnimator objectAnimator = ObjectAnimator.ofInt(viewWrapper,
					"height",offsetHeight,getContentViewHeight());
			objectAnimator.setDuration(200);
			objectAnimator.start();
		}

		
	}

	private void doEndAnim() {
		Log.i("etong", "offsetY" + offsetY);
		Animation anim = new TranslateAnimation(0, 0, 0, offsetY-getStatuBarHeight()+4);
		anim.setDuration(200);
		contentView.startAnimation(anim);

		anim.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				editText.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				contentView.setVisibility(View.INVISIBLE);
				finish();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
		
		Log.i("etong", "type: "+animType);
		if(animType.equals("fade") ){
			Log.i("etong","fade");
			Animation fadeAnim= AnimationUtils.loadAnimation(this, R.anim.abc_fade_out);
			fadeAnim.setDuration(500);
			listView.startAnimation(fadeAnim);
		}else if(animType.equals("zoom")){
			Log.i("etong","zoom");
			ViewWrapper viewWrapper = new ViewWrapper(contentView);
			ObjectAnimator objectAnimator = ObjectAnimator.ofInt(viewWrapper,
					"height",offsetHeight);
			objectAnimator.setDuration(150);
			objectAnimator.start();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_activity2, menu);
		return true;
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

	public class ViewWrapper {

		private View view;

		public ViewWrapper(View view) {
			this.view = view;
		}

		public int getHeight() {
			return view.getLayoutParams().height;
		}

		public void setHeight(int height) {
			view.getLayoutParams().height = height;
			view.requestLayout();
		}

	}

	@Override
	public void onBackPressed() {
		doEndAnim();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
