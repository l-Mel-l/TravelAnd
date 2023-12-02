package com.example.traveland;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ServerAccesor {
    private static String serviceAddress;
    public ServerAccesor(String ServiceAddress){
        serviceAddress = ServiceAddress;
    }
    //Получить список тем из списка заметок
    public ArrayList<String>getStringListFromNoteList(ArrayList<Note> noteList){
        ArrayList<String> stringList = new ArrayList<>();
        for (Note note : noteList){
            stringList.add(note.theme);
        }
        return stringList;
    }
    //получить данные с сервера
    public static ArrayList<TravelPlan> getData() throws IOException{
        return Parse(GetContent());
    }
    //из json в данные
    public static ArrayList<TravelPlan> Parse(String content){
        try {
            Gson gson = new Gson();
            TravelData travelData = gson.fromJson(content, TravelData.class);
            return travelData != null ? (ArrayList<TravelPlan>) travelData.getTravelPlans() : new ArrayList<>();
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public class TravelData {
        private List<TravelPlan> travelPlans;

        public List<TravelPlan> getTravelPlans() {
            return travelPlans;
        }
    }

    public class TravelPlan {
        private String destination;
        private String date;

        public String getDestination() {
            return destination;
        }

        public String getDate() {
            return date;
        }
    }
    //подключение и забирание данных
    private static String GetContent() throws IOException{
        BufferedReader reader = null;
        InputStream stream = null;
        HttpURLConnection connection = null;
        try{
            URL url = new URL(serviceAddress);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder buf = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null){
                buf.append(line).append("\n");
                System.out.println(buf);
            }
            return buf.toString();
        }
        catch (Exception exception){
            exception.printStackTrace();
            throw exception;
        }
        finally {
            if (reader != null) {
                reader.close();
            }
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    public static void syncDataWithServer(DataBaseAccessor dataBaseAccessor) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<TravelPlan> serverData = getData();

                    // Очистка локальной базы данных
                    //dataBaseAccessor.clearDatabase();

                    // Сохранение данных из сервера в локальную базу данных
                    for (TravelPlan note : serverData) {
                        dataBaseAccessor.insertNote(note.getDestination(), note.getDate());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    // Обработка ошибок при выполнении запроса
                }
            }
        });

        thread.start();
    }
    public static void sendDataToServer(String theme, String noteText) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Создание объекта заметки
                    Note note = new Note(theme, noteText);

                    // Преобразование заметки в JSON
                    Gson gson = new Gson();

                    String json = gson.toJson(note);
                    System.out.println(json);
                    // Создание URL и открытие HTTP-соединения
                    URL url = new URL(serviceAddress);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    // Настройка соединения для отправки POST-запроса
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/json");

                    // Получение потока вывода и запись JSON-данных
                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = json.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    // Получение ответа от сервера
                    int responseCode = connection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Успешная отправка данных на сервер
                        System.out.println("Good");
                        // Дополнительные действия при необходимости
                    } else {
                        // Ошибка при отправке данных
                        System.out.println("Don't good ERROR: " + responseCode);
                        // Обработка ошибки
                    }

                    // Закрытие соединения
                    connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                    // Обработка ошибок при отправке данных
                }
            }
        });

        thread.start();
    }
}
