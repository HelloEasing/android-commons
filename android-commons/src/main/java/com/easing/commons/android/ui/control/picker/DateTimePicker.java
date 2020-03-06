package com.easing.commons.android.ui.control.picker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import com.easing.commons.android.time.Times;

import java.util.Calendar;

public class DateTimePicker implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Context ctx;

    private DatePickerDialog datePicker;
    private TimePickerDialog timePicker;
    private TextView parent;
    private Calendar calendar;

    public static DateTimePicker show(Context ctx, TextView parent) {
        DateTimePicker picker = new DateTimePicker();
        picker.ctx = ctx;
        picker.parent = parent;
        picker.calendar = Times.getCalendar();
        picker.datePicker = new DatePickerDialog(picker.ctx, picker, picker.calendar.get(Calendar.YEAR), picker.calendar.get(Calendar.MONTH), picker.calendar.get(Calendar.DAY_OF_MONTH));
        picker.datePicker.show();
        return picker;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        calendar.set(year, month, day);
        showTimePicker();
    }

    public void showTimePicker() {
        timePicker = new TimePickerDialog(ctx, this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePicker.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        String datetime = Times.formatCalendar(calendar, Times.FORMAT_01);
        parent.setText(datetime);
    }
}
