package com.easing.commons.android.ui.control.edit;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.easing.commons.android.R;
import com.easing.commons.android.format.Texts;
import com.easing.commons.android.ui.dialog.TipBox;
import com.easing.commons.android.view.Views;

import java.util.ArrayList;
import java.util.List;

//带数据过滤功能的TextAdapter
public class FilterableTextAdapter extends BaseAdapter implements Filterable {

    protected AutoCompleteEdit autoCompleteEdit;

    private List<String> originItems = new ArrayList();
    private List<String> filteredItems = new ArrayList();

    private TextFilter filter = new TextFilter();

    private OnFilter onFilter;

    public void onFilter(OnFilter onFilter) {
        this.onFilter = onFilter;
    }

    public int getCount() {
        return filteredItems.size();
    }

    @Override
    public String getItem(int position) {
        return filteredItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View root = Views.inflate(parent.getContext(), R.layout.item_text_list);
        TextView text = root.findViewById(R.id.tv);
        text.setText(getItem(position));
        //点击设置文本，并关闭下拉框
        root.setOnClickListener(v -> {
            autoCompleteEdit.setText(text.getText());
            autoCompleteEdit.setSelection(text.getText().length());
            autoCompleteEdit.dismissDropDown();
        });
        return root;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    //重置全部可选数据
    public void reset(List<String> items) {
        originItems.clear();
        originItems.addAll(items);
    }

    //文本过滤器，用于定义匹配规则，更新匹配项
    public class TextFilter extends Filter {

        //数据过滤
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String key = constraint.toString().toLowerCase();
            List<String> temp = new ArrayList();
            //如果没有限制，则显示全部
            if (constraint.toString().isEmpty()) {
                temp.addAll(originItems);
                FilterResults results = new FilterResults();
                results.values = temp;
                results.count = temp.size();
                return results;
            }
            //如果不是拼音，以关键字开头，或包含关键字，则匹配
            for (String item : originItems)
                if (item != null)
                    if (item.toLowerCase().startsWith(key))
                        temp.add(item);
            for (String item : originItems)
                if (item.toLowerCase().contains(key) && !temp.contains(item))
                    temp.add(item);
            //如果是拼音，拼音以关键字开头，或简拼以关键字开头，则匹配
            if (key.matches("^[a-z]+$")) {
                for (String item : originItems) {
                    if (Texts.getSimplePinyin(item).startsWith(key) && !temp.contains(item))
                        temp.add(item);
                }
                for (String item : originItems) {
                    if (Texts.getPinyin(item).contains(key) && !temp.contains(item))
                        temp.add(item);
                }
            }
            //封装过滤结果
            FilterResults results = new FilterResults();
            results.values = temp;
            results.count = temp.size();
            return results;
        }

        //发布过滤结果
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //根据过滤结果去更新adapter
            if (results.count == 0)
                notifyDataSetInvalidated();
            else {
                filteredItems = (List<String>) results.values;
                notifyDataSetChanged();
            }
            //执行回调
            if (onFilter != null)
                onFilter.OnFilter((List<String>) results.values, results.count);
        }
    }

    public interface OnFilter {

        void OnFilter(List<String> items, int count);
    }

}
