package com.easing.commons.android.ui.control.tab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import androidx.appcompat.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.LinearLayout;

import com.easing.commons.android.view.Views;
import com.easing.commons.android.R;
import com.easing.commons.android.value.color.Colors;

import java.util.ArrayList;

/**
 * 调用bindTitles时才会显示
 */
@SuppressLint("DrawAllocation")
public class TabIndicator extends LinearLayout {
    public static final int BUTTON_HEIGHT = 120;

    private int tabCount;
    private int tabWidth;
    private int tabHeight;
    private int pageCount;

    private int offsetX;

    private Bitmap flagBitmap;
    private Bitmap backBitmap;
    private Paint paint;

    private ArrayList<String> titles;
    private ArrayList<Button> buttons;
    private ViewPager pager;

    public TabIndicator(Context context) {
        super(context);
        init(context, null);
    }

    public TabIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TabIndicator(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        init(context, attrs);
    }

    // 初始化
    private void init(Context context, AttributeSet attrSet) {
        //解析属性
        TypedArray attrs = context.obtainStyledAttributes(attrSet, R.styleable.TabIndicator);
        this.tabCount = attrs.getInt(R.styleable.TabIndicator_tabCount, 3);
        this.tabWidth = (int) Views.getScreenSize(context).w / tabCount;
        this.tabHeight = this.tabWidth / 8;
        this.pageCount = this.tabCount;
        attrs.recycle();

        //初始化图像
        this.backBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tab_indicator_slider);
        this.flagBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tab_indicator_dot);

        //初始化画笔
        this.paint = new Paint();

        //解决onDraw不执行的BUG
        super.setOrientation(LinearLayout.HORIZONTAL);
        super.setBackgroundResource(R.drawable.color_transparent);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Rect rt1 = new Rect(0, BUTTON_HEIGHT - 5, pageCount * tabWidth, BUTTON_HEIGHT + tabHeight - 5);
        Rect rt2 = new Rect(offsetX, BUTTON_HEIGHT - 5, offsetX + tabWidth, BUTTON_HEIGHT + tabHeight - 5);
        canvas.drawBitmap(backBitmap, null, rt1, paint);
        canvas.drawBitmap(flagBitmap, null, rt2, paint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        //由于使用的图片原因，滑动条的高度必须是宽度的1/8，才能保证图片不失真
        tabWidth = (int) Views.getScreenSize(super.getContext()).w / tabCount;
        tabHeight = tabWidth / 8;
    }

    //设置标题，根据标题添加按钮
    public void bindTitles(ArrayList<String> titles) {
        this.titles = titles;
        this.buttons = new ArrayList<Button>();
        this.pageCount = titles.size();
        super.removeAllViews();

        for (String title : titles) {
            Button bt = createButton(title);
            this.buttons.add(bt);
            super.addView(bt);
        }

        super.setLayoutParams(new LayoutParams(Views.WRAP_CONTENT, BUTTON_HEIGHT + tabHeight - 5));
    }

    //绑定ViewPager
    public void bindPager(ViewPager viewPager) {
        this.pager = viewPager;
        this.pager.addOnPageChangeListener(new OnPageChangeListener() {
            public void onPageSelected(int pos) {
                //按钮同步选中
                activate(pos);
            }

            public void onPageScrolled(int pos, float offsetPercent, int offset) {
                //指示器同步滚动
                scroll(pos, offsetPercent);
            }

            public void onPageScrollStateChanged(int state) {
            }
        });

        //激活第一项
        activate(0);
    }

    private AppCompatButton createButton(String title) {
        AppCompatButton bt = new AppCompatButton(super.getContext());
        LayoutParams params = new LayoutParams(tabWidth, BUTTON_HEIGHT);
        bt.setLayoutParams(params);
        bt.setText(title);
        bt.setTextColor(Colors.WHITE);
        bt.setGravity(Gravity.CENTER);
        bt.setBackgroundResource(R.drawable.button_m04);
        bt.setAllCaps(false);
        //按钮和ViewPager关联
        bt.setOnTouchListener((v, e) -> {
            if (e.getAction() != MotionEvent.ACTION_DOWN)
                return false;
            int index = this.buttons.indexOf(bt);
            pager.setCurrentItem(index);
            return false;
        });
        return bt;
    }

    public void activate(int pos) {
        for (Button bt : buttons)
            bt.setSelected(false);
        buttons.get(pos).setSelected(true);
    }

    private void scroll(int pos, float offsetPercent) {
        // 这里需要注意下ViewPager的滚动值表示规则：
        // pos永远是正值，表示当前屏幕左页的index
        // offsetPercent永远是值左页的偏移左边缘的比例
        // 最后一页无法再向左移动，所以offsetPercent永远是0

        // 计算总偏移量
        offsetX = (int) (tabWidth * (pos + offsetPercent));

        // 当页数超出Tab数时，需要滚动显示
        // 首屏从倒数第二个按钮对应的页开始更新
        // 由于首屏是从倒数第二个按钮开始更新的
        // 所以到达末屏倒数第二个按钮，Indicator就已经滚动到末尾，不需要继续滚动了
        if (pageCount > tabCount)
            if (pos >= tabCount - 2 && pos < pageCount - 2)
                super.scrollTo((int) ((pos - tabCount + 2 + offsetPercent) * tabWidth), 0);

        super.invalidate();
    }

}
