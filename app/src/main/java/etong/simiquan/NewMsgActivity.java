package etong.simiquan;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;


public class NewMsgActivity extends ActionBarActivity {

    private CircleCanvasLayout contentView;

    private ASListView listView;

    private int[] resIds = {R.drawable.tu1, R.drawable.tu2, R.drawable.tu3, R.drawable.tu4};

    private List<Integer> imageIdList = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_msg_activity);

        findAllView();
        stowData();
        setAllListener();
    }

    private void findAllView() {
        contentView = (CircleCanvasLayout) findViewById(R.id.contentView);
        listView = (ASListView) findViewById(R.id.listview);
    }

    private void stowData() {

        for (int i = 0; i < 10; i++) {
            int num = (int) (Math.random() * (resIds.length));
            imageIdList.add(resIds[num]);
        }

        listView.setAdapter(new MyAdapter(this, imageIdList));
    }

    private void setAllListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(new Intent(NewMsgActivity.this, MyActivity5.class));
                intent.putExtra("position", i);
                intent.putExtra("imageId", imageIdList.get(i));
                startActivityForResult(intent, 200);
//                overridePendingTransition(R.anim.nothing,R.anim.abc_slide_out_top);
            }
        });
    }

    public void moveToPosition(View view) {
//        ObjectAnimator moveAnimator = ObjectAnimator.ofFloat(view,"translationY",(view.getBottom()*2));
//        moveAnimator.setDuration(1000);
//        moveAnimator.start();
        Animation moveAnimation = new TranslateAnimation(0, 0, 0, view.getBottom() * 2);
        moveAnimation.setDuration(1000);
        view.startAnimation(moveAnimation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int position =  data.getIntExtra("position",0);
        listView.move(0,5);
    }
}
