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
	
	public static final List<String> units = Arrays.asList("100g","200ml","1��","1�H","1��");
	public static final Integer Staple = 0;//��H
	public static final Integer MainDish = 1;//���
	public static final Integer SideDish = 2;//����
	public static final Integer Drink = 3;//���݂���
	public static final Integer Fruit = 4;//�ʕ�
	public static final Integer EatOut = 5;//�O�H
	public static final Integer Snack = 6;//�ԐH	
	public static final Integer Others =7;//���̑�
	public static final Map<String,Integer> kinds = Collections.unmodifiableMap(new HashMap<String, Integer>(){{
		put("��H",Staple);
		put("���",MainDish);
		put("����",SideDish);
		put("���ݕ�",Drink);
		put("�ʕ�",Fruit);
		put("�O�H",EatOut);
		put("�ԐH",Snack);
		put("���̑�",Others);
	}});

	
	
	private static final int BLOB_SIZE_MAX = 500000;	
	private static final int WIDTH_MAX = 300;
	private static final int HEIGHT_MAX = 200;

	
	// �e�[�u����
	public static final String TABLE_NAME = "FoodData";
	
	// �J������
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_UNIT = "unit";	
	public static final String COLUMN_KIND = "kind";
	
//	public static final String COLUMN_PROTEIN = "protein";
//	public static final String COLUMN_CARBOHYDRATE = "carbohydrate";
//	public static final String COLUMN_LIPID = "lipid";
	public static final String COLUMN_IMAGE = "image";	
	
	

	
	// �v���p�e�B
	private Long rowid = null;
	private String name = null;
	private String unit = null;
	private String kind = null;

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
	

	/**
	 * ListView�\���̍ۂɗ��p����̂�PFC���Ԃ�
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
	public Integer[] calcPFCpropotion(){
		Integer energy = MealRecord.calcEnergy(protein,carbohydrate,lipid);
		return new Integer[]{
				(int)(100 * this.protein*MealRecord.E_PROTEIN / energy),
				(int)(100 * this.carbohydrate* MealRecord.E_CARBOHYDRATE / energy),
				(int)(100 * this.lipid * MealRecord.E_LIPID / energy)
				};
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit){
			this.unit = unit;
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
}
