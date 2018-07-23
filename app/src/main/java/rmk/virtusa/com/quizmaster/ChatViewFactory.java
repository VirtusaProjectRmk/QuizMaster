package rmk.virtusa.com.quizmaster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import rmk.virtusa.com.quizmaster.model.Chat;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static rmk.virtusa.com.quizmaster.adapter.ChatAdapter.CHAT_MEDIA_AUDIO;
import static rmk.virtusa.com.quizmaster.adapter.ChatAdapter.CHAT_MEDIA_DATE;
import static rmk.virtusa.com.quizmaster.adapter.ChatAdapter.CHAT_MEDIA_PHOTO;
import static rmk.virtusa.com.quizmaster.adapter.ChatAdapter.CHAT_MEDIA_VIDEO;

public class ChatViewFactory {

    public static View getChatView(ViewGroup root, Chat chat) {
        View view;
        Context context = root.getContext();
        if (chat.getIsMedia()) {
            switch (chat.getMediaType()) {
                //TODO implement chat option for audio and video
                case CHAT_MEDIA_AUDIO:
                    view = LayoutInflater.from(context).inflate(R.layout.chat_type_audio, root, false);
                    LinearLayout ll = (LinearLayout) view;
                    TextView audioTimerTextView = (TextView) ll.getChildAt(1);
                    audioTimerTextView.setText("02:33");
                    break;
                case CHAT_MEDIA_PHOTO:
                    view = LayoutInflater.from(context).inflate(R.layout.chat_type_photo, root, false);
                    ImageView imageView = (ImageView) view;
                    Glide.with(context)
                            .load(chat.getChat())
                            .transition(withCrossFade())
                            .into(imageView);
                    break;
                case CHAT_MEDIA_VIDEO:
                    view = LayoutInflater.from(context).inflate(R.layout.chat_type_video, root, false);
                    break;
                case CHAT_MEDIA_DATE:
                    view = LayoutInflater.from(context).inflate(R.layout.chat_type_date, root, false);
                    break;
                default:
                    //TODO handle corrupted chats
                    view = new View(context);
                    break;
            }
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.chat_type_text, null, false);
            TextView textView = (TextView) view;
            textView.setText(chat.getChat());
        }
        return view;
    }
}
