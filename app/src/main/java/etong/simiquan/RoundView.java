package etong.simiquan;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2014/9/2.
 */
public class RoundView extends RelativeLayout {

    private Paint paint;

    public RoundView(Context context) {
        super(context);
        init();
    }

    public RoundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paint=new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
