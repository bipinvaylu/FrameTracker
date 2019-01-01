package test.example.frametracker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;

import java.io.File;


public class TrackerActivity extends AppCompatActivity {

    private CameraView cameraView;
    private FrameTracker frameTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);
        cameraView = findViewById(R.id.cameraView);
        frameTracker = new FrameTracker(cameraView);

        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onVideoTaken(File video) {
                super.onVideoTaken(video);
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
        cameraView.start();
        startTracking(1L);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    200);
        } else {
//            Uncomment this - It will stop frame processor
//            startCapturingVideo();
        }
    }

    public void startCapturingVideo() {
        if (cameraView.isCapturingVideo()) {
            cameraView.stopCapturingVideo();
        }
        Toast.makeText(this, "Recording...", Toast.LENGTH_SHORT)
                .show();
        cameraView.startCapturingVideo(
                new File(getVideoFilePath(),
                        System.currentTimeMillis() + ".mp4")
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200) {
            if (grantResults.length >= 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Uncomment this - It will stop frame processor
//                startCapturingVideo();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Uncomment this - It will stop frame processor
        cameraView.stopCapturingVideo();
        frameTracker.stopTracking();
        cameraView.stop();
    }

}
