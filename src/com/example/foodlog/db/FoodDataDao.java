package com.example.foodlog.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * DailyRecord�p�f�[�^�A�N�Z�X�N���X
 */
public class FoodDataDao {
	
	private DatabaseOpenHelper helper = null;
	private static SQLiteDatabase db;
	
	public FoodDataDao(Context context) {
		helper = new DatabaseOpenHelper(context);
	}
	
	/**
	 * fooddata�̕ۑ�
	 * rowid��null�̏ꍇ��insert�Arowid��!null�̏ꍇ��update
	 * @param FoodData �ۑ��Ώۂ̃I�u�W�F�N�g
	 * @return �ۑ�����
	 */
	public FoodData save( FoodData data){
		db = helper.getWritableDatabase();
		FoodData result = null;
		try {
			ContentValues values = new ContentValues();
			values.put( FoodData.COLUMN_NAME, data.getName());
			values.put( FoodData.COLUMN_UNIT, data.getUnit());
			values.put( FoodData.COLUMN_KIND, data.getKind());
			values.put( FoodData.COLUMN_PROTEIN, data.getProtein());
			values.put( FoodData.COLUMN_CARBOHYDRATE, data.getCarbohydrate());
			values.put( FoodData.COLUMN_LIPID, data.getLipid());
			
			Long rowId = data.getRowid();
			// ID��null�̏ꍇ��insert
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
	 * ���R�[�h�̍폜
	 * @param FoodData �폜�Ώۂ̃I�u�W�F�N�g
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
	 * id��DailyRecord�����[�h����
	 * @param rowId PK
	 * @return ���[�h����
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
	 * �ꗗ���擾����
	 * @return ��������
	 */
	public List<FoodData> list() {
		return list(null,null,null,null,FoodData.COLUMN_ID);
	}
	/**
	 * �ꗗ���擾����
	 * @return ��������
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
			db.close();
		}
		return dataList;
	}
	
	/**
	 * �J�[�\������I�u�W�F�N�g�ւ̕ϊ�
	 * @param cursor �J�[�\��
	 * @return �ϊ�����
	 * @throws Exception 
	 */
	private FoodData getData( Cursor cursor) throws Exception{
		FoodData data = new FoodData();

		data.setRowid( cursor.getLong(cursor.getColumnIndex(FoodData.COLUMN_ID)));
		data.setName(cursor.getString(cursor.getColumnIndex(FoodData.COLUMN_NAME)));
		data.setUnit(cursor.getString(cursor.getColumnIndex(FoodData.COLUMN_UNIT)));
		data.setKind(cursor.getInt(cursor.getColumnIndex(FoodData.COLUMN_KIND)));
		data.setProtein(cursor.getDouble(cursor.getColumnIndex(FoodData.COLUMN_PROTEIN)));
		data.setCarbohydrate(cursor.getDouble(cursor.getColumnIndex(FoodData.COLUMN_CARBOHYDRATE)));
		data.setLipid(cursor.getDouble(cursor.getColumnIndex(FoodData.COLUMN_LIPID)));

		return data;
	}
}

