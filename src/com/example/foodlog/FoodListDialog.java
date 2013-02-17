package com.example.foodlog;

import java.util.List;

import com.example.foodlog.db.FoodData;
import com.example.foodlog.db.FoodDataDao;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FoodListDialog extends Dialog {
	private LinearLayout parent;
	private Context context;
    FoodDataDao dao;
	Integer energyMax = null;
	Integer date;
	Boolean mustImage;
	
	public FoodListDialog(Context context,Integer energyMax, Integer date, Boolean mustImage) {
		super(context);
		this.context = context;
		this.energyMax = energyMax;
		this.date = date;
		this.mustImage = mustImage;
        // 自動生成されたR.javaの定数を指定してXMLからレイアウトを生成
        setContentView(R.layout.food_list_diaog);
        parent = (LinearLayout)findViewById(R.id.parentLayout); 
        DataLoadTask task = new DataLoadTask();
        task.execute();
        
	}

	/**
	 * 一覧データの取得と表示を行うタスク
	 */
	public class DataLoadTask extends AsyncTask<Object, Integer, List<FoodData>> {
		// 処理中ダイアログ
		private ProgressDialog progressDialog = null;

		@Override
		protected void onPreExecute() {
			// バックグラウンドの処理前にUIスレッドでダイアログ表示
			progressDialog = new ProgressDialog(context);
			progressDialog.setMessage(context.getResources().getText(
					R.string.data_loading));
			progressDialog.setIndeterminate(true);
			progressDialog.show();
		}

		@Override
		protected List<FoodData> doInBackground(Object... params) {
			// 一覧データの取得をバックグラウンドで実行
			if(dao == null)
				dao = new FoodDataDao(context);
	        return dao.foodList(energyMax, date, mustImage);
		}

		@Override
		protected void onPostExecute(List<FoodData> result) {
			// 処理中ダイアログをクローズ
			progressDialog.dismiss();

	
			// 表示データのクリア
			parent.removeAllViews();
			for(FoodData data:result){
	        	if(data.getImage()!=null){
	        		ImageView image = new ImageView(context);
	        		image.setImageBitmap(data.getImageBitamp());
	        		parent.addView(image);
	        	}else{
	        		TextView text = new TextView(context);
	        		text.setTextSize(3.0f);
	        		text.setText(data.getName());
	        		parent.addView(text);
	        	}
	        }

		}
	}
	
}
