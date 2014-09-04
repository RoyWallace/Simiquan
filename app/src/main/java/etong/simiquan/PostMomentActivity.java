package etong.simiquan;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class PostMomentActivity extends Activity {

    private RelativeLayout contentView;

    private TextView titleView;

    private View lineView;

    private RelativeLayout headView;

    private ImageView imageView;

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentView = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.post_moment_activity,null);
        findAllView();
        startAnimation();
        stowData();
        setContentView(contentView);

    }


    private void findAllView() {

        titleView = (TextView) contentView.findViewById(R.id.titleView);

        lineView = contentView.findViewById(R.id.lineView);

        headView = (RelativeLayout) contentView.findViewById(R.id.headView);

        imageView = (ImageView) contentView.findViewById(R.id.backgroundImageView);

        editText = (EditText) contentView.findViewById(R.id.editText);

        Log.i("etong","findAllView finish");

    }

    private void startAnimation() {
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.abc_slide_in_top);
        titleView.startAnimation(animation);
        lineView.startAnimation(animation);
        headView.startAnimation(AnimationUtils.loadAnimation(this,R.anim.abc_fade_in));
        editText.startAnimation(AnimationUtils.loadAnimation(this,R.anim.abc_slide_in_bottom));
    }

    private void endAnimation(){
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.abc_slide_out_top);
        titleView.startAnimation(animation);
        lineView.startAnimation(animation);
        headView.startAnimation(AnimationUtils.loadAnimation(this,R.anim.abc_fade_out));
        editText.startAnimation(AnimationUtils.loadAnimation(this,R.anim.abc_slide_out_bottom));
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                titleView.setVisibility(View.GONE);
                headView.setVisibility(View.GONE);
                editText.setVisibility(View.GONE);
                contentView.setVisibility(View.GONE);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void stowData() {
        imageView.setImageResource(R.drawable.tu1);
        Log.i("etong","stowData finish");
    }

    @Override
    public void onBackPressed() {
        endAnimation();
    }
}
