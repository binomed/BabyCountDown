/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.binomed.babycountdown;

import java.util.Calendar;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.widget.RemoteViews;

/**
 * Helper methods to simplify talking with and parsing responses from a lightweight Wiktionary API. Before making any requests, you should call {@link #prepareUserAgent(Context)} to generate a User-Agent string based on your application package name and version.
 */
public class WidgetHelper {

	private static final String TAG = "WidgetHelper"; //$NON-NLS-1$

	/**
	 * Build a widget update to show the current Wiktionary "Word of the day." Will block until the online API returns.
	 */
	public static RemoteViews buildUpdate(Context context, SharedPreferences preferences, String prefix, int widgetId) {

		long time = preferences.getLong(prefix + String.valueOf(R.id.idDate), 0l);
		int colorIndex = preferences.getInt(prefix + String.valueOf(R.id.idColorSpinner), 0);
		int term = preferences.getInt(prefix + String.valueOf(R.id.idTermSpinner), 0);

		Calendar calendarStart = Calendar.getInstance();
		calendarStart.setTimeInMillis(time);
		Calendar calendarEnd = Calendar.getInstance();
		calendarEnd.setTimeInMillis(time);
		Calendar today = Calendar.getInstance();

		switch (term) {
		case 0:
			calendarEnd.add(Calendar.WEEK_OF_YEAR, 38);
			break;
		case 1:
			calendarEnd.add(Calendar.MONTH, 9);
			break;
		default:
			break;
		}

		long diffFromStart, diffFromToday;
		int monthToShow = 0;
		int weekToShow = 0;
		int dayToShow = 0;
		if (today.getTimeInMillis() < calendarEnd.getTimeInMillis() && today.getTimeInMillis() > calendarStart.getTimeInMillis()) {

			diffFromToday = calendarEnd.getTimeInMillis() - today.getTimeInMillis();
			diffFromStart = today.getTimeInMillis() - calendarStart.getTimeInMillis();

			Calendar dateFromToday = Calendar.getInstance();
			Calendar dateFromStart = Calendar.getInstance();
			dateFromToday.setTimeInMillis(diffFromToday);
			dateFromStart.setTimeInMillis(diffFromStart);

			monthToShow = dateFromStart.get(Calendar.MONTH);
			weekToShow = dateFromStart.get(Calendar.WEEK_OF_YEAR);
			dayToShow = dateFromToday.get(Calendar.DAY_OF_YEAR);
		}
		RemoteViews updateViews = null;
		updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

		// Gestion des mises à jour d'infos
		updateViews.setTextViewText(R.id.nbMonth, String.valueOf(monthToShow)); //$NON-NLS-1$
		updateViews.setTextViewText(R.id.nbWeek, String.valueOf(weekToShow)); //$NON-NLS-1$
		updateViews.setTextViewText(R.id.nbDay, String.valueOf(dayToShow)); //$NON-NLS-1$

		// Gestion des couleurs
		int color = Color.BLACK;
		switch (colorIndex) {
		case 0:
			color = Color.BLACK;
			break;
		case 1:
			color = Color.BLUE;
			break;
		case 2:
			color = Color.CYAN;
			break;
		case 3:
			color = Color.DKGRAY;
			break;
		case 4:
			color = Color.GRAY;
			break;
		case 5:
			color = Color.GREEN;
			break;
		case 6:
			color = Color.LTGRAY;
			break;
		case 7:
			color = Color.MAGENTA;
			break;
		case 8:
			color = Color.RED;
			break;
		case 9:
			color = Color.WHITE;
			break;
		case 10:
			color = Color.YELLOW;
			break;

		default:
			break;
		}
		updateViews.setTextColor(R.id.nbMonth, color); //$NON-NLS-1$
		updateViews.setTextColor(R.id.nbWeek, color); //$NON-NLS-1$
		updateViews.setTextColor(R.id.nbDay, color); //$NON-NLS-1$
		updateViews.setTextColor(R.id.txtNbMonth, color); //$NON-NLS-1$
		updateViews.setTextColor(R.id.txtOr, color); //$NON-NLS-1$
		updateViews.setTextColor(R.id.txtNbWeek, color); //$NON-NLS-1$
		updateViews.setTextColor(R.id.txtCountDown, color); //$NON-NLS-1$
		updateViews.setTextColor(R.id.txtNbDay, color); //$NON-NLS-1$

		Intent openConfigView = new Intent(context, CountDownConfigActivity.class);
		openConfigView.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
		PendingIntent pendingConfigIntent = PendingIntent.getActivity(context, 0, openConfigView, PendingIntent.FLAG_UPDATE_CURRENT);
		updateViews.setOnClickPendingIntent(R.id.widget, pendingConfigIntent);

		return updateViews;
	}

	public static void updateWidget(Context context, SharedPreferences preferences, String prefix, int widgetId) {
		RemoteViews updateViews = WidgetHelper.buildUpdate(context, preferences, prefix, widgetId);
		// Push update for this widget to the home screen
		ComponentName thisWidget = new ComponentName(context, Widget.class);
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		manager.updateAppWidget(thisWidget, updateViews);
	}

}
