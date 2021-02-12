package com.example.notificationreader.ui;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.notificationreader.R;
import com.example.notificationreader.misc.Const;
import com.example.notificationreader.service.MyForeGroundService;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import static com.example.notificationreader.misc.Const.ACTION_NOTIFICATION_LISTENER_SETTINGS;
import static com.example.notificationreader.misc.Const.ACTION_START_FOREGROUND_SERVICE;
import static com.example.notificationreader.misc.Const.ACTION_STOP_FOREGROUND_SERVICE;
import static com.example.notificationreader.misc.Const.ENABLED_NOTIFICATION_LISTENERS;
import static com.example.notificationreader.misc.Const.SORT_BY.ALL;
import static com.example.notificationreader.misc.Const.SORT_BY.PER_DAY;
import static com.example.notificationreader.misc.Const.SORT_BY.PER_HOUR;
import static com.example.notificationreader.misc.Const.SORT_BY.PER_MONTH;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ImageView imageView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button activatorButton;
    private RecyclerView recyclerView;
    Const.SORT_BY currentMarker = ALL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_notes));
        setSupportActionBar(toolbar);

        /*
         Check foreground service permission
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Dexter.withContext(getApplicationContext())
                    .withPermission(Manifest.permission.FOREGROUND_SERVICE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                        }
                    }).check();
        }

        imageView = findViewById(R.id.no_noty);
        activatorButton=findViewById(R.id.button_server_activator);

        activatorButton.setOnClickListener(view -> {
            if(activatorButton.getText().equals(getResources().getString(R.string.start))){
                activatorButton.setBackgroundColor(getResources().getColor(R.color.button_stop_color,getTheme()));
                activatorButton.setText(R.string.stop);
                activatorButton.setTextColor(getResources().getColor(R.color.black,getTheme()));

                Intent playIntent = new Intent(MainActivity.this, MyForeGroundService.class);
                playIntent.setAction(ACTION_START_FOREGROUND_SERVICE);
                ContextCompat.startForegroundService(MainActivity.this,playIntent);

            }else {
                activatorButton.setBackgroundColor(getResources().getColor(R.color.black,getTheme()));
                activatorButton.setText(R.string.start);
                activatorButton.setTextColor(getResources().getColor(R.color.white,getTheme()));

                Intent pauseIntent = new Intent(MainActivity.this, MyForeGroundService.class);
                pauseIntent.setAction(ACTION_STOP_FOREGROUND_SERVICE);
                ContextCompat.startForegroundService(getApplicationContext(),pauseIntent);
            }

        });
        /*
        Used to access to all notifications
         */
        if (!isNotificationServiceEnabled()){
            buildNotificationServiceAlertDialog().show();
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.recycler_container);
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout = findViewById(R.id.swiper);
        swipeRefreshLayout.setColorSchemeResources(R.color.design_default_color_primary);
        swipeRefreshLayout.setOnRefreshListener(this);

        update(ALL);
    }

    private void update(Const.SORT_BY marker) {
        BrowseAdapter adapter = new BrowseAdapter(this, marker);
        recyclerView.setAdapter(adapter);

        if(adapter.getItemCount() == 0) {
            Toast.makeText(this, R.string.empty_log_file, Toast.LENGTH_LONG).show();
            imageView.setVisibility(View.VISIBLE);
        }else {
            imageView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.action_all: {
                item.setChecked(true);
                currentMarker=ALL;
                break;
            }
            case R.id.action_per_hour: {
                item.setChecked(true);
                currentMarker = PER_HOUR;
                break;
            }
            case R.id.action_per_day: {
                item.setChecked(true);
                currentMarker = PER_DAY;
                break;
            }
            case R.id.action_per_month: {
                item.setChecked(true);
                currentMarker = PER_MONTH;
                break;
            }
        }
        update(currentMarker);

        return super.onOptionsItemSelected(item);
    }

    /**
     * Is Notification Service Enabled.
     */
    private boolean isNotificationServiceEnabled(){
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (String name : names) {
                final ComponentName cn = ComponentName.unflattenFromString(name);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Build Notification Listener Alert Dialog.
     */
    private AlertDialog buildNotificationServiceAlertDialog(){
       AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.notification_listener_service);
        alertDialogBuilder.setMessage(R.string.notification_listener_service_explanation);

        alertDialogBuilder.setPositiveButton(R.string.yes,
                (dialog, id) -> startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS)));
        alertDialogBuilder.setNegativeButton(R.string.no,
                (dialog, id) -> {
                    // If you choose to not enable the notification listener
                    // the app. will not work as expected
                });
        return(alertDialogBuilder.create());
    }

    @Override
    public void onRefresh() {
        update(currentMarker);
        swipeRefreshLayout.setRefreshing(false);
    }
}