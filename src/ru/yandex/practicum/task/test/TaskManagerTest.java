package ru.yandex.practicum.task.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.task.constants.TaskStatus;
import ru.yandex.practicum.task.logic.TaskManager;
import ru.yandex.practicum.task.models.EpicTask;
import ru.yandex.practicum.task.models.SubTask;
import ru.yandex.practicum.task.models.Task;
import java.time.LocalDateTime;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.task.logic.Managers.timeFormatter;

abstract class TaskManagerTest<T extends TaskManager> {

    static TaskManager taskManager;


    @Test
    void getTaskWithWrongID(){
        assertNull(taskManager.getTaskByID(-2L));
    }

    @Test
    void getEpicWithWrongID(){
        assertNull(taskManager.getEpicTaskByID(-2L));
    }

    @Test
    void getSubTaskWithWrongID(){
        assertNull(taskManager.getSubTaskByID(-2L));
    }


    @Test
    void getPrioritizedTasksShouldReturnSize4NotEpicShouldReturn() {

        EpicTask epicTask = new EpicTask("Эпик для подзадачи ", "Описание Эпика для подзадачи");
        long epicID = taskManager.createNewEpicTask(epicTask);
        SubTask subTask = new SubTask("Подзадача 1","Описание подзадачи 1", epicID);
        subTask.setStartTime(LocalDateTime.parse("25.05.2022 10:00",timeFormatter));
        subTask.setDuration(30);
        taskManager.createNewSubTask(subTask);
        subTask = new SubTask("Подзадача 2","Описание подзадачи 2", epicID);
        subTask.setStartTime(LocalDateTime.parse("23.05.2022 12:00",timeFormatter));
        subTask.setDuration(20);
        taskManager.createNewSubTask(subTask);
        subTask = new SubTask("Подзадача 3","Описание подзадачи 2", epicID);
        subTask.setStartTime(LocalDateTime.parse("25.04.2023 03:00",timeFormatter));
        subTask.setDuration(45);
        taskManager.createNewSubTask(subTask);
        Task task = new Task("Задача 1","Описание задачи 1");
        task.setStartTime(LocalDateTime.parse("01.01.2023 13:14",timeFormatter));
        task.setDuration(55);
        taskManager.createNewTask(task);
        assertEquals(4, taskManager.getPrioritizedTasks().size());
    }

    @Test
    void getPrioritizedTasksShouldReturnSubTask2WithEarliestDate() {

        EpicTask epicTask = new EpicTask("Эпик для подзадачи ", "Описание Эпика для подзадачи");
        long epicID = taskManager.createNewEpicTask(epicTask);
        SubTask subTask = new SubTask("Подзадача 1","Описание подзадачи 1", epicID);
        subTask.setStartTime(LocalDateTime.parse("25.05.2022 10:00",timeFormatter));
        subTask.setDuration(30);
        taskManager.createNewSubTask(subTask);
        subTask = new SubTask("Подзадача 2","Описание подзадачи 2", epicID);
        subTask.setStartTime(LocalDateTime.parse("23.05.2022 12:00",timeFormatter));
        subTask.setDuration(20);
        long subTaskID = taskManager.createNewSubTask(subTask);
        subTask = new SubTask("Подзадача 3","Описание подзадачи 2", epicID);
        subTask.setStartTime(LocalDateTime.parse("25.04.2023 03:00",timeFormatter));
        subTask.setDuration(45);
        taskManager.createNewSubTask(subTask);
        Task task = new Task("Задача 1","Описание задачи 1");
        task.setStartTime(LocalDateTime.parse("01.01.2023 13:14",timeFormatter));
        task.setDuration(55);
        taskManager.createNewTask(task);

        assertEquals(subTaskID, ((Task)taskManager.getPrioritizedTasks().toArray()[0]).getID());
    }


    @Test
    void checkEpicStartEndDates() {

        EpicTask epicTask = new EpicTask("Эпик для подзадачи ", "Описание Эпика для подзадачи");
        long epicID = taskManager.createNewEpicTask(epicTask);
        SubTask subTask = new SubTask("Подзадача 1","Описание подзадачи 1", epicID);
        subTask.setStartTime(LocalDateTime.parse("27.05.2022 12:00",timeFormatter));
        subTask.setDuration(30);
        taskManager.createNewSubTask(subTask);
        subTask = new SubTask("Подзадача 2","Описание подзадачи 2", epicID);
        subTask.setStartTime(LocalDateTime.parse("01.06.2022 03:00",timeFormatter));
        subTask.setDuration(20);
        taskManager.createNewSubTask(subTask);
        subTask = new SubTask("Подзадача 3","Описание подзадачи 2", epicID);
        subTask.setStartTime(LocalDateTime.parse("25.05.2022 10:00",timeFormatter));
        subTask.setDuration(45);
        taskManager.createNewSubTask(subTask);

        assertEquals(LocalDateTime.parse("2022-05-25T10:00"), taskManager.getEpicTaskByID(epicID).getStartTime());
        assertEquals(LocalDateTime.parse("01.06.2022 03:20",timeFormatter), taskManager.getEpicTaskByID(epicID).getEndTime());
    }

    @Test
    void checkEpicDurationShouldBe95min() {

        EpicTask epicTask = new EpicTask("Эпик для подзадачи ", "Описание Эпика для подзадачи");
        long epicID = taskManager.createNewEpicTask(epicTask);
        SubTask subTask = new SubTask("Подзадача 1","Описание подзадачи 1", epicID);
        subTask.setStartTime(LocalDateTime.parse("27.05.2022 12:00",timeFormatter));
        subTask.setDuration(30);
        taskManager.createNewSubTask(subTask);
        subTask = new SubTask("Подзадача 2","Описание подзадачи 2", epicID);
        subTask.setStartTime(LocalDateTime.parse("01.06.2022 03:00",timeFormatter));
        subTask.setDuration(20);
        taskManager.createNewSubTask(subTask);
        subTask = new SubTask("Подзадача 3","Описание подзадачи 2", epicID);
        subTask.setStartTime(LocalDateTime.parse("25.05.2022 10:00",timeFormatter));
        subTask.setDuration(45);
        taskManager.createNewSubTask(subTask);

        assertEquals(95L, taskManager.getEpicTaskByID(epicID).getDuration());

    }

    @Test
    void checkBusySchedulerShouldReturnNegativeTaskID() {

        EpicTask epicTask = new EpicTask("Эпик для подзадачи ", "Описание Эпика для подзадачи");
        long epicID = taskManager.createNewEpicTask(epicTask);
        SubTask subTask = new SubTask("Подзадача 1","Описание подзадачи 1", epicID);
        subTask.setStartTime(LocalDateTime.parse("27.05.2022 12:00",timeFormatter));
        subTask.setDuration(30);
        taskManager.createNewSubTask(subTask);
        subTask = new SubTask("Подзадача 2","Описание подзадачи 2", epicID);
        subTask.setStartTime(LocalDateTime.parse("01.06.2022 03:00",timeFormatter));
        subTask.setDuration(20);
        taskManager.createNewSubTask(subTask);
        subTask = new SubTask("Подзадача 3","Описание подзадачи 2", epicID);
        subTask.setStartTime(LocalDateTime.parse("25.05.2022 10:03",timeFormatter));
        subTask.setDuration(30);
        taskManager.createNewSubTask(subTask);
        Task task = new Task("Задача 1","Описание задачи 1");
        task.setStartTime(LocalDateTime.parse("25.05.2022 10:32",timeFormatter));
        task.setDuration(20);
        assertEquals(-1L, taskManager.createNewTask(task));
    }

    @Test
    void getAllTasks() {
        taskManager.createNewTask(new Task("Простая задача 1","Описание простой задачи 1"));
        taskManager.createNewTask(new Task("Простая задача 2","Описание простой задачи 2"));
        taskManager.createNewTask(new Task("Простая задача 3","Описание простой задачи 3"));
        assertNotNull(taskManager.getAllTasks());
        assertEquals(taskManager.getAllTasks().size(),3);
    }

    @Test
    void getAllEpics() {
        taskManager.createNewEpicTask(new EpicTask("Эпик 1","Описание эпика 1"));
        taskManager.createNewEpicTask(new EpicTask("Эпик 2","Описание эпика 2"));
        taskManager.createNewEpicTask(new EpicTask("Эпик 3","Описание эпика 3"));
        assertNotNull(taskManager.getAllEpics());
        assertEquals(taskManager.getAllEpics().size(),3);
    }

    @Test
    void getAllSubTasks() {
        long epicId1 = taskManager.createNewEpicTask(new EpicTask("Эпик 1","Описание эпика 1"));
        long epicId2 = taskManager.createNewEpicTask(new EpicTask("Эпик 2","Описание эпика 2"));

        long taskId1 = taskManager.createNewSubTask(new SubTask("Подзадача 1","Описание подзадачи 1", epicId1));
        taskManager.createNewSubTask(new SubTask("Подзадача 2","Описание подзадачи 2", epicId2));
        taskManager.createNewSubTask(new SubTask("Подзадача 3","Описание подзадачи 3", epicId2));
        assertNotNull(taskManager.getAllSubTasks());
        assertEquals(taskManager.getAllSubTasks().size(),3);
        assertEquals(taskManager.getSubTaskByID(taskId1).getParentId(),epicId1);

    }

    @Test
    void deleteAllTasks() {

        taskManager.createNewTask(new Task("Простая задача 1","Описание простой задачи 1"));
        taskManager.createNewTask(new Task("Простая задача 2","Описание простой задачи 2"));
        taskManager.deleteAllTasks();
        assertEquals(taskManager.getAllTasks().size(),0);
    }

    @Test
    void deleteAllSubTasks() {

        long epicId1 = taskManager.createNewEpicTask(new EpicTask("Эпик 1","Описание эпика 1"));
        long epicId2 = taskManager.createNewEpicTask(new EpicTask("Эпик 2","Описание эпика 2"));

        taskManager.createNewSubTask(new SubTask("Подзадача 1","Описание подзадачи 1", epicId1));
        taskManager.createNewSubTask(new SubTask("Подзадача 2","Описание подзадачи 2", epicId2));
        taskManager.createNewSubTask(new SubTask("Подзадача 3","Описание подзадачи 3", epicId2));
        taskManager.deleteAllSubTasks();
        assertEquals(taskManager.getAllSubTasks().size(),0);
    }

    @Test
    void deleteAllEpics() {
        taskManager.createNewEpicTask(new EpicTask("Эпик 1","Описание эпика 1"));
        taskManager.createNewEpicTask(new EpicTask("Эпик 2","Описание эпика 2"));
        taskManager.deleteAllEpics();
        assertEquals(taskManager.getAllEpics().size(),0);


    }

    @Test
    void deleteTaskByID() {

        long taskID1 = taskManager.createNewTask(new Task("Простая задача 1","Описание простой задачи 1"));
        long taskID2 = taskManager.createNewTask(new Task("Простая задача 2","Описание простой задачи 2"));
        taskManager.deleteTaskByID(taskID2);
        assertEquals(taskManager.getAllTasks().size(),1);
        assertNull(taskManager.getTaskByID(taskID2));
    }

    @Test
    void deleteEpicByID() {

        long epicID1 = taskManager.createNewEpicTask(new EpicTask("Эпик 1","Описание эпика 1"));
        long epicID2 = taskManager.createNewEpicTask(new EpicTask("Эпик 2","Описание эпика 2"));
        long subTaskID = taskManager.createNewSubTask(new SubTask("Подзадача для Эпика","Описание подзадачи для эпика", epicID2));
        taskManager.deleteEpicByID(epicID2);
        assertEquals(taskManager.getAllEpics().size(),1);
        assertNull(taskManager.getEpicTaskByID(epicID2));
        assertNull(taskManager.getSubTaskByID(subTaskID));
    }

    @Test
    void deleteSubTaskByID() {

        long epicId1 = taskManager.createNewEpicTask(new EpicTask("Эпик 1","Описание эпика 1"));
        long epicId2 = taskManager.createNewEpicTask(new EpicTask("Эпик 2","Описание эпика 2"));
        long taskId1 = taskManager.createNewSubTask(new SubTask("Подзадача 1","Описание подзадачи 1", epicId1));
        taskManager.createNewSubTask(new SubTask("Подзадача 2","Описание подзадачи 2", epicId2));
        taskManager.createNewSubTask(new SubTask("Подзадача 3","Описание подзадачи 3", epicId2));
        taskManager.deleteSubTaskByID(taskId1);
        assertEquals(taskManager.getAllSubTasks().size(),2);
        assertNull(taskManager.getSubTaskByID(taskId1));


    }

    @Test
    void getSubTaskByID() {
        long epicId1 = taskManager.createNewEpicTask(new EpicTask("Эпик 1","Описание эпика 1"));
        long epicId2 = taskManager.createNewEpicTask(new EpicTask("Эпик 2","Описание эпика 2"));
        long taskId1 = taskManager.createNewSubTask(new SubTask("Подзадача 1","Описание подзадачи 1", epicId1));
        SubTask subTask = new SubTask("Подзадача 2","Описание подзадачи 2", epicId2);
        long taskID2 = taskManager.createNewSubTask(subTask);
        SubTask gotTask = taskManager.getSubTaskByID(taskID2);
        assertNotNull(gotTask);
        assertEquals(gotTask, subTask);

    }

    @Test
    void getTaskByID() {
        Task task = new Task("Задача 1","Описание задачи 1");
        long epicId1 = taskManager.createNewTask(task);
        long epicId2 = taskManager.createNewTask(new Task("Задача 2","Описание задачи 2"));
        Task gotTask = taskManager.getTaskByID(epicId1);
        assertNotNull(gotTask);
        assertEquals(gotTask, task);
    }

    @Test
    void getEpicTaskByID() {
        EpicTask epic = new EpicTask("Эпик 1","Описание эпика 1");
        long epicId1 = taskManager.createNewEpicTask(epic);
        long epicId2 = taskManager.createNewEpicTask(new EpicTask("Эпик 2","Описание эпика 2"));
        EpicTask gotTask = taskManager.getEpicTaskByID(epicId1);
        assertNotNull(gotTask);
        assertEquals(gotTask, epic);
    }
    @DisplayName("GIVEN instance of Task from TaskManager received by TaskID"+
                "WHEN create new Task with sameID and different attributes " +
                "(name, description, status, startTime, duration), call updateTask(task) " +
                "and receive Task from manager by same TaskID " +
            "THEN getters return updated values")
    @Test
    void updateTask_CheckAttributesGeneral() {
        long taskID1 = taskManager.createNewTask(new Task("Простая задача 1","Описание простой задачи 1"));
        Task oldTask = taskManager.getTaskByID(taskID1);
        oldTask.setStartTime(LocalDateTime.parse("28.04.2022 12:00",timeFormatter));
        oldTask.setDuration(20);
        Task newTask = new Task ("обновленное название 1", "обновленное описание 1");
        newTask.setID(taskID1);
        newTask.setStatus(TaskStatus.IN_PROCESS);
        newTask.setStartTime(LocalDateTime.parse("29.04.2022 12:25",timeFormatter));
        newTask.setDuration(30);
        taskManager.updateTask(newTask);
        Task updatedTask = taskManager.getTaskByID(taskID1);
        assertEquals("обновленное название 1", updatedTask.getName());
        assertEquals("обновленное описание 1", updatedTask.getDescription());
        assertEquals(TaskStatus.IN_PROCESS, updatedTask.getStatus());
        assertEquals(30, updatedTask.getDuration());
        assertEquals(LocalDateTime.parse("29.04.2022 12:25",timeFormatter), updatedTask.getStartTime());
    }

    @DisplayName("GIVEN instance of SubTask from TaskManager received by TaskID"+
            "WHEN create new SubTask with sameID and different attributes " +
            "(name, description, status, startTime, duration), call updateSubTask(task) " +
            "and receive SubTask from manager by same TaskID " +
            "THEN getters return updated values")
    @Test
    void updateSubTask_CheckAttributesGeneral() {
        long epicId1 = taskManager.createNewEpicTask(new EpicTask("Эпик 1","Описание эпика 1"));
        long taskID1 = taskManager.createNewSubTask(new SubTask("Подзадача 1","Описание подзадачи 1", epicId1));
        SubTask oldTask = taskManager.getSubTaskByID(taskID1);
        oldTask.setStartTime(LocalDateTime.parse("28.04.2022 12:00",timeFormatter));
        oldTask.setDuration(20);
        SubTask newTask = new SubTask ("обновленное название 1", "обновленное описание 1", epicId1);
        newTask.setID(taskID1);
        newTask.setStatus(TaskStatus.IN_PROCESS);
        newTask.setStartTime(LocalDateTime.parse("29.04.2022 12:25",timeFormatter));
        newTask.setDuration(30);
        taskManager.updateSubTask(newTask);
        SubTask updatedTask = taskManager.getSubTaskByID(taskID1);
        assertEquals("обновленное название 1", updatedTask.getName());
        assertEquals("обновленное описание 1", updatedTask.getDescription());
        assertEquals(TaskStatus.IN_PROCESS, updatedTask.getStatus());
        assertEquals(30, updatedTask.getDuration());
        assertEquals(LocalDateTime.parse("29.04.2022 12:25",timeFormatter), updatedTask.getStartTime());
    }

    @DisplayName("GIVEN instance of EpicTask from TaskManager received by TaskID"+
            "WHEN create new EpicTask with sameID and different attributes " +
            "(name, description), call updateTask(task) " +
            "and receive EpicTask from manager by same TaskID " +
            "THEN getters return updated values of name and description")
    @Test
    void updateEpicTask_CheckAttributesGeneral() {
        long taskID1 = taskManager.createNewEpicTask(new EpicTask("Эпик 1","Описание эпика 1"));
        Task oldTask = taskManager.getEpicTaskByID(taskID1);
        EpicTask newTask = new EpicTask ("обновленное название 1", "обновленное описание 1");
        newTask.setID(taskID1);
        taskManager.updateEpicTask(newTask);
        Task updatedTask = taskManager.getEpicTaskByID(taskID1);
        assertEquals("обновленное название 1", updatedTask.getName());
        assertEquals("обновленное описание 1", updatedTask.getDescription());

    }

    @DisplayName("GIVEN instance of EpicTask from TaskManager received by TaskID"+
            "WHEN try to call setStatus(), setStartDate(), setDuration() " +
            "THEN getters return updated values of name and description")

    @Test
    void updateEpicTask_CheckAttributesCantBeChanged (){
        long taskID1 = taskManager.createNewEpicTask(new EpicTask("Эпик 1","Описание эпика 1"));
        Task oldTask = taskManager.getEpicTaskByID(taskID1);
        EpicTask newTask = new EpicTask ("Эпик 1", "Описание эпика 1");
        newTask.setID(taskID1);
        newTask.setStartTime(LocalDateTime.parse("29.04.2022 12:25",timeFormatter));
        newTask.setDuration(20);
        newTask.setStatus(TaskStatus.IN_PROCESS);
        taskManager.updateEpicTask(newTask);
        Task updatedTask = taskManager.getEpicTaskByID(taskID1);
        assertEquals(TaskStatus.NEW, updatedTask.getStatus());
        assertEquals(0, updatedTask.getDuration());
        assertNull(updatedTask.getStartTime());

    }
    /*Для расчёта статуса Epic. Граничные условия:
    a. Пустой список подзадач.
    b. Все подзадачи со статусом NEW.
    c. Все подзадачи со статусом DONE.
    d. Подзадачи со статусами NEW и DONE.
    e. Подзадачи со статусом IN_PROGRESS.*/

    @Test
    void checkEpicStatusBasedOnSubTasksStatuses (){
        long taskID = taskManager.createNewEpicTask(new EpicTask ("Эпик 1", "Описание эпика 1"));

        //a. Пустой список подзадач.
        assertEquals(taskManager.getEpicTaskByID(taskID).getStatus(),TaskStatus.NEW);

        //b. Все подзадачи со статусом NEW.
        long subID1 = taskManager.createNewSubTask(new SubTask("Подзадача 1", "Описание подзадачи 1", taskID));
        long subID2 = taskManager.createNewSubTask(new SubTask("Подзадача 2", "Описание подзадачи 2", taskID));
        assertEquals(TaskStatus.NEW, taskManager.getEpicTaskByID(taskID).getStatus());

        //c. Все подзадачи со статусом DONE.
        SubTask subTask = taskManager.getSubTaskByID(subID1);
        subTask.setStatus(TaskStatus.DONE);
        taskManager.updateSubTask(subTask);
        subTask = taskManager.getSubTaskByID(subID2);
        subTask.setStatus(TaskStatus.DONE);
        taskManager.updateSubTask(subTask);

        assertEquals(TaskStatus.DONE, taskManager.getEpicTaskByID(taskID).getStatus());

        //Подзадачи со статусами NEW и DONE.
        subTask.setStatus(TaskStatus.NEW);
        taskManager.updateSubTask(subTask);
        assertEquals(TaskStatus.IN_PROCESS, taskManager.getEpicTaskByID(taskID).getStatus());

        //Подзадачи со статусом IN_PROGRESS.
        subTask = taskManager.getSubTaskByID(subID1);
        subTask.setStatus(TaskStatus.IN_PROCESS);
        taskManager.updateSubTask(subTask);
        subTask = taskManager.getSubTaskByID(subID2);
        subTask.setStatus(TaskStatus.IN_PROCESS);
        taskManager.updateSubTask(subTask);
        assertEquals(TaskStatus.IN_PROCESS, taskManager.getEpicTaskByID(taskID).getStatus());

    }

    @Test
    void createNewTaskTest() {
        Task task = new Task("Простая задача 1","Описание простой задачи 1");
        task.setStartTime(LocalDateTime.parse("26.04.2022 11:05",timeFormatter));
        task.setDuration(15);
        long taskID = taskManager.createNewTask(task);
        final Task savedTask = taskManager.getTaskByID(taskID);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
        final Map<Long,Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(taskID), "Задачи не совпадают.");
    }

    @Test
    void checkIfEpicExistsForSubTask(){
        long taskID = taskManager.createNewEpicTask(new EpicTask ("Эпик 1", "Описание эпика 1"));
        long subID1 = taskManager.createNewSubTask(new SubTask("Подзадача 1", "Описание подзадачи 1", taskID));
        taskManager.createNewSubTask(new SubTask("Подзадача 2", "Описание подзадачи 2", taskID));
        assertEquals(taskManager.getSubTaskByID(subID1).getParentId(),taskID);
    }

    @Test
    void createNewEpicTest() {
        EpicTask epicTask = new EpicTask("Эпик 1 ", "Описание Эпик 1");
        long taskID = taskManager.createNewEpicTask(epicTask);
        final Task savedEpic = taskManager.getEpicTaskByID(taskID);
        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epicTask, savedEpic, "Эпики не совпадают.");
        final Map<Long,EpicTask> tasks = taskManager.getAllEpics();
        assertNotNull(tasks, "Эпики на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество эпиков.");
        assertEquals(epicTask, tasks.get(taskID), "Задачи не совпадают.");
    }

    @Test
    void createNewSubTaskTest() {

        EpicTask epicTask = new EpicTask("Эпик для подзадачи ", "Описание Эпика для подзадачи");
        long epicID = taskManager.createNewEpicTask(epicTask);
        SubTask task = new SubTask("Подзадача 1","Описание подзадачи 1", epicID);
        task.setStartTime(LocalDateTime.parse("27.04.2022 09:25",timeFormatter));
        task.setDuration(20);
        long taskID = taskManager.createNewSubTask(task);
        final SubTask savedTask = taskManager.getSubTaskByID(taskID);
        assertNotNull(savedTask, "Подзадача не найдена.");
        assertEquals(task, savedTask, "Подзадачи не совпадают.");
        final Map<Long,SubTask> tasks = taskManager.getAllSubTasks();
        assertNotNull(tasks, "Подзадачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество подзадач.");
        assertEquals(task, tasks.get(taskID), "Подзадачи не совпадают.");
    }

    @Test
    void getEpicSubTasks() {

        EpicTask epicTask = new EpicTask("Эпик для подзадачи ", "Описание Эпика для подзадачи");
        long epicID = taskManager.createNewEpicTask(epicTask);
        SubTask task = new SubTask("Подзадача 1","Описание подзадачи 1", epicID);
        taskManager.createNewSubTask(task);
        task = new SubTask("Подзадача 2","Описание подзадачи 2", epicID);
        taskManager.createNewSubTask(task);
        task = new SubTask("Подзадача 3","Описание подзадачи 2", epicID);
        taskManager.createNewSubTask(task);
        assertEquals(3, epicTask.getSubTasksIDsList().size());
    }
}