package irc.message;

public class IRCMessageBuilder {

    public static IRCMessage build(String ch, String msg) {
        return new IRCMessage("PRIVMSG " + ch + " :" + msg + "\r\n");
    }
}
