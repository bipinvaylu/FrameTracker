package test.example.frametracker;

import android.support.annotation.NonNull;
import android.util.Log;

import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Frame;
import com.otaliastudios.cameraview.FrameProcessor;


/**
 * This class is providing the feature of tracking the person face in live camera preview.
 */
public class FrameTracker {

    private boolean isTracking = false;
    private CameraView cameraView;

    public FrameTracker(CameraView cameraView) {
        this.cameraView = cameraView;
    }

    public void startTracking() {
        Log.d("FrameTracker", "Start Tracking...");
        cameraView.addFrameProcessor(new FrameProcessor() {
            @Override
            public void process(@NonNull Frame frame) {
                Log.d("FrameTracker", "Tracking frame running");
                if(!isTracking) {
                    return;
                }
                if (frame.getData() == null) {
                    return;
                }
            }
        });
        isTracking = true;
    }

    public void stopTracking() {
        Log.d("FrameTracker", "Stop Tracking...");
        isTracking = false;
        cameraView.clearFrameProcessors();
    }
}
