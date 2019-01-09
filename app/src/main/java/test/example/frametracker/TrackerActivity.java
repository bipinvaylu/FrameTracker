package test.example.frametracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import kotlin.Unit;
import timber.log.Timber;


import com.priyankvasa.android.cameraviewex.CameraView;
import com.priyankvasa.android.cameraviewex.ErrorLevel;

import java.io.File;


public class TrackerActivity extends AppCompatActivity {

    private CameraView cameraView;
//    private FrameTracker frameTracker;

    private static final int REQUEST_VIDEO_PERMISSIONS = 1;

    private static final String[] VIDEO_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);
        cameraView = findViewById(R.id.cameraView);
        findViewById(R.id.startRecording).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraView.isVideoRecording()) {
                    stopVideo();
                    ((AppCompatButton) v).setText("Start Recording");
                } else {
                    startTakingVideo();
                    ((AppCompatButton) v).setText("Stop Recording");
                }
            }
        });
//        frameTracker = new FrameTracker(cameraView);

//        cameraView.addCameraListener(new CameraListener() {
//            @Override
//            public void onVideoTaken(VideoResult result) {
//                super.onVideoTaken(result);
//                String newVideoPath = result.getFile().getAbsolutePath();
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newVideoPath));
//                intent.setDataAndType(Uri.parse(newVideoPath), "video/mp4");
//                startActivity(intent);
//            }
//        });

        cameraView.addCameraErrorListener((Throwable t, ErrorLevel errorLevel) -> {
            Log.e(TrackerActivity.class.getCanonicalName(), "ERROR: level: " + errorLevel + ", message: " + t.getLocalizedMessage());
            return Unit.INSTANCE;
        });

        cameraView.setPreviewFrameListener((Image image) -> {
            Log.d(TrackerActivity.class.getCanonicalName(), "Image frame ....");
            return Unit.INSTANCE;
        });
    }

    private void startTracking(long delay) {
        Toast.makeText(this, "Tracking...", Toast.LENGTH_SHORT)
                .show();
//        frameTracker.startTracking();
    }

    private String getVideoFilePath() {
        File dir = new File(
                Environment.getExternalStorageDirectory() + "/tracker/");
        dir.mkdirs();
        return dir.getAbsolutePath();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!hasPermissionsGranted(VIDEO_PERMISSIONS)) {
            requestVideoPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
        startTracking(1L);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    200);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopVideo();
//        frameTracker.stopTracking();
        cameraView.stop(true);
    }

    public void stopVideo() {
        if (cameraView.isVideoRecording()) {
            cameraView.stopVideoRecording();
        }
    }

    @SuppressLint("MissingPermission")
    public void startTakingVideo() {
        stopVideo();
        Toast.makeText(this, "Recording...", Toast.LENGTH_SHORT)
                .show();
        cameraView.startVideoRecording(new File(getVideoFilePath(),
                    System.currentTimeMillis() + ".mp4"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200) {
            if (grantResults.length >= 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startTakingVideo();
            }
        }
    }

    private boolean shouldShowRequestPermissionRationale(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                return true;
            }
        }
        return false;
    }

    private void requestVideoPermissions() {
        if (shouldShowRequestPermissionRationale(VIDEO_PERMISSIONS)) {
            //
        } else {
            ActivityCompat.requestPermissions(this, VIDEO_PERMISSIONS, REQUEST_VIDEO_PERMISSIONS);
        }
    }


    private boolean hasPermissionsGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

}
