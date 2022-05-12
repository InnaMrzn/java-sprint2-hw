import ru.yandex.practicum.task.constants.TaskStatus;
import ru.yandex.practicum.task.logic.*;
import ru.yandex.practicum.task.models.EpicTask;
import ru.yandex.practicum.task.models.Task;
import ru.yandex.practicum.task.server.KVServer;
import java.io.IOException;

public class Main {

    //static HttpTaskServer taskManagerServer;
    static KVServer storageServer;

    public static void main (String[] args){
        try {
            storageServer = new KVServer();
            storageServer.start();
            TaskManager manager = Managers.getDefault();
            manager.createNewTask(new Task("Hello epic", "description", TaskStatus.NEW));
            EpicTask epic = manager.createNewEpicTask(new EpicTask("epic name", "epic description"));
            manager.deleteAllEpics();

        } catch (IOException ex){
            System.out.println(ex.getMessage());
        }

    }


}
