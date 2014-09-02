package etong.simiquan;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
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
        mImageRes = resId;
    }


    private void endScraling() {
        if (mHeaderContainer.getBottom() >= mHeaderHeight)
        mScalingRunnalable.startAnimation(200L);
    }

    private void init(Context paramContext) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        ((Activity) paramContext).getWindowManager().getDefaultDisplay()
                .getMetrics(localDisplayMetrics);
        mScreenHeight = localDisplayMetrics.heightPixels;
        mHeaderContainer = new FrameLayout(paramContext);
        mHeaderImage = new ImageView(paramContext);
        int i = localDisplayMetrics.widthPixels;
        setHeaderViewSize(i, 450);
        mShadow = new ImageView(paramContext);
        FrameLayout.LayoutParams localLayoutParams = new FrameLayout.LayoutParams(
                -1, -2);
        localLayoutParams.gravity = 80;
        mShadow.setLayoutParams(localLayoutParams);
        mHeaderContainer.addView(mHeaderImage);
        mHeaderContainer.addView(mShadow);
        addHeaderView(mHeaderContainer);
        mScalingRunnalable = new ScalingRunnalable();
        super.setOnScrollListener(this);
    }

    private void onSecondaryPointerUp(MotionEvent me) {
        int i = (me.getAction()) >> 8;
        if (me.getPointerId(i) == mActivePointerId)
            if (i != 0) {
                int j = 1;
                mLastMotionY = me.getY(0);
                mActivePointerId = me.getPointerId(0);
                return;
            }
    }

    private void reset() {
        mActivePointerId = -1;
        mLastMotionY = -1.0F;
        mMaxScale = -1.0F;
        mLastScale = -1.0F;
    }

    public ImageView getHeaderView() {
        return mHeaderImage;
    }

    public void setTitlebarHeight(int height) {
        titlebarHeight = height;
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
        if (mHeaderHeight == 0) {
            mHeaderHeight = mHeaderContainer.getHeight();
        }
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
        float f = mHeaderHeight - mHeaderContainer.getBottom();
        if ((f > 0.0F) && (f < mHeaderHeight)) {
            int i = (int) (0.5D * f);
            mHeaderContainer.scrollTo(0, -i);
        } else if (mHeaderContainer.getScrollY() != 0) {
            mHeaderContainer.scrollTo(0, 0);
        }
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(paramAbsListView, paramInt1,
                    paramInt2, paramInt3);
        }
    }

    public void onScrollStateChanged(AbsListView paramAbsListView, int paramInt) {
        if (mOnScrollListener != null)
            mOnScrollListener.onScrollStateChanged(paramAbsListView,
                    paramInt);
    }

    public boolean onTouchEvent(MotionEvent me) {
        switch (me.getAction()) {
            case 4:
            case MotionEvent.ACTION_DOWN:
                if (!mScalingRunnalable.mIsFinished) {
                    mScalingRunnalable.abortAnimation();
                }
                mLastMotionY = me.getY();
                mActivePointerId = me.getPointerId(0);
                mMaxScale = (float)mScreenHeight / (float)mHeaderHeight;
                mLastScale = (mHeaderContainer.getBottom() / mHeaderHeight);
                break;
            case MotionEvent.ACTION_MOVE:
                int j = me.findPointerIndex(mActivePointerId);
                if (j == -1) {
                } else {
                    if (mLastMotionY == -1.0F)
                        mLastMotionY = me.getY(j);
                    if (mHeaderContainer.getBottom() >= mHeaderHeight) {
                        ViewGroup.LayoutParams localLayoutParams = mHeaderContainer
                                .getLayoutParams();
                        float f = ((me.getY(j) - mLastMotionY + mHeaderContainer
                                .getBottom()) / mHeaderHeight - mLastScale)
                                / 2.0F + mLastScale;
                        if ((mLastScale <= 1.0D) && (f < mLastScale)) {
                            localLayoutParams.height = mHeaderHeight;
                            mHeaderContainer
                                    .setLayoutParams(localLayoutParams);
                            return super.onTouchEvent(me);
                        }
                        mLastScale = Math.min(Math.max(f, 1.0F),
                                mMaxScale);
                        localLayoutParams.height = ((int) (mHeaderHeight * mLastScale));
                        if (localLayoutParams.height < mScreenHeight)
                            mHeaderContainer
                                    .setLayoutParams(localLayoutParams);
                        mLastMotionY = me.getY(j);
                        return true;
                    }
                    mLastMotionY = me.getY(j);
                }
                break;
            case MotionEvent.ACTION_UP:
                reset();
                endScraling();
                break;
            case MotionEvent.ACTION_CANCEL:
                int i = me.getActionIndex();
                mLastMotionY = me.getY(i);
                mActivePointerId = me.getPointerId(i);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                onSecondaryPointerUp(me);
                mLastMotionY = me.getY(me
                        .findPointerIndex(mActivePointerId));
                break;
            case 6:
        }
        return super.onTouchEvent(me);
    }

    public void setHeaderViewSize(int paramInt1, int paramInt2) {
        Object localObject = mHeaderContainer.getLayoutParams();
        if (localObject == null)
            localObject = new LayoutParams(paramInt1, paramInt2);
        ((ViewGroup.LayoutParams) localObject).width = paramInt1;
        ((ViewGroup.LayoutParams) localObject).height = paramInt2;
        mHeaderContainer
                .setLayoutParams((ViewGroup.LayoutParams) localObject);
        mHeaderHeight = paramInt2;
    }

    public void setHeaderViewClonerSize(int paramInt1, int paramInt2) {
        Object localObject = mHeaderContainerCloner.getLayoutParams();
        if (localObject == null)
            localObject = new LayoutParams(paramInt1, paramInt2);
        ((ViewGroup.LayoutParams) localObject).width = paramInt1;
        ((ViewGroup.LayoutParams) localObject).height = paramInt2;
        mHeaderContainerCloner
                .setLayoutParams((ViewGroup.LayoutParams) localObject);
//        mHeaderHeight = paramInt2;
    }

    public void setOnScrollListener(
            OnScrollListener paramOnScrollListener) {
        mOnScrollListener = paramOnScrollListener;
    }

    public void setShadow(int paramInt) {
        mShadow.setBackgroundResource(paramInt);
    }

    class ScalingRunnalable implements Runnable {
        long mDuration;
        boolean mIsFinished = true;
        float mScale;
        long mStartTime;

        ScalingRunnalable() {
        }

        public void abortAnimation() {
            mIsFinished = true;
        }

        public boolean isFinished() {
            return mIsFinished;
        }

        public void run() {
            float f2;
            ViewGroup.LayoutParams localLayoutParams;
            if ((!mIsFinished) && (mScale > 1.0D)) {
                float f1 = ((float) SystemClock.currentThreadTimeMillis() - (float) mStartTime)
                        / (float) mDuration;
                f2 = mScale - (mScale - 1.0F)
                        * PullToZoomListView.sInterpolator.getInterpolation(f1);
                localLayoutParams = PullToZoomListView.this.mHeaderContainer
                        .getLayoutParams();
                if (f2 > 1.0F) {
                    localLayoutParams.height = PullToZoomListView.this.mHeaderHeight;
                    ;
                    localLayoutParams.height = ((int) (f2 * PullToZoomListView.this.mHeaderHeight));
                    PullToZoomListView.this.mHeaderContainer
                            .setLayoutParams(localLayoutParams);
                    PullToZoomListView.this.post(this);
                    return;
                }
                mIsFinished = true;
            }
        }

        public void startAnimation(long paramLong) {
            mStartTime = SystemClock.currentThreadTimeMillis();
            mDuration = paramLong;
            mScale = ((float) (PullToZoomListView.this.mHeaderContainer
                    .getBottom()) / PullToZoomListView.this.mHeaderHeight);
            mIsFinished = false;
            PullToZoomListView.this.post(this);
        }
    }
}
