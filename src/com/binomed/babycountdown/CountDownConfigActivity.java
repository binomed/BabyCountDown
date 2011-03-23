package com.binomed.babycountdown;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

public class CountDownConfigActivity extends Activity implements OnClickListener, OnDateSetListener {

	private static final String TAG = "CountDownConfigActivity";
	public static final String PREFS_NAME = "com.binomed.babycountdown.Widget";
	public static final String PREF_PREFIX_KEY = "prefix_";

	private static final int DATE_DIALOG_ID = 0;
	private int mAppWidgetId = 0;

	private TextView dateText;
	private Button btnDate;
	private Spinner spinnerColor;
	private Spinner spinnerTerm;
	private Button btnCancel;
	private Button btnValid;

	private int mYear, mMonth, mDay;
	private int spinnerColorId, spinnerTermId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set the result to CANCELED. This will cause the widget host to cancel
		// out of the widget placement if they press the back button.
		setResult(RESULT_CANCELED);

		setContentView(R.layout.config_layout);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		// If they gave us an intent without the widget id, just bail.
		if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			this.finish();
		}

		initViews();
		initViewsState();
		initListeners();
		display();
	}

	private void initViews() {

		dateText = (TextView) findViewById(R.id.idDate);
		btnDate = (Button) findViewById(R.id.idTxtDate);
		spinnerColor = (Spinner) findViewById(R.id.idColorSpinner);
		spinnerTerm = (Spinner) findViewById(R.id.idTermSpinner);
		btnCancel = (Button) findViewById(R.id.idCancel);
		btnValid = (Button) findViewById(R.id.idValid);

	}

	private void initViewsState() {
		ArrayAdapter<String> adapterColorSpinner = new ArrayAdapter<String>(this //
				, android.R.layout.simple_spinner_item//
				, getResources().getStringArray(R.array.colors)//
		);
		adapterColorSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerColor.setAdapter(adapterColorSpinner);

		ArrayAdapter<String> adapterTermSpinner = new ArrayAdapter<String>(this //
				, android.R.layout.simple_spinner_item//
				, getResources().getStringArray(R.array.terms)//
		);
		adapterTermSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerTerm.setAdapter(adapterTermSpinner);

		SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);

		String prefix_prefs = PREF_PREFIX_KEY + mAppWidgetId;
		long time = prefs.getLong(prefix_prefs + String.valueOf(R.id.idDate), -1l);
		Calendar calendar = Calendar.getInstance();
		spinnerColorId = prefs.getInt(prefix_prefs + String.valueOf(R.id.idColorSpinner), 0);
		spinnerTermId = prefs.getInt(prefix_prefs + String.valueOf(R.id.idTermSpinner), 0);
		if (time != -1) {
			calendar.setTimeInMillis(time);
		}
		mYear = calendar.get(Calendar.YEAR);
		mMonth = calendar.get(Calendar.MONTH);
		mDay = calendar.get(Calendar.DAY_OF_MONTH);
	}

	private void initListeners() {

		btnValid.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnDate.setOnClickListener(this);

	}

	private void display() {
		dateText.setText(new StringBuilder() //
				// Month is 0 based so add 1
				.append(mMonth + 1).append("-") //
				.append(mDay).append("-") //
				.append(mYear).append(" "));

		spinnerColor.setSelection(spinnerColorId);
		spinnerTerm.setSelection(spinnerTermId);

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, this, mYear, mMonth, mDay);
		default:
			break;
		}
		return null;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.idValid:

			SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
			Editor editor = prefs.edit();

			Calendar date = Calendar.getInstance();
			date.set(Calendar.DAY_OF_MONTH, mDay);
			date.set(Calendar.MONTH, mMonth);
			date.set(Calendar.YEAR, mYear);

			String prefix_prefs = PREF_PREFIX_KEY + mAppWidgetId;

			editor.putLong(prefix_prefs + String.valueOf(R.id.idDate), date.getTimeInMillis());
			editor.putInt(prefix_prefs + String.valueOf(R.id.idColorSpinner), spinnerColor.getSelectedItemPosition());
			editor.putInt(prefix_prefs + String.valueOf(R.id.idTermSpinner), spinnerTerm.getSelectedItemPosition());

			editor.commit();

			WidgetHelper.updateWidget(this, prefs, prefix_prefs, mAppWidgetId);

			// Make sure we pass back the original appWidgetId
			Intent resultValue = new Intent();
			resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
			this.setResult(this.RESULT_OK, resultValue);
			this.finish();
			break;
		case R.id.idCancel:
			this.finish();
			break;
		case R.id.idTxtDate:
			showDialog(DATE_DIALOG_ID);
			break;

		default:
			break;
		}
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		mDay = dayOfMonth;
		mMonth = monthOfYear;
		mYear = year;
		display();
	}
}
