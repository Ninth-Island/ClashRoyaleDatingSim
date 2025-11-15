import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;


public class CharacterConversationSimulator {

    private static final String API_URL = "https://api.anthropic.com/v1/messages";
    private static final String API_KEY = "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";
    private static final String ANTHROPIC_VERSION = "2023-06-01";

    private String characterPersonality;
    private String userBio;
    private List<JSONObject> conversationHistory;
    private UserProfile userP = new UserProfile();

    public CharacterConversationSimulator(String apiKey, String characterPersonality, String userBio) {
        // API key is now hardcoded, but we keep the parameter for compatibility
        this.characterPersonality = characterPersonality;
        this.userBio = userBio;
        this.conversationHistory = new ArrayList<>();
    }

    /**
     * Represents a response from the character including their message and score
     */
    public static class CharacterResponse {
        public String message;
        public int score;

        public CharacterResponse(String message, int score) {
            this.message = message;
            this.score = score;
        }

        @Override
        public String toString() {
            return "Message: " + message + "\nScore: " + score + "/10";
        }
    }

    /**
     * Sends user statement to Claude and gets character response with score
     * @param userStatement What the user said
     * @return CharacterResponse containing the character's reply and user's score
     * @throws Exception if the API call fails
     */
    public CharacterResponse sendMessage(String userStatement) throws Exception {
        // Add user message to history
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", userStatement);
        conversationHistory.add(userMessage);

        // Get character's response
        String characterReply = getCharacterReply();

        // Add character's response to history
        JSONObject assistantMessage = new JSONObject();
        assistantMessage.put("role", "assistant");
        assistantMessage.put("content", characterReply);
        conversationHistory.add(assistantMessage);

        // Get score for user's statement
        int score = scoreUserStatement(userStatement, characterReply);

        return new CharacterResponse(characterReply, score);
    }

    /**
     * Gets the character's response based on conversation history
     */
    private String getCharacterReply() throws Exception {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "claude-haiku-4-5-20251001");
        requestBody.put("max_tokens", 500);

        // Create system message with character personality
        String systemPrompt = "You are roleplaying as a character with this personality: " + characterPersonality + "\n" +
                "You are on a date with someone who has this bio: " + userBio + "\n" +
                "Stay in character at all times. Be engaging and respond naturally to what they say. " +
                "Keep responses relatively brief (2-3 sentences) as this is a chat conversation.";

        requestBody.put("system", systemPrompt);

        // Convert conversation history to JSONArray
        JSONArray messages = new JSONArray();
        for (JSONObject msg : conversationHistory) {
            messages.put(msg);
        }

        requestBody.put("messages", messages);

        return makeAPICall(requestBody);
    }

    /**
     * Scores the user's statement based on how well it resonates with the character
     */
    private int scoreUserStatement(String userStatement, String characterReply) throws Exception {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "claude-haiku-4-5-20251001");
        requestBody.put("max_tokens", 10);

        // Create scoring prompt as system message
        String systemPrompt = "You are evaluating a dating conversation. Rate the user's statement on a scale of 0-10 based on:\n" +
                "- How appropriate and engaging it is\n" +
                "- How well it matches the character's personality and interests\n" +
                "- Social skills and charm\n" +
                "- Romantic potential\n\n" +
                "Respond with ONLY a single number from 0 to 10.";

        requestBody.put("system", systemPrompt);

        // Create user message with context
        JSONArray messages = new JSONArray();
        JSONObject scoringMessage = new JSONObject();
        scoringMessage.put("role", "user");
        scoringMessage.put("content",
                "Character Personality: " + characterPersonality + "\n" +
                "User Bio: " + userBio + "\n" +
                "User Said: \"" + userStatement + "\"\n" +
                "Character Responded: \"" + characterReply + "\"\n\n" +
                "Rate this from 0-10:"
        );
        messages.put(scoringMessage);

        requestBody.put("messages", messages);

        String response = makeAPICall(requestBody);

        try {
            // Extract just the number from the response
            String trimmed = response.trim().replaceAll("[^0-9]", "");
            int scoreValue = Integer.parseInt(trimmed);
            this.userP.updateScore(scoreValue);
            return this.userP.getScore();
        } catch (NumberFormatException e) {
            // Default to 5 if parsing fails
            System.err.println("Warning: Could not parse score, defaulting to 5. Response was: " + response);
            return 5;
        }
    }

    /**
     * Makes the actual API call to Anthropic Claude
     */
    private String makeAPICall(JSONObject requestBody) throws Exception {
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("x-api-key", API_KEY);
        connection.setRequestProperty("anthropic-version", ANTHROPIC_VERSION);
        connection.setDoOutput(true);

        // Send request
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Read response
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            // Parse JSON response - Claude's format
            JSONObject jsonResponse = new JSONObject(response.toString());

            // Claude returns content as an array of content blocks
            JSONArray contentArray = jsonResponse.getJSONArray("content");
            String reply = contentArray.getJSONObject(0).getString("text");

            return reply;
        } else {
            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(connection.getErrorStream(), "utf-8"));
            StringBuilder errorResponse = new StringBuilder();
            String line;

            while ((line = errorReader.readLine()) != null) {
                errorResponse.append(line);
            }
            errorReader.close();

            throw new Exception("API Error (Code " + responseCode + "): " + errorResponse.toString());
        }
    }

    /**
     * Gets initial greeting from character based on user's photo
     */
    public String getInitialGreetingWithPhoto(String imagePath) throws Exception {
        // Read and encode image
        String base64Image = encodeImageToBase64(imagePath);
        String mediaType = getMediaType(imagePath);

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "claude-haiku-4-5-20251001");
        requestBody.put("max_tokens", 500);

        // Create system message
        String systemPrompt = "You are roleplaying as a character with this personality: " + characterPersonality + "\n" +
                "You are on a dating app and just matched with someone who has this bio: " + userBio + "\n" +
                "You just saw their profile picture. Comment on their appearance, demeanor, and what you notice in the background. " +
                "Be charming, witty, and flirty. Keep it brief (2-3 sentences). Stay in character.";

        requestBody.put("system", systemPrompt);

        // Create message with image
        JSONArray messages = new JSONArray();
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");

        // Content array with image and text
        JSONArray content = new JSONArray();

        // Image content
        JSONObject imageContent = new JSONObject();
        imageContent.put("type", "image");
        JSONObject imageSource = new JSONObject();
        imageSource.put("type", "base64");
        imageSource.put("media_type", mediaType);
        imageSource.put("data", base64Image);
        imageContent.put("source", imageSource);
        content.put(imageContent);

        // Text content
        JSONObject textContent = new JSONObject();
        textContent.put("type", "text");
        textContent.put("text", "This is my profile picture. What do you think?");
        content.put(textContent);

        userMessage.put("content", content);
        messages.put(userMessage);

        requestBody.put("messages", messages);

        return makeAPICall(requestBody);
    }

    /**
     * Gets simple initial greeting without photo
     */
    public String getInitialGreeting() throws Exception {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "claude-haiku-4-5-20251001");
        requestBody.put("max_tokens", 500);

        String systemPrompt = "You are roleplaying as a character with this personality: " + characterPersonality + "\n" +
                "You are on a dating app and just matched with someone who has this bio: " + userBio + "\n" +
                "Start the conversation with a brief, charming greeting (2-3 sentences). Stay in character.";

        requestBody.put("system", systemPrompt);

        JSONArray messages = new JSONArray();
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", "Say hello!");
        messages.put(userMessage);

        requestBody.put("messages", messages);

        return makeAPICall(requestBody);
    }

    /**
     * Encodes image file to base64 string
     */
    private String encodeImageToBase64(String imagePath) throws Exception {
        File imageFile = new File(imagePath);
        FileInputStream fileInputStream = new FileInputStream(imageFile);
        byte[] imageBytes = new byte[(int) imageFile.length()];
        fileInputStream.read(imageBytes);
        fileInputStream.close();
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    /**
     * Gets media type from file extension
     */
    private String getMediaType(String imagePath) {
        String lower = imagePath.toLowerCase();
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".webp")) return "image/webp";
        return "image/jpeg"; // default
    }

    /**
     * Resets the conversation while keeping character and user info
     */
    public void resetConversation() {
        conversationHistory.clear();
    }

    // Example usage
    public static void main(String[] args) {
        try {
            // Define character personality
            String characterPersonality = "A fierce but kind knight from Clash Royale. " +
                    "You are brave, honorable, and love talking about battles and strategy. " +
                    "You have a soft side and appreciate genuine conversation. " +
                    "You value courage and loyalty.";

            // Define user bio
            String userBio = "A strategy game enthusiast who enjoys deep conversations " +
                    "and has a good sense of humor.";

            // Create simulator (API key parameter is ignored now)
            CharacterConversationSimulator simulator =
                    new CharacterConversationSimulator("", characterPersonality, userBio);

            // Simulate conversation
            System.out.println("=== CLASH ROYALE DATING SIMULATOR (powered by Claude) ===\n");

            // Message 1
            String userMsg1 = "Hi! I've heard you're quite the warrior. What's your favorite battle strategy?";
            System.out.println("You: " + userMsg1);
            CharacterResponse response1 = simulator.sendMessage(userMsg1);
            System.out.println("Knight: " + response1.message);
            System.out.println("Your Score: " + response1.score + "/10\n");

            // Message 2
            String userMsg2 = "That sounds amazing! I really admire your bravery and tactical thinking.";
            System.out.println("You: " + userMsg2);
            CharacterResponse response2 = simulator.sendMessage(userMsg2);
            System.out.println("Knight: " + response2.message);
            System.out.println("Your Score: " + response2.score + "/10\n");

            // Message 3
            String userMsg3 = "Want to grab some elixir together sometime?";
            System.out.println("You: " + userMsg3);
            CharacterResponse response3 = simulator.sendMessage(userMsg3);
            System.out.println("Knight: " + response3.message);
            System.out.println("Your Score: " + response3.score + "/10\n");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
