package ru.yandex.practicum.task.client;

import ru.yandex.practicum.task.exception.BadServerResponseException;

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
        authToken = register(serverURL);
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
            if (response.statusCode() != 200) {
                throw new BadServerResponseException("Ошибка при сохранении данных на KVServer. Сервер вернул код "
                        + response.statusCode());
            }

        } catch(IllegalArgumentException ex){
            System.out.println("Введённый вами адрес не соответствует формату URL. Попробуйте, пожалуйста, снова.");
        } catch (IOException | InterruptedException ex){
            throw new BadServerResponseException("Ошибка при сохранении данных на KVServer. " +
                    "Проверьте URL запроса "+urlString+" и повторите попытку");
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
            if (response.statusCode() != 200) {
                throw new BadServerResponseException("Ошибка при получении данных от KVServer. Сервер вернул код "
                        + response.statusCode());
            }
            bodyText = response.body();
        } catch(IllegalArgumentException ex){
            System.out.println("Введённый вами адрес не соответствует формату URL. Попробуйте, пожалуйста, снова.");
        } catch (IOException | InterruptedException ex){
            throw new BadServerResponseException("Ошибка при получении данных от KVServer. " +
                    "Проверьте URL запроса "+urlString+" и повторите попытку");
        }

        return bodyText;
    }

    //метод авторизуется на сервере и возвращает токен авторизации
    private String register (String serverURL)  {

        String token ="";
        try {
            URI uri = URI.create(urlString+"/register");
            HttpRequest request = HttpRequest.newBuilder().GET()
                    .header("Accept", "text/html").uri(uri).build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new BadServerResponseException("Ошибка при авторизации на KVServer. Сервер вернул код "
                        + response.statusCode());
            }
            token = response.body().toString();;
        } catch(IllegalArgumentException ex){
            System.out.println("Введённый вами адрес не соответствует формату URL. Попробуйте, пожалуйста, снова.");
        } catch (IOException | InterruptedException ex){
            throw new BadServerResponseException("Ошибка при авторизации на KVServer. " +
                    "Проверьте URL запроса "+serverURL+" и повторите попытку");
        }

        return token;
    }
}
