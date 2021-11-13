package pt.ua.dicoogle.taskManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import pt.ua.dicoogle.sdk.datastructs.Report;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.task.Task;

import java.lang.reflect.Field;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class RunningIndexTasksTest {

//  @Mock
  private RunningIndexTasks runningIndexTasks;

  @BeforeEach
  public void init() throws IllegalAccessException {
    this.runningIndexTasks = RunningIndexTasks.getInstance();
//    this.runningIndexTasks = mock(RunningIndexTasks.class);

  }

  @Test
  public void testAddTask() {
    this.runningIndexTasks.addTask("123", new Task<>("123", new Callable<Report>() {
      @Override
      public Report call() throws Exception {
        return null;
      }
    }));
    this.runningIndexTasks.addTask("124", new Task<>("124", new Callable<Report>() {
      @Override
      public Report call() throws Exception {
        return new Report();
      }
    }));

//    verify(this.runningIndexTasks, times(2)).hookRemoveRunningTasks();
    assertEquals(2, this.runningIndexTasks.getRunningTasks().size());
  }

}
