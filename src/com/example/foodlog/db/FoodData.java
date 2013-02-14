package com.example.foodlog.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("serial")
public class FoodData implements Serializable {
	
	private static List<String> units = Arrays.asList("g","ml","個");
		
	// テーブル名
	public static final String TABLE_NAME = "FoodData";
	
	// カラム名
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_UNIT = "unit";	
	public static final String COLUMN_KIND = "kind";//朝食1,昼食2、夕食4、間食8の合計値
	
	public static final String COLUMN_PROTEIN = "protein";
	public static final String COLUMN_CARBOHYDRATE = "carbohydrate";
	public static final String COLUMN_LIPID = "lipid";
	
	static final Double E_PROTEIN = 4.0;
	static final Double E_CARBOHYDRATE = 4.0;
	static final Double E_LIPID = 9.0;
	
	static final Integer Breakfast = 1;
	static final Integer Lunch = 2;
	static final Integer Dinner = 4;
	static final Integer Snack = 8;
	
	

	
	// プロパティ
	private Long rowid = null;
	private String name = null;
	private String unit = null;
	private Integer kind = null;

	private Double protein = null;
	private Double carbohydrate = null;
	private Double lipid = null;
	

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
		builder.append(name);
		builder.append(", ");
		builder.append(pfc[0] + ":" + pfc[1] + ":" + pfc[2]);
		return builder.toString();
	}
	static public int calcEnergy(Double protein, Double carbohydrate, Double lipid){
		return (int)(protein*E_PROTEIN + carbohydrate * E_CARBOHYDRATE + lipid * E_LIPID);
	}
	public int calcEnergy(){
		return (int)(this.protein*E_PROTEIN 
				+ this.carbohydrate * E_CARBOHYDRATE 
				+ this.lipid * E_LIPID);
	}
	public Integer[] calcPFCpropotion(){
		Integer energy = calcEnergy();
		return new Integer[]{
				(int)(this.protein*E_PROTEIN / energy),
				(int)(this.carbohydrate*E_CARBOHYDRATE / energy),
				(int)(this.lipid*E_LIPID / energy)
				};
	}
	public Integer getKind() {
		return kind;
	}
	public void setKind(Integer kind) {
		this.kind = kind;
	}
	public void setKind(Boolean kind[]) {
		int i = 0;
		this.kind = 0;
		for(Boolean b:kind){
			this.kind += b? (int)Math.pow(2,i) : 0 ;
			++i;
		}
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) throws Exception{
		if(units.contains(unit)){
			this.unit = unit;
		}else{
			throw new Exception("unregesterd unit");
		}
	}
}
