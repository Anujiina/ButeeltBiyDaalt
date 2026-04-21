package flashcard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Main entry point for the Flashcard CLI application.
 *
 * <p>Two modes:
 *
 * <ul>
 *   <li>No arguments: interactive menu mode (create/edit decks and cards, then study)
 *   <li>With cards-file: direct study mode from a text file
 * </ul>
 */
public class Main {

  private static final int DEFAULT_REPETITIONS = 1;

  /**
   * Application entry point.
   *
   * @param args command-line arguments
   */
  public static void main(String[] args) {
    Options options = buildOptions();
    CommandLineParser parser = new DefaultParser();
    HelpFormatter formatter = new HelpFormatter();

    CommandLine cmd;
    try {
      cmd = parser.parse(options, args);
    } catch (ParseException e) {
      System.err.println("Error: " + e.getMessage());
      formatter.printHelp("flashcard [cards-file] [options]", options);
      System.exit(1);
      return;
    }

    if (cmd.hasOption("help")) {
      formatter.printHelp("flashcard [cards-file] [options]", options);
      System.exit(0);
      return;
    }

    List<String> remaining = cmd.getArgList();

    if (remaining.isEmpty()) {
      runInteractiveMode(cmd);
    } else if (remaining.size() == 1) {
      runFileMode(remaining.get(0), cmd);
    } else {
      System.err.println("Error: Too many arguments.");
      formatter.printHelp("flashcard [cards-file] [options]", options);
      System.exit(1);
    }
  }

  // ---------------------------------------------------------------
  // Interactive mode
  // ---------------------------------------------------------------

  private static void runInteractiveMode(CommandLine cmd) {
    DeckStorage storage = new DeckStorage();
    List<Deck> decks = storage.loadAll();
    Scanner scanner = new Scanner(System.in);
    CliMenu menu = new CliMenu(scanner);

    int repetitions = parseRepetitions(cmd);
    System.out.println();
    System.out.println("  ================================");
    System.out.println("   Welcome to Flashcards CLI!");
    System.out.println("   Data: " + storage.getFilePath());
    System.out.println("  ================================");

    while (true) {
      int choice = menu.show("MAIN MENU", List.of("Study", "Edit my decks", "Quit"));

      if (choice < 0 || choice == 2) {
        System.out.println();
        System.out.println("  Bye!");
        break;
      } else if (choice == 0) {
        studyMenu(menu, decks, storage, repetitions);
      } else if (choice == 1) {
        new DeckEditor(menu, storage, decks).run();
      }
    }
  }

  private static void studyMenu(
      CliMenu menu, List<Deck> decks, DeckStorage storage, int repetitions) {
    if (decks.isEmpty()) {
      menu.message("No decks yet! Go to 'Edit my decks' to create one.");
      return;
    }

    List<String> deckChoices = new ArrayList<>();
    for (Deck d : decks) {
      deckChoices.add(d.getName() + "  (" + d.getCards().size() + " cards)");
    }
    deckChoices.add("< Back");

    int selected = menu.show("CHOOSE DECK TO STUDY", deckChoices);
    if (selected < 0 || selected == deckChoices.size() - 1) {
      return;
    }

    Deck deck = decks.get(selected);

    int orderChoice =
        menu.show(
            "CARD ORDER", List.of("Random", "Worst first", "Recent mistakes first", "< Back"));
    if (orderChoice < 0 || orderChoice == 3) {
      return;
    }
    String order =
        switch (orderChoice) {
          case 1 -> "worst-first";
          case 2 -> "recent-mistakes-first";
          default -> "random";
        };

    int invertChoice =
        menu.show("CARD SIDE", List.of("Normal  (Q -> A)", "Inverted  (A -> Q)", "< Back"));
    if (invertChoice < 0 || invertChoice == 2) {
      return;
    }
    boolean invertCards = invertChoice == 1;

    CardOrganizer organizer = buildOrganizer(order);
    new StudySession(menu, deck, organizer, repetitions, invertCards).run();
  }

  // ---------------------------------------------------------------
  // File mode (legacy)
  // ---------------------------------------------------------------

  private static void runFileMode(String cardsFile, CommandLine cmd) {
    String order = cmd.getOptionValue("order", "random");
    if (!order.equals("random")
        && !order.equals("worst-first")
        && !order.equals("recent-mistakes-first")) {
      System.err.println("Error: Invalid --order value '" + order + "'.");
      System.exit(1);
      return;
    }

    int repetitions = parseRepetitions(cmd);
    boolean invertCards = cmd.hasOption("invertCards");

    CardLoader loader = new CardLoader();
    List<FlashCard> cards;
    try {
      cards = loader.load(cardsFile);
    } catch (IOException e) {
      System.err.println("Error reading file '" + cardsFile + "': " + e.getMessage());
      System.exit(1);
      return;
    } catch (IllegalArgumentException e) {
      System.err.println("Error in file format: " + e.getMessage());
      System.exit(1);
      return;
    }

    CardOrganizer organizer = buildOrganizer(order);
    new FlashCardSession(cards, organizer, repetitions, invertCards).run();
  }

  // ---------------------------------------------------------------
  // Helpers
  // ---------------------------------------------------------------

  private static CardOrganizer buildOrganizer(String order) {
    return switch (order) {
      case "worst-first" -> new WorstFirstSorter();
      case "recent-mistakes-first" -> new RecentMistakesFirstSorter();
      default -> new RandomSorter();
    };
  }

  private static int parseRepetitions(CommandLine cmd) {
    if (cmd.hasOption("repetitions")) {
      try {
        int r = Integer.parseInt(cmd.getOptionValue("repetitions"));
        if (r < 1) {
          throw new NumberFormatException();
        }
        return r;
      } catch (NumberFormatException e) {
        System.err.println("Error: --repetitions must be a positive integer.");
        System.exit(1);
      }
    }
    return DEFAULT_REPETITIONS;
  }

  private static Options buildOptions() {
    Options options = new Options();
    options.addOption(Option.builder("h").longOpt("help").desc("Show this help message").build());
    options.addOption(
        Option.builder()
            .longOpt("order")
            .hasArg()
            .argName("order")
            .desc("Card order: random (default), worst-first, recent-mistakes-first")
            .build());
    options.addOption(
        Option.builder()
            .longOpt("repetitions")
            .hasArg()
            .argName("num")
            .desc("Consecutive correct answers required per card (default: 1)")
            .build());
    options.addOption(
        Option.builder().longOpt("invertCards").desc("Swap question and answer sides").build());
    return options;
  }
}
