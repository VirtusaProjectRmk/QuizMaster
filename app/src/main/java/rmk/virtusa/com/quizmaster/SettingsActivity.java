package rmk.virtusa.com.quizmaster;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppActivity {

    @BindView(R.id.settingsThemeSwitch)
    Switch settingsThemeSwitch;

    SharedPreferences preferences;
    SharedPreferences.Editor preferencesEditor;
    @BindView(R.id.settingsAboutText)
    TextView settingsAboutText;

    @OnClick({R.id.settingsLogoutBtn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settingsLogoutBtn:
                FirebaseAuth.getInstance().signOut();
                preferencesEditor.remove(getString(R.string.settings_isAdmin)).commit();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            settingsAboutText.setText("Quiz Master v" + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        preferences = getSharedPreferences(getString(R.string.settings_pref_file), MODE_PRIVATE);
        preferencesEditor = preferences.edit();

        settingsThemeSwitch.setChecked(preferences.getBoolean(getString(R.string.settings_isDark), true));
        settingsThemeSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            preferencesEditor.putBoolean(getString(R.string.settings_isDark), b);
            preferencesEditor.apply();
            Toast.makeText(this, "Restart app to view changes", Toast.LENGTH_LONG).show();
        });
    }
}
