package ru.yandex.practicum.task.logic;

import ru.yandex.practicum.task.models.Task;
import java.util.List;

public interface HistoryManager {

    void add(Task task);

    List<Task> getHistory();

}
