package com.example.foodlog;


import java.util.Calendar;

import com.example.foodlog.R;
import com.example.foodlog.db.DatabaseOpenHelper;
import com.example.foodlog.db.MealRecord;

import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener{
	final static Calendar calendar = Calendar.getInstance();
	final static Integer year = calendar.get(Calendar.YEAR);
	final static Integer month = calendar.get(Calendar.MONTH);
	final static Integer day = calendar.get(Calendar.DAY_OF_MONTH);
		
	private TextView listlabel;
	private TextView dailylabel;
	private TextView recordlabel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		listlabel = (TextView)findViewById( R.id.listlabel);
		listlabel.setOnClickListener(this);
		dailylabel = (TextView)findViewById( R.id.dailylabel);
		dailylabel.setOnClickListener(this);
		recordlabel = (TextView)findViewById( R.id.recordlabel);
		recordlabel.setOnClickListener(this);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
    /**
     * 『一覧』要素クリック時の処理
     * 選択されたエンティティを詰めてList画面へ遷移する
     */
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.listlabel:
			// 一覧画面へ遷移する明示的インテントを生成
			Intent listIntent = new Intent(MainActivity.this, ListActivity.class);
			// アクティビティを開始する
			startActivity(listIntent);
			break;
		case R.id.dailylabel:
			// 一日の画面へ遷移する明示的インテントを生成
			Intent dailyIntent = new Intent(MainActivity.this, DailyMealListActivity.class);
			// アクティビティを開始する
			startActivity(dailyIntent);
			break;
		case R.id.recordlabel:
			// 記録画面へ遷移する明示的インテントを生成
			Intent recordIntent = new Intent(MainActivity.this, RecordActivity.class);
			// アクティビティを開始する
			startActivity(recordIntent);
			break;
		}
	}
    /**
     * オプションメニューの選択
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
        case R.id.menu_settings:
            break;
        case R.id.menu_food_regist:
        	Intent foodIntent = new Intent(this,FoodRegistActivity.class);
        	startActivity(foodIntent);
            break;
        }
        return true;
    };

}
