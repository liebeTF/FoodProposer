package com.example.foodlog;

import java.io.InputStream;

import com.example.foodlog.db.FoodData;
import com.example.foodlog.db.FoodDataDao;
import com.example.foodlog.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * �L�^�A�N�e�B�r�e�B
 */
public class FoodRegistActivity extends Activity {
	private static final int REQUEST_GALLERY = 0;
	
	// �Ώۂ�DailyRecord�I�u�W�F�N�g
	private FoodData food = null;
	FoodDataDao dao = new FoodDataDao( this);	
    
    // UI���i
    private Spinner kindSpinner = null;
	private ArrayAdapter<String> kindAdapter;
	private ArrayAdapter<String> unitAdapter;
    private TextView foodText = null;
    private Spinner unitSpinner = null;
    private EditText proteinText = null;
    private EditText carbohydrateText = null;
    private EditText lipidText = null;
    private SeekBar satisfactionBar = null;
    private ImageView foodImage = null;
    private Button setImageButton = null;

    
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.food_regist);

		// �C���e���g����I�u�W�F�N�g���擾
		Intent intent = getIntent();
		food = (FoodData)intent.getSerializableExtra( FoodData.TABLE_NAME);
		if(food==null){
			food = new FoodData();
		}
		

		/* UI���i�̎擾 */
		//���t
		kindSpinner = (Spinner)findViewById( R.id.kindSpinner);
		unitSpinner = (Spinner)findViewById( R.id.unitSpinner);
		foodText = (TextView)findViewById( R.id.foodText);

		proteinText = (EditText)findViewById( R.id.proteinText);
		proteinText.setInputType(InputType.TYPE_CLASS_NUMBER);

		carbohydrateText = (EditText) findViewById(R.id.carbohydrateText);
		carbohydrateText.setInputType(InputType.TYPE_CLASS_NUMBER);

		lipidText = (EditText) findViewById(R.id.lipidText);
		lipidText.setInputType(InputType.TYPE_CLASS_NUMBER);
		
		satisfactionBar = (SeekBar)findViewById(R.id.satisfactionBar);		
		
		foodImage = (ImageView)findViewById(R.id.foodImage);
		setImageButton = (Button)findViewById(R.id.setImageButton);
		setImageButton.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				// TODO �����������ꂽ���\�b�h�E�X�^�u
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intent, REQUEST_GALLERY);							
			}
		});
		kindAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item);
		kindSpinner.setAdapter(kindAdapter);
		kindSpinner.setOnItemSelectedListener(new OnItemSelectedListener());

		unitAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item);
		unitSpinner.setAdapter(unitAdapter);
		unitSpinner.setOnItemSelectedListener(new OnItemSelectedListener());
		
		setView();

	}

	/**
	 * �I�v�V�������j���[�̐���
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.food_regist, menu);
		return true;
	}
	
	/**
	 * ��ʂ̃N���A
	 */
	private void refleshView(){
		kindAdapter.clear();
		unitAdapter.clear();
		proteinText.setText(null);
		carbohydrateText.setText(null);
		lipidText.setText(null);
		foodImage.setImageResource(R.drawable.ic_launcher);
		setView();		
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
			food = new FoodData();
			refleshView();
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
					FoodDataDao dao = new FoodDataDao( FoodRegistActivity.this);
					dao.delete( food);					
					
					// ���b�Z�[�W�\��
					Toast toast = Toast.makeText(FoodRegistActivity.this, R.string.deleted, Toast.LENGTH_SHORT);
					toast.show();	

					Intent listIntent = new Intent(FoodRegistActivity.this,FoodListActivity.class);
					startActivity(listIntent);
				}
			});
			// �L�����Z���{�^���ݒ�
			builder.setNegativeButton( android.R.string.cancel, null);
			// �_�C�A���O�̕\��
			builder.show();
			break;
		case R.id.menu_copy:
			food = FoodData.copyFood(food);
			refleshView();
			break;
		}
		return true;
	}
	private void setView(){
		if (food != null) {
			// UI���i�ɒl��ݒ�
			kindAdapter.add("�I��K�{");
			Integer i = 1;
			kindSpinner.setSelection(0);			
			for (String kind: FoodData.kinds) {
				kindAdapter.add(kind);
				if(kind.equals(food.getKind())){
					kindSpinner.setSelection(i);
				}
				++i;
			}

			foodText.setText(food.getName());
			
			i = 1;
			unitAdapter.add("�I��K�{");
			unitSpinner.setSelection(0);			
			for (String unit: FoodData.units) {
				unitAdapter.add(unit);
				if(unit.equals(food.getUnit())){
					unitSpinner.setSelection(i);
				}
				++i;
			}

			
			if (food.getProtein() != null)
				proteinText.setText(String.valueOf(food.getProtein()));

			if (food.getCarbohydrate() != null)
				carbohydrateText.setText(String.valueOf(food.getCarbohydrate()));

			if (food.getLipid() != null)
				lipidText.setText(String.valueOf(food.getLipid()));

			if (food.getSatisfaction() != null)
				satisfactionBar.setProgress(food.getSatisfaction());

			if(food.getImage() != null)
				foodImage.setImageBitmap(food.getImageBitamp());

		} else {// food��null
			finish();
		}
		
	}
	
	//�ۑ�
	private Boolean menu_save(){
		if( food == null){
			return false;
		}
		String str;
		Double protein;
		Double carbohydrate;
		Double lipid;

	
		// ���̓`�F�b�N
		str = (String)kindSpinner.getSelectedItem();
		if(!food.setKind(str)){
			Toast.makeText(this,R.string.error_required,
					Toast.LENGTH_SHORT).show();
			return false;			
		}

		str = (String)unitSpinner.getSelectedItem();
		if(!food.setUnit(str)){
			Toast.makeText(this,R.string.error_required,
					Toast.LENGTH_SHORT).show();
			return false;			
		}
		
		str = foodText.getText().toString();
		if(!str.equals("")){
			food.setName(str);
		}else{
			Toast.makeText(this,R.string.error_required,
					Toast.LENGTH_SHORT).show();
			return false;
		}

		
		
		str = proteinText.getText().toString();
		if(!str.equals("")){
			try {
				protein = Double.valueOf(str);
				food.setProtein(protein);
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
				food.setCarbohydrate(carbohydrate);
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
				food.setLipid(lipid);
			} catch (NumberFormatException e) {
				lipidText.setText(null);
				Toast.makeText(this, R.string.error_required_number,
						Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		food.setSatisfaction(satisfactionBar.getProgress());

		if(food.getDate()==null)
			food.setDate(20000001);

		
		// �X�V����
		FoodData result = dao.save( food);
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
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
			try {
				InputStream in = getContentResolver().openInputStream(
						data.getData());
				Bitmap img = BitmapFactory.decodeStream(in);
				in.close();
				// �I�������摜��\��
				food.setImageBitmap(img);
				foodImage.setImageBitmap(food.getImageBitamp());
			} catch (Exception ex) {
				Log.d(ex.toString(), ex.getMessage());
			} finally {
			}
		}
	}
	
	private class OnItemSelectedListener implements AdapterView.OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			Spinner s = (Spinner)parent;
			String str =s.getSelectedItem().toString();
			switch (parent.getId()) {
			case R.id.unitSpinner:
				food.setUnit(str);						
				break;
			case R.id.kindSpinner:
				food.setKind(str);						
				break;
			default:
				break;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO �����������ꂽ���\�b�h�E�X�^�u
			
		}
		
	}
}


