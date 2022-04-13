import ru.yandex.practicum.task.constants.TaskStatus;
import ru.yandex.practicum.task.logic.HistoryManager;
import ru.yandex.practicum.task.logic.Managers;
import ru.yandex.practicum.task.logic.TaskManager;
import ru.yandex.practicum.task.models.EpicTask;
import ru.yandex.practicum.task.models.SubTask;
import ru.yandex.practicum.task.models.Task;

import java.util.HashMap;
import java.util.List;

public class Main {

public static void main(String[] args) {

    //сценарий работает когда в классе Managers переменная isBacked = false
        TaskManager taskManager = Managers.getDefault();

        HashMap<Long, SubTask> tasks = taskManager.getAllSubTasks();
        
        Task myTask1 = new Task("Простая задача 1","Описание простой задачи 1");
        Task myTask2 = new Task("Простая задача 2","Описание простой задачи 2");
        taskManager.createNewTask(myTask1);//ID 0
        taskManager.createNewTask(myTask2);//ID 1

        EpicTask epicTask1 = new EpicTask("Эпик 1 ", "Описание Эпик 1");
        EpicTask epicTask2 = new EpicTask("Эпик 2 ", "Описание Эпик 2");
        taskManager.createNewEpicTask(epicTask1);//ID 2
        taskManager.createNewEpicTask(epicTask2);//ID 3

        // Создаем Подзадачи для двух Эпиков (ID 2 и 3)
        taskManager.createNewSubTask(new SubTask("Подзадача 1", "Описание Подзадача 1", 2));
        taskManager.createNewSubTask(new SubTask("Подзадача 2", "Описание Подзадача 2", 2));
        taskManager.createNewSubTask(new SubTask("Подзадача 3", "Описание Подзадача 3", 2));

        taskManager.createNewSubTask(new SubTask("Подзадача 4", "Описание Подзадача 4", 3));
        taskManager.createNewSubTask(new SubTask("Подзадача 5", "Описание Подзадача 5", 3));

        //создаем Эпик без Подзадач
        taskManager.createNewEpicTask(new EpicTask("Эпик 3 ", "Описание Эпик 3"));

        //создаем еще три задачи, чтобы общее число было больше 10

        taskManager.createNewTask(new Task("Простая задача 3","Описание простой задачи 3"));//
        taskManager.createNewTask(new Task("Простая задача 4","Описание простой задачи 4"));//
        taskManager.createNewTask(new Task("Простая задача 5","Описание простой задачи 5"));//

        printAllTasks(taskManager);

        //проверяем работу менеджера истории задач
        System.out.println("\n************ НАЧАЛО ПРОВЕРКИ ИСТОРИИ ЗАДАЧ***********\n");
        System.out.println("случай когда просмотренных задач меньше 10:");


        taskManager.getTaskByID(0L);
        taskManager.getTaskByID(1L);
        taskManager.getTaskByID(0L);
        taskManager.getSubTaskByID(7L);
        taskManager.getEpicTaskByID(3L);
        taskManager.getTaskByID(0L);
        List<Task> history = taskManager.getHistoryManager().getHistory();
        for (Task task: history){
            System.out.print("Тип:"+task.getClass().getSimpleName()+" ID="+task.getID()+", ");

        }
        System.out.println();

        //добавим еще просмотры, чтобы общее кол-во после удаления повторов было больше 10
        System.out.println("\nслучай когда просмотренных уникальных задач больше 10:"+
                "\nпри добавлении в историю каждой задачи свыше 10-ти, удаляется самая первая задча в истории");
        taskManager.getSubTaskByID(7L);
        taskManager.getSubTaskByID(4L);
        taskManager.getSubTaskByID(7L);
        taskManager.getSubTaskByID(8L);
        taskManager.getSubTaskByID(7L);
        taskManager.getSubTaskByID(4L);
        taskManager.getEpicTaskByID(2L);
        taskManager.getSubTaskByID(5L);
        taskManager.getSubTaskByID(6L);
        taskManager.getSubTaskByID(5L);
        taskManager.getTaskByID(10L);
        taskManager.getTaskByID(11L);
        taskManager.getEpicTaskByID(9L);
        taskManager.getTaskByID(11L);
        taskManager.getTaskByID(12L);

        HistoryManager historyManager = taskManager.getHistoryManager();

        for (Task task: historyManager.getHistory()){
            System.out.print("Тип:"+task.getClass().getSimpleName()+" ID="+task.getID()+", ");

        }
        System.out.println();

        System.out.println("\n\n************ НАЧАЛО ПРОВЕРКИ ОБНОВЛЕНИЯ ЗАДАЧ***********\n");

        //Проверяем как работает обновление задач. Особое внимание изменению статуса Эпика при различных комбинациях подзадач
        Task updatedTask = new Task("Обновлено имя простой задачи 1","Обновлено описание простой задачи 1");
        updatedTask.setID(Long.parseLong("0"));
        updatedTask.setStatus(TaskStatus.IN_PROCESS);
        taskManager.updateTask(updatedTask);

        EpicTask updatedEpic = new EpicTask("Обновлено имя Эпика 1","Обновлено Описание Эпика 1");
        updatedEpic.setID(Long.parseLong("2"));
        //пытаемся поменять статус Эпика, проверяем, что это невозможно
        updatedEpic.setStatus(TaskStatus.IN_PROCESS);
        taskManager.updateEpicTask(updatedEpic);

        SubTask updatedSubTask = new SubTask("Обновленная подзадача 5","Обновленное описание подзадачи 5",taskManager.getSubTaskByID(Long.parseLong("8")).getParentId());
        updatedSubTask.setID(Long.parseLong("8"));
        //updatedSubTask.setStatus(TaskStatus.IN_PROCESS);
        //updatedSubTask.setStatus(TaskStatus.DONE);
        updatedSubTask.setStatus(TaskStatus.DONE);
        taskManager.updateSubTask(updatedSubTask);

        updatedSubTask = new SubTask("Обновленная подзадача 4","Обновленное описание подзадачи 4",taskManager.getSubTaskByID(Long.parseLong("7")).getParentId());
        updatedSubTask.setID(Long.parseLong("7"));
        //updatedSubTask.setStatus(TaskStatus.IN_PROCESS);
        updatedSubTask.setStatus(TaskStatus.DONE);
        //updatedSubTask.setStatus(TaskStatus.NEW);
        taskManager.updateSubTask(updatedSubTask);

        printAllTasks(taskManager);



        /*проверяем удаление задач.
        При удалении Эпика должны удаляться все его подзадачи из общего списка подзадач.
        При удалении всех эпиков должны удаляться все подзадачи
        При удалении подзадачи:
        1) должен проверяться и при необходимости обновляться статус Эпика.
        2) подзадача должна удаляться из переменной HashMap в соответствующем Эпике

        При удаления любого типа задач номер этой задачи должен удаляться из истории просмотров*/


        System.out.println("\n************ НАЧАЛО ПРОВЕРКИ УДАЛЕНИЯ ЗАДАЧ***********\n");

        taskManager.deleteSubTaskByID (Long.parseLong("7"));

        printAllTasks(taskManager);
        for (Task task: historyManager.getHistory()){
            System.out.print("Тип:"+task.getClass().getSimpleName()+" ID="+task.getID()+", ");

        }
        System.out.println();

        taskManager.deleteEpicByID(Long.parseLong("2"));
        printAllTasks(taskManager);
        for (Task task: historyManager.getHistory()){
            System.out.print("Тип:"+task.getClass().getSimpleName()+" ID="+task.getID()+", ");

        }
        System.out.println();

        taskManager.deleteTaskByID(Long.parseLong("1"));
        printAllTasks(taskManager);
        taskManager.deleteAllSubTasks();

        System.out.println("Удаляем все задачи ");
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubTasks();
        taskManager.deleteAllEpics();
        printAllTasks(taskManager);

    }

    private static void printAllTasks(TaskManager taskManager) {

        for (long nextTask: taskManager.getAllTasks().keySet()){
            System.out.println(taskManager.getAllTasks().get(nextTask));
        }
        System.out.println("\n");


        for (long nextEpic: taskManager.getAllEpics().keySet()){
            System.out.println(taskManager.getAllEpics().get(nextEpic));
            List<Long> nextSubTasksIDsList = taskManager.getAllEpics().get(nextEpic).getSubTasksIDsList();
            System.out.println("Subtasks: ");
            for (Long nextSubTaskID: nextSubTasksIDsList) {
                System.out.print ("    "+nextSubTaskID);
            }
            System.out.println();

        }

        for (long nextTask: taskManager.getAllSubTasks().keySet()){
            System.out.println(taskManager.getAllSubTasks().get(nextTask));
        }
    }
    
    }
         