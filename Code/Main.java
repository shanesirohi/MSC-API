package Code;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Scanner;

/**
 * Main class for spam detection.
 */
public class Main {

    // Instance variables for storing probabilities and settings

    /**
     * Map to store spam probabilities for each word.
     */
    private Map<String, Double> spamProbabilities;

    /**
     * Map to store ham probabilities for each word.
     */
    private Map<String, Double> hamProbabilities;

    /**
     * Prior probability of a message being spam.
     */
    private double spamPriorProbability;

    /**
     * Prior probability of a message being ham.
     */
    private double hamPriorProbability;

    /**
     * Smoothing factor for Laplace smoothing.
     */
    private double smoothingFactor = 0.01;

    /**
     * Trains the spam detection model.
     * 
     * @param labeledMessages List of labeled messages used for training.
     */
    public void train(List<LabeledMessage> labeledMessages) {
        // Preprocess messages and calculate word frequencies
        Map<String, Integer> hamWordFrequencies = new HashMap<>();
        Map<String, Integer> spamWordFrequencies = new HashMap<>();
        int totalHamMessages = 0;
        int totalSpamMessages = 0;

        // Loop through each labeled message to process and calculate word frequencies
        for (LabeledMessage labeledMessage : labeledMessages) {
            // Retrieve message and spam label
            String message = labeledMessage.getMessage();
            boolean isSpam = labeledMessage.isSpam();

            // Calculate word frequencies for the current message
            Map<String, Integer> wordFrequencies = calculateWordFrequencies(message);

            // Update word frequencies based on spam or ham label
            if (isSpam) {
                incrementWordFrequencies(spamWordFrequencies, wordFrequencies);
                totalSpamMessages++;
            } else {
                incrementWordFrequencies(hamWordFrequencies, wordFrequencies);
                totalHamMessages++;
            }
        }

        // Calculate prior probabilities
        int totalMessages = totalHamMessages + totalSpamMessages;
        hamPriorProbability = (double) totalHamMessages / totalMessages;
        spamPriorProbability = (double) totalSpamMessages / totalMessages;

        // Calculate conditional probabilities using Naive Bayes
        spamProbabilities = calculateConditionalProbabilities(spamWordFrequencies, totalSpamMessages);
        hamProbabilities = calculateConditionalProbabilities(hamWordFrequencies, totalHamMessages);
    }

    /**
     * Predicts whether a message is spam.
     * 
     * @param message The message to be classified.
     * @return true if the message is predicted to be spam, false otherwise.
     */
    public boolean predict(String message) {
        // Preprocess message and calculate probability of being spam
        double spamScore = calculateSpamScore(message);

        // Classify message as spam or not spam based on threshold
        return spamScore > 0.5; // You can adjust the threshold as needed
    }

    /**
     * Calculates the spam score of a given message.
     * 
     * @param message The message to calculate the spam score for.
     * @return The spam score of the message.
     */
    private double calculateSpamScore(String message) {
        // Tokenize message and calculate spam score using Naive Bayes
        double spamScore = Math.log(spamPriorProbability);
        double hamScore = Math.log(hamPriorProbability);

        // Calculate word frequencies for the message
        Map<String, Integer> wordFrequencies = calculateWordFrequencies(message);

        // Update scores based on word frequencies and conditional probabilities
        for (Map.Entry<String, Integer> entry : wordFrequencies.entrySet()) {
            String word = entry.getKey();
            int frequency = entry.getValue();

            // Retrieve spam and ham probabilities for the current word
            double spamProbability = spamProbabilities.getOrDefault(word, smoothingFactor);
            double hamProbability = hamProbabilities.getOrDefault(word, smoothingFactor);

            // Update scores based on word frequency and conditional probabilities
            spamScore += frequency * Math.log(spamProbability);
            hamScore += frequency * Math.log(hamProbability);
        }

        // Return the difference between spam and ham scores
        return spamScore - hamScore;
    }

    /**
     * Calculates word frequencies for a given message.
     * 
     * @param message The message to calculate word frequencies for.
     * @return A map containing word frequencies.
     */
    private Map<String, Integer> calculateWordFrequencies(String message) {
        // Perform preprocessing steps (e.g., tokenization, lowercasing, removing punctuation)
        // and return word frequencies as a map
        Map<String, Integer> wordFrequencies = new HashMap<>();
        String[] words = message.toLowerCase().split("\\W+");
        for (String word : words) {
            wordFrequencies.put(word, wordFrequencies.getOrDefault(word, 0) + 1);
        }
        return wordFrequencies;
    }

    /**
     * Increments word frequencies in the destination map with frequencies from the source map.
     * 
     * @param dest The destination map to increment word frequencies.
     * @param src The source map containing word frequencies to be added.
     */
    private void incrementWordFrequencies(Map<String, Integer> dest, Map<String, Integer> src) {
        for (Map.Entry<String, Integer> entry : src.entrySet()) {
            String word = entry.getKey();
            int frequency = entry.getValue();
            dest.put(word, dest.getOrDefault(word, 0) + frequency);
        }
    }

    /**
     * Calculates conditional probabilities based on word frequencies and total messages.
     * 
     * @param wordFrequencies Map containing word frequencies.
     * @param totalMessages Total number of messages.
     * @return Map containing conditional probabilities for words.
     */
    private Map<String, Double> calculateConditionalProbabilities(Map<String, Integer> wordFrequencies, int totalMessages) {
        Map<String, Double> conditionalProbabilities = new HashMap<>();
        int totalWords = wordFrequencies.values().stream().mapToInt(Integer::intValue).sum();
        for (Map.Entry<String, Integer> entry : wordFrequencies.entrySet()) {
            String word = entry.getKey();
            int frequency = entry.getValue();
            double probability = (frequency + smoothingFactor) / (totalWords + smoothingFactor * totalMessages);
            conditionalProbabilities.put(word, probability);
        }
        return conditionalProbabilities;
    }

    /**
     * Main method to run the spam detection program.
     * 
     * @param args Command-line arguments (not used in this program).
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Main detector = new Main();

        // Continuously prompt the user for input until they choose to exit
        while (true) {
            System.out.print("Enter any message (or type 'exit' to quit): ");
            String newMessage = scanner.nextLine();
            
            // Exit the program if the user types 'exit'
            if (newMessage.equalsIgnoreCase("exit")) {
                break;
            }

            // Import dataset from Dataset.java (assuming the dataset is loaded elsewhere)
            List<LabeledMessage> labeledMessages = dataset.getDataset();

            // Train the model
            detector.train(labeledMessages);

            // Predict whether the new message is spam or not
            boolean prediction = detector.predict(newMessage);
            System.out.println("Is it spam? " + prediction);
        }

        scanner.close();
    }
}

/**
 * Class representing a labeled message.
 */
class LabeledMessage {
    private String message;
    private boolean isSpam;

    /**
     * Constructs a LabeledMessage object with the given message and spam label.
     * 
     * @param message The message content.
     * @param isSpam Whether the message is spam (true) or not (false).
     */
    public LabeledMessage(String message, boolean isSpam) {
        this.message = message;
        this.isSpam = isSpam;
    }

    /**
     * Gets the message content.
     * 
     * @return The message content.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Checks if the message is spam.
     * 
     * @return True if the message is spam, false otherwise.
     */
    public boolean isSpam() {
        return isSpam;
    }
}
