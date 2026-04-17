package web.sportflow.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;

class ReferenceSportExerciseCatalogTest {

  @Test
  void exerciseLinkDto_equalsHashCodeAndToString_useArrayContent() throws Exception {
    Class<?> type =
        Class.forName("web.sportflow.config.ReferenceSportExerciseCatalog$ExerciseLinkDto");
    Constructor<?> ctor = type.getDeclaredConstructor(String.class, String[].class);
    ctor.setAccessible(true);

    Object linkA = ctor.newInstance("Course", new String[] {"Sprint", "Burpees"});
    Object linkB = ctor.newInstance("Course", new String[] {"Sprint", "Burpees"});
    Object linkC = ctor.newInstance("Course", new String[] {"Burpees", "Sprint"});

    assertEquals(linkA, linkA);
    assertEquals(linkA, linkB);
    assertEquals(linkA.hashCode(), linkB.hashCode());
    assertNotEquals(linkA, linkC);
    assertNotEquals(linkA, null);
    assertNotEquals(linkA, "not-an-exercise-link");
    assertTrue(linkA.toString().contains("sportName=Course"));
    assertTrue(linkA.toString().contains("exerciseNames=[Sprint, Burpees]"));
  }
}
