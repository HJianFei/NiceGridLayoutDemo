package com.hjianfei.ninegridlayout.image_zoom;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.lang.reflect.Field;

public class ZoomDragImageViewPager extends ViewPager {
    ImageDetailActivity.MyPagerAdapter adapter;

    public ZoomDragImageViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
        this.adapter = (ImageDetailActivity.MyPagerAdapter) adapter;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        ImageDetailActivity.ViewHolder tag = getCurrentViewHolder();
        boolean consume = tag.content_iv.dragAndZoomTouchListener.onTouch(ev);
        if (!consume || ev.getAction() != MotionEvent.ACTION_MOVE) {
            super.onTouchEvent(ev);
        } else {
            resetLastMotionX(ev.getX());
        }
        return true;
    }

    private void resetLastMotionX(float x) {
        try {
            Field mLastMotionX = ViewPager.class.getDeclaredField("mLastMotionX");
            mLastMotionX.setAccessible(true);
            mLastMotionX.set(this, x);

            Field mInitialMotionX = ViewPager.class.getDeclaredField("mInitialMotionX");
            mInitialMotionX.setAccessible(true);
            mInitialMotionX.set(this, x);
        } catch (Exception e) {
        }
    }

    public void resetImageMatrix() {
        ImageDetailActivity.ViewHolder tag = getCurrentViewHolder();
        tag.content_iv.resetImageMatrix();
    }

    public ImageDetailActivity.ViewHolder getCurrentViewHolder() {
        View item = adapter.getItem(getCurrentItem());
        ImageDetailActivity.ViewHolder tag = (ImageDetailActivity.ViewHolder) item.getTag();
        return tag;
    }

    // 滑动距离及坐标 归还父控件焦点
    private float xDistance, yDistance, xLast, yLast, xDown, mLeft;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();
                xDown = ev.getX();
                mLeft = ev.getX();// 解决与侧边栏滑动冲突
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();

                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                xLast = curX;
                yLast = curY;
                if (mLeft < 100 || xDistance < yDistance) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    if (getCurrentItem() == 0) {
                        if (curX < xDown) {
                            getParent().requestDisallowInterceptTouchEvent(true);
                        } else {
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    } else if (getCurrentItem() == (getAdapter().getCount() - 1)) {
                        if (curX > xDown) {
                            getParent().requestDisallowInterceptTouchEvent(true);
                        } else {
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    } else {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
