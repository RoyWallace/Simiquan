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
        if (this.mHeaderContainer.getBottom() >= this.mHeaderHeight)
            Log.d("mmm", "endScraling");
        this.mScalingRunnalable.startAnimation(200L);
    }

    private void init(Context paramContext) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        if(!isInEditMode())
        ((Activity) paramContext).getWindowManager().getDefaultDisplay()
                .getMetrics(localDisplayMetrics);
        this.mScreenHeight = localDisplayMetrics.heightPixels;
//        this.mHeaderContainer = new RelativeLayout(paramContext);
//        this.mHeaderContainer.setBackgroundResource(R.color.black);
//        this.mHeaderImage = new ImageView(paramContext);
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
//        LayoutInflater inflater = (LayoutInflater) paramContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        inflater.inflate(R.layout.image_list_view, this);
//        this.mHeaderContainer = (RelativeLayout) findViewById(R.id.headView);
//        this.mHeaderImage = (ImageView) findViewById(R.id.backgroundImageView);

//        int i = localDisplayMetrics.widthPixels;
//        setHeaderViewSize(i, (int) (9.0F * (i / 16.0F)));
//        this.mShadow = new ImageView(paramContext);
//        FrameLayout.LayoutParams localLayoutParams = new FrameLayout.LayoutParams(
//                -1, -2);
//        localLayoutParams.gravity = 80;
//        this.mShadow.setLayoutParams(localLayoutParams);
//        this.mHeaderContainer.addView(this.mHeaderImage,lp);
//        this.mHeaderContainer.addView(this.mShadow);
//        addHeaderView(this.mHeaderContainer);
        this.mScalingRunnalable = new ScalingRunnalable();
        super.setOnScrollListener(this);
    }

    private void onSecondaryPointerUp(MotionEvent paramMotionEvent) {
        int i = (paramMotionEvent.getAction()) >> 8;
        if (paramMotionEvent.getPointerId(i) == this.mActivePointerId)
            if (i != 0) {
                int j = 1;
                this.mLastMotionY = paramMotionEvent.getY(0);
                this.mActivePointerId = paramMotionEvent.getPointerId(0);
                return;
            }
    }

    private void reset() {
        this.mActivePointerId = -1;
        this.mLastMotionY = -1.0F;
        this.mMaxScale = -1.0F;
        this.mLastScale = -1.0F;
    }

    @Override
    public void addHeaderView(View v) {
        super.addHeaderView(v);
        this.mHeaderContainer = (RelativeLayout) v;
        this.mHeaderContainer.setBackgroundResource(R.color.black);
        this.mHeaderImage = (ImageView) v.findViewById(R.id.backgroundImageView);
    }

    public ImageView getHeaderView() {
        return this.mHeaderImage;
    }

    public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent) {
        return super.onInterceptTouchEvent(paramMotionEvent);
    }

    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2,
                            int paramInt3, int paramInt4) {
        super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
        if (this.mHeaderHeight == 0)
            this.mHeaderHeight = this.mHeaderContainer.getHeight();
        Log.i("etong","mHeaderHeight: "+mHeaderHeight);

    }

    @Override
    public void onScroll(AbsListView paramAbsListView, int paramInt1,
                         int paramInt2, int paramInt3) {
        Log.d("mmm", "onScroll");
        if(this.mHeaderContainer!= null) {
            float f = this.mHeaderHeight - this.mHeaderContainer.getBottom();
            Log.d("mmm", "f|" + f);
            if ((f > 0.0F) && (f < this.mHeaderHeight)) {
                Log.d("mmm", "1");
                int i = (int) (0.5D * f);
                this.mHeaderContainer.scrollTo(0, -i);
            } else if (this.mHeaderImage.getScrollY() != 0) {
                Log.d("mmm", "2");
                this.mHeaderImage.scrollTo(0, 0);
            }
            if (this.mOnScrollListener != null) {
                this.mOnScrollListener.onScroll(paramAbsListView, paramInt1,
                        paramInt2, paramInt3);
            }
        }
    }

    public void onScrollStateChanged(AbsListView paramAbsListView, int paramInt) {
        if (this.mOnScrollListener != null)
            this.mOnScrollListener.onScrollStateChanged(paramAbsListView,
                    paramInt);
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent) {
        Log.d("mmm", "" + (0xFF & paramMotionEvent.getAction()));
        switch (0xFF & paramMotionEvent.getAction()) {
            case 4:
            case 0:
                if (!this.mScalingRunnalable.mIsFinished) {
                    this.mScalingRunnalable.abortAnimation();
                }
                this.mLastMotionY = paramMotionEvent.getY();
                this.mActivePointerId = paramMotionEvent.getPointerId(0);
                this.mMaxScale = (this.mScreenHeight / this.mHeaderHeight);
                this.mLastScale = (this.mHeaderContainer.getBottom() / this.mHeaderHeight);
                break;
            case 2:
                Log.d("mmm", "mActivePointerId" + mActivePointerId);
                int j = paramMotionEvent.findPointerIndex(this.mActivePointerId);
                if (j == -1) {
                    Log.e("PullToZoomListView", "Invalid pointerId="
                            + this.mActivePointerId + " in onTouchEvent");
                } else {
                    if (this.mLastMotionY == -1.0F)
                        this.mLastMotionY = paramMotionEvent.getY(j);
//                    if (this.mHeaderContainer.getBottom() >= this.mHeaderHeight) {
//                        ViewGroup.LayoutParams localLayoutParams = this.mHeaderContainer
//                                .getLayoutParams();
//                        float f = ((paramMotionEvent.getY(j) - this.mLastMotionY + this.mHeaderContainer
//                                .getBottom()) / this.mHeaderHeight - this.mLastScale)
//                                / 2.0F + this.mLastScale;
//                        if ((this.mLastScale <= 1.0D) && (f < this.mLastScale)) {
//                            localLayoutParams.height = this.mHeaderHeight;
//                            this.mHeaderContainer
//                                    .setLayoutParams(localLayoutParams);
//                            return super.onTouchEvent(paramMotionEvent);
//                        }
//                        this.mLastScale = Math.min(Math.max(f, 1.0F),
//                                this.mMaxScale);
//                        localLayoutParams.height = ((int) (this.mHeaderHeight * this.mLastScale));
//                        if (localLayoutParams.height < this.mScreenHeight)
//                            this.mHeaderContainer
//                                    .setLayoutParams(localLayoutParams);
//                        this.mLastMotionY = paramMotionEvent.getY(j);
//                        return true;
//                    }
                    if(this.mHeaderContainer.getBottom() >= this.mHeaderHeight){
                        ViewGroup.LayoutParams localLayoutParams = this.mHeaderContainer
                                .getLayoutParams();
                        Log.i("etong","height: "+localLayoutParams.height);
                        localLayoutParams.height = mHeaderContainer.getHeight()+(int)(paramMotionEvent.getY()-mLastMotionY);

                        if (localLayoutParams.height < 600)
                            this.mHeaderContainer
                                    .setLayoutParams(localLayoutParams);
                        this.mLastMotionY = paramMotionEvent.getY(j);
                        return true;
                    }
                    this.mLastMotionY = paramMotionEvent.getY(j);
                }
                break;
            case 1:
                reset();
                endScraling();
                break;
            case 3:
                int i = paramMotionEvent.getActionIndex();
                this.mLastMotionY = paramMotionEvent.getY(i);
                this.mActivePointerId = paramMotionEvent.getPointerId(i);
                break;
            case 5:
                onSecondaryPointerUp(paramMotionEvent);
                this.mLastMotionY = paramMotionEvent.getY(paramMotionEvent
                        .findPointerIndex(this.mActivePointerId));
                break;
            case 6:
        }
        return super.onTouchEvent(paramMotionEvent);
    }

    public void setHeaderViewSize(int paramInt1, int paramInt2) {
        Object localObject = this.mHeaderContainer.getLayoutParams();
        if (localObject == null)
            localObject = new AbsListView.LayoutParams(paramInt1, paramInt2);
        ((ViewGroup.LayoutParams) localObject).width = paramInt1;
        ((ViewGroup.LayoutParams) localObject).height = paramInt2;
        this.mHeaderContainer
                .setLayoutParams((ViewGroup.LayoutParams) localObject);
        this.mHeaderHeight = paramInt2;
    }

    public void setOnScrollListener(
            AbsListView.OnScrollListener paramOnScrollListener) {
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
                this.mIsFinished = true;
            }
        }

        public void startAnimation(long paramLong) {
            this.mStartTime = SystemClock.currentThreadTimeMillis();
            this.mDuration = paramLong;
            this.mScale = ((float) (LollipopView.this.mHeaderContainer
                    .getBottom()) / LollipopView.this.mHeaderHeight);
            this.mIsFinished = false;
            LollipopView.this.post(this);
        }
    }
}

