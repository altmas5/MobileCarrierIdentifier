package com.altmas5.mobilecarrieridentifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class Identificar extends Activity implements TextWatcher
{

	private static final int CONTACT_PICKER_RESULT = 1001;
	static final String DEBUG_TAG = "";
	EditText editText1;
	TextView textView2;
	Button button1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_identificar);
		
		textView2 = (TextView)findViewById(R.id.textView2);
		button1 = (Button)findViewById(R.id.button1);
		button1.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				textView2.setText(compareNum(editText1.getText().toString()));
				//finish();
			}
		}
		);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_identificar, menu);
		return true;
	}

	public void showContacts(View view){
		
		Intent contactPicked = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
		ImageButton ib = (ImageButton) findViewById(R.id.imageButton1);
		
		startActivityForResult(contactPicked, CONTACT_PICKER_RESULT);
		
		
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == RESULT_OK)
		{
			switch (requestCode){
			case CONTACT_PICKER_RESULT:
			Cursor cursor = null;
			String number = "";
			try {
					Uri result = data.getData();
					// Obtener ID de contacto
					String id = result.getLastPathSegment();
					// consultar numeros
					cursor = getContentResolver().query(Phone.CONTENT_URI,  
							null, Phone.CONTACT_ID + "=?", new String[] { id },  
							null);
					int numberIdx = cursor.getColumnIndex(Phone.NUMBER);

					if (cursor.moveToFirst()) 
					{  
						//recorrer todos los nums encontrados						
						number = cursor.getString(numberIdx);
						//removiendo los guiones
						number = number.replace("-","");
						number = number.replace(" ","");
						if(number.length()>8)
						 {
							Toast.makeText(getBaseContext(),"El número "+number+" excede los 8 dígitos",Toast.LENGTH_LONG).show();
						 }
						Log.v(DEBUG_TAG, "Obtuvo num : " + number);  
					} 
					else 
					{  
						Log.w(DEBUG_TAG, "Sin resultados");  
					}  
			} 
			catch (Exception e) 
			{
			 Log.e(DEBUG_TAG, "Falla al obtener la info", e);
			}
			finally
			{
				if (cursor != null){cursor.close();}
					editText1 = (EditText)findViewById(R.id.editText1);
					//deditText1.addTextChangedListener(this);
					editText1.setText(number);
	                
	            	
	                	if (number.length() == 0) 
	                		{  
	                		Toast.makeText(this, "No se ha encontrado num para contacto.",  
	                            Toast.LENGTH_LONG).show();  
	                		}
	                		else
	                		{
	                			Toast.makeText(getBaseContext(),"Num: "+number,Toast.LENGTH_LONG).show();
	                		}
			}
				break;
			}
		}
		else{
			Log.w(DEBUG_TAG, "Warning: resultado de la actividad no retorno OK"); 
		}

	}
	
	public void call(View view){
		
		
	}

	@Override
	public void afterTextChanged(Editable et) {

		//String op = compareNum(et.toString());
		//textView2.setText(op);
		
	}

	public String compareNum(String numb){
		String carrier = "No definido";
		
		//String movRegEx = "[89]{2}[5-9]{1}[0-9]{5}$";
		String movRegEx = "([83]{2}[[2]|[7-9]]{1}[0-9]{5}$)|" +
				"([8]{1}[4-5]{1}[5-8]{1}[0-9]{5}$)|" +
				"([86]{2}[7-8]{1}[0-9]{5}$)|" +
				"([87]{2}[5-9]{1}[0-9]{5}$)|" +
				"([880]{3}[2-9]{1}[0-9]{5}$)|" +
				"([88]{2}[[1]|[6]|[8]]{1}[0-9]{5}$)|" +
				"([887]{3}[1-9]{1}[0-9]{4})$|" +
				"([889]{3}[3-9]{1}[0-9]{4})$|" +
				"([89]{2}[5-9]{1}[0-9]{5}$)";
		Pattern pm = Pattern.compile(movRegEx);
		Matcher mm = pm.matcher(numb);
		
		String convRegEx = "[2]{1}[0-9]{7}$";
		Pattern pc = Pattern.compile(convRegEx);
		Matcher mc = pc.matcher(numb);
		
		String claroRegEx = "([83]{2}[[3]|[5]|[6]]{1}[0-9]{5}$)|" +
				"([84]{2}[[0-4]|[9]]{1}[0-9]{5}$)|" +
				"([85]{2}[0-3]{1}[0-9]{5}$)|" +
				"([86]{2}[[0-6]|[9]]{1}[0-9]{5}$)|" +
				"([873]{3}[0-2]{1}[0-9]{4}$)|" +
				"([874]{3}[1-9]{1}[0-9]{4}$)|" +
				"([87]{2}[0-2]{1}[0-9]{5}$)|" +
				"([88]{2}[2-5]{1}[0-9]{5}$)|" +
				"([89]{2}[0-4]{1}[0-9]{5}$)";
		
		Pattern p = Pattern.compile(claroRegEx);
		Matcher m = p.matcher(numb);
		
		try {

			if(mc.matches()){
				carrier = "Convencional";
			}
			else if(mm.matches())
			{
				carrier = "Movistar";
			}
			else if(m.matches())
			{
				carrier = "Claro";
			}
			
		} catch (Exception e) {
			Log.e(DEBUG_TAG,"Ha fallado");
		}
		
		return carrier;
	}
	
	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}
}
