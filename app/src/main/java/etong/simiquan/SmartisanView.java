package etong.simiquan;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

    public LinearListView linearListView;

    public SmartisanView(Context context) {
        super(context);
    }

    public SmartisanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.image_list_view, this);
        relativeLayout = (RelativeLayout) findViewById(R.id.headView);
        imageView = (ImageView) findViewById(R.id.backgroundImageView);
        linearListView = (LinearListView) findViewById(R.id.listView);

    }

    public SmartisanView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
                if (imageView.getHeight() < 100) {
                    setImageViewHeight(100);
                    return true;
                } else if (imageView.getHeight() == 100) {
                    Log.i("etong", "<=100");
                    if (increase > 0) {
                        changeImageViewHeight(increase);
                        return true;
                    }
                } else if (imageView.getHeight() == 500) {
                    Log.i("etong", ">=500");
                    if (increase < 0) {
                        changeImageViewHeight(increase);
                        return true;
                    }
                } else if (imageView.getHeight() > 500) {
                    setImageViewHeight(500);
                    return true;
                } else {
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

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
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

}
