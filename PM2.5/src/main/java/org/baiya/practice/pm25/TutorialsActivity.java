package org.baiya.practice.pm25;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;


public class TutorialsActivity extends Activity implements Runnable {

    public static final String TAG = TutorialsActivity.class.getSimpleName();
    private final int[] TUTORIALS_TITLE = {
            R.string.tutorial_1_title,
            R.string.tutorial_2_title,
            R.string.tutorial_3_title,
            R.string.tutorial_4_title,
            R.string.tutorial_5_title
    };
    private final int[] TUTORIALS_MESSAGE = {
            R.string.tutorial_1_message,
            R.string.tutorial_2_message,
            R.string.tutorial_3_message,
            R.string.tutorial_4_message,
            R.string.tutorial_5_message
    };
    private final int[] TUTORIALS_PIC = {
            R.drawable.tutorial_1,
            R.drawable.tutorial_2,
            R.drawable.tutorial_3,
            R.drawable.tutorial_4,
            R.drawable.tutorial_5
    };
    private View mContainer;
    private ViewPager mViewPager;
    private CirclePageIndicator mIndicator;
    private ProgressBar mProgressBar;
    private HandlerThread mHandlerThread;
    private Handler mThreadHandler;
    private Handler mHandler;
    private ArrayList<Bitmap> mPics = new ArrayList<Bitmap>();

    private PagerAdapter mPageAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return TUTORIALS_PIC.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = View.inflate(TutorialsActivity.this, R.layout.tutorials_image, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.image);
            TextView title = (TextView) view.findViewById(R.id.tutorials_title);
            TextView message = (TextView) view.findViewById(R.id.tutorial_message);
            title.setText(getString(TUTORIALS_TITLE[position]));
            message.setText(getString(TUTORIALS_MESSAGE[position]));
            imageView.setImageBitmap(mPics.get(position));
            container.addView(view, 0);
            return view;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setIcon(R.drawable.widget_back);
            actionBar.setHomeButtonEnabled(true);
        }
        setContentView(R.layout.activity_tutorials);

        mHandler = new Handler();

        mHandlerThread = new HandlerThread(TAG);
        mHandlerThread.start();
        Looper looper = mHandlerThread.getLooper();
        mThreadHandler = new Handler(looper);
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                mPics.clear();
                int length = TUTORIALS_PIC.length;
                for (int i = 0; i < length; i++) {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), TUTORIALS_PIC[i]);
                    mPics.add(bitmap);
                }
                mHandler.post(TutorialsActivity.this);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item != null && item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void run() {
        mViewPager = (ViewPager) findViewById(R.id.tutorials_pager);
        mViewPager.setAdapter(mPageAdapter);

        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mViewPager);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(300L);
        Animator[] animators = new Animator[2];
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        float[] progressAlpha = {0x3f800000, 0};
        animators[0] = ObjectAnimator.ofFloat(mProgressBar, "alpha", progressAlpha);

        mContainer = findViewById(R.id.container);
        float[] containerAlpha = {0, 0x3f800000};
        animators[1] = ObjectAnimator.ofFloat(mContainer, "alpha", containerAlpha);

        animatorSet.playTogether(animators);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        mContainer.setAlpha(1.0F);
        mContainer.setVisibility(View.VISIBLE);
        mProgressBar.setAlpha(0.0F);
        mProgressBar.setVisibility(View.VISIBLE);

        animatorSet.start();
    }
}
