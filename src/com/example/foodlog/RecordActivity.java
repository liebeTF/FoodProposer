package com.example.foodlog;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.zip.Inflater;

import com.example.foodlog.db.MealRecord;
import com.example.foodlog.db.MealRecordDao;
import com.example.foodlog.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * �L�^�A�N�e�B�r�e�B
 */
public class RecordActivity extends Activity {
	// �Ώۂ�DailyRecord�I�u�W�F�N�g
	private MealRecord record = null;
	MealRecordDao dao = new MealRecordDao( this);
	String recordNth = null;
    
    // UI���i
    private TextView dateText = null;
    private TextView timeText = null;
    private TextView satiety1Text = null;
    private TextView satiety2Text = null;
    private EditText nthText = null;
    private EditText mealTimeText = null;
    private EditText memberText = null;
    private EditText proteinText = null;
    private EditText carbohydrateText = null;
    private EditText lipidText = null;
    private TextView energyText = null;
    private Button calCalButton = null;
    private TextView nthRecordedText = null;

    
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record);

		// �C���e���g����I�u�W�F�N�g���擾
		Intent intent = getIntent();
		record = (MealRecord)intent.getSerializableExtra( MealRecord.TABLE_NAME);
		recordNth = (String)intent.getSerializableExtra( MealRecord.COLUMN_NTH);
		if(record==null){
			record = new MealRecord();
		}
		if(record.getYear()==null){
			record.setYear(MainActivity.calendar.get(Calendar.YEAR));
			record.setMonth(MainActivity.calendar.get(Calendar.MONTH));
			record.setDay(MainActivity.calendar.get(Calendar.DAY_OF_MONTH));
		}
		if(record.getHour()==null){
			record.setHour(MainActivity.calendar.get(Calendar.HOUR_OF_DAY));
			record.setMinute(MainActivity.calendar.get(Calendar.MINUTE));
		}
		

		/* UI���i�̎擾 */
		//���t
		dateText = (TextView)findViewById( R.id.dateText);
		dateText.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				new DatePickerDialog(RecordActivity.this, 2010,
						new DatePickerDialog.OnDateSetListener(){
					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						record.setYear(year);
						record.setMonth(monthOfYear);
						record.setDay(dayOfMonth);
						dateText.setText(record.getDate());
					}
				}
	            ,record.getYear(), record.getMonth(), record.getDay())
				.show();            
			}
		});
		timeText = (TextView)findViewById( R.id.timeText);
		timeText.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				new TimePickerDialog(RecordActivity.this, 2010,
						new TimePickerDialog.OnTimeSetListener() {
							
							@Override
							public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
								record.setHour(hourOfDay);
								record.setMinute(minute);
								timeText.setText(record.getTime());
								}
							}
	            ,record.getHour(), record.getMinute(),true)
				.show();            
			}
		});

		satiety1Text = (TextView)findViewById( R.id.satiety1Text);
		satiety1Text.setOnClickListener(new SatietyOnClickListner());
		satiety2Text = (TextView)findViewById( R.id.satiety2Text);
		satiety2Text.setOnClickListener(new SatietyOnClickListner());
		
		nthRecordedText = (TextView)findViewById(R.id.nthRecordedText);
		nthRecordedText.setText(recordNth);
		nthText = (EditText)findViewById( R.id.nthText);
		mealTimeText = (EditText)findViewById( R.id.mealTimeText);
		memberText = (EditText)findViewById( R.id.memberText);

		proteinText = (EditText)findViewById( R.id.proteinText);
		proteinText.setInputType(InputType.TYPE_CLASS_NUMBER);
		
		carbohydrateText = (EditText)findViewById( R.id.carbohydrateText);
		carbohydrateText.setInputType(InputType.TYPE_CLASS_NUMBER);
		
		lipidText = (EditText)findViewById( R.id.lipidText);
		lipidText.setInputType(InputType.TYPE_CLASS_NUMBER);

		energyText = (TextView)findViewById( R.id.energyText);
//		energyText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		calCalButton = (Button)findViewById(R.id.calCalButton);
		calCalButton.setOnClickListener(new ViewGroup.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO �����������ꂽ���\�b�h�E�X�^�u
				Integer energy;
				Double p,f,c;
				try{
				p = Double.valueOf(proteinText.getText().toString());
				}catch (NumberFormatException ex){
					p = 0.0;
				}
				try{
					f = Double.valueOf(lipidText.getText().toString());
				}catch (NumberFormatException ex){
					f = 0.0;
				}
				try{
					c = Double.valueOf(carbohydrateText.getText().toString());
				}catch (NumberFormatException ex){
					c = 0.0;
				}
				energy = MealRecord.calcEnergy(p,c,f);
				if(energy==0){
					energyText.setText(R.string.uncalculated);
					record.setEnergy(null);
					Toast.makeText(RecordActivity.this, "�v�Z���ʂ�0�ł�",
							Toast.LENGTH_SHORT).show();					
				}else{
					record.setEnergy(energy);
					energyText.setText(String.valueOf(energy)+" kcal");
					proteinText.setText(String.valueOf(p));
					lipidText.setText(String.valueOf(f));
					carbohydrateText.setText(String.valueOf(c));
				}
			}
		});
		
		if( record != null){
			// UI���i�ɒl��ݒ�
			dateText.setText( record.getDate());
			timeText.setText(record.getTime());
			if(record.getProtein() != null)
				proteinText.setText(String.valueOf(record.getProtein()));

			if(record.getCarbohydrate() != null)
				carbohydrateText.setText(String.valueOf(record.getCarbohydrate()));

			if(record.getLipid() != null)
				lipidText.setText(String.valueOf(record.getLipid()));

			if(record.getEnergy() != null)
				energyText.setText(String.valueOf(record.getEnergy() +" kcal"));

			if(record.getNth() != null)
				nthText.setText(String.valueOf(record.getNth()));

			if(record.getMealtime() != null)
				mealTimeText.setText(String.valueOf(record.getMealtime()));

			if(record.getMember() != null)
				memberText.setText(String.valueOf(record.getMember()));

			if(record.getSatiety1() != null)
				satiety1Text.setText(R.string.recorded);

			if(record.getSatiety2() != null)
				satiety2Text.setText(R.string.recorded);
			
		}else{//record��null
			finish();
		}
		
	}

	/**
	 * �I�v�V�������j���[�̐���
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.record, menu);
		return true;
	}
	
	/**
	 * ��ʂ̃N���A
	 */
	private void clear(){
		record = null;
		dateText.setText( null);
		proteinText.setText(null);
		carbohydrateText.setText(null);
		lipidText.setText(null);
		energyText.setText(null);
		
	}

	/**
	 * �I�v�V�������j���[�̑I��
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
			// �V�K
		case R.id.menu_new:
			clear();
			break;
			// �ۑ�
		case R.id.menu_save:
			
			if(menu_save()){
				// �ۑ����ɏI�����A�O�̃A�N�e�B�r�e�B�֖߂�
				setResult( RESULT_OK);
				finish();
			}else{
				return false;
			}
			break;
			// �폜
		case R.id.menu_delete:
			// �m�F�_�C�A���O�̕\��
			AlertDialog.Builder builder = new AlertDialog.Builder( this);
			// �A�C�R���ݒ�
			builder.setIcon(android.R.drawable.ic_dialog_alert);
			// �^�C�g���ݒ�
			builder.setTitle( R.string.confirm_delete);
			// OK�{�^���ݒ�
			builder.setPositiveButton( android.R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// �폜����
					MealRecordDao dao = new MealRecordDao( RecordActivity.this);
					dao.delete( record);
					
					// ��ʂ̍X�V
					clear();
					
					// ���b�Z�[�W�\��
					Toast toast = Toast.makeText(RecordActivity.this, R.string.deleted, Toast.LENGTH_SHORT);
					toast.show();
					
//					Intent listIntent = new Intent(RecordActivity.this,ListActivity.class);
//					startActivity(listIntent);
					finish();
				}
			});
			// �L�����Z���{�^���ݒ�
			builder.setNegativeButton( android.R.string.cancel, null);
			// �_�C�A���O�̕\��
			builder.show();
			break;
		}
		return true;
	}
	
	private Boolean menu_save(){
		if( record == null){
			return false;
		}
		String str;
		Integer nth;//�K�{
		Integer mealtime;//�K�{
		Integer member;//default 1
		Double protein;
		Double carbohydrate;
		Double lipid;

	
		// ���̓`�F�b�N
		str = nthText.getText().toString();
		if(!str.equals("")){
			try {
				nth = Integer.valueOf(str);
				record.setNth(nth);
			} catch (NumberFormatException e) {
				nthText.setText(null);
				Toast.makeText(this, R.string.error_required_number,
						Toast.LENGTH_SHORT).show();
				return false;
			}
		}else{
			Toast.makeText(this,R.string.error_required,
					Toast.LENGTH_SHORT).show();
			return false;
		}
		str = mealTimeText.getText().toString();		
		if(!str.equals("")){
			try {
				mealtime = Integer.valueOf(str);
				record.setMealtime(mealtime);
			} catch (NumberFormatException e) {
				nthText.setText(null);
				Toast.makeText(this, R.string.error_required_number,
						Toast.LENGTH_SHORT).show();
				return false;
			}
		}else{
			Toast.makeText(this,R.string.error_required,
					Toast.LENGTH_SHORT).show();
			return false;
		}
		
		
		str = memberText.getText().toString();		
		if(!str.equals("")){
			try {
				member = Integer.valueOf(str);
				record.setMember(member);
			} catch (NumberFormatException e) {
				nthText.setText(null);
				Toast.makeText(this, R.string.error_required_number,
						Toast.LENGTH_SHORT).show();
				return false;
			}
		}

		
		
		str = proteinText.getText().toString();
		if(!str.equals("")){
			try {
				protein = Double.valueOf(str);
				record.setProtein(protein);
			} catch (NumberFormatException e) {
				proteinText.setText(null);
				Toast.makeText(this, R.string.error_required_number,
						Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		
		str = carbohydrateText.getText().toString();
		if(!str.equals("")){
			try {
				carbohydrate = Double.valueOf(str);
				record.setCarbohydrate(carbohydrate);
			} catch (NumberFormatException e) {
				carbohydrateText.setText(null);
				Toast.makeText(this, R.string.error_required_number,
						Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		str = lipidText.getText().toString();
		if(!str.equals("")){
			try {
				lipid = Double.valueOf(str);
				record.setLipid(lipid);
			} catch (NumberFormatException e) {
				lipidText.setText(null);
				Toast.makeText(this, R.string.error_required_number,
						Toast.LENGTH_SHORT).show();
				return false;
			}
		}			

		
		// �X�V����
		MealRecord result = dao.save( record);
		if(result!=null){
			// ���b�Z�[�W�\��
			Toast toast = Toast.makeText(this, R.string.saved,
					Toast.LENGTH_SHORT);
			toast.show();
			return true;
		}else{
			// ���b�Z�[�W�\��
			Toast toast = Toast.makeText(this, "�ۑ����s�I",
					Toast.LENGTH_SHORT);
			toast.show();
			return false;
		}
	}
	
	private class SatietyOnClickListner implements View.OnClickListener{
		
		

		@Override
		public void onClick(View v) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			LayoutInflater inflater = LayoutInflater.from(RecordActivity.this);
			View actionAlertView = inflater.inflate(R.layout.satiety_seekbar,
								(ViewGroup)findViewById(R.id.satietySeekBarRoot));
			final SeekBar sb = (SeekBar)actionAlertView.findViewById(R.id.satietySeekBar);
			Integer satiety = null;
			Integer id = v.getId();			
			AlertDialog.Builder adb = new AlertDialog.Builder(RecordActivity.this);
			switch(id){
			case R.id.satiety1Text:
				satiety = record.getSatiety1();
				adb.setTitle("�H�O");
				adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO �����������ꂽ���\�b�h�E�X�^�u						
						record.setSatiety1(sb.getProgress());
						satiety1Text.setText(R.string.recorded);
					}
				});
				adb.setNeutralButton("�L�^�폜", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO �����������ꂽ���\�b�h�E�X�^�u						
						record.setSatiety1(null);
						satiety1Text.setText(R.string.satiety1);
					}
				});
				break;
			case R.id.satiety2Text:
				satiety = record.getSatiety2();
				adb.setTitle("�H��");
				adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO �����������ꂽ���\�b�h�E�X�^�u						
						record.setSatiety2(sb.getProgress());
						satiety2Text.setText(R.string.recorded);
					}
				});
				adb.setNeutralButton("�L�^�폜", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO �����������ꂽ���\�b�h�E�X�^�u						
						record.setSatiety2(null);
						satiety2Text.setText(R.string.satiety2);
					}
				});
				break;
			default:
				break;
			}
			if(satiety == null){
				sb.setProgress(50);
			}else{
				sb.setProgress(satiety);
			}
			adb.setView(actionAlertView);
			adb.show();
		}		
	}

	
}


