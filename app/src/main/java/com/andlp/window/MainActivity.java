package com.andlp.window;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayout; //j
    VideoView mVV, mVVWindows;//界面VideoView     悬浮窗口VideoView
    RelativeLayout mRelative;

    float x, y;       // 窗口宽高值
    View mWindowsView;//悬浮窗口布局

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mRelative = (RelativeLayout) findViewById(R.id.relative);
        mVV = (VideoView) findViewById(R.id.videoView);
        playVideo(mVV);
    }

    //播放视频
    private void playVideo(VideoView vv) {
        ContentResolver resolver = getContentResolver();
        Cursor c = resolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if (c.moveToNext()) {

            String path = c.getString(c.getColumnIndex(MediaStore.Video.Media.DATA));
            vv.setVideoPath(path);
            vv.requestFocus();
            vv.start();
        }
    }

    public void showWindow(View vv) {
        mRelative.setVisibility(View.GONE);
        // 取得系统窗体
//        mWindowManager = (WindowManager) getApplicationContext() .getSystemService("window");

        mWindowManager =(WindowManager) MainActivity.this.getSystemService(Context.WINDOW_SERVICE);
        // 窗体的布局样式
        mLayout = new WindowManager.LayoutParams();
        // 设置窗体显示类型——TYPE_SYSTEM_ALERT(系统提示)
        mLayout.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        // 设置窗体焦点及触摸：
        // FLAG_NOT_FOCUSABLE(不能获得按键输入焦点)
        mLayout.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 设置显示的模式
        mLayout.format = PixelFormat.RGBA_8888;
        // 设置对齐的方法
        mLayout.gravity = Gravity.TOP | Gravity.LEFT;
        // 设置窗体宽度和高度
        mLayout.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayout.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //将制定View解析后添加到窗口管理器里面
        mWindowsView = View.inflate(this, R.layout.windows_layout, null);
        mVVWindows = (VideoView) mWindowsView.findViewById(R.id.videoView);
        playVideo(mVVWindows);
        mWindowManager.addView(mWindowsView, mLayout);

        mWindowsView.setOnTouchListener(new View.OnTouchListener() {
            float mTouchStartX;
            float mTouchStartY;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                x = event.getRawX();
                y = event.getRawY() - 25;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mTouchStartX = event.getX();
                        mTouchStartY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //原始坐标减去移动坐标
                        mLayout.x = (int) (x - mTouchStartX);
                        mLayout.y = (int) (y - mTouchStartY);
                        mWindowManager.updateViewLayout(mWindowsView, mLayout);
                        Log.i("main", "x值=" + x + "\ny值=" + y + "\nmTouchX" + mTouchStartX + "\nmTouchY=" + mTouchStartY);
                        break;
                }
                return true;
            }
        });
    }

    //关闭窗口点击事件
    public void ivDelete(View view) {
        mWindowManager.removeView(mWindowsView);
        mRelative.setVisibility(View.VISIBLE);
    }

}
