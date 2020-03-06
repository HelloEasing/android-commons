package com.easing.commons.android.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public abstract class ViewAdapter<T> {
	
	protected ViewGroup parent;
	protected Context context;
	@Getter
	protected final List<T> datas = new ArrayList();
	
	public ViewAdapter bindParent(ViewGroup parent) {
		this.parent = parent;
		this.context = parent.getContext();
		return this;
	}
	
	public void unbind() {
		this.parent = null;
		this.context = null;
		this.datas.clear();
	}
	
	public ViewAdapter bindData(List<T> datas) {
		this.datas.clear();
		this.datas.addAll(datas);
		return this;
	}
	
	abstract public View createView(int pos);
	
	public void onItemClick(int pos) {
	}
	
	public boolean onItemLongClick(int pos) {
		return false;
	}
}
