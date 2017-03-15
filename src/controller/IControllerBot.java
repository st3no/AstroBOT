package controller;

import bot.ErrorBot;
import bot.StatusBot;

public interface IControllerBot {
    void errorBot(ErrorBot error);
    void statusBot(StatusBot status);
}
