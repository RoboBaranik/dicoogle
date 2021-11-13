package pt.ua.dicoogle.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QueryExpressionBuilderTest {

  private QueryExpressionBuilder builder;

  @BeforeEach
  public void init() {
    ArrayList tags = new ArrayList(Arrays.asList("tag1", "tag2^tag3"));
    this.builder = new QueryExpressionBuilder(
            "test of^the builder",
            tags);
  }

  @Test
  void testGetQueryString() {
    this.builder.setTags(new ArrayList<>(Arrays.asList("first", "second")));
    String queryString = this.builder.getQueryString();
    String expectedResult = "(" +
            "first:test OR second:testOR others:test" + " ) AND (" +
            "first:of OR second:ofOR others:of" + " ) AND (" +
            "first:the OR second:theOR others:the" + " ) AND (" +
            "first:builder OR second:builderOR others:builder" + " )";
    assertEquals(expectedResult, queryString);
  }

}
