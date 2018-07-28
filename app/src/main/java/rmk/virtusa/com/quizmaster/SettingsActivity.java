package rmk.virtusa.com.quizmaster;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppActivity {

    @BindView(R.id.settingsThemeSwitch)
    Switch settingsThemeSwitch;

    @OnClick({R.id.settingsLogoutBtn})
    public void onClick(View v) {
        FirebaseAuth.getInstance().signOut();
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }

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
