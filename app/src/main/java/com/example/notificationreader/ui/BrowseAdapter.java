package com.example.notificationreader.ui;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notificationreader.R;
import com.example.notificationreader.misc.Const;
import com.example.notificationreader.misc.DatabaseHelper;
import com.example.notificationreader.misc.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class BrowseAdapter extends RecyclerView.Adapter<BrowserViewHolder> {

private final static int LIMIT = Integer.MAX_VALUE;
private final static String PAGE_SIZE = "20";

private DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
private DateFormat timeFormat = DateFormat.getTimeInstance();

    private Activity context;
    private ArrayList<DataItem> data = new ArrayList<>();
    private HashMap<String, Drawable> iconCache = new HashMap<>();
    private Handler handler = new Handler();
    private Const.SORT_BY marker ;

    private String lastDate = "";
    private boolean shouldLoadMore = true;

    BrowseAdapter(Activity context, Const.SORT_BY marker) {
            this.context = context;
            this.marker = marker;
            loadData(marker);
    }

    @NonNull
    @Override
    public BrowserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_browse, parent, false);
        return new BrowserViewHolder(view);
        }

    @Override
    public void onBindViewHolder(@NonNull BrowserViewHolder vh, int position) {
        DataItem item = data.get(position);

        if(iconCache.containsKey(item.getPackageName()) && iconCache.get(item.getPackageName()) != null) {
            vh.icon.setImageDrawable(iconCache.get(item.getPackageName()));
        } else {
            vh.icon.setImageResource(R.mipmap.ic_launcher);
        }

        vh.item.setTag("" + item.getId());
        vh.name.setText(item.getAppName());

        if(item.getPreview().length() == 0) {
            vh.preview.setVisibility(View.GONE);
            vh.text.setVisibility(View.VISIBLE);
            vh.text.setText(item.getText());
        } else {
            vh.text.setVisibility(View.GONE);
            vh.preview.setVisibility(View.VISIBLE);
            vh.preview.setText(item.getPreview());
        }

        if(item.shouldShowDate()) {
            vh.date.setVisibility(View.VISIBLE);
            vh.date.setText(item.getDate());
        } else {
            vh.date.setVisibility(View.GONE);
        }

        vh.itemTime.setText(item.getTime());
        vh.itemDate.setText(item.getDate());

        if(position == getItemCount() - 1) {
        loadMore(item.getId());
        }
        }

    @Override
    public int getItemCount() {
        return data.size();
        }

    private void loadData(Const.SORT_BY marker) {
            switch (marker){
                case ALL:{
                    loadMore(Integer.MAX_VALUE);
                    break;
                }
                case PER_DAY:{
                    loadMore(Integer.MAX_VALUE);
                    break;
                }
                case PER_HOUR:{
                    loadMore(Integer.MAX_VALUE);
                    break;
                }
                case PER_MONTH:{
                    loadMore(Integer.MAX_VALUE);
                    break;
                }
            }
    }

    private void loadMore(long afterId) {
        if(!shouldLoadMore) {
        if(Const.DEBUG) System.out.println("not loading more items");
        return;
        }

        if(Const.DEBUG) System.out.println("loading more items");
        int before = getItemCount();
        try {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = db.query(DatabaseHelper.PostedEntry.TABLE_NAME,
        new String[] {
        DatabaseHelper.PostedEntry._ID,
        DatabaseHelper.PostedEntry.COLUMN_NAME_CONTENT
        },
        DatabaseHelper.PostedEntry._ID + " < ?",
        new String[] {""+afterId},
        null,
        null,
        DatabaseHelper.PostedEntry._ID + " DESC",
        PAGE_SIZE);

        if(cursor != null && cursor.moveToFirst()) {
        for(int i = 0; i < cursor.getCount(); i++) {
        DataItem dataItem = new DataItem(context, cursor.getLong(0), cursor.getString(1));

        String thisDate = dataItem.getDate();
        if(lastDate.equals(thisDate)) {
        dataItem.setShowDate(false);
        }
        lastDate = thisDate;

        data.add(dataItem);
        cursor.moveToNext();
        }
        cursor.close();
        }

        db.close();
        databaseHelper.close();
        } catch (Exception e) {
        if(Const.DEBUG) e.printStackTrace();
        }
        int after = getItemCount();

        if(before == after) {
        if(Const.DEBUG) System.out.println("no new items loaded: " + getItemCount());
        shouldLoadMore = false;
        }

        if(getItemCount() > LIMIT) {
        if(Const.DEBUG) System.out.println("reached the limit, not loading more items: " + getItemCount());
        shouldLoadMore = false;
        }

        handler.post(() -> notifyDataSetChanged());
        }

    private class DataItem {

        private long id;
        private String packageName;
        private String appName;
        private String text;
        private String preview;
        private String date;
        private String time;
        private boolean showDate;


        DataItem(Context context, long id, String str) {
        this.id = id;
        try {
            JSONObject json = new JSONObject(str);
            packageName = json.getString("packageName");
            appName = Util.getAppNameFromPackage(context, packageName, false);
            text = str;
            time = timeFormat.format(json.optLong("systemTime"));
                    /*DateUtils.getRelativeDateTimeString(
                    context,
                    json.optLong("systemTime"),
                    DateUtils.MINUTE_IN_MILLIS,
                    DateUtils.WEEK_IN_MILLIS,
                    0).toString();*/

            String title = json.optString("title");
            String text = json.optString("text");
            preview = (title + "\n" + text).trim();

            if(!iconCache.containsKey(packageName)) {
                iconCache.put(packageName, Util.getAppIconFromPackage(context, packageName));
            }

            date = format.format(json.optLong("systemTime"));
            showDate = true;
        } catch (JSONException e) {
            if(Const.DEBUG) e.printStackTrace();
        }
    }

        public long getId() {
            return id;
        }

        public String getPackageName() {
            return packageName;
        }

        public String getAppName() {
            return appName;
        }

        public String getText() {
            return text;
        }

        public String getPreview() {
            return preview;
        }

        public String getDate() {
            return date;
        }

        public String getTime() {
            return time;
        }

        public boolean shouldShowDate() {
            return showDate;
        }

        public void setShowDate(boolean showDate) {
        this.showDate = showDate;
    }

}
}
