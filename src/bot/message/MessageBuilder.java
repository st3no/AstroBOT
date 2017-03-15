package bot.message;

public class MessageBuilder {

    private static final String NAME_TAG = "display-name=";

    public static String build(String commandMessage, String twitchTag) {
        if (commandMessage == null || twitchTag == null) return null;
        String twitchName = "";

        int beginNameIndex = twitchTag.indexOf(NAME_TAG) + NAME_TAG.length();
        int endNameIndex = -1;

        if (beginNameIndex > NAME_TAG.length()) endNameIndex = twitchTag.indexOf(";", beginNameIndex);

        if ((beginNameIndex > -1) && (endNameIndex > -1)) {
            try {
                twitchName = twitchTag.substring(beginNameIndex, endNameIndex);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                twitchName = "";
            }
        }
        
        try {
            return commandMessage.replace("$user", twitchName);
        } catch (NullPointerException e) {
            return null;
        }
    }
}

