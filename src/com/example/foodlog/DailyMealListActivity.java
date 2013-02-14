package com.example.foodlog;

import java.util.Calendar;
import java.util.List;

import com.example.foodlog.db.MealRecord;
import com.example.foodlog.db.MealRecordDao;
import com.example.foodlog.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.app.DatePickerDialog;
import android.widget.DatePicker;

/**
 * 一覧表示アクティビティ
 */
public class DailyMealListActivity extends Activity implements OnItemClickListener{
	static MealRecordDao dao;
	Integer year;
	Integer month;
	Integer day;
	
	private TextView dateText;
	private TextView proteinText;
	private TextView carbohydrateText;
	private TextView lipidText;
	private TextView energyText;
	
	
    // 一覧表示用ListView
    private ListView listView = null;
    private ArrayAdapter<MealRecord> arrayAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 自動生成されたR.javaの定数を指定してXMLからレイアウトを生成
        setContentView(R.layout.daily_meal_list);
        
		// インテントからオブジェクトを取得
		Intent intent = getIntent();
		year = (Integer)intent.getSerializableExtra( MealRecord.COLUMN_YEAR);
		if(year==null)
			year = MainActivity.calendar.get(Calendar.YEAR);

		month = (Integer)intent.getSerializableExtra( MealRecord.COLUMN_MONTH);
		if(month==null)
			month = MainActivity.calendar.get(Calendar.MONTH);
		
		day = (Integer)intent.getSerializableExtra( MealRecord.COLUMN_DAY);
		if(day==null)
			day = MainActivity.calendar.get(Calendar.DAY_OF_MONTH);
		

		/*UI部品*/
		dateText = (TextView)findViewById(R.id.dateText);
		dateText.setText(String.format("%04d/%02d/%02d", year,month+1,day));

		proteinText = (TextView)findViewById(R.id.proteinText);
		carbohydrateText = (TextView)findViewById(R.id.carbohydrateText);
		lipidText = (TextView)findViewById(R.id.lipidText);
		energyText = (TextView)findViewById(R.id.energyText);


        // XMLで定義したandroid:idの値を指定してListViewを取得します。
        listView = (ListView) findViewById(R.id.list);

        // ListViewに表示する要素を保持するアダプタを生成します。
        arrayAdapter = new ArrayAdapter<MealRecord>(this,
                android.R.layout.simple_list_item_1);

        // アダプタを設定
        listView.setAdapter(arrayAdapter);
        
        // リスナの追加
        listView.setOnItemClickListener( this);
        
        
  
    }
	
	/**
	 * アクティビティが前面に来るたびにデータを更新
	 */
	@Override
	protected void onResume() {
	        super.onResume();

	        // データ取得タスクの実行
	        DataLoadTask task = new DataLoadTask();
	        task.execute();
	}

	/**
	 * 一覧データの取得と表示を行うタスク
	 */
	public class DataLoadTask extends AsyncTask<Object, Integer, List<MealRecord>> {
	        // 処理中ダイアログ
	        private ProgressDialog progressDialog = null;

	        @Override
	        protected void onPreExecute() {
	                // バックグラウンドの処理前にUIスレッドでダイアログ表示
	                progressDialog = new ProgressDialog(DailyMealListActivity.this);
	                progressDialog.setMessage(getResources().getText(
	                                R.string.data_loading));
	                progressDialog.setIndeterminate(true);
	                progressDialog.show();
	        }

	        @Override
	        protected List<MealRecord> doInBackground(Object... params) {
	                // 一覧データの取得をバックグラウンドで実行
	                dao = new MealRecordDao(DailyMealListActivity.this);
	                return dao.list(year,month,day,MealRecord.COLUMN_NTH);
	        }

	        @Override
	        protected void onPostExecute(List<MealRecord> result) {
	                // 処理中ダイアログをクローズ
	                progressDialog.dismiss();

	                // 表示データのクリア
	                arrayAdapter.clear();
	                Double proteinSum=0.0;
	                Double carbohydrateSum=0.0;
	                Double lipidSum=0.0;

	                // 表示データの設定
	                for (MealRecord record : result) {
	                	proteinSum += (record.getProtein()==null)? 0:record.getProtein();
	                	carbohydrateSum += (record.getCarbohydrate()==null)? 0:record.getCarbohydrate();
	                	lipidSum += (record.getLipid()==null) ? 0: record.getLipid();
	                	arrayAdapter.add(record);
	                }
	                energyText.setText(MealRecord.calcEnergy(proteinSum, carbohydrateSum, lipidSum)+" kcal ");
	                proteinText.setText("P " +proteinSum+" g");
	                carbohydrateText.setText("C " +carbohydrateSum+" g");
	                lipidText.setText("F " +lipidSum+" g");
	        }
	}
    /**
     * List要素クリック時の処理
     * 選択されたエンティティを詰めて参照画面へ遷移する
     */
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 選択された要素を取得する
        MealRecord record = (MealRecord)parent.getItemAtPosition( position);
        // 参照画面へ遷移する明示的インテントを生成
        Intent recordIntent = new Intent( this, RecordActivity.class);
        // 選択されたオブジェクトをインテントに詰める
        recordIntent.putExtra( MealRecord.TABLE_NAME, record);
        // アクティビティを開始する
        startActivity( recordIntent);
    }
    /**
     * オプションメニューの生成
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // XMLで定義したmenuを指定する。
        inflater.inflate(R.menu.daily_meal_list, menu);
        return true;
    }
    /**
     * オプションメニューの選択
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
        case R.id.menu_new:

			final Intent recordIntent = new Intent(DailyMealListActivity.this , RecordActivity.class);
			final MealRecord record = new MealRecord();
			record.setYear(year);
			record.setMonth(month);
			record.setDay(day);
			new TimePickerDialog(this, 2010,
					new TimePickerDialog.OnTimeSetListener() {

						@Override
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							record.setHour(hourOfDay);
							record.setMinute(minute);
							// オブジェクトをインテントに詰める
							recordIntent.putExtra(MealRecord.TABLE_NAME, record);
							startActivity(recordIntent);
						}
					}, MainActivity.calendar.get(Calendar.HOUR_OF_DAY), MainActivity.calendar.get(Calendar.MINUTE), true).show();

            break;
        }
        return true;
    };
}