package ru.yandex.practicum.task.logic;

import ru.yandex.practicum.task.models.EpicTask;
import ru.yandex.practicum.task.models.SubTask;
import ru.yandex.practicum.task.models.Task;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;


public interface TaskManager {

    //этот метод добавлен для целей тестирования. Можно его удалить после тестирования
    long getNextTaskID();

    List<Task> getHistory();

    TreeSet<Task> getPrioritizedTasks();

    List<Task> getAllTasks();

    List<EpicTask> getAllEpics();

    List<SubTask> getAllSubTasks();

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

    Task createNewTask (Task task);

    EpicTask createNewEpicTask (EpicTask task);

    SubTask createNewSubTask (SubTask task);

    HashMap<Long, SubTask> getEpicSubTasks (Long epicID);




}
