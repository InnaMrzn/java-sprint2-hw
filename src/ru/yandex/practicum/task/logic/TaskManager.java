package ru.yandex.practicum.task.logic;

import ru.yandex.practicum.task.models.EpicTask;
import ru.yandex.practicum.task.models.SubTask;
import ru.yandex.practicum.task.models.Task;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;


public interface TaskManager {

    //этот метод добавлен для целей тестирования. Можно его удалить после тестирования
    List<Task> getHistory();

    TreeSet<Task> getPrioritizedTasks();

    Map<Long, Task> getAllTasks();

    Map<Long, EpicTask> getAllEpics();

    Map<Long, SubTask> getAllSubTasks();

    void deleteAllTasks();

    void deleteAllSubTasks();

    void deleteAllEpics ();

    void deleteTaskById(Long taskID);

    void deleteEpicById(Long taskID);

    void deleteSubTaskById(Long subTaskID);

    SubTask getSubTaskById (Long id);

    Task getTaskById (Long id);

    EpicTask getEpicTaskById (Long id);

    void updateTask (Task task);

    void updateSubTask (SubTask task);

    void updateEpicTask (EpicTask updatedEpic);

    Task createNewTask (Task task);

    EpicTask createNewEpicTask (EpicTask task);

    SubTask createNewSubTask (SubTask task);

    Map<Long, SubTask> getEpicSubTasks (Long epicID);




}
