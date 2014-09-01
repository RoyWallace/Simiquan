package etong.simiquan;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

public class PullToZoomListView extends ListView implements
        AbsListView.OnScrollListener {
    private static final int INVALID_VALUE = -1;
    private static final String TAG = "PullToZoomListView";
    private static final Interpolator sInterpolator = new Interpolator() {
        public float getInterpolation(float paramAnonymousFloat) {
            float f = paramAnonymousFloat - 1.0F;
            return 1.0F + f * (f * (f * (f * f)));
        }
    };
    int mActivePointerId = -1;
    private FrameLayout mHeaderContainer;
    private FrameLayout mHeaderContainerCloner;
    private int mHeaderHeight;
    private ImageView mHeaderImage;
    private ImageView mHeaderImageCloner;
    private int mImageRes;
    float mLastMotionY = -1.0F;
    float mLastScale = -1.0F;
    float mMaxScale = -1.0F;
    private OnScrollListener mOnScrollListener;
    private ScalingRunnalable mScalingRunnalable;
    private int mScreenHeight;
    private ImageView mShadow;
    private int titlebarHeight;

    public PullToZoomListView(Context paramContext) {
        super(paramContext);
        init(paramContext);
    }

    public PullToZoomListView(Context paramContext,
                              AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        init(paramContext);
    }

    public PullToZoomListView(Context paramContext,
                              AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        init(paramContext);
    }

    public void setHeadViewImageRes(int resId) {
        this.mImageRes = resId;
    }


    private void endScraling() {
        if (this.mHeaderContainer.getBottom() >= this.mHeaderHeight)
            Log.d("mmm", "endScraling");
        this.mScalingRunnalable.startAnimation(200L);
    }

    private void init(Context paramContext) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        ((Activity) paramContext).getWindowManager().getDefaultDisplay()
                .getMetrics(localDisplayMetrics);
        this.mScreenHeight = localDisplayMetrics.heightPixels;
        this.mHeaderContainer = new FrameLayout(paramContext);
        this.mHeaderImage = new ImageView(paramContext);
        int i = localDisplayMetrics.widthPixels;
        Log.i("etong", "i: " + i);
        setHeaderViewSize(i, 350);
        this.mShadow = new ImageView(paramContext);
        FrameLayout.LayoutParams localLayoutParams = new FrameLayout.LayoutParams(
                -1, -2);
        localLayoutParams.gravity = 80;
        this.mShadow.setLayoutParams(localLayoutParams);
        this.mHeaderContainer.addView(this.mHeaderImage);
        this.mHeaderContainer.addView(this.mShadow);
        addHeaderView(this.mHeaderContainer);
        this.mScalingRunnalable = new ScalingRunnalable();
        super.setOnScrollListener(this);
    }

    private void onSecondaryPointerUp(MotionEvent me) {
        int i = (me.getAction()) >> 8;
        if (me.getPointerId(i) == this.mActivePointerId)
            if (i != 0) {
                int j = 1;
                this.mLastMotionY = me.getY(0);
                this.mActivePointerId = me.getPointerId(0);
                return;
            }
    }

    private void reset() {
        this.mActivePointerId = -1;
        this.mLastMotionY = -1.0F;
        this.mMaxScale = -1.0F;
        this.mLastScale = -1.0F;
    }

    public ImageView getHeaderView() {
        return this.mHeaderImage;
    }

    public void setTitlebarHeight(int height) {
        this.titlebarHeight = height;
    }

    public boolean onInterceptTouchEvent(MotionEvent me) {
        return super.onInterceptTouchEvent(me);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mHeaderImageCloner != null) {
            if (getChildAt(1) != null && getChildAt(1).getTop() <= titlebarHeight)
                drawChild(canvas, mHeaderImageCloner, getDrawingTime());
        }


    }

    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2,
                            int paramInt3, int paramInt4) {
        super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
        if (this.mHeaderHeight == 0)
            this.mHeaderHeight = this.mHeaderContainer.getHeight();
        if (mHeaderImageCloner == null) {
            mHeaderImageCloner = new ImageView(getContext());
            mHeaderImageCloner.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mHeaderImageCloner.layout(0, 0, mHeaderImage.getWidth(), titlebarHeight);
            mHeaderImageCloner.setImageResource(mImageRes);
        }
    }

    @Override
    public void onScroll(AbsListView paramAbsListView, int paramInt1,
                         int paramInt2, int paramInt3) {
        Log.d("etong", "onScroll");
        float f = this.mHeaderHeight - this.mHeaderContainer.getBottom();
        if ((f > 0.0F) && (f < this.mHeaderHeight)) {
            int i = (int) (0.5D * f);
            this.mHeaderImage.scrollTo(0, -i);
        } else if (this.mHeaderImage.getScrollY() != 0) {
            this.mHeaderImage.scrollTo(0, 0);
        }
        if (this.mOnScrollListener != null) {
            this.mOnScrollListener.onScroll(paramAbsListView, paramInt1,
                    paramInt2, paramInt3);
        }
    }

    public void onScrollStateChanged(AbsListView paramAbsListView, int paramInt) {
        if (this.mOnScrollListener != null)
            this.mOnScrollListener.onScrollStateChanged(paramAbsListView,
                    paramInt);
    }

    public boolean onTouchEvent(MotionEvent me) {
        switch (me.getAction()) {
            case 4:
            case MotionEvent.ACTION_DOWN:
                if (!this.mScalingRunnalable.mIsFinished) {
                    this.mScalingRunnalable.abortAnimation();
                }
                this.mLastMotionY = me.getY();
                this.mActivePointerId = me.getPointerId(0);
                this.mMaxScale = (this.mScreenHeight / this.mHeaderHeight);
                this.mLastScale = (this.mHeaderContainer.getBottom() / this.mHeaderHeight);
                break;
            case MotionEvent.ACTION_MOVE:
                int j = me.findPointerIndex(this.mActivePointerId);
                if (j == -1) {
                } else {
                    if (this.mLastMotionY == -1.0F)
                        this.mLastMotionY = me.getY(j);
                    if (this.mHeaderContainer.getBottom() >= this.mHeaderHeight) {
                        ViewGroup.LayoutParams localLayoutParams = this.mHeaderContainer
                                .getLayoutParams();
                        float f = ((me.getY(j) - this.mLastMotionY + this.mHeaderContainer
                                .getBottom()) / this.mHeaderHeight - this.mLastScale)
                                / 2.0F + this.mLastScale;
                        if ((this.mLastScale <= 1.0D) && (f < this.mLastScale)) {
                            localLayoutParams.height = this.mHeaderHeight;
                            this.mHeaderContainer
                                    .setLayoutParams(localLayoutParams);
                            return super.onTouchEvent(me);
                        }
                        this.mLastScale = Math.min(Math.max(f, 1.0F),
                                this.mMaxScale);
                        localLayoutParams.height = ((int) (this.mHeaderHeight * this.mLastScale));
                        if (localLayoutParams.height < this.mScreenHeight)
                            this.mHeaderContainer
                                    .setLayoutParams(localLayoutParams);
                        this.mLastMotionY = me.getY(j);
                        return true;
                    }
                    this.mLastMotionY = me.getY(j);
                }
                break;
            case MotionEvent.ACTION_UP:
                reset();
                endScraling();
                break;
            case MotionEvent.ACTION_CANCEL:
                int i = me.getActionIndex();
                this.mLastMotionY = me.getY(i);
                this.mActivePointerId = me.getPointerId(i);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                onSecondaryPointerUp(me);
                this.mLastMotionY = me.getY(me
                        .findPointerIndex(this.mActivePointerId));
                break;
            case 6:
        }
        return super.onTouchEvent(me);
    }

    public void setHeaderViewSize(int paramInt1, int paramInt2) {
        Log.i("etong", "height: " + paramInt2);
        Object localObject = this.mHeaderContainer.getLayoutParams();
        if (localObject == null)
            localObject = new LayoutParams(paramInt1, paramInt2);
        ((ViewGroup.LayoutParams) localObject).width = paramInt1;
        ((ViewGroup.LayoutParams) localObject).height = paramInt2;
        this.mHeaderContainer
                .setLayoutParams((ViewGroup.LayoutParams) localObject);
        this.mHeaderHeight = paramInt2;
    }

    public void setHeaderViewClonerSize(int paramInt1, int paramInt2) {
        Log.i("etong", "height: " + paramInt2);
        Object localObject = this.mHeaderContainerCloner.getLayoutParams();
        if (localObject == null)
            localObject = new LayoutParams(paramInt1, paramInt2);
        ((ViewGroup.LayoutParams) localObject).width = paramInt1;
        ((ViewGroup.LayoutParams) localObject).height = paramInt2;
        this.mHeaderContainerCloner
                .setLayoutParams((ViewGroup.LayoutParams) localObject);
//        this.mHeaderHeight = paramInt2;
    }

    public void setOnScrollListener(
            OnScrollListener paramOnScrollListener) {
        this.mOnScrollListener = paramOnScrollListener;
    }

    public void setShadow(int paramInt) {
        this.mShadow.setBackgroundResource(paramInt);
    }

    class ScalingRunnalable implements Runnable {
        long mDuration;
        boolean mIsFinished = true;
        float mScale;
        long mStartTime;

        ScalingRunnalable() {
        }

        public void abortAnimation() {
            this.mIsFinished = true;
        }

        public boolean isFinished() {
            return this.mIsFinished;
        }

        public void run() {
            float f2;
            ViewGroup.LayoutParams localLayoutParams;
            if ((!this.mIsFinished) && (this.mScale > 1.0D)) {
                float f1 = ((float) SystemClock.currentThreadTimeMillis() - (float) this.mStartTime)
                        / (float) this.mDuration;
                f2 = this.mScale - (this.mScale - 1.0F)
                        * PullToZoomListView.sInterpolator.getInterpolation(f1);
                localLayoutParams = PullToZoomListView.this.mHeaderContainer
                        .getLayoutParams();
                if (f2 > 1.0F) {
                    Log.d("mmm", "f2>1.0");
                    localLayoutParams.height = PullToZoomListView.this.mHeaderHeight;
                    ;
                    localLayoutParams.height = ((int) (f2 * PullToZoomListView.this.mHeaderHeight));
                    PullToZoomListView.this.mHeaderContainer
                            .setLayoutParams(localLayoutParams);
                    PullToZoomListView.this.post(this);
                    return;
                }
                this.mIsFinished = true;
            }
        }

        public void startAnimation(long paramLong) {
            this.mStartTime = SystemClock.currentThreadTimeMillis();
            this.mDuration = paramLong;
            this.mScale = ((float) (PullToZoomListView.this.mHeaderContainer
                    .getBottom()) / PullToZoomListView.this.mHeaderHeight);
            this.mIsFinished = false;
            PullToZoomListView.this.post(this);
        }
    }
}
