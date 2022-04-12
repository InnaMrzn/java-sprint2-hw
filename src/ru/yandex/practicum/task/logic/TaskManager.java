package ru.yandex.practicum.task.logic;

import ru.yandex.practicum.task.models.EpicTask;
import ru.yandex.practicum.task.models.SubTask;
import ru.yandex.practicum.task.models.Task;
import java.util.HashMap;


public interface TaskManager {

    //этот метод добавлен для целей тестирования. Можно его удалить после тестирования
    long getNextTaskID();

    HistoryManager getHistoryManager();

    HashMap<Long, Task> getAllTasks();

    HashMap<Long, EpicTask> getAllEpics();

    HashMap<Long, SubTask> getAllSubTasks();

    void deleteAllTasks();

    void deleteAllSubTasks();

    void deleteAllEpics ();

    void deleteTaskByID(Long taskID);

    void deleteEpicByID(Long taskID);

    void deleteSubTaskByID(Long subTaskID);

    SubTask getSubTaskByID (Long id);

    Task getTaskByID (Long id);

    EpicTask getEpicTaskByID (Long id);

    void updateTask (Task task);

    void updateSubTask (SubTask task);

    void updateEpicTask (EpicTask updatedEpic);

    void createNewTask (Task task);

    void createNewEpicTask (EpicTask task);

    void createNewSubTask (SubTask task);

    HashMap<Long, SubTask> getEpicSubTasks (Long epicID);




}
