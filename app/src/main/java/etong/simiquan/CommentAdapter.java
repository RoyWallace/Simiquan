package etong.simiquan;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CommentAdapter extends BaseAdapter {

	private Context context;

	private List<String> strs = new ArrayList<String>();

	public CommentAdapter(Context context, List<String> strs) {
		this.context = context;
		this.strs = strs;
	}

	@Override
	public int getCount() {
		return strs.size();
	}

	@Override
	public Object getItem(int position) {
		return 0;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {

		if (view == null) {
			view = ((Activity) context).getLayoutInflater().inflate(R.layout.comment_item, null);
		}
		TextView textView = (TextView) view;
		textView.setText(strs.get(position));

		return view;
	}

}
