package bot;

import bot.command.CommandParser;
import bot.message.MessageBuilder;
import cfg.CFGManager;
import controller.IControllerBot;
import irc.Client;
import irc.message.IRCMessage;
import irc.message.IRCMessageBuilder;

import java.io.IOException;

public class Bot extends Thread {

    private Client client;
    private IControllerBot controller;
    private boolean isRunning;

    public Bot(Client client, IControllerBot controller) {
        this.client = client;
        this.controller = controller;
    }

    @Override
    public void start() {
        super.start();
        isRunning = true;
    }

    public void stopp() {
        isRunning = false;
        client.stop();
    }

    @Override
    public void run() {
        IRCMessage ircMessageReceived;
        String messageCommand;
        String message;

        try {
            while (isRunning && client.isConnected()) {
                ircMessageReceived = new IRCMessage(client.read());

                /*  -----------------------------------DEBUG PRINT-----------------------------------
                    System.out.println("IRCFull>>  " + ircMessageReceived.toString() + "\r\n");
                    System.out.println("IRCMessage>>  " + ircMessageReceived.getMessage() + "\r\n");
                    System.out.println("IRCUser>>  " + ircMessageReceived.getUser() + "\r\n");
                    System.out.println("IRCTwitchTag>>  " + ircMessageReceived.getTwitchTag() + "\r\n"); */

                messageCommand = CommandParser.parseCommandMessage(ircMessageReceived.getMessage());
                message = MessageBuilder.build(messageCommand, ircMessageReceived.getTwitchTag());

                if (message != null)
                    client.write(IRCMessageBuilder.build(new CFGManager().getChannel(), message).toString());

                if ("PING :tmi.twitch.tv".equals(ircMessageReceived.toString())) {
                    client.write("PONG :tmi.twitch.tv\r\n");
                } else if (":tmi.twitch.tv NOTICE * :Invalid NICK".equals(ircMessageReceived.toString())) {
                    System.out.println("ERROR NICK!");
                    isRunning = false;
                    client.stop();
                    controller.errorBot(ErrorBot.NICK);
                    controller.statusBot(StatusBot.STOPPED);
                } else if (":tmi.twitch.tv NOTICE * :Improperly formatted auth".equals(ircMessageReceived.toString())) {
                    System.out.println("ERROR PASS!");
                    isRunning = false;
                    client.stop();
                    controller.errorBot(ErrorBot.PASS);
                    controller.statusBot(StatusBot.STOPPED);
                }

                sleep(100);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
