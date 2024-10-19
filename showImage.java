package translateapp.jason.com;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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
import java.net.URI;
import java.util.List;

public class showImage extends AppCompatActivity {

    private static final String TAG = showImage.class.getSimpleName();
    static ImageView imageView;
    private Uri mImageUri;
    private Bitmap bitmap;
    Button picAnalyze;
    static String bringText;
    static String receiveCode;
    static int receivePosition;
    static int receiveToPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        imageView = findViewById(R.id.set_img);
        picAnalyze = findViewById(R.id.analyze_pic);
        mImageUri = getIntent().getData();
        imageView.setImageURI(mImageUri);

        try {
            InputStream inputStream = getContentResolver().openInputStream(mImageUri);
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();
        receiveCode = intent.getStringExtra("code");
        receivePosition = intent.getIntExtra("position", 0);
        receiveToPosition = intent.getIntExtra("toPosition", 0);

        picAnalyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testAnalyze();
            }
        });
    }

    public void testAnalyze()
    {

        MLLocalTextSetting setting = new MLLocalTextSetting.Factory().setOCRMode(MLLocalTextSetting.OCR_DETECT_MODE).setLanguage(receiveCode).create();
        MLTextAnalyzer analyzer = MLAnalyzerFactory.getInstance().getLocalTextAnalyzer(setting);
        MLFrame frame = MLFrame.fromBitmap(bitmap);
        Task<MLText> task = analyzer.asyncAnalyseFrame(frame);
        task.addOnSuccessListener(new OnSuccessListener<MLText>()
        {
            @Override
            public void onSuccess(MLText text)
            {
                MLText tt = text;
                bringText = displaySuccess(tt);
                Intent intent = new Intent(getApplicationContext(), ImageTranslation.class);
                intent.putExtra("text", bringText);
                intent.putExtra("secondPosition", receivePosition);
                intent.putExtra("secondToPosition", receiveToPosition);
                startActivity(intent);
                stop(analyzer);
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(Exception e)
            {
                Log.e(showImage.TAG, "failed: " + e.getMessage());
                Intent intent = new Intent(showImage.this, ImageTranslation.class);
                startActivity(intent);
                Toast.makeText(showImage.this, "Analyze Failed, No Internet Connection", Toast.LENGTH_SHORT).show();
                stop(analyzer);
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
            Log.e(showImage.TAG, "Stop failed: " + e.getMessage());
        }
    }

    public static String getBringText()
    {
        return bringText;
    }

}