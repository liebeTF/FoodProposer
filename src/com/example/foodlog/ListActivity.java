package com.example.foodlog;

import java.util.Calendar;
import java.util.List;

import com.example.foodlog.db.MealRecord;
import com.example.foodlog.db.MealRecordDao;
import com.example.foodlog.R;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.app.DatePickerDialog;
import android.widget.DatePicker;

/**
 * 一覧表示アクティビティ
 */
public class ListActivity extends Activity implements OnItemClickListener{
	static MealRecordDao dao;
	
	
    // 一覧表示用ListView
    private ListView listView = null;
    private ArrayAdapter<MealRecord> arrayAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 自動生成されたR.javaの定数を指定してXMLからレイアウトを生成
        setContentView(R.layout.list);

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
	                progressDialog = new ProgressDialog(ListActivity.this);
	                progressDialog.setMessage(getResources().getText(
	                                R.string.data_loading));
	                progressDialog.setIndeterminate(true);
	                progressDialog.show();
	        }

	        @Override
	        protected List<MealRecord> doInBackground(Object... params) {
	                // 一覧データの取得をバックグラウンドで実行
	                dao = new MealRecordDao(ListActivity.this);
	                return dao.list();
	        }

	        @Override
	        protected void onPostExecute(List<MealRecord> result) {
	                // 処理中ダイアログをクローズ
	                progressDialog.dismiss();

	                // 表示データのクリア
	                arrayAdapter.clear();

	                // 表示データの設定
	                for (MealRecord record : result) {
	                        arrayAdapter.add(record);
	                }
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
        inflater.inflate(R.menu.list, menu);
        return true;
    }
    /**
     * オプションメニューの選択
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
        case R.id.menu_search:

            // 入力画面へ遷移
            final Integer y= MainActivity.year;
            final Integer m= MainActivity.month;
            final Integer d= MainActivity.day;
            
            new DatePickerDialog(this, 2010,new DatePickerDialog.OnDateSetListener(){
				@Override
				public void onDateSet(DatePicker view, int year,
						int monthOfYear, int dayOfMonth) {
					Intent dailyIntent = new Intent(ListActivity.this , DailyMealListActivity.class);
//					MealRecord record = dao.load(year,monthOfYear,dayOfMonth);
//					if(record == null){
//						record = new MealRecord();
//						record.setYear(year);
//						record.setMonth(monthOfYear);
//						record.setDay(dayOfMonth);
//					}
			        // オブジェクトをインテントに詰める
			        dailyIntent.putExtra( MealRecord.COLUMN_YEAR, year);			
			        dailyIntent.putExtra( MealRecord.COLUMN_MONTH, monthOfYear);			
			        dailyIntent.putExtra( MealRecord.COLUMN_DAY, dayOfMonth);			
					startActivity( dailyIntent);
				}            	
            }
            ,y, m, d).show();            
            break;
        }
        return true;
    };
}