package etong.simiquan;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
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

    public LinearListView linearListView;

    public SmartisanView(Context context) {
        super(context);
    }

    public SmartisanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.image_list_view, this);
        relativeLayout = (RelativeLayout) findViewById(R.id.headView);
        imageView=(ImageView) findViewById(R.id.backgroundImageView);
        linearListView = (LinearListView) findViewById(R.id.listView);

    }

    public SmartisanView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


}
