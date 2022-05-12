package ru.yandex.practicum.task.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private String urlString;
    private String authToken;

    public KVTaskClient(String serverURL) {
        this.urlString = serverURL;
        try {
            URI uri = URI.create(urlString+"/register");
            HttpRequest request = HttpRequest.newBuilder().GET()
                    .header("Accept", "text/html").uri(uri).build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            authToken = response.body().toString();
        } catch(IllegalArgumentException ex){
            System.out.println("Введённый вами адрес не соответствует формату URL. Попробуйте, пожалуйста, снова.");
        } catch (IOException | InterruptedException ex){
            System.out.println("Во время выполнения запроса возникла ошибка. Проверьте, пожалуйста, URL-адрес и повторите попытку..");
        }

    }

    public String getUrlString() {
        return urlString;
    }

    public void put(String key, String json){
        try{
            URI uri = URI.create(urlString+"/save/"+key+"?API_KEY="+authToken);

            HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type","application/json").uri(uri).build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send (request, HttpResponse.BodyHandlers.ofString());

        } catch(IllegalArgumentException ex){
            System.out.println("Введённый вами адрес не соответствует формату URL. Попробуйте, пожалуйста, снова.");
        } catch (IOException | InterruptedException ex){
            System.out.println("Во время выполнения запроса возникла ошибка. Проверьте, пожалуйста, URL-адрес и повторите попытку...");
        }
    }

    public String load(String key){

        String bodyText = "";
        try{
            URI uri = URI.create(urlString+"/load/"+key+"?API_KEY="+authToken);
            HttpRequest request = HttpRequest.newBuilder().GET()
                    .header("Accept","text/html").uri(uri).build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send (request, HttpResponse.BodyHandlers.ofString());
            bodyText = response.body();
        } catch(IllegalArgumentException ex){
            System.out.println("Введённый вами адрес не соответствует формату URL. Попробуйте, пожалуйста, снова.");
        } catch (IOException | InterruptedException ex){
            System.out.println("Во время выполнения запроса возникла ошибка. Проверьте, пожалуйста, URL-адрес и повторите попытку....");
        }

        return bodyText;
    }
}
