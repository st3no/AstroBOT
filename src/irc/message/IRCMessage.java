package irc.message;

public class IRCMessage {

    private String message;

    public IRCMessage(String msg) {
        this.message = msg;
    }

    /**
     * @return IRCMessage without IRC prefix
     */
    public String getMessage() {
        if (message == null) return null;

        String userMessage = null;
        // inizia il messeggio irc (esclude il prefisso twitch tag)
        int initMessageIndex = message.indexOf(' ');
        int beginMessageIndex = -1;

        if (initMessageIndex > -1) beginMessageIndex = message.indexOf(':', initMessageIndex + 2);

        if (beginMessageIndex > -1) {
            try {
                userMessage = message.substring(beginMessageIndex + 1);
            } catch (IndexOutOfBoundsException e) {
                System.err.println(getClass().getName() + " -> " + e.getClass().getName() + " -> " + message);
                userMessage = null;
            }
        }

        return userMessage;
    }

    /**
     * @return Username of sender
     */
    public String getUser() {
        if (message == null) return null;

        String userName = null;
        // inizia il messeggio irc (esclude il prefisso twitch tag)
        int initMessageIndex = message.indexOf(' ');
        int beginNameIndex = -1;

        if (initMessageIndex > -1) beginNameIndex = message.indexOf(':', initMessageIndex);

        int endNameIndex = message.indexOf('!');

        if ((beginNameIndex > -1) && (endNameIndex > -1)) {
            try {
                userName = message.substring(beginNameIndex + 1, endNameIndex);
            } catch (IndexOutOfBoundsException e) {
                System.err.println(getClass().getName() + " -> " + e.getClass().getName() + " -> " + message);
                userName = null;
            }
        }

        return userName;
    }

    public String getTwitchTag() {
        if (message == null) return null;
        String twitchTag = null;

        int endMessageIndex = message.indexOf(' ');
        if (endMessageIndex > -1) {
            try {
                twitchTag = message.substring(0, endMessageIndex);
            } catch (IndexOutOfBoundsException e) {
                System.err.println(getClass().getName() + " -> " + e.getClass().getName() + " -> " + message);
                twitchTag = null;
            }
        }

        return twitchTag;
    }

    @Override
    public String toString() {
        return message;
    }
}
