package com.example.foodlog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.example.foodlog.db.DatabaseOpenHelper;
import com.example.foodlog.db.MealRecord;
import com.example.foodlog.db.MealRecordDao;
import com.example.foodlog.db.StatisticsRecord;
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
 * �ꗗ�\���A�N�e�B�r�e�B
 */
public class DailyMealListActivity extends Activity implements OnItemClickListener{
	static MealRecordDao dao;
	StatisticsRecord statistics = null;
	Integer year;
	Integer month;
	Integer day;
	private List<String> recorded = new ArrayList();;
	
	private TextView dateText;
	private TextView proteinText;
	private TextView carbohydrateText;
	private TextView lipidText;
	private TextView energyText;
	private Integer energyMax;
	private final Integer ENERGY_MAX = 2000;
	
	
    // �ꗗ�\���pListView
    private ListView listView = null;
    private ArrayAdapter<MealRecord> arrayAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // �����������ꂽR.java�̒萔���w�肵��XML���烌�C�A�E�g�𐶐�
        setContentView(R.layout.daily_meal_list);
        
		// �C���e���g����I�u�W�F�N�g���擾
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
		

		/*UI���i*/
		dateText = (TextView)findViewById(R.id.dateText);
		dateText.setText(String.format("%04d/%02d/%02d", year,month+1,day));

		proteinText = (TextView)findViewById(R.id.proteinText);
		carbohydrateText = (TextView)findViewById(R.id.carbohydrateText);
		lipidText = (TextView)findViewById(R.id.lipidText);
		energyText = (TextView)findViewById(R.id.energyText);


        // XML�Œ�`����android:id�̒l���w�肵��ListView���擾���܂��B
        listView = (ListView) findViewById(R.id.list);

        // ListView�ɕ\������v�f��ێ�����A�_�v�^�𐶐����܂��B
        arrayAdapter = new ArrayAdapter<MealRecord>(this,
                android.R.layout.simple_list_item_1);

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
	public class DataLoadTask extends AsyncTask<Object, Integer, List<MealRecord>> {
		// �������_�C�A���O
		private ProgressDialog progressDialog = null;

		@Override
		protected void onPreExecute() {
			// �o�b�N�O���E���h�̏����O��UI�X���b�h�Ń_�C�A���O�\��
			progressDialog = new ProgressDialog(DailyMealListActivity.this);
			progressDialog.setMessage(getResources().getText(
					R.string.data_loading));
			progressDialog.setIndeterminate(true);
			progressDialog.show();
		}

		@Override
		protected List<MealRecord> doInBackground(Object... params) {
			// �ꗗ�f�[�^�̎擾���o�b�N�O���E���h�Ŏ��s
			dao = new MealRecordDao(DailyMealListActivity.this);
			return dao.list(year, month, day, MealRecord.COLUMN_NTH);
		}

		@Override
		protected void onPostExecute(List<MealRecord> result) {
			// �������_�C�A���O���N���[�Y
			progressDialog.dismiss();

			// �\���f�[�^�̃N���A
			arrayAdapter.clear();
			recorded.clear();

			// �\���f�[�^�̐ݒ�
			statistics = dao
					.listToStatistics(result, StatisticsRecord.MODE_SUM);
			for (MealRecord record : result) {
				recorded.add(record.getNth().toString());
				arrayAdapter.add(record);
			}
			if (statistics != null) {
				energyText.setText(statistics.getEnergy() + " kcal ");
				energyMax = ENERGY_MAX -( (statistics.getEnergy() != null)? statistics.getEnergy() : 0);
				proteinText.setText("P " + statistics.getProtein() + " g");
				carbohydrateText.setText("C " + statistics.getCarbohydrate()
						+ " g");
				lipidText.setText("F " + statistics.getLipid() + " g");
			}
		}
	}
    /**
     * List�v�f�N���b�N���̏���
     * �I�����ꂽ�G���e�B�e�B���l�߂ĎQ�Ɖ�ʂ֑J�ڂ���
     */
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // �I�����ꂽ�v�f���擾����
        MealRecord record = (MealRecord)parent.getItemAtPosition( position);
        // �Q�Ɖ�ʂ֑J�ڂ��閾���I�C���e���g�𐶐�
        Intent recordIntent = new Intent( this, RecordActivity.class);
        // �I�����ꂽ�I�u�W�F�N�g���C���e���g�ɋl�߂�
        recordIntent.putExtra( MealRecord.TABLE_NAME, record);
        // �A�N�e�B�r�e�B���J�n����
        startActivity( recordIntent);
    }
    /**
     * �I�v�V�������j���[�̐���
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // XML�Œ�`����menu���w�肷��B
        inflater.inflate(R.menu.daily_meal_list, menu);
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
							// �I�u�W�F�N�g���C���e���g�ɋl�߂�
							String nth = DatabaseOpenHelper.join(recorded, ",")+"�H�ځ@�L�^�ς�";
							recordIntent.putExtra(MealRecord.TABLE_NAME, record);
							recordIntent.putExtra(MealRecord.COLUMN_NTH, nth);
							startActivity(recordIntent);
						}
					}, MainActivity.calendar.get(Calendar.HOUR_OF_DAY), MainActivity.calendar.get(Calendar.MINUTE), true).show();

            break;
        case R.id.menu_spare_meal:
        	Calendar cal = Calendar.getInstance();
        	cal.add(Calendar.DATE, 7);
        	Integer date = cal.get(Calendar.YEAR)*10000 + cal.get(Calendar.MONTH)*100 + cal.get(Calendar.DAY_OF_MONTH);
        	new FoodListDialog(this,energyMax,date,false).show();
        	break;
        }
        return true;
    };
}