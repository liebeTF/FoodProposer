package com.example.foodlog.db;

import java.io.Serializable;
import java.util.Locale;

@SuppressWarnings("serial")
public class StatisticsRecord implements Serializable {

	public static final int TERM_DAY = 0;
	public static final int TERM_MONTH = 1;
	public static final int TERM_YEAR = 2;

	
	public static final int MODE_AVERAGE = 0;	//平均
	public static final int MODE_SUM = 1;		//合計

	// プロパティ
	private int term = TERM_DAY;
	private int mode = MODE_SUM;
	private Integer year = null;
	private Integer month = null;
	private Integer day = null;
	private Integer mealCount = null;

	private Double protein = null;
	private Double carbohydrate = null;
	private Double lipid = null;
	private Integer energy = null;
	

		
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
		if(year!=null)
			builder.append(year);
		if(month!=null)
			builder.append(String.format("/%02d",month+1));
		if(day!=null)
			builder.append(String.format("/%02d",day));

		builder.append(", ");
		switch (mode) {
		case MODE_SUM:
			builder.append("合計 ");			
			break;
		case MODE_AVERAGE:
			builder.append("平均 ");			
			break;
		default:
			break;
		}
		if( energy != null){			
			builder.append(energy);
			builder.append(" kcal");

		}
		
		return builder.toString();
	}
	public Integer getEnergy() {
		return energy;
	}
	public void setEnergy(Integer energy) {
		this.energy = energy;
	}
	public Integer getMealCount() {
		return mealCount;
	}
	public void setMealCount(Integer mealCount) {
		this.mealCount = mealCount;
	}
	public int getTerm() {
		return term;
	}
	public void setTerm(int term) {
		this.term = term;
	}
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
}
