package interdroid.swan.stepahead;

import interdroid.swan.ExpressionManager;
import interdroid.swan.swansong.Expression;
import interdroid.swan.swansong.ExpressionFactory;
import interdroid.swan.swansong.TriState;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class StepAheadWidget extends AppWidgetProvider {

	private static final String ID = "StepAheadWidget";

	private static TriState state = TriState.UNDEFINED;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (ExpressionManager.ACTION_NEW_TRISTATE.equals(intent.getAction())) {
			state = TriState.valueOf(intent
					.getStringExtra(ExpressionManager.EXTRA_NEW_TRISTATE));
			onUpdate(
					context,
					AppWidgetManager.getInstance(context),
					AppWidgetManager.getInstance(context).getAppWidgetIds(
							new ComponentName(context, StepAheadWidget.class)));
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
					.parse("self@step:today?min_steps=1&min_time=10000 > other@step:today?min_steps=1&min_time=60000");
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
		switch (state) {
		case TRUE:
			views.setTextViewText(R.id.appwidget_text, "You are ahead!");
			break;
		case FALSE:
			views.setTextViewText(R.id.appwidget_text, "You are behind :(");
			break;
		case UNDEFINED:
			views.setTextViewText(R.id.appwidget_text, "Unknown...");
			break;
		}

		// Instruct the widget manager to update the widget
		appWidgetManager.updateAppWidget(appWidgetId, views);
	}
}
