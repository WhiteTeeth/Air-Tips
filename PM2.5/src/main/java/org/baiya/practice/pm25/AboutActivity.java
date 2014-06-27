package org.baiya.practice.pm25;

import android.animation.Animator;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.BounceInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;


public class AboutActivity extends Activity implements View.OnClickListener {

    public static final String TAG = AboutActivity.class.getSimpleName();

    private TextView mContactView;
    private TextView mRatingView;
    private TextView mSourcesView;
    private TextView mVersionView;
    private TextView mTutorialsView;
    private View mDetailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        String action = getIntent().getAction();
        if (actionBar != null) {
            if (Intent.ACTION_MAIN.equalsIgnoreCase(action)) {
                actionBar.setIcon(R.drawable.ic_launcher);
                actionBar.setTitle(R.string.app_name);
            } else {
                actionBar.setIcon(R.drawable.widget_back);
                actionBar.setTitle(R.string.about);
                actionBar.setHomeButtonEnabled(true);
            }
        }
        setContentView(R.layout.activity_about);

        mVersionView = (TextView) findViewById(R.id.app_version);
        PackageManager packageManager = getPackageManager();
        try {
            String versionName = packageManager.getPackageInfo(getPackageName(), 0).versionName;
            Object[] objects = new Object[1];
            objects[0] = versionName;
            String version = getString(R.string.about_version, objects);
            mVersionView.setText(version);

            mContactView = (TextView) findViewById(R.id.contact_us);
            mRatingView = (TextView) findViewById(R.id.rating_us);
            mSourcesView = (TextView) findViewById(R.id.data_sources);
            mTutorialsView = (TextView) findViewById(R.id.tutorials);
            mContactView.setOnClickListener(this);
            mRatingView.setOnClickListener(this);
            mSourcesView.setOnClickListener(this);
            mTutorialsView.setOnClickListener(this);

            findViewById(R.id.container).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    refreshView();
                    return false;
                }
            });
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            mVersionView.setVisibility(View.GONE);
        }
    }

    private void refreshView() {
        if (mDetailView != null) return;
        mDetailView = View.inflate(this, R.layout.about_detail, null);
        TextView copyright = (TextView) mDetailView.findViewById(R.id.copyright);
        String str = getString(R.string.app_name);
        Object[] objects = new Object[1];
        objects[0] = str;
        str = getString(R.string.about_copyright, objects);
        copyright.setText(str);
        View indicator = findViewById(R.id.about_indicator);
        int topMargin = indicator.getTop();
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) indicator.getLayoutParams();
        marginLayoutParams.topMargin = topMargin;
        indicator.setLayoutParams(marginLayoutParams);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.bottom_container);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.addView(mDetailView, layoutParams);
        mDetailView.findViewById(R.id.developer).setOnClickListener(this);
        mDetailView.findViewById(R.id.gui).setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        switch (v.getId()) {
            case R.id.tutorials: {
                intent = new Intent(this, TutorialsActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.rating_us: {
                intent.setAction(Intent.ACTION_VIEW);
                Uri uri = Uri.parse("market://details?id=com.james.pm25");
                intent.setData(uri);
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    uri = Uri.parse("https://play.google.com/store/apps/details?id=com.james.pm25");
                    intent.setData(uri);
                    startActivity(intent);
                }
                break;
            }
            case R.id.contact_us: {
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/html");
                String[] strings = new String[1];
                strings[0] = "lhwhiteteeth@gmail.com";
                intent.putExtra(Intent.EXTRA_EMAIL, strings);
                String subject = getString(R.string.feedback);
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                startActivity(intent);
                break;
            }
            case R.id.data_sources: {
                intent.setAction(Intent.ACTION_VIEW);
                Uri uri = Uri.parse("http://www.pm25.in/");
                intent.setData(uri);
                startActivity(intent);
                break;
            }
            case R.id.developer:
            case R.id.gui: {
                v.setClickable(false);
                ViewPropertyAnimator animator = v.animate();
                animator.cancel();
                animator = animator.rotationXBy(360.0F).setDuration(600L);
                BounceInterpolator interpolator = new BounceInterpolator();
                animator.setInterpolator(interpolator);
                Animator.AnimatorListener listener = new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        v.setClickable(true);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                };
                animator.setListener(listener).start();
                break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item != null && item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
