package com.example.administrator.camera;

import android.content.ContentValues;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.example.administrator.myapplication.R;

import java.io.IOException;
import java.io.OutputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2015/11/10.
 */
public class CameraTest extends AppCompatActivity implements View.OnClickListener, SurfaceHolder.Callback
        , Camera.PictureCallback {
    @InjectView(R.id.surfaceview)
    SurfaceView surfaceView;
    @InjectView(R.id.take)
    Button take;
    @InjectView(R.id.close)
    Button close;
    private SurfaceHolder surfaceHolder;
    private Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_view);
        ButterKnife.inject(this);
        init();
    }

    private void init() {
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(this);
        close.setOnClickListener(this);
        take.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.take:
                camera.takePicture(null, null, null, this);
            case R.id.close:
                camera.stopPreview();
                camera.release();
                break;
        }
    }

    private static final int LARGEST_WIDTH = 500;
    private static final int LARGEST_HEIGHT = 500;

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();
        Camera.Parameters params = camera.getParameters();
        if (this.getResources().getConfiguration().orientation
                != Configuration.ORIENTATION_LANDSCAPE) {
            camera.setDisplayOrientation(90);
        }
       /* int bestWidth = 0;
        int bestHeight = 0;
        List<Camera.Size> previewSizes = params.getSupportedPreviewSizes();
        if (previewSizes.size() > 1) {
            Iterator<Camera.Size> it = previewSizes.iterator();
            while (it.hasNext()) {
                Camera.Size aSize = it.next();
                if (aSize.width > bestWidth && aSize.width <= LARGEST_WIDTH
                        && aSize.height > bestHeight && aSize.height <= LARGEST_HEIGHT) {
                    bestWidth = aSize.width;
                    bestHeight = aSize.height;
                }


            }
            if (bestWidth != 0 && bestHeight != 0) {
                params.setPreviewSize(bestWidth, bestHeight);
                surfaceView.setLayoutParams(new LinearLayout.LayoutParams(bestWidth, bestHeight));
            }
        }*/


        try {
            camera.setParameters(params);
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
//        camera.stopPreview();
//        camera.release();

    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        try {
            OutputStream out = getContentResolver().openOutputStream(imageUri);
            out.write(data);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //当调用takePicture()方法是预览暂停,返回图片后重新预览
        camera.startPreview();

    }
}
