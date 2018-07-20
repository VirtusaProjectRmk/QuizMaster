package rmk.virtusa.com.quizmaster;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppActivity {

    @BindView(R.id.settingsThemeSwitch)
    Switch settingsThemeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        SharedPreferences preferences = getSharedPreferences(getString(R.string.settings_pref_file), MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = preferences.edit();

        settingsThemeSwitch.setChecked(preferences.getBoolean(getString(R.string.settings_isDark), true));
        settingsThemeSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            preferencesEditor.putBoolean(getString(R.string.settings_isDark), b);
            preferencesEditor.apply();
            Toast.makeText(this, "Restart app to view changes", Toast.LENGTH_LONG).show();
        });
    }
}
