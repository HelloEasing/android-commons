package com.easing.commons.android.ui.control.selector;

import android.content.Context;
import android.util.AttributeSet;

import com.easing.commons.android.app.CommonActivity;
import com.easing.commons.android.ui.dialog.OptionDialog;
import com.easing.commons.android.ui.dialog.TipBox;

import androidx.appcompat.widget.AppCompatTextView;

//TODO
//下拉框式选项框
public class OptionCombo<T> extends AppCompatTextView {

    private T[] options;
    private T selectedOption;
    private int selectedIndex;

    public OptionCombo(Context context, AttributeSet attrSet) {
        super(context, attrSet);
        init(context, attrSet);
    }

    private void init(Context context, AttributeSet attrSet) {
        setOnClickListener(v -> {
            if (options == null || options.length == 0) {
                TipBox.tip("无可用选项");
                return;
            }
            OptionDialog.create((CommonActivity) context, options, (itemView, option, index) -> {
                setText(option.toString());
                selectedOption = option;
                selectedIndex = index;
            }).show();
        });
    }

    public OptionCombo<T> options(T[] options) {
        this.options = options;
        return this;
    }

    public OptionCombo<T> clearSelection() {
        selectedOption = null;
        selectedIndex = -1;
        return this;
    }

    public OptionCombo<T> select(int index) {
        if (index < 0 || index >= options.length)
            return clearSelection();
        this.selectedIndex = index;
        this.selectedOption = options[index];
        return this;
    }

    public OptionCombo<T> select(T option) {
        for (int i = 0; i < options.length; i++)
            if (options[i] == option) {
                this.selectedIndex = i;
                this.selectedOption = option;
                return this;
            }
        return this;
    }

    public T selectedOption() {
        return selectedOption;
    }

    public int selectedIndex() {
        return selectedIndex;
    }
}
