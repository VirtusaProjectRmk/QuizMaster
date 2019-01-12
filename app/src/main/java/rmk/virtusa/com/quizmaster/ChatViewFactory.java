package rmk.virtusa.com.quizmaster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import rmk.virtusa.com.quizmaster.model.Message;

import static rmk.virtusa.com.quizmaster.adapter.MessageAdapter.CHAT_MEDIA_AUDIO;
import static rmk.virtusa.com.quizmaster.adapter.MessageAdapter.CHAT_MEDIA_DATE;
import static rmk.virtusa.com.quizmaster.adapter.MessageAdapter.CHAT_MEDIA_FILE;
import static rmk.virtusa.com.quizmaster.adapter.MessageAdapter.CHAT_MEDIA_PHOTO;
import static rmk.virtusa.com.quizmaster.adapter.MessageAdapter.CHAT_MEDIA_VIDEO;

public class ChatViewFactory {

    public static View getChatView(ViewGroup root, Message message) {
        View view;
        Context context = root.getContext();
        if (message.isMedia()) {
            switch (message.getMediaType()) {
                //TODO implement message option for audio and video
                case CHAT_MEDIA_AUDIO:
                    view = LayoutInflater.from(context).inflate(R.layout.message_type_audio, root, false);
                    LinearLayout ll = (LinearLayout) view;
                    TextView audioTimerTextView = (TextView) ll.getChildAt(1);
                    //TODO remove
                    audioTimerTextView.setText("02:33");
                    break;
                case CHAT_MEDIA_PHOTO:
                    view = LayoutInflater.from(context).inflate(R.layout.message_type_photo, root, false);
                    ImageView imageView = (ImageView) view;
                    if (message.getChat().isEmpty()) break;
                    Picasso.get()
                            .load(message.getChat())
                            .error(R.drawable.default_user)
                            .into(imageView);
                    break;
                case CHAT_MEDIA_VIDEO:
                    view = LayoutInflater.from(context).inflate(R.layout.message_type_video, root, false);
                    break;
                case CHAT_MEDIA_DATE:
                    view = LayoutInflater.from(context).inflate(R.layout.message_type_date, root, false);
                    break;
                case CHAT_MEDIA_FILE:
                    view = LayoutInflater.from(context).inflate(R.layout.message_type_file, root, false);
                    break;
                default:
                    //TODO handle corrupted chats
                    view = new View(context);
                    break;
            }
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.message_type_text, root, false);
            TextView textView = (TextView) view;
            textView.setText(message.getChat());
        }
        return view;
    }
}
