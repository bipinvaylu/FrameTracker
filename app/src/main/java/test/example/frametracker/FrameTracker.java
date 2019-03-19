package test.example.frametracker;

import android.util.Log;



/**
 * This class is providing the feature of tracking the person face in live camera preview.
 */
public class FrameTracker {

    private boolean isTracking = false;
//    private CameraView cameraView;

//    public FrameTracker(CameraView cameraView) {
//        this.cameraView = cameraView;
//    }

    public void startTracking() {
        Log.d("FrameTracker", "Start Tracking...");
//        cameraView.addFrameProcessor(new FrameProcessor() {
//            @Override
//            public void process(Frame frame) {
//                Log.d("FrameTracker", "Tracking frame running, Frame is null: " + (frame == null));
//            }
//        });

        isTracking = true;
    }

    public void stopTracking() {
        Log.d("FrameTracker", "Stop Tracking...");
        isTracking = false;
//        cameraView.clearFrameProcessors();
    }
}
