package com.example.foodlog.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("serial")
public class MealRecord implements Serializable {
		
	// テーブル名
	public static final String TABLE_NAME = "MealRecord";
	
	// カラム名
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_FOODS = "foods";
	public static final String COLUMN_YEAR = "year";
	public static final String COLUMN_MONTH = "month";
	public static final String COLUMN_DAY = "day";
	public static final String COLUMN_NTH = "nth";
	public static final String COLUMN_HOUR = "hour";
	public static final String COLUMN_MINUTE = "minute";
	public static final String COLUMN_MEALTIME = "mealtime";
	public static final String COLUMN_SATIETY1 = "satiety1";
	public static final String COLUMN_SATIETY2 = "satiety2";
	public static final String COLUMN_MEMBER = "member";
	
	
	public static final String COLUMN_PROTEIN = "protein";
	public static final String COLUMN_CARBOHYDRATE = "carbohydrate";
	public static final String COLUMN_LIPID = "lipid";
	public static final String COLUMN_ENERGY = "energy";
	
	static public Double E_PROTEIN = 4.0;
	static public Double E_CARBOHYDRATE = 4.0;
	static public Double E_LIPID = 9.0;
	
	

	
	// プロパティ
	private Long rowid = null;
	private List<String> foods = null;//食品名 　量のリスト
	private Integer year = null;
	private Integer month = null;
	private Integer day = null;
	private Integer nth = null;
	private Integer hour = null;
	private Integer minute = null;
	private Integer mealtime = null;

	private Integer satiety1 = null;
	private Integer satiety2 = null;
	private Integer member = null;
	
	private Double protein = null;
	private Double carbohydrate = null;
	private Double lipid = null;
	private Integer energy = null;
	

	public Long getRowid() {
		return rowid;
	}
	public void setRowid(Long rowid) {
		this.rowid = rowid;
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
	


	public String getDate(){
		String str = String.format(Locale.JAPAN,"%04d/%02d/%02d", this.year, this.month+1, this.day);
		return str;		
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public Integer getMonth() {
		return month;
	}
	public void setMonth(Integer month) {
		this.month = month;
	}
	public Integer getDay() {
		return day;
	}
	public void setDay(Integer day) {
		this.day = day;
	}
	/**
	 * ListView表示の際に利用するので時刻+熱量を返す
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
//		builder.append( getDate());
//		builder.append(", ");
		builder.append(getNth()+"食目");
		builder.append("," +   getTime());
		
		if( energy != null){
			builder.append(", ");
			builder.append(energy);
			builder.append(" kcal");

			if(satiety1==null ||
					satiety2==null ||
					member==null){
				builder.append("　未記録あり");				
			}
		}else{
			builder.append("　未記録あり");
		}
		return builder.toString();
	}
	public Integer getNth() {
		return nth;
	}
	public void setNth(Integer nth) {
		this.nth = nth;
	}
	public Integer getHour() {
		return hour;
	}
	public void setHour(Integer hour) {
		this.hour = hour;
	}
	public Integer getMinute() {
		return minute;
	}
	public void setMinute(Integer minute) {
		this.minute = minute;
	}
	public String getTime() {
		String str = String.format(Locale.JAPAN,"%02d:%02d", this.hour, this.minute);
		return str;

	}
	public Integer getEnergy() {
		return energy;
	}
	public void setEnergy(Integer energy) {
		this.energy = energy;
	}
	static public Integer calcEnergy(Double protein, Double carbohydrate, Double lipid){
		if(protein==null && carbohydrate==null && lipid==null)
			return null;
		if(protein == null)
			protein = 0.0;
		if(carbohydrate == null)
			carbohydrate = 0.0;
		if(lipid == null)
			lipid=0.0;
		return (int)(protein*E_PROTEIN + carbohydrate * E_CARBOHYDRATE + lipid * E_LIPID);
	}
	public Integer getSatiety1() {
		return satiety1;
	}
	public void setSatiety1(Integer satiety1) {
		this.satiety1 = satiety1;
	}
	public Integer getSatiety2() {
		return satiety2;
	}
	public void setSatiety2(Integer satiety2) {
		this.satiety2 = satiety2;
	}
	public Integer getMealtime() {
		return mealtime;
	}
	public void setMealtime(Integer mealtime) {
		this.mealtime = mealtime;
	}
	public Integer getMember() {
		return member;
	}
	public void setMember(Integer member) {
		this.member = member;
	}
	public List<String> getFoods() {
		return foods;
	}
	public void setFoods(List<String> foods) {
		this.foods = foods;
	}
	/**
	 * ”/”で区切られたfoodsをList<String>でセット
	 * @param foods
	 */
	public void setFoodString(String foods) {
		if(foods != null)
			this.foods =  Arrays.asList( foods.split("/"));
	}
	public String getFoodString() {
		return DatabaseOpenHelper.join(foods, "/");
	}
	public boolean addFood(String food){
		return foods.add(food);
	}
	public boolean removeFood(String food){
		return foods.remove(food);
	}
	public boolean addFood(String food,String quantity){
		return foods.add(food+" " +quantity);
	}
	public boolean removeFood(String food,String quantity){
		return foods.remove(food+" "+quantity);
	}
	public boolean addFood(String food, Double protein, Double carbohydrate, Double lipid){
		this.protein += protein;
		this.carbohydrate += carbohydrate;
		this.lipid += lipid;
		return foods.add(food);
	}
	public boolean removeFood(String food, Double protein, Double carbohydrate, Double lipid){
		this.protein -= protein;
		this.carbohydrate -= carbohydrate;
		this.lipid -= lipid;
		return foods.remove(food);
	}
	
	
	public class Food extends FoodData{
		private Double scale;

		public Double getScale() {
			return scale;
		}
		public Double getQuantity() {
			String[] parts = getUnitParts();
			Double num = Double.valueOf(parts[0]);
			return num * scale;
		}

		public void setScale(double scale) {
			this.scale = scale;
		}
	}

}
