package rmk.virtusa.com.quizmaster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class Felix {
    public static void show(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        View view = LayoutInflater.from(context).inflate(R.layout.ai_toast, null, false);
        TextView textView = view.findViewById(R.id.aiToastText);
        textView.setText(message);
        toast.setView(view);
        toast.show();
    }
}
