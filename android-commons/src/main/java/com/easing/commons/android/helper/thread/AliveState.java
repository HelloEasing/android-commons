package com.easing.commons.android.helper.thread;

import lombok.Getter;

//控制线程自动结束的标志位
public class AliveState {

    @Getter
    private boolean alive = true;

    private AliveState() {
    }

    public static AliveState create() {
        return new AliveState();
    }

    public void resume() {
        alive = true;
    }

    public void kill() {
        alive = false;
    }
}
