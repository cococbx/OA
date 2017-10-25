package com.wanhuiyuan.szoa.gridview;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.cec.szoa.R;
import com.wanhuiyuan.szoa.adapter.RenyuanGridViewAdapter;
import com.wanhuiyuan.szoa.adapter.ReplaceUserGridViewAdapter;
import com.wanhuiyuan.szoa.bean.Login;
import com.wanhuiyuan.szoa.bean.Meeting;

public class ReplaceUserListViewAdapter extends BaseExpandableListAdapter
		implements OnItemClickListener {

	public static final int ItemHeight = 48;// ÿ��ĸ߶�
	public static final int PaddingLeft = 36;// ÿ��ĸ߶�
	private int myPaddingLeft = 0;

	private MyGridView toolbarGrid;

	private List<TreeNode> treeNodes = new ArrayList<TreeNode>();

	private Activity parentContext;

	private LayoutInflater layoutInflater;
	
	private Meeting mMeeting;

	static public class TreeNode {
		public Object parent;
		public List<Login> childs = new ArrayList<Login>();
	}

	public ReplaceUserListViewAdapter(Activity view, int myPaddingLeft, Meeting meeting) {
		parentContext = view;
		this.myPaddingLeft = myPaddingLeft;
		this.mMeeting = meeting;
	}

	public List<TreeNode> GetTreeNode() {
		return treeNodes;
	}

	public void UpdateTreeNode(List<TreeNode> nodes) {
		treeNodes = nodes;
	}

	public void RemoveAll() {
		treeNodes.clear();
	}

	public Login getChild(int groupPosition, int childPosition) {
		return treeNodes.get(groupPosition).childs.get(childPosition);
	}

	public int getChildrenCount(int groupPosition) {
		int size = 1;
//		System.out.println(treeNodes.get(groupPosition).childs.size());
//		if (treeNodes.get(groupPosition).childs.size() % 2 == 0)
//			size = treeNodes.get(groupPosition).childs.size() / 2;
//		else
//			size = treeNodes.get(groupPosition).childs.size() / 2 + 1;
//		size = treeNodes.size();
//		if(treeNodes.size()>1)
//			size=1;
		return size;
	}

	static public TextView getTextView(Context context) {
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, ItemHeight);
		TextView textView = new TextView(context);
		textView.setLayoutParams(lp);
		textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);

		return textView;
	}

	/**
	 * ���Զ���ExpandableListView
	 */
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
//		if (convertView == null) {
			layoutInflater = (LayoutInflater) parentContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.view, null);
			toolbarGrid = (MyGridView) convertView
					.findViewById(R.id.GridView_toolbar);
			toolbarGrid.setNumColumns(4);// ����ÿ������
			toolbarGrid.setGravity(Gravity.CENTER);// λ�þ���
			toolbarGrid.setHorizontalSpacing(10);// ˮƽ���
			toolbarGrid
					.setAdapter(getMenuAdapter(treeNodes.get(groupPosition).childs));// ���ò˵�Adapter
			toolbarGrid.setOnItemClickListener(this);
//		}
		return convertView;
	}

	/**
	 * ���Զ���list
	 */
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		TextView textView = getTextView(this.parentContext);
		textView.setText(getGroup(groupPosition).toString());
		textView.setPadding(myPaddingLeft + PaddingLeft, 10, 10, 10);
		textView.setBackgroundColor(parentContext.getResources().getColor(
				R.color.renyuan_group_bg));
		LayoutParams p = (LayoutParams) textView.getLayoutParams();
		p.height = 100;
		textView.setLayoutParams(p);
		textView.setTextSize(22);
		textView.setTextColor(parentContext.getResources().getColor(
				R.color.black));
		return textView;
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public Object getGroup(int groupPosition) {
		return treeNodes.get(groupPosition).parent;
	}

	public int getGroupCount() {
		return treeNodes.size();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public boolean hasStableIds() {
		return true;
	}

	/**
	 * ����˵�Adapter
	 * 
	 * @param menuNameArray
	 *            ����
	 * @param imageResourceArray
	 *            ͼƬ
	 * @return SimpleAdapter
	 */
	private ReplaceUserGridViewAdapter getMenuAdapter(List<Login> childs) {
		ReplaceUserGridViewAdapter gvAapter = new ReplaceUserGridViewAdapter(
				parentContext, childs, mMeeting);
		return gvAapter;
	}

	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
//		Intent intent = new Intent();
//		Bundle bundle = new Bundle();
//		bundle.putSerializable("user", childs.get(position));
//		intent.putExtras(bundle);
//		setResult(11, intent);
//		finish();
//		Toast.makeText(parentContext,id+ "当前选中的是:" + position, Toast.LENGTH_SHORT)
//		.show();
	}
}