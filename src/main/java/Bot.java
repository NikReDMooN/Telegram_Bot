import com.google.common.eventbus.DeadEvent;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.ApiConstants;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Optional;

public class Bot extends TelegramLongPollingBot {

    public int condition = 0; //состояние бота
    public int do_or_not = 0; //распознал текст или нет

    @Override
    public String getBotUsername() {
        return "StudioVoiceTest";
    } //регитсрация имени бота в программе

    @Override
    public String getBotToken() {
        return "???????";
    } // регистрация токена бота в программе

    @Override
    public void onUpdateReceived(Update update) {
        //проверка на наличие сообщения
        if (update.hasMessage()) {
            handleMessage( update.getMessage());
        }
    }

   /* private void ReceivedThenSend(Update update) {// на текст бот присылает этот текст
        String message_txt = update.getMessage().getText(); //какое сообщение получил бот
        long chat_id = update.getMessage().getChatId(); //получение id чата
        SendMessage message = new SendMessage(); // создание "объекта" сообщения
        message.setChatId(chat_id); //куда отправить
        message.setText(message_txt); // что отправить
        try {
            execute(message); // выполни отправку

            handleMessage(update, update.getMessage());
        }   catch(TelegramApiException e){
            e.printStackTrace(); // если ошибка то какая
        }

    }*/

    private boolean analys(String text){
        String[] splitarray = text.split("_");
        if (splitarray.length != 3) return false;
        for(int i =1; i < 3; i++)
           if (isNumeric(splitarray[i])) return false;
        return true;
    }

    private void handleMessage( Message message){
      //  System.out.println("ты тут");
     //   System.out.println(message.toString());
        if (message.hasText() && message.hasEntities()) { //есть текст и команда
    //        System.out.println(message.toString());
            Optional<MessageEntity> commandEntity = //запихиваем сообщение в контейнер
            message.getEntities().stream().filter(e -> "bot_command".equals(e.getType())).findFirst(); //ищем команду
        //    System.out.println(commandEntity.toString());
            if (commandEntity.isPresent()) { //есть значение или нет
              //  System.out.println("3");
                //дальше обризаем строку, чтобы выделить именно команду
         //       System.out.println("commandEntity.get().getOffset()");
                String command = message.getText().substring(commandEntity.get().getOffset(), //.get - возвращаяет значение, далее начальную позицию
                        commandEntity.get().getLength());
                docommand( message, command);

            }
        }
        if (message.hasText() && !message.hasEntities() && condition == 1) {
            do_or_not = 1;
            String text = message.getText();
            boolean txt_check = analys(text);
            //System.out.println(txt_check);
            if(txt_check) {
                    SendMessage newmessage = new SendMessage();

                    newmessage.setChatId("917631670");
                    newmessage.setText(message.getText());
                    try {
                        execute(newmessage); // выполни отправку
                    }   catch(TelegramApiException e){
                        e.printStackTrace(); // если ошибка то какая
                    }
                    condition = 0;
            }
            else {
                SendMessage newmessage = new SendMessage();
                newmessage.setChatId(message.getChatId().toString());
                newmessage.setText("Не правильный формат ввода");
                try {
                    execute(newmessage); // выполни отправку
                }   catch(TelegramApiException e){
                    e.printStackTrace(); // если ошибка то какая
                }
            }
        }
        if (do_or_not == 0) {
            try {
                execute(SendMessage.builder()
                        .text("Я вас не понимаю, простите \uD83D\uDE2D")
                        .chatId(message.getChatId().toString())
                        .build());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else do_or_not = 0;

    }

    private void docommand( Message message, String command){
        switch (command) {
            case "/news":
                do_or_not = 1;
                donews(message);
                break;
            case "/reserve_a_seat":
                do_or_not = 1;
                ReserveASeat( message);
                break;
        }
    }

    private void ReserveASeat( Message message) {
        //System.out.println("dwqdwqdw");
        try {
            execute(SendMessage.builder()
                    .text("Укажите вашу группу, имя и фамилию. Пример: ИУ9-51б_БОТ_СТУДИЯГОЛОС")
                    .chatId(message.getChatId().toString())
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
        condition = 1;
    }

    private void donews(Message message){
        try {
            execute(SendMessage.builder()
                    .text("Новостей нет, но вы держитесь!")
                    .chatId(message.getChatId().toString())
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isNumeric(String strNum) {
        return strNum.matches("-?\\d+(\\.\\d+)?");
    }

    public static void main(String[] args){
        Bot mybot = new Bot();
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class); // создание бота
            telegramBotsApi.registerBot(mybot); // регистрация его API

        } catch(Exception e) {
            e.printStackTrace();
        }

        Update myupdate = new Update(); // создание объекта апдейтов
        mybot.onUpdateReceived(myupdate); //выполнение метода, требующего объект класса апдейт
    }
}
