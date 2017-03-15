package bot.command;

import cfg.CFGManager;

public class CommandParser {

    public static String parseCommandMessage(String msg) {
        try {
            String command = msg.split(" ")[0];
            return (String) new CFGManager().getCommands().get(command);
        } catch (NullPointerException e) {
            return null;
        }
    }
}
