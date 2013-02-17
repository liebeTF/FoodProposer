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
        // �����������ꂽR.java�̒萔���w�肵��XML���烌�C�A�E�g�𐶐�
        setContentView(R.layout.food_list_diaog);
        parent = (LinearLayout)findViewById(R.id.parentLayout); 
        DataLoadTask task = new DataLoadTask();
        task.execute();
        
	}

	/**
	 * �ꗗ�f�[�^�̎擾�ƕ\�����s���^�X�N
	 */
	public class DataLoadTask extends AsyncTask<Object, Integer, List<FoodData>> {
		// �������_�C�A���O
		private ProgressDialog progressDialog = null;

		@Override
		protected void onPreExecute() {
			// �o�b�N�O���E���h�̏����O��UI�X���b�h�Ń_�C�A���O�\��
			progressDialog = new ProgressDialog(context);
			progressDialog.setMessage(context.getResources().getText(
					R.string.data_loading));
			progressDialog.setIndeterminate(true);
			progressDialog.show();
		}

		@Override
		protected List<FoodData> doInBackground(Object... params) {
			// �ꗗ�f�[�^�̎擾���o�b�N�O���E���h�Ŏ��s
			if(dao == null)
				dao = new FoodDataDao(context);
	        return dao.foodList(energyMax, date, mustImage);
		}

		@Override
		protected void onPostExecute(List<FoodData> result) {
			// �������_�C�A���O���N���[�Y
			progressDialog.dismiss();

	
			// �\���f�[�^�̃N���A
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
