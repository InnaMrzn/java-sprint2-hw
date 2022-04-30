package ru.yandex.practicum.task.models;

public class TaskHistoryNode {

    private final Task data;
    private TaskHistoryNode prev;
    public TaskHistoryNode next;


    public TaskHistoryNode getNext() {
        return next;
    }

    public void setNext(TaskHistoryNode next) {
        this.next = next;
    }

    public TaskHistoryNode getPrev() {
        return prev;
    }

    public void setPrev(TaskHistoryNode prev) {
        this.prev = prev;
    }

    public TaskHistoryNode(TaskHistoryNode prev, Task data, TaskHistoryNode next) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }

    public Task getData() {

        return data;
    }

}