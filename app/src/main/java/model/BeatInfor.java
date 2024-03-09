package model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BeatInfor implements Serializable {
    @SerializedName("filename")
    @Expose
    private String filename;
    @SerializedName("data")
    @Expose
    private String data;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @NonNull
    @Override
    public String toString() {
        return "beat:\n" + "filename:" + filename + "\n" + "data: " + data;
    }
}
