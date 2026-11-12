package com.bookshelf.bookshelfapi.config;

import com.bookshelf.bookshelfapi.model.Book;
import com.bookshelf.bookshelfapi.model.Category;
import com.bookshelf.bookshelfapi.model.Loan;
import com.bookshelf.bookshelfapi.model.Tag;
import com.bookshelf.bookshelfapi.repository.BookRepository;
import com.bookshelf.bookshelfapi.repository.CategoryRepository;
import com.bookshelf.bookshelfapi.repository.LoanRepository;
import com.bookshelf.bookshelfapi.repository.TagRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class DataSeeder {

    // Shelf section code per category, used to build call numbers like FIC-ORW-01
    private static final Map<String, String> SECTION_CODES = Map.of(
            "Fiction", "FIC",
            "Science Fiction", "SF",
            "Fantasy", "FAN",
            "Mystery & Thriller", "MYS",
            "Programming", "PRG",
            "History", "HIS",
            "Science", "SCI",
            "Biography", "BIO"
    );

    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final TagRepository tagRepository;
    private final LoanRepository loanRepository;

    private final Map<String, Integer> shelfCounters = new HashMap<>();

    public DataSeeder(CategoryRepository categoryRepository,
                      BookRepository bookRepository,
                      TagRepository tagRepository,
                      LoanRepository loanRepository) {
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
        this.tagRepository = tagRepository;
        this.loanRepository = loanRepository;
    }

    // Runs once at startup and fills an empty database with sample data
    @Bean
    public CommandLineRunner seedDatabase() {
        return args -> {
            if (categoryRepository.count() > 0) return;

            // Create categories
            Category fiction = category("Fiction", "Novels, short stories, and literary fiction");
            Category sciFi = category("Science Fiction", "Futuristic and speculative fiction");
            Category fantasy = category("Fantasy", "Magic, myth, and other worlds");
            Category mystery = category("Mystery & Thriller", "Crime, suspense, and whodunits");
            Category programming = category("Programming", "Software development and coding books");
            Category history = category("History", "Historical non-fiction");
            Category science = category("Science", "Popular science and nature");
            Category biography = category("Biography", "Lives and memoirs");

            // Create tags
            Tag classic = tagRepository.save(new Tag("Classic"));
            Tag bestseller = tagRepository.save(new Tag("Bestseller"));
            Tag dystopian = tagRepository.save(new Tag("Dystopian"));
            Tag awardWinner = tagRepository.save(new Tag("Award Winner"));
            Tag beginner = tagRepository.save(new Tag("Beginner Friendly"));
            Tag series = tagRepository.save(new Tag("Series"));

            // Create books (title, author, description, replacement cost, copies, ISBN, category, tags)
            List<Book> books = List.of(
                    // Fiction
                    book("The Great Gatsby", "F. Scott Fitzgerald",
                            "A story of decadence and excess in the Jazz Age.",
                            "12.99", 4, "978-0743273565", fiction, classic),
                    book("1984", "George Orwell",
                            "A dystopian novel about totalitarian government surveillance.",
                            "11.49", 5, "978-0451524935", fiction, classic, dystopian),
                    book("To Kill a Mockingbird", "Harper Lee",
                            "A child's view of racial injustice in a small Southern town.",
                            "14.99", 4, null, fiction, classic, awardWinner),
                    book("Pride and Prejudice", "Jane Austen",
                            "Elizabeth Bennet and Mr. Darcy navigate manners, money, and marriage.",
                            "9.99", 3, null, fiction, classic),
                    book("The Catcher in the Rye", "J. D. Salinger",
                            "Holden Caulfield's restless days in New York City.",
                            "10.99", 2, null, fiction, classic),
                    book("Brave New World", "Aldous Huxley",
                            "A future society engineered for stability and pleasure.",
                            "13.99", 3, null, fiction, classic, dystopian),
                    book("The Road", "Cormac McCarthy",
                            "A father and son walk through a burned, empty America.",
                            "15.49", 2, null, fiction, awardWinner),
                    inLanguage("French", book("Le Petit Prince", "Antoine de Saint-Exupéry",
                            "A pilot stranded in the desert meets a young prince from another planet.",
                            "9.99", 3, null, fiction, classic)),
                    inLanguage("Spanish", book("Cien años de soledad", "Gabriel García Márquez",
                            "Seven generations of the Buendía family in the town of Macondo.",
                            "16.99", 2, null, fiction, classic)),
                    inLanguage("German", book("Der Process", "Franz Kafka",
                            "Josef K. is arrested and tried for a crime no one will name.",
                            "11.99", 2, null, fiction, classic)),

                    // Science Fiction
                    book("Dune", "Frank Herbert",
                            "An epic science fiction novel set in the distant future.",
                            "15.99", 4, "978-0441013593", sciFi, classic, bestseller),
                    book("Neuromancer", "William Gibson",
                            "The pioneering cyberpunk novel.",
                            "13.49", 2, "978-0441569595", sciFi),
                    book("Foundation", "Isaac Asimov",
                            "A mathematician plans to shorten the dark age after a galactic empire falls.",
                            "14.49", 3, null, sciFi, classic, series),
                    book("The Left Hand of Darkness", "Ursula K. Le Guin",
                            "An envoy on a winter planet whose people have no fixed gender.",
                            "13.99", 2, null, sciFi, awardWinner),
                    book("The Martian", "Andy Weir",
                            "An astronaut stranded on Mars has to science his way home.",
                            "16.99", 4, null, sciFi, bestseller),
                    book("Project Hail Mary", "Andy Weir",
                            "A lone astronaut wakes up with no memory and a mission to save Earth.",
                            "18.99", 2, null, sciFi, bestseller),
                    book("Fahrenheit 451", "Ray Bradbury",
                            "A fireman whose job is burning books starts to read them.",
                            "11.99", 1, null, sciFi, classic, dystopian),

                    // Fantasy
                    book("The Hobbit", "J. R. R. Tolkien",
                            "Bilbo Baggins is swept into a quest to reclaim a dragon's hoard.",
                            "12.99", 5, null, fantasy, classic),
                    book("The Name of the Wind", "Patrick Rothfuss",
                            "Kvothe tells the story of how he became a legend.",
                            "17.99", 2, null, fantasy, series),
                    book("A Game of Thrones", "George R. R. Martin",
                            "Noble houses fight for the Iron Throne as winter approaches.",
                            "19.99", 3, null, fantasy, bestseller, series),
                    book("Mistborn: The Final Empire", "Brandon Sanderson",
                            "A crew of thieves plans to overthrow an immortal emperor.",
                            "16.49", 3, null, fantasy, series),
                    book("The Way of Kings", "Brandon Sanderson",
                            "Soldiers, scholars, and assassins on a storm-swept world.",
                            "21.99", 1, null, fantasy, series),

                    // Mystery & Thriller
                    book("Gone Girl", "Gillian Flynn",
                            "A wife vanishes on her anniversary, and her husband is the suspect.",
                            "14.99", 3, null, mystery, bestseller),
                    book("The Girl with the Dragon Tattoo", "Stieg Larsson",
                            "A journalist and a hacker dig into a decades-old disappearance.",
                            "15.99", 2, null, mystery, bestseller, series),
                    book("And Then There Were None", "Agatha Christie",
                            "Ten strangers on an island are killed one by one.",
                            "10.49", 3, null, mystery, classic),
                    book("The Silent Patient", "Alex Michaelides",
                            "A painter shoots her husband and never speaks again.",
                            "16.49", 2, null, mystery, bestseller),
                    book("The Da Vinci Code", "Dan Brown",
                            "A symbologist follows clues hidden in famous works of art.",
                            "13.49", 2, null, mystery, bestseller),
                    inLanguage("Italian", book("Il nome della rosa", "Umberto Eco",
                            "A monk investigates a series of deaths in a medieval abbey.",
                            "15.49", 2, null, mystery, classic)),

                    // Programming
                    book("Clean Code", "Robert C. Martin",
                            "A handbook of agile software craftsmanship.",
                            "34.99", 4, "978-0132350884", programming),
                    book("Spring in Action", "Craig Walls",
                            "Comprehensive guide to the Spring Framework.",
                            "39.99", 3, "978-1617294945", programming),
                    book("The Pragmatic Programmer", "Andrew Hunt, David Thomas",
                            "Practical advice for working programmers.",
                            "42.99", 2, null, programming, classic),
                    book("Effective Java", "Joshua Bloch",
                            "Best practices for the Java platform.",
                            "45.99", 2, null, programming),
                    book("Design Patterns", "Erich Gamma et al.",
                            "Reusable solutions to common object-oriented design problems.",
                            "54.99", 1, null, programming, classic),
                    book("Head First Java", "Kathy Sierra, Bert Bates",
                            "A visual, hands-on introduction to Java.",
                            "39.99", 3, null, programming, beginner),
                    book("Refactoring", "Martin Fowler",
                            "Improving the design of existing code, one small step at a time.",
                            "47.99", 1, null, programming),
                    book("Grokking Algorithms", "Aditya Bhargava",
                            "An illustrated guide to common algorithms.",
                            "34.99", 3, null, programming, beginner),

                    // History
                    book("Sapiens", "Yuval Noah Harari",
                            "A brief history of humankind.",
                            "16.99", 4, "978-0062316097", history, bestseller),
                    book("Guns, Germs, and Steel", "Jared Diamond",
                            "Why some societies conquered others.",
                            "18.49", 2, null, history, awardWinner),
                    book("The Silk Roads", "Peter Frankopan",
                            "World history told from the trade routes between East and West.",
                            "19.99", 2, null, history),
                    book("SPQR", "Mary Beard",
                            "A history of ancient Rome.",
                            "17.99", 2, null, history),

                    // Science
                    book("A Brief History of Time", "Stephen Hawking",
                            "Black holes, the Big Bang, and the nature of time.",
                            "15.99", 3, null, science, classic, bestseller),
                    book("Cosmos", "Carl Sagan",
                            "A tour of the universe and our place in it.",
                            "16.99", 2, null, science, classic),
                    book("The Selfish Gene", "Richard Dawkins",
                            "Evolution seen from the point of view of the gene.",
                            "14.99", 2, null, science),
                    book("The Gene: An Intimate History", "Siddhartha Mukherjee",
                            "The story of genetics, from Mendel to gene editing.",
                            "19.49", 1, null, science, awardWinner),

                    // Biography
                    book("Steve Jobs", "Walter Isaacson",
                            "The authorized biography of Apple's co-founder.",
                            "20.99", 2, null, biography, bestseller),
                    book("Educated", "Tara Westover",
                            "A woman raised off-grid in Idaho goes on to earn a PhD.",
                            "15.99", 3, null, biography, bestseller),
                    book("Long Walk to Freedom", "Nelson Mandela",
                            "Mandela's autobiography, from childhood to the presidency.",
                            "18.99", 2, null, biography),
                    book("The Diary of a Young Girl", "Anne Frank",
                            "A Jewish teenager's diary written in hiding during the war.",
                            "9.49", 3, null, biography, classic),

                    // No category
                    book("The Elements of Style", "William Strunk Jr., E. B. White",
                            "A short guide to clear English writing.",
                            "8.99", 2, null, null)
            );
            Map<String, Book> byTitle = bookRepository.saveAll(books).stream()
                    .collect(Collectors.toMap(Book::getTitle, Function.identity()));

            // Create loans (book, borrower, email, loaned N days ago, loan period, returned N days ago or null)
            List<Loan> loans = new ArrayList<>();
            Set<Book> lentBooks = new LinkedHashSet<>();
            Object[][] loanData = {
                    // Active
                    {"1984", "Emma Wilson", "emma.wilson@example.com", 3, 14, null},
                    {"Fahrenheit 451", "Liam Carter", "liam.carter@example.com", 5, 21, null},
                    {"The Da Vinci Code", "Sofia Martinez", "sofia.martinez@example.com", 2, 14, null},
                    {"The Da Vinci Code", "Noah Kim", "noah.kim@example.com", 6, 14, null},
                    {"Clean Code", "Ethan Clarke", "ethan.clarke@example.com", 10, 28, null},
                    {"The Hobbit", "Mia Novak", "mia.novak@example.com", 1, 14, null},
                    {"Project Hail Mary", "Olivia Brown", "olivia.brown@example.com", 4, 14, null},
                    // Overdue
                    {"Dune", "Lucas Rossi", "lucas.rossi@example.com", 30, 14, null},
                    {"Sapiens", "Emma Wilson", "emma.wilson@example.com", 25, 21, null},
                    {"Gone Girl", "Noah Kim", "noah.kim@example.com", 20, 14, null},
                    {"Effective Java", "Ethan Clarke", "ethan.clarke@example.com", 40, 28, null},
                    // Returned
                    {"Pride and Prejudice", "Mia Novak", "mia.novak@example.com", 40, 21, 22},
                    {"The Martian", "Liam Carter", "liam.carter@example.com", 35, 14, 21},
                    {"Educated", "Sofia Martinez", "sofia.martinez@example.com", 28, 14, 15},
                    {"Cosmos", "Olivia Brown", "olivia.brown@example.com", 18, 14, 3},
                    {"And Then There Were None", "Lucas Rossi", "lucas.rossi@example.com", 15, 14, 2},
            };
            LocalDate today = LocalDate.now();
            for (Object[] row : loanData) {
                Book book = byTitle.get((String) row[0]);
                LocalDate loanDate = today.minusDays((Integer) row[3]);
                Loan loan = new Loan(book, (String) row[1], (String) row[2], loanDate, loanDate.plusDays((Integer) row[4]));
                if (row[5] != null) {
                    loan.setReturnedAt(today.minusDays((Integer) row[5]));
                } else {
                    // Copies that are still out are not on the shelf
                    book.setCopiesAvailable(book.getCopiesAvailable() - 1);
                    lentBooks.add(book);
                }
                loans.add(loan);
            }
            bookRepository.saveAll(lentBooks);
            loanRepository.saveAll(loans);

            System.out.println("=== Database seeded with " +
                    bookRepository.count() + " books, " +
                    categoryRepository.count() + " categories and " +
                    loanRepository.count() + " loans ===");
        };
    }

    private Category category(String name, String description) {
        return categoryRepository.save(new Category(name, description));
    }

    private Book book(String title, String author, String description, String replacementCost, int copies,
                      String isbn, Category category, Tag... tags) {
        Book book = new Book(title, author, description, new BigDecimal(replacementCost), copies, isbn, "English");
        book.setCategory(category);
        book.setShelfLocation(shelfLocation(category, author));
        for (Tag tag : tags) {
            book.addTag(tag);
        }
        return book;
    }

    private Book inLanguage(String language, Book book) {
        book.setLanguage(language);
        return book;
    }

    // Section code + first three letters of the first author's surname + running number, e.g. FIC-ORW-01
    private String shelfLocation(Category category, String author) {
        String section = category == null ? "GEN" : SECTION_CODES.get(category.getName());
        String firstAuthor = author.split(",")[0].replace("et al.", "").replace("Jr.", "").trim();
        String[] names = firstAuthor.split("\\s+");
        String surname = Normalizer.normalize(names[names.length - 1], Normalizer.Form.NFD)
                .replaceAll("[^A-Za-z]", "")
                .toUpperCase();
        String prefix = section + "-" + surname.substring(0, Math.min(3, surname.length()));
        int number = shelfCounters.merge(prefix, 1, Integer::sum);
        return String.format("%s-%02d", prefix, number);
    }
}
