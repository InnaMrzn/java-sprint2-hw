package ru.yandex.practicum.task.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.task.logic.HistoryManager;
import ru.yandex.practicum.task.logic.Managers;
import ru.yandex.practicum.task.logic.TaskManager;
import ru.yandex.practicum.task.models.Task;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {


    static HistoryManager historyMgr;
    static TaskManager taskManager;

    @BeforeAll
    public static void beforeAll (){
        Managers.setIsBacked(false);
        taskManager = Managers.getDefault();
        historyMgr = taskManager.getHistoryManager();
    }

    @AfterEach
    public void afterEach(){
       taskManager.deleteAllEpics();
       taskManager.deleteAllTasks();
       taskManager.deleteAllSubTasks();
       historyMgr.getHistory().clear();
    }

    @Test
    void addAndReceiveOneTask() {
        Task task = new Task("Задача 1","Описание задачи 1");
        long taskID = taskManager.createNewTask(task);
        taskManager.getTaskByID(taskID);
        assertEquals(1,historyMgr.getHistory().size());
    }

    @Test
    void getEmptyHistoryShouldReturn0() {

        assertEquals(0,historyMgr.getHistory().size());
    }

    @Test
    void getHistory4ElementsWithoutDuplicatesShouldReturn4() {
        Task task = new Task("Задача 1","Описание задачи 1");
        long taskID1 = taskManager.createNewTask(task);
        task = new Task("Задача 2","Описание задачи 2");
        long taskID2 = taskManager.createNewTask(task);
        task = new Task("Задача 3","Описание задачи 3");
        long taskID3 = taskManager.createNewTask(task);
        task = new Task("Задача 4","Описание задачи 4");
        long taskID4 = taskManager.createNewTask(task);
        taskManager.getTaskByID(taskID2);
        taskManager.getTaskByID(taskID1);
        taskManager.getTaskByID(taskID4);
        taskManager.getTaskByID(taskID3);
        assertEquals(4,historyMgr.getHistory().size());

    }

    @Test
    void getHistory5ElementsWith1DuplicateShouldReturn4() {
        Task task = new Task("Задача 1","Описание задачи 1");
        long taskID1 = taskManager.createNewTask(task);
        task = new Task("Задача 2","Описание задачи 2");
        long taskID2 = taskManager.createNewTask(task);
        task = new Task("Задача 3","Описание задачи 3");
        long taskID3 = taskManager.createNewTask(task);
        task = new Task("Задача 4","Описание задачи 4");
        long taskID4 = taskManager.createNewTask(task);
        taskManager.getTaskByID(taskID2);
        taskManager.getTaskByID(taskID1);
        taskManager.getTaskByID(taskID4);
        taskManager.getTaskByID(taskID2);
        taskManager.getTaskByID(taskID3);
        assertEquals(4,historyMgr.getHistory().size());

    }

    @Test
    void removeOneElementShouldReturn3() {
        Task task = new Task("Задача 1","Описание задачи 1");
        long taskID1 = taskManager.createNewTask(task);
        task = new Task("Задача 2","Описание задачи 2");
        long taskID2 = taskManager.createNewTask(task);
        task = new Task("Задача 3","Описание задачи 3");
        long taskID3 = taskManager.createNewTask(task);
        task = new Task("Задача 4","Описание задачи 4");
        long taskID4 = taskManager.createNewTask(task);
        taskManager.getTaskByID(taskID2);
        taskManager.getTaskByID(taskID1);
        taskManager.getTaskByID(taskID4);
        taskManager.getTaskByID(taskID3);
        assertEquals(4,historyMgr.getHistory().size());
        historyMgr.remove(taskID2);
        assertEquals(3,historyMgr.getHistory().size());

    }
}