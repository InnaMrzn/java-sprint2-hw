package ru.yandex.practicum.task.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.*;
import ru.yandex.practicum.task.constants.TaskStatus;
import ru.yandex.practicum.task.models.EpicTask;
import ru.yandex.practicum.task.models.SubTask;
import ru.yandex.practicum.task.models.Task;
import ru.yandex.practicum.task.server.HttpTaskServer;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.task.server.KVServer;
import ru.yandex.practicum.task.utility.LocalDateTimeAdapter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class HttpTaskServerTest {

    HttpTaskServer httpServer;
    KVServer kvServer;

    @BeforeEach
    public void beforeEach(){
        try {
            kvServer = new KVServer();
            kvServer.start();
            httpServer = new HttpTaskServer();
            httpServer.start();
        }catch (IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    @AfterEach
    public void afterEach(){
        kvServer.stop();
        httpServer.stop();
    }

    @Test
    public void wrongPathSentInRequest(){
        try{
        URI uri = URI.create("http://localhost:8080/tasks/tasksssss");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().GET()
                .header("Accept", "application/json").uri(uri).build();
        HttpResponse<String> response = client.send (request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        } catch(IllegalArgumentException ex){
            ex.getMessage();
        } catch (IOException | InterruptedException ex){
            ex.getMessage();
        }
    }
    @Test
    public void Post2TasksAndGetAll(){
        try {
            URI uri = URI.create("http://localhost:8080/tasks/task");
            HttpClient client = HttpClient.newHttpClient();
            //Cоздаем две задачи с помощью метода POST
            Task task1 = new Task("Наименование задачи 1 POST", "описание задачи 1 POST", TaskStatus.NEW, LocalDateTime.now(), 10);

            HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(task1)))
                    .header("Content-Type","application/json").uri(uri).build();
            HttpResponse<String> response = client.send (request, HttpResponse.BodyHandlers.ofString());
            int rCode = response.statusCode();
            assertEquals(201,rCode);

            Task task2 = new Task("Наименование задачи 1 POST", "описание задачи 1 POST", TaskStatus.NEW);
            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(task2)))
                    .header("Content-Type","application/json").uri(uri).build();

            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

           //получаем все задачи с помощью GET
            request = HttpRequest.newBuilder().GET()
                    .header("Accept", "application/json").uri(uri).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(200,rCode);
            assertNotNull(response.body());
            ArrayList getResult = getGson().fromJson(response.body(),new TypeToken<ArrayList<Task>>() {}.getType());
            assertEquals(2, getResult.size());

        } catch(IllegalArgumentException ex){
            ex.getMessage();
        } catch (IOException | InterruptedException ex){
            ex.getMessage();
        }

    }

    @Test
    public void Post2TasksAndGetTaskById(){

        try {
            URI uri = URI.create("http://localhost:8080/tasks/task");
            HttpClient client = HttpClient.newHttpClient();
            //Cоздаем две задачи с помощью метода POST
            Task task1 = new Task("Наименование задачи 1 POST", "описание задачи 1 POST", TaskStatus.NEW, LocalDateTime.now(), 10);

            HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(task1)))
                    .header("Content-Type","application/json").uri(uri).build();
            HttpResponse<String> response = client.send (request, HttpResponse.BodyHandlers.ofString());
            int rCode = response.statusCode();
            assertEquals(201,rCode);

            Task task2 = new Task("Наименование задачи 1 POST", "описание задачи 1 POST", TaskStatus.NEW);
            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(task2)))
                    .header("Content-Type","application/json").uri(uri).build();

            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

            //получаем одну задачу с помощью GET
            uri = URI.create("http://localhost:8080/tasks/task?id="+1);
            request = HttpRequest.newBuilder().GET()
                    .header("Accept", "application/json").uri(uri).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(200,rCode);
            assertNotNull(response.body());
            assertEquals(1, getGson().fromJson(response.body(), Task.class).getTaskId());

        } catch(IllegalArgumentException ex){
            ex.getMessage();
        } catch (IOException | InterruptedException ex){
            ex.getMessage();
        }

    }

    @Test
    public void Post2EpicsAndGetAll(){
        try {
            URI uri = URI.create("http://localhost:8080/tasks/epic");
            HttpClient client = HttpClient.newHttpClient();
            //Cоздаем две задачи с помощью метода POST
            EpicTask epic1 = new EpicTask("Наименование эпика 1 POST", "описание задачи 1 POST");

            HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(epic1)))
                    .header("Content-Type","application/json").uri(uri).build();
            HttpResponse<String> response = client.send (request, HttpResponse.BodyHandlers.ofString());
            int rCode = response.statusCode();
            assertEquals(201,rCode);

            EpicTask epic2 = new EpicTask("Наименование эпика 1 POST", "описание задачи 1 POST");
            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(epic2)))
                    .header("Content-Type","application/json").uri(uri).build();

            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

            //получаем все задачи с помощью GET
            request = HttpRequest.newBuilder().GET()
                    .header("Accept", "application/json").uri(uri).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(200,rCode);
            assertNotNull(response.body());
            ArrayList getResult = getGson().fromJson(response.body(),new TypeToken<ArrayList<EpicTask>>() {}.getType());
            assertEquals(2, getResult.size());

        } catch(IllegalArgumentException ex){
            ex.getMessage();
        } catch (IOException | InterruptedException ex){
            ex.getMessage();
        }

    }

    @Test
    public void Post2EpicsAndGetEpicById(){

        try {
            URI uri = URI.create("http://localhost:8080/tasks/epic");
            HttpClient client = HttpClient.newHttpClient();
            //Cоздаем две задачи с помощью метода POST
            EpicTask epic1 = new EpicTask("Наименование эпика 1 POST", "описание эпика 1 POST");

            HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(epic1)))
                    .header("Content-Type","application/json").uri(uri).build();
            HttpResponse<String> response = client.send (request, HttpResponse.BodyHandlers.ofString());
            int rCode = response.statusCode();
            assertEquals(201,rCode);

            EpicTask epic2 = new EpicTask("Наименование эпика 1 POST", "описание эпика 1 POST");
            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(epic2)))
                    .header("Content-Type","application/json").uri(uri).build();

            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

            //получаем одну задачу с помощью GET
            uri = URI.create("http://localhost:8080/tasks/epic?id="+1);
            request = HttpRequest.newBuilder().GET()
                    .header("Accept", "application/json").uri(uri).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(200,rCode);
            assertNotNull(response.body());
            assertEquals(1, getGson().fromJson(response.body(), EpicTask.class).getTaskId());

        } catch(IllegalArgumentException ex){
            ex.getMessage();
        } catch (IOException | InterruptedException ex){
            ex.getMessage();
        }

    }

    @Test
    public void Post2SubTasksAdnGetSubTaskById(){

        try {
            URI uri = URI.create("http://localhost:8080/tasks/epic");
            HttpClient client = HttpClient.newHttpClient();
            //Cоздаем две задачи с помощью метода POST
            EpicTask epic1 = new EpicTask("Наименование эпика 1 POST", "описание эпика 1 POST");

            HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(epic1)))
                    .header("Content-Type","application/json").uri(uri).build();
            HttpResponse<String> response = client.send (request, HttpResponse.BodyHandlers.ofString());
            int rCode = response.statusCode();
            assertEquals(201,rCode);

            SubTask sub1 = new SubTask("Наименование подзадачи 1 POST",
                    "описание подзадачи 1 POST", TaskStatus.NEW, 1, LocalDateTime.now().plusMinutes(50), 20);
            uri = URI.create("http://localhost:8080/tasks/subtask");
            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(sub1)))
                    .header("Content-Type","application/json").uri(uri).build();

            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

            SubTask sub2 = new SubTask("Наименование подзадачи 2 POST",
                    "описание подзадачи 2 POST", TaskStatus.NEW, 1, LocalDateTime.now().plusMinutes(100), 20);
            uri = URI.create("http://localhost:8080/tasks/subtask");
            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(sub2)))
                    .header("Content-Type","application/json").uri(uri).build();

            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

            //получаем одну задачу с помощью GET
            uri = URI.create("http://localhost:8080/tasks/subtask?id="+2);
            request = HttpRequest.newBuilder().GET()
                    .header("Accept", "application/json").uri(uri).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(200,rCode);
            assertNotNull(response.body());
            assertEquals(2, getGson().fromJson(response.body(), SubTask.class).getTaskId());

        } catch(IllegalArgumentException ex){
            ex.getMessage();
        } catch (IOException | InterruptedException ex){
            ex.getMessage();
        }

    }

    @Test
    public void Post2SubTasksAndGetAll(){

        try {
            URI uri = URI.create("http://localhost:8080/tasks/epic");
            HttpClient client = HttpClient.newHttpClient();
            //Cоздаем две задачи с помощью метода POST
            EpicTask epic1 = new EpicTask("Наименование эпика 1 POST", "описание эпика 1 POST");

            HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(epic1)))
                    .header("Content-Type","application/json").uri(uri).build();
            HttpResponse<String> response = client.send (request, HttpResponse.BodyHandlers.ofString());
            int rCode = response.statusCode();
            assertEquals(201,rCode);

            SubTask sub1 = new SubTask("Наименование подзадачи 1 POST",
                    "описание подзадачи 1 POST", TaskStatus.NEW, 1, LocalDateTime.now().plusMinutes(50), 20);
            uri = URI.create("http://localhost:8080/tasks/subtask");
            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(sub1)))
                    .header("Content-Type","application/json").uri(uri).build();

            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

            SubTask sub2 = new SubTask("Наименование подзадачи 2 POST",
                    "описание подзадачи 2 POST", TaskStatus.NEW, 1, LocalDateTime.now().plusMinutes(100), 20);
            uri = URI.create("http://localhost:8080/tasks/subtask");
            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(sub2)))
                    .header("Content-Type","application/json").uri(uri).build();

            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

            //получаем одну задачу с помощью GET
            request = HttpRequest.newBuilder().GET()
                    .header("Accept", "application/json").uri(uri).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(200,rCode);
            ArrayList getResult = getGson().fromJson(response.body(),new TypeToken<ArrayList<SubTask>>() {}.getType());
            assertEquals(2, getResult.size());

        } catch(IllegalArgumentException ex){
            ex.getMessage();
        } catch (IOException | InterruptedException ex){
            ex.getMessage();
        }

    }

    @Test
    public void GetAllSubTasksForEpic(){

        try {

            URI uri = URI.create("http://localhost:8080/tasks/epic");
            HttpClient client = HttpClient.newHttpClient();
            //Cоздаем два эпика
            EpicTask epic1 = new EpicTask("Наименование эпика 1 POST", "описание эпика 1 POST");

            HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(epic1)))
                    .header("Content-Type","application/json").uri(uri).build();
            HttpResponse<String> response = client.send (request, HttpResponse.BodyHandlers.ofString());
            int rCode = response.statusCode();
            assertEquals(201,rCode);

            EpicTask epic2 = new EpicTask("Наименование эпика 2 POST", "описание эпика 2 POST");

            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(epic2)))
                    .header("Content-Type","application/json").uri(uri).build();
            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

            //создаем по 2 подзадачи к каждому эпику
            SubTask sub1 = new SubTask("Наименование подзадачи 1 POST",
                    "1 подзадача 1 эпика", TaskStatus.NEW, 1, LocalDateTime.now().plusMinutes(200), 20);
            uri = URI.create("http://localhost:8080/tasks/subtask");
            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(sub1)))
                    .header("Content-Type","application/json").uri(uri).build();
            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

            SubTask sub2 = new SubTask("Наименование подзадачи 2 POST",
                    "2 подзадача первого эпика", TaskStatus.NEW, 1);
            uri = URI.create("http://localhost:8080/tasks/subtask");
            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(sub2)))
                    .header("Content-Type","application/json").uri(uri).build();
            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

            SubTask sub3 = new SubTask("Наименование подзадачи 3 POST",
                    "1 подзадача 2 эпика", TaskStatus.NEW, 2);
            uri = URI.create("http://localhost:8080/tasks/subtask");
            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(sub3)))
                    .header("Content-Type","application/json").uri(uri).build();
            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

            SubTask sub4 = new SubTask("Наименование подзадачи 4 POST",
                    "2 подзадача 2 эпика", TaskStatus.NEW, 2);
            uri = URI.create("http://localhost:8080/tasks/subtask");
            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(sub4)))
                    .header("Content-Type","application/json").uri(uri).build();
            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

            //получаем все поздачи эпика 2 с помощью GET
            uri = URI.create("http://localhost:8080/tasks/subtask/epic?id="+2);
            request = HttpRequest.newBuilder().GET()
                    .header("Accept", "application/json").uri(uri).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(200,rCode);
            HashMap<Long, SubTask> getResult = getGson().fromJson(response.body(),new TypeToken<HashMap<Long, SubTask>>() {}.getType());
            assertEquals(2, getResult.size());

        } catch(IllegalArgumentException ex){
            ex.getMessage();
        } catch (IOException | InterruptedException ex){
            ex.getMessage();
        }

    }

    @Test
    public void GetPrioritizedTasks(){

        try {

            URI uri = URI.create("http://localhost:8080/tasks/epic");
            HttpClient client = HttpClient.newHttpClient();
            //Cоздаем два эпика
            EpicTask epic1 = new EpicTask("Наименование эпика 1 POST", "описание эпика 1 POST");

            HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(epic1)))
                    .header("Content-Type","application/json").uri(uri).build();
            HttpResponse<String> response = client.send (request, HttpResponse.BodyHandlers.ofString());
            int rCode = response.statusCode();
            assertEquals(201,rCode);

            EpicTask epic2 = new EpicTask("Наименование эпика 2 POST", "описание эпика 2 POST");

            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(epic2)))
                    .header("Content-Type","application/json").uri(uri).build();
            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

            //создаем по 2 подзадачи к каждому эпику
            SubTask sub1 = new SubTask("Наименование подзадачи 1 POST",
                    "1 подзадача 1 эпика", TaskStatus.NEW, 1, LocalDateTime.now().plusMinutes(200), 20);
            uri = URI.create("http://localhost:8080/tasks/subtask");
            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(sub1)))
                    .header("Content-Type","application/json").uri(uri).build();
            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

            SubTask sub2 = new SubTask("Наименование подзадачи 2 POST",
                    "2 подзадача первого эпика", TaskStatus.NEW, 1);
            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(sub2)))
                    .header("Content-Type","application/json").uri(uri).build();
            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

            SubTask sub3 = new SubTask("Наименование подзадачи 3 POST",
                    "1 подзадача 2 эпика", TaskStatus.NEW, 2);
            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(sub3)))
                    .header("Content-Type","application/json").uri(uri).build();
            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

            SubTask sub4 = new SubTask("Наименование подзадачи 4 POST",
                    "2 подзадача 2 эпика", TaskStatus.NEW, 2);
            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(sub4)))
                    .header("Content-Type","application/json").uri(uri).build();
            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

            Task task1 = new Task("Наименование задачи 1 POST",
                    "1 подзадача 1 эпика", TaskStatus.NEW, LocalDateTime.now(), 20);
            uri = URI.create("http://localhost:8080/tasks/task");
            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(task1)))
                    .header("Content-Type","application/json").uri(uri).build();
            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

            //получаем все задачи prioritizedTasks
            uri = URI.create("http://localhost:8080/tasks");
            request = HttpRequest.newBuilder().GET()
                    .header("Accept", "application/json").uri(uri).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(200,rCode);


        } catch(IllegalArgumentException ex){
            ex.getMessage();
        } catch (IOException | InterruptedException ex){
            ex.getMessage();
        }

    }

    @Test
    public void GetHistoryWithoutDuplicates(){

        try {

            URI uri = URI.create("http://localhost:8080/tasks/epic");
            HttpClient client = HttpClient.newHttpClient();
            //Cоздаем два эпика
            EpicTask epic1 = new EpicTask("Наименование эпика 1 POST", "описание эпика 1 POST");

            HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(epic1)))
                    .header("Content-Type","application/json").uri(uri).build();
            HttpResponse<String> response = client.send (request, HttpResponse.BodyHandlers.ofString());
            int rCode = response.statusCode();
            assertEquals(201,rCode);

            EpicTask epic2 = new EpicTask("Наименование эпика 2 POST", "описание эпика 2 POST");

            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(epic2)))
                    .header("Content-Type","application/json").uri(uri).build();
            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

            //создаем по 2 подзадачи к каждому эпику
            SubTask sub1 = new SubTask("Наименование подзадачи 1 POST",
                    "1 подзадача 1 эпика", TaskStatus.NEW, 1, LocalDateTime.now().plusMinutes(200), 20);
            uri = URI.create("http://localhost:8080/tasks/subtask");
            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(sub1)))
                    .header("Content-Type","application/json").uri(uri).build();
            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

            SubTask sub2 = new SubTask("Наименование подзадачи 2 POST",
                    "2 подзадача первого эпика", TaskStatus.NEW, 1);
            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(sub2)))
                    .header("Content-Type","application/json").uri(uri).build();
            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

            SubTask sub3 = new SubTask("Наименование подзадачи 3 POST",
                    "1 подзадача 2 эпика", TaskStatus.NEW, 2);
            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(sub3)))
                    .header("Content-Type","application/json").uri(uri).build();
            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

            SubTask sub4 = new SubTask("Наименование подзадачи 4 POST",
                    "2 подзадача 2 эпика", TaskStatus.NEW, 2);
            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(sub4)))
                    .header("Content-Type","application/json").uri(uri).build();
            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

            Task task1 = new Task("Наименование задачи 1 POST",
                    "описание задачи", TaskStatus.NEW, LocalDateTime.now(), 20);
            uri = URI.create("http://localhost:8080/tasks/task");
            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(task1)))
                    .header("Content-Type","application/json").uri(uri).build();
            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

            //получаем задачу c id=7
            uri = URI.create("http://localhost:8080/tasks/task?id=7");
            request = HttpRequest.newBuilder().GET()
                    .header("Accept", "application/json").uri(uri).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(200,rCode);

            //получаем эпик c id=1
            uri = URI.create("http://localhost:8080/tasks/epic?id=1");
            request = HttpRequest.newBuilder().GET()
                    .header("Accept", "application/json").uri(uri).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(200,rCode);

            //получаем подзадачу с id=3
            uri = URI.create("http://localhost:8080/tasks/subtask?id=3");
            request = HttpRequest.newBuilder().GET()
                    .header("Accept", "application/json").uri(uri).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(200,rCode);

            //еще раз получаем задачу c id=7
            uri = URI.create("http://localhost:8080/tasks/task?id=7");
            request = HttpRequest.newBuilder().GET()
                    .header("Accept", "application/json").uri(uri).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(200,rCode);

            //в истории должны быть 1, 3, 7 без повторов
            uri = URI.create("http://localhost:8080/tasks/history");
            request = HttpRequest.newBuilder().GET()
                    .header("Accept", "application/json").uri(uri).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(200,rCode);
            assertNotNull(response.body());
            ArrayList<Task> getResult = getGson().fromJson(response.body(),new TypeToken<ArrayList<Task>>() {}.getType());
            assertEquals(3, getResult.size());

        } catch(IllegalArgumentException ex){
            ex.getMessage();
        } catch (IOException | InterruptedException ex){
            ex.getMessage();
        }

    }

    @Test
    public void UpdateTask(){
        try {
            URI uri = URI.create("http://localhost:8080/tasks/task");
            HttpClient client = HttpClient.newHttpClient();

            //Cоздаем задачу с помощью метода POST
            Task task1 = new Task("Наименование задачи 1 POST", "описание задачи 1 POST",
                    TaskStatus.NEW, LocalDateTime.now(), 10);

            HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(task1)))
                    .header("Content-Type","application/json").uri(uri).build();
            HttpResponse<String> response = client.send (request, HttpResponse.BodyHandlers.ofString());
            int rCode = response.statusCode();
            assertEquals(201,rCode);

            //Обновляем задачу (ID задачи уже существует) методом POST
            Task task2 = new Task("Обновленное наименование", "обновленное описание",
                    TaskStatus.IN_PROCESS, LocalDateTime.now().plusMinutes(80), 15);
            task2.setTaskId(1L);

            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(task2)))
                    .header("Content-Type","application/json").uri(uri).build();
            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

            //получаем задачу с помощью GET
            uri = URI.create("http://localhost:8080/tasks/task?id="+1);
            request = HttpRequest.newBuilder().GET()
                    .header("Accept", "application/json").uri(uri).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(200,rCode);
            assertNotNull(response.body());
            assertEquals("Обновленное наименование", getGson().fromJson(response.body(), Task.class).getName());

        } catch(IllegalArgumentException ex){
            ex.getMessage();
        } catch (IOException | InterruptedException ex){
            ex.getMessage();
        }

    }

    @Test
    public void UpdateEpic(){
        try {
            URI uri = URI.create("http://localhost:8080/tasks/epic");
            HttpClient client = HttpClient.newHttpClient();

            //Cоздаем эпик с помощью метода POST
            EpicTask epic1 = new EpicTask("Наименование задачи 1 POST", "описание задачи 1 POST");
            HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(epic1)))
                    .header("Content-Type","application/json").uri(uri).build();
            HttpResponse<String> response = client.send (request, HttpResponse.BodyHandlers.ofString());
            int rCode = response.statusCode();
            assertEquals(201,rCode);

            //создаем подзадачу для эпика
            uri = URI.create("http://localhost:8080/tasks/subtask");
            SubTask task1 = new SubTask("Наименование задачи 1 POST", "описание задачи 1 POST",
                    TaskStatus.NEW, 1, LocalDateTime.now(), 10);

            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(task1)))
                    .header("Content-Type","application/json").uri(uri).build();
            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

            //Обновляем эпик (ID эпика уже существует) методом POST
            uri = URI.create("http://localhost:8080/tasks/epic");
            EpicTask epic2 = new EpicTask("Обновленное наименование", "обновленное описание");
            epic2.setTaskId(1L);

            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(epic2)))
                    .header("Content-Type","application/json").uri(uri).build();
            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);


            //получаем эпик с помощью GET. Проверяем что подзадачи созранились
            uri = URI.create("http://localhost:8080/tasks/epic?id="+1);
            request = HttpRequest.newBuilder().GET()
                    .header("Accept", "application/json").uri(uri).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(200,rCode);
            assertNotNull(response.body());
            assertEquals("Обновленное наименование", getGson().fromJson(response.body(), EpicTask.class).getName());

        } catch(IllegalArgumentException ex){
            ex.getMessage();
        } catch (IOException | InterruptedException ex){
            ex.getMessage();
        }

    }

    @Test
    public void UpdateSubTask(){
        try {
            HttpClient client = HttpClient.newHttpClient();
            // создаем эпик
            URI uri = URI.create("http://localhost:8080/tasks/epic");
            EpicTask epic1 = new EpicTask("Наименование эпика 1 POST", "описание задачи 1 POST");

            HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(epic1)))
                    .header("Content-Type","application/json").uri(uri).build();
            HttpResponse<String> response = client.send (request, HttpResponse.BodyHandlers.ofString());
            int rCode = response.statusCode();
            assertEquals(201,rCode);

            //создаем подзадачу
            uri = URI.create("http://localhost:8080/tasks/subtask");
            SubTask task1 = new SubTask("Наименование задачи 1 POST", "описание задачи 1 POST",
                    TaskStatus.NEW, 1, LocalDateTime.now(), 10);

            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(task1)))
                    .header("Content-Type","application/json").uri(uri).build();
            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

            //Обновляем подзадачу (ID подзадачи уже существует) методом POST
            SubTask task2 = new SubTask("Обновленное наименование", "обновленное описание",
                    TaskStatus.IN_PROCESS, 1, LocalDateTime.now().plusMinutes(80), 15);
            task2.setTaskId(2L);

            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(task2)))
                    .header("Content-Type","application/json").uri(uri).build();
            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

            //получаем подзадачу с помощью GET
            uri = URI.create("http://localhost:8080/tasks/subtask?id="+2);
            request = HttpRequest.newBuilder().GET()
                    .header("Accept", "application/json").uri(uri).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(200,rCode);
            assertNotNull(response.body());
            assertEquals("Обновленное наименование", getGson().fromJson(response.body(), SubTask.class).getName());

        } catch(IllegalArgumentException ex){
            ex.getMessage();
        } catch (IOException | InterruptedException ex){
            ex.getMessage();
        }

    }

    @Test
    public void DeleteTaskById(){
        try {
            URI uri = URI.create("http://localhost:8080/tasks/task");
            HttpClient client = HttpClient.newHttpClient();

            //Cоздаем задачу с помощью метода POST
            Task task1 = new Task("Наименование задачи 1 POST", "описание задачи 1 POST",
                    TaskStatus.NEW, LocalDateTime.now(), 10);

            HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(task1)))
                    .header("Content-Type","application/json").uri(uri).build();
            HttpResponse<String> response = client.send (request, HttpResponse.BodyHandlers.ofString());
            int rCode = response.statusCode();
            assertEquals(201,rCode);

            //Удаляем задачу id=1 методом DELETE
            uri = URI.create("http://localhost:8080/tasks/task?id=1");
            request = HttpRequest.newBuilder().DELETE()
                    .uri(uri).build();
            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(200,rCode);

            //получаем задачу с помощью GET
            uri = URI.create("http://localhost:8080/tasks/task?id="+1);
            request = HttpRequest.newBuilder().GET()
                    .header("Accept", "application/json").uri(uri).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(200,rCode);
            assertNull(getGson().fromJson(response.body(), Task.class));

        } catch(IllegalArgumentException ex){
            ex.getMessage();
        } catch (IOException | InterruptedException ex){
            ex.getMessage();
        }

    }
    @Test
    public void DeleteSubTaskById(){
        try {
            HttpClient client = HttpClient.newHttpClient();
            // создаем эпик
            URI uri = URI.create("http://localhost:8080/tasks/epic");
            EpicTask epic1 = new EpicTask("Наименование эпика 1 POST", "описание задачи 1 POST");

            HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(epic1)))
                    .header("Content-Type","application/json").uri(uri).build();
            HttpResponse<String> response = client.send (request, HttpResponse.BodyHandlers.ofString());
            int rCode = response.statusCode();
            assertEquals(201,rCode);

            //создаем подзадачу
            uri = URI.create("http://localhost:8080/tasks/subtask");
            SubTask task1 = new SubTask("Наименование задачи 1 POST", "описание задачи 1 POST",
                    TaskStatus.NEW, 1, LocalDateTime.now(), 10);

            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(task1)))
                    .header("Content-Type","application/json").uri(uri).build();
            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

            //Удаляем подзадачу методом DELETE
            uri = URI.create("http://localhost:8080/tasks/subtask?id=2");
            request = HttpRequest.newBuilder().DELETE()
                    .uri(uri).build();
            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(200,rCode);

            //получаем подзадачу с помощью GET
            uri = URI.create("http://localhost:8080/tasks/subtask?id="+2);
            request = HttpRequest.newBuilder().GET()
                    .header("Accept", "application/json").uri(uri).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(200,rCode);

            //получаем подзадачи эпика
            //получаем все поздачи эпика 2 с помощью GET
            uri = URI.create("http://localhost:8080/tasks/subtask/epic?id="+1);
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(200,rCode);
            assertNull(getGson().fromJson(response.body(), SubTask.class));

        } catch(IllegalArgumentException ex){
            ex.getMessage();
        } catch (IOException | InterruptedException ex){
            ex.getMessage();
        }

    }
    @Test
    public void DeleteEpicById(){
        try {
            URI uri = URI.create("http://localhost:8080/tasks/epic");
            HttpClient client = HttpClient.newHttpClient();

            //Cоздаем эпик с помощью метода POST
            EpicTask epic1 = new EpicTask("Наименование задачи 1 POST", "описание задачи 1 POST");
            HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(epic1)))
                    .header("Content-Type","application/json").uri(uri).build();
            HttpResponse<String> response = client.send (request, HttpResponse.BodyHandlers.ofString());
            int rCode = response.statusCode();
            assertEquals(201,rCode);

            //создаем подзадачу для эпика
            uri = URI.create("http://localhost:8080/tasks/subtask");
            SubTask task1 = new SubTask("Наименование задачи 1 POST", "описание задачи 1 POST",
                    TaskStatus.NEW, 1, LocalDateTime.now(), 10);

            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(task1)))
                    .header("Content-Type","application/json").uri(uri).build();
            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

            //Удаляем эпик методом DELETE
            uri = URI.create("http://localhost:8080/tasks/epic?id=1");

            request = HttpRequest.newBuilder().DELETE()
                    .uri(uri).build();
            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(200,rCode);


            //получаем эпик и его подзадачу с помощью GET. Проверяем что они удалились
            uri = URI.create("http://localhost:8080/tasks/epic?id="+1);
            request = HttpRequest.newBuilder().GET()
                    .header("Accept", "application/json").uri(uri).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(200,rCode);

            //получаем эпик и его подзадачу с помощью GET. Проверяем что они удалились
            uri = URI.create("http://localhost:8080/tasks/subtask?id="+2);
            request = HttpRequest.newBuilder().GET()
                    .header("Accept", "application/json").uri(uri).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(200,rCode);
            assertNull(getGson().fromJson(response.body(), EpicTask.class));

        } catch(IllegalArgumentException ex){
            ex.getMessage();
        } catch (IOException | InterruptedException ex){
            ex.getMessage();
        }
    }

    @Test
    public void DeleteAllTasks(){
        try {
            URI uri = URI.create("http://localhost:8080/tasks/task");
            HttpClient client = HttpClient.newHttpClient();

            //Cоздаем задачу с помощью метода POST
            Task task1 = new Task("Наименование задачи 1 POST", "описание задачи 1 POST",
                    TaskStatus.NEW, LocalDateTime.now(), 10);

            HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(task1)))
                    .header("Content-Type","application/json").uri(uri).build();
            HttpResponse<String> response = client.send (request, HttpResponse.BodyHandlers.ofString());
            int rCode = response.statusCode();
            assertEquals(201,rCode);

            //Cоздаем задачу с помощью метода POST
            Task task2 = new Task("Наименование задачи 1 POST", "описание задачи 1 POST",
                    TaskStatus.NEW, LocalDateTime.now().plusMinutes(40), 10);

            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(task2)))
                    .header("Content-Type","application/json").uri(uri).build();
            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

            //Удаляем все задачи методом DELETE
            request = HttpRequest.newBuilder().DELETE()
                    .uri(uri).build();
            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(200,rCode);

            //получаем все задачи с помощью GET
            uri = URI.create("http://localhost:8080/tasks/task");
            request = HttpRequest.newBuilder().GET()
                    .header("Accept", "application/json").uri(uri).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(200,rCode);
            ArrayList <Task> result = getGson().fromJson(response.body(), new TypeToken<ArrayList<Task>>() {}.getType());
            assertEquals(0, result.size());

        } catch(IllegalArgumentException ex){
            ex.getMessage();
        } catch (IOException | InterruptedException ex){
            ex.getMessage();
        }

    }
    @Test
    public void DeleteAllSubTasks(){
        try {
            HttpClient client = HttpClient.newHttpClient();
            // создаем эпик
            URI uri = URI.create("http://localhost:8080/tasks/epic");
            EpicTask epic1 = new EpicTask("Наименование эпика 1 POST", "описание задачи 1 POST");

            HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(epic1)))
                    .header("Content-Type","application/json").uri(uri).build();
            HttpResponse<String> response = client.send (request, HttpResponse.BodyHandlers.ofString());
            int rCode = response.statusCode();
            assertEquals(201,rCode);

            //создаем подзадачу
            uri = URI.create("http://localhost:8080/tasks/subtask");
            SubTask task1 = new SubTask("Наименование задачи 1 POST", "описание задачи 1 POST",
                    TaskStatus.NEW, 1, LocalDateTime.now(), 10);

            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(task1)))
                    .header("Content-Type","application/json").uri(uri).build();
            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

            //создаем еще одну подзадачу
            SubTask task2 = new SubTask("Наименование задачи 2 POST", "описание задачи 2 POST",
                    TaskStatus.NEW, 1, LocalDateTime.now().plusMinutes(100), 10);

            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(task2)))
                    .header("Content-Type","application/json").uri(uri).build();
            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

            //Удаляем все подзадачи методом DELETE
            uri = URI.create("http://localhost:8080/tasks/subtask");
            request = HttpRequest.newBuilder().DELETE()
                    .uri(uri).build();
            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(200,rCode);


            //получаем все подзадачи с помощью GET
            uri = URI.create("http://localhost:8080/tasks/subtask");
            request = HttpRequest.newBuilder().GET()
                    .header("Accept", "application/json").uri(uri).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(200,rCode);
            ArrayList <Task> result = getGson().fromJson(response.body(), new TypeToken<ArrayList<Task>>() {}.getType());
            assertEquals(0, result.size());

        } catch(IllegalArgumentException ex){
            ex.getMessage();
        } catch (IOException | InterruptedException ex){
            ex.getMessage();
        }

    }
    @Test
    public void DeleteAllEpics(){
        try {
            URI uri = URI.create("http://localhost:8080/tasks/epic");
            HttpClient client = HttpClient.newHttpClient();

            //Cоздаем эпик с помощью метода POST
            EpicTask epic1 = new EpicTask("Наименование задачи 1 POST", "описание задачи 1 POST");
            HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(epic1)))
                    .header("Content-Type","application/json").uri(uri).build();
            HttpResponse<String> response = client.send (request, HttpResponse.BodyHandlers.ofString());
            int rCode = response.statusCode();
            assertEquals(201,rCode);

            //Cоздаем еще один эпик с помощью метода POST
            EpicTask epic2 = new EpicTask("Наименование задачи 1 POST", "описание задачи 1 POST");
            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(epic2)))
                    .header("Content-Type","application/json").uri(uri).build();
            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

            //создаем подзадачу для эпика
            uri = URI.create("http://localhost:8080/tasks/subtask");
            SubTask task1 = new SubTask("Наименование задачи 1 POST", "описание задачи 1 POST",
                    TaskStatus.NEW, 1, LocalDateTime.now(), 10);

            request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(taskToJSon(task1)))
                    .header("Content-Type","application/json").uri(uri).build();
            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(201,rCode);

            //Удаляем эпики методом DELETE
            uri = URI.create("http://localhost:8080/tasks/epic");

            request = HttpRequest.newBuilder().DELETE()
                    .uri(uri).build();
            response = client.send (request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(200,rCode);


            //получаем эпики и подзадачи с помощью GET. Проверяем что они удалились
            uri = URI.create("http://localhost:8080/tasks/epic");
            request = HttpRequest.newBuilder().GET()
                    .header("Accept", "application/json").uri(uri).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(200,rCode);
            //получаем эпик и его подзадачу с помощью GET. Проверяем что они удалились
            uri = URI.create("http://localhost:8080/tasks/subtask");
            request = HttpRequest.newBuilder().GET()
                    .header("Accept", "application/json").uri(uri).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            rCode = response.statusCode();
            assertEquals(200,rCode);
            ArrayList <Task> result = getGson().fromJson(response.body(), new TypeToken<ArrayList<EpicTask>>() {}.getType());
            assertEquals(0, result.size());

        } catch(IllegalArgumentException ex){
            ex.getMessage();
        } catch (IOException | InterruptedException ex){
            ex.getMessage();
        }
    }

    private Gson getGson(){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
        return gson;
    }
    private String taskToJSon (Task task){

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();

        String result ="";
        if (task.getClass().getSimpleName().equals("Task")){
            result = gson.toJson(task);

        } else if (task.getClass().getSimpleName().equals("EpicTask")){
            result = gson.toJson((EpicTask)task);

        } else if (task.getClass().getSimpleName().equals("SubTask")){
            result = gson.toJson((SubTask)task);

        }

        return result;
    }
}
