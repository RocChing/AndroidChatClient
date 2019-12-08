package com.roc.chatclient.ui;

import androidx.appcompat.app.AppCompatActivity;

import com.roc.chatclient.R;
import com.roc.chatclient.widget.ARVideoView;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.ar.rtmpc_hybrid.ARRtmpcEngine;
import org.webrtc.VideoRenderer;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class VideoCallActivity extends AppCompatActivity {

    private final static int ID_RTMP_PUSH_START = 100;
    private final static int ID_RTMP_PUSH_EXIT = 101;

    private final int WIDTH_DEF = 480;
    private final int HEIGHT_DEF = 640;
    private final int FRAMERATE_DEF = 20;
    private final int BITRATE_DEF = 800 * 1000;

    private final int SAMPLE_RATE_DEF = 22050;
    private final int CHANNEL_NUMBER_DEF = 2;

    private Camera _mCamera = null;
    private boolean _bIsFront = true;
    //    private SWVideoEncoder _swEncH264 = null;
    private int _iDegrees = 0;

    private int _iRecorderBufferSize = 0;

    private DataOutputStream _outputStream = null;

    private AudioRecord _AudioRecorder = null;
    private byte[] _RecorderBuffer = null;
//    private FdkAacEncode _fdkaacEnc = null;
    private int _fdkaacHandle = 0;
    private Thread _h264EncoderThread = null;
    private Thread _AacEncoderThread = null;

    private Button _SwitchCameraBtn = null;

    private boolean _bStartFlag = false;

    private int _iCameraCodecType = android.graphics.ImageFormat.NV21;

    private byte[] _yuvNV21 = new byte[WIDTH_DEF * HEIGHT_DEF * 3 / 2];
    private byte[] _yuvEdit = new byte[WIDTH_DEF * HEIGHT_DEF * 3 / 2];

    private final String Tag = "VideoCallActivity";
    private final String LOG_TAG = "VideoCallActivity";
    private SurfaceView _mSurfaceView;
    private CameraManager cameraManager;
    private ARVideoView mVideoView;

    private RelativeLayout root_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        _mSurfaceView = findViewById(R.id.surfaceView);
        _SwitchCameraBtn = findViewById(R.id.SwitchCamerabutton);
        cameraManager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);

        root_layout = findViewById(R.id.root_layout);

        InitAll();
    }

    private void InitAll() {
        WindowManager wm = this.getWindowManager();

        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        int iNewWidth = (int) (height * 3.0 / 4.0);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        int iPos = width - iNewWidth;
        layoutParams.setMargins(iPos, 0, 0, 0);

        _mSurfaceView.getHolder().setFixedSize(HEIGHT_DEF, WIDTH_DEF);
        _mSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        _mSurfaceView.getHolder().setKeepScreenOn(true);
        _mSurfaceView.getHolder().addCallback(new SurceCallBack());
        _mSurfaceView.setLayoutParams(layoutParams);

        InitAudioRecord();

        _SwitchCameraBtn = (Button) findViewById(R.id.SwitchCamerabutton);
//        _SwitchCameraBtn.setOnClickListener(_switchCameraOnClickedEvent);

//        RtmpStartMessage();//开始推流
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void Start() {
        _AudioRecorder.startRecording();
        _AacEncoderThread = new Thread(_aacEncoderRunnable);
        _AacEncoderThread.setPriority(Thread.MAX_PRIORITY);
        _AacEncoderThread.start();
    }

    private Runnable _aacEncoderRunnable = new Runnable() {
        @Override
        public void run() {
            DataOutputStream outputStream = null;
            if (true) {
                File saveDir = Environment.getExternalStorageDirectory();
                String strFilename = saveDir + "/aaa.aac";
                try {
                    outputStream = new DataOutputStream(new FileOutputStream(strFilename));
                } catch (FileNotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }

            long lSleepTime = SAMPLE_RATE_DEF * 16 * 2 / _RecorderBuffer.length;

            while (!_AacEncoderThread.interrupted() && _bStartFlag) {
                int iPCMLen = _AudioRecorder.read(_RecorderBuffer, 0, _RecorderBuffer.length); // Fill buffer
                if ((iPCMLen != _AudioRecorder.ERROR_BAD_VALUE) && (iPCMLen != 0)) {
                    if (_fdkaacHandle != 0) {
//                        byte[] aacBuffer = _fdkaacEnc.FdkAacEncode(_fdkaacHandle, _RecorderBuffer);
//                        if (aacBuffer != null) {
//                            long lLen = aacBuffer.length;
//
//                            _rtmpSessionMgr.InsertAudioData(aacBuffer);
//                            //Log.i(LOG_TAG, "fdk aac length="+lLen+" from pcm="+iPCMLen);
//                            if (DEBUG_ENABLE) {
//                                try {
//                                    outputStream.write(aacBuffer);
//                                } catch (IOException e) {
//                                    // TODO Auto-generated catch block
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
                    }
                } else {
                    Log.i(LOG_TAG, "######fail to get PCM data");
                }
                try {
                    Thread.sleep(lSleepTime / 10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.i(LOG_TAG, "AAC Encoder Thread ended ......");
        }
    };

    private final class SurceCallBack implements SurfaceHolder.Callback {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            _mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success) {
                        InitCamera();
                        camera.cancelAutoFocus();//只有加上了这一句，才会自动对焦。
                    }
                }
            });
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            _iDegrees = getDisplayOritation(getDispalyRotation(), 0);
            if (_mCamera != null) {
                InitCamera();
                return;
            }
            _mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            InitCamera();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    }

    private void InitAudioRecord() {
        _iRecorderBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_DEF,
                AudioFormat.CHANNEL_CONFIGURATION_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);
        _AudioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE_DEF, AudioFormat.CHANNEL_CONFIGURATION_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, _iRecorderBufferSize);
        _RecorderBuffer = new byte[_iRecorderBufferSize];

//        _fdkaacEnc = new FdkAacEncode();
//        _fdkaacHandle = _fdkaacEnc.FdkAacInit(SAMPLE_RATE_DEF, CHANNEL_NUMBER_DEF);
    }

    public Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Bundle b = msg.getData();
            int ret;
            switch (msg.what) {
                case ID_RTMP_PUSH_START: {
                    Start();
                    break;
                }
            }
        }
    };

    public void InitCamera() {
        Camera.Parameters p = _mCamera.getParameters();

        Camera.Size prevewSize = p.getPreviewSize();
        Log.i(LOG_TAG, "Original Width:" + prevewSize.width + ", height:" + prevewSize.height);

        List<Camera.Size> PreviewSizeList = p.getSupportedPreviewSizes();
        List<Integer> PreviewFormats = p.getSupportedPreviewFormats();
        Log.i(LOG_TAG, "Listing all supported preview sizes");
        for (Camera.Size size : PreviewSizeList) {
            Log.i(LOG_TAG, "  w: " + size.width + ", h: " + size.height);
        }

        Log.i(LOG_TAG, "Listing all supported preview formats");
        Integer iNV21Flag = 0;
        Integer iYV12Flag = 0;
        for (Integer yuvFormat : PreviewFormats) {
            Log.i(LOG_TAG, "preview formats:" + yuvFormat);
            if (yuvFormat == android.graphics.ImageFormat.YV12) {
                iYV12Flag = android.graphics.ImageFormat.YV12;
            }
            if (yuvFormat == android.graphics.ImageFormat.NV21) {
                iNV21Flag = android.graphics.ImageFormat.NV21;
            }
        }

        if (iNV21Flag != 0) {
            _iCameraCodecType = iNV21Flag;
        } else if (iYV12Flag != 0) {
            _iCameraCodecType = iYV12Flag;
        }
        p.setPreviewSize(HEIGHT_DEF, WIDTH_DEF);
        p.setPreviewFormat(_iCameraCodecType);
        p.setPreviewFrameRate(FRAMERATE_DEF);

        _mCamera.setDisplayOrientation(_iDegrees);
        p.setRotation(_iDegrees);
        _mCamera.setPreviewCallback(_previewCallback);
        _mCamera.setParameters(p);
        try {
            _mCamera.setPreviewDisplay(_mSurfaceView.getHolder());
        } catch (Exception e) {
            return;
        }
        _mCamera.cancelAutoFocus();//只有加上了这一句，才会自动对焦。

        _mCamera.startPreview();
    }

    private Camera.PreviewCallback _previewCallback = new Camera.PreviewCallback() {

        @Override
        public void onPreviewFrame(byte[] YUV, Camera currentCamera) {
            if (!_bStartFlag) {
                return;
            }

            boolean bBackCameraFlag = true;

            byte[] yuv420 = null;

            if (_iCameraCodecType == android.graphics.ImageFormat.YV12) {
                yuv420 = new byte[YUV.length];
//                _swEncH264.swapYV12toI420_Ex(YUV, yuv420, HEIGHT_DEF, WIDTH_DEF);
            } else if (_iCameraCodecType == android.graphics.ImageFormat.NV21) {
//                yuv420 = _swEncH264.swapNV21toI420(YUV, HEIGHT_DEF, WIDTH_DEF);
            }

            if (yuv420 == null) {
                return;
            }
            if (!_bStartFlag) {
                return;
            }
//            _yuvQueueLock.lock();
//            if (_YUVQueue.size() > 1) {
//                _YUVQueue.clear();
//            }
//            _YUVQueue.offer(yuv420);
//            _yuvQueueLock.unlock();
        }
    };

    private int getDispalyRotation() {
        int i = getWindowManager().getDefaultDisplay().getRotation();
        switch (i) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return 270;
        }
        return 0;
    }

    private int getDisplayOritation(int degrees, int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int result = 0;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    private String getCameraId(int type) {
        String id = "";
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristic = cameraManager.getCameraCharacteristics(cameraId);
                Integer facing = characteristic.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == type) {//CameraCharacteristics.LENS_FACING_FRONT
                    Log.d(Tag, "onSurfaceTextureAvailable: front camera is cameraid=" + cameraId);
                    id = cameraId;
                    break;
                }
            }
        } catch (Exception e) {
            Log.e(Tag, e.getMessage(), e);
        }
        return id;
    }
}
