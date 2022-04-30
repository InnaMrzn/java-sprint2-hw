package ru.yandex.practicum.task.test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.task.logic.Managers;


class InMemoryTaskManagerTest extends TaskManagerTest {

    @BeforeAll
    public static void beforeAll (){
        Managers.setIsBacked(false);

    }

    @BeforeEach
    public void afterEach(){
       taskManager = Managers.getDefault();

    }



}