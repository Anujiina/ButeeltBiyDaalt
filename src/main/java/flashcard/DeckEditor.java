package flashcard;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles interactive deck and card editing via the plain terminal CLI.
 */
public class DeckEditor {

    private final CliMenu menu;
    private final DeckStorage storage;
    private final List<Deck> decks;

    /**
     * Creates a DeckEditor.
     *
     * @param menu    the CLI menu helper
     * @param storage the persistent storage
     * @param decks   the current list of decks (modified in place)
     */
    public DeckEditor(CliMenu menu, DeckStorage storage, List<Deck> decks) {
        this.menu = menu;
        this.storage = storage;
        this.decks = decks;
    }

    /**
     * Shows the deck list and handles deck-level operations.
     */
    public void run() {
        while (true) {
            List<String> choices = buildDeckChoices();
            int selected = menu.show("EDIT DECKS", choices);
            if (selected < 0) {
                return;
            }

            int createIndex = choices.size() - 1;

            if (selected == createIndex) {
                createDeck();
            } else {
                editDeck(decks.get(selected));
            }
        }
    }

    private List<String> buildDeckChoices() {
        List<String> choices = new ArrayList<>();
        for (Deck deck : decks) {
            choices.add(deck.getName() + "  (" + deck.getCards().size() + " cards)");
        }
        choices.add("+ Create new deck");
        return choices;
    }

    private void createDeck() {
        System.out.println();
        System.out.println("  -- New Deck --");
        String name = menu.prompt("Deck name", "", true);
        if (name == null) {
            return;
        }
        String desc = menu.prompt("Description", "", false);
        if (desc == null) {
            desc = "";
        }
        Deck deck = new Deck(name, desc);
        decks.add(deck);
        storage.saveAll(decks);
        menu.message("Deck \"" + name + "\" created!");
    }

    private void editDeck(Deck deck) {
        while (true) {
            int selected = menu.show(
                "DECK: " + deck.getName().toUpperCase(),
                List.of("Edit cards", "Rename deck", "Delete deck", "< Back"));

            if (selected < 0 || selected == 3) {
                return;
            }
            switch (selected) {
                case 0 -> editCards(deck);
                case 1 -> renameDeck(deck);
                case 2 -> {
                    if (deleteDeck(deck)) {
                        return;
                    }
                }
                default -> { /* no-op */ }
            }
        }
    }

    private void renameDeck(Deck deck) {
        System.out.println();
        System.out.println("  -- Rename Deck --");
        String name = menu.prompt("New name", deck.getName(), true);
        if (name == null) {
            return;
        }
        String desc = menu.prompt("Description", deck.getDescription(), false);
        deck.setName(name);
        if (desc != null) {
            deck.setDescription(desc);
        }
        storage.saveAll(decks);
        menu.message("Deck updated!");
    }

    private boolean deleteDeck(Deck deck) {
        if (menu.confirm("Delete \"" + deck.getName() + "\"? This cannot be undone.")) {
            decks.remove(deck);
            storage.saveAll(decks);
            menu.message("Deck deleted.");
            return true;
        }
        menu.message("Cancelled.");
        return false;
    }

    // ---------------------------------------------------------------
    // Card editing
    // ---------------------------------------------------------------

    private void editCards(Deck deck) {
        while (true) {
            List<String> choices = buildCardChoices(deck);
            int selected = menu.show(
                "CARDS IN: " + deck.getName().toUpperCase(), choices);

            if (selected < 0) {
                return;
            }

            int newIndex = choices.size() - 1;
            if (selected == newIndex) {
                createCard(deck);
            } else {
                editCard(deck, selected);
            }
        }
    }

    private List<String> buildCardChoices(Deck deck) {
        List<String> choices = new ArrayList<>();
        List<FlashCard> cards = deck.getCards();
        for (int i = 0; i < cards.size(); i++) {
            FlashCard c = cards.get(i);
            choices.add((i + 1) + ". Q: " + trunc(c.getQuestion(), 30)
                + "  |  A: " + trunc(c.getAnswer(), 20));
        }
        choices.add("+ New card");
        return choices;
    }

    private void createCard(Deck deck) {
        System.out.println();
        System.out.println("  -- New Card --");
        String question = menu.prompt("Question", "", true);
        if (question == null) {
            return;
        }
        String answer = menu.prompt("Answer", "", true);
        if (answer == null) {
            return;
        }
        deck.addCard(new FlashCard(question, answer));
        storage.saveAll(decks);
        menu.message("Card created!");
    }

    private void editCard(Deck deck, int index) {
        FlashCard card = deck.getCards().get(index);
        System.out.println();
        System.out.println("  Q: " + card.getQuestion());
        System.out.println("  A: " + card.getAnswer());

        int selected = menu.show(
            "EDIT CARD",
            List.of("Modify", "Delete", "< Back"));

        if (selected == 0) {
            modifyCard(deck, index, card);
        } else if (selected == 1) {
            deleteCard(deck, index, card);
        }
    }

    private void modifyCard(Deck deck, int index, FlashCard card) {
        System.out.println();
        System.out.println("  -- Edit Card --");
        String question = menu.prompt("Question", card.getQuestion(), true);
        if (question == null) {
            return;
        }
        String answer = menu.prompt("Answer", card.getAnswer(), true);
        if (answer == null) {
            return;
        }
        deck.getCards().set(index, new FlashCard(question, answer));
        storage.saveAll(decks);
        menu.message("Card updated!");
    }

    private void deleteCard(Deck deck, int index, FlashCard card) {
        if (menu.confirm("Delete card: \"" + card.getQuestion() + "\"?")) {
            deck.removeCard(index);
            storage.saveAll(decks);
            menu.message("Card deleted.");
        } else {
            menu.message("Cancelled.");
        }
    }

    private String trunc(String text, int max) {
        return text.length() <= max ? text : text.substring(0, max - 3) + "...";
    }
}
