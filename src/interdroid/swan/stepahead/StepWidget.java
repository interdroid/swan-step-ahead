package interdroid.swan.stepahead;

import interdroid.swan.ExpressionManager;
import interdroid.swan.swansong.Expression;
import interdroid.swan.swansong.ExpressionFactory;
import interdroid.swan.swansong.TimestampedValue;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class StepWidget extends AppWidgetProvider {

	private static final String ID = "StepWidget";

	private static int nrSteps = 0;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (ExpressionManager.ACTION_NEW_VALUES.equals(intent.getAction())) {
			Parcelable[] parcelables = (Parcelable[]) intent
					.getParcelableArrayExtra(ExpressionManager.EXTRA_NEW_VALUES);
			if (parcelables != null && parcelables.length == 1) {
				TimestampedValue value = (TimestampedValue) parcelables[0];
				nrSteps = (Integer) value.getValue();
				onUpdate(
						context,
						AppWidgetManager.getInstance(context),
						AppWidgetManager.getInstance(context).getAppWidgetIds(
								new ComponentName(context, StepWidget.class)));
			}

		}
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// There may be multiple widgets active, so update all of them
		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
		}
	}

	@Override
	public void onEnabled(Context context) {
		// Enter relevant functionality for when the first widget is created
		try {
			Expression expression = ExpressionFactory
					.parse("self@step:today?min_steps=10&min_time=10000");
			ExpressionManager.unregisterExpression(context, ID);
			ExpressionManager.registerExpression(context, ID, expression, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDisabled(Context context) {
		ExpressionManager.unregisterExpression(context, ID);
	}

	static void updateAppWidget(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId) {

		// Construct the RemoteViews object
		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.step_widget);
		views.setTextViewText(R.id.appwidget_text, nrSteps + " steps");

		// Instruct the widget manager to update the widget
		appWidgetManager.updateAppWidget(appWidgetId, views);
	}
}
