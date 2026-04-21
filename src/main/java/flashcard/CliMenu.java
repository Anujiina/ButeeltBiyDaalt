package flashcard;

import java.util.List;
import java.util.Scanner;

/**
 * Simple terminal menu helper using only System.in / System.out.
 * Displays numbered lists and reads the user's choice.
 */
public class CliMenu {

    private final Scanner scanner;

    /**
     * Creates a CliMenu using the given scanner.
     *
     * @param scanner the input scanner
     */
    public CliMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Displays a numbered menu and returns the chosen index (0-based).
     * Returns -1 if the user types 'q' or input ends.
     *
     * @param title   the menu title
     * @param choices the list of choice labels
     * @return 0-based index of the selected choice, or -1 to quit/back
     */
    public int show(String title, List<String> choices) {
        while (true) {
            System.out.println();
            printDivider();
            System.out.println("  " + title);
            printDivider();
            for (int i = 0; i < choices.size(); i++) {
                System.out.printf("  [%d] %s%n", i + 1, choices.get(i));
            }
            printDivider();
            System.out.print("  Choose (1-" + choices.size() + ") or q to go back: ");

            String line = readLine();
            if (line == null || line.equalsIgnoreCase("q")) {
                return -1;
            }
            try {
                int choice = Integer.parseInt(line.trim());
                if (choice >= 1 && choice <= choices.size()) {
                    return choice - 1;
                }
            } catch (NumberFormatException ignored) {
                // fall through to retry
            }
            System.out.println("  Invalid input. Please try again.");
        }
    }

    /**
     * Prompts for a single line of text input.
     * Returns null if the user cancels (empty input on a required field, or EOF).
     *
     * @param prompt       the prompt label
     * @param defaultValue shown in brackets; returned if the user presses Enter with no input
     * @param required     if true, empty input (with no default) is rejected
     * @return the entered string, or null if cancelled
     */
    public String prompt(String prompt, String defaultValue, boolean required) {
        while (true) {
            if (defaultValue != null && !defaultValue.isEmpty()) {
                System.out.print("  " + prompt + " [" + defaultValue + "]: ");
            } else {
                System.out.print("  " + prompt + ": ");
            }

            String line = readLine();
            if (line == null) {
                return null;
            }
            if (line.trim().isEmpty()) {
                if (defaultValue != null && !defaultValue.isEmpty()) {
                    return defaultValue;
                }
                if (!required) {
                    return "";
                }
                System.out.println("  This field is required.");
                continue;
            }
            return line.trim();
        }
    }

    /**
     * Asks a yes/no question and returns true if the user answers yes.
     *
     * @param question the question to ask
     * @return true if the user typed y or yes
     */
    public boolean confirm(String question) {
        System.out.print("  " + question + " (y/n): ");
        String line = readLine();
        return line != null && (line.equalsIgnoreCase("y") || line.equalsIgnoreCase("yes"));
    }

    /**
     * Prints a message with surrounding blank lines.
     *
     * @param message the message to print
     */
    public void message(String message) {
        System.out.println();
        System.out.println("  " + message);
    }

    /**
     * Reads a line from the scanner, trimmed. Returns null on EOF.
     *
     * @return trimmed line or null
     */
    public String readLine() {
        if (!scanner.hasNextLine()) {
            return null;
        }
        return scanner.nextLine().trim();
    }

    private void printDivider() {
        System.out.println("  " + "-".repeat(48));
    }
}
