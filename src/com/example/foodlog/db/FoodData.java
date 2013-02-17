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
public class FoodData implements Serializable {
	
	public static final List<String> units = Arrays.asList("100g","200ml","1個","1食","1枚");
	public static final List<String> kinds = Arrays.asList(
			"主食","主菜","副菜","飲み物","果物",
			"外食","間食","その他"
			);
	public static final Integer Staple = 0;//主食
	public static final Integer MainDish = 1;//主菜
	public static final Integer SideDish = 2;//副菜
	public static final Integer Drink = 3;//飲みもの
	public static final Integer Fruit = 4;//果物
	public static final Integer EatOut = 5;//外食
	public static final Integer Snack = 6;//間食	
	public static final Integer Others =7;//その他
//	public static final Map<String, Integer> kinds = Collections
//			.unmodifiableMap(new HashMap<String, Integer>() {
//				{
//					put("主食", Staple);
//					put("主菜", MainDish);
//					put("副菜", SideDish);
//					put("飲み物", Drink);
//					put("果物", Fruit);
//					put("外食", EatOut);
//					put("間食", Snack);
//					put("その他", Others);
//				}
//			});
	
	
	private static final int BLOB_SIZE_MAX = 500000;	
	private static final int WIDTH_MAX = 300;
	private static final int HEIGHT_MAX = 200;

	
	// テーブル名
	public static final String TABLE_NAME = "FoodData";
	
	// カラム名
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_UNIT = "unit";	
	public static final String COLUMN_KIND = "kind";
	public static final String COLUMN_SATISFACTION = "satisfaction";
	public static final String COLUMN_ATE_DATE = "ate_date";	
	public static final String COLUMN_IMAGE = "image";
	
	

	
	// プロパティ
	private Long rowid = null;
	private String name = null;
	private String unit = null;
	private String kind = null;
	
	private Integer satisfaction = null;
	private Integer date = null;
	
	private Double protein = null;
	private Double carbohydrate = null;
	private Double lipid = null;

	private byte[] image = null;
	

	public Long getRowid() {
		return rowid;
	}
	public void setRowid(Long rowid) {
		this.rowid = rowid;
	}
		
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	//タンパク質のsetter getter
	public Double getProtein() {
		return protein;
	}
	public void setProtein(Double protein) {
		this.protein = protein;
	}

	//炭水化物のsetter getter
	public Double getCarbohydrate() {
		return carbohydrate;
	}
	public void setCarbohydrate(Double carbohydrate) {
		this.carbohydrate = carbohydrate;
	}

	//脂質のsetter getter
	public Double getLipid() {
		return lipid;
	}
	public void setLipid(Double lipid) {
		this.lipid = lipid;
	}
	

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
	public Integer[] calcPFCpropotion(){
		Integer energy = getEnergy();
		return new Integer[]{
				(int)(100 * this.protein*MealRecord.E_PROTEIN / energy),
				(int)(100 * this.carbohydrate* MealRecord.E_CARBOHYDRATE / energy),
				(int)(100 * this.lipid * MealRecord.E_LIPID / energy)
				};
	}
	public Integer getEnergy() {
		return MealRecord.calcEnergy(protein,carbohydrate,lipid);
	}
	public String getKind() {
		return kind;
	}
	public boolean setKind(String kind) {
		if(kinds.contains(kind)){
			this.kind = kind;
			return true;
		}else{
			return false;
		}
	}
	public String getUnit() {
		return unit;
	}
	public boolean setUnit(String unit){
		if(units.contains(unit)){
			this.unit = unit;
			return true;
		}else{
			return false;
		}
	}
	public Bitmap getImageBitamp() {
		return BitmapFactory.decodeByteArray(image, 0, image.length);
	}
	public void setImageBitmap(Bitmap image) {
		int size = image.getByteCount();
		int width = image.getWidth();
		int height = image.getHeight();
		if(size > BLOB_SIZE_MAX){
			if(width * HEIGHT_MAX > height * WIDTH_MAX){
				float scale = (float)WIDTH_MAX/width;
				image = Bitmap.createScaledBitmap(image, WIDTH_MAX, (int)(scale*height), true);
			}else{
				float scale = (float)HEIGHT_MAX/height;
				image = Bitmap.createScaledBitmap(image, (int)(scale*width), HEIGHT_MAX, true);
			}
		}		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		image.compress(CompressFormat.JPEG, 100, bos);
		this.image = bos.toByteArray();
	}
	public void setImage(byte[] image) {
		this.image = image;
	}
	public byte[] getImage() {
		return this.image;
	}
	public Integer getYear(){
		return date/10000;
	}
	public Integer getMonth(){
		return (date/100)%100;
	}
	public Integer getday(){
		return date%100;
	}
	public Integer getDate() {
		return date;
	}
	public void setDate(Integer date) {
		this.date = date;
	}
	public void setDate(Integer year,Integer month,Integer day) {
		this.date = year * 10000 + month* 100 * day;
	}
	public Integer getSatisfaction() {
		return satisfaction;
	}
	public void setSatisfaction(Integer satisfaction) {
		if(satisfaction<0){
			satisfaction = 0;
		}else if(satisfaction>100){
			satisfaction = 100;
		}
		this.satisfaction = satisfaction;
	}
	static public FoodData copyFood(FoodData data){
		FoodData result = new FoodData();
		result.setName(nextName(data.getName()));
		result.setKind(data.getKind());
		result.setUnit(data.getUnit());
		result.setProtein(data.getProtein());
		result.setCarbohydrate(data.getCarbohydrate());
		result.setLipid(data.getLipid());
		result.setSatisfaction(data.getSatisfaction());
		return result; 
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
}
