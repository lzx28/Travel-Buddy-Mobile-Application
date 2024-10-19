package translateapp.jason.com;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.account.service.AccountAuthService;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;

import java.util.ArrayList;

public class History extends AppCompatActivity {
    //Initialize
    DrawerLayout drawerLayout;
    AccountAuthService service;
    private static final String TAG = "TranslationHistory";

    DatabaseHelper mDatabaseHelper;

    private ListView mListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        //Assign Variable
        drawerLayout = findViewById(R.id.drawer_layout);
        mListView = findViewById(R.id.historyList);
        mDatabaseHelper = new DatabaseHelper(this);

        historyListView();
    }

    public void ClickMenu(View view){
        //Open Drawer
        NavigationBar.openDrawer(drawerLayout);
    }

    public void ClickLogo(View view){
        //Close drawer
        NavigationBar.closeDrawer(drawerLayout);
    }

    public void ClickHome(View view){
        //Redirect activity to home
        NavigationBar.redirectActivity(this, Home.class);
    }

    public void ClickDashboard(View view){
        //Recreate activity
        recreate();
    }


    public void ClickLogout(View view){
        //Close app
        //NavigationBar.logout(this);
        confirmDialog();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //close drawer
        NavigationBar.closeDrawer(drawerLayout);
    }

    private void historyListView()
    {
        Log.d(TAG, "historyListView: Displaying data in the ListView.");
        Cursor data = mDatabaseHelper.getData();
        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext())
        {
            listData.add(data.getString(1) + "\n" + data.getString(2));
        }

        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectItem = parent.getItemAtPosition(position).toString();
                //Log.d(TAG, "History Data 1 : " + selectItem);
                //int passPosition = 0;
                //passPosition = position + 1;
                Log.d(TAG, "onItemClick: You Clicked on " + selectItem);

                int itemID = -1;
                itemID = mDatabaseHelper.getItemID(selectItem);

                if(itemID > -1)
                {
                    Log.d(TAG, "onItemClick: The ID is: " + itemID);
                    Intent intent = new Intent(getApplicationContext(), DeleteDataActivity.class);
                    intent.putExtra("id", itemID);
                    intent.putExtra("item", selectItem);
                    startActivity(intent);
                }

                else
                {
                    Toast.makeText(History.this, "No ID associated with the selected item.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void confirmDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you wish to logout")
                .setTitle("Logout")
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        //finishAffinity();
                        //System.exit(0);
                        MainActivity.getmInstanceActivity().signOut();
                        MainActivity.getmInstanceActivity().cancelAuthorization();
                        Intent intent = new Intent(History.this, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // CANCEL
                    }
                });
        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}