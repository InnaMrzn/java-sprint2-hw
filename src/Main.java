import ru.yandex.practicum.task.logic.HistoryManager;
import ru.yandex.practicum.task.logic.Managers;
import ru.yandex.practicum.task.models.EpicTask;
import ru.yandex.practicum.task.models.SubTask;
import ru.yandex.practicum.task.models.Task;
import ru.yandex.practicum.task.logic.TaskManager;
import ru.yandex.practicum.task.constants.TaskStatus;

import java.util.HashMap;
import java.util.List;

public class Main {


    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();
        /* Это блок тестирования, будет удален после прохождение проверки кода.
        Симулирует работу с методами, которые в реальности будут производиться из интерфейса пользователя.*.
        */
        System.out.println("**********НАЧАЛО ПРОВЕРКИ СОЗДАНИЯ ЗАДАЧ***********\n");
        //Проверяет метод создания  задач разных типов. ID генерится сквозной нумерацией для всех типов 0, 1, 2 и т.д.
        Task myTask1 = new Task("Простая задача 1","Описание простой задачи 1");
        Task myTask2 = new Task("Простая задача 2","Описание простой задачи 2");
        taskManager.createNewTask(myTask1);//ID 0
        taskManager.createNewTask(myTask2);//ID 1

        EpicTask epicTask1 = new EpicTask("Эпик 1 ", "Описание Эпик 1");
        EpicTask epicTask2 = new EpicTask("Эпик 2 ", "Описание Эпик 2");
        taskManager.createNewEpicTask(epicTask1);//ID 2
        taskManager.createNewEpicTask(epicTask2);//ID 3

        // Создаем Подзадачи для двух Эпиков (ID 2 и 3)
        taskManager.createNewSubTask(new SubTask("Подзадача 1", "Описание Подзадача 1", 2));//ID 4
        taskManager.createNewSubTask(new SubTask("Подзадача 2", "Описание Подзадача 2", 2));//ID 5
        taskManager.createNewSubTask(new SubTask("Подзадача 3", "Описание Подзадача 3", 2));//ID 6

        taskManager.createNewSubTask(new SubTask("Подзадача 4", "Описание Подзадача 4", 3));//ID 7
        taskManager.createNewSubTask(new SubTask("Подзадача 5", "Описание Подзадача 5", 3));//ID 8

        //создаем Эпик без Подзадач
        taskManager.createNewEpicTask(new EpicTask("Эпик 3 ", "Описание Эпик 3"));//ID 9

        //создаем еще три задачи, чтобы общее число было больше 10

        taskManager.createNewTask(new Task("Простая задача 3","Описание простой задачи 3"));// ID 10
        taskManager.createNewTask(new Task("Простая задача 4","Описание простой задачи 4"));// ID 11
        taskManager.createNewTask(new Task("Простая задача 5","Описание простой задачи 5"));// ID 12

        //printAllTasks(taskManager);

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
        taskManager.getTaskByID(11L);
        taskManager.getTaskByID(12L);

        HistoryManager historyManager = taskManager.getHistoryManager();

        for (Task task: historyManager.getHistory()){
            System.out.print("Тип:"+task.getClass().getSimpleName()+" ID="+task.getID()+", ");
        }

        System.out.println("\n\n******НАЧАЛО ПРОВЕРКИ УДАЛЕНИЯ МЕТОДА remove(int ID) в классе InMemoryHistoryManager*******"+
                "\n*******Удаляем Задачу с ID=0 и Эпик с ID=2 (автоматически удаляются подзадачи 4, 5 и 6)");
        historyManager.remove(0);
        historyManager.remove(2);

        for (Task task: historyManager.getHistory()){
            System.out.print("Тип:"+task.getClass().getSimpleName()+" ID="+task.getID()+", ");
        }



        /*System.out.println("\n\n************ НАЧАЛО ПРОВЕРКИ ОБНОВЛЕНИЯ ЗАДАЧ***********\n");

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



        /* проверяем удаление задач.
        При удалении Эпика должны удаляться все его подзадачи из общего списка подзадач.
        При удалении всех эпиков должны удаляться все подзадачи
        При удалении подзадачи:
        1) должен проверяться и при необходимости обновляться статус Эпика.
        2) подзадача должна удаляться из переменной HashMap в соответствующем Эпике
         */

        /*System.out.println("\n************ НАЧАЛО ПРОВЕРКИ УДАЛЕНИЯ ЗАДАЧ***********\n");
        System.out.println("Удаляем подзадачу по ID "+7);
        taskManager.deleteSubTaskByID (Long.parseLong("7"));
        //Если удалить подзадачу с ID=8, в Эпике не останется подзадач и его статус должен поменяться на NEW
        //taskManager.deleteSubTaskByID (8);

        printAllTasks(taskManager);
        System.out.println("Удаляем Эпик по ID "+2);
        taskManager.deleteEpicByID(Long.parseLong("2"));
        printAllTasks(taskManager);
        System.out.println("Удаляем обычную задачу по ID "+1);
        taskManager.deleteTaskByID(Long.parseLong("1"));
        printAllTasks(taskManager);

        //System.out.println("Удаляем все задачи ");
        //taskManager.deleteAllTasks();
        //taskManager.deleteAllSubTasks();
        //taskManager.deleteAllEpics();
        //printAllTasks(taskManager);*/

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
