
# 🎶 JavaTelegramMusicBot

**JavaTelegramMusicBot** is a Telegram bot that helps you search for music on **YouTube** and **Spotify**. Just type the song name, and the bot will provide you with direct links to listen or watch your favorite tracks—all within Telegram! 🎧

---

## 🚀 Features

- 🔍 Search for songs on **YouTube** and **Spotify** with a single command.
- 🎵 Provides direct links to listen or watch the music.
- 💬 Simple and user-friendly Telegram interface.
- 🔐 Securely loads sensitive data like API keys from a `.env` file.

---

## 📋 Commands

| Command            | Description                                          |
|--------------------|------------------------------------------------------|
| `/search <query>`  | Search for a song on **YouTube** and **Spotify**.    |

---

## 🛠️ Setup Instructions

### Prerequisites

1. **Java** (version 8 or higher)
2. **Maven** for dependency management
3. A Telegram bot token (get one from [BotFather](https://t.me/botfather))
4. API keys for **YouTube** and **Spotify** ([YouTube Data API v3](https://console.cloud.google.com/marketplace/product/google/youtube.googleapis.com) и [Spotify for developer](https://developer.spotify.com/dashboard))

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/reqv1em/JavaTelegramMusicBot.git
   cd JavaTelegramMusicBot
   ```

2. Create a `.env` file in the project root:
   ```bash
   BOT_TOKEN=your_telegram_bot_token
   BOT_USERNAME=your_bot_username
   SPOTIFY_CLIENT_ID=your_spotify_client_id
   SPOTIFY_CLIENT_SECRET=your_spotify_client_secret
   YOUTUBE_API_KEY=your_youtube_api_key
   ```

3. Build the project:
   ```bash
   mvn clean install
   ```

4. Run the bot:
   ```bash
   java -jar target/FindMusicBot-1.0-SNAPSHOT.jar
   ```

---

## 📦 Example `.env` File

```env
BOT_TOKEN=your_telegram_bot_token
BOT_USERNAME=FindMusicBot
SPOTIFY_CLIENT_ID=your_spotify_client_id
SPOTIFY_CLIENT_SECRET=your_spotify_client_secret
YOUTUBE_API_KEY=your_youtube_api_key
```

---

## 🖼️ Preview

### Example Search
When you type `/search Close Eyes`, the bot responds with:

```
🎶 Search results for: Close Eyes

🔴 YouTube:
- DVRST - Close Eyes - https://www.youtube.com/watch?v=ao4RCon11eY

🟢 Spotify:
- Close Eyes by DVRST - https://open.spotify.com/track/3CLSHJv5aUROAN2vfOyCOh
```

---



## 📜 License

This project is licensed under the [MIT License](https://opensource.org/licenses/MIT).

---

## 🌟 Acknowledgements

- [Telegram Bots API](https://core.telegram.org/bots/tutorial)
- [YouTube Data API](https://developers.google.com/youtube/v3)
- [Spotify Web API](https://developer.spotify.com/documentation/web-api/)
