package org.baiya.practice.pm25;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import org.baiya.practice.pm25.bean.City;

public class LocationSelectedActivity extends Activity
        implements Handler.Callback, AdapterView.OnItemClickListener, TextWatcher {

    public static final String TAG = LocationSelectedActivity.class.getSimpleName();
    private ListView mListView;
    private Handler mHandler;
    private EditText mSearchEdit;
    private ImageView mSearchCleanBtn;
    private LocationAdapter mAdapter;

    public static void startActivityForResult(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity.getApplicationContext(), LocationSelectedActivity.class);
        activity.startActivityForResult(intent, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setIcon(R.drawable.widget_back);
            actionBar.setHomeButtonEnabled(true);
        }
        setContentView(R.layout.activity_location);
        mListView = (ListView) findViewById(R.id.location_list);
        mAdapter = new LocationAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        mHandler = new Handler(this);

        mSearchEdit = (EditText) findViewById(R.id.search_text);
        mSearchEdit.addTextChangedListener(this);

        mSearchCleanBtn = (ImageView) findViewById(R.id.search_clean);
        mSearchCleanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchEdit.setText("");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean handleMessage(Message msg) {
        String search = msg.obj.toString();
        mAdapter.changeSearch(search);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        City city = mAdapter.getItem(position);
        Intent intent = new Intent();
        intent.putExtra("RESULT_LOCATION", city);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        Message message = Message.obtain(mHandler);
        message.obj = s.toString();
        message.what = 0;
        mHandler.removeMessages(0);
        mHandler.sendMessageDelayed(message, 200);
    }
}
