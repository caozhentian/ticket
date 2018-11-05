package zt.com.ti.ticket.entity;

/**
 * 作者：created by ztcao on 2018/11/5 14 : 10
 */
public class ApiInfo {
    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isOk(){
        return code == 0 ;
    }
}
