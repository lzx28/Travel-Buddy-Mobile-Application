package translateapp.jason.com;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DeleteDataActivity extends AppCompatActivity {

    private static final String TAG = "DeleteDataActivity";
    private Button btnDelete;
    private TextView item;
    DatabaseHelper mDatabaseHelper;

    private String selectedItem;
    private int selectedID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_data);
        btnDelete = findViewById(R.id.deleteButton);
        item = findViewById(R.id.selectedItem);
        mDatabaseHelper = new DatabaseHelper(this);

        Intent receiveIntent = getIntent();
        selectedItem = receiveIntent.getStringExtra("item");
        selectedID = receiveIntent.getIntExtra("id", -1);
        item.setSelected(true);
        item.setHorizontallyScrolling(true);
        item.setText(selectedItem);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDatabaseHelper.deleteData(selectedID);
                Intent intent = new Intent(DeleteDataActivity.this, History.class);
                startActivity(intent);
                Toast.makeText(DeleteDataActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void BackToHistory(View view){
        //Back to Home
        Intent intent = new Intent(this, History.class);
        startActivity(intent);
    }
}