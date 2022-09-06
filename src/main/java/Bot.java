
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.HashMap;
import java.util.Optional;

public class Bot extends TelegramLongPollingBot {

    static CreateData mydata = new CreateData();

    //static int condition = 0; //состояние бота,
    //0 - изначальное
    //1 - после reserve_a_seat получение ГРУППА_ИМЯ_ФАМИЛИЯ
    //2 - получение даты на которое хочет прийти
    static HashMap<String, Integer> concerts = new HashMap(); //хранит информацию о дате выступлений и кол-во мест

    public HashMap<String, Integer> id_conditions = new HashMap<String, Integer>(); //состояния бота в зависимости от чата



    public int do_or_not = 0; //распознал текст или нет

    @Override
    public String getBotUsername() {
        return "StudioVoiceTest";
    } //регитсрация имени бота в программе

    @Override
    public String getBotToken() {
        return "5748038899:AAGwBnrnWoB9YWoQyV4tdf5wENs3_xYuKos";
    } // регистрация токена бота в программе

    @Override
    public void onUpdateReceived(Update update) {
        //проверка на наличие сообщения
        if (update.hasMessage()) {
            if(id_conditions.get(update.getMessage().getChatId().toString()) == null) {
                id_conditions.put(update.getMessage().getChatId().toString(), 0);
            }
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
        if (message.hasText() && message.hasEntities() && id_conditions.get(message.getChatId().toString()) == 0) {
            Optional<MessageEntity> commandEntity = //запихиваем сообщение в контейнер
            message.getEntities().stream().filter(e -> "bot_command".equals(e.getType())).findFirst(); //ищем команду
            if (commandEntity.isPresent()) {
                String command = message.getText().substring(commandEntity.get().getOffset(), //.get - возвращаяет значение, далее начальную позицию
                        commandEntity.get().getLength());
                docommand( message, command);

            }
        }
        if ((message.getChatId().toString().equals("917631670") || message.getChatId().toString().equals("349094427") )
                && message.getText().equals("люблю_театр")
                && id_conditions.get(message.getChatId().toString()) == 0 ) {
            docommand(message, "люблю_театр");
            do_or_not = 1;
        }
        if(message.hasText() && id_conditions.get(message.getChatId().toString()) == 2) {
            boolean result =  write_data(message);

            if(result == true)  {
                CreateHelpData createhelpdata= new CreateHelpData();
                createhelpdata.write_new_info(concerts);
                id_conditions.put(message.getChatId().toString(), 0);
                //condition = 0;
            }
            do_or_not = 1;

        }
        if (message.hasText() && !message.hasEntities() && id_conditions.get(message.getChatId().toString()) == 1) {
            boolean result =  write_name(message);
            if (result == true) {
                id_conditions.put(message.getChatId().toString(), 2);
            }
            do_or_not = 1;

            //condition = 2;

        }
        if (do_or_not == 0 && !message.hasEntities()) {
            try {
                execute(SendMessage.builder()
                        .text("Я вас не понимаю, простите \uD83D\uDE2D")
                        .chatId(message.getChatId().toString())
                        .build());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else  {
            do_or_not = 0;
        }

    }

    private void how_i_work(Message message){
        try {
            execute(SendMessage.builder()
                    .text("Вот как я работаю /n news - ПОСЛЕДНИЕ НОВОСТИ НАШЕЙ ЗАМЕЧАТЕЛЬНОЙ СТУДИИ /n " +
                            "reserve_a_seat - ЗАБРОНИРУЙТЕ 1 МЕСТО НА БЛИЖАЙШИЙ СПЕКТАКЛЬ /n")
                    .chatId(message.getChatId())
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*private void  make_a_record(Message message){
        String text = message.getText();
        boolean txt_check = analys(text);
        //System.out.println(txt_check);
        if(txt_check) {

            mydata.write_name(text);
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
    }*/




    private void docommand( Message message, String command){
        switch (command) {
            case "/news":
                do_or_not = 1;
                news(message);
                break;
            case "/reserve_a_seat":
                do_or_not = 1;
                ReserveASeat( message);
                break;
            case "/how_i_work":
                how_i_work(message);
                break;
            case "люблю_театр":
                do_or_not = 1;
                send_info_to_host(message);
            case "write_name":
                do_or_not = 1;
                write_name(message);
                break;
            case "write_data":
                do_or_not = 1;
                write_data(message);

        }
    }

    private boolean write_name(Message message){
        if (analys(message.getText())) {
            mydata.write_name(message.getText());
            try {
                execute(SendMessage.builder()
                    .text("Укажите, пожалуйста, дату, когда хотите прийти в формате ДД_ММ :)")
                    .chatId(message.getChatId().toString())
                    .build());
            } catch (Exception e) {
                e.printStackTrace();
                }
            return true;
        }
        else {
            try {
                execute(SendMessage.builder()
                        .text("Не правильный формат, повторите попытку :(")
                        .chatId(message.getChatId().toString())
                        .build());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    private boolean write_data(Message message){
        String txt = message.getText();
        if (!check_data(txt)) {
            System.out.println("weeeeeeeeeeeeeeeeeeeeeee");
            try {
                execute(SendMessage.builder()
                        .text("Не правильный формат даты :(")
                        .chatId(message.getChatId().toString())
                        .build());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
        System.out.println("ты тут");
        if (concerts.get(message.getText()) == 0 ) {
            try {
                execute(SendMessage.builder()
                        .text("Приносим извинения, но все места заняли :(")
                        .chatId(message.getChatId().toString())
                        .build());
            } catch (Exception e) {
                e.printStackTrace();

            }
            return true;
        }
        mydata.write_data(txt);
        concerts.put(txt, concerts.get(txt) - 1);
        System.out.println(concerts.get(txt) + "aaaaaa");
        CreateHelpData myhelpdata = new CreateHelpData();
        myhelpdata.write_new_info(concerts);
        try {
            execute(SendMessage.builder()
                    .text("Большое спасибо :)")
                    .chatId(message.getChatId().toString())
                    .build());
        } catch (Exception e) {
            e.printStackTrace();

        }
        return true;

    }

    private boolean check_data(String txt){
        System.out.println("1 \n");
        System.out.println(check_format_data(txt));
        if(!check_format_data(txt)) {
            System.out.println("2");
            return false;
        }
        System.out.println("5");
        if(concerts.get(txt) == null) {
            System.out.println("3");
            return false;
        }
        System.out.println("4");
        return true;
    }

    private boolean check_format_data(String txt){ //дописать
        String[] split = txt.split("_");
       // System.out.println(split[0] + split[1]);
        if (split.length !=2 ) return false;
        if (split[0] == null ) return false;
        if (split[1] == null) return false;
        System.out.println("YES \n");

        int helpint = Integer.parseInt(split[1]);
        System.out.println(helpint);
        if (helpint > 12  ) return false;
        System.out.println("YES YES");
        helpint = Integer.parseInt(split[0]);
        if(helpint > 31) return false;
        return true;
    }

    private void send_info_to_host(Message message) {

        try {
            StringBuilder send = mydata.make_txt();
            execute(SendMessage.builder()
                    .text(send.toString())
                    .chatId(message.getChatId().toString())
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
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
        id_conditions.put(message.getChatId().toString(), 1);
    }

    private void news(Message message){
        try {
            execute(SendMessage.builder()
                    .text("Новостей нет, но вы держитесь ")
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
        CreateHelpData parce_info = new CreateHelpData();
        concerts = parce_info.ParcDatesAndKol();
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
