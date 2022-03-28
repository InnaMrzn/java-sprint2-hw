package ru.yandex.practicum.task.models;

public class TaskHistoryNode<T> {
    public Task data;
    public TaskHistoryNode<Task> next;
    public TaskHistoryNode<Task> prev;

    public TaskHistoryNode(TaskHistoryNode<Task> prev, Task data, TaskHistoryNode<Task> next) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }
}