package com.hjianfei.ninegridlayout.image_zoom;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.bumptech.glide.Glide;
import com.hjianfei.ninegridlayout.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageDetailActivity extends AppCompatActivity implements OnClickListener, OnLongClickListener, OnPageChangeListener {
    ZoomDragImageViewPager vp;
    Map<Integer, Bitmap> bigBitmapsCache = new HashMap<>();
    ImageView[] positionGuide;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  //设置全屏
        setContentView(R.layout.image_detail_activity);
        int pos = getIntent().getIntExtra("pos", 0);
        ArrayList<String> picList = getIntent().getStringArrayListExtra("picList");
        initPositionGuideLay(picList == null ? 0 : picList.size());
        vp = (ZoomDragImageViewPager) findViewById(R.id.vp);
        vp.setAdapter(new MyPagerAdapter(picList));
        vp.setOnPageChangeListener(this);
        vp.setOffscreenPageLimit(5);
        vp.setCurrentItem(pos);
    }

    private void initPositionGuideLay(int size) {
        LinearLayout group = (LinearLayout) findViewById(R.id.viewGroup);
        positionGuide = new ImageView[size];
        for (int i = 0; i < size; i++) {
            ImageView imageView = new ImageView(this);
            LayoutParams lp = new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp.setMargins(5, 0, 5, 0);
            imageView.setLayoutParams(lp);
            positionGuide[i] = imageView;
            if (i == 0) {
                positionGuide[i]
                        .setBackgroundResource(R.drawable.guid_activity_dot_selected);
            } else {
                positionGuide[i]
                        .setBackgroundResource(R.drawable.guid_activity_dot_normal);
            }
            group.addView(positionGuide[i]);
        }
    }

    public class ViewHolder {
        int pos;
        public ZoomDragImageIV content_iv;
        public ImageView iv_save;

        public void setPos(int pos) {
            this.pos = pos;
        }

        public int getPos() {
            return pos;
        }

    }

    public void initView(ViewHolder vh, View v) {
        vh.content_iv = (ZoomDragImageIV) v.findViewById(R.id.content_iv);
        vh.content_iv.dragAndZoomTouchListener = new DragAndZoomTouchListener(vh.content_iv);
        v.setTag(vh);
    }

    public class MyPagerAdapter extends PagerAdapter {

        ArrayList<String> screenshot_samples;
        List<View> pagerViews = new ArrayList<>();

        public MyPagerAdapter(ArrayList<String> screenshot_samples) {
            this.screenshot_samples = screenshot_samples;
            for (String pic : screenshot_samples) {
                View v = View.inflate(ImageDetailActivity.this, R.layout.image_detail_lay, null);
                ViewHolder vh = new ViewHolder();
                initView(vh, v);
                pagerViews.add(v);
            }
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(final View arg0, final int position) {
            View v = pagerViews.get(position);
            final ViewHolder vh = (ViewHolder) v.getTag();
            vh.setPos(position);
            if (!screenshot_samples.get(position).equals("")) {
                Glide.with(ImageDetailActivity.this).load(screenshot_samples.get(vh.pos).trim()).asBitmap().error(R.drawable.placeholder_figure).into(vh.content_iv);
            } else {
                Glide.with(ImageDetailActivity.this).load(screenshot_samples.get(vh.pos)).asBitmap().error(R.drawable.placeholder_figure).into(vh.content_iv);
            }
            ((ViewPager) arg0).addView(v);
            return v;
        }


        @Override
        public int getCount() {
            return screenshot_samples == null ? 0 : screenshot_samples.size();
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(pagerViews.get(arg1));
        }

        public View getItem(int position) {
            return pagerViews.get(position);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.content_iv) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        for (int i : bigBitmapsCache.keySet()) {
            if (bigBitmapsCache.get(i) != null) {
                bigBitmapsCache.get(i).recycle();
            }
        }
        super.onDestroy();
    }


    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < positionGuide.length; i++) {
            positionGuide[i]
                    .setBackgroundResource(R.drawable.guid_activity_dot_normal);
            if (position == i) {
                positionGuide[position]
                        .setBackgroundResource(R.drawable.guid_activity_dot_selected);
            }
        }
        vp.resetImageMatrix();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onLongClick(final View v) {


        return true;
    }

}
