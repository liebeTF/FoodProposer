package com.example.foodlog.db;

import java.io.Serializable;
import java.util.Locale;

@SuppressWarnings("serial")
public class StatisticsRecord implements Serializable {

	public static final int TERM_DAY = 0;
	public static final int TERM_MONTH = 1;
	public static final int TERM_YEAR = 2;

	
	public static final int MODE_AVERAGE = 0;	//����
	public static final int MODE_SUM = 1;		//���v

	// �v���p�e�B
	private int term = TERM_DAY;
	private int mode = MODE_SUM;
	private Integer year = null;
	private Integer month = null;
	private Integer day = null;
	private Integer mealCount = null;

	private Double protein = null;
	private Double carbohydrate = null;
	private Double lipid = null;
	

		
	//�^���p�N����setter getter
	public Double getProtein() {
		return protein;
	}
	public void setProtein(Double protein) {
		this.protein = protein;
	}

	//�Y��������setter getter
	public Double getCarbohydrate() {
		return carbohydrate;
	}
	public void setCarbohydrate(Double carbohydrate) {
		this.carbohydrate = carbohydrate;
	}

	//������setter getter
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
	 * ListView�\���̍ۂɗ��p����̂Ŏ���+�M�ʂ�Ԃ�
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
			builder.append("���v ");			
			break;
		case MODE_AVERAGE:
			builder.append("���� ");			
			break;
		default:
			break;
		}
		if( getEnergy() != null){			
			builder.append(getEnergy());
			builder.append(" kcal");

		}
		
		return builder.toString();
	}
	public Integer getEnergy() {
		return MealRecord.calcEnergy(protein, carbohydrate, lipid);
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
