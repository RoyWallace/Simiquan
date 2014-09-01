package etong.simiquan;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.linearlistview.LinearListView;

/**
 * Created by Administrator on 2014/8/30.
 */
public class SmartisanView extends ScrollView {

    public RelativeLayout relativeLayout;

    public ImageView imageView;

    public RelativeLayout relativeLayoutCloner;

    public ImageView imageViewCloner;

    public LinearListView linearListView;

    public int headViewTop;

    public SmartisanView(Context context) {
        super(context);
        initView();
    }

    public SmartisanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();

    }

    public SmartisanView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.image_list_view, this);
        relativeLayout = (RelativeLayout) findViewById(R.id.headView);
        imageView = (ImageView) findViewById(R.id.backgroundImageView);
        linearListView = (LinearListView) findViewById(R.id.listView);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (imageViewCloner != null) {
//            imageViewCloner.layout(12, 12, imageView.getWidth(), 100);
            Log.i("etong","imageViewCloner y: "+imageViewCloner.getTop());
            drawChild(canvas, imageViewCloner, getDrawingTime());
        }

    }

    public OverScrolled overScrolled;

    public void setOverScrolled(OverScrolled overScrolled){
        this.overScrolled = overScrolled;
    }


    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        relativeLayout.scrollTo(0, relativeLayout.getTop() - scrollY / 2);
        Log.i("etong", "linearListView top: " + (linearListView.getTop() - scrollY));
        if(overScrolled!=null)
        overScrolled.showTitle();
        if (imageViewCloner == null) {
            imageViewCloner = new ImageView(getContext());
            imageViewCloner.setImageResource(R.drawable.tu1);
            imageViewCloner.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageViewCloner.layout(12,-200, imageView.getWidth(), 100);
        }
        if (linearListView.getTop() - scrollY <= 100) {
            imageViewCloner.setVisibility(View.VISIBLE);
        } else {
            imageViewCloner.setVisibility(View.GONE);
        }
    }

    private boolean start = true;
    float offsetY = 0;
    float y = 0;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                offsetY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (start) {
                    offsetY = ev.getRawY();
                    start = false;
                }
                y = ev.getRawY();
//                if (getScrollY() == 0) {
                int increase = (int) (y - offsetY);
                Log.i("etong", "y = " + y);
                Log.i("etong", "offsetY = " + offsetY);
                Log.i("etong", "increase: " + increase);
                offsetY = y;
                 if (imageView.getHeight() >= 400) {
                    Log.i("etong", "changeHeight");
                    changeImageViewHeight(increase);
                    return true;
                }
//                }
                break;
            case MotionEvent.ACTION_CANCEL:
                offsetY = 0;
                y = 0;
                start = true;
                break;
            case MotionEvent.ACTION_UP:
                offsetY = 0;
                y = 0;
                start = true;
                break;
        }

        return super.onTouchEvent(ev);
    }

    public void changeImageViewHeight(int increase) {
        int offsetH = relativeLayout.getHeight();
        relativeLayout.getLayoutParams().height = offsetH + increase;
        imageView.getLayoutParams().height = offsetH + increase;
        relativeLayout.requestLayout();
        imageView.requestLayout();
    }

    public void setImageViewHeight(int height) {
        relativeLayout.getLayoutParams().height = height;
        imageView.getLayoutParams().height = height;
        relativeLayout.requestLayout();
        imageView.requestLayout();
    }

    public interface OverScrolled{
        public void showTitle();
    };

}
