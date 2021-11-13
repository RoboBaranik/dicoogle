package pt.ua.dicoogle.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.helpers.AttributesImpl;
import pt.ua.dicoogle.core.settings.ClientSettings;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class XMLClientSupportTest {

  private XMLClientSupport support;
  private Map<String, Boolean> booleanMap;
  private ClientSettings cs;

  @BeforeEach
  public void init() {
    this.support = new XMLClientSupport();

    this.booleanMap = new HashMap<>();
    this.booleanMap.put("ExternalViewer", false);
    this.booleanMap.put("DefaultServerHost", false);
    this.booleanMap.put("DefaultServerPort", false);
    this.booleanMap.put("DefaultUsername", false);
    this.booleanMap.put("DefaultPassword", false);
    this.booleanMap.put("TempFilesDir", false);
    this.booleanMap.put("AutoConnect", false);
    try {
      Field csField = this.support.getClass().getDeclaredField("cs");
      csField.setAccessible(true);
      this.cs = mock(ClientSettings.class);
      csField.set(this.support, this.cs);
//      csField.setAccessible(false);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testStartElement() {
    this.testElements(true);
  }

  @Test
  public void testEndElement() {
    this.testElements(false);
  }

  @Test
  public void testCharactersFalse() {
    this.support.characters("1234".toCharArray(), 0, 4);
    verify(this.cs, times(0)).setExtV(any());
    verify(this.cs, times(0)).setDefaultServerHost(any());
    verify(this.cs, times(0)).setDefaultServerPort(anyInt());
    verify(this.cs, times(0)).setDefaultUserName(any());
    verify(this.cs, times(0)).setDefaultPassword(any());
    verify(this.cs, times(0)).setTempFilesDir(any());
    verify(this.cs, times(0)).setAutoConnect(anyBoolean());
  }

  @Test
  public void testCharactersTrue() {
    this.testElements(true);
    this.support.characters("1234".toCharArray(), 0, 4);
    verify(this.cs, times(1)).setExtV(any());

    this.setBoolean("isViewer", false);
    this.support.characters("1234".toCharArray(), 0, 4);
    verify(this.cs, times(1)).setDefaultServerHost(any());

    this.setBoolean("isHost", false);
    this.support.characters("1234".toCharArray(), 0, 4);
    verify(this.cs, times(1)).setDefaultServerPort(anyInt());

    this.setBoolean("isPort", false);
    this.support.characters("1234".toCharArray(), 0, 4);
    verify(this.cs, times(1)).setDefaultUserName(any());

    this.setBoolean("isUsername", false);
    this.support.characters("1234".toCharArray(), 0, 4);
    verify(this.cs, times(1)).setDefaultPassword(any());

    this.setBoolean("isPassword", false);
    this.support.characters("1234".toCharArray(), 0, 4);
    verify(this.cs, times(1)).setTempFilesDir(any());

    this.setBoolean("isTempDir", false);
    this.support.characters("1234".toCharArray(), 0, 4);

    this.support.characters("true".toCharArray(), 0, 4);
    verify(this.cs, times(2)).setAutoConnect(anyBoolean());
  }

  @Test
  public void testGetXML() {
    ClientSettings xml = this.support.getXML();
    // We need correct config to return not null value.
    assertNull(xml);
  }

  @Test
  public void testPrintXML() {
    try {
      this.support.printXML();
      // We need correct config to not throw exception.
      fail();
    } catch (Exception e) {
      // Passed
    }
  }

  private void testElements(boolean forTrue) {
    List<String> keys = new ArrayList<>(this.booleanMap.keySet());
    for (int i = 0; i < this.booleanMap.size(); i++) {
      String localName = keys.get(i);
      if (forTrue) {
        this.support.startElement("uri", localName, "qName", new AttributesImpl());
      } else {
        this.support.endElement("uri", localName, "qName");
      }
      this.updateBooleanMapByRealBooleans();
      if (forTrue) {
        assertTrue(this.booleanMap.get(localName));
      } else {
        assertFalse(this.booleanMap.get(localName));
      }
    }
  }

  private void updateBooleanMapByRealBooleans() {
    for (Field field : this.support.getClass().getDeclaredFields()) {
      field.setAccessible(true);
      try {
        switch (field.getName()) {
          case "isViewer":
            this.booleanMap.replace("ExternalViewer", (boolean) field.get(this.support));
            break;
          case "isHost":
            this.booleanMap.replace("DefaultServerHost", (boolean) field.get(this.support));
            break;
          case "isPort":
            this.booleanMap.replace("DefaultServerPort", (boolean) field.get(this.support));
            break;
          case "isTempDir":
            this.booleanMap.replace("TempFilesDir", (boolean) field.get(this.support));
            break;
          case "isUsername":
            this.booleanMap.replace("DefaultUsername", (boolean) field.get(this.support));
            break;
          case "isPassword":
            this.booleanMap.replace("DefaultPassword", (boolean) field.get(this.support));
            break;
          case "isAutoConnect":
            this.booleanMap.replace("AutoConnect", (boolean) field.get(this.support));
            break;
        }
      } catch (IllegalAccessException e) {
        continue;
      }
      field.setAccessible(false);
    }
  }

  private void setBoolean(String name, boolean value) {
    try {
      Field declaredField = this.support.getClass().getDeclaredField(name);
      declaredField.setAccessible(true);
      declaredField.set(this.support, value);
      declaredField.setAccessible(false);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      e.printStackTrace();
    }
  }

}
