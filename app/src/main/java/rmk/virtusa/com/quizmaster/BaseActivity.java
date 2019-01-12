package rmk.virtusa.com.quizmaster;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle args) {
        super.onCreate(args);
        SharedPreferences preferences = getSharedPreferences(getString(R.string.settings_pref_file), MODE_PRIVATE);
        boolean isDark = preferences.getBoolean(getString(R.string.settings_isDark), true);
        //setTheme(preferences.getString());
        if (isDark) {
            setTheme(R.style.AppTheme);
        } else {
            this.setTheme(R.style.AppTheme_Indigo);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

}
