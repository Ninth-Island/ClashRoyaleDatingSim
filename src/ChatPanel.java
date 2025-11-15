import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChatPanel extends JPanel {
    private JFrame parentFrame;
    private ClashCard matchedCard;
    private String userBio;
    private String userImagePath;
    private CharacterConversationSimulator chatSimulator;

    private JPanel messagesPanel;
    private JTextField messageField;
    private JButton sendButton;
    private JScrollPane scrollPane;

    private List<ChatMessage> messages = new ArrayList<>();
    private int characterMessageCount = 0;

    // Clash Royale colors
    private static final Color CLASH_BLUE = new Color(74, 144, 226);
    private static final Color CLASH_DARK_BLUE = new Color(45, 88, 167);
    private static final Color USER_BUBBLE_COLOR = new Color(74, 144, 226);
    private static final Color CHAR_BUBBLE_COLOR = new Color(245, 245, 245);
    private static final Color BACKGROUND_COLOR = new Color(255, 255, 255);

    public ChatPanel(JFrame parent, ClashCard card, String bio, String imagePath) {
        this.parentFrame = parent;
        this.matchedCard = card;
        this.userBio = bio;
        this.userImagePath = imagePath;

        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Initialize chat simulator (API key is hardcoded now)
        initializeChatSimulator();
        buildChatInterface();

        // Send initial greeting with photo if available
        sendInitialGreeting();
    }

    private void initializeChatSimulator() {
        // Create character personality based on card
        String characterPersonality = createCharacterPersonality();

        try {
            // API key is hardcoded in CharacterConversationSimulator, so we pass empty string
            chatSimulator = new CharacterConversationSimulator("", characterPersonality, userBio);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parentFrame,
                "Error initializing chat: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private String createCharacterPersonality() {
        String name = matchedCard.name;
        String personality = matchedCard.personality;
        String category = matchedCard.category;

        // Create a personality based on the card's characteristics
        return String.format(
            "You are %s from Clash Royale, a %s. Your personality can be described as: %s. " +
            "You are on a dating app and just matched with someone. " +
            "Be charming, witty, and stay in character based on your Clash Royale persona. " +
            "Keep responses relatively brief (2-3 sentences) as this is a chat conversation. " +
            "Show interest in getting to know your match.",
            name, category, personality
        );
    }

    private void buildChatInterface() {
        // Header with card info
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Messages area with background
        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setOpaque(false); // Make transparent to see background
        messagesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Wrapper panel to hold messages at the top
        JPanel messagesWrapper = new JPanel(new BorderLayout());
        messagesWrapper.setOpaque(false);
        messagesWrapper.add(messagesPanel, BorderLayout.NORTH);

        // Create background panel with Clash Royale wallpaper
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.add(messagesWrapper, BorderLayout.CENTER);

        scrollPane = new JScrollPane(backgroundPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);

        // Input area
        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(CLASH_BLUE);
        headerPanel.setPreferredSize(new Dimension(800, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Back button
        JButton backButton = new JButton("←");
        backButton.setFont(new Font("Arial", Font.BOLD, 24));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(CLASH_BLUE);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> goBackToSwipe());
        headerPanel.add(backButton, BorderLayout.WEST);

        // Center panel with image and name
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        centerPanel.setOpaque(false);

        // Card image (circular)
        JLabel imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(60, 60));
        try {
            String cleanImagePath = cleanImagePath(matchedCard.image);
            String[] possiblePaths = {
                "images/" + cleanImagePath,
                "src/images/" + cleanImagePath,
                "../images/" + cleanImagePath
            };

            Image cardImage = null;
            for (String path : possiblePaths) {
                File imageFile = new File(path);
                if (imageFile.exists()) {
                    cardImage = ImageIO.read(imageFile);
                    break;
                }
            }

            if (cardImage != null) {
                Image scaledImage = cardImage.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImage));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Name label
        JLabel nameLabel = new JLabel(matchedCard.name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        nameLabel.setForeground(Color.WHITE);

        centerPanel.add(imageLabel);
        centerPanel.add(nameLabel);
        headerPanel.add(centerPanel, BorderLayout.CENTER);

        return headerPanel;
    }

    private String cleanImagePath(String imagePath) {
        if (imagePath.startsWith("./")) {
            imagePath = imagePath.substring(2);
        }
        if (imagePath.startsWith("images/")) {
            imagePath = imagePath.substring(7);
        }
        return imagePath;
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Message input field
        messageField = new JTextField();
        messageField.setFont(new Font("Arial", Font.PLAIN, 16));
        messageField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        messageField.addActionListener(e -> sendMessage());

        // Send button
        sendButton = new JButton("Send");
        sendButton.setFont(new Font("Arial", Font.BOLD, 14));
        sendButton.setBackground(CLASH_BLUE);
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorderPainted(false);
        sendButton.setPreferredSize(new Dimension(80, 45));
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.addActionListener(e -> sendMessage());

        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        return inputPanel;
    }

    private void sendMessage() {
        String userMessage = messageField.getText().trim();
        if (userMessage.isEmpty()) {
            return;
        }

        // Disable input while processing
        messageField.setEnabled(false);
        sendButton.setEnabled(false);

        // Display user message
        addMessage(userMessage, true);
        messageField.setText("");

        // Send to Claude in background thread
        new Thread(() -> {
            try {
                CharacterConversationSimulator.CharacterResponse response =
                    chatSimulator.sendMessage(userMessage);

                // Update UI on EDT
                SwingUtilities.invokeLater(() -> {
                    addMessage(response.message, false);

                    // Check if date ended after 3 messages
                    if (response.dateFailed) {
                        // FAILURE CASE: User failed to romance the character
                        handleDateFailure();
                    } else if (characterMessageCount >= 3) {
                        // SUCCESS CASE: User successfully romanced the character
                        handleDateSuccess();
                    } else {
                        // Continue conversation - re-enable input
                        messageField.setEnabled(true);
                        sendButton.setEnabled(true);
                        messageField.requestFocus();
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(parentFrame,
                        "Error: " + e.getMessage(),
                        "Chat Error",
                        JOptionPane.ERROR_MESSAGE);
                    messageField.setEnabled(true);
                    sendButton.setEnabled(true);
                });
            }
        }).start();
    }

    private void addMessage(String text, boolean isUser) {
        messages.add(new ChatMessage(text, isUser));

        // Play deploy sound for character messages
        if (!isUser) {
            AudioPlayer.playDeploySound(matchedCard.name);
            characterMessageCount++;
        }

        JPanel messageBubble = createMessageBubble(text, isUser);
        messagesPanel.add(messageBubble);
        messagesPanel.add(Box.createVerticalStrut(8));

        messagesPanel.revalidate();
        messagesPanel.repaint();

        // Scroll to bottom
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    private JPanel createMessageBubble(String text, boolean isUser) {
        JPanel bubbleContainer = new JPanel(new FlowLayout(
            isUser ? FlowLayout.RIGHT : FlowLayout.LEFT, 10, 0));
        bubbleContainer.setOpaque(false);
        bubbleContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        // Create bubble
        JPanel bubble = new JPanel(new BorderLayout());
        bubble.setBackground(isUser ? USER_BUBBLE_COLOR : CHAR_BUBBLE_COLOR);
        bubble.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        // Message text
        JTextArea messageText = new JTextArea(text);
        messageText.setEditable(false);
        messageText.setLineWrap(true);
        messageText.setWrapStyleWord(true);
        messageText.setOpaque(false);
        messageText.setFont(new Font("Arial", Font.PLAIN, 15));
        messageText.setForeground(isUser ? Color.WHITE : Color.BLACK);

        // Set preferred width for text wrapping
        messageText.setSize(new Dimension(450, Integer.MAX_VALUE));
        Dimension preferredSize = messageText.getPreferredSize();
        messageText.setPreferredSize(new Dimension(
            Math.min(450, preferredSize.width),
            preferredSize.height
        ));

        bubble.add(messageText, BorderLayout.CENTER);

        // Make bubble rounded
        bubble.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(20, isUser ? USER_BUBBLE_COLOR : CHAR_BUBBLE_COLOR, 0),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        bubbleContainer.add(bubble);
        return bubbleContainer;
    }

    private void goBackToSwipe() {
        // Go back to matches or swiping
        JOptionPane.showMessageDialog(parentFrame,
            "Chat session ended",
            "Info",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void sendInitialGreeting() {
        // Send initial greeting in background thread
        new Thread(() -> {
            try {
                String greeting;
                if (userImagePath != null && !userImagePath.isEmpty()) {
                    // Get greeting with photo analysis
                    greeting = chatSimulator.getInitialGreetingWithPhoto(userImagePath);
                } else {
                    // Get simple greeting without photo
                    greeting = chatSimulator.getInitialGreeting();
                }

                // Display greeting on EDT
                SwingUtilities.invokeLater(() -> {
                    addMessage(greeting, false);
                });
            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    addMessage("Hey! I'm excited to chat with you!", false);
                });
            }
        }).start();
    }

    private void handleDateFailure() {
        // Disable input - date is over
        messageField.setEnabled(false);
        sendButton.setEnabled(false);

        // Play crying king sound
        AudioPlayer.playCryingKing();

        // Show failure message and restart after delay
        new Thread(() -> {
            try {
                Thread.sleep(2000); // Wait 2 seconds for crying sound
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(parentFrame,
                        "You failed to romance " + matchedCard.name + "!\n" +
                        "Better luck next time in the Arena!",
                        "Date Failed",
                        JOptionPane.INFORMATION_MESSAGE);

                    // Restart to bio entry
                    restartToBioEntry();
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleDateSuccess() {
        // Disable input - date is over
        messageField.setEnabled(false);
        sendButton.setEnabled(false);

        // Get final "asking out" message in background
        new Thread(() -> {
            try {
                String askOutMessage = chatSimulator.getFinalAskOutMessage();

                // Display the final message
                SwingUtilities.invokeLater(() -> {
                    addMessage(askOutMessage, false);
                });

                // Wait a few seconds, then restart
                Thread.sleep(3000);
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(parentFrame,
                        "Success! You romanced " + matchedCard.name + "!\n" +
                        "They want to see you again!",
                        "Date Success",
                        JOptionPane.INFORMATION_MESSAGE);

                    // Restart to bio entry
                    restartToBioEntry();
                });
            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    restartToBioEntry();
                });
            }
        }).start();
    }

    private void restartToBioEntry() {
        parentFrame.getContentPane().removeAll();
        parentFrame.add(new ProfileInputPanel(parentFrame));
        parentFrame.revalidate();
        parentFrame.repaint();
    }

    // Simple message class
    private static class ChatMessage {
        String text;
        boolean isUser;

        ChatMessage(String text, boolean isUser) {
            this.text = text;
            this.isUser = isUser;
        }
    }

    // Rounded border class (reused from SwipePanel)
    private static class RoundedBorder extends javax.swing.border.AbstractBorder {
        private int radius;
        private Color color;
        private int thickness;

        RoundedBorder(int radius, Color color, int thickness) {
            this.radius = radius;
            this.color = color;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (thickness > 0) {
                g2d.setColor(color);
                g2d.setStroke(new BasicStroke(thickness));
                g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            }

            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = insets.top = insets.bottom = thickness;
            return insets;
        }
    }

    // Background panel with Clash Royale wallpaper
    private static class BackgroundPanel extends JPanel {
        private Image backgroundImage;
        private static final float IMAGE_OPACITY = 0.15f;

        public BackgroundPanel() {
            setOpaque(true);
            setBackground(BACKGROUND_COLOR);
            loadBackgroundImage();
        }

        private void loadBackgroundImage() {
            // Try to load one of the nice wallpaper images
            String[] wallpaperPaths = {
                "resources/stadium-clash-royale-4k-ec.jpg",
                "resources/1124066.jpg",
                "resources/clash-royale_nten.jpg",
                "src/resources/stadium-clash-royale-4k-ec.jpg",
                "src/resources/1124066.jpg",
                "src/resources/clash-royale_nten.jpg"
            };

            for (String path : wallpaperPaths) {
                try {
                    File imageFile = new File(path);
                    if (imageFile.exists()) {
                        backgroundImage = ImageIO.read(imageFile);
                        break;
                    }
                } catch (Exception e) {
                    // Continue trying other paths
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (backgroundImage == null) {
                return;
            }

            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int panelWidth = getWidth();
            int panelHeight = getHeight();

            // Scale image to cover the panel while maintaining aspect ratio
            int imgWidth = backgroundImage.getWidth(null);
            int imgHeight = backgroundImage.getHeight(null);

            double panelRatio = (double) panelWidth / panelHeight;
            double imgRatio = (double) imgWidth / imgHeight;

            int drawWidth, drawHeight, drawX, drawY;

            if (panelRatio > imgRatio) {
                // Panel is wider, scale based on width
                drawWidth = panelWidth;
                drawHeight = (int) (panelWidth / imgRatio);
                drawX = 0;
                drawY = (panelHeight - drawHeight) / 2;
            } else {
                // Panel is taller, scale based on height
                drawHeight = panelHeight;
                drawWidth = (int) (panelHeight * imgRatio);
                drawX = (panelWidth - drawWidth) / 2;
                drawY = 0;
            }

            // Draw image with opacity
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, IMAGE_OPACITY));
            g2d.drawImage(backgroundImage, drawX, drawY, drawWidth, drawHeight, null);

            g2d.dispose();
        }
    }
}
