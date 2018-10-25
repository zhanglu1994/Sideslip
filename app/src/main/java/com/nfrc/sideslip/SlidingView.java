package com.nfrc.sideslip;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;

/**
 * Created by zhangl on 2018/10/25.
 */

public class SlidingView extends HorizontalScrollView {



    // 手势处理类
    private GestureDetector mGestureDetector;


    // 菜单的宽度
    private int mMenuWidth;

    private View mContentView,mMenuView;


    private boolean isOpen = false;

    //是否拦截
    private boolean isIntercept = false;

    public SlidingView(Context context) {
        this(context,null);
    }

    public SlidingView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SlidingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SlidingMenu);
        float rightMargin = array.getDimension(
                R.styleable.SlidingMenu_menuRightMargin, ScreenUtils.dip2px(context, 100));
        // 菜单页的宽度是 = 屏幕的宽度 - 右边的一小部分距离（自定义属性）
        mMenuWidth = (int) (getScreenWidth(context) - rightMargin);

        mGestureDetector = new GestureDetector(context, mGestureListener);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        isIntercept = false;

        if (isOpen){
           float currentX = ev.getX();

           if (currentX > mMenuWidth){
               closeMenu();
               isIntercept = true;

               return true;
           }

        }


        return super.onInterceptTouchEvent(ev);
    }

    private GestureDetector.OnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {



            if (isOpen){

                if (velocityX < 0){
                    closeMenu();
                }

            }else {
                if (velocityX > 0){
                    openMenu();
                }

            }

            return true;

        }
    };









    /**
     * 布局加载完成  才会回调
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        //指定宽高：1.内容页宽度  是屏幕的宽度


        ViewGroup container = (ViewGroup) getChildAt(0);

        // 2.给其指定宽度
        mMenuView =  container.getChildAt(0);
        // 2.1 指定菜单的宽度 LayoutParams 是布局的一些属性

        mMenuView.getLayoutParams().width = mMenuWidth;

        mContentView = container.getChildAt(1);
        // 2.1 指定内容的宽度  指定宽高会重新摆放  onLayout() 方法
        mContentView.getLayoutParams().width = ScreenUtils.getScreenWidth(getContext());



    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);


        float scale = 1f * l/mMenuWidth;   //  scale -> 1 - 0
        float rightScale = 0.7f + 0.3f*scale;

        //设置右边的缩放
        ViewCompat.setPivotX(mContentView,0);
        ViewCompat.setPivotY(mContentView,mContentView.getMeasuredHeight()/2);
        ViewCompat.setScaleX(mContentView,rightScale);
        ViewCompat.setScaleY(mContentView,rightScale);


        //左边动画
        float leftAlpha = 0.5f + (1 - scale)*0.5f;
        ViewCompat.setAlpha(mMenuView,leftAlpha);

        float leftScale = 0.5f + (1 - scale)*0.3f;
        ViewCompat.setScaleX(mMenuView,leftScale);
        ViewCompat.setScaleY(mMenuView,leftScale);


        ViewCompat.setTranslationX(mMenuView,l*0.7f);

    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //初始化进来关闭
        scrollTo(mMenuWidth,0);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (isIntercept){

            return true;
        }

        if (mGestureDetector.onTouchEvent(ev)){
            return true;
        }


       if (ev.getAction() == MotionEvent.ACTION_UP){
           // 根据手指滚动距离判断
           int currentScroll = getScrollX();
           if (currentScroll < mMenuWidth /2){ //关闭
               openMenu();
           }else {      //打开

               closeMenu();
           }
           return false;

       }

        return super.onTouchEvent(ev);
    }

    private void openMenu() {
        smoothScrollTo(0,0);
        isOpen = true;
    }

    private void closeMenu() {
        smoothScrollTo(mMenuWidth,0);
        isOpen = false;
    }

    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    private int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }


}
