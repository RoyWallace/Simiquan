package etong.simiquan;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class LollipopView extends ListView implements
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
    private RelativeLayout mHeaderContainer;
    private int mHeaderHeight;
    private ImageView mHeaderImage;
    float mLastMotionY = -1.0F;
    float mLastScale = -1.0F;
    float mMaxScale = -1.0F;
    private AbsListView.OnScrollListener mOnScrollListener;
    private ScalingRunnalable mScalingRunnalable;
    private int mScreenHeight;
    private ImageView mShadow;

    public LollipopView(Context paramContext) {
        super(paramContext);
        init(paramContext);
    }

    public LollipopView(Context paramContext,
                        AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        init(paramContext);
    }

    public LollipopView(Context paramContext,
                        AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        init(paramContext);
    }

    private void endScraling() {
        if (mHeaderContainer.getBottom() >= mHeaderHeight)
            Log.d("mmm", "endScraling");
        mScalingRunnalable.startAnimation(200L);
    }

    private void init(Context paramContext) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        if(!isInEditMode())
        ((Activity) paramContext).getWindowManager().getDefaultDisplay()
                .getMetrics(localDisplayMetrics);
        mScreenHeight = localDisplayMetrics.heightPixels;
//        mHeaderContainer = new RelativeLayout(paramContext);
//        mHeaderContainer.setBackgroundResource(R.color.black);
//        mHeaderImage = new ImageView(paramContext);
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
//        LayoutInflater inflater = (LayoutInflater) paramContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        inflater.inflate(R.layout.image_list_view, this);
//        mHeaderContainer = (RelativeLayout) findViewById(R.id.headView);
//        mHeaderImage = (ImageView) findViewById(R.id.backgroundImageView);

//        int i = localDisplayMetrics.widthPixels;
//        setHeaderViewSize(i, (int) (9.0F * (i / 16.0F)));
//        mShadow = new ImageView(paramContext);
//        FrameLayout.LayoutParams localLayoutParams = new FrameLayout.LayoutParams(
//                -1, -2);
//        localLayoutParams.gravity = 80;
//        mShadow.setLayoutParams(localLayoutParams);
//        mHeaderContainer.addView(mHeaderImage,lp);
//        mHeaderContainer.addView(mShadow);
//        addHeaderView(mHeaderContainer);
        mScalingRunnalable = new ScalingRunnalable();
        super.setOnScrollListener(this);
    }

    private void onSecondaryPointerUp(MotionEvent paramMotionEvent) {
        int i = (paramMotionEvent.getAction()) >> 8;
        if (paramMotionEvent.getPointerId(i) == mActivePointerId)
            if (i != 0) {
                int j = 1;
                mLastMotionY = paramMotionEvent.getY(0);
                mActivePointerId = paramMotionEvent.getPointerId(0);
                return;
            }
    }

    private void reset() {
        mActivePointerId = -1;
        mLastMotionY = -1.0F;
        mMaxScale = -1.0F;
        mLastScale = -1.0F;
    }

    @Override
    public void addHeaderView(View v) {
        super.addHeaderView(v);
        mHeaderContainer = (RelativeLayout) v;
        mHeaderContainer.setBackgroundResource(R.color.black);
        mHeaderImage = (ImageView) v.findViewById(R.id.backgroundImageView);
    }

    public ImageView getHeaderView() {
        return mHeaderImage;
    }

    public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent) {
        return super.onInterceptTouchEvent(paramMotionEvent);
    }

    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2,
                            int paramInt3, int paramInt4) {
        super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
        if (mHeaderHeight == 0)
            mHeaderHeight = mHeaderContainer.getHeight();
    }

    @Override
    public void onScroll(AbsListView paramAbsListView, int paramInt1,
                         int paramInt2, int paramInt3) {
        if(mHeaderContainer!= null) {
            float f = mHeaderHeight - mHeaderContainer.getBottom();
            if ((f > 0.0F) && (f < mHeaderHeight)) {
                int i = (int) (0.5D * f);
                mHeaderContainer.scrollTo(0, -i);
            } else if (mHeaderImage.getScrollY() != 0) {
                mHeaderImage.scrollTo(0, 0);
            }
            if (mOnScrollListener != null) {
                mOnScrollListener.onScroll(paramAbsListView, paramInt1,
                        paramInt2, paramInt3);
            }
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
                mLastScale = (float)mHeaderContainer.getBottom() /(float)mHeaderHeight;
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
            localObject = new AbsListView.LayoutParams(paramInt1, paramInt2);
        ((ViewGroup.LayoutParams) localObject).width = paramInt1;
        ((ViewGroup.LayoutParams) localObject).height = paramInt2;
        mHeaderContainer
                .setLayoutParams((ViewGroup.LayoutParams) localObject);
        mHeaderHeight = paramInt2;
    }

    public void setOnScrollListener(
            AbsListView.OnScrollListener paramOnScrollListener) {
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
                        * LollipopView.sInterpolator.getInterpolation(f1);
                localLayoutParams = LollipopView.this.mHeaderContainer
                        .getLayoutParams();
                if (f2 > 1.0F) {
                    Log.d("mmm", "f2>1.0");
                    localLayoutParams.height = LollipopView.this.mHeaderHeight;
                    ;
                    localLayoutParams.height = ((int) (f2 * LollipopView.this.mHeaderHeight));
                    LollipopView.this.mHeaderContainer
                            .setLayoutParams(localLayoutParams);
                    LollipopView.this.post(this);
                    return;
                }
                mIsFinished = true;
            }
        }

        public void startAnimation(long paramLong) {
            mStartTime = SystemClock.currentThreadTimeMillis();
            mDuration = paramLong;
            mScale = ((float) (LollipopView.this.mHeaderContainer
                    .getBottom()) / LollipopView.this.mHeaderHeight);
            mIsFinished = false;
            LollipopView.this.post(this);
        }
    }
}

