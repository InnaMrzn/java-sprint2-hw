package ru.yandex.practicum.task.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import ru.yandex.practicum.task.logic.Managers;


class InMemoryTaskManagerTest extends TaskManagerTest {

    @BeforeAll
    public static void beforeAll (){
        Managers.setIsBacked(false);
        taskManager = Managers.getDefault();
    }

    @AfterEach
    public void afterEach(){
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubTasks();
        taskManager.deleteAllEpics();
        taskManager.getPrioritizedTasks().clear();
        taskManager.getHistoryManager().getHistory().clear();

    }

}