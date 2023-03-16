# Broadcast messages from Kafka Telegram Bot
Service get message from Kafka and send it to user by chat id.

# How it starts
1. Download _docker-compose.yml_ file.
2. Create bot or get Telegram bot API token. Use [@BotFather](https://t.me/BotFather)
3. Insert Telegram bot API token in _docker-compose.yml_.
4. Start service `docker-compose up`

# Connect to Kafka
If your application will run at docker compose. Make sure you have added the network settings as follows:
```docker
networks:
  default:
    external:
      name: telegram_messages_sender_net 
```
#### Default settings Kafka server:
* Group-id: `telegram.broadcast`
* Topic: `telegram_messages_dto`
* JsonSerializer.TYPE_MAPPINGS name mapping set to `dto`

#### Kafka message format

```json
{
  "chatId": 3887778,
  "message": "Message"
}
```
# Commands

- `/subscribe` - subscribe on broadcast.
- `/unsubscribe` - unsubscribe from broadcast.

[//]: # (- `/sendall` - send message to all users.)