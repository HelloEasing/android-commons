package com.easing.commons.android.ui.control.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.easing.commons.android.ui.adapter.ViewAdapter;

import lombok.Getter;

public class FlowLayout extends ViewGroup {
	
	private Context context;
	@Getter
	private ViewAdapter adapter;
	
	public FlowLayout(Context context) {
		super(context, null, 0);
	}
	
	public FlowLayout(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
	}
	
	public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs, defStyleAttr);
	}
	
	private void init(Context context, AttributeSet attrSet, int style) {
		this.context = context;
	}
	
	@Override
	protected void onMeasure(int wSpec, int hSpec) {
		int mode_w = MeasureSpec.getMode(wSpec);
		int mode_h = MeasureSpec.getMode(hSpec);
		int size_w = MeasureSpec.getSize(wSpec);
		int size_h = MeasureSpec.getSize(hSpec);
		
		//流布局必须明确指定宽度
		if (mode_w == MeasureSpec.AT_MOST)
			throw new RuntimeException("FlowLayout must exactly specify width");
		
		int usedWidth = 0;
		int remainWidth = 0;
		int totalHeight = 0; //所有子View的高度和
		int lineHeight = 0; //当前子View的高度
		int maxLineHeight = 0; //每个View的高度都不一样，记录同一行需要的最大高度
		
		//循环遍历子View，测量总尺寸
		for (int i = 0; i < getChildCount(); i++) {
			remainWidth = size_w - usedWidth - getPaddingLeft() - getPaddingRight();
			View child = getChildAt(i);
			measureChild(child, wSpec, hSpec);
			
			//当剩余空间不足以放下子View时换行
			if (remainWidth < child.getMeasuredWidth()) {
				//累加高度，作为当前行高度
				totalHeight += maxLineHeight;
				//切换到下一行
				maxLineHeight = 0;
				usedWidth = 0;
			}
			
			//宽度累加
			MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
			usedWidth += lp.leftMargin + lp.rightMargin + child.getMeasuredWidth();
			//高度取最大值
			lineHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
			maxLineHeight = Math.max(lineHeight, maxLineHeight);
		}
		
		//子View最终高度和
		totalHeight += maxLineHeight + getPaddingTop() + getPaddingBottom();
		//根据约束确定ViewGroup高度
		if (mode_h == MeasureSpec.AT_MOST && totalHeight > size_h)
			size_h = Math.min(totalHeight, size_h);
		super.setMeasuredDimension(size_w, size_h);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		final int width = right - left;
		final int paddingLeft = getPaddingLeft();
		final int paddingRight = getPaddingRight();
		final int paddingTop = getPaddingTop();
		
		int childTop = paddingTop;
		int childLeft = paddingLeft;
		
		int usedWidth = 0;
		int remainWidth = width - paddingLeft - paddingRight;
		int totalHeight = paddingTop; //所有子View的高度和
		int lineHeight = 0; //当前子View的高度
		int maxLineHeight = 0; //每个View的高度都不一样，记录同一行需要的最大高度
		
		//遍历子View，确定位置
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			int childWidth = child.getMeasuredWidth();
			int childHeight = child.getMeasuredHeight();
			MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
			usedWidth += lp.leftMargin + lp.rightMargin + childWidth;
			lineHeight = childHeight + lp.topMargin + lp.bottomMargin;
			
			//当剩余空间不足以放下子View时换行
			if (remainWidth < usedWidth) {
				//确定在下一行的位置
				totalHeight += maxLineHeight;
				childLeft = paddingLeft;
				childTop = totalHeight;
				//切换到下一行
				maxLineHeight = 0;
				usedWidth = lp.leftMargin + childWidth + lp.rightMargin;
			}
			maxLineHeight = Math.max(lineHeight, maxLineHeight);
			
			//定位子View
			child.layout(childLeft + lp.leftMargin, childTop + lp.topMargin, childLeft + lp.leftMargin + childWidth, childTop + lp.topMargin + childHeight);
			
			//初始化下个子View位置
			childLeft = childLeft + lp.leftMargin + childWidth + lp.rightMargin;
		}
	}
	
	//通过Adapter来AddView
	public void setViewAdapter(ViewAdapter adapter) {
		this.adapter = adapter;
		removeAllViews();
		for (int i = 0; i < adapter.getDatas().size(); i++) {
			View view = adapter.createView(i);
			final int pos = i;
			view.setOnClickListener(v -> adapter.onItemClick(pos));
			view.setOnLongClickListener(v -> adapter.onItemLongClick(pos));
			addView(view);
		}
		requestLayout();
	}
	
	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		MarginLayoutParams lp = new MarginLayoutParams(getContext(), attrs);
		return lp;
	}
	
	@Override
	protected LayoutParams generateDefaultLayoutParams() {
		MarginLayoutParams lp = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.setMargins(10, 10, 10, 10);
		return lp;
	}
	
	@Override
	protected LayoutParams generateLayoutParams(LayoutParams p) {
		MarginLayoutParams lp = new MarginLayoutParams(p);
		return lp;
	}
}



