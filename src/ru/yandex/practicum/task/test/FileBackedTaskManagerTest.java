package ru.yandex.practicum.task.test;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.task.constants.TaskStatus;
import ru.yandex.practicum.task.models.EpicTask;
import ru.yandex.practicum.task.models.Task;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest {



    @Test
    void writeEmptyTaskCollectionToFile(){

        assertDoesNotThrow(() -> taskManager.deleteAllTasks());
        }

    @Test
    void writeEpicWithoutSubTasksWithoutHistoryToFile(){
        EpicTask epicTask1 = new EpicTask("Эпик 1 ", "Описание Эпик 1");
        EpicTask epicTask2 = new EpicTask("Эпик 2 ", "Описание Эпик 2");

        assertDoesNotThrow(() -> {
            taskManager.createNewEpicTask(epicTask1);//ID 2
            taskManager.createNewEpicTask(epicTask2);//ID 3

        });

    }

    @Test
    void writeTasksWithHistoryToFile(){
        EpicTask epicTask1 = new EpicTask("Эпик 1", "Описание Эпик 1");
        EpicTask epicTask2 = new EpicTask("Эпик 2", "Описание Эпик 2");
        Task newTask = new Task("Задача 1 ", "Описание Задачи 1", TaskStatus.NEW);
        assertDoesNotThrow(() -> {
            EpicTask epic = taskManager.createNewEpicTask(epicTask1);
            taskManager.createNewEpicTask(epicTask2);
            Task task = taskManager.createNewTask(newTask);
            taskManager.getTaskById(task.getTaskId());
            taskManager.getEpicTaskById(epic.getTaskId());
            taskManager.getEpicTaskById(epic.getTaskId());

        });

    }

}