package com.wanhuiyuan.szoa.adapter;

import java.util.List;

import com.cec.szoa.R;
import com.wanhuiyuan.szoa.bean.Login;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LoginAdapter extends BaseAdapter {
	List<Login> loginList;
	Context context;
	
	private static class ViewHolder {
		TextView nameTxt;
		TextView deptTxt;
	}
	
	public LoginAdapter(List<Login> loginList, Context context){
		this.context = context;
		this.loginList = loginList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return loginList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder;

        if (convertView == null) {
        	viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_login, parent, false);
            viewHolder.nameTxt = (TextView) convertView.findViewById(R.id.person_name);
            viewHolder.deptTxt = (TextView) convertView.findViewById(R.id.dept_name);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        
        Login login = loginList.get(position);
        
        viewHolder.nameTxt.setText(login.getTruename());
        viewHolder.deptTxt.setText("(" + login.getDeptName() + ")");
        
        return convertView;
	}

}
