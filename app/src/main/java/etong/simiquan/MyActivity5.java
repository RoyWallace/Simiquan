package etong.simiquan;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

public class MyActivity5 extends Activity {

    private View contentView;

    private ImageView titleView;

    private RelativeLayout headView;

    private ImageView backgroundView;

    private TextView textView;

    private EditText editText;

    private PullToZoomListView listView;

    private float density;
    int offsetY;
    int offsetHeight;

    int imageId;

    private String animType;

    private List<String> strs = new ArrayList<String>();

    private List<Animator> startAnims = new ArrayList<Animator>();

    private List<Animator> endAnims = new ArrayList<Animator>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();

        findAllView();

        madeData();

        doStartAnim();

        setAllListener();

    }

    public void init() {
        density = getResources().getDisplayMetrics().density;
        Intent intent = getIntent();
        offsetHeight = intent.getIntExtra("offsetHeight", 0);
        offsetY = intent.getIntExtra("offsetY", 0);
        animType = intent.getStringExtra("animType");
        imageId = intent.getIntExtra("imageId", R.drawable.tu2);
    }

    public void findAllView() {

        LayoutInflater inflater = LayoutInflater.from(this);
        contentView = inflater.inflate(R.layout.my5_activity, null);
        setContentView(contentView);

        //采用头布局和列表布局分离的写法，方便头布局直接在activity里直接获得和修改。
        //
        int padding = getResources().getDimensionPixelOffset(R.dimen.normal_padding);
        titleView = (ImageView) findViewById(R.id.titleView);

        //列表头布局
        headView = (RelativeLayout) inflater.inflate(R.layout.head_view, null);
        //头布局图片控件
        backgroundView = (ImageView) headView.findViewById(R.id.backgroundImageView);
        //头布局文字显示控件
        textView = (TextView) headView.findViewById(R.id.textView);

        listView = (PullToZoomListView) findViewById(R.id.listView);
        //初始化头布局,设置headview高度，TitleView高度
        listView.setHeadView(headView, (int) (300 * density) + padding, (int) (50 * density));

        editText = (EditText) findViewById(R.id.editText);

    }

    public void madeData() {

        strs.add("ABCDEFGHIJKLMN");
        strs.add("ABCDEFGHIJKLMN");
        strs.add("ABCDEFGHIJKLMN");
        strs.add("ABCDEFGHIJKLMN");
        strs.add("ABCDEFGHIJKLMN");
        strs.add("ABCDEFGHIJKLMN");
        strs.add("ABCDEFGHIJKLMN");
        strs.add("ABCDEFGHIJKLMN");
        strs.add("ABCDEFGHIJKLMN");
        strs.add("ABCDEFGHIJKLMN");
        strs.add("ABCDEFGHIJKLMN");


        //设置头布局背景图片
        backgroundView.setImageResource(imageId);
        //设置标题栏背景图片
        titleView.setImageResource(imageId);

        CommentAdapter commentAdapter = new CommentAdapter(this, strs);
        listView.setAdapter(commentAdapter);

    }

    public void setAllListener() {

        //titleView 显示监听
        listView.setOnShowTitleViewListener(new PullToZoomListView.OnShowTitleViewListener() {
            @Override
            public void onShow() {
                if(titleView.getVisibility()==View.GONE)
                    titleView.setVisibility(View.VISIBLE);
            }
            @Override
            public void onHide() {
                if(titleView.getVisibility()==View.VISIBLE)
                    titleView.setVisibility(View.GONE);
            }
        });

        //titleView点击事件
        titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.smoothScrollToPosition(0);
            }
        });

        //headView点击事件处理
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("etong", "itemClick");
                if (listView.isClickable() && i == 0) {
                    Log.i("etong", "click  " + i);
                }
            }
        });

        //改变文字颜色回调
        listView.setChangeTextAlpha(new PullToZoomListView.ChangeTextAlpha() {
            @Override
            public void onChange(float a) {
                textView.setTextColor(Color.argb((int) (a * 255), 255, 255, 255));
            }
        });

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

    public int getContentViewHeight() {
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        int screenHeight = wm.getDefaultDisplay().getHeight();
        return screenHeight - getStatuBarHeight();
    }

    private void doStartAnim() {
        ObjectAnimator translateAnim = ObjectAnimator.ofFloat(contentView,
                "translationY", offsetY - getStatuBarHeight(), 0);

        ViewWrapper viewWrapper = new ViewWrapper(contentView);
        ObjectAnimator commentAnim = ObjectAnimator.ofInt(viewWrapper,
                "height", offsetHeight, getContentViewHeight());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translateAnim, commentAnim);
        animatorSet.setDuration(200);
        animatorSet.start();

    }

    private void doEndAnim() {
        listView.smoothScrollToPosition(0);

        ObjectAnimator translateAnim = ObjectAnimator.ofFloat(contentView,
                "translationY", 0, offsetY - getStatuBarHeight());
        translateAnim.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator arg0) {
                editText.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animator arg0) {

            }

            @Override
            public void onAnimationEnd(Animator arg0) {
                contentView.setVisibility(View.INVISIBLE);
                finish();
            }

            @Override
            public void onAnimationCancel(Animator arg0) {

            }
        });

        ViewWrapper viewWrapper = new ViewWrapper(contentView);
        ObjectAnimator commentAnim = ObjectAnimator.ofInt(viewWrapper, "height", contentView.getHeight(), offsetHeight);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translateAnim, commentAnim);
        animatorSet.setDuration(200);
        animatorSet.start();

    }

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
    }

    @Override
    public void onBackPressed() {
        doEndAnim();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
