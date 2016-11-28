import at.absoluteimmersion.core.Loader;
import at.absoluteimmersion.core.ReactionClient;
import at.absoluteimmersion.core.Story;
import at.absoluteimmersion.core.StoryException;
import net.ancientabyss.data.tables.pojos.Command;
import net.ancientabyss.data.tables.records.CommandRecord;
import org.jivesoftware.smack.SmackException;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.ancientabyss.data.Tables.COMMAND;

public class TextAdventuresBot extends TelegramLongPollingBot {

    private static final Logger Logger = LoggerFactory.getLogger(TextAdventuresBot.class);

    private static final String BOT_USERNAME = "TextAdventures_Bot";
    private static final String BOT_TOKEN_PROPERTY_NAME = "TELEGRAM_BOT_TOKEN";
    private static final String DB_URL_PROPERTY_NAME = "JDBC_DATABASE_URL";
    private static final String DB_USER_PROPERTY_NAME = "JDBC_DATABASE_USERNAME";
    private static final String DB_PASS_PROPERTY_NAME = "JDBC_DATABASE_PASSWORD";

    public class Client implements ReactionClient {
        private Long chatId;

        public void SetChatId(Long chatId) {
            this.chatId = chatId;
        }

        public Client(Long chatId) {
            this.chatId = chatId;
        }

        @Override
        public void reaction(String text) {
            sendMessageToChat(text, chatId);
        }
    }

    public class UserSession {
        private Story story;
        private Client client;

        public UserSession(Story story, Client client) {
            this.story = story;
            this.client = client;
        }
    }

    private Map<Integer, UserSession> sessions; // TODO: persist!

    private String testStory =
            "<story>\n" +
                    "    <settings>\n" +
                    "        <setting name=\"initial_command\" value=\"enter ch001\" />\n" +
                    "        <setting name=\"object_error\" value=\"uuhm?\" />\n" +
                    "        <setting name=\"action_error\" value=\"o.O?\" />\n" +
                    "    </settings>\n" +
                    "    <part name=\"ch001\" condition=\"NOT game_started\">\n" +
                    "        <action name=\"enter\" state=\"game_started\" text=\"\n" +
                    "=============\\n\n" +
                    "    *Work in progress!*\\n\n" +
                    "    (c) 2012-2016 by AncientAbyss\\n\n" +
                    "=============\\n\n" +
                    "powered by AbsoluteImmersion, https://github.com/AncientAbyss/AbsImm\\n\n" +
                    "\t\t\"/>\n" +
                    "        <action name=\"enter\" state=\"in_ch-intro\"/>\n" +
                    "        <action name=\"enter\" command=\"enter ch-intro\"/>\n" +
                    "    </part>\n" +
                    "    <part name=\"ch-intro\" condition=\"in_ch-intro\">\n" +
                    "        <action name=\"enter\" text=\"\n" +
                    "Hello carbon based unit! You reached a prototype of AbsImm, a simple, open, general purpose text adventure engine. I am sorry to have to tell you that I do not have stories available for you quite yet.\\n\n" +
                    "However, if you are interested in the project, and want to see it move forward, please /contact me or have a look at my /homepage.\\n\n" +
                    "Are you, by any chance, a /writer or /developer?\\n\n" +
                    "Please /contact me if you want to know more or want this project to kick off!\\n\n" +
                    "\\n\n" +
                    "Have a nice day, and thanks for stopping by!\n" +
                    "\t\t\"/>\n" +
                    "        <part name=\"\">\n" +
                    "            <action name=\"developer\" state=\"in_ch-developer\" command=\"enter ch-developer\"/>\n" +
                    "            <action name=\"writer\" state=\"in_ch-writer\" command=\"enter ch-writer\"/>\n" +
                    "            <action name=\"contact\" state=\"in_ch-contact\" command=\"enter ch-contact\"/>\n" +
                    "            <action name=\"homepage\" state=\"in_ch-homepage\" command=\"enter ch-homepage\"/>\n" +
                    "            <action name=\"AbsImm\" state=\"in_ch-homepage\" command=\"enter ch-homepage\"/>\n" +
                    "        </part>\n" +
                    "    </part>\n" +
                    "    <part name=\"ch-writer\" condition=\"in_ch-writer\">\n" +
                    "        <action name=\"enter\" text=\"\n" +
                    "Wohaa, cool! If you want to share one of your stories as interactive adventure,\n" +
                    "you can already do so, just have a look at the /homepage!\n" +
                    "If you need assistance, or simply do not want to do this yourself, please let me know! (/contact).\n" +
                    "        \"/>\n" +
                    "    </part>\n" +
                    "    <part name=\"ch-developer\" condition=\"in_ch-developer\">\n" +
                    "        <action name=\"enter\" text=\"\n" +
                    "Thats great news! If you want to contribute, or just have a look at the source, please visit my GitHub page at https://github.com/AncientAbyss/AbsImm\\n\n" +
                    "I would be super happy if you would like to contribute, you know? Lets build the most accessible and generic text adventure engine ever!\n" +
                    "        \"/>\n" +
                    "    </part>\n" +
                    "    <part name=\"ch-contact\" condition=\"in_ch-contact\">\n" +
                    "        <action name=\"enter\" text=\"\n" +
                    "Reach me through mail ancientabyss@zoho.com or visit me at https://github.com/AncientAbyss/AbsImm\\n\n" +
                    "I will be happy if I could assist you with *every* request or issue you might experience.\\n\n" +
                    "\\n\n" +
                    "Everyone.\\n\n" +
                    "\\n\n" +
                    "Just tell me.\\n\n" +
                    "\\n\n" +
                    "I am lonely anyways.\\n\n" +
                    "\\n\n" +
                    "So, write me! :)\n" +
                    "        \"/>\n" +
                    "    </part>\n" +
                    "    <part name=\"ch-homepage\" condition=\"in_ch-homepage\">\n" +
                    "        <action name=\"enter\" text=\"\n" +
                    "You can find all information that is available so far at https://github.com/AncientAbyss/AbsImm\\n\n" +
                    "        \"/>\n" +
                    "    </part>\n" +
                    "</story>";

    public TextAdventuresBot() {
        sessions = new HashMap<>();
    }

    private void insertCommand(int userId, String value) {
        try (Connection conn = DriverManager.getConnection(
                Util.readFromPropertyOrEnv(DB_URL_PROPERTY_NAME),
                Util.readFromPropertyOrEnv(DB_USER_PROPERTY_NAME),
                Util.readFromPropertyOrEnv(DB_PASS_PROPERTY_NAME));
             DSLContext create = DSL.using(conn, SQLDialect.POSTGRES)
        ) {
            CommandRecord command = create.newRecord(COMMAND);
            command.setUserId(userId);
            command.setValue(value);
            command.store();
        } catch (SQLException e) {
            Logger.error(e.getMessage());
        }
    }

    private List<Command> getCommands(int userId) {
        try (Connection conn = DriverManager.getConnection(
                Util.readFromPropertyOrEnv(DB_URL_PROPERTY_NAME),
                Util.readFromPropertyOrEnv(DB_USER_PROPERTY_NAME),
                Util.readFromPropertyOrEnv(DB_PASS_PROPERTY_NAME));
             DSLContext create = DSL.using(conn, SQLDialect.POSTGRES)
        ) {
            List<Command> commands = create.select().from(COMMAND).where(COMMAND.USER_ID.eq(userId)).orderBy(COMMAND.ID).fetch().into(Command.class);
            return commands;
        } catch (SQLException e) {
            Logger.error(e.getMessage());
        }
        return new ArrayList<>();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!(update.hasMessage() && update.getMessage().hasText())) {
            return;
        }
        Logger.trace("Handling bot request...");

        try {
            handleUpdate(update);
        } catch (StoryException | SmackException.NotConnectedException e) {
            Logger.error(e.getMessage());
        }
    }

    private void handleUpdate(Update update) throws StoryException, SmackException.NotConnectedException {
        Integer userId = update.getMessage().getFrom().getId();

        List<Command> commands = getCommands(userId);
        insertCommand(userId, update.getMessage().getText());

        if (!sessions.containsKey(userId)) { // TODO: check if session is active...
            initUserSession(update, userId, commands);
            if (commands.isEmpty()) {
                return; // ignore the first command, which just started the game
            }
        }

        UserSession session = sessions.get(userId);
        session.client.SetChatId(update.getMessage().getChatId());

        session.story.interact(update.getMessage().getText().replaceAll("^/", ""));
    }

    private void initUserSession(Update update, Integer userId, List<Command> commands) throws StoryException, SmackException.NotConnectedException {
        Story story = new Loader().fromString(testStory);
        if (!commands.isEmpty()) {
            Logger.trace("Restoring game...");
            story.tell();
            for (Command command : commands) {
                story.interact(command.getValue());
            }
        }

        Client client = new Client(update.getMessage().getChatId());
        story.addClient(client);
        sessions.put(userId, new UserSession(story, client));

        if (commands.isEmpty()) {
            story.tell();
        }
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return Util.readFromPropertyOrEnv(BOT_TOKEN_PROPERTY_NAME);
    }

    private void sendMessageToChat(String text, Long chatId) {
        try {
            String line = text.replace("\\n", "\n");
            SendMessage message = new SendMessage()
                    .setChatId(chatId)
                    .setText(line)
                    .disableWebPagePreview()
                    .enableMarkdown(true);
            sendMessage(message);
        } catch (TelegramApiException e) {
            Logger.error(e.getMessage());
        }
    }
}
