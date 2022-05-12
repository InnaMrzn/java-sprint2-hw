package ru.yandex.practicum.task.test;

import org.junit.jupiter.api.BeforeAll;
import ru.yandex.practicum.task.server.KVServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpHistoryManagerTest extends HistoryManagerTest {

    static KVServer storageServer;

    @BeforeAll
    public static void beforeAll(){
        try {
            storageServer = new KVServer();
            storageServer.start();
            URI uri = URI.create("http://localhost:8078/save/practicum?API_KEY=DEBUG");
            HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(getInitialTestData()))
                    .header("Content-Type", "application/json").uri(uri).build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException |InterruptedException ex){
            System.out.println("Во время выполнения запроса возникла ошибка. Проверьте, пожалуйста, URL-адрес и повторите попытку.....");
        }

    }



    private static String getInitialTestData (){
        return "{\n" +
                "  \"currentTaskId\": 4,\n" +
                "  \"tasks\": {\n" +
                "    \"3\": {\n" +
                "      \"name\": \"Подзадача 1\",\n" +
                "      \"taskId\": 3,\n" +
                "      \"description\": \"Описание подзадачи 1\",\n" +
                "      \"status\": \"NEW\",\n" +
                "      \"startTime\": \"27.05.2022 09:25\",\n" +
                "      \"duration\": 20\n" +
                "    }\n" +
                "  },\n" +
                "  \"epicTasks\": {\n" +
                "    \"0\": {\n" +
                "      \"epicSubTasksIds\": [\n" +
                "        1,\n" +
                "        2\n" +
                "      ],\n" +
                "      \"endTime\": \"27.04.2022 09:45\",\n" +
                "      \"name\": \"Эпик для подзадачи \",\n" +
                "      \"taskId\": 0,\n" +
                "      \"description\": \"Описание Эпика для подзадачи\",\n" +
                "      \"status\": \"NEW\",\n" +
                "      \"startTime\": \"26.04.2022 09:25\",\n" +
                "      \"duration\": 40\n" +
                "    }\n" +
                "  },\n" +
                "  \"subTasks\": {\n" +
                "    \"1\": {\n" +
                "      \"parentId\": 0,\n" +
                "      \"name\": \"Подзадача 1\",\n" +
                "      \"taskId\": 1,\n" +
                "      \"description\": \"Описание подзадачи 1\",\n" +
                "      \"status\": \"NEW\",\n" +
                "      \"startTime\": \"27.04.2022 09:25\",\n" +
                "      \"duration\": 20\n" +
                "    },\n" +
                "    \"2\": {\n" +
                "      \"parentId\": 0,\n" +
                "      \"name\": \"Подзадача 1\",\n" +
                "      \"taskId\": 2,\n" +
                "      \"description\": \"Описание подзадачи 1\",\n" +
                "      \"status\": \"NEW\",\n" +
                "      \"startTime\": \"26.04.2022 09:25\",\n" +
                "      \"duration\": 20\n" +
                "    }\n" +
                "  },\n" +
                "  \"historyManager\": {\n" +
                "    \"inHistoryNodes\": {\n" +
                "      \"1\": 1,\n" +
                "      \"3\": 3\n" +
                "    }\n" +
                "  }\n" +
                "}";

    }


}
