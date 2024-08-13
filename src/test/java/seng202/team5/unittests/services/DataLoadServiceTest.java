package seng202.team5.unittests.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import seng202.team5.models.Wine;
import seng202.team5.services.DataLoadService;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;


public class DataLoadServiceTest {
  private DataLoadService dataLoadService;
  private String csvFilePath;

  @BeforeEach
  public void setUp() {
    csvFilePath = System.getProperty("user.dir") + "/src/test/resources/test.csv";
    dataLoadService = new DataLoadService();
  }


  @Test
  public void loadWinesTest() {
    List<Wine> wines = dataLoadService.processWinesFromCsv(csvFilePath);
    assertEquals(16, wines.size());
    Wine wine = wines.getFirst();
    assertEquals("Nicosia 2013 Vulkà Bianco  (Etna)", wine.getName());
    assertEquals(2013, wine.getYear());
    assertEquals(87, wine.getRating());
  }
}
