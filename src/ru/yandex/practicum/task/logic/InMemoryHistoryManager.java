package ru.yandex.practicum.task.logic;

import ru.yandex.practicum.task.models.Task;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    private static List<Task> historyList = new LinkedList();
    private static long MAX_HISTORY_LENGTH = 10L;

    @Override
    public void add(Task task) {
        if (historyList.size()>=MAX_HISTORY_LENGTH){
            historyList.remove(0);
        }
        historyList.add(task);
    }

    @Override
    public List<Task> getHistory(){

        return historyList;

    }

}
