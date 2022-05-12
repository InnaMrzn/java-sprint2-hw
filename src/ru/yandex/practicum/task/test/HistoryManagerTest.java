package ru.yandex.practicum.task.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.task.constants.TaskStatus;
import ru.yandex.practicum.task.logic.Managers;
import ru.yandex.practicum.task.logic.TaskManager;
import ru.yandex.practicum.task.models.Task;
import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    TaskManager taskManager;

    @BeforeEach

    public void beforeEach(){
        taskManager = Managers.getDefault();
    }

    @Test
    void addAndReceiveOneTask() {
        taskManager.deleteAllTasks();
        Task task = new Task("Задача 1","Описание задачи 1", TaskStatus.NEW);
        Task result = taskManager.createNewTask(task);
        taskManager.getTaskById(result.getTaskId());
        assertEquals(1,taskManager.getHistory().size());
    }

    @Test
    void getEmptyHistoryShouldReturn0() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubTasks();
        taskManager.deleteAllEpics();
        assertEquals(0,taskManager.getHistory().size());
    }

    @Test
    void getHistory4ElementsWithoutDuplicatesShouldReturn4() {
        Task task = new Task("Задача 1","Описание задачи 1", TaskStatus.NEW);
        Task task1 = taskManager.createNewTask(task);
        task = new Task("Задача 2","Описание задачи 2", TaskStatus.NEW);
        Task task2 = taskManager.createNewTask(task);
        task = new Task("Задача 3","Описание задачи 3", TaskStatus.NEW);
        Task task3 = taskManager.createNewTask(task);
        task = new Task("Задача 4","Описание задачи 4", TaskStatus.NEW);
        Task task4 = taskManager.createNewTask(task);
        taskManager.getTaskById(task2.getTaskId());
        taskManager.getTaskById(task1.getTaskId());
        taskManager.getTaskById(task4.getTaskId());
        taskManager.getTaskById(task3.getTaskId());
        assertEquals(4,taskManager.getHistory().size());

    }

    @Test
    void getHistory5ElementsWith1DuplicateShouldReturn4() {
        taskManager.deleteAllTasks();
        Task task = new Task("Задача 1","Описание задачи 1", TaskStatus.NEW);
        Task task1 = taskManager.createNewTask(task);
        task = new Task("Задача 2","Описание задачи 2", TaskStatus.NEW);
        Task task2 = taskManager.createNewTask(task);
        task = new Task("Задача 3","Описание задачи 3", TaskStatus.NEW);
        Task task3 = taskManager.createNewTask(task);
        task = new Task("Задача 4","Описание задачи 4", TaskStatus.NEW);
        Task task4 = taskManager.createNewTask(task);
        taskManager.getTaskById(task2.getTaskId());
        taskManager.getTaskById(task1.getTaskId());
        taskManager.getTaskById(task4.getTaskId());
        taskManager.getTaskById(task2.getTaskId());
        taskManager.getTaskById(task3.getTaskId());
        assertEquals(4,taskManager.getHistory().size());

    }

    @Test
    void removeOneElementShouldReturn3() {
        taskManager.deleteAllTasks();
        Task task = new Task("Задача 1","Описание задачи 1", TaskStatus.NEW);
        Task task1 = taskManager.createNewTask(task);
        task = new Task("Задача 2","Описание задачи 2", TaskStatus.NEW);
        Task task2 = taskManager.createNewTask(task);
        task = new Task("Задача 3","Описание задачи 3", TaskStatus.NEW);
        Task task3 = taskManager.createNewTask(task);
        task = new Task("Задача 4","Описание задачи 4", TaskStatus.NEW);
        Task task4 = taskManager.createNewTask(task);
        taskManager.getTaskById(task2.getTaskId());
        taskManager.getTaskById(task1.getTaskId());
        taskManager.getTaskById(task4.getTaskId());
        taskManager.getTaskById(task3.getTaskId());
        assertEquals(4,taskManager.getHistory().size());
        taskManager.deleteTaskById(task2.getTaskId());
        assertEquals(3,taskManager.getHistory().size());

    }


}