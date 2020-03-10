package com.snake;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/*
 * ���Ǵ�Ҷ�֪��Androidƽ̨�ṩ������һ�����ݿ⸨����������������ݿ⣬���������̳���SQLiteOpenHelper�࣬
 * �ڸ���Ĺ������У�����Context�еķ�����������һ��ָ�����Ƶ����ݿ����
 * �̳к���չSQLiteOpenHelper����Ҫ���Ĺ���������д�������������� 
       onCreate(SQLiteDatabase db) : �����ݿⱻ�״δ���ʱִ�и÷�����һ�㽫������ȳ�ʼ�������ڸ÷�����ִ�С� 
       onUpgrade(SQLiteDatabse db, int oldVersion,int new Version)���������ݿ�ʱ����İ汾���뵱ǰ�İ汾�Ų�ͬʱ����ø÷����� 
 * ����������������Ҫʵ�ֵķ����⣬������ѡ���Ե�ʵ��onOpen�������÷�������ÿ�δ����ݿ�ʱ�����á� 
  
 * SQLiteOpenHelper ��Ļ����÷��ǣ�
 * ����Ҫ�������һ�����ݿⲢ������ݿ����ʱ�����ȸ���ָ�����ļ�������һ����������
 * Ȼ����øö����getWritableDatabase �� getReadableDatabase���� ���SQLiteDatabase ���� 
  
 * ����getReadableDatabase �������صĲ�������ֻ�����ݿ����
 * һ����˵�÷�����getWriteableDatabase �����ķ��������ͬ��ֻ�������ݿ������ֻ��Ȩ�޻��������ʱ�Ż᷵��һ��ֻ�������ݿ���� 
 */
public class SQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_NAME = "Score";
	
	public SQLiteHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// �����ݿ��״δ���ʱִ�и÷�����һ�㽫������ȳ�ʼ���������ڸ÷�����ִ��. 
		// ��дonCreate����������execSQL����������  
		arg0.execSQL("create table if not exists "+TABLE_NAME+"(id integer primary key autoincrement, name varchar , score integer , time integer)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		//�������ݿ�ʱ����İ汾���뵱ǰ�İ汾�Ų�ͬʱ����ø÷���
	}

}
