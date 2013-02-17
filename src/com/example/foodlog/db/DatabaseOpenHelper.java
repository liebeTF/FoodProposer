package com.example.foodlog.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * データベース処理クラス
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {

	// データベース名の定数
	private static final String DB_NAME = "FoodLogDB";
	private static final Integer VERSION = 1;
	/**
	 * コンストラクタ
	 */
	public DatabaseOpenHelper(Context context) {
		// 指定したデータベース名が存在しない場合は、新たに作成されonCreate()が呼ばれる
		// バージョンを変更するとonUpgrade()が呼ばれる
		super(context, DB_NAME, null, VERSION);
	}
	
	/**
	 * データベースの生成に呼び出されるので、 スキーマの生成を行う
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.beginTransaction();
		
		try{
			StringBuilder createSql = new StringBuilder();

			// MealRecordテーブルの生成
			createSql.append("create table " + MealRecord.TABLE_NAME + " (");
			createSql.append(MealRecord.COLUMN_ID + " integer primary key autoincrement not null,");
			createSql.append(MealRecord.COLUMN_FOODS + " text,");
			createSql.append(MealRecord.COLUMN_YEAR + " integer not null,");
			createSql.append(MealRecord.COLUMN_MONTH + " integer not null,");
			createSql.append(MealRecord.COLUMN_DAY + " integer not null,");
			createSql.append(MealRecord.COLUMN_HOUR + " integer not null,");
			createSql.append(MealRecord.COLUMN_NTH + " integer not null,");
			createSql.append(MealRecord.COLUMN_MINUTE + " integer not null,");
			createSql.append(MealRecord.COLUMN_MEALTIME + " integer,");
			createSql.append(MealRecord.COLUMN_SATIETY1 + " integer,");
			createSql.append(MealRecord.COLUMN_SATIETY2 + " integer,");
			createSql.append(MealRecord.COLUMN_MEMBER + " integer,");
			createSql.append(MealRecord.COLUMN_PROTEIN + " real,");
			createSql.append(MealRecord.COLUMN_CARBOHYDRATE + " real,");
			createSql.append(MealRecord.COLUMN_LIPID + " real,");
			createSql.append(MealRecord.COLUMN_ENERGY + " integer,");
			createSql.append("unique(" + MealRecord.COLUMN_YEAR  
					+ ", " + MealRecord.COLUMN_MONTH 
					+ ", " + MealRecord.COLUMN_DAY 
					+ ", " + MealRecord.COLUMN_NTH
					+")");
			createSql.append(")");
			db.execSQL( createSql.toString());
			

			// MealRecordテーブルの生成
			createSql = new StringBuilder();
			createSql.append("create table " + FoodData.TABLE_NAME + " (");
			createSql.append(FoodData.COLUMN_ID + " integer primary key autoincrement not null,");
			createSql.append(FoodData.COLUMN_NAME + " text not null,");
			createSql.append(FoodData.COLUMN_UNIT + " text default '"+FoodData.units.get(0)+"',");
			createSql.append(FoodData.COLUMN_KIND + " text dafault 'その他',");
			createSql.append(FoodData.COLUMN_SATISFACTION + " integer default 50,");
			createSql.append(FoodData.COLUMN_ATE_DATE + " integer not null,");
			createSql.append(FoodData.COLUMN_IMAGE + " blob,");
			createSql.append(MealRecord.COLUMN_PROTEIN + " real not null,");
			createSql.append(MealRecord.COLUMN_CARBOHYDRATE + " real not null,");
			createSql.append(MealRecord.COLUMN_LIPID + " real not null");
			createSql.append(")");
			db.execSQL( createSql.toString());
						


			db.setTransactionSuccessful();
		} catch(Exception ex){
			Log.d(ex.toString(), ex.getMessage());
		}finally {
			db.endTransaction();
		}
	}

	/**
	 * データベースの更新 親クラスのコンストラクタに渡すversionを変更したときに呼び出される
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 指定したテーブルのカラム構成をチェックし、
		// 同名のカラムについてはアップグレード後もデータを引き継ぎます。
		// 同名のカラムで型に互換性がない場合はエラーになるので注意。

		// 更新対象のテーブル
		List<String> targetTables = list(db);
		db.beginTransaction();
		try {
			for(String targetTable: targetTables){
			// 元カラム一覧
			final List<String> columns = getColumns(targetTable,null);
			// 初期化
			db.execSQL("ALTER TABLE " + targetTable + " RENAME TO temp_"
					+ targetTable);
			onCreate(db);
			// 新カラム一覧
			final List<String> newColumns = getColumns(targetTable,null);

			// 変化しないカラムのみ抽出
			columns.retainAll(newColumns);

			// 共通データを移す。(OLDにしか存在しないものは捨てられ, NEWにしか存在しないものはNULLになる)
			final String cols = join(columns, ",");
			db.execSQL(String.format(
					"INSERT INTO %s (%s) SELECT %s from temp_%s", targetTable,
					cols, cols, targetTable));
			// 終了処理
			db.execSQL("DROP TABLE temp_" + targetTable);
			}
			db.setTransactionSuccessful();
		} catch(SQLiteException ex){
			Log.d(ex.toString(), ex.getMessage());
		}
		finally {
			db.endTransaction();
		}
	}	
	/**
	 * 指定したテーブルのカラム名リストを取得する。
	 * @param db
	 * @param tableName
	 * @return カラム名のリスト
	 */
	public List<String> getColumns( String tableName) {
		String[] exception = new String[]{
				MealRecord.COLUMN_ID, 
				MealRecord.COLUMN_YEAR, 
				MealRecord.COLUMN_MONTH, 
				MealRecord.COLUMN_DAY,
				MealRecord.COLUMN_HOUR,
				MealRecord.COLUMN_MINUTE
				};
		return getColumns(tableName,exception);
	}
	public List<String> getColumns( String tableName,String[] exception) {
		SQLiteDatabase db = getReadableDatabase();
		List<String> ar = null;
		Cursor c = null;
		try {
			c = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 1", null);
			if (c != null) {
				ar = new ArrayList<String>(Arrays.asList(c.getColumnNames()));
				if(exception!=null){
					for (String s : exception) {
						ar.remove(s);
					}
				}
			}
		} catch(Exception ex){
			Log.d("Exception:", ex.getMessage());
		}finally {
			if (c != null)
				c.close();
		}
		return ar;
	}
	 
	/**
	 * 文字列を任意の区切り文字で連結する。
	 * @param list
	 * 文字列のリスト
	 * @param delim
	 * 区切り文字
	 * @return 連結後の文字列
	 */
	public static String join(List<String> list, String delim) {
		if(list==null)
			return null;
		final StringBuilder buf = new StringBuilder();
		final int num = list.size();
		for (int i = 0; i < num; i++) {
			if (i != 0)
				buf.append(delim);
			buf.append((String) list.get(i));
		}
		return buf.toString();
	}
	/**
	 * テーブル名一覧を取得する
	 * @return 検索結果
	 */
	public List<String> list(SQLiteDatabase db) {
		if(db==null){
			db = getReadableDatabase();
		}
		List<String> tableList;
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("SELECT * FROM sqlite_master WHERE type='table' ", null);
			Log.d("個数", String.valueOf(cursor.getCount()));
			tableList = new ArrayList<String>();
			cursor.moveToFirst();
			while( !cursor.isAfterLast()){
				String name = cursor.getString(cursor.getColumnIndex("name"));
				if(!name.equals("android_metadata") &&
						!name.equals("sqlite_sequence")){
					tableList.add( name);
				}
				cursor.moveToNext();
			}
		} finally {	
			if(cursor != null)
				cursor.close();
		}
		return tableList;
	}
}
