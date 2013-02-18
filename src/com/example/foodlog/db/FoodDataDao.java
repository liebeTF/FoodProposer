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
public class FoodDataDao {
	
	private DatabaseOpenHelper helper = null;
	private static SQLiteDatabase db;
	
	public FoodDataDao(Context context) {
		helper = new DatabaseOpenHelper(context);
	}
	
	/**
	 * fooddataの保存
	 * rowidがnullの場合はinsert、rowidが!nullの場合はupdate
	 * @param FoodData 保存対象のオブジェクト
	 * @return 保存結果
	 */
	public FoodData save( FoodData data){
		db = helper.getWritableDatabase();
		FoodData result = null;
		try {
			ContentValues values = new ContentValues();
			values.put( FoodData.COLUMN_NAME, data.getName());
			values.put( FoodData.COLUMN_UNIT, data.getUnit());
			values.put( FoodData.COLUMN_KIND, data.getKind());
			values.put( FoodData.COLUMN_IMAGE, data.getImage());
			values.put( FoodData.COLUMN_DATE, data.getDate());
			values.put( FoodData.COLUMN_SATISFACTION, data.getSatisfaction());
			values.put( MealRecord.COLUMN_PROTEIN, data.getProtein());
			values.put( MealRecord.COLUMN_CARBOHYDRATE, data.getCarbohydrate());
			values.put( MealRecord.COLUMN_LIPID, data.getLipid());
			
			Long rowId = data.getRowid();
			// IDがnullの場合はinsert
			if( rowId == null){
				rowId = db.insert( FoodData.TABLE_NAME, null, values);
			}
			else{
				db.update( FoodData.TABLE_NAME, values, FoodData.COLUMN_ID + "=?", new String[]{ String.valueOf( rowId)});
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
	 * @param FoodData 削除対象のオブジェクト
	 */
	public void delete(FoodData record) {
		SQLiteDatabase db = helper.getWritableDatabase();
		try {
			db.delete( FoodData.TABLE_NAME, FoodData.COLUMN_ID + "=?", new String[]{ String.valueOf( record.getRowid())});
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
	public FoodData load(Long rowId) {
		SQLiteDatabase db = helper.getReadableDatabase();		
		FoodData data = null;
		try {
			Cursor cursor = db.query( FoodData.TABLE_NAME, null, FoodData.COLUMN_ID + "=?", new String[]{ String.valueOf( rowId)}, null, null, null);
			cursor.moveToFirst();
			data = getData( cursor);
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
	public List<FoodData> list() {
		return list(null,null,null,null,FoodData.COLUMN_ID);
	}
	/**
	 * 一覧を取得する
	 * @return 検索結果
	 */
	public List<FoodData> list(String kind) {
		return list(FoodData.COLUMN_KIND + "=?" , new String[]{kind},null,null,FoodData.COLUMN_ID);
	}
	/**
	 * 一覧を取得する
	 * @return 検索結果
	 */
	public List<FoodData> list(String selection, String[] selectionArgs,String groupBy, String having, String orderBy) {
		SQLiteDatabase db = helper.getReadableDatabase();
		
		List<FoodData> dataList	 = new ArrayList<FoodData>();
		try {
			Cursor cursor = db.query( FoodData.TABLE_NAME, null, selection, selectionArgs, groupBy, having, orderBy);
			cursor.moveToFirst();
			while( !cursor.isAfterLast()){
				dataList.add( getData( cursor));
				cursor.moveToNext();
			}
		}catch(Exception ex){
			Log.d(ex.toString(), ex.getMessage());
		} finally {
//			db.close();
		}
		return dataList;
	}
	
	/**
	 * カーソルからオブジェクトへの変換
	 * @param cursor カーソル
	 * @return 変換結果
	 * @throws Exception 
	 */
	private FoodData getData( Cursor cursor) throws Exception{
		FoodData data = new FoodData();

		data.setRowid( cursor.getLong(cursor.getColumnIndex(FoodData.COLUMN_ID)));
		data.setName(cursor.getString(cursor.getColumnIndex(FoodData.COLUMN_NAME)));
		data.setUnit(cursor.getString(cursor.getColumnIndex(FoodData.COLUMN_UNIT)));
		data.setKind(cursor.getString(cursor.getColumnIndex(FoodData.COLUMN_KIND)));
		data.setDate(cursor.getInt(cursor.getColumnIndex(FoodData.COLUMN_DATE)));
		data.setSatisfaction(cursor.getInt(cursor.getColumnIndex(FoodData.COLUMN_SATISFACTION)));

		if(!cursor.isNull((cursor.getColumnIndex(FoodData.COLUMN_IMAGE))))
			data.setImage(cursor.getBlob(cursor.getColumnIndex(FoodData.COLUMN_IMAGE)));

		data.setProtein(cursor.getDouble(cursor.getColumnIndex(MealRecord.COLUMN_PROTEIN)));
		data.setCarbohydrate(cursor.getDouble(cursor.getColumnIndex(MealRecord.COLUMN_CARBOHYDRATE)));
		data.setLipid(cursor.getDouble(cursor.getColumnIndex(MealRecord.COLUMN_LIPID)));
		return data;
	}
	
	public List<FoodData> foodList(Integer energyMax,Integer date,Boolean mustImage){
		SQLiteDatabase db = helper.getReadableDatabase();
		
		List<FoodData> dataList	 = new ArrayList<FoodData>();
		try {
			String selection = FoodData.COLUMN_DATE +" < "+ date;
			Cursor cursor = db.query( FoodData.TABLE_NAME, null, selection, null, null, null, FoodData.COLUMN_SATISFACTION+" desc");
			cursor.moveToFirst();
			Integer energy = energyMax;
			FoodData data;
			while( !cursor.isAfterLast()){
				data = getData( cursor);
				if(data.getEnergy() < energy && (!mustImage || data.getImage()!=null)){
					energy -= data.getEnergy();
					dataList.add(data);
				}
				cursor.moveToNext();
			}
		}catch(Exception ex){
			Log.d(ex.toString(), ex.getMessage());
		} finally {
		}				
		return dataList;
	}
}


