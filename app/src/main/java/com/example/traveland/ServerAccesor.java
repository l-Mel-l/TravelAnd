package com.example.traveland;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


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
    public static ArrayList<Note> getData() throws IOException{
        return Parse(GetContent());
    }
    //из json в данные
    public static ArrayList<Note> Parse(String content){
        ArrayList<Note> dataItems;
        try{
            Gson gson = new Gson();
            Type listofComputers = new TypeToken<ArrayList<Note>>() {}.getType();
            dataItems = gson.fromJson(content,listofComputers);
            return dataItems;
        }catch (Exception ex){

        }
        return null;
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
            exception.printStackTrace(); // Лучше заменить на логирование
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
    public void sendDataToServer(ArrayList<Note> notes) {
        // Преобразование списка заметок в JSON
        Gson gson = new Gson();
        String json = gson.toJson(notes);

        // Отправка данных на сервер (например, с использованием HTTP POST-запроса)
        // Реализуйте этот метод в соответствии с вашей серверной структурой
        // ...
    }
    public static void syncDataWithServer(DataBaseAccessor dataBaseAccessor) {
        try {
            ArrayList<Note> serverData = getData();

            // Очистка локальной базы данных
            //dataBaseAccessor.clearDatabase();

            // Сохранение данных из сервера в локальную базу данных
            for (Note note : serverData) {
                dataBaseAccessor.insertNote(note.theme, note.noteText);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Обработка ошибок при получении данных с сервера
        }
    }
}
