package etong.simiquan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;
import android.widget.ImageView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * Created by Administrator on 2014/9/10.
 */
public class ASListView extends ListView {

    //动画中
    private boolean doingAnim = false;

    //移动镜像
    private ImageView moveImageView;

    public boolean isDoingAnim() {
        return doingAnim;
    }

    public ASListView(Context context) {
        super(context);
    }

    public ASListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ASListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 创建移动的镜像
     *
     * @param bitmap
     */
    private void createMoveImage(Bitmap bitmap, View view) {
        moveImageView = new ImageView(getContext());
        moveImageView.setImageBitmap(bitmap);
        moveImageView.layout(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
    }

    /**
     * 从界面上面移动镜像
     */
    private void removeMoveImage() {
        if (moveImageView != null) {
            moveImageView = null;
        }
    }

    /**
     *  排序前摇
     * @param fromPosition
     * @param toPosition
     */
    public void move(int fromPosition, int toPosition) {

        final View view = getChildAt(fromPosition - getFirstVisiblePosition());
        if (view == null) {
            return;
        }
        if (toPosition <= fromPosition) {
            return;
        }
        //开启mDragItemView绘图缓存
        view.setDrawingCacheEnabled(true);
        //获取mDragItemView在缓存中的Bitmap对象
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        //这一步很关键，释放绘图缓存，避免出现重复的镜像
        view.destroyDrawingCache();
        //新建移动的ImageView
        createMoveImage(bitmap, view);

        //隐藏移动对象，用镜像代替移动
        view.setVisibility(View.INVISIBLE);

        int translationY = view.getBottom() + ((toPosition - fromPosition - 1) * view.getHeight()) - view.getTop();
        ObjectAnimator moveAnimator = ObjectAnimator.ofFloat(moveImageView, "translationY", translationY);
        ViewWrapper viewWrapper = new ViewWrapper(view);
        ObjectAnimator heightAnimator = ObjectAnimator.ofInt(viewWrapper, "height", view.getHeight(), 0);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(moveAnimator, heightAnimator);
        animatorSet.setDuration(500);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                doingAnim =true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //动画执行完毕，恢复列表显示
                view.setVisibility(View.VISIBLE);
                //去除镜像
                removeMoveImage();
                doingAnim =false;
                //通知动画执行完毕
                if (onPositionChangeListener != null)
                    onPositionChangeListener.changed();

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                doingAnim =false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (moveImageView != null) {
            drawChild(canvas, moveImageView, getDrawingTime());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setOnPositionChangeListener(OnPositionChangeListener onPositionChangeListener) {
        this.onPositionChangeListener = onPositionChangeListener;
    }

    private OnPositionChangeListener onPositionChangeListener;

    interface OnPositionChangeListener {
        public void changed();
    }
}
