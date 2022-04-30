package ru.yandex.practicum.task.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.task.constants.TaskStatus;
import ru.yandex.practicum.task.exception.TimeIsBusyException;
import ru.yandex.practicum.task.logic.TaskManager;
import ru.yandex.practicum.task.models.EpicTask;
import ru.yandex.practicum.task.models.SubTask;
import ru.yandex.practicum.task.models.Task;
import java.time.LocalDateTime;
import java.util.List;
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
        EpicTask epic = taskManager.createNewEpicTask(epicTask);
        SubTask subTask = new SubTask("Подзадача 1","Описание подзадачи 1", TaskStatus.NEW, epic.getTaskId());
        subTask.setStartTime(LocalDateTime.parse("25.05.2022 10:00",timeFormatter));
        subTask.setDuration(30);
        taskManager.createNewSubTask(subTask);
        subTask = new SubTask("Подзадача 2","Описание подзадачи 2", TaskStatus.NEW, epic.getTaskId());
        subTask.setStartTime(LocalDateTime.parse("23.05.2022 12:00",timeFormatter));
        subTask.setDuration(20);
        taskManager.createNewSubTask(subTask);
        subTask = new SubTask("Подзадача 3","Описание подзадачи 2", TaskStatus.NEW, epic.getTaskId());
        subTask.setStartTime(LocalDateTime.parse("25.04.2023 03:00",timeFormatter));
        subTask.setDuration(45);
        taskManager.createNewSubTask(subTask);
        Task task = new Task("Задача 1","Описание задачи 1", TaskStatus.NEW);
        task.setStartTime(LocalDateTime.parse("01.01.2023 13:14",timeFormatter));
        task.setDuration(55);
        taskManager.createNewTask(task);
        assertEquals(4, taskManager.getPrioritizedTasks().size());
    }

    @Test
    void getPrioritizedTasksShouldReturnSubTask2WithEarliestDate() {

        EpicTask epicTask = new EpicTask("Эпик для подзадачи ", "Описание Эпика для подзадачи");
        EpicTask epic= taskManager.createNewEpicTask(epicTask);
        SubTask subTask = new SubTask("Подзадача 1","Описание подзадачи 1", TaskStatus.NEW, epic.getTaskId());
        subTask.setStartTime(LocalDateTime.parse("25.05.2022 10:00",timeFormatter));
        subTask.setDuration(30);
        taskManager.createNewSubTask(subTask);
        subTask = new SubTask("Подзадача 2","Описание подзадачи 2", TaskStatus.NEW, epic.getTaskId());
        subTask.setStartTime(LocalDateTime.parse("23.05.2022 12:00",timeFormatter));
        subTask.setDuration(20);
        SubTask result = taskManager.createNewSubTask(subTask);
        subTask = new SubTask("Подзадача 3","Описание подзадачи 2", TaskStatus.NEW, epic.getTaskId());
        subTask.setStartTime(LocalDateTime.parse("25.04.2023 03:00",timeFormatter));
        subTask.setDuration(45);
        taskManager.createNewSubTask(subTask);
        Task task = new Task("Задача 1","Описание задачи 1", TaskStatus.NEW);
        task.setStartTime(LocalDateTime.parse("01.01.2023 13:14",timeFormatter));
        task.setDuration(55);
        taskManager.createNewTask(task);

        assertEquals(result.getTaskId(), ((Task)taskManager.getPrioritizedTasks().toArray()[0]).getTaskId());
    }


    @Test
    void checkEpicStartEndDates() {

        EpicTask epicTask = new EpicTask("Эпик для подзадачи ", "Описание Эпика для подзадачи");
        EpicTask epic = taskManager.createNewEpicTask(epicTask);
        SubTask subTask = new SubTask("Подзадача 1","Описание подзадачи 1", TaskStatus.NEW, epic.getTaskId());
        subTask.setStartTime(LocalDateTime.parse("27.05.2022 12:00",timeFormatter));
        subTask.setDuration(30);
        taskManager.createNewSubTask(subTask);
        subTask = new SubTask("Подзадача 2","Описание подзадачи 2", TaskStatus.NEW, epic.getTaskId());
        subTask.setStartTime(LocalDateTime.parse("01.06.2022 03:00",timeFormatter));
        subTask.setDuration(20);
        taskManager.createNewSubTask(subTask);
        subTask = new SubTask("Подзадача 3","Описание подзадачи 2", TaskStatus.NEW, epic.getTaskId());
        subTask.setStartTime(LocalDateTime.parse("25.05.2022 10:00",timeFormatter));
        subTask.setDuration(45);
        taskManager.createNewSubTask(subTask);

        assertEquals(LocalDateTime.parse("2022-05-25T10:00"), taskManager.getEpicTaskByID(epic.getTaskId()).getStartTime());
        assertEquals(LocalDateTime.parse("01.06.2022 03:20",timeFormatter), taskManager.getEpicTaskByID(epic.getTaskId()).getEndTime());
    }

    @Test
    void checkEpicDurationShouldBe95min() {

        EpicTask epicTask = new EpicTask("Эпик для подзадачи ", "Описание Эпика для подзадачи");
        EpicTask epic = taskManager.createNewEpicTask(epicTask);
        SubTask subTask = new SubTask("Подзадача 1","Описание подзадачи 1", TaskStatus.NEW, epicTask.getTaskId());
        subTask.setStartTime(LocalDateTime.parse("27.05.2022 12:00",timeFormatter));
        subTask.setDuration(30);
        taskManager.createNewSubTask(subTask);
        subTask = new SubTask("Подзадача 2","Описание подзадачи 2", TaskStatus.NEW, epicTask.getTaskId());
        subTask.setStartTime(LocalDateTime.parse("01.06.2022 03:00",timeFormatter));
        subTask.setDuration(20);
        taskManager.createNewSubTask(subTask);
        subTask = new SubTask("Подзадача 3","Описание подзадачи 2", TaskStatus.NEW, epicTask.getTaskId());
        subTask.setStartTime(LocalDateTime.parse("25.05.2022 10:00",timeFormatter));
        subTask.setDuration(45);
        taskManager.createNewSubTask(subTask);
        assertEquals(95L, taskManager.getEpicTaskByID(epicTask.getTaskId()).getDuration());

    }

    @Test
    void checkBusySchedulerShouldThrowException() {

        EpicTask newEpic = new EpicTask("Эпик для подзадачи ", "Описание Эпика для подзадачи");
        EpicTask result = taskManager.createNewEpicTask(newEpic);
        SubTask subTask = new SubTask("Подзадача 1","Описание подзадачи 1", TaskStatus.NEW, result.getTaskId());
        subTask.setStartTime(LocalDateTime.parse("27.05.2022 12:00",timeFormatter));
        subTask.setDuration(30);
        taskManager.createNewSubTask(subTask);
        subTask = new SubTask("Подзадача 2","Описание подзадачи 2", TaskStatus.NEW, result.getTaskId());
        subTask.setStartTime(LocalDateTime.parse("01.06.2022 03:00",timeFormatter));
        subTask.setDuration(20);
        taskManager.createNewSubTask(subTask);
        subTask = new SubTask("Подзадача 3","Описание подзадачи 2", TaskStatus.NEW, result.getTaskId());
        subTask.setStartTime(LocalDateTime.parse("25.05.2022 10:03",timeFormatter));
        subTask.setDuration(30);
        taskManager.createNewSubTask(subTask);
        Task task = new Task("Задача 1","Описание задачи 1", TaskStatus.NEW);
        task.setStartTime(LocalDateTime.parse("25.05.2022 10:32",timeFormatter));
        task.setDuration(20);
        TimeIsBusyException ex = assertThrows(TimeIsBusyException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                taskManager.createNewTask(task);
            }
        });
        String exceptionMessage ="Невозможно сохранить задачу с началом 25.05.2022 10:32" +
                " и продолжительностью 20 мин. \n" +
                "Интервал с 25.05.2022 10:30 уже занят.";
        assertEquals(exceptionMessage, ex.getMessage());
    }

    @Test
    void getAllTasks() {
        taskManager.createNewTask(new Task("Простая задача 1","Описание простой задачи 1", TaskStatus.NEW));
        taskManager.createNewTask(new Task("Простая задача 2","Описание простой задачи 2", TaskStatus.NEW));
        taskManager.createNewTask(new Task("Простая задача 3","Описание простой задачи 3", TaskStatus.NEW));
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
        EpicTask epic1 = taskManager.createNewEpicTask(new EpicTask("Эпик 1","Описание эпика 1"));
        EpicTask epic2 = taskManager.createNewEpicTask(new EpicTask("Эпик 2","Описание эпика 2"));

        SubTask subTask = taskManager.createNewSubTask(new SubTask("Подзадача 1","Описание подзадачи 1", TaskStatus.NEW, epic1.getTaskId()));
        taskManager.createNewSubTask(new SubTask("Подзадача 2","Описание подзадачи 2", TaskStatus.NEW, epic2.getTaskId()));
        taskManager.createNewSubTask(new SubTask("Подзадача 3","Описание подзадачи 3", TaskStatus.NEW, epic2.getTaskId()));
        assertNotNull(taskManager.getAllSubTasks());
        assertEquals(taskManager.getAllSubTasks().size(),3);
        assertEquals(taskManager.getSubTaskByID(subTask.getTaskId()).getParentId(),epic1.getTaskId());

    }

    @Test
    void deleteAllTasks() {

        taskManager.createNewTask(new Task("Простая задача 1","Описание простой задачи 1", TaskStatus.NEW));
        taskManager.createNewTask(new Task("Простая задача 2","Описание простой задачи 2", TaskStatus.NEW));
        taskManager.deleteAllTasks();
        assertEquals(taskManager.getAllTasks().size(),0);
    }

    @Test
    void deleteAllSubTasks() {

        EpicTask epic1 = taskManager.createNewEpicTask(new EpicTask("Эпик 1","Описание эпика 1"));
        EpicTask epic2 = taskManager.createNewEpicTask(new EpicTask("Эпик 2","Описание эпика 2"));

        taskManager.createNewSubTask(new SubTask("Подзадача 1","Описание подзадачи 1", TaskStatus.NEW, epic1.getTaskId()));
        taskManager.createNewSubTask(new SubTask("Подзадача 2","Описание подзадачи 2", TaskStatus.NEW, epic2.getTaskId()));
        taskManager.createNewSubTask(new SubTask("Подзадача 3","Описание подзадачи 3", TaskStatus.NEW, epic2.getTaskId()));
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

        Task task1 = taskManager.createNewTask(new Task("Простая задача 1","Описание простой задачи 1", TaskStatus.NEW));
        Task task2 = taskManager.createNewTask(new Task("Простая задача 2","Описание простой задачи 2", TaskStatus.NEW));
        taskManager.deleteTaskByID(task2.getTaskId());
        assertEquals(taskManager.getAllTasks().size(),1);
        assertNull(taskManager.getTaskByID(task2.getTaskId()));
    }

    @Test
    void deleteEpicByID() {

        EpicTask epic1 = taskManager.createNewEpicTask(new EpicTask("Эпик 1","Описание эпика 1"));
        EpicTask epic2 = taskManager.createNewEpicTask(new EpicTask("Эпик 2","Описание эпика 2"));
        SubTask subTask= taskManager.createNewSubTask(new SubTask("Подзадача для Эпика","Описание подзадачи для эпика", TaskStatus.NEW, epic2.getTaskId()));
        taskManager.deleteEpicByID(epic2.getTaskId());
        assertEquals(taskManager.getAllEpics().size(),1);
        assertNull(taskManager.getEpicTaskByID(epic2.getTaskId()));
        assertNull(taskManager.getSubTaskByID(subTask.getTaskId()));
    }

    @Test
    void deleteSubTaskByID() {

        EpicTask epic1 = taskManager.createNewEpicTask(new EpicTask("Эпик 1","Описание эпика 1"));
        EpicTask epic2 = taskManager.createNewEpicTask(new EpicTask("Эпик 2","Описание эпика 2"));
        SubTask task1 = taskManager.createNewSubTask(new SubTask("Подзадача 1","Описание подзадачи 1", TaskStatus.NEW, epic1.getTaskId()));
        taskManager.createNewSubTask(new SubTask("Подзадача 2","Описание подзадачи 2", TaskStatus.NEW, epic2.getTaskId()));
        taskManager.createNewSubTask(new SubTask("Подзадача 3","Описание подзадачи 3", TaskStatus.NEW, epic2.getTaskId()));
        taskManager.deleteSubTaskByID(task1.getTaskId());
        assertEquals(taskManager.getAllSubTasks().size(),2);
        assertNull(taskManager.getSubTaskByID(task1.getTaskId()));


    }

    @Test
    void getSubTaskByID() {
        EpicTask epic1 = taskManager.createNewEpicTask(new EpicTask("Эпик 1","Описание эпика 1"));
        EpicTask epic2 = taskManager.createNewEpicTask(new EpicTask("Эпик 2","Описание эпика 2"));
        SubTask taskId1 = taskManager.createNewSubTask(new SubTask("Подзадача 1","Описание подзадачи 1", TaskStatus.NEW, epic1.getTaskId()));
        SubTask subTask = new SubTask("Подзадача 2","Описание подзадачи 2", TaskStatus.NEW, epic2.getTaskId());
        SubTask task2 = taskManager.createNewSubTask(subTask);
        SubTask gotTask = taskManager.getSubTaskByID(task2.getTaskId());
        assertNotNull(gotTask);
        assertEquals(gotTask, subTask);

    }

    @Test
    void getTaskByID() {
        Task task = new Task("Задача 1","Описание задачи 1", TaskStatus.NEW);
        Task task1 = taskManager.createNewTask(task);
        taskManager.createNewTask(new Task("Задача 2","Описание задачи 2", TaskStatus.NEW));
        Task gotTask = taskManager.getTaskByID(task1.getTaskId());
        assertNotNull(gotTask);
        assertEquals(gotTask, task);
    }

    @Test
    void getEpicTaskByID() {
        EpicTask epic = new EpicTask("Эпик 1","Описание эпика 1");
        EpicTask epic1 = taskManager.createNewEpicTask(epic);
        EpicTask epic2 = taskManager.createNewEpicTask(new EpicTask("Эпик 2","Описание эпика 2"));
        EpicTask gotTask = taskManager.getEpicTaskByID(epic1.getTaskId());
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
        Task task1 = taskManager.createNewTask(new Task("Простая задача 1","Описание простой задачи 1", TaskStatus.NEW));
        Task oldTask = taskManager.getTaskByID(task1.getTaskId());
        oldTask.setStartTime(LocalDateTime.parse("28.04.2022 12:00",timeFormatter));
        oldTask.setDuration(20);
        Task newTask = new Task ("обновленное название 1", "обновленное описание 1", TaskStatus.NEW);
        newTask.setTaskId(task1.getTaskId());
        newTask.setStatus(TaskStatus.IN_PROCESS);
        newTask.setStartTime(LocalDateTime.parse("29.07.2022 12:25",timeFormatter));
        newTask.setDuration(30);
        taskManager.updateTask(newTask);
        Task updatedTask = taskManager.getTaskByID(task1.getTaskId());
        assertEquals("обновленное название 1", updatedTask.getName());
        assertEquals("обновленное описание 1", updatedTask.getDescription());
        assertEquals(TaskStatus.IN_PROCESS, updatedTask.getStatus());
        assertEquals(30, updatedTask.getDuration());
        assertEquals(LocalDateTime.parse("29.07.2022 12:25",timeFormatter), updatedTask.getStartTime());
    }

    @DisplayName("GIVEN instance of SubTask from TaskManager received by TaskID"+
            "WHEN create new SubTask with sameID and different attributes " +
            "(name, description, status, startTime, duration), call updateSubTask(task) " +
            "and receive SubTask from manager by same TaskID " +
            "THEN getters return updated values")
    @Test
    void updateSubTask_CheckAttributesGeneral() {
        EpicTask epic1 = taskManager.createNewEpicTask(new EpicTask("Эпик 1","Описание эпика 1"));
        SubTask task1 = taskManager.createNewSubTask(new SubTask("Подзадача 1","Описание подзадачи 1",TaskStatus.NEW, epic1.getTaskId()));
        SubTask oldTask = taskManager.getSubTaskByID(task1.getTaskId());
        oldTask.setStartTime(LocalDateTime.parse("28.04.2022 12:00",timeFormatter));
        oldTask.setDuration(20);
        SubTask newTask = new SubTask ("обновленное название 1", "обновленное описание 1", TaskStatus.NEW, epic1.getTaskId());
        newTask.setTaskId(task1.getTaskId());
        newTask.setStatus(TaskStatus.IN_PROCESS);
        newTask.setStartTime(LocalDateTime.parse("29.04.2022 12:25",timeFormatter));
        newTask.setDuration(30);
        taskManager.updateSubTask(newTask);
        SubTask updatedTask = taskManager.getSubTaskByID(task1.getTaskId());
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
        EpicTask task1 = taskManager.createNewEpicTask(new EpicTask("Эпик 1","Описание эпика 1"));
        Task oldTask = taskManager.getEpicTaskByID(task1.getTaskId());
        EpicTask newTask = new EpicTask ("обновленное название 1", "обновленное описание 1");
        newTask.setTaskId(task1.getTaskId());
        taskManager.updateEpicTask(newTask);
        Task updatedTask = taskManager.getEpicTaskByID(task1.getTaskId());
        assertEquals("обновленное название 1", updatedTask.getName());
        assertEquals("обновленное описание 1", updatedTask.getDescription());

    }

    @DisplayName("GIVEN instance of EpicTask from TaskManager received by TaskID"+
            "WHEN try to call setStatus(), setStartDate(), setDuration() " +
            "THEN getters return updated values of name and description")

    @Test
    void updateEpicTask_CheckAttributesCantBeChanged (){
        EpicTask task1 = taskManager.createNewEpicTask(new EpicTask("Эпик 1","Описание эпика 1"));
        Task oldTask = taskManager.getEpicTaskByID(task1.getTaskId());
        EpicTask newTask = new EpicTask ("Эпик 1", "Описание эпика 1");
        newTask.setTaskId(task1.getTaskId());
        newTask.setStartTime(LocalDateTime.parse("29.04.2022 12:25",timeFormatter));
        newTask.setDuration(20);
        newTask.setStatus(TaskStatus.IN_PROCESS);
        taskManager.updateEpicTask(newTask);
        Task updatedTask = taskManager.getEpicTaskByID(task1.getTaskId());
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
        EpicTask epic = taskManager.createNewEpicTask(new EpicTask ("Эпик 1", "Описание эпика 1"));

        //a. Пустой список подзадач.
        assertEquals(taskManager.getEpicTaskByID(epic.getTaskId()).getStatus(),TaskStatus.NEW);

        //b. Все подзадачи со статусом NEW.
        SubTask sub1 = taskManager.createNewSubTask(new SubTask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, epic.getTaskId()));
        SubTask sub2 = taskManager.createNewSubTask(new SubTask("Подзадача 2", "Описание подзадачи 2", TaskStatus.NEW, epic.getTaskId()));
        assertEquals(TaskStatus.NEW, taskManager.getEpicTaskByID(epic.getTaskId()).getStatus());

        //c. Все подзадачи со статусом DONE.
        SubTask subTask = taskManager.getSubTaskByID(sub1.getTaskId());
        subTask.setStatus(TaskStatus.DONE);
        taskManager.updateSubTask(subTask);
        subTask = taskManager.getSubTaskByID(sub2.getTaskId());
        subTask.setStatus(TaskStatus.DONE);
        taskManager.updateSubTask(subTask);

        assertEquals(TaskStatus.DONE, taskManager.getEpicTaskByID(epic.getTaskId()).getStatus());

        //Подзадачи со статусами NEW и DONE.
        subTask.setStatus(TaskStatus.NEW);
        taskManager.updateSubTask(subTask);
        assertEquals(TaskStatus.IN_PROCESS, taskManager.getEpicTaskByID(epic.getTaskId()).getStatus());

        //Подзадачи со статусом IN_PROGRESS.
        subTask = taskManager.getSubTaskByID(sub1.getTaskId());
        subTask.setStatus(TaskStatus.IN_PROCESS);
        taskManager.updateSubTask(subTask);
        subTask = taskManager.getSubTaskByID(sub2.getTaskId());
        subTask.setStatus(TaskStatus.IN_PROCESS);
        taskManager.updateSubTask(subTask);
        assertEquals(TaskStatus.IN_PROCESS, taskManager.getEpicTaskByID(epic.getTaskId()).getStatus());

    }

    @Test
    void createNewTaskTest() {
        Task task = new Task("Простая задача 1","Описание простой задачи 1", TaskStatus.NEW);
        task.setStartTime(LocalDateTime.parse("26.04.2022 11:05",timeFormatter));
        task.setDuration(15);
        Task result = taskManager.createNewTask(task);
        final Task savedTask = taskManager.getTaskByID(task.getTaskId());
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
        final List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    void checkIfEpicExistsForSubTask(){
        EpicTask task = taskManager.createNewEpicTask(new EpicTask ("Эпик 1", "Описание эпика 1"));
        SubTask sub1 = taskManager.createNewSubTask(new SubTask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, task.getTaskId()));
        taskManager.createNewSubTask(new SubTask("Подзадача 2", "Описание подзадачи 2", TaskStatus.NEW, task.getTaskId()));
        assertEquals(taskManager.getSubTaskByID(sub1.getTaskId()).getParentId(),sub1.getParentId());
    }

    @Test
    void createNewEpicTest() {
        EpicTask epicTask = new EpicTask("Эпик 1 ", "Описание Эпик 1");
        EpicTask result = taskManager.createNewEpicTask(epicTask);
        assertNotNull(result, "Эпик не найден.");
        assertEquals(epicTask, result, "Эпики не совпадают.");
        final List<EpicTask> tasks = taskManager.getAllEpics();
        assertNotNull(tasks, "Эпики на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество эпиков.");
        assertEquals(epicTask, result, "Задачи не совпадают.");
    }

    @Test
    void createNewSubTaskTest() {

        EpicTask epicTask = new EpicTask("Эпик для подзадачи ", "Описание Эпика для подзадачи");
        EpicTask epic= taskManager.createNewEpicTask(epicTask);
        SubTask task = new SubTask("Подзадача 1","Описание подзадачи 1", TaskStatus.NEW, epic.getTaskId());
        task.setStartTime(LocalDateTime.parse("27.04.2022 09:25",timeFormatter));
        task.setDuration(20);
        SubTask task1 = taskManager.createNewSubTask(task);
        final SubTask savedTask = taskManager.getSubTaskByID(task.getTaskId());
        assertNotNull(savedTask, "Подзадача не найдена.");
        assertEquals(task, savedTask, "Подзадачи не совпадают.");
        final List<SubTask> tasks = taskManager.getAllSubTasks();
        assertNotNull(tasks, "Подзадачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество подзадач.");
        assertEquals(task, task1, "Поззадачи не совпадают");
    }

    @Test
    void getEpicSubTasks() {

        EpicTask epicTask = new EpicTask("Эпик для подзадачи ", "Описание Эпика для подзадачи");
        EpicTask epic = taskManager.createNewEpicTask(epicTask);
        SubTask task = new SubTask("Подзадача 1","Описание подзадачи 1", TaskStatus.NEW, epicTask.getTaskId());
        taskManager.createNewSubTask(task);
        task = new SubTask("Подзадача 2","Описание подзадачи 2", TaskStatus.NEW, epic.getTaskId());
        taskManager.createNewSubTask(task);
        task = new SubTask("Подзадача 3","Описание подзадачи 2", TaskStatus.NEW, epicTask.getTaskId());
        taskManager.createNewSubTask(task);
        assertEquals(3, epicTask.getSubTasksIDs().size());
    }
}