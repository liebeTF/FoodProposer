package com.example.foodlog;

import java.util.List;

import com.example.foodlog.db.FoodDataDao;
import com.example.foodlog.db.FoodData;
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

/**
 * �ꗗ�\���A�N�e�B�r�e�B
 */
public class FoodListActivity extends Activity implements OnItemClickListener{
	static FoodDataDao dao;
	
	
    // �ꗗ�\���pListView
    private ListView listView = null;
    private ArrayAdapter<FoodData> arrayAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // �����������ꂽR.java�̒萔���w�肵��XML���烌�C�A�E�g�𐶐�
        setContentView(R.layout.food_list);

        // XML�Œ�`����android:id�̒l���w�肵��ListView���擾���܂��B
        listView = (ListView) findViewById(R.id.list);

        // ListView�ɕ\������v�f��ێ�����A�_�v�^�𐶐����܂��B
//        arrayAdapter = new ArrayAdapter<FoodData>(this,android.R.layout.simple_list_item_1);
        arrayAdapter = new ArrayAdapter<FoodData>(this,android.R.layout.simple_list_item_1);

        // �A�_�v�^��ݒ�
        listView.setAdapter(arrayAdapter);
        
        // ���X�i�̒ǉ�
        listView.setOnItemClickListener( this);
  
    }
	
	/**
	 * �A�N�e�B�r�e�B���O�ʂɗ��邽�тɃf�[�^���X�V
	 */
	@Override
	protected void onResume() {
	        super.onResume();

	        // �f�[�^�擾�^�X�N�̎��s
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
	                progressDialog = new ProgressDialog(FoodListActivity.this);
	                progressDialog.setMessage(getResources().getText(
	                                R.string.data_loading));
	                progressDialog.setIndeterminate(true);
	                progressDialog.show();
	        }
	
	        @Override
	        protected List<FoodData> doInBackground(Object... params) {
	            // �ꗗ�f�[�^�̎擾���o�b�N�O���E���h�Ŏ��s
	        	if(dao==null)
	                dao = new FoodDataDao(FoodListActivity.this);
	        	return dao.list();
	        }

	        @Override
	        protected void onPostExecute(List<FoodData> result) {
	                // �������_�C�A���O���N���[�Y
	                progressDialog.dismiss();

	                // �\���f�[�^�̃N���A
	                arrayAdapter.clear();

	                // �\���f�[�^�̐ݒ�
	                for (FoodData record : result) {
	                        arrayAdapter.add(record);
	                }
	        }
	}
    /**
     * List�v�f�N���b�N���̏���
     * �I�����ꂽ�G���e�B�e�B���l�߂ĎQ�Ɖ�ʂ֑J�ڂ���
     */
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // �I�����ꂽ�v�f���擾����
        FoodData data = (FoodData)parent.getItemAtPosition( position);
        Intent foodIntent = new Intent( this, FoodRegistActivity.class);
        foodIntent.putExtra(FoodData.TABLE_NAME, data);
		startActivity( foodIntent);   	
    }
    /**
     * �I�v�V�������j���[�̐���
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // XML�Œ�`����menu���w�肷��B
        inflater.inflate(R.menu.food_list, menu);
        return true;
    }
    /**
     * �I�v�V�������j���[�̑I��
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
        case R.id.menu_new:
       
            break;
        }
        return true;
    };
}