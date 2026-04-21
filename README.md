# Flashcard System

Interactive command-line flashcard learning system built with Java and Maven.

## Requirements
- Java 17+
- Maven 3.6+

## Build

```bash
mvn package
```

## Usage

```
flashcard <cards-file> [options]

Options:
  -h, --help                  Show this help message
      --order <order>         Card order: random (default), worst-first, recent-mistakes-first
      --repetitions <num>     Consecutive correct answers required per card (default: 1)
      --invertCards           Swap question and answer sides
```

### Examples

```bash
# Run with sample cards, random order
java -jar target/flashcard-1.0-SNAPSHOT.jar sample-cards.txt

# Require 3 correct answers per card, worst-first ordering
java -jar target/flashcard-1.0-SNAPSHOT.jar sample-cards.txt --order worst-first --repetitions 3

# Practice in reverse (answer → question)
java -jar target/flashcard-1.0-SNAPSHOT.jar sample-cards.txt --invertCards

# Recent mistakes first
java -jar target/flashcard-1.0-SNAPSHOT.jar sample-cards.txt --order recent-mistakes-first
```

## Cards File Format

Plain text file with `Q:` and `A:` prefixes. Lines starting with `#` are comments.

```
# This is a comment
Q: What is 2 + 2?
A: 4

Q: Capital of Mongolia?
A: Ulaanbaatar
```

## Achievements

| Achievement | Condition |
|-------------|-----------|
| SPEEDY      | Average response time under 5 seconds per round |
| CORRECT     | All cards answered correctly in a round |
| REPEAT      | One card answered more than 5 times total |
| CONFIDENT   | One card answered correctly 3+ times consecutively |

## Architecture

| Class | Role |
|-------|------|
| `FlashCard` | Card model with attempt tracking |
| `CardOrganizer` | Interface for card ordering strategies |
| `RandomSorter` | Shuffles cards randomly |
| `WorstFirstSorter` | Sorts by lowest correct ratio first |
| `RecentMistakesFirstSorter` | Recent wrong answers come first |
| `CardLoader` | Parses cards from file |
| `FlashCardSession` | Manages rounds and user interaction |
| `AchievementTracker` | Evaluates and reports achievements |
| `Main` | CLI entry point |
