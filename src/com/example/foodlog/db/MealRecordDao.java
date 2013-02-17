package com.example.foodlog.db;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * DailyRecord�p�f�[�^�A�N�Z�X�N���X
 */
public class MealRecordDao {
	
	private DatabaseOpenHelper helper = null;
	private static SQLiteDatabase db;
	
	public MealRecordDao(Context context) {
		helper = new DatabaseOpenHelper(context);
	}
	
	/**
	 * record�̕ۑ�
	 * rowid��null�̏ꍇ��insert�Arowid��!null�̏ꍇ��update
	 * @param MealRecord �ۑ��Ώۂ̃I�u�W�F�N�g
	 * @return �ۑ�����
	 */
	public MealRecord save( MealRecord record){
		db = helper.getWritableDatabase();
		MealRecord result = null;
		try {
			ContentValues values = new ContentValues();
			values.put( MealRecord.COLUMN_FOODS, record.getFoodString());
			values.put( MealRecord.COLUMN_YEAR, record.getYear());
			values.put( MealRecord.COLUMN_MONTH, record.getMonth());
			values.put( MealRecord.COLUMN_DAY, record.getDay());
			values.put( MealRecord.COLUMN_NTH, record.getNth());
			values.put( MealRecord.COLUMN_HOUR, record.getHour());
			values.put( MealRecord.COLUMN_MINUTE, record.getMinute());
			values.put( MealRecord.COLUMN_MEALTIME, record.getMealtime());
			
			values.put( MealRecord.COLUMN_SATIETY1, record.getSatiety1());
			values.put( MealRecord.COLUMN_SATIETY2, record.getSatiety2());
			values.put( MealRecord.COLUMN_MEMBER, record.getMember());
			
			values.put( MealRecord.COLUMN_PROTEIN, record.getProtein());
			values.put( MealRecord.COLUMN_CARBOHYDRATE, record.getCarbohydrate());
			values.put( MealRecord.COLUMN_LIPID, record.getLipid());
			values.put( MealRecord.COLUMN_ENERGY, record.getEnergy());
			
			Long rowId = record.getRowid();
			// ID��null�̏ꍇ��insert
			if( rowId == null){
				rowId = db.insert( MealRecord.TABLE_NAME, null, values);
			}
			else{
				db.update( MealRecord.TABLE_NAME, values, MealRecord.COLUMN_ID + "=?", new String[]{ String.valueOf( rowId)});
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
	 * @param MealRecord �폜�Ώۂ̃I�u�W�F�N�g
	 */
	public void delete(MealRecord record) {
		SQLiteDatabase db = helper.getWritableDatabase();
		try {
			db.delete( MealRecord.TABLE_NAME, MealRecord.COLUMN_ID + "=?", new String[]{ String.valueOf( record.getRowid())});
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
	public MealRecord load(Long rowId) {
		SQLiteDatabase db = helper.getReadableDatabase();		
		MealRecord record = null;
		try {
			Cursor cursor = db.query( MealRecord.TABLE_NAME, null, MealRecord.COLUMN_ID + "=?", new String[]{ String.valueOf( rowId)}, null, null, null);
			cursor.moveToFirst();
			record = getRecord( cursor);
		} catch(Exception ex){
			Log.d(ex.toString(), ex.getMessage());
		} finally{
		}
		return record;
	}
	
	/**
	 * �������ʈꗗ���擾����
	 * @param table
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return ��������
	 */
	public List<MealRecord> list(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
		SQLiteDatabase db = helper.getReadableDatabase();
		
		List<MealRecord> RecordList = null;
		try {
			Cursor cursor = db.query( table, columns, selection, selectionArgs,groupBy,having,orderBy);
			RecordList = new ArrayList<MealRecord>();
			cursor.moveToFirst();
			while( !cursor.isAfterLast()){
				MealRecord record = getRecord( cursor); 
				RecordList.add( record );
				cursor.moveToNext();
			}
		}catch(Exception ex){
			Log.d(ex.toString(), ex.getMessage());
		}finally {
		}
		return RecordList;
	}

	/**
	 * �ꗗ���擾����
	 * @return ��������
	 */
	public List<MealRecord> list() {
		return list(MealRecord.TABLE_NAME,null,null,null,null,null,MealRecord.COLUMN_ID);
	}
	/**
	 * ���̓��̈ꗗ���擾����
	 * @param year
	 * @param month
	 * @param day
	 * @return ��������
	 */
	public List<MealRecord> list(Integer year,Integer month,Integer day,String orderBy) {
		String selection = MealRecord.COLUMN_YEAR + "=? and "
							+ MealRecord.COLUMN_MONTH + "=? and "
							+ MealRecord.COLUMN_DAY + "=?";
		String selectionArgs[] = new String[]{String.valueOf(year),String.valueOf(month),String.valueOf(day)};
		return list(MealRecord.TABLE_NAME,null,selection,selectionArgs,null,null,orderBy);
	}
	/**
	 * ���̓��̍��v���擾����
	 * @param mealList ���t���ɂȂ��MealRecord
	 * @param mode ���ρA���v
	 * @return ���v����
	 * mealList����̏ꍇ��null��Ԃ�
	 */
	public StatisticsRecord listToStatistics(List<MealRecord> mealList, Integer mode) {
		
		StatisticsRecord statistics = null ;
		boolean flag_year=true;
		boolean flag_month=true;
		boolean flag_day=true;
		
		double protein=0;
		double lipid=0;
		double carbohydrate=0;
		int energy=0; 
		int count_protein=0;
		int count_carbohydrate=0;
		int count_lipid=0;
		int count_energy=0;
		Iterator<MealRecord> it = mealList.iterator();
		if (it.hasNext()) {
			statistics = new StatisticsRecord();
			MealRecord meal = it.next();
			if (meal.getProtein() != null) {
				protein += meal.getProtein();
				++count_protein;
			}
			if (meal.getCarbohydrate() != null) {
				carbohydrate += meal.getCarbohydrate();
				++count_carbohydrate;
			}
			if (meal.getLipid() != null) {
				lipid += meal.getLipid();
				++count_lipid;
			}
			if (meal.getEnergy() != null) {
				energy += meal.getEnergy();
				++count_energy;
			}
			int year = meal.getYear();
			int month = meal.getMealtime();
			int day = meal.getDay();

			while (it.hasNext()) {
				meal = it.next();
				if (flag_year)
					flag_year = (year == meal.getYear());
				if (flag_month)
					flag_month = (month == meal.getMonth());
				if (flag_day)
					flag_day = (day == meal.getDay());
				if (meal.getProtein() != null) {
					protein += meal.getProtein();
					++count_protein;
				}
				if (meal.getCarbohydrate() != null) {
					carbohydrate += meal.getCarbohydrate();
					++count_carbohydrate;
				}
				if (meal.getLipid() != null) {
					lipid += meal.getLipid();
					++count_lipid;
				}
				if (meal.getEnergy() != null) {
					energy += meal.getEnergy();
					++count_energy;
				}
			}
			statistics.setMode(mode);
			switch (statistics.getMode()) {
			case StatisticsRecord.MODE_AVERAGE:
				if (count_protein != 0)
					protein /= count_protein;
				if (count_carbohydrate != 0)
					carbohydrate /= count_carbohydrate;
				if (count_lipid != 0)
					lipid /= count_lipid;
				if (count_energy != 0)
					energy /= count_energy;
				break;
			case StatisticsRecord.MODE_SUM:
				break;
			default:
				break;
			}
			statistics.setProtein(protein);
			statistics.setCarbohydrate(carbohydrate);
			statistics.setLipid(lipid);
//			statistics.setEnergy(energy);
			if (flag_year)
				statistics.setYear(year);
			if (flag_month)
				statistics.setMonth(month);
			if (flag_day)
				statistics.setDay(day);
		}
		
		return statistics;
	}

	

	/**
	 * �J�[�\������I�u�W�F�N�g�ւ̕ϊ�
	 * @param cursor �J�[�\��
	 * @return �ϊ�����
	 */
	private MealRecord getRecord( Cursor cursor){
		MealRecord record = new MealRecord();
		record.setRowid( cursor.getLong(cursor.getColumnIndex(MealRecord.COLUMN_ID)));
		record.setFoodString( cursor.getString(cursor.getColumnIndex(MealRecord.COLUMN_FOODS)));
		record.setYear( cursor.getInt(cursor.getColumnIndex(MealRecord.COLUMN_YEAR)));
		record.setMonth(cursor.getInt(cursor.getColumnIndex(MealRecord.COLUMN_MONTH)));
		record.setDay(cursor.getInt(cursor.getColumnIndex(MealRecord.COLUMN_DAY)));
		record.setHour(cursor.getInt(cursor.getColumnIndex(MealRecord.COLUMN_HOUR)));
		record.setMinute(cursor.getInt(cursor.getColumnIndex(MealRecord.COLUMN_MINUTE)));

		record.setNth(cursor.getInt(cursor.getColumnIndex(MealRecord.COLUMN_NTH)));
		record.setMealtime(cursor.getInt(cursor.getColumnIndex(MealRecord.COLUMN_MEALTIME)));

		if(!cursor.isNull((cursor.getColumnIndex(MealRecord.COLUMN_SATIETY1))))
			record.setSatiety1(cursor.getInt(cursor.getColumnIndex(MealRecord.COLUMN_SATIETY1)));
		if(!cursor.isNull((cursor.getColumnIndex(MealRecord.COLUMN_SATIETY2))))
			record.setSatiety2(cursor.getInt(cursor.getColumnIndex(MealRecord.COLUMN_SATIETY2)));
		if(!cursor.isNull((cursor.getColumnIndex(MealRecord.COLUMN_MEMBER))))
			record.setMember(cursor.getInt(cursor.getColumnIndex(MealRecord.COLUMN_MEMBER)));

		if(!cursor.isNull((cursor.getColumnIndex(MealRecord.COLUMN_PROTEIN))))
			record.setProtein(cursor.getDouble(cursor.getColumnIndex(MealRecord.COLUMN_PROTEIN)));
		if(!cursor.isNull((cursor.getColumnIndex(MealRecord.COLUMN_CARBOHYDRATE))))
			record.setCarbohydrate(cursor.getDouble(cursor.getColumnIndex(MealRecord.COLUMN_CARBOHYDRATE)));
		if(!cursor.isNull((cursor.getColumnIndex(MealRecord.COLUMN_LIPID))))
			record.setLipid(cursor.getDouble(cursor.getColumnIndex(MealRecord.COLUMN_LIPID)));
		if(!cursor.isNull((cursor.getColumnIndex(MealRecord.COLUMN_ENERGY))))
			record.setEnergy(cursor.getInt(cursor.getColumnIndex(MealRecord.COLUMN_ENERGY)));

		return record;
	}
	
	/**
	 * �������ʈꗗ���擾����
	 * @param table
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return ��������
	 */
	public List<StatisticsRecord> statisticsList(int term,int mode) {
		SQLiteDatabase db = helper.getReadableDatabase();
		String table = MealRecord.TABLE_NAME;
		List<String> stringList = new ArrayList<String>();
		String[] columns= null;
		String selection=null;
		String[] selectionArgs=null;
		String groupBy=null;
		String having=null;
		String orderBy;
		switch(term){
		case StatisticsRecord.TERM_DAY:
			stringList.add(MealRecord.COLUMN_MONTH);
			stringList.add(MealRecord.COLUMN_DAY);
			groupBy = MealRecord.COLUMN_YEAR + "," + MealRecord.COLUMN_MONTH +"," + MealRecord.COLUMN_DAY;
			orderBy = MealRecord.COLUMN_YEAR + "," + MealRecord.COLUMN_MONTH +"," + MealRecord.COLUMN_DAY;
			break;
		case StatisticsRecord.TERM_MONTH:
			stringList.add(MealRecord.COLUMN_MONTH);
			groupBy = MealRecord.COLUMN_YEAR + "," + MealRecord.COLUMN_MONTH;
			orderBy = MealRecord.COLUMN_YEAR + "," + MealRecord.COLUMN_MONTH;
			break;
		case StatisticsRecord.TERM_YEAR:
			groupBy = MealRecord.COLUMN_YEAR;
			orderBy = MealRecord.COLUMN_YEAR;
			break;
		default:
			stringList.add(MealRecord.COLUMN_MONTH);
			stringList.add(MealRecord.COLUMN_DAY);
			groupBy = MealRecord.COLUMN_YEAR + "," + MealRecord.COLUMN_MONTH +"," + MealRecord.COLUMN_DAY;
			orderBy = MealRecord.COLUMN_YEAR + "," + MealRecord.COLUMN_MONTH +"," + MealRecord.COLUMN_DAY;
			break;
		}
		stringList.add(MealRecord.COLUMN_YEAR);
		String str;
		switch (mode) {
		case StatisticsRecord.MODE_SUM:
			str = "SUM";
			break;
		case StatisticsRecord.MODE_AVERAGE:
			str = "AVG";
			break;
		default:
			str = "AVG";
			break;
		}
		stringList.add(str + "(" + MealRecord.COLUMN_PROTEIN + ") AS "+ MealRecord.COLUMN_PROTEIN);
		stringList.add(str + "(" + MealRecord.COLUMN_CARBOHYDRATE + ") AS " + MealRecord.COLUMN_CARBOHYDRATE);
		stringList.add(str + "(" + MealRecord.COLUMN_LIPID + ") AS "+ MealRecord.COLUMN_LIPID);
		stringList.add(str + "(" + MealRecord.COLUMN_ENERGY +") AS " + MealRecord.COLUMN_ENERGY);
		columns = (String[])stringList.toArray(new String[0]);
		List<StatisticsRecord> statisticsList = null;
		try {
			Cursor cursor = db.query( table, columns, selection, selectionArgs,groupBy,having,orderBy);
			statisticsList = new ArrayList<StatisticsRecord>();
			cursor.moveToFirst();
			while( !cursor.isAfterLast()){
				statisticsList.add( getStatisticsRecord( cursor,term,mode));
				cursor.moveToNext();
			}
		}catch(Exception ex){
			Log.d(ex.toString(), ex.getMessage());
		}finally {
		}
		return statisticsList;
	}

	private StatisticsRecord getStatisticsRecord(Cursor cursor, int term,
			int mode) {
		StatisticsRecord record = new StatisticsRecord();
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		record.setMode(mode);
		if(!cursor.isNull((cursor.getColumnIndex(MealRecord.COLUMN_PROTEIN))))
			record.setProtein(cursor.getDouble(cursor.getColumnIndex(MealRecord.COLUMN_PROTEIN)));
		if(!cursor.isNull((cursor.getColumnIndex(MealRecord.COLUMN_CARBOHYDRATE))))
			record.setCarbohydrate(cursor.getDouble(cursor.getColumnIndex(MealRecord.COLUMN_CARBOHYDRATE)));
		if(!cursor.isNull((cursor.getColumnIndex(MealRecord.COLUMN_LIPID))))
			record.setLipid(cursor.getDouble(cursor.getColumnIndex(MealRecord.COLUMN_LIPID)));
//		if(!cursor.isNull((cursor.getColumnIndex(MealRecord.COLUMN_ENERGY))))
//			record.setEnergy(cursor.getInt(cursor.getColumnIndex(MealRecord.COLUMN_ENERGY)));
		switch (term) {
		case StatisticsRecord.TERM_DAY:
			record.setYear( cursor.getInt(cursor.getColumnIndex(MealRecord.COLUMN_YEAR)));
			record.setMonth(cursor.getInt(cursor.getColumnIndex(MealRecord.COLUMN_MONTH)));
			record.setDay(cursor.getInt(cursor.getColumnIndex(MealRecord.COLUMN_DAY)));			
			break;
		case StatisticsRecord.TERM_MONTH:
			record.setYear( cursor.getInt(cursor.getColumnIndex(MealRecord.COLUMN_YEAR)));
			record.setMonth(cursor.getInt(cursor.getColumnIndex(MealRecord.COLUMN_MONTH)));
			break;
		case StatisticsRecord.TERM_YEAR:
			record.setYear( cursor.getInt(cursor.getColumnIndex(MealRecord.COLUMN_YEAR)));
			break;
		default:
			record.setYear( cursor.getInt(cursor.getColumnIndex(MealRecord.COLUMN_YEAR)));
			break;
		}		
		return record;
	}

}


