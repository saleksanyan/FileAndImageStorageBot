package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * provides a function of processing the file
 */

public class FileProcessing {


    private static final String BOT_TOKEN = "6667454308:AAHQAO8Ik8hGkGmClGwp9KBzhQsIjHrk7wU";

    public static void processFile(Update update, Files file) {
        // Retrieve the Document object from the update
        Document document = update.getMessage().getDocument();

        if (document != null) {
            String fileId = document.getFileId();

            byte[] fileBytes = downloadFile(fileId);

            file.setFileData(fileBytes);

        }
    }

    private static byte[] downloadFile(String fileId) {
        try {
            String fileUrl = "https://api.telegram.org/bot" + BOT_TOKEN + "/getFile?file_id=" + fileId;

            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            String filePath = parseFilePathFromJson(connection.getInputStream());

            String downloadUrl = "https://api.telegram.org/file/bot" + BOT_TOKEN + "/" + filePath;

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
