/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package can.example.app;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import can.example.app.R;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class CanPreferences extends PreferenceActivity {
	private ImageButton back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.set_preference);
		addPreferencesFromResource(R.xml.setting);

		back = (ImageButton) findViewById(R.id.back);
		// Baud rates
		final ListPreference baudrates = (ListPreference) findPreference("CANBAUDRATE");
		baudrates.setSummary(baudrates.getValue());
		baudrates
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						preference.setSummary((String) newValue);
						return true;
					}
				});

		// display format
		final ListPreference displayF = (ListPreference) findPreference("DisplayF");
		displayF.setSummary(displayF.getValue());
		displayF.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				preference.setSummary((String) newValue);
				return true;
			}
		});
		// auto clear
		final ListPreference autoclear = (ListPreference) findPreference("AutoClear");
		autoclear.setSummary(autoclear.getValue());
		autoclear
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						preference.setSummary((String) newValue);
						return true;
					}
				});

		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				CanPreferences.this.finish();
			}
		});
	}

}
