package com.example.foodlog.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

/**
 * DailyRecord用データアクセスクラス
 */
public class FoodRecordDao {
	
	private DatabaseOpenHelper helper = null;
	private static SQLiteDatabase db;
	
	public FoodRecordDao(Context context) {
		helper = new DatabaseOpenHelper(context);
	}
	
	/**
	 * fooddataの保存
	 * rowidがnullの場合はinsert、rowidが!nullの場合はupdate
	 * @param FoodRecord 保存対象のオブジェクト
	 * @return 保存結果
	 */
	public FoodRecord save( FoodRecord data){
		db = helper.getWritableDatabase();
		FoodRecord result = null;
		try {
			ContentValues values = new ContentValues();
			values.put( FoodRecord.COLUMN_NAME, data.getName());
			values.put( FoodRecord.COLUMN_UNIT, data.getUnit());
			values.put( FoodRecord.COLUMN_KIND, data.getKind());
			values.put( FoodRecord.COLUMN_IMAGE, data.getImage());
			values.put( FoodRecord.COLUMN_DATE, data.getDate());
			values.put( FoodRecord.COLUMN_SATISFACTION, data.getSatisfaction());
			values.put( MealRecord.COLUMN_PROTEIN, data.getProtein());
			values.put( MealRecord.COLUMN_CARBOHYDRATE, data.getCarbohydrate());
			values.put( MealRecord.COLUMN_LIPID, data.getLipid());
			
			Long rowId = data.getRowid();
			// IDがnullの場合はinsert
			if( rowId == null){
				rowId = db.insert( FoodRecord.TABLE_NAME, null, values);
			}
			else{
				db.update( FoodRecord.TABLE_NAME, values, FoodRecord.COLUMN_ID + "=?", new String[]{ String.valueOf( rowId)});
			}
			result = load( rowId);
		} catch(Exception ex){
			Log.d(ex.toString(), ex.getMessage());
		} finally{
		}
		return result;
	}
	
	/**
	 * レコードの削除
	 * @param FoodRecord 削除対象のオブジェクト
	 */
	public void delete(FoodRecord record) {
		SQLiteDatabase db = helper.getWritableDatabase();
		try {
			db.delete( FoodRecord.TABLE_NAME, FoodRecord.COLUMN_ID + "=?", new String[]{ String.valueOf( record.getRowid())});
		} catch(Exception ex){
			Log.d(ex.toString(), ex.getMessage());
		} finally{
		}
	}
	
	/**
	 * idでDailyRecordをロードする
	 * @param rowId PK
	 * @return ロード結果
	 */
	public FoodRecord load(Long rowId) {
		SQLiteDatabase db = helper.getReadableDatabase();		
		FoodRecord data = null;
		try {
			Cursor cursor = db.query( FoodRecord.TABLE_NAME, null, FoodRecord.COLUMN_ID + "=?", new String[]{ String.valueOf( rowId)}, null, null, null);
			cursor.moveToFirst();
			data = getRecord( cursor);
		} catch(Exception ex){
			Log.d(ex.toString(), ex.getMessage());
		} finally{
		}
		return data;
	}

	/**
	 * 一覧を取得する
	 * @return 検索結果
	 */
	public List<FoodRecord> list() {
		return list(null,null,null,null,FoodRecord.COLUMN_ID);
	}
	/**
	 * 一覧を取得する
	 * @return 検索結果
	 */
	public List<FoodRecord> list(String kind) {
		return list(FoodRecord.COLUMN_KIND + "=?" , new String[]{kind},null,null,FoodRecord.COLUMN_ID);
	}
	/**
	 * 一覧を取得する
	 * @return 検索結果
	 */
	public List<FoodRecord> list(String selection, String[] selectionArgs,String groupBy, String having, String orderBy) {
		SQLiteDatabase db = helper.getReadableDatabase();
		
		List<FoodRecord> recordList	 = new ArrayList<FoodRecord>();
		try {
			Cursor cursor = db.query( FoodRecord.TABLE_NAME, null, selection, selectionArgs, groupBy, having, orderBy);
			cursor.moveToFirst();
			while( !cursor.isAfterLast()){
				recordList.add( getRecord( cursor));
				cursor.moveToNext();
			}
		}catch(Exception ex){
			Log.d(ex.toString(), ex.getMessage());
		} finally {
//			db.close();
		}
		return recordList;
	}
	
	/**
	 * カーソルからオブジェクトへの変換
	 * @param cursor カーソル
	 * @return 変換結果
	 * @throws Exception 
	 */
	private FoodRecord getRecord( Cursor cursor) throws Exception{
		FoodRecord record = new FoodRecord();

		record.setRowid( cursor.getLong(cursor.getColumnIndex(FoodRecord.COLUMN_ID)));
		record.setName(cursor.getString(cursor.getColumnIndex(FoodRecord.COLUMN_NAME)));
		record.setMealId(cursor.getLong(cursor.getColumnIndex(FoodRecord.COLUMN_MEAL_ID)));
		record.setUnit(cursor.getString(cursor.getColumnIndex(FoodRecord.COLUMN_UNIT)));
		record.setKind(cursor.getString(cursor.getColumnIndex(FoodRecord.COLUMN_KIND)));
		record.setDate(cursor.getString(cursor.getColumnIndex(FoodRecord.COLUMN_DATE)));
		record.setSatisfaction(cursor.getInt(cursor.getColumnIndex(FoodRecord.COLUMN_SATISFACTION)));

		if(!cursor.isNull((cursor.getColumnIndex(FoodRecord.COLUMN_IMAGE))))
			record.setImage(cursor.getBlob(cursor.getColumnIndex(FoodRecord.COLUMN_IMAGE)));

		record.setProtein(cursor.getDouble(cursor.getColumnIndex(MealRecord.COLUMN_PROTEIN)));
		record.setCarbohydrate(cursor.getDouble(cursor.getColumnIndex(MealRecord.COLUMN_CARBOHYDRATE)));
		record.setLipid(cursor.getDouble(cursor.getColumnIndex(MealRecord.COLUMN_LIPID)));
		return record;
	}
	
}


