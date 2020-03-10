package com.snake;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DalScore {
	SQLiteHelper myHelper;
	public static final String DB_NAME = "Snake.db";
	private SQLiteDatabase db;

	public DalScore(Context context) {
		// 创建MySQLiteOpenHelper辅助类对象
		myHelper = new SQLiteHelper(context, DB_NAME, null, 1);
		// 获取数据库对象
		db = myHelper.getWritableDatabase();
	}

	/**
	 * 向数据库中插入
	 * 
	 * @param helper
	 */
	public void insertData(Score s) {
		db = myHelper.getWritableDatabase();
		// 使用execSQL方法向表中插入数据
		String sql = "insert into Score( name, score, time) values('"
				+ s.getName() + "'," + s.getScore() + ","
				+ s.getTime() + ")";
		db.execSQL(sql);
		db.close();
	}

	public void updateData(Score s,int id) {
		db = myHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
//		values.put("id", s.getId());
		values.put("name", s.getName());
		values.put("score", s.getScore());
		values.put("time", s.getTime());
		db.update("Score", values, "id="+id, null);
		db.close();
	}

	public ArrayList<Score> queryData() {
		db = myHelper.getWritableDatabase();
		ArrayList<Score> scores = new ArrayList<Score>();
//		Cursor cursor = db.query("Score", new String[]{"name","score","time"}, "id="+s.getId(), null, null, null, null);
		Cursor cursor = db.query("Score", new String[]{"id","name","score","time"}, null, null, null, null, null);
		//记录数
//		int num = cursor.getColumnCount();
		
		int idIndex = cursor.getColumnIndex("id");
		int nameIndex = cursor.getColumnIndex("name");
		int scoreIndex = cursor.getColumnIndex("score");
		int timeIndex = cursor.getColumnIndex("time");
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			Score s = new Score();
			s.setId(cursor.getInt(idIndex));
			s.setName(cursor.getString(nameIndex));
			s.setScore(cursor.getInt(scoreIndex));
			s.setTime(cursor.getInt(timeIndex));
			scores.add(s);
			cursor.moveToNext();
		}
		cursor.close();
		db.close();
		return scores;
	}

	public void deleteData(Score s) {
		db = myHelper.getWritableDatabase();
		db.delete("Score", "id="+s.getId(), null);
		db.close();
	}
	
	public void insert(int id,Score s){
		ArrayList<Score> scores = queryData();
		db.close();
		for(int i = scores.size()-1 ; i > id ; i--){
			updateData(scores.get(i-1),scores.get(i).getId());
		}
		updateData(s, id);
	}

}
