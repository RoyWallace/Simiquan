package etong.simiquan;

import android.view.View;

/**
 * Created by Administrator on 2014/9/3.
 */
public class ViewWrapper {

    private View view;

    public ViewWrapper(View view) {
        this.view = view;
    }

    public int getHeight() {
        return view.getLayoutParams().height;
    }

    public void setHeight(int height) {
        view.getLayoutParams().height = height;
        view.requestLayout();
    }

    public int getWidth() {
        return view.getLayoutParams().width;
    }

    public void setWidth(int width) {
        view.getLayoutParams().width = width;
        view.requestLayout();
    }
}
