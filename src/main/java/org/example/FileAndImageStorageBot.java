package org.example;
import static java.lang.Math.toIntExact;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.hibernate.Session;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * class that creates a bot for storing file and image
 */
public class FileAndImageStorageBot extends TelegramLongPollingBot {

    private static final SessionFactory SESSION_FACTORY =
            HibernateConfig.getSessionFactory();


    /**
     * running everytime when the user sends a message
     * @param update user's update
     */
    @Override
    public void onUpdateReceived(Update update) {
        Session session = SESSION_FACTORY.openSession();
        Transaction transaction = session.beginTransaction();
        Message updatedMessage = update.getMessage();
        String user_username = updatedMessage.getChat().getUserName();

        long user_id = updatedMessage.getChat().getId();
        long chat_id = updatedMessage.getChatId();
        SendMessage message = new SendMessage();
        message.setText("Here you go!");
        message.setChatId(chat_id);
        if ((update.hasMessage()) && (updatedMessage.hasText() || updatedMessage.hasPhoto() || updatedMessage.hasDocument() )) {
            if (updatedMessage.hasText() && updatedMessage.getText().equals("/start")) {
                checkingUser(session, user_id, user_username, message);
            } else if (updatedMessage.hasText() && updatedMessage.getText().equals("/addImage")) {
                message.setText("Please send the image");
            } else if (updatedMessage.hasText() && updatedMessage.getText().equals("/addFile")) {

                message.setText("Please send the file");

            } else if (updatedMessage.hasText() && updatedMessage.getText().equals("/Files")) {
                getFiles(session, user_id, chat_id, message);
            } else if (updatedMessage.hasText() && updatedMessage.getText().equals("/Images")) {
                getImages(session, user_id, chat_id, message);
            }else if (updatedMessage.hasText() && updatedMessage.getText().equals("/SpecificFileAndImage")) {
                message.setText("Please enter the ID of the file and/or image");
            } else if (updatedMessage.hasText() && updatedMessage.getText().matches("\\d+") && !updatedMessage.hasDocument() && !updatedMessage.hasDocument()) {
                getFileByID(session, updatedMessage, user_id, chat_id, message);
                getImageByID(session, updatedMessage, user_id, chat_id, message);
            }else if (updatedMessage.hasDocument()) {

                addFile(update, updatedMessage, session, user_id, message);
            }
            else if (updatedMessage.hasPhoto()) {
                addImage(update, updatedMessage, session, user_id, message);
            } else{
                    message.setText("Unknown command");
            }
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            transaction.commit();
            session.close();
        }
    }


    /**
     * adds the image to the database
     * @param update current user's update
     * @param updatedMessage current user's message
     * @param session current session
     * @param user_id current user's id
     * @param message the message that we should send to the user
     */
    private static void addImage(Update update, Message updatedMessage, Session session, long user_id, SendMessage message) {
        Images image = new Images();

        if(updatedMessage.getCaption()!=null)
            image.setImageName(updatedMessage.getCaption());
        else
            image.setImageName(String.valueOf(updatedMessage.getMessageId()));

        image.setUser(Logging.getById(session, user_id));

        ImageProcessing.processImage(update, image);
        session.persist(image);
        session.flush();

        message.setText("Image has been successfully saved");
    }

    /**
     * adds the file to the database
     * @param update current user's update
     * @param updatedMessage current user's message
     * @param session current session
     * @param user_id current user's id
     * @param message the message that we should send to the user
     */
    private static void addFile(Update update, Message updatedMessage, Session session, long user_id, SendMessage message) {
        Files file = new Files();

        if(updatedMessage.getCaption()!=null)
            file.setFileName(updatedMessage.getCaption());
        else
            file.setFileName(String.valueOf(updatedMessage.getMessageId()));

        file.setUser(Logging.getById(session, user_id));

        FileProcessing.processFile(update, file);
        session.persist(file);
        session.flush();


        message.setText("File has been successfully saved");
    }

    /**
     * get a file from the database considering the file's id and user's id
     * @param session current session
     * @param updatedMessage current user's message
     * @param user_id current user's id
     * @param chat_id current user's chat id
     * @param message the message that we should send to the user
     */

    private void getFileByID(Session session, Message updatedMessage, long user_id, long chat_id, SendMessage message) {
        Query<Files> getFile = session.createNamedQuery("get_file_by_id", Files.class);
        getFile.setParameter("fileID", Long.parseLong(updatedMessage.getText()));
        getFile.setParameter("userID", user_id);
        getFile.addQueryHint(updatedMessage.getText());
        List<Files> files = getFile.getResultList();

        if (files != null && !files.isEmpty()) {
            byte[] fileData = files.get(0).getFileData();

            ByteArrayInputStream inputStream = new ByteArrayInputStream(fileData);

            InputFile file = new InputFile(inputStream, "File");

            SendDocument sendDocument = new SendDocument();
            sendDocument.setChatId(chat_id);
            sendDocument.setDocument(file);
            sendDocument.setCaption("File ID: " + files.get(0).getFileID());

            try {
                execute(sendDocument);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else {
            message.setText("File not found");

        }
    }


    /**
     * get an image from the database considering the image's id and user's id
     * @param session current session
     * @param updatedMessage current user's message
     * @param user_id current user's id
     * @param chat_id current user's chat id
     * @param message the message that we should send to the user
     */
    private void getImageByID(Session session, Message updatedMessage, long user_id, long chat_id, SendMessage message) {
        Query<Images> getImages = session.createNamedQuery("get_image_by_id", Images.class);
        getImages.setParameter("imageID", Long.parseLong(updatedMessage.getText()));
        getImages.setParameter("userID", user_id);
        List<Images> images = getImages.getResultList();
        if (images != null && !images.isEmpty()) {
            byte[] imageData = images.get(0).getImageData();

            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);

            InputFile photo = new InputFile(inputStream, "Image");
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chat_id);
            sendPhoto.setPhoto(photo);
            sendPhoto.setCaption("Image ID: " + images.get(0).getImageID());
            try {
                execute(sendPhoto);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        } else {
            message.setText("Image not found");

        }
    }

    /**
     * get all images from the database considering that users ID
     * @param session current session
     * @param user_id current user's id
     * @param chat_id current user's chat id
     * @param message the message that we should send to the user
     */

    private void getImages(Session session, long user_id, long chat_id, SendMessage message) {
        Query<Images> getImages = session.createNamedQuery("get_images", Images.class);
        getImages.setParameter("userID", user_id);
        List<Images> images = getImages.getResultList();

        if (!images.isEmpty()) {
            for (Images image : images) {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(image.getImageData());

                InputFile photo = new InputFile(inputStream, "Image");
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(chat_id);
                sendPhoto.setPhoto(photo);
                sendPhoto.setCaption("Image ID: " + image.getImageID());
                try {
                    execute(sendPhoto);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        } else {
            message.setText("There are no images in the database");
        }
    }

    /**
     * get all files from the database considering that users ID
     * @param session current session
     * @param user_id current user's id
     * @param chat_id current user's chat id
     * @param message the message that we should send to the user
     */
    private void getFiles(Session session, long user_id, long chat_id, SendMessage message) {
        Query<Files> getFiles = session.createNamedQuery("get_files", Files.class);
        getFiles.setParameter("userID", user_id);
        List<Files> files = getFiles.getResultList();

        if (!files.isEmpty()) {
            for (Files value : files) {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(value.getFileData());

                InputFile file = new InputFile(inputStream, "File");

                SendDocument sendDocument = new SendDocument();
                sendDocument.setChatId(chat_id);
                sendDocument.setDocument(file);
                sendDocument.setCaption("File ID: " + value.getFileID());

                try {
                    execute(sendDocument);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        } else {
            message.setText("There are no files in the database");
        }
    }

    /**
     * checking the id and username of the user before starting the program
     */
    private static void checkingUser(Session session, long user_id, String user_username, SendMessage message) {
        String check;
        check = Logging.check(session, (long) toIntExact(user_id), user_username);
        message.setText("Here is your keyboard");

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        row1.add("/addImage");
        row1.add("/addFile");
        if (check.equals("exists")) {
            row1.add("/Files");
            row1.add("/Images");
            row2.add("/SpecificFileAndImage");
        }
        keyboard.add(row1);
        keyboard.add(row2);

        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);
    }

    /**
     *gets the bots username
     */
    @Override
    public String getBotUsername() {
        // Return bot username
        // If bot username is @MyAmazingBot, it must return 'MyAmazingBot'
        return "File_And_Image_Bot";
    }

    /**
     *gets the bots token
     */
    @Override
    public String getBotToken() {
        // Return bot token from BotFather
        return "6667454308:AAHQAO8Ik8hGkGmClGwp9KBzhQsIjHrk7wU";
    }





}
