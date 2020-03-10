package com.snake;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/*
 * 我们大家都知道Android平台提供给我们一个数据库辅助类来创建或打开数据库，这个辅助类继承自SQLiteOpenHelper类，
 * 在该类的构造器中，调用Context中的方法创建并打开一个指定名称的数据库对象。
 * 继承和扩展SQLiteOpenHelper类主要做的工作就是重写以下两个方法。 
       onCreate(SQLiteDatabase db) : 当数据库被首次创建时执行该方法，一般将创建表等初始化操作在该方法中执行。 
       onUpgrade(SQLiteDatabse db, int oldVersion,int new Version)：当打开数据库时传入的版本号与当前的版本号不同时会调用该方法。 
 * 除了上述两个必须要实现的方法外，还可以选择性地实现onOpen方法，该方法会在每次打开数据库时被调用。 
  
 * SQLiteOpenHelper 类的基本用法是：
 * 当需要创建或打开一个数据库并获得数据库对象时，首先根据指定的文件名创建一个辅助对象，
 * 然后调用该对象的getWritableDatabase 或 getReadableDatabase方法 获得SQLiteDatabase 对象。 
  
 * 调用getReadableDatabase 方法返回的并不总是只读数据库对象，
 * 一般来说该方法和getWriteableDatabase 方法的返回情况相同，只有在数据库仅开放只读权限或磁盘已满时才会返回一个只读的数据库对象。 
 */
public class SQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_NAME = "Score";
	
	public SQLiteHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// 当数据库首次创建时执行该方法，一般将创建表等初始化操作放在该方法中执行. 
		// 重写onCreate方法，调用execSQL方法创建表  
		arg0.execSQL("create table if not exists "+TABLE_NAME+"(id integer primary key autoincrement, name varchar , score integer , time integer)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		//当打开数据库时传入的版本号与当前的版本号不同时会调用该方法
	}

}
