package com.example.traveland;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class ServerAccesor {
    private String serviceAddress;
    public ServerAccesor(String serviceAddress){
        serviceAddress = serviceAddress;
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
    public ArrayList<Note> getData() throws IOException{
        return Parse(GetContent());
    }
    //из json в данные
    public ArrayList<Note> Parse(String content){
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
    private String GetContent() throws IOException{
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
            }
            return buf.toString();
        }
        catch (Exception exception){

        }
        finally {
            if (reader != null){
                reader.close();
            }
        }

        return null;
    }
}
