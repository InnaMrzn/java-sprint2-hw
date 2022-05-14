package ru.yandex.practicum.task.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.task.logic.Managers;
import ru.yandex.practicum.task.logic.TaskManager;
import ru.yandex.practicum.task.models.EpicTask;
import ru.yandex.practicum.task.models.SubTask;
import ru.yandex.practicum.task.models.Task;
import ru.yandex.practicum.task.utility.LocalDateTimeAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class HttpTaskServer  {

    private final TaskManager taskManager;
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final HttpServer httpServer;
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public HttpTaskServer() throws IOException {
        this.taskManager = Managers.getDefault();
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskManagerHandler());
    }



    public void start() {
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        httpServer.stop(5);
        System.out.println("HTTP-сервер остановлен на " + PORT + " порту!");
    }

    class TaskManagerHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();
            String subPath = path.substring("/tasks".length());
            String queryString = httpExchange.getRequestURI().getQuery();
            String response = "";
            int rCode =0;
             switch (method) {
                case "POST":
                    InputStream inputStream = httpExchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                    if (body.isBlank()) {
                        httpExchange.sendResponseHeaders(400, 0);
                        httpExchange.close();
                        break;
                    }
                    if (subPath.equalsIgnoreCase("/subtask")) {
                        SubTask subTask = gson.fromJson(body, SubTask.class);
                        if (subTask.getTaskId() == null ||taskManager.getSubTaskById(subTask.getTaskId()) == null) {
                            SubTask result = taskManager.createNewSubTask(subTask);
                            response = "Подзадача успешно создана. Ей присвоен ID "+result.getTaskId();
                        } else {
                            taskManager.updateSubTask(subTask);
                            response = "Подзадача успешно обновлена.";
                        }
                        rCode =201;
                    } else if (subPath.equalsIgnoreCase("/task")) {
                        Task task = gson.fromJson(body, Task.class);
                        if (task.getTaskId() == null ||taskManager.getTaskById(task.getTaskId()) == null) {
                            Task result = taskManager.createNewTask(task);
                            response = "Задача успешно создана. Ей присвоен ID "+result.getTaskId();
                        } else {
                            taskManager.updateTask(task);
                            response = "Задача успешно обновлена.";
                        }
                        rCode =201;
                    } else if (subPath.equalsIgnoreCase("/epic")){
                        EpicTask task = gson.fromJson(body, EpicTask.class);
                        if (task.getTaskId() == null ||taskManager.getEpicTaskById(task.getTaskId()) == null) {
                            EpicTask result = taskManager.createNewEpicTask(task);
                            response = "Эпик успешно создан. Ему присвоен ID "+result.getTaskId();
                        } else {
                            taskManager.updateEpicTask(task);
                            response = "Эпик успешно обновлен.";

                        }
                        rCode =201;
                    } else {
                        rCode =404;
                        response = "путь "+subPath+" не поддерживается";
                    }

                    httpExchange.sendResponseHeaders(rCode, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                    break;
                case "DELETE":

                    if (subPath.equalsIgnoreCase("/subtask")) {
                        if (queryString == null || queryString.isEmpty()) {
                            taskManager.deleteAllSubTasks();
                            response = "Все подзадачи успешно удалены.";
                        } else {
                            taskManager.deleteSubTaskById(Long.parseLong(queryString.substring("id=".length())));
                            response = "Подзадача с ID <ID Добавить сюда> успешно удалена.";
                        }
                        rCode =200;
                    } else if (subPath.equalsIgnoreCase("/task")) {
                        if (queryString == null || queryString.isEmpty()){
                            taskManager.deleteAllTasks();
                            response = "Все задачи успешно удалены.";
                        } else {
                            taskManager.deleteTaskById(Long.parseLong(queryString.substring("id=".length())));
                            response = "Задача с ID <ID Добавить сюда> успешно удалена.";
                        }
                        rCode =200;
                    } else if (subPath.equalsIgnoreCase("/epic")){
                        if (queryString == null || queryString.isEmpty()) {
                            taskManager.deleteAllEpics();
                            response = "Все эпики успешно удалены.";
                        } else {
                            taskManager.deleteEpicById(Long.parseLong(queryString.substring("id=".length())));
                            response = "Эпик с ID <ID Добавить сюда> успешно удален.";
                        }
                        rCode =200;
                    } else {
                        rCode =404;
                        response = "путь "+subPath+" не поддерживается";
                    }

                    httpExchange.sendResponseHeaders(rCode, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                    break;
                case "GET":
                    if (subPath.isEmpty() || subPath.isBlank()) {
                           response = gson.toJson(taskManager.getPrioritizedTasks());
                           rCode =200;
                    } else if (subPath.equalsIgnoreCase("/subtask")) {
                        if (queryString == null || queryString.isEmpty()) {
                            response = gson.toJson(taskManager.getAllSubTasks().values());
                        } else {
                            long id = Long.parseLong(queryString.substring("id=".length()));
                            if (subPath.substring("/subtask".length()).equalsIgnoreCase("/epic"))
                                response = gson.toJson(taskManager.getEpicSubTasks(id));
                            else
                                response = gson.toJson(taskManager.getSubTaskById(id));
                        }
                        rCode =200;
                    } if (subPath.isEmpty() || subPath.isBlank()) {
                    response = gson.toJson(taskManager.getPrioritizedTasks());
                    rCode =200;
                } else if (subPath.equalsIgnoreCase("/subtask")) {
                    if (queryString == null || queryString.isEmpty()) {
                        response = gson.toJson(taskManager.getAllSubTasks().values());
                    } else {
                        long id = Long.parseLong(queryString.substring("id=".length()));
                        response = gson.toJson(taskManager.getSubTaskById(id));
                    }
                    rCode =200;

                } else if (subPath.equalsIgnoreCase("/subtask/epic")) {
                    if (queryString == null || queryString.isEmpty()) {
                        rCode = 400;
                        response = "Необходимо указать ID эпика";
                    } else {
                        long id = Long.parseLong(queryString.substring("id=".length()));
                           response = gson.toJson(taskManager.getEpicSubTasks(id));
                        rCode =200;
                    }

                } else if (subPath.equalsIgnoreCase("/task")) {
                        if (queryString == null || queryString.isEmpty()){
                            response = gson.toJson(taskManager.getAllTasks().values());
                        } else {
                            long id = Long.parseLong(queryString.substring("id=".length()));
                            response = gson.toJson(taskManager.getTaskById(id));
                        }
                        rCode =200;
                    } else if (subPath.equalsIgnoreCase("/epic")){
                        if (queryString == null || queryString.isEmpty()){
                            response = gson.toJson(taskManager.getAllEpics().values());
                        } else {
                            long id = Long.parseLong(queryString.substring("id=".length()));
                            response = gson.toJson(taskManager.getEpicTaskById(id));
                        }
                        rCode =200;
                    } else if (subPath.equalsIgnoreCase("/history")){
                           response = gson.toJson(taskManager.getHistory());
                           rCode =200;
                    } else {
                        rCode =404;
                        response = "путь "+subPath+" не поддерживается";
                    }

                    httpExchange.sendResponseHeaders(rCode, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                    break;
                default:
                    System.out.println("method " + method + " is not supported");
            }

        }


    }

}
