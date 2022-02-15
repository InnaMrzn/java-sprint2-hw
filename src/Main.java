import ru.yandex.practicum.task.EpicTask;
import ru.yandex.practicum.task.SubTask;
import ru.yandex.practicum.task.Task;
import ru.yandex.practicum.task.TaskManager;
import ru.yandex.practicum.task.constants.TaskStatus;

import java.util.HashMap;

public class Main {


    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();
        /* Это блок тестирования, будет удален после прохождение проверки кода.
        Симулирует работу с методами, которые в реальности будут производиться из интерфейса пользователя.*.
        */

        //Проверяет метод создания  задач разных типов.
        Task myTask1 = new Task("Простая задача 1","Описание простой задачи 1");
        Task myTask2 = new Task("Простая задача 2","Описание простой задачи 2");
        taskManager.createNewTask(myTask1);
        taskManager.createNewTask(myTask2);

        EpicTask epicTask1 = new EpicTask("Эпик 1 ", "Описание Эпик 1");
        EpicTask epicTask2 = new EpicTask("Эпик 2 ", "Описание Эпик 2");
        taskManager.createNewEpicTask(epicTask1);
        taskManager.createNewEpicTask(epicTask2);

        // В классе TaskManager сквозная нумерация ID для всех типов классов начиная с 0. Поэтому epicTask1 будет иметь ID 2
        taskManager.createNewSubTask(new SubTask("Подкласс 1", "Описание подкласса 1", 2));
        taskManager.createNewSubTask(new SubTask("Подкласс 2", "Описание подкласса 2", 2));
        taskManager.createNewSubTask(new SubTask("Подкласс 3", "Описание подкласса 3", 2));

        taskManager.createNewSubTask(new SubTask("Подкласс 4", "Описание подкласса 4", 3));
        taskManager.createNewSubTask(new SubTask("Подкласс 5", "Описание подкласса 5", 3));

        System.out.println("**********НАЧАЛО ПРОВЕРКИ СОЗДАНИЯ ЗАДАЧ***********\n");
        printAllTasks(taskManager);

        //Проверяем как работает обновление задач. Особое внимание изменению статуса Эпика при различных комбинациях подзадач
        Task updatedTask = new Task("Обновлено имя простой задачи 1","Обновлено описание простой задачи 1");
        updatedTask.setID(0);
        updatedTask.setStatus(TaskStatus.IN_PROCESS);
        taskManager.updateTask(updatedTask);

        EpicTask updatedEpic = new EpicTask("Обновлено имя Эпика 1","Обновлено Описание Эпика 1");
        updatedEpic.setID(2);
        //пытаемся поменять статус Эпика, проверяем, что это невозможно
        updatedEpic.setStatus(TaskStatus.IN_PROCESS);
        taskManager.updateEpicTask(updatedEpic);

        SubTask updatedSubTask = new SubTask("Обновленная подзадача 5","Обновленное описание подзадачи 5",taskManager.getSubTaskByID(8).getParentId());
        updatedSubTask.setID(8);
        //updatedSubTask.setStatus(TaskStatus.IN_PROCESS);
        //updatedSubTask.setStatus(TaskStatus.DONE);
        updatedSubTask.setStatus(TaskStatus.DONE);
        taskManager.updateSubTask(updatedSubTask);

        updatedSubTask = new SubTask("Обновленная подзадача 4","Обновленное описание подзадачи 4",taskManager.getSubTaskByID(7).getParentId());
        updatedSubTask.setID(7);
        //updatedSubTask.setStatus(TaskStatus.IN_PROCESS);
        updatedSubTask.setStatus(TaskStatus.DONE);
        //updatedSubTask.setStatus(TaskStatus.NEW);
        taskManager.updateSubTask(updatedSubTask);

        System.out.println("\n************ НАЧАЛО ПРОВЕРКИ ОБНОВЛЕНИЯ ЗАДАЧ***********\n");
        printAllTasks(taskManager);

        /* проверяем удаление задач.
        При удалении Эпика должны удаляться все его подзадачи из общего списка подзадач.
        При удалении всех эпиков должны удаляться все подзадачи
        При удалении подзадачи:
        1) должен проверяться и при необходимости обновляться статус Эпика.
        2) подзадача должна удаляться из переменной HashMap в соответствующем Эпике
         */
        taskManager.deleteSubTaskByID (7);
        //Если удалить подзадачу с ID=8, в Эпике не останется подзадач и его статус должен поменяться на NEW
        //taskManager.deleteSubTaskByID (8);
        System.out.println("\n************ НАЧАЛО ПРОВЕРКИ УДАЛЕНИЯ ЗАДАЧ***********\n");
        System.out.println("Удаляем подзадачу по ID ");
        printAllTasks(taskManager);
        System.out.println("Удаляем Эпик по ID ");
        taskManager.deleteEpicByID(2);
        printAllTasks(taskManager);
        System.out.println("Удаляем обычную задачу по ID ");
        taskManager.deleteTaskByID(1);
        printAllTasks(taskManager);

        //System.out.println("Удаляем все задачи ");
        //taskManager.deleteAllTasks();
        //taskManager.deleteAllSubTasks();
        //taskManager.deleteAllEpics();
        //printAllTasks(taskManager);

    }

    private static void printAllTasks(TaskManager taskManager) {

        for (int nextTask: taskManager.getAllTasks().keySet()){
            System.out.println(taskManager.getAllTasks().get(nextTask));
        }
        System.out.println("\n");

        for (int nextTask: taskManager.getAllSubTasks().keySet()){
            System.out.println(taskManager.getAllSubTasks().get(nextTask));
        }
        System.out.println("\n");

        for (int nextEpic: taskManager.getAllEpics().keySet()){
            System.out.println(taskManager.getAllEpics().get(nextEpic));
            HashMap<Integer, SubTask> nextSubTasksMap = taskManager.getAllEpics().get(nextEpic).getSubTasksMap();
            for (int nextSubTask: nextSubTasksMap.keySet()) {
                System.out.println ("    "+nextSubTasksMap.get(nextSubTask));
            }

        }
    }
}
