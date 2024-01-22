package com.own.third.api.tg;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendPhoto;

import java.io.File;

public class TelegramBotHandler {

    private final TelegramBot bot;

    public TelegramBotHandler(String botToken) {
        this.bot = new TelegramBot(botToken);
    }

    public void sendImageToTg(String chatId, String imgPath) {
        SendPhoto sendPhotoReq = new SendPhoto(chatId, new File(imgPath));
        bot.execute(sendPhotoReq);
    }

}
