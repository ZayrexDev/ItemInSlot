package xyz.zcraft.idk.util;

import java.io.Serializable;
import java.util.Calendar;

public class Mark implements Serializable {
    private String content;
    private String title;
    private Calendar time;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return title;
    }

    public Mark(String title, String content) {
        this.content = content;
        this.title = title;
        this.time = Calendar.getInstance();
    }
}
