package com.wanhuiyuan.szoa.adapter;

import java.util.List;
import java.util.ListIterator;

import net.tsz.afinal.FinalDb;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wanhuiyuan.down.DownloadService2;
import com.wanhuiyuan.down.ThreadInfo;
import com.wanhuiyuan.szoa.MyApplication;
import com.cec.szoa.R;
import com.wanhuiyuan.szoa.bean.MeetingYiti;
import com.wanhuiyuan.szoa.bean.YitiFile;
import com.wanhuiyuan.szoa.myview.MyProgressBar;
import com.wanhuiyuan.szoa.myview.MyToast;
import com.wanhuiyuan.szoa.service.DownloadService;
import com.wanhuiyuan.szoa.uiutil.MyFileUtil;
import com.wanhuiyuan.szoa.uiutil.NetworkStatusHandler;

public class YItiExpandableListAdapter extends BaseExpandableListAdapter {
	private Context mContext;
	private List<MeetingYiti> yitiGroup;
	private LayoutInflater inflater;
	FinalDb db;
	List<YitiFile> files;
	public static Handler handler;
	long clickTime = 0;// 记录点击时间，防止连续点击

	String SDCARD_PATH = Environment.getExternalStorageDirectory().getPath()
			.toString();

	public YItiExpandableListAdapter(Context context,
			List<MeetingYiti> yitiGroup) {
		mContext = context;
		this.yitiGroup = yitiGroup;
		inflater = LayoutInflater.from(context);
		db = FinalDb.create(context, "szoa.db");
		// files = db.findAllByWhere(YitiFile.class, " 1=1");
	}

	public void bindData(List<MeetingYiti> yitiGroup) {
		// files = db.findAllByWhere(YitiFile.class, " 1=1");
		this.yitiGroup = yitiGroup;
		this.notifyDataSetChanged();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return yitiGroup.get(groupPosition).getFiles().get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		final View view;
		// if (convertView == null) {
		view = inflater.inflate(R.layout.yiti_child_item, parent, false);
		// } else {
		// view = convertView;
		// }
		MyProgressBar progressBar = (MyProgressBar) view
				.findViewById(R.id.progressBar1);
		LinearLayout progressLayout = (LinearLayout) view
				.findViewById(R.id.progressLayout);
		String fileId = yitiGroup.get(groupPosition).getFiles()
				.get(childPosition).getId();
		ThreadInfo threadInfo = db.findById(fileId, ThreadInfo.class);
		if (threadInfo != null) {
			progressBar.setProgress(threadInfo.getProgress());
		}

		TextView textview = (TextView) view.findViewById(R.id.file_name);
		textview.setText(yitiGroup.get(groupPosition).getFiles()
				.get(childPosition).getFileName());
		files = db.findAllByWhere(
				YitiFile.class,
				" id='"
						+ yitiGroup.get(groupPosition).getFiles()
								.get(childPosition).getId() + "'");

		// 长按文件名进行重新下载 11.17 tom
		textview.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				// if (files.size()>0&& files.get(0).getIsDown() == 1) {

				// 如果是在联网下可以进行下载，否则提示网络不通不能下载，
				if (NetworkStatusHandler.isNetWorkAvaliable(mContext)) {

					// daming 修改 2016-11-26，判断文件当前状态，如果是完成则下载，否则提示正在下载
					List<YitiFile> dbfiles = db.findAllByWhere(YitiFile.class,
							"id='"
									+ yitiGroup.get(groupPosition).getFiles()
											.get(childPosition).getId()
									+ "' and isDown=1 ");
					// 如果文件已经下载完毕则重新下载，否则不可以重新下载
					if (dbfiles.size() != 0) {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								mContext);
						builder.setCancelable(false);
						builder.setTitle("提示"); // 设置标题
						builder.setMessage("是否重新下载！"); // 设置内容
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() { // 设置确定按钮
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										//
										// yitiGroup.get(groupPosition).getFiles()
										// .get(childPosition).setIsDown(0);
										// MyApplication.downedFilesNum-=1;
										// try {
										// db.save(yitiGroup.get(groupPosition)
										// .getFiles().get(childPosition));//
										// 保存到数据库
										// } catch (Exception e) {
										// db.update(yitiGroup.get(groupPosition)
										// .getFiles().get(childPosition));
										// }
										//
										// view.findViewById(R.id.progressLayout)
										// .setVisibility(View.VISIBLE);
										//
										// Intent msgIntent = new
										// Intent(mContext,
										// DownloadService.class);
										// msgIntent.putExtra("Command",
										// "REDOWNLOAD");
										// mContext.startService(msgIntent);
										// MyToast.show(mContext, "文件重新下载中...");

										// daming 11.26
										yitiGroup.get(groupPosition).getFiles()
												.get(childPosition)
												.setIsDown(0);
										try {
											db.save(yitiGroup
													.get(groupPosition)
													.getFiles()
													.get(childPosition));// 保存到数据库
										} catch (Exception e) {
											db.update(yitiGroup
													.get(groupPosition)
													.getFiles()
													.get(childPosition));
										}

										view.findViewById(R.id.progressLayout)
												.setVisibility(View.VISIBLE);

										Intent intent = new Intent(mContext, DownloadService2.class);
							            intent.setAction(DownloadService2.ACTION_START);
							            intent.putExtra("fileinfo", yitiGroup
												.get(groupPosition)
												.getFiles()
												.get(childPosition));
							            mContext.startService(intent);
										MyToast.show(mContext, "文件重新下载中...");
									}
								});
						builder.setNegativeButton(R.string.cancel,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {
										arg0.cancel();
									}
								});

						builder.create().show();

					} else {
						MyToast.show(mContext, "文件正在下载，无法重新下载。");
					}
				} else {
					MyToast.show(mContext, "网络不通，不能下载。");
				}
				return true;
			}
		});
		textview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				List<YitiFile> dbfiles = db.findAllByWhere(
						YitiFile.class,
						"id='"
								+ yitiGroup.get(groupPosition).getFiles()
										.get(childPosition).getId()
								+ "' and isDown=1 ");
				// 判断文件是否下载完成
				if (dbfiles.size() != 0) {
					if ((System.currentTimeMillis() - clickTime) > 3000) {
						clickTime = System.currentTimeMillis();
						MyFileUtil.doOpenFile(mContext, yitiGroup
								.get(groupPosition).getFiles().get(childPosition),
								yitiGroup.get(groupPosition).getMeetingId());
					}
				} else {
					MyToast.show(mContext, "文件尚未下载完成");
				}
			}

		});

		// boolean isdown = false;
		// final Handler handler;
		if (files.size() > 0) {
			for (int i = 0; i < files.size(); i++) {
				if (yitiGroup.get(groupPosition).getFiles().get(childPosition)
						.getVersion().equals(files.get(i).getVersion())
						&& files.get(i).getIsDown() == 1) {
					// isdown = true;
					view.findViewById(R.id.progressLayout).setVisibility(
							View.GONE);
					break;
				} else if (yitiGroup.get(groupPosition).getFiles()
						.get(childPosition).getId()
						.equals(files.get(i).getId())
						&& files.get(i).getIsDown() == 3) {
					// yitiGroup.get(groupPosition).getFiles().remove(childPosition);
				} else {
					view.findViewById(R.id.progressLayout).setVisibility(
							View.VISIBLE);
					// db.deleteById(YitiFile.class, files.get(i).getId());
				}
			}
		}
		// if (!isdown) {
		//
		// handler = new Handler() {
		// public void handleMessage(Message msg) {
		//
		// switch (msg.what) {
		// case 0:
		// if (yitiGroup.get(groupPosition).getFiles()
		// .get(childPosition).getId()
		// .equals(msg.getData().get("fileId"))) {
		// progressLayout.setVisibility(View.VISIBLE);
		// }
		// break;
		// case 1:
		// if (yitiGroup.get(groupPosition).getFiles()
		// .get(childPosition).getId()
		// .equals(msg.getData().get("fileId"))) {
		// progressBar
		// .setProgress(msg.getData().getInt("now"));
		// }
		// break;
		// case 2:
		// if (yitiGroup.get(groupPosition).getFiles()
		// .get(childPosition).getId()
		// .equals(msg.getData().get("fileId"))) {
		// progressLayout.setVisibility(View.GONE);
		// }
		// break;
		//
		// case 3:
		// if (yitiGroup.get(groupPosition).getFiles()
		// .get(childPosition).getId()
		// .equals(msg.getData().get("fileId"))) {
		// }
		// break;
		//
		// default:
		// break;
		// }
		//
		// }
		//
		// };
		// Message message3 = new Message();
		// message3.what = 3;
		// handler.sendMessage(message3);
		// }
		// if (yitiGroup.get(groupPosition).getFiles().get(childPosition)
		// .getIsDown() == 3)
		// view.setVisibility(View.GONE);
		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return yitiGroup.get(groupPosition).getFiles().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return yitiGroup.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return yitiGroup.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		convertView = inflater.inflate(R.layout.yiti_group_item, parent, false);
		TextView view = (TextView) convertView.findViewById(R.id.yiti_name);
		view.setText(yitiGroup.get(groupPosition).getName());

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

}
