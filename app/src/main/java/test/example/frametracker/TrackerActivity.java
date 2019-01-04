package test.example.frametracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraLogger;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.VideoResult;

import java.io.File;


public class TrackerActivity extends AppCompatActivity {

    private CameraView cameraView;
    private FrameTracker frameTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);
        cameraView = findViewById(R.id.cameraView);
        CameraLogger.setLogLevel(CameraLogger.LEVEL_VERBOSE);
        CameraLogger.registerLogger(new CameraLogger.Logger() {
            @Override
            public void log(int level, String tag, String message, Throwable throwable) {
                Log.d(tag, "CameraLogger - " + message);
            }
        });
        findViewById(R.id.startRecording).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraView.isTakingVideo()) {
                    stopVideo();
                    ((AppCompatButton) v).setText("Start Recording");
                } else {
                    startTakingVideo();
                    ((AppCompatButton) v).setText("Stop Recording");
                }
            }
        });
        frameTracker = new FrameTracker(cameraView);

        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onVideoTaken(VideoResult result) {
                super.onVideoTaken(result);
                String newVideoPath = result.getFile().getAbsolutePath();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newVideoPath));
                intent.setDataAndType(Uri.parse(newVideoPath), "video/mp4");
                startActivity(intent);
            }
        });

    }

    private void startTracking(long delay) {
        Toast.makeText(this, "Tracking...", Toast.LENGTH_SHORT)
                .show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                frameTracker.startTracking();
            }
        }, delay);
    }

    private String getVideoFilePath() {
        File dir = new File(
                Environment.getExternalStorageDirectory() + "/tracker/");
        dir.mkdirs();
        return dir.getAbsolutePath();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.open();
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
        frameTracker.stopTracking();
        cameraView.close();
    }

    public void stopVideo() {
        if (cameraView.isTakingVideo()) {
            cameraView.stopVideo();
        }
    }
    public void startTakingVideo() {
        stopVideo();
        Toast.makeText(this, "Recording...", Toast.LENGTH_SHORT)
                .show();
        cameraView.takeVideoSnapshot(new File(getVideoFilePath(),
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

}
