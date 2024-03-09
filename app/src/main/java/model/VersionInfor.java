package model;

public class VersionInfor {
    private boolean success;
    private String status, msg;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public VersionInfor(boolean success, String status, String msg) {
        this.success = success;
        this.status = status;
        this.msg = msg;
    }
}
