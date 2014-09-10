package etong.simiquan;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ImageView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2014/9/10.
 */
public class ASListView extends ListView {

    private boolean doingAnim = false;

    private WindowManager windowManager;

    private WindowManager.LayoutParams mWindowLayoutParams;

    private ImageView moveImageView;

    private int startLeft;
    private int startRight;
    private int startTop;
    private int startBottom;

    private int mStatusHeight;

    public ASListView(Context context) {
        super(context);
    }

    public ASListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mStatusHeight = getStatuBarHeight(); //获取状态栏的高度
        Log.i("etong", "height" + mStatusHeight);
    }

    public ASListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public int getStatuBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sbar;
    }

    /**
     * 创建拖动的镜像
     *
     * @param bitmap
     * @param x      按下的点相对父控件的X坐标
     * @param y      按下的点相对父控件的X坐标
     */
    private void createMoveImage(Bitmap bitmap, int x, int y) {
        mWindowLayoutParams = new WindowManager.LayoutParams();
        mWindowLayoutParams.format = PixelFormat.TRANSLUCENT; //图片之外的其他地方透明
        mWindowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        mWindowLayoutParams.x = x;
        mWindowLayoutParams.y = y;
        mWindowLayoutParams.alpha = 1.0f; //透明度
        mWindowLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mWindowLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        moveImageView = new ImageView(getContext());
        moveImageView.setImageBitmap(bitmap);
        windowManager.addView(moveImageView, mWindowLayoutParams);
    }

    /**
     * 从界面上面移动拖动镜像
     */
    private void removeMoveImage() {
        if (moveImageView != null) {
            windowManager.removeView(moveImageView);
            moveImageView = null;
        }
    }

    public void move(int fromPosition, int toPosition) {

        final View view = getChildAt(fromPosition);
        if (view == null) {
            return;
        }
        //开启mDragItemView绘图缓存
        view.setDrawingCacheEnabled(true);
        //获取mDragItemView在缓存中的Bitmap对象
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        //这一步很关键，释放绘图缓存，避免出现重复的镜像
        view.destroyDrawingCache();
        int[] location = new int[2];
        view.getLocationOnScreen(location);

        createMoveImage(bitmap, location[0], location[1] - mStatusHeight);

        ObjectAnimator moveAnimator = ObjectAnimator.ofFloat(moveImageView, "translationY", view.getTop() - mStatusHeight, view.getBottom() * (toPosition - fromPosition) - mStatusHeight);

        ViewWrapper viewWrapper = new ViewWrapper(view);
        ObjectAnimator heightAnimator = ObjectAnimator.ofInt(viewWrapper, "height", view.getHeight(), 0);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(moveAnimator, heightAnimator);
        animatorSet.setDuration(1000);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.VISIBLE);
                removeMoveImage();
                if(onPositionChangeListener !=null)
                    onPositionChangeListener.changed();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();

        postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.INVISIBLE);

            }
        },80);

    }

    public OnPositionChangeListener getOnPositionChangeListener() {
        return onPositionChangeListener;
    }

    public void setOnPositionChangeListener(OnPositionChangeListener onPositionChangeListener) {
        this.onPositionChangeListener = onPositionChangeListener;
    }

    private OnPositionChangeListener onPositionChangeListener;

    interface OnPositionChangeListener {
        public void changed();
    }
}
