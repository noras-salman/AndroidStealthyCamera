package com.nasable.stealthycamera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;


public class StealthyCamera {

    /**
     * @RequiresPermission(Manifest.permission.CAMERA)
     * @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
     */
    public StealthyCamera(Context context) {
        this.context = context;
    }

    public interface OnActionListener {
        public void onPictureTaken();

        public void onPictureTaken(byte[] data);

        public void onPictureTaken(Bitmap bitmap);

        public void onError(Exception e);
    }

    private int currentCameraId = -1;
    private static String cameraName = ""; // front or back
    private final String TAG = "StealthyCamera";
    private String directoryName = "StealthyCamera";
    public final static String CAMERA_NAME_FRONT = "FRONT";
    public final static String CAMERA_NAME_BACK = "BACK";
    private Context context;
    private boolean VERBOSE = false;
    private int IMAGE_COMPRESSION_RATIO = 70;
    private String IMAGE_EXTENSION = ".jpg";
    private boolean writeToFileOnFinish = true;

    /*Camera variables*/
    //a surface holder
    private SurfaceHolder sHolder;
    //a variable to control the camera
    private Camera mCamera;
    //the camera parameters
    private Camera.Parameters parameters;

    private OnActionListener onActionListener;

    public void setOnActionListener(OnActionListener onActionListener) {
        this.onActionListener = onActionListener;
    }

    /**
     * @param status Default value is true
     */
    public void setWriteToFileOnFinish(boolean status) {
        this.writeToFileOnFinish = status;
    }

    /**
     * @param ratio Default value is 70
     */
    public void setImageCompressionRatio(int ratio) {
        this.IMAGE_COMPRESSION_RATIO = ratio;
    }

    /**
     * @param dotExtension Default value is .jpg
     */
    public void setFileExtension(String dotExtension) {
        this.IMAGE_EXTENSION = dotExtension;
    }


    /**
     * @param directoryName Default value is StealthyCamera
     */
    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    /**
     * Verbose state shows toasts on certain actions
     *
     * @param verbose Default value is false
     */
    public void setVerbose(boolean verbose) {
        this.VERBOSE = verbose;
    }

    public boolean requiresPermissions(Activity activity) {
        return requiresWritePermission(activity) || requiresCameraPermission(activity);
    }

    public void requestPermissions(Activity activity) {
        String[] permissionList;
        if (writeToFileOnFinish)
            permissionList = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        else
            permissionList = new String[]{Manifest.permission.CAMERA};
        if (requiresPermissions(activity))
            ActivityCompat.requestPermissions(activity,
                    permissionList,
                    1);
    }

    private boolean requiresCameraPermission(Activity activity) {
        int permissionCheck = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA);
        return permissionCheck != PackageManager.PERMISSION_GRANTED && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;

    }

    private boolean requiresWritePermission(Activity activity) {
        int permissionCheck = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return writeToFileOnFinish && permissionCheck != PackageManager.PERMISSION_GRANTED && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;

    }

    private String getTimeStamp() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return timeStamp;
    }


    private String getFullDirectoryPath() {
        return Environment.getExternalStorageDirectory() + "/" + directoryName;
    }
    





 /*
    *  takeShot():
    *  ----------
    *  input -- >  currentCamera = (Camera ID to thake shot from)
    *
    *  Takes a photo using a surface view "hidden"
    * */


    public void takeShot() {
        mCamera = Camera.open(currentCameraId);
        Log.d(TAG, "Camera opend " + currentCameraId);
        SurfaceView sv = new SurfaceView(context);

        try {
            mCamera.setPreviewDisplay(sv.getHolder());
            parameters = mCamera.getParameters();

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                SurfaceTexture st = new SurfaceTexture(MODE_PRIVATE);
                mCamera.setPreviewTexture(st);
            }

            //set camera parameters
            mCamera.setParameters(parameters);
            mCamera.startPreview();
            mCamera.takePicture(null, null, mCall);

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Get a surface
        sHolder = sv.getHolder();
        //tells Android that this surface will have its data constantly replaced
        sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    //#######################################################
    //                  CAMERA METHODS
    //#######################################################

    /*
    * switchCamera():
    * --------------
    * Switch between front and back camera
    * */
    public void selectFrontCamera() {
        Camera.CameraInfo info = new Camera.CameraInfo();

        Log.d(TAG, "Number of cameras is " + Camera.getNumberOfCameras());
        if (Camera.getNumberOfCameras() == 1) {
            //Cant Switch Camera ..Only one available
            currentCameraId = info.facing;
            cameraName = "";
        } else {
            Log.d(TAG, "Current Camera " + info.facing);
            //swap the id of the camera to be used
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
            cameraName = CAMERA_NAME_FRONT;
            Log.d(TAG, "Switched to Front");
        }

    }

    public void selectBackCamera() {
        Camera.CameraInfo info = new Camera.CameraInfo();

        Log.d(TAG, "Number of cameras is " + Camera.getNumberOfCameras());
        if (Camera.getNumberOfCameras() == 1) {
            //Cant Switch Camera ..Only one available
            currentCameraId = info.facing;
            cameraName = "";
        } else {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
            cameraName = CAMERA_NAME_BACK;
            Log.d(TAG, "Switched to Back");

        }
    }

    //#######################################################
    //                  IMAGE/FILE METHODS
    //#######################################################

    /*
    *  CallBack when picture taken.
    *  ---------------------------
    *  Saves the image to sdCard
    * */
    Camera.PictureCallback mCall = new Camera.PictureCallback() {

        public void onPictureTaken(byte[] data, Camera camera) {

            if (data != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                if (bitmap != null) {


                    String directory = getFullDirectoryPath();

                    String file_name = directory + "/"
                            + getTimeStamp() + cameraName + IMAGE_EXTENSION;

                    File file = new File(directory);
                    if (!file.isDirectory() && writeToFileOnFinish) {
                        file.mkdir();
                    }


                    file = new File(file_name);


                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_COMPRESSION_RATIO, fileOutputStream);
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        mCamera.stopPreview();
                        mCamera.release();

                        // Comment this if you want everything hidden
                        if (VERBOSE)
                            Toast.makeText(context, "photo taken (" + file_name + ")", Toast.LENGTH_SHORT).show();



                        if (onActionListener != null) {
                            onActionListener.onPictureTaken();
                            onActionListener.onPictureTaken(bitmap);
                            onActionListener.onPictureTaken(data);
                        }
                    } catch (Exception exception) {
                        if (onActionListener != null)
                            onActionListener.onError(exception);
                        Log.d(TAG, "Exception");
                        exception.printStackTrace();
                    }
                }
            }
        }
    };


}
