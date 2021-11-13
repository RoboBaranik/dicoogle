package pt.ua.dicoogle.core.query;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.mockito.Mock;
import pt.ua.dicoogle.sdk.datastructs.Report;
import pt.ua.dicoogle.sdk.datastructs.SearchResult;
import pt.ua.dicoogle.sdk.task.Task;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExportToCSVQueryTaskTest {

  private OutputStream outputStream;
  private ExportToCSVQueryTask export;

  @BeforeEach
  public void init() {
    this.outputStream = new OutputStream() {
      final StringBuilder builder = new StringBuilder();
      @Override
      public void write(int b) throws IOException {
        this.builder.append((char) b);
      }

      public String toString() {
        return this.builder.toString();
      }
      public void add(String s) {
        try {
          this.write(s.getBytes());
        } catch (IOException exception) {
          exception.printStackTrace();
        }
      }

      @Override
      public void write(byte[] b) throws IOException {
//        System.out.println(new String(b));
        super.write(b);
        this.builder.append(new String(b));
      }

      @Override
      public void write(byte[] b, int off, int len) throws IOException {
//        System.out.println(new String(b));
        super.write(b, off, len);
        char[] chars = new char[b.length];
        for (int i = 0; i < b.length; i++) chars[i] = (char) b[i];
//        this.builder.insert(this.builder.toString().length(), chars, off, len);
      }

      @Override
      public void flush() throws IOException {
        super.flush();
//        builder.delete(0, builder.toString().length());
      }
    };
    this.export = new ExportToCSVQueryTask(Arrays.asList("Test1", "Test2"), this.outputStream);

  }

  @Test
  public void testHeader() throws IOException {
    this.export.onCompletion();
//    System.out.println(Arrays.toString("\"Test1\";\"Test2\";\"Test3\";\n".getBytes()));
//    System.out.println(Arrays.toString(this.outputStream.toString().getBytes()));
    assertEquals("\"Test1\";\"Test2\";" + getStreamEnd(), this.outputStream.toString());
  }

  @Test
  public void testHeaderAndCsv() {
    Task<Iterable<SearchResult>> task2 = mockTask();
    HashMap<String, Object> hashMap = new HashMap<>();
    hashMap.put("Test1", "t1;t2");
    hashMap.put("Test2", "t3;t4");
    HashMap<String, Object> hashMap2 = new HashMap<>();
    hashMap2.put("Test1", "t10;t20");
    hashMap2.put("Test2", "t30;t40");
    try {
      List<SearchResult> srList = Arrays.asList(new SearchResult(new URI("uri1"), 0, hashMap),
              new SearchResult(new URI("uri2"), 13, hashMap2));
      when(task2.get()).thenReturn(srList);
    } catch (URISyntaxException | ExecutionException | InterruptedException e) {
      e.printStackTrace();
      fail();
    }
    this.export.onReceive(task2);
    this.export.onCompletion();
    assertEquals("\"Test1\";\"Test2\";\n\"t1,t2\";\"t3,t4\";\n\"t10,t20\";\"t30,t40\";\n", this.outputStream.toString().replace(getStreamEnd(), "\n"));
  }

  private String getStreamEnd() {
    return ((char) 13) + "\n";
  }

  @SuppressWarnings("unchecked")
  private <T> Task<T> mockTask() {
    return mock(Task.class);
  }

}
