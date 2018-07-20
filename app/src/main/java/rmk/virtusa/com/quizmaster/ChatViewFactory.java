package rmk.virtusa.com.quizmaster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import rmk.virtusa.com.quizmaster.model.Chat;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ChatViewFactory {
    public static View getChatView(ViewGroup root, Chat chat) {
        View view;
        Context context = root.getContext();
        if (chat.getIsMedia()) {
            //TODO check if media is photo, audio or video
            view = LayoutInflater.from(context).inflate(R.layout.chat_type_photo, root, false);
            ImageView imageView = (ImageView) view;
            Glide.with(context).load(chat.getChat())
                    .transition(withCrossFade())
                    .into(imageView);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.chat_type_text, root, false);
            TextView textView = (TextView) view;
            textView.setText(chat.getChat());
        }
        return view;
    }
}
