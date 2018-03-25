package com.example.icoper.launcher;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.icoper.launcher.ui.TwoWayAdapterView;
import com.example.icoper.launcher.ui.TwoWayGridView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LauncherActivity extends AppCompatActivity implements View.OnClickListener {
    private int DEFAULT_ROWS = 5;

    private PackageManager manager;
    private List<AppDetail> apps;
    private TwoWayGridView gridView;
    private Button changeWall;
    private ImageView backgroundImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        backgroundImage = (ImageView)findViewById(R.id.la_background_iv);
        changeWall = (Button) findViewById(R.id.al_change_wallp_btn);
        changeWall.setOnClickListener(this);
        gridView = (TwoWayGridView) findViewById(R.id.la_grid_view);
        gridView.setNumRows(DEFAULT_ROWS);
        loadApps();
        loadListView();
        addClickListener();
    }


    private void loadApps() {
        manager = getPackageManager();
        apps = new ArrayList<AppDetail>();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
        for (ResolveInfo ri : availableActivities) {
            AppDetail app = new AppDetail();
            app.lable = ri.loadLabel(manager);
            app.name = ri.activityInfo.packageName;

            app.icon = ri.activityInfo.loadIcon(manager);
            apps.add(app);
        }

    }

    private void addClickListener() {
        gridView.setOnItemClickListener(new TwoWayAdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(TwoWayAdapterView<?> av, View v, int pos,
                                    long id) {
                Intent i = manager.getLaunchIntentForPackage(apps.get(pos).name.toString());
                LauncherActivity.this.startActivity(i);
            }
        });
    }

    private void loadListView() {

        ArrayAdapter<AppDetail> adapter = new ArrayAdapter<AppDetail>(this,
                R.layout.app_item_layout,
                apps) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.app_item_layout, null);
                }

                ImageView appIcon = (ImageView) convertView.findViewById(R.id.ai_icon_iv);
                appIcon.setImageDrawable(apps.get(position).icon);

                TextView appLabel = (TextView) convertView.findViewById(R.id.ai_app_name_tv);
                appLabel.setText(apps.get(position).lable);

                return convertView;
            }
        };

        gridView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.al_change_wallp_btn) {
            changeWallpaper();
        }
    }

    private void changeWallpaper() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri chosenImageUri = data.getData();

            Bitmap mBitmap = null;
            try {
                mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), chosenImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (mBitmap != null) {
                Drawable drawable = new BitmapDrawable(getResources(),mBitmap);
                backgroundImage.setBackground(drawable);
            }
        }
    }
}

