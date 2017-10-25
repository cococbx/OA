package com.wanhuiyuan.down;

import java.util.List;

import net.tsz.afinal.FinalDb;
import android.content.Context;

public class ThreadDAOImpl2 implements ThreadDAO2 {
	FinalDb db;
    private static final String TAG = "ThreadDAOImpl";


    public ThreadDAOImpl2(Context context) {
    	db = FinalDb.create(context, "szoa.db");
    }

    @Override
    public synchronized void insertThread(ThreadInfo threadInfo) {
    	try {
    		db.save(threadInfo);
		} catch (Exception e) {
			db.update(threadInfo);
		}
    	
    }

    @Override
    public synchronized void deleteThread(String id) {
    	db.deleteById(ThreadInfo.class, id);
    }

    @Override
    public synchronized void updateThread(ThreadInfo threadInfo) {
    	db.update(threadInfo);
    }

    @Override
    public List<ThreadInfo> getThread(String id) {
    	
        return db.findAllByWhere(ThreadInfo.class, "id='"+id+"'");
    }

    @Override
    public boolean isExists(String url, int thread_id) {
    	List<ThreadInfo> lists = db.findAllByWhere(ThreadInfo.class, "url="+url+" and id="+thread_id);
       if(lists.size()>0){
    	   return true;
       }else{
    	   return false;
       }
    }
}
