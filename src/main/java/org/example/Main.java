package org.example;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * in the main method the program starts
 */

public class Main {

    public static void main(String[] args) throws TelegramApiException {

        // Instantiate Telegram Bots API
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

        // Register our bot
        try {
            botsApi.registerBot(new FileAndImageStorageBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}