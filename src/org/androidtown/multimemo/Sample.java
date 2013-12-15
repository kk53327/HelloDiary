package org.androidtown.multimemo;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class Sample extends FragmentActivity {

	InputMethodManager imm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sample);
		
		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EditText et = (EditText)findViewById(R.id.et);
				String msg = et.getText().toString();
				
				imm.hideSoftInputFromInputMethod(et.getWindowToken(), 0);
				
				Intent intent = new Intent(Sample.this, Sample.class);
				intent.putExtra("msg", msg);
				startActivityForResult(intent, 0); // start activity
			}
		});
	}

	@Override
	protected  void onActivityResult(int requestCode, int resultCode, Intent data){
		if(resultCode == RESULT_OK){
			String result = data.getStringExtra("result"); // receive the data
			
			TextView tv2 = (TextView)findViewById(R.id.tv2);
			tv2.setText(result);
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
