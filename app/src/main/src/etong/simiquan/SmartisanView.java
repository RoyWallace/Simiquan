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
            imageViewCloner.layout(12, 12, imageView.getWidth(), 100);
            drawChild(canvas, imageViewCloner, getDrawingTime());
        }

    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        relativeLayout.scrollTo(0, relativeLayout.getTop() - scrollY / 2);
        Log.i("etong", "linearListView top: " + (linearListView.getTop() - scrollY));
        if (imageViewCloner == null) {
            imageViewCloner = new ImageView(getContext());
            imageViewCloner.setImageResource(R.drawable.tu1);
            imageViewCloner.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageViewCloner.layout(12,100, imageView.getWidth(), 100);
        }
        if (linearListView.getTop() - scrollY <= 100) {
            imageViewCloner.setVisibility(View.VISIBLE);
        } else {
            imageViewCloner.setVisibility(View.GONE);
        }
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
