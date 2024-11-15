package com;

import io.github.cdimascio.dotenv.Dotenv;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.net.URLEncoder;
import java.util.Base64;

public class Bot extends TelegramLongPollingBot {

    private static final Dotenv dotenv = Dotenv.load();

    private static final String BOT_TOKEN = dotenv.get("BOT_TOKEN");
    private static final String BOT_USERNAME = dotenv.get("BOT_USERNAME");
    private static final String SPOTIFY_CLIENT_ID = dotenv.get("SPOTIFY_CLIENT_ID");
    private static final String SPOTIFY_CLIENT_SECRET = dotenv.get("SPOTIFY_CLIENT_SECRET");
    private static final String YOUTUBE_API_KEY = dotenv.get("YOUTUBE_API_KEY");



    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String userMessage = message.getText();
            String chatId = message.getChatId().toString();

            if (userMessage.startsWith("/search")) {
                String query = userMessage.replace("/search", "").trim();
                if (query.isEmpty()) {
                    sendMessage(chatId, "Please, specify the song name after the command /search.");
                } else {
                    String youtubeResults = searchYouTube(query);
                    String spotifyResults = searchSpotify(query);
                    String response = "🎶 Search results for: " + query + "\n\n" +
                            "🔴 YouTube:\n" + youtubeResults + "\n" +
                            "🟢 Spotify:\n" + spotifyResults;
                    sendMessage(chatId, response);
                }
            } else {
                sendMessage(chatId, "Hi! I’m a music bot. Use the command /search <song name> to find music on YouTube and Spotify.");
            }
        }
    }

    private void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String searchYouTube(String query) {
        String encodedQuery;
        try {
            encodedQuery = URLEncoder.encode(query, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return "⚠️ An error occurred while encoding the request.";
        }

        String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&maxResults=3&q=" + encodedQuery + "&key=" + YOUTUBE_API_KEY;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            CloseableHttpResponse response = httpClient.execute(request);
            String jsonResponse = EntityUtils.toString(response.getEntity());

            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            JsonArray items = jsonObject.getAsJsonArray("items");

            if (items == null || items.size() == 0) {
                return "⚠️ No results found on YouTube.";
            }

            StringBuilder result = new StringBuilder();
            for (JsonElement item : items) {
                JsonObject video = item.getAsJsonObject();
                String title = video.getAsJsonObject("snippet").get("title").getAsString();
                String videoId = video.getAsJsonObject("id").get("videoId").getAsString();
                result.append("- ").append(title).append(" - https://www.youtube.com/watch?v=").append(videoId).append("\n");
            }

            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "⚠️ An error occurred while searching on YouTube.";
        }
    }

    private String getSpotifyAccessToken() {
        String auth = SPOTIFY_CLIENT_ID + ":" + SPOTIFY_CLIENT_SECRET;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost("https://accounts.spotify.com/api/token");
            request.setHeader("Authorization", "Basic " + encodedAuth);
            request.setHeader("Content-Type", "application/x-www-form-urlencoded");
            request.setEntity(new StringEntity("grant_type=client_credentials"));

            CloseableHttpResponse response = httpClient.execute(request);
            String jsonResponse = EntityUtils.toString(response.getEntity());

            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            return jsonObject.get("access_token").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String searchSpotify(String query) {
        String accessToken = getSpotifyAccessToken();
        if (accessToken == null) {
            return "⚠️ Failed to access Spotify API.";
        }

        String encodedQuery;
        try {
            encodedQuery = URLEncoder.encode(query, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return "⚠️ An error occurred while encoding the request.";
        }

        String url = "https://api.spotify.com/v1/search?type=track&limit=3&q=" + encodedQuery;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            request.setHeader("Authorization", "Bearer " + accessToken);

            CloseableHttpResponse response = httpClient.execute(request);
            String jsonResponse = EntityUtils.toString(response.getEntity());

            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            JsonArray items = jsonObject.getAsJsonObject("tracks").getAsJsonArray("items");

            if (items == null || items.size() == 0) {
                return "⚠️ No results found on Spotify.";
            }

            StringBuilder result = new StringBuilder();
            for (JsonElement item : items) {
                JsonObject track = item.getAsJsonObject();
                String title = track.get("name").getAsString();
                String artist = track.getAsJsonArray("artists").get(0).getAsJsonObject().get("name").getAsString();
                String trackUrl = track.get("external_urls").getAsJsonObject().get("spotify").getAsString();
                result.append("- ").append(title).append(" by ").append(artist).append(" - ").append(trackUrl).append("\n");
            }

            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "⚠️ An error occurred while searching on Spotify.";
        }
    }
}
