package cfg;

import javafx.beans.property.SimpleStringProperty;

public class CFGData {
    private SimpleStringProperty cmd;
    private SimpleStringProperty msg;

    public CFGData(String cmd, String msg) {
        this.cmd = new SimpleStringProperty(cmd);
        this.msg = new SimpleStringProperty(msg);
    }

    public String getCmd() {
        return cmd.get();
    }

    public void setCmd(String cmd) {
        this.cmd.set(cmd);
    }

    public String getMsg() {
        return msg.get();
    }

    public void setMsg(String msg) {
        this.msg.set(msg);
    }

}