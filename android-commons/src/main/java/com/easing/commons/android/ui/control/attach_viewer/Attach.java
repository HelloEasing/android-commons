package com.easing.commons.android.ui.control.attach_viewer;

import com.easing.commons.android.helper.data.JsonSerial;
import com.easing.commons.android.io.Files;

import lombok.Data;

@Data
public class Attach implements JsonSerial {

    public String path;
    public String name;
    public String type = "";
    public String description;

    public static Attach create(String path, String name) {
        Attach attach = new Attach();
        attach.path = path;
        attach.name = name == null ? Files.getFileName(path) : name;
        attach.type = Files.getExtensionName(path);
        return attach;
    }

    public boolean isFile() {
        return Files.isFile(path);
    }
}
