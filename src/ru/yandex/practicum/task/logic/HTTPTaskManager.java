package ru.yandex.practicum.task.logic;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import ru.yandex.practicum.task.client.KVTaskClient;
import ru.yandex.practicum.task.models.EpicTask;
import ru.yandex.practicum.task.models.SubTask;
import ru.yandex.practicum.task.models.Task;
import ru.yandex.practicum.task.models.TaskHistoryNode;
import ru.yandex.practicum.task.utility.LocalDateTimeAdapter;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Map;

class HistoryManagerCreator implements InstanceCreator {
    @Override
    public HistoryManager createInstance(Type type) {
        return Managers.getDefaultHistory();
    }
}

class TaskHistoryNodeSerializator implements JsonSerializer<TaskHistoryNode> {
    public JsonElement serialize(TaskHistoryNode src, Type typeOfSrc, JsonSerializationContext context) {
        Task task = src.getData();
        return new JsonPrimitive(task.getTaskId());
    }
}

public class HTTPTaskManager extends FileBackedTaskManager{

    transient final private KVTaskClient kvClient;
    transient final private String key ="taskManager";

    public HTTPTaskManager (String serverURL){

        super();
        kvClient = new KVTaskClient(serverURL);
        loadFromStorage(kvClient.load(key));

    }


    @Override
    public String toString() {
        Type mapType = new TypeToken<Map<Integer, Task>>() {}.getType();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(HistoryManager.class, new HistoryManagerCreator())
                .registerTypeAdapter(TaskHistoryNode.class, new TaskHistoryNodeSerializator())
                .setPrettyPrinting()
                .create();

        return gson.toJson(this);
    }


    @Override
    protected void loadFromStorage(String storageContent){
        Type taskMapType = new TypeToken<Map<Long, Task>>() {}.getType();
        Type subTaskMapType = new TypeToken<Map<Long, SubTask>>() {}.getType();
        Type epicMapType = new TypeToken<Map<Long, EpicTask>>() {}.getType();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(HistoryManager.class, new HistoryManagerCreator())
                .registerTypeAdapter(TaskHistoryNode.class, new TaskHistoryNodeSerializator())
                .create();

        JsonElement jsonElement = JsonParser.parseString(storageContent);
        if (jsonElement.isJsonObject()) {
            JsonObject jSonTaskManager = jsonElement.getAsJsonObject();
            currentTaskId = jSonTaskManager.get("currentTaskId").getAsLong();
            if (jSonTaskManager.get("tasks").isJsonObject()){
                tasks.putAll(gson.fromJson(jSonTaskManager.get("tasks"), taskMapType));
            }

            if (jSonTaskManager.get("subTasks").isJsonObject()){
                subTasks.putAll(gson.fromJson(jSonTaskManager.get("subTasks"), subTaskMapType));
            }

            if (jSonTaskManager.get("epicTasks").isJsonObject()){
                epicTasks.putAll(gson.fromJson(jSonTaskManager.get("epicTasks"), epicMapType));
            }

            if (jSonTaskManager.get("historyManager").isJsonObject()){
                JsonObject jSonHistoryManager = jSonTaskManager.get("historyManager").getAsJsonObject();
                JsonElement jSonHistoryIds = jSonHistoryManager.get("inHistoryNodes");
                if (jSonHistoryIds.isJsonObject()){
                    loadHistoryFromStorage(jSonHistoryIds.getAsJsonObject().toString());

                }
            }

        }
    }

    @Override
    protected void save (){
      kvClient.put(key,this.toString());
    }

   @Override
    protected void loadHistoryFromStorage(String historyIDsString){
        JsonElement jSonHistoryIds = JsonParser.parseString(historyIDsString);
        if (jSonHistoryIds.isJsonObject()) {
            for (String nextId : jSonHistoryIds.getAsJsonObject().keySet()) {
                Long id = jSonHistoryIds.getAsJsonObject().get(nextId).getAsLong();
                Task task = null;
                if (tasks.get(id) != null)
                    task = tasks.get(id);
                else if (subTasks.get(id) != null)
                    task = subTasks.get(id);
                else if (epicTasks.get(id) != null)
                    task = epicTasks.get(id);
                historyManager.add(task);

            }
        }

    }

}
