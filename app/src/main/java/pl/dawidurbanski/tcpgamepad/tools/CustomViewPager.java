package pl.dawidurbanski.tcpgamepad.tools;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by dawid on 30.01.2016.
 */
public class CustomViewPager extends ViewPager {

    private boolean enabled = true;

    private void init() {
        this.enabled = true;
    }

    public CustomViewPager(Context context)
    {
        super(context);
        init();
    }

    public CustomViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.enabled)
            return super.onTouchEvent(event);
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.enabled)
            return super.onInterceptTouchEvent(event);
        return false;
    }

    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
