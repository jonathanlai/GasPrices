package net.trajano.gasprices;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.Time;

/**
 * <p>
 * This is a adaptor that is used to manage the {@link SharedPreferences} used
 * by the application so that it shields the clients from knowing what the
 * actual properties are. This pattern was used for readability at the expense
 * of performance loss for creating the object.
 * </p>
 * <p>
 * A non-private {@link SharedPreferences} was used because we need it to be
 * accessed by a widget which is not running in the same user space as the
 * application therefore won't be able to get it by using
 * {@link Activity#getPreferences(int)}.
 * </p>
 * 
 * @author Archimedes Trajano (developer@trajano.net)
 * 
 */
public final class PreferenceAdaptor implements SharedPreferences {
	/**
	 * This is an internal class to adapt the editor to add a few methods to
	 * hide knowledge from clients what the keys are.
	 * 
	 * @author Archimedes Trajano (developer@trajano.net)
	 * 
	 */
	public static class Editor implements
			android.content.SharedPreferences.Editor {
		private final android.content.SharedPreferences.Editor editor;

		public Editor(final android.content.SharedPreferences.Editor editor) {
			this.editor = editor;
		}

		@Override
		public void apply() {
			editor.apply();
		}

		@Override
		public android.content.SharedPreferences.Editor clear() {
			return editor.clear();
		}

		@Override
		public boolean commit() {
			return editor.commit();
		}

		@Override
		public android.content.SharedPreferences.Editor putBoolean(
				final String key, final boolean value) {
			return editor.putBoolean(key, value);
		}

		@Override
		public android.content.SharedPreferences.Editor putFloat(
				final String key, final float value) {
			return editor.putFloat(key, value);
		}

		@Override
		public android.content.SharedPreferences.Editor putInt(
				final String key, final int value) {
			return editor.putInt(key, value);
		}

		@Override
		public android.content.SharedPreferences.Editor putLong(
				final String key, final long value) {
			return editor.putLong(key, value);
		}

		@Override
		public android.content.SharedPreferences.Editor putString(
				final String key, final String value) {
			return editor.putString(key, value);
		}

		@Override
		public android.content.SharedPreferences.Editor putStringSet(
				final String arg0, final Set<String> arg1) {
			return editor.putStringSet(arg0, arg1);
		}

		@Override
		public android.content.SharedPreferences.Editor remove(final String key) {
			return editor.remove(key);
		}

	}

	/**
	 * Last updated in seconds since epoch.
	 */
	private static final String LAST_UPDATED_KEY = "last_updated"; // $NON-NLS-1$

	/**
	 * {@link SharedPreferences} file name.
	 */
	private static final String SHARED_PREFERENCES_NAME = "gasprices.properties"; // $NON-NLS-1$

	/**
	 * Widget preference key prefix.
	 */
	private static final String WIDGET_PREFERENCE_KEY_PREFIX = "widget_"; // $NON-NLS-1$

	/**
	 * This returns the next update date given the last update data. There are
	 * three times for updates: 5pm, 8pm and midnight.
	 * 
	 * @param lastUpdate
	 *            last update time
	 * @return the next update time.
	 */
	public static Date nextUpdateDate(final Date lastUpdate) {
		return nextUpdateDate(lastUpdate.getTime());
	}

	/**
	 * This returns the next update date given the last update data. There are
	 * three times for updates: 5pm, 8pm and midnight.
	 * 
	 * @param lastUpdateTime
	 *            last update time as seconds since epoch.
	 * @return the next update time.
	 */
	public static Date nextUpdateDate(final long lastUpdateTime) {
		final Time t = new Time();
		t.set(lastUpdateTime);
		if (t.hour < 17) {
			t.hour = 17;
			t.minute = 0;
			t.second = 0;
		} else if (t.hour < 20) {
			t.hour = 20;
			t.minute = 0;
			t.second = 0;
		} else {
			t.monthDay += 1;
			t.hour = 0;
			t.minute = 0;
			t.second = 0;
		}
		return new Date(t.normalize(false));
	}

	/**
	 * This sets the #LAST_UPDATED_KEY preference to the current time.
	 * 
	 * TODO this does not seem right doing it here, this business logic should
	 * be moved to a separate wrapper. Perhaps I would need two versions of the
	 * wrapper, one mutable one not?
	 * 
	 * @param editor
	 */
	public static void setLastUpdatedToNow(
			final android.content.SharedPreferences.Editor editor) {
		editor.putLong(LAST_UPDATED_KEY, new Date().getTime());
	}

	/**
	 * Sets a widget preference.
	 * 
	 * @param editor
	 *            an editor
	 * @param widgetId
	 *            the widget ID
	 * @param preferenceData
	 *            preference data to store.
	 */
	public static void setWidgetPreference(final Editor editor,
			final int widgetId, final String preferenceData) {
		editor.putString(WIDGET_PREFERENCE_KEY_PREFIX, preferenceData);
	}

	/**
	 * This is the {@link SharedPreferences} object that is being wrapped.
	 */
	private final SharedPreferences preferences;

	/**
	 * Gets the {@link SharedPreferences} object used by the application as
	 * specified by {@link #SHARED_PREFERENCES_NAME}.
	 * 
	 * @param context
	 *            context.
	 */
	public PreferenceAdaptor(final Context context) {
		preferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME,
				Context.MODE_PRIVATE);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(final String key) {
		return preferences.contains(key);
	}

	/**
	 * This creates a new editor that wraps the original
	 * {@link android.content.SharedPreferences.Editor}.
	 */
	@Override
	public android.content.SharedPreferences.Editor edit() {
		return editor();
	}

	/**
	 * This creates a new editor that wraps the original
	 * {@link android.content.SharedPreferences.Editor}. It is a convenience
	 * method so that a cast is not needed.
	 */
	public Editor editor() {
		return new Editor(preferences.edit());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, ?> getAll() {
		return preferences.getAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getBoolean(final String key, final boolean defValue) {
		return preferences.getBoolean(key, defValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getFloat(final String key, final float defValue) {
		return preferences.getFloat(key, defValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getInt(final String key, final int defValue) {
		return preferences.getInt(key, defValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getLong(final String key, final long defValue) {
		return preferences.getLong(key, defValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getString(final String key, final String defValue) {
		return preferences.getString(key, defValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<String> getStringSet(final String arg0, final Set<String> arg1) {
		return preferences.getStringSet(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registerOnSharedPreferenceChangeListener(
			final OnSharedPreferenceChangeListener listener) {
		preferences.registerOnSharedPreferenceChangeListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void unregisterOnSharedPreferenceChangeListener(
			final OnSharedPreferenceChangeListener listener) {
		preferences.unregisterOnSharedPreferenceChangeListener(listener);
	}
}