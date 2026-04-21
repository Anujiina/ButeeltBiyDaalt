package flashcard;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a named collection (deck) of flashcards.
 */
public class Deck {

    private final String id;
    private String name;
    private String description;
    private final List<FlashCard> cards;

    /**
     * Creates a new Deck with a generated ID.
     *
     * @param name        the deck name
     * @param description a short description
     */
    public Deck(String name, String description) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.cards = new ArrayList<>();
    }

    /** @return unique deck ID */
    public String getId() {
        return id;
    }

    /** @return deck name */
    public String getName() {
        return name;
    }

    /** @param name new name */
    public void setName(String name) {
        this.name = name;
    }

    /** @return deck description */
    public String getDescription() {
        return description;
    }

    /** @param description new description */
    public void setDescription(String description) {
        this.description = description;
    }

    /** @return list of cards in this deck */
    public List<FlashCard> getCards() {
        return cards;
    }

    /**
     * Adds a card to this deck.
     *
     * @param card the card to add
     */
    public void addCard(FlashCard card) {
        cards.add(card);
    }

    /**
     * Removes a card from this deck by index.
     *
     * @param index the index to remove
     */
    public void removeCard(int index) {
        cards.remove(index);
    }

    @Override
    public String toString() {
        return name + " (" + cards.size() + " cards)";
    }
}
