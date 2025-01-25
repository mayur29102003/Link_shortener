
import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

public class LinkShortener {
    private static final String BASE_URL = "http://short.ly/";
    private HashMap<String, String> urlMap; // Maps short URLs to long URLs
    private HashMap<String, String> reverseMap; // Maps long URLs to short URLs
    private static final String DATA_FILE = "url_mappings.txt"; // File to store mappings

    // Constructor
    public LinkShortener() {
        urlMap = new HashMap<>();
        reverseMap = new HashMap<>();
        loadMappings(); // Load mappings from file at startup
    }

    // Method to shorten a URL
    public String shortenURL(String longURL) {
        if (reverseMap.containsKey(longURL)) {
            return BASE_URL + reverseMap.get(longURL); // Return existing short URL
        }

        // Generate a unique short key
        String shortKey = Integer.toHexString(longURL.hashCode());

        // Handle collisions
        while (urlMap.containsKey(shortKey)) {
            longURL += System.currentTimeMillis(); // Modify URL slightly
            shortKey = Integer.toHexString(longURL.hashCode());
        }

        // Add mappings
        urlMap.put(shortKey, longURL);
        reverseMap.put(longURL, shortKey);

        return BASE_URL + shortKey;
    }

    // Method to expand a short URL
    public String expandURL(String shortURL) {
        String shortKey = shortURL.replace(BASE_URL, "");
        if (urlMap.containsKey(shortKey)) {
            return urlMap.get(shortKey);
        } else {
            return "Error: Invalid short URL!";
        }
    }

    // Save mappings to a file
    public void saveMappings() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE))) {
            for (String shortKey : urlMap.keySet()) {
                writer.println(shortKey + "," + urlMap.get(shortKey));
            }
            System.out.println("Mappings saved successfully!");
        } catch (IOException e) {
            System.out.println("Error saving mappings: " + e.getMessage());
        }
    }

    // Load mappings from a file
    private void loadMappings() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    String shortKey = parts[0];
                    String longURL = parts[1];
                    urlMap.put(shortKey, longURL);
                    reverseMap.put(longURL, shortKey);
                }
            }
            System.out.println("Mappings loaded successfully!");
        } catch (FileNotFoundException e) {
            System.out.println("No existing mappings found. Starting fresh.");
        } catch (IOException e) {
            System.out.println("Error loading mappings: " + e.getMessage());
        }
    }

    // Display all mappings
    public void displayMappings() {
        System.out.println("\n=== URL Mappings ===");
        for (String shortKey : urlMap.keySet()) {
            System.out.println(BASE_URL + shortKey + " -> " + urlMap.get(shortKey));
        }
    }

    // Main method (CLI)
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LinkShortener shortener = new LinkShortener();

        while (true) {
            System.out.println("\n=== Link Shortener ===");
            System.out.println("1. Shorten URL");
            System.out.println("2. Expand URL");
            System.out.println("3. View Mappings");
            System.out.println("4. Save Mappings");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline


switch (choice) {
                case 1:
                    System.out.print("Enter the long URL: ");
                    String longURL = scanner.nextLine();
                    String shortURL = shortener.shortenURL(longURL);
                    System.out.println("Shortened URL: " + shortURL);
                    break;

                case 2:
                    System.out.print("Enter the short URL: ");
                    String shortInput = scanner.nextLine();
                    String originalURL = shortener.expandURL(shortInput);
                    System.out.println("Original URL: " + originalURL);
                    break;

                case 3:
                    shortener.displayMappings();
                    break;

                case 4:
                    shortener.saveMappings();
                    break;

                case 5:
                    shortener.saveMappings(); // Save before exiting
                    System.out.println("Exiting... Goodbye!");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }
}