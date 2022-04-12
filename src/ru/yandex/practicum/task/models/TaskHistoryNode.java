package ru.yandex.practicum.task.models;

public class TaskHistoryNode {

    private Task data;
    public TaskHistoryNode prev;
    public TaskHistoryNode next;

    public TaskHistoryNode(TaskHistoryNode prev, Task data, TaskHistoryNode next) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }

    public Task getData() {

        return data;
    }

}