package com.jetsup.ussdracharge;

import static com.jetsup.ussdracharge.custom.ISPConstants.M_TAG;
import static com.jetsup.ussdracharge.custom.ISPConstants.SELECT_SIM_SLOT;
import static com.jetsup.ussdracharge.custom.ISPConstants.SIM_CARD_INFORMATION;
import static com.jetsup.ussdracharge.custom.ISPConstants.SIM_CARD_PRESENT;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.core.UseCaseGroup;
import androidx.camera.core.ViewPort;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;
import com.jetsup.ussdracharge.custom.ScanAnalyzer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@ExperimentalGetImage
public class ScannerActivity extends AppCompatActivity {

    public static String creditNumber = "";
    public static boolean dataChanged;
    public static Map<String, Integer> simInfo;
    public static String prefix;
    public static String rechargeFor;
    String[] simCards;
    boolean simPresent;
    EditText recognizedCreditText;
    FloatingActionButton fabCameraFlash;
    PreviewView cameraPreview;
    CameraControl cameraControl;
    private boolean flashOn;
    private ProcessCameraProvider cameraProvider;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        recognizedCreditText = findViewById(R.id.recognizedCreditText);
        fabCameraFlash = findViewById(R.id.fabCameraFlash);
        cameraPreview = findViewById(R.id.cameraPreview);

        simCards = getIntent().getStringArrayExtra(SIM_CARD_INFORMATION);
        simPresent = getIntent().getBooleanExtra(SIM_CARD_PRESENT, false);
        if (simPresent) {
            simInfo = new HashMap<>();
        }
        for (String simCard : simCards) {
            simInfo.put(simCard.substring(1), Integer.parseInt(String.valueOf(simCard.charAt(0))));
        }

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            int CAMERA_PERMISSION_CODE = 1;
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }

        fabCameraFlash.setOnClickListener(v -> {
            if (flashOn) {
                fabCameraFlash.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_flash_off_24, null));
                cameraControl.enableTorch(false);
                flashOn = false;
            } else {
                fabCameraFlash.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_flash_on_24, null));
                cameraControl.enableTorch(true);
                flashOn = true;
            }
        });

        bindToCamera();

        new Thread(() -> {
            while (true) {
                if (dataChanged) {
                    StringBuilder rectifiedString = new StringBuilder();
                    for (int i = 0; i < creditNumber.length(); i++) {
                        try {
                            rectifiedString.append(Integer.parseInt(String.valueOf(creditNumber.charAt(i))));
                        } catch (NumberFormatException ignore) {
                        }
                    }
                    runOnUiThread(() -> {
                        recognizedCreditText.setText(rectifiedString);
                        cameraProvider.unbindAll();
//                        onBackPressed();
                        // TODO: Check if both sim are of the same ISP

                        Uri dialUri = Uri.parse("tel:" + prefix + creditNumber + Uri.encode("#"));
                        Intent rechargeIntent = new Intent(Intent.ACTION_CALL);
                        rechargeIntent.setData(dialUri);
                        if (ScannerActivity.simInfo.size() > 0) {
                            Log.w(M_TAG, "Here " + rechargeFor);
                            rechargeIntent.putExtra(SELECT_SIM_SLOT, simInfo.get(rechargeFor));
                        }
                        Toast.makeText(getBaseContext(), "Recharging " + rechargeFor, Toast.LENGTH_SHORT).show();
                        startActivity(rechargeIntent);
                    });
                    startActivity(new Intent(this, MainActivity.class));
                    this.finish();
                    dataChanged = false;
                }
            }
        }).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void bindToCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void bindPreview(ProcessCameraProvider cameraProvider) {
        this.cameraProvider = cameraProvider;
        ScanAnalyzer imageAnalyzer = new ScanAnalyzer(this);

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(1280, 720))
//                .setTargetResolution(new Size(176, 144))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();
        imageAnalysis.setAnalyzer(getMainExecutor(), imageAnalyzer);
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(cameraPreview.getSurfaceProvider());
        ViewPort viewPort = cameraPreview.getViewPort();

        if (viewPort != null) {
            UseCaseGroup useCaseGroup = new UseCaseGroup.Builder()
                    .addUseCase(preview)
                    .addUseCase(imageAnalysis)
                    .setViewPort(viewPort)
                    .build();
            this.cameraProvider.unbindAll();
            Camera camera = this.cameraProvider.bindToLifecycle(this, cameraSelector, useCaseGroup);//, imageAnalysis,preview);
            cameraControl = camera.getCameraControl();
            cameraControl.setLinearZoom(0f);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        flashOn = false;
    }
}
