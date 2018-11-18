package rmk.virtusa.com.quizmaster;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class AIToast {
    static Toast makeText(Context context, String message, int type) {
        @SuppressLint("ShowToast")
        Toast toast = Toast.makeText(context, message, type);
        View view = LayoutInflater.from(context).inflate(R.layout.ai_toast, null, false);
        TextView textView = view.findViewById(R.id.aiToastText);
        textView.setText(message);

        toast.setView(view);
        return toast;
    }
}
