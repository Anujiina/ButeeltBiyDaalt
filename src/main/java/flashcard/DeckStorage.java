package flashcard;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles persistent storage of decks and cards using a JSON file. The data file is stored at
 * ~/.flashcards/data.json by default.
 */
public class DeckStorage {

  private static final String DEFAULT_DIR = "data" + File.separator + ".flashcards";
  private static final String DEFAULT_FILE = DEFAULT_DIR + File.separator + "data.json";

  private final String filePath;
  private final Gson gson;

  /** Creates a DeckStorage using the default data file location. */
  public DeckStorage() {
    this(DEFAULT_FILE);
  }

  /**
   * Creates a DeckStorage using a custom file path (useful for testing).
   *
   * @param filePath path to the JSON data file
   */
  public DeckStorage(String filePath) {
    this.filePath = filePath;
    this.gson = new GsonBuilder().setPrettyPrinting().create();
    ensureDirectoryExists();
  }

  /**
   * Loads all decks from the JSON file.
   *
   * @return list of decks, or empty list if file doesn't exist
   */
  public List<Deck> loadAll() {
    File file = new File(filePath);
    if (!file.exists()) {
      return new ArrayList<>();
    }
    try (FileReader reader = new FileReader(file)) {
      Type listType = new TypeToken<List<Deck>>() {}.getType();
      List<Deck> decks = gson.fromJson(reader, listType);
      return decks != null ? decks : new ArrayList<>();
    } catch (IOException e) {
      System.err.println("Warning: Could not read data file: " + e.getMessage());
      return new ArrayList<>();
    }
  }

  /**
   * Saves all decks to the JSON file.
   *
   * @param decks the list of decks to save
   */
  public void saveAll(List<Deck> decks) {
    try (FileWriter writer = new FileWriter(filePath)) {
      gson.toJson(decks, writer);
    } catch (IOException e) {
      System.err.println("Warning: Could not save data file: " + e.getMessage());
    }
  }

  /**
   * @return the file path used for storage
   */
  public String getFilePath() {
    return filePath;
  }

  private void ensureDirectoryExists() {
    File dir = new File(filePath).getParentFile();
    if (dir != null && !dir.exists()) {
      dir.mkdirs();
    }
  }
}
