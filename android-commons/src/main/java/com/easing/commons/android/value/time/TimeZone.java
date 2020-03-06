package com.easing.commons.android.value.time;

import android.annotation.TargetApi;

import java.time.ZoneId;

import lombok.Getter;

@Getter
@TargetApi(26)
public enum TimeZone {

  DEFAULT(), GREENWICH("Greenwich"), BEIJING("Asia/Shanghai"), NEW_YORK("America/New_York"), LONDON("Europe/London");

  private String name;
  private ZoneId zoneId;

  private TimeZone() {
    this.name = ZoneId.systemDefault().getId();
    this.zoneId = ZoneId.of(name);
  }

  private TimeZone(String name) {
    this.name = name;
    this.zoneId = ZoneId.of(name);
  }
}
