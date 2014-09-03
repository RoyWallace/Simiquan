package etong.simiquan;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

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
    private RelativeLayout mHeadView;
    private int mHeaderHeight;
    private int mImageRes;
    float mLastMotionY = -1.0F;
    float mLastScale = -1.0F;
    float mMaxScale = -1.0F;
    private OnScrollListener mOnScrollListener;
    private ScalingRunnalable mScalingRunnalable;
    private int mScreenHeight;
    private int mScreenWidth;
    private ImageView mShadow;
    private int mTitleViewHeight;
    private boolean clickable=true;

    public boolean isClickable(){
        return clickable;
    }


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

    /**
     * 设置头布局，背景图片布局，文字布局，标题栏布局，头布局高度，标题栏布局高度
     * @param headView
     * @param height
     * @param titleViewHeight
     */
    public void setHeadView(RelativeLayout headView,int height,int titleViewHeight){
        this.mHeadView = headView;
        this.mHeaderHeight = height;
        this.mTitleViewHeight = titleViewHeight;
        setHeaderViewSize(height);
        addHeaderView(headView);
    }

    public RelativeLayout getHeadView(){
        return mHeadView;
    }

    private void endScraling() {
        if (mHeadView.getBottom() >= mHeaderHeight)
        mScalingRunnalable.startAnimation(200L);
    }

    private void init(Context context) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(localDisplayMetrics);
        mScreenHeight = localDisplayMetrics.heightPixels;
        mScreenWidth = localDisplayMetrics.widthPixels;
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

    public boolean onInterceptTouchEvent(MotionEvent me) {
        return super.onInterceptTouchEvent(me);
    }

    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2,
                            int paramInt3, int paramInt4) {
        super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
        if (mHeaderHeight == 0) {
            mHeaderHeight = mHeadView.getHeight();
        }

    }

    @Override
    public void onScroll(AbsListView paramAbsListView, int paramInt1,
                         int paramInt2, int paramInt3) {
        if(mHeadView!= null) {
            if(mHeadView.getBottom()>=0) {
                float bh = (float) (mHeadView.getBottom() - mTitleViewHeight);
                float ht = (float) (mHeaderHeight - mTitleViewHeight);
                float sb = (float)(mScreenHeight-mHeadView.getBottom());
                float a = bh<=ht?bh/ht:sb/bh;
                mChangeTextAlpha.onChange(a);
            }
            if(mHeadView.getBottom()<=mTitleViewHeight){
                mShowListener.onShow();
            }else{
                mShowListener.onHide();
            }
            float f = mHeaderHeight - mHeadView.getBottom();
            if ((f > 0.0F) && (f < mHeaderHeight)) {
                int i = (int) (0.5D * f);
                mHeadView.scrollTo(0, -i);
            } else if (mHeadView.getScrollY() != 0) {
                mHeadView.scrollTo(0, 0);
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

    private void reset() {
        mActivePointerId = -1;
        mLastMotionY = -1.0F;
        mMaxScale = -1.0F;
        mLastScale = -1.0F;
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
                mLastScale = (float)mHeadView.getBottom() / (float)mHeaderHeight;
                break;
            case MotionEvent.ACTION_MOVE:
                int j = me.findPointerIndex(mActivePointerId);
                if (j == -1) {
                } else {
                    if (mLastMotionY == -1.0F)
                        mLastMotionY = me.getY(j);
                    if (mHeadView.getBottom() >= mHeaderHeight) {
                        ViewGroup.LayoutParams localLayoutParams = mHeadView
                                .getLayoutParams();
                        float f = ((me.getY(j) - mLastMotionY + mHeadView
                                .getBottom()) / mHeaderHeight - mLastScale)
                                / 2.0F + mLastScale;
                        if ((mLastScale <= 1.0D) && (f < mLastScale)) {
                            localLayoutParams.height = mHeaderHeight;
                            mHeadView
                                    .setLayoutParams(localLayoutParams);
                            return super.onTouchEvent(me);
                        }
                        mLastScale = Math.min(Math.max(f, 1.0F),
                                mMaxScale);
                        localLayoutParams.height = ((int) (mHeaderHeight * mLastScale));
                        if (localLayoutParams.height < mScreenHeight) {
                            mHeadView
                                    .setLayoutParams(localLayoutParams);
                        }
                        mLastMotionY = me.getY(j);
                        return true;
                    }
                    mLastMotionY = me.getY(j);
                }
                break;
            case MotionEvent.ACTION_UP:
                if(mHeadView.getBottom() > mHeaderHeight){
                    clickable = false;
                }else{
                    clickable = true;
                }
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

    public void setHeaderViewSize(int paramInt2) {
        ViewGroup.LayoutParams layoutParams = mHeadView.getLayoutParams();
        if (layoutParams == null)
            layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, paramInt2);
        mHeadView
                .setLayoutParams(layoutParams);
        mHeaderHeight = paramInt2;
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
                localLayoutParams = PullToZoomListView.this.mHeadView
                        .getLayoutParams();
                if (f2 > 1.0F) {
                    localLayoutParams.height = PullToZoomListView.this.mHeaderHeight;
                    localLayoutParams.height = ((int) (f2 * PullToZoomListView.this.mHeaderHeight));
                    PullToZoomListView.this.mHeadView
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
            mScale = ((float) (PullToZoomListView.this.mHeadView
                    .getBottom()) / PullToZoomListView.this.mHeaderHeight);
            mIsFinished = false;
            PullToZoomListView.this.post(this);
        }
    }

    private OnShowTitleViewListener mShowListener;

    public void setOnShowTitleViewListener(OnShowTitleViewListener showListener){
        this.mShowListener = showListener;
    }

    public interface OnShowTitleViewListener{
        public void onShow();
        public void onHide();
    }

    private ChangeTextAlpha mChangeTextAlpha;

    public void setChangeTextAlpha(ChangeTextAlpha c){
        this.mChangeTextAlpha = c;
    }

    public interface ChangeTextAlpha{
        public void onChange(float a);
    }
}
