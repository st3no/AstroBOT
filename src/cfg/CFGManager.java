package cfg;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

public class CFGManager {

    private File cfg;

    private String twitchServer;
    private String twitchPort;

    private String userPass;
    private String userName;
    private String userChannel;

    private ArrayList<CFGData> data;

    private HashMap commands;


    public CFGManager() {
        data = new ArrayList<>();
        commands = new HashMap<String, String>();

        try {
            cfg = new File(getClass().getResource("cfg.json").toURI());
        } catch (URISyntaxException e) {
            cfg = null;
            e.printStackTrace();
        }

        loadCFG();
    }


    public String getServer() {
        return twitchServer;
    }

    public String getPort() {
        return twitchPort;
    }

    public String getPass() {
        return userPass;
    }

    public String getNick() {
        return userName;
    }

    public String getChannel() {
        return userChannel;
    }

    public ArrayList<CFGData> getCFGData() {
        return data;
    }

    public HashMap getCommands() {
        return commands;
    }


    public void setPass(String pass) {
        userPass = pass;
        saveCFG();
    }

    public void setNick(String nick) {
        userName = nick.toLowerCase();
        setChannel("#" + userName);
        saveCFG();
    }

    private void setChannel(String channel) {
        userChannel = channel;
    }


    public void addCommand(String cmd, String msg) {
        data.add(new CFGData(cmd, msg));
        saveCFG();
    }

    public void editCommand(CFGData editData, String cmd, String msg) {
        int indexData = data.indexOf(editData);

        data.get(indexData).setCmd(cmd);
        data.get(indexData).setMsg(msg);

        saveCFG();
    }

    public void deleteCommand(CFGData deleteData) {
        data.remove(deleteData);
        saveCFG();
    }


    private void loadCFG() {
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(cfg));

            twitchServer = (String) jsonObject.get("twitch_server");
            twitchPort = (String) jsonObject.get("twitch_port");

            userPass = (String) jsonObject.get("user_pass");
            userName = (String) jsonObject.get("user_name");
            userChannel = (String) jsonObject.get("user_channel");

            JSONArray jsonArray = (JSONArray) jsonObject.get("commands");
            JSONObject jsonObj;
            String cmd, msg;

            for (Object obj : jsonArray) {
                jsonObj = (JSONObject) obj;
                cmd = (String) jsonObj.get("cmd");
                msg = (String) jsonObj.get("msg");

                data.add(new CFGData(cmd, msg));
                commands.put(cmd, msg);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void saveCFG() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("twitch_server", twitchServer);
        jsonObject.put("twitch_port", twitchPort);

        jsonObject.put("user_pass", userPass);
        jsonObject.put("user_name", userName);
        jsonObject.put("user_channel", userChannel);

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj;
        for (CFGData d : data) {
            jsonObj = new JSONObject();
            jsonObj.put("cmd", d.getCmd());
            jsonObj.put("msg", d.getMsg());
            jsonArray.add(jsonObj);
        }

        jsonObject.put("commands", jsonArray);

        try {
            FileWriter fw = new FileWriter(cfg);
            fw.write(jsonObject.toJSONString());
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
