package com.easing.commons.android.ui.control.selector;

import com.easing.commons.android.value.option.Option;

public class OptionSelectorHandler {

    //获取选中option的id
    public static String optionId(OptionSelector<Option> optionSelector) {
        if (optionSelector.empty()) return null;
        return optionSelector.selectedOption().id;
    }

    //获取选中option的id，没有则返回控件的text
    public static String optionIdAllowException(OptionSelector<Option> optionSelector) {
        if (optionSelector.empty()) return optionSelector.getText().toString();
        return optionSelector.selectedOption().id;
    }

    //获取option的name，没有则返回text
    public static String optionNameAllowException(String id, Option[] options) {
        if (id == null) return "";
        for (Option option : options)
            if (id.equals(option.id))
                return option.name;
        return id;
    }

    //根据id来选中对应的option
    public static OptionSelector<Option> selectByOptionId(OptionSelector<Option> optionSelector, String id) {
        optionSelector.clearSelection();
        Option[] options = optionSelector.options();
        for (Option option : options)
            if (option.id.equals(id))
                optionSelector.select(option);
        return optionSelector;
    }

    //根据id来选中对应的option，没有则将id作为文本显示
    public static OptionSelector<Option> selectByOptionIdAllowException(OptionSelector<Option> optionSelector, String id) {
        optionSelector.clearSelection();
        Option[] options = optionSelector.options();
        for (Option option : options)
            if (option.id.equals(id))
                optionSelector.select(option);
        if (optionSelector.empty()) optionSelector.setText(id);
        return optionSelector;
    }
}
