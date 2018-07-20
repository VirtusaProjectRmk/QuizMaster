package rmk.virtusa.com.quizmaster;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class AppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle args) {
        super.onCreate(args);
        SharedPreferences preferences = getSharedPreferences(getString(R.string.settings_pref_file), MODE_PRIVATE);
        boolean isDark = preferences.getBoolean(getString(R.string.settings_isDark), true);
        //setTheme(preferences.getString());
        if (isDark) {
            setTheme(R.style.AppTheme);
        } else {
            this.setTheme(R.style.AppTheme_Light);
        }
    }
}
