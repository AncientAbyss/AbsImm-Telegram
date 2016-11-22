AbsImm Telegram
===============

A Telegram Bot for AbsImm.
Try it: http://telegram.me/textadventures_bot

Besides connecting to Telegram, the app also starts a web server, which is used to keep the bot alive.


Requirements
------------

* JDK 1.8
* Gradle 2.2
* Telegram Bot Token


Run
---

        gradle stage
        java -jar -Dport=$PORT -DTELEGRAM_BOT_TOKEN=$TELEGRAM_BOT_TOKEN build/libs/client-telegram-1.0-SNAPSHOT-all.jar
