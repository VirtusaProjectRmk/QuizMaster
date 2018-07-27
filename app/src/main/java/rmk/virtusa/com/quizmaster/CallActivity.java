package rmk.virtusa.com.quizmaster;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.EglBase;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CallActivity extends AppCompatActivity {

    @BindView(R.id.callVideoRenderer)
    SurfaceViewRenderer callVideoRenderer;

    @Nullable
    private VideoCapturer createCameraCapturer(CameraEnumerator cameraEnumerator) {
        final String cams[] = cameraEnumerator.getDeviceNames();
        for (String cam : cams) {
            if (cameraEnumerator.isBackFacing(cam)) {
                VideoCapturer videoCapturer = cameraEnumerator.createCapturer(cam, null);
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        for (String cam : cams) {
            if (!cameraEnumerator.isFrontFacing(cam)) {
                VideoCapturer videoCapturer = cameraEnumerator.createCapturer(cam, null);
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }
        return null;
    }

    private VideoCapturer createVideoCapturer() {
        VideoCapturer videoCapturer = createCameraCapturer(new Camera1Enumerator(false));
        if (videoCapturer == null) {
            Toast.makeText(this, "Failed to get cam", Toast.LENGTH_LONG).show();
            finish();
        }
        return videoCapturer;
    }

    void startCapture() {

        PeerConnectionFactory.InitializationOptions options = PeerConnectionFactory.InitializationOptions.builder(this).createInitializationOptions();
        PeerConnectionFactory.initialize(options);
        PeerConnectionFactory peerConnectionFactory = PeerConnectionFactory.builder().createPeerConnectionFactory();


        VideoCapturer videoCapturerAndroid = createVideoCapturer();
        MediaConstraints constraints = new MediaConstraints();

        VideoSource videoSource = peerConnectionFactory.createVideoSource(videoCapturerAndroid);
        VideoTrack localVideoTrack = peerConnectionFactory.createVideoTrack("100", videoSource);

        AudioSource audioSource = peerConnectionFactory.createAudioSource(constraints);
        AudioTrack localAudioTrack = peerConnectionFactory.createAudioTrack("101", audioSource);

        videoCapturerAndroid.startCapture(1000, 1000, 30);

        callVideoRenderer.setMirror(true);

        EglBase rootEglBase = EglBase.create();
        callVideoRenderer.init(rootEglBase.getEglBaseContext(), null);

        localVideoTrack.addSink(callVideoRenderer);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        ButterKnife.bind(this);
        startCapture();
    }
}
