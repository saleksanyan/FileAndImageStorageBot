package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;
import java.io.IOException;
import java.util.List;

/**
 * provides a function of processing the images
 */
public class ImageProcessing {

    private static final String BOT_TOKEN = "6667454308:AAHQAO8Ik8hGkGmClGwp9KBzhQsIjHrk7wU";
    
    public static void processImage(Update update, Images image) {
        List<PhotoSize> photoSizes = update.getMessage().getPhoto();

        if (!photoSizes.isEmpty()) {
            PhotoSize largestPhoto = photoSizes.get(photoSizes.size() - 1);

            String fileId = largestPhoto.getFileId();

            byte[] imageBytes = downloadImage(fileId);
            image.setImageData(imageBytes);
        }
    }

    private static byte[] downloadImage(String fileId) {
        try {
            String imageUrl = "https://api.telegram.org/bot" + BOT_TOKEN + "/getFile?file_id=" + fileId;

            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            String filePath = parseFilePathFromJson(connection.getInputStream());

            System.out.println("File Path: " + filePath);

            String downloadUrl = "https://api.telegram.org/file/bot" + BOT_TOKEN + "/" + filePath;

            // Debug: Print the download URL
            System.out.println("Download URL: " + downloadUrl);

            URL downloadUrlObject = new URL(downloadUrl);
            HttpURLConnection downloadConnection = (HttpURLConnection) downloadUrlObject.openConnection();

            return downloadConnection.getInputStream().readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }


    private static String parseFilePathFromJson(InputStream inputStream) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper(); // Assuming you have Jackson library available
        JsonNode rootNode = objectMapper.readTree(inputStream);

        if (rootNode.has("ok") && rootNode.get("ok").asBoolean()) {
            JsonNode resultNode = rootNode.get("result");
            if (resultNode.has("file_path")) {
                return resultNode.get("file_path").asText();
            }
        }

        return "";
    }


}

