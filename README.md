AbsImm Telegram
===============

A Telegram Bot for AbsImm.
Try it: http://telegram.me/textadventures_bot

Besides connecting to Telegram, the app also starts a web server, which is used to keep the bot alive.


Requirements
------------

* JDK 1.8
* Gradle 2.2
* A Telegram Bot Token
* A PostgreSQL database


Usage
-----

* Configuration

    * The following environment variables need to be available when building and running the project 

            // the url of the database, e.g. jdbc:postgresql://localhost:5432/absimm
            JDBC_DATABASE_URL=[DB_URL]
            // database credentials
            JDBC_DATABASE_USERNAME=[DB_USERNAME]
            JDBC_DATABASE_PASSWORD=[DB_PASSWORD]

    * The following environment variables need to be available when running the project

            // the port for the web server
            PORT=[PORT]
            // the token from the Telegram Bot Father
            TELEGRAM_BOT_TOKEN=[BOT_TOKEN]

* Build the project (includes code generation and database migration)

        gradle stage

* Start the application

        java -jar build/libs/client-telegram-1.0-SNAPSHOT-all.jar
