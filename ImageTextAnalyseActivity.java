/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package translateapp.jason.com;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.text.MLLocalTextSetting;
import com.huawei.hms.mlsdk.text.MLText;
import com.huawei.hms.mlsdk.text.MLTextAnalyzer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ImageTextAnalyseActivity extends AppCompatActivity{
    private static final String TAG = ImageTextAnalyseActivity.class.getSimpleName();
    private static final int CAMERA_PERMISSION_CODE = 1001;
    private static final int PICK_IMAGE_REQUEST = 1002;
    private static final int resultCode = -1;
    private Uri mImageUri;
    private Bitmap bitmap;
    Button setImage, analyzeImage;
    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        setImage = findViewById(R.id.take_pic);
        imageView = (ImageView) findViewById(R.id.set_img);
        analyzeImage = findViewById(R.id.analyze_pic);
        setImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestBitmap();
            }
        });

        analyzeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testAnalyze();
            }
        });

        //this.findViewById(R.id.analyze_pic).setOnClickListener(this);

        // Check whether the app has the camera permission.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // The app has the camera permission.
        } else {
            // Apply for the camera permission.
            requestCameraPermission();
        }
    }

    public void requestBitmap()
    {
        Intent intent;
        if(Build.VERSION.SDK_INT < 19)
        {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        }

        else
        {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        }

        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE_REQUEST)
        {
            mImageUri = data.getData();
            imageView.setImageURI(mImageUri);

            try {
                InputStream inputStream = getContentResolver().openInputStream(mImageUri);
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }


    private void requestCameraPermission() {
        final String[] permissions = new String[]{Manifest.permission.CAMERA};
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, CAMERA_PERMISSION_CODE);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != CAMERA_PERMISSION_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // The camera permission is granted.
        }

    }

    public void testAnalyze()
    {

        MLLocalTextSetting setting = new MLLocalTextSetting.Factory().setOCRMode(MLLocalTextSetting.OCR_DETECT_MODE).setLanguage("de").create();
        MLTextAnalyzer analyzer = MLAnalyzerFactory.getInstance().getLocalTextAnalyzer(setting);
        MLFrame frame = MLFrame.fromBitmap(bitmap);
        Task<MLText> task = analyzer.asyncAnalyseFrame(frame);
        task.addOnSuccessListener(new OnSuccessListener<MLText>()
        {
            @Override
            public void onSuccess(MLText text)
            {
                MLText tt = text;
                Toast.makeText(ImageTextAnalyseActivity.this, tt.getStringValue(), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(Exception e)
            {
                Log.e(ImageTextAnalyseActivity.TAG, "failed: " + e.getMessage());
            }
        });
    }

    /**
     * Text recognition on the device
     */
    public void localAnalyzer(int imageId, Resources resources) {
        // Create the text analyzer MLTextAnalyzer to recognize characters in images. You can set MLLocalTextSetting to
        // specify languages that can be recognized.
        // If you do not set the languages, only Romance languages can be recognized by default.
        // Use default parameter settings to configure the on-device text analyzer. Only Romance languages can be
        // recognized.
        // analyzer = MLAnalyzerFactory.getInstance().getLocalTextAnalyzer();
        // Use the customized parameter MLLocalTextSetting to configure the text analyzer on the device.
        MLLocalTextSetting setting = new MLLocalTextSetting.Factory()
                .setOCRMode(MLLocalTextSetting.OCR_DETECT_MODE)
                .setLanguage("en")
                .create();
        MLTextAnalyzer analyzer = MLAnalyzerFactory.getInstance()
                .getLocalTextAnalyzer(setting);
        // Create an MLFrame by using android.graphics.Bitmap.
        Bitmap bitmap = BitmapFactory.decodeResource(resources, imageId);
        MLFrame frame = MLFrame.fromBitmap(bitmap);
        Task<MLText> task = analyzer.asyncAnalyseFrame(frame);
        task.addOnSuccessListener(new OnSuccessListener<MLText>() {
            @Override
            public void onSuccess(MLText text) {
                // Recognition success.
                String result = ImageTextAnalyseActivity.this.displaySuccess(text);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                // Recognition failure.
                Log.e(ImageTextAnalyseActivity.TAG, "failed: " + e.getMessage());
            }
        });
    }


    private String displaySuccess(MLText mlText) {
        String result = "";
        List<MLText.Block> blocks = mlText.getBlocks();
        for (MLText.Block block : blocks) {
            for (MLText.TextLine line : block.getContents()) {
                result += line.getStringValue() + "\n";
            }
        }
        return result;
    }

    protected void stop(MLTextAnalyzer analyzer) {
        if (analyzer == null) {
            return;
        }
        try {
            analyzer.stop();
        } catch (IOException e) {
            Log.e(ImageTextAnalyseActivity.TAG, "Stop failed: " + e.getMessage());
        }
    }
}
