package org.baiya.practice.pm25;

import android.app.ActionBar;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.baiya.practice.pm25.bean.City;
import org.baiya.practice.pm25.bean.PmBean;
import org.baiya.practice.pm25.cache.WidgetManager;


public class ConfigureActivity extends Activity
        implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    public static final String TAG = ConfigureActivity.class.getSimpleName();
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private Button mOkBtn;
    private ImageView mPreviewImage;
    private TextView mPlaceBtn;
    private RadioGroup mDisplayRadio;
    private RadioGroup mStyleRadio;
    private View mColorContainer;
    private View mStyleContainer;
    private City mCity;
    private PmBean.Type mWidgetType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setIcon(R.drawable.setting_icon);
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            mAppWidgetId = bundle.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            unexpectedWidget();
        }
        initView();
        switch (AppWidgetManager.getInstance(this).getAppWidgetInfo(mAppWidgetId).initialLayout) {
            case R.layout.widget_21:
                mWidgetType = PmBean.Type.BIG;
                configureBigWidget();
                break;
            case R.layout.widget_11:
                mWidgetType = PmBean.Type.SMALL;
                configureSmallWidget();
                break;
        }
    }

    private void unexpectedWidget() {
        setResult(RESULT_CANCELED, getIntent());
        finish();
    }

    @Override
    public void onBackPressed() {
        unexpectedWidget();
        super.onBackPressed();
    }

    private void initView() {
        mPreviewImage = (ImageView) findViewById(R.id.preview_image);
        mPlaceBtn = (TextView) findViewById(R.id.place_btn);
        mColorContainer = findViewById(R.id.style_container);
        mStyleContainer = findViewById(R.id.display_container);
        mDisplayRadio = (RadioGroup) findViewById(R.id.display_group);
        mStyleRadio = (RadioGroup) findViewById(R.id.style_group);
    }

    private void configureBigWidget() {
        mPreviewImage.setImageResource(R.drawable.preview_big);
        mPlaceBtn.setOnClickListener(this);
        mColorContainer.setVisibility(View.GONE);
        mStyleContainer.setVisibility(View.GONE);
    }

    private void configureSmallWidget() {
        mPlaceBtn.setOnClickListener(this);
        mPreviewImage.setImageResource(R.drawable.preview_small_light_graph);
        mStyleRadio.check(R.id.style_white);
        mDisplayRadio.check(R.id.display_icon);
        mStyleRadio.setOnCheckedChangeListener(this);
        mDisplayRadio.setOnCheckedChangeListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mCity = (City) data.getSerializableExtra("RESULT_LOCATION");
            mPlaceBtn.setText(mCity.getLocaleStr(this));
            mOkBtn.setEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.configure, menu);
        View view = menu.findItem(R.id.ok).getActionView();
        mOkBtn = (Button) view.findViewById(R.id.setting_done);
        mOkBtn.setEnabled(false);
        mOkBtn.setOnClickListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return (id == R.id.ok) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        int previewDrawable = 0;
        int checkedDisplayId = mDisplayRadio.getCheckedRadioButtonId();
        int checkedStyleId = mStyleRadio.getCheckedRadioButtonId();
        if (checkedDisplayId == R.id.display_value) {
            if (checkedStyleId == R.id.style_white) {
                previewDrawable = R.drawable.preview_small_light_number;
            } else if (checkedStyleId == R.id.style_black) {
                previewDrawable = R.drawable.preview_small_dark_number;
            }
        } else if (checkedDisplayId == R.id.display_icon) {
            if (checkedStyleId == R.id.style_white) {
                previewDrawable = R.drawable.preview_small_light_graph;
            } else if (checkedStyleId == R.id.style_black) {
                previewDrawable = R.drawable.preview_small_dark_graph;
            }
        }
        mPreviewImage.setImageResource(previewDrawable);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.place_btn:
                LocationSelectedActivity.startActivityForResult(this);
                break;
            case R.id.setting_done:
                settingDone();
                break;
        }
    }

    private void settingDone() {
        mOkBtn.setEnabled(false);
        PmBean pmBean = new PmBean();
        pmBean.city = mCity;
        pmBean.widgetId = mAppWidgetId;
        pmBean.pm25 = "???";
        switch (mWidgetType) {
            case BIG:
                pmBean.type = PmBean.Type.BIG;
                break;
            case SMALL:
                pmBean.type = PmBean.Type.SMALL;
                break;
            default:
                throw new IllegalArgumentException("unknown widget type");
        }
        switch (mDisplayRadio.getCheckedRadioButtonId()) {
            case R.id.display_value:
                pmBean.display = PmBean.Display.VALUE;
                break;
            case R.id.display_icon:
                pmBean.display = PmBean.Display.GRAPH;
                break;
            default:
                pmBean.display = PmBean.Display.UNKNOWN;
        }
        switch (mStyleRadio.getCheckedRadioButtonId()) {
            case R.id.style_white:
                pmBean.style = PmBean.Style.WHITE;
                break;
            case R.id.style_black:
                pmBean.style = PmBean.Style.BLACK;
                break;
            default:
                pmBean.style = PmBean.Style.UNKNOWN;
        }

        WidgetManager.savePmBean(this, pmBean);

        UpdateService.createWidget(ConfigureActivity.this, mAppWidgetId);

        setResult(RESULT_OK, getIntent());
        finish();
    }
}
