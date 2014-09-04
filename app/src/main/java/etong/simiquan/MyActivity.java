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
import android.widget.ImageView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.nineoldandroids.animation.Animator;


public class MyActivity extends ActionBarActivity {

    private CircleCanvasLayout contentView;

    private ListView listView;

    private ImageView writeImageView;
    
    private String animType = "fade";
    
    private int [] resIds={R.drawable.tu1,R.drawable.tu2,R.drawable.tu3,R.drawable.tu4};
    
    private List<Integer> imageIdList = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_activity);
        
        findAllView();
        stowData();
        setAllListener();
    }

    private void findAllView() {
        contentView = (CircleCanvasLayout) findViewById(R.id.contentView);
        listView = (ListView) findViewById(R.id.listview);
        writeImageView = (ImageView) findViewById(R.id.writeImageView);

    }

    private void createCircleMenu(){
        FragmentManager fm = getSupportFragmentManager();
    }

    private void stowData(){
    	
    	for(int i=0;i<10;i++){
    		int num=(int)(Math.random()*(resIds.length));
    		imageIdList.add(resIds[num]);
    	}
    	
        listView.setAdapter(new MyAdapter(this,imageIdList));
    }

    private void setAllListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(new Intent(MyActivity.this,MyActivity5.class));
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                intent.putExtra("offsetHeight", view.getHeight());
                intent.putExtra("offsetY", location[1]);
                intent.putExtra("animType", animType);
                intent.putExtra("imageId", imageIdList.get(i));

                startActivity(intent);
//                overridePendingTransition(R.anim.nothing,R.anim.abc_slide_out_top);
            }
        });

        writeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final int cx = (writeImageView.getLeft()+writeImageView.getRight())/2;
                final int cy = (writeImageView.getTop()+writeImageView.getBottom())/2;
                contentView.setPaintColor(getResources().getColor(R.color.white));
//                contentView.setMinRadius(writeImageView.getWidth());
                contentView.setDrawAtBack(false);
                contentView.setPosition(cx,cy);
                contentView.setZoomInListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Intent intent = new Intent(new Intent(MyActivity.this,PostMomentActivity.class));
                        intent.putExtra("cx", cx);
                        intent.putExtra("cy", cy);
                        startActivityForResult(intent, 200);
//                        overridePendingTransition(R.anim.nothing,R.anim.abc_slide_out_top);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                contentView.ZoomIn();

            }
        });
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
        int id = item.getItemId();
        
        animType = (String) item.getTitle();
        
//        if (id == R.id.fade) {
//        	animType = (String) item.getTitle();
//            return true;
//        }else if(id == R.id.zoom){
//        	animType = (String) item.getTitle();
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final int cx = (writeImageView.getLeft()+writeImageView.getRight())/2;
        final int cy = (writeImageView.getTop()+writeImageView.getBottom())/2;
        contentView.setPaintColor(getResources().getColor(R.color.white));
//                contentView.setMinRadius(writeImageView.getWidth());
        contentView.setDrawAtBack(false);
        contentView.setOutDuration(200);
        contentView.setPosition(cx,cy);
        contentView.ZoomOut();
    }
}
