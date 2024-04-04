import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class betaVersion {
    private Map<String, Double> spamProbabilities;
    private Map<String, Double> hamProbabilities;
    private double spamPriorProbability;
    private double hamPriorProbability;
    private double smoothingFactor = 0.01;

    public void train(List<LabeledMessage> labeledMessages) {
        // Preprocess messages and calculate word frequencies
        Map<String, Integer> hamWordFrequencies = new HashMap<>();
        Map<String, Integer> spamWordFrequencies = new HashMap<>();
        int totalHamMessages = 0;
        int totalSpamMessages = 0;

        for (LabeledMessage labeledMessage : labeledMessages) {
            String message = labeledMessage.getMessage();
            boolean isSpam = labeledMessage.isSpam();

            Map<String, Integer> wordFrequencies = calculateWordFrequencies(message);

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

    public boolean predict(String message) {
        // Preprocess message and calculate probability of being spam
        double spamScore = calculateSpamScore(message);

        // Classify message as spam or not spam based on threshold
        return spamScore > 0.5; // You can adjust the threshold as needed
    }

    private double calculateSpamScore(String message) {
        // Tokenize message and calculate spam score using Naive Bayes
        double spamScore = Math.log(spamPriorProbability);
        double hamScore = Math.log(hamPriorProbability);

        Map<String, Integer> wordFrequencies = calculateWordFrequencies(message);
        for (Map.Entry<String, Integer> entry : wordFrequencies.entrySet()) {
            String word = entry.getKey();
            int frequency = entry.getValue();

            double spamProbability = spamProbabilities.getOrDefault(word, smoothingFactor);
            double hamProbability = hamProbabilities.getOrDefault(word, smoothingFactor);

            spamScore += frequency * Math.log(spamProbability);
            hamScore += frequency * Math.log(hamProbability);
        }

        return spamScore - hamScore;
    }

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

    private void incrementWordFrequencies(Map<String, Integer> dest, Map<String, Integer> src) {
        for (Map.Entry<String, Integer> entry : src.entrySet()) {
            String word = entry.getKey();
            int frequency = entry.getValue();
            dest.put(word, dest.getOrDefault(word, 0) + frequency);
        }
    }

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

    public static void main(String[] args) {
        betaVersion detector = new betaVersion();

        // Sample training data (list of labeled messages)
        List<LabeledMessage> labeledMessages = new ArrayList<>();
        labeledMessages.add(new LabeledMessage("I hope you're doing well", false));
        labeledMessages.add(new LabeledMessage("Let's meet for coffee tomorrow", false));
        labeledMessages.add(new LabeledMessage("Buy now and get 50% off", true));
        labeledMessages.add(new LabeledMessage("Congratulations! You've won a prize", true));

        // Train the model
        detector.train(labeledMessages);

        // Test the model with new messages
        String newMessage1 = "Get your free ebook today";
        boolean prediction1 = detector.predict(newMessage1);
        System.out.println("Is '" + newMessage1 + "' spam? " + prediction1);

        String newMessage2 = "Are you available for a call?";
        boolean prediction2 = detector.predict(newMessage2);
        System.out.println("Is '" + newMessage2 + "' spam? " + prediction2);
    }
}

class LabeledMessage {
    private String message;
    private boolean isSpam;

    public LabeledMessage(String message, boolean isSpam) {
        this.message = message;
        this.isSpam = isSpam;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSpam() {
        return isSpam;
    }
}
