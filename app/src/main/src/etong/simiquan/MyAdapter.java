package etong.simiquan;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * Created by Administrator on 2014/8/28.
 */
public class MyAdapter extends BaseAdapter {

    private Context context;
    
    private List<Integer> imageIdList = new ArrayList<Integer>();


    public MyAdapter(Context context,List<Integer> imageIdList){
        this.context = context;
        this.imageIdList = imageIdList;
    }

    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int i) {
        return 0;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();
        ViewHolder holder = null;

        if(view == null){
            holder = new ViewHolder();
            view = layoutInflater.inflate(R.layout.list_item,null);
            holder.imageView = (ImageView) view.findViewById(R.id.backgroundImageView);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        holder.imageView.setImageResource(imageIdList.get(i));
        return view;
    }

    class ViewHolder{
        ImageView imageView;

    }
}
