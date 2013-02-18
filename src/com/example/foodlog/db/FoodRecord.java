package com.example.foodlog.db;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

@SuppressWarnings("serial")
public class FoodRecord extends FoodData {
	
	// テーブル名
	public static final String TABLE_NAME = "FoodRecord";
	
	// カラム名
	public static final String COLUMN_MEAL_ID = "meal_id";
	public static final String COLUMN_SCALE = "scale";
	
	

	
	// プロパティ
	private Long mealId = null;
	private String scale = null;

	private String date = null;
	

	

	/**
	 * ListView表示の際に利用するのでPFC比を返す
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		Integer pfc[] = calcPFCpropotion();
		builder.append(kind);
		builder.append(": ");
		builder.append(name);
		builder.append(", ");
		builder.append(pfc[0] + ":" + pfc[1] + ":" + pfc[2]);
		return builder.toString();
	}
	public Integer[] getDateArray(){
		String[] date = this.date.split("-");
		
		return new Integer[]{
				Integer.valueOf(date[0]),
				Integer.valueOf(date[1]),
				Integer.valueOf(date[2])
		};		
	}

	public void setDate(String date) {
		this.date = date;
	}
	public void setFood(FoodRecord data){
		setName(data.getName());
		setKind(data.getKind());
		setUnit(data.getUnit());
		setProtein(data.getProtein());
		setCarbohydrate(data.getCarbohydrate());
		setLipid(data.getLipid());
	}
	static public String nextName(String name){
		Integer start=name.lastIndexOf("(");
		if(start < 0)
			return name+"(2)";
		Integer end=name.lastIndexOf(")");
		if(start < 0)
			return name+"(2)";
		String numStr = name.substring(start+1,end);
		Integer num;
		try{
			num = Integer.valueOf(numStr);
			++num;
		}catch(NumberFormatException ex){
			return name+"(2)";
		}		
		return name.substring(0,start) + "("+ num +")";
	}
	public Long getMealId() {
		return mealId;
	}
	public void setMealId(Long mealId) {
		this.mealId = mealId;
	}
	public String getScale() {
		return scale;
	}
	public void setScale(String scale) {
		this.scale = scale;
	}
}
