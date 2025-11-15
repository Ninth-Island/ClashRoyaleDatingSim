import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.*;
import java.util.List;
import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

class SwipePanel extends JPanel {
    private String userName;
    private String userBio;
    private List<ClashCard> cards;
    private int currentIndex = 0;
    private List<ClashCard> matches;
    private Image pekkaImage;
    private ImageIcon cryingGif;
    private java.util.List<Image> backgroundImages;

    // User preferences
    private int preferredHeightFeet;
    private int preferredHeightInches;
    private String preferredAttackRange;
    private int preferredFreeTime;
    private int preferredHumanness;
    private String sexualPreference;

    // Clash Royale colors
    private static final Color CLASH_BLUE = new Color(74, 144, 226);
    private static final Color CLASH_DARK_BLUE = new Color(45, 88, 167);
    private static final Color CLASH_GOLD = new Color(255, 183, 77);
    private static final Color CARD_BG = new Color(255, 255, 255, 250);

    public SwipePanel(JFrame parent, String name, String bio, int heightFeet, int heightInches,
                      String attackRange, int freeTime, int humanness, String preference) {
        this.userName = name;
        this.userBio = bio;
        this.preferredHeightFeet = heightFeet;
        this.preferredHeightInches = heightInches;
        this.preferredAttackRange = attackRange;
        this.preferredFreeTime = freeTime;
        this.preferredHumanness = humanness;
        this.sexualPreference = preference;
        this.matches = new ArrayList<>();

        setLayout(new BorderLayout(0, 0));

        // Load images
        loadImages();

        // Check for instant Mega Knight match (Gay preference)
        if (sexualPreference.equals("Gay")) {
            initializeCardsForMegaKnight();
            showInstantMegaKnightMatch(parent);
            return;
        }

        // Initialize cards based on preference
        initializeCards();

        // Show current card
        showCurrentCard(parent);
    }

    private void loadImages() {
        backgroundImages = new ArrayList<>();
        try {
            pekkaImage = ImageIO.read(getClass().getResourceAsStream("/resources/6f9b3f2201c9af83c69aeb604f64e25d.jpg"));
            cryingGif = new ImageIcon(getClass().getResource("/resources/crying.gif"));

            // Load background images
            String[] imageFiles = {
                "/resources/6f9b3f2201c9af83c69aeb604f64e25d.jpg",
                "/resources/clash-royale_nten.jpg",
                "/resources/in-a-realistic-sense-if-all-the-troops-from-clash-royale-v0-jlghikjobhpf1.png.webp",
                "/resources/explained-when-did-clash-royale-come-out-release-date-history.jpg",
                "/resources/clashroyale-1653673820137.jpg.webp"
            };

            for (String imagePath : imageFiles) {
                try {
                    Image img = ImageIO.read(getClass().getResourceAsStream(imagePath));
                    if (img != null) {
                        backgroundImages.add(img);
                    }
                } catch (Exception e) {
                    System.err.println("Could not load: " + imagePath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Draw gradient background
        GradientPaint gradient = new GradientPaint(0, 0, CLASH_BLUE, 0, height, CLASH_DARK_BLUE);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);

        // Draw diagonal pattern
        g2d.setColor(new Color(255, 255, 255, 10));
        for (int i = -height; i < width; i += 30) {
            g2d.drawLine(i, 0, i + height, height);
        }

        // Scatter small images
        if (!backgroundImages.isEmpty()) {
            int[][] positions = {
                // Left side - top to bottom
                {-60, 80, 120, 28, -15},
                {-45, 220, 100, 22, 10},
                {-35, 360, 110, 25, -8},
                {-50, 520, 95, 20, 12},
                {-40, 680, 105, 23, -10},
                {-55, height - 150, 90, 18, 8},

                // Right side - top to bottom
                {width - 140, 90, 115, 26, 18},
                {width - 120, 240, 105, 24, -12},
                {width - 130, 380, 100, 21, 14},
                {width - 115, 540, 110, 23, -16},
                {width - 125, 690, 95, 19, 10},
                {width - 110, height - 170, 100, 22, -11},

                // Additional scattered in corners
                {20, 50, 85, 16, -20},
                {width - 70, 60, 80, 15, 22},
                {15, height - 100, 90, 17, 15},
                {width - 75, height - 110, 85, 16, -18}
            };

            for (int i = 0; i < positions.length; i++) {
                Image img = backgroundImages.get(i % backgroundImages.size());
                int[] pos = positions[i];
                int x = pos[0];
                int y = pos[1];
                int size = pos[2];
                float opacity = pos[3] / 100f;
                double rotation = Math.toRadians(pos[4]);

                int imgWidth = (int) (img.getWidth(null) * size / (double) img.getHeight(null));
                int imgHeight = size;

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
                g2d.rotate(rotation, x + imgWidth/2, y + imgHeight/2);
                g2d.drawImage(img, x, y, imgWidth, imgHeight, this);
                g2d.rotate(-rotation, x + imgWidth/2, y + imgHeight/2);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }
        }

        // Vignette
        RadialGradientPaint vignette = new RadialGradientPaint(
            width/2f, height/2f, width/1.5f,
            new float[]{0.0f, 1.0f},
            new Color[]{new Color(0, 0, 0, 0), new Color(0, 0, 0, 80)}
        );
        g2d.setPaint(vignette);
        g2d.fillRect(0, 0, width, height);
    }

    private void initializeCards() {
        cards = new ArrayList<>();

        try {
            // Load JSON file - try multiple paths
            String content = null;
            String[] possiblePaths = {
                "clash_royale_cards_updated.json",         // When running from src directory
                "src/clash_royale_cards_updated.json",     // When running from root
                "../clash_royale_cards_updated.json"
            };

            for (String path : possiblePaths) {
                File jsonFile = new File(path);
                if (jsonFile.exists()) {
                    content = new String(Files.readAllBytes(Paths.get(path)));
                    System.out.println("Loaded JSON from: " + path);
                    break;
                }
            }

            if (content == null) {
                System.err.println("Could not find clash_royale_cards_updated.json");
                return;
            }

            // Simple JSON parsing - split by card objects
            String[] cardStrings = content.split("\\},\\s*\\{");

            for (String cardStr : cardStrings) {
                // Clean up the string
                cardStr = cardStr.replace("[", "").replace("]", "").replace("{", "").replace("}", "");

                // Parse fields
                String cardName = extractJsonString(cardStr, "card_name");
                String image = extractJsonString(cardStr, "image");
                String height = extractJsonString(cardStr, "height");
                String attackRange = extractJsonString(cardStr, "attack_range");
                double freeTime = extractJsonDouble(cardStr, "free_time");
                double humanoidScore = extractJsonDouble(cardStr, "humanoid_score");
                String category = extractJsonString(cardStr, "category");
                String personality = extractJsonString(cardStr, "personality");

                // Filter based on sexual preference
                if (sexualPreference.equals("Something Else")) {
                    // Only show buildings
                    if (!category.equals("building")) {
                        continue;
                    }
                } else {
                    // For "Straight" - only show troops (exclude buildings and mega knight)
                    if (category.equals("building") || category.equals("mega knight")) {
                        continue;
                    }
                }

                ClashCard card = new ClashCard(cardName, image, height, attackRange,
                                               freeTime, humanoidScore, category, personality);
                cards.add(card);
            }

            Collections.shuffle(cards);
            System.out.println("Loaded " + cards.size() + " cards from JSON (filtered by preference: " + sexualPreference + ")");

        } catch (Exception e) {
            System.err.println("Error loading cards from JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeCardsForMegaKnight() {
        cards = new ArrayList<>();

        try {
            // Load JSON file - try multiple paths
            String content = null;
            String[] possiblePaths = {
                "clash_royale_cards_updated.json",
                "src/clash_royale_cards_updated.json",
                "../clash_royale_cards_updated.json"
            };

            for (String path : possiblePaths) {
                File jsonFile = new File(path);
                if (jsonFile.exists()) {
                    content = new String(Files.readAllBytes(Paths.get(path)));
                    break;
                }
            }

            if (content == null) {
                System.err.println("Could not find clash_royale_cards_updated.json");
                return;
            }

            // Find Mega Knight
            String[] cardStrings = content.split("\\},\\s*\\{");

            for (String cardStr : cardStrings) {
                cardStr = cardStr.replace("[", "").replace("]", "").replace("{", "").replace("}", "");

                String cardName = extractJsonString(cardStr, "card_name");
                if (cardName.equals("Mega Knight")) {
                    String image = extractJsonString(cardStr, "image");
                    String height = extractJsonString(cardStr, "height");
                    String attackRange = extractJsonString(cardStr, "attack_range");
                    double freeTime = extractJsonDouble(cardStr, "free_time");
                    double humanoidScore = extractJsonDouble(cardStr, "humanoid_score");
                    String category = extractJsonString(cardStr, "category");
                    String personality = extractJsonString(cardStr, "personality");

                    ClashCard megaKnight = new ClashCard(cardName, image, height, attackRange,
                                                          freeTime, humanoidScore, category, personality);
                    matches.add(megaKnight);
                    System.out.println("Instant match with Mega Knight!");
                    break;
                }
            }

        } catch (Exception e) {
            System.err.println("Error loading Mega Knight: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String extractJsonString(String json, String key) {
        try {
            String pattern = "\"" + key + "\":\\s*\"([^\"]+)\"";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(json);
            if (m.find()) {
                return m.group(1);
            }
        } catch (Exception e) {
            System.err.println("Error extracting " + key + ": " + e.getMessage());
        }
        return "";
    }

    private double extractJsonDouble(String json, String key) {
        try {
            String pattern = "\"" + key + "\":\\s*([0-9.]+)";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(json);
            if (m.find()) {
                return Double.parseDouble(m.group(1));
            }
        } catch (Exception e) {
            System.err.println("Error extracting " + key + ": " + e.getMessage());
        }
        return 0.0;
    }

    private String generateBio(ClashCard card) {
        // Simple bio based on personality and stats
        String bio = "A " + card.personality.toLowerCase() + " " + card.category + " from the Arena. ";

        if (card.humanoidScore >= 0.9) {
            bio += "Very humanoid and relatable. ";
        } else if (card.humanoidScore >= 0.6) {
            bio += "Somewhat humanoid. ";
        } else {
            bio += "Unique and mysterious. ";
        }

        if (card.freeTime >= 0.6) {
            bio += "Has plenty of free time for dates!";
        } else if (card.freeTime >= 0.4) {
            bio += "Balances work and personal life.";
        } else {
            bio += "Very busy, but makes time for the right person.";
        }

        return bio;
    }

    private void showCurrentCard(JFrame parent) {
        removeAll();
        setLayout(new BorderLayout(0, 0));

        if (currentIndex >= cards.size()) {
            showMatches(parent);
            return;
        }

        ClashCard card = cards.get(currentIndex);

        // Title
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        JLabel titleLabel = new JLabel("Find Your Match");
        titleLabel.setFont(Fonts.ClashFontLarge);
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel);
        add(topPanel, BorderLayout.NORTH);

        // Card in center
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        // Create the outer card panel with fixed size
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(CARD_BG);
        cardPanel.setBorder(new RoundedBorder(15, CLASH_GOLD, 3));
        cardPanel.setPreferredSize(new Dimension(350, 600));

        // Create scrollable content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_BG);

        // Profile photo
        JLabel photoLabel = new JLabel();
        photoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        try {
            // Load card's specific image - try multiple paths
            Image cardImage = null;

            String[] possiblePaths = {
                "images/" + card.image,                    // When running from src directory
                "src/images/" + card.image,                // When running from root
                "../images/" + card.image
            };

            for (String path : possiblePaths) {
                File imageFile = new File(path);
                if (imageFile.exists()) {
                    cardImage = ImageIO.read(imageFile);
                    if (cardImage != null) {
                        break;
                    }
                }
            }

            if (cardImage != null) {
                // Scale image while maintaining aspect ratio
                int maxSize = 200;
                int originalWidth = cardImage.getWidth(null);
                int originalHeight = cardImage.getHeight(null);

                double scale = Math.min((double) maxSize / originalWidth, (double) maxSize / originalHeight);
                int scaledWidth = (int) (originalWidth * scale);
                int scaledHeight = (int) (originalHeight * scale);

                Image scaled = cardImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                photoLabel.setIcon(new ImageIcon(scaled));
            } else {
                System.err.println("Warning: Could not load image for " + card.name + ": " + card.image);
                // Show a placeholder text
                photoLabel.setText("[" + card.name + "]");
                photoLabel.setFont(Fonts.ClashFontLarge);
                photoLabel.setForeground(Color.GRAY);
            }
        } catch (Exception e) {
            System.err.println("ERROR loading image for " + card.name + ": " + e.getMessage());
            e.printStackTrace();
        }
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(photoLabel);
        contentPanel.add(Box.createVerticalStrut(15));

        // Name
        JLabel nameLabel = new JLabel(card.name);
        nameLabel.setFont(Fonts.ClashFontLarge);
        nameLabel.setForeground(CLASH_DARK_BLUE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(nameLabel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Bio - generate based on personality
        String bio = generateBio(card);
        JTextArea bioArea = new JTextArea(bio);
        bioArea.setWrapStyleWord(true);
        bioArea.setLineWrap(true);
        bioArea.setEditable(false);
        bioArea.setOpaque(false);
        bioArea.setFont(Fonts.ClashFontSmall);
        bioArea.setForeground(Color.DARK_GRAY);

        // Set a fixed width and let height expand
        bioArea.setSize(new Dimension(300, Integer.MAX_VALUE));
        Dimension d = bioArea.getPreferredSize();
        bioArea.setPreferredSize(new Dimension(300, d.height));

        bioArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel bioPanel = new JPanel();
        bioPanel.setOpaque(false);
        bioPanel.setLayout(new BorderLayout());
        bioPanel.add(bioArea, BorderLayout.CENTER);
        bioPanel.setMaximumSize(new Dimension(320, Integer.MAX_VALUE));
        contentPanel.add(bioPanel);
        contentPanel.add(Box.createVerticalStrut(15));

        // Stats section
        JPanel statsPanel = new JPanel(new GridLayout(6, 1, 5, 8));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(320, 180));

        statsPanel.add(createStatLabel("Height: " + card.heightFeet + "'" + card.heightInches + "\""));
        statsPanel.add(createStatLabel("Attack Range: " + card.attackRange));
        statsPanel.add(createStatLabel("Free Time: " + String.format("%.0f%%", card.freeTime * 100)));
        statsPanel.add(createStatLabel("Humanoid Score: " + String.format("%.0f%%", card.humanoidScore * 100)));
        statsPanel.add(createStatLabel("Category: " + card.category));
        statsPanel.add(createStatLabel("Personality: " + card.personality));

        JPanel statsPanelWrapper = new JPanel();
        statsPanelWrapper.setOpaque(false);
        statsPanelWrapper.add(statsPanel);
        contentPanel.add(statsPanelWrapper);
        contentPanel.add(Box.createVerticalStrut(20));

        // Wrap content in scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        cardPanel.add(scrollPane, BorderLayout.CENTER);

        centerWrapper.add(cardPanel);
        add(centerWrapper, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setOpaque(false);

        JButton passButton = createStyledButton("PASS", new Color(220, 53, 69));
        passButton.addActionListener(e -> swipe(parent, false));

        JButton likeButton = createStyledButton("LIKE", new Color(40, 167, 69));
        likeButton.addActionListener(e -> swipe(parent, true));

        buttonPanel.add(passButton);
        buttonPanel.add(likeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    private JLabel createStatLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Fonts.ClashFontSmall);
        label.setForeground(CLASH_DARK_BLUE);
        return label;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(Fonts.ClashFontMedium);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Calculate width based on text length, minimum 120px
        FontMetrics fm = button.getFontMetrics(Fonts.ClashFontMedium);
        int textWidth = fm.stringWidth(text);
        int buttonWidth = Math.max(150, textWidth + 60); // Add generous padding
        button.setPreferredSize(new Dimension(buttonWidth, 50));
        button.setMinimumSize(new Dimension(buttonWidth, 50));

        button.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(10, bgColor.darker(), 0),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void swipe(JFrame parent, boolean liked) {
        if (liked) {
            if (Math.random() > 0.2) {
                matches.add(cards.get(currentIndex));
            }
        }
        currentIndex++;
        showCurrentCard(parent);
    }

    private void showInstantMegaKnightMatch(JFrame parent) {
        removeAll();
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        JLabel titleLabel = new JLabel("IT'S A MATCH!");
        titleLabel.setFont(Fonts.ClashFontLarge);
        titleLabel.setForeground(CLASH_GOLD);
        topPanel.add(titleLabel);
        add(topPanel, BorderLayout.NORTH);

        JPanel matchPanel = new JPanel();
        matchPanel.setLayout(new BoxLayout(matchPanel, BoxLayout.Y_AXIS));
        matchPanel.setOpaque(false);

        ClashCard megaKnight = matches.get(0);

        // Add Mega Knight photo
        matchPanel.add(Box.createVerticalStrut(20));
        try {
            Image cardImage = null;
            String imagePath = "/images/" + megaKnight.image;
            try {
                cardImage = ImageIO.read(getClass().getResourceAsStream(imagePath));
                System.out.println("Successfully loaded Mega Knight image from: " + imagePath);
            } catch (Exception e) {
                System.err.println("Could not load card image: " + imagePath);
                e.printStackTrace();
            }

            if (cardImage != null) {
                Image scaledImage = cardImage.getScaledInstance(300, 300, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                matchPanel.add(imageLabel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        matchPanel.add(Box.createVerticalStrut(20));

        JLabel matchName = new JLabel("MEGA KNIGHT");
        matchName.setFont(Fonts.ClashFontLarge);
        matchName.setForeground(CLASH_GOLD);
        matchName.setAlignmentX(Component.CENTER_ALIGNMENT);
        matchPanel.add(matchName);

        matchPanel.add(Box.createVerticalStrut(10));

        JLabel matchMessage = new JLabel("<html><div style='text-align: center;'>Congratulations!<br>You've been matched with the legendary MEGA KNIGHT!</div></html>");
        matchMessage.setFont(Fonts.ClashFontMedium);
        matchMessage.setForeground(Color.WHITE);
        matchMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
        matchMessage.setHorizontalAlignment(SwingConstants.CENTER);
        matchPanel.add(matchMessage);

        add(matchPanel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private void showMatches(JFrame parent) {
        removeAll();
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        JLabel titleLabel = new JLabel("Your Matches!");
        titleLabel.setFont(Fonts.ClashFontLarge);
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel);
        add(topPanel, BorderLayout.NORTH);

        if (matches.isEmpty()) {
            JPanel noMatchPanel = new JPanel();
            noMatchPanel.setLayout(new BoxLayout(noMatchPanel, BoxLayout.Y_AXIS));
            noMatchPanel.setOpaque(false);

            if (cryingGif != null) {
                JLabel gifLabel = new JLabel(cryingGif);
                gifLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                noMatchPanel.add(Box.createVerticalStrut(50));
                noMatchPanel.add(gifLabel);
                noMatchPanel.add(Box.createVerticalStrut(20));

                // Play crying king audio
                AudioPlayer.playCryingKing();
            }

            JLabel noMatches = new JLabel("<html><div style='text-align: center;'>No matches yet...<br>Better luck next time!</div></html>");
            noMatches.setFont(Fonts.ClashFont);
            noMatches.setForeground(Color.WHITE);
            noMatches.setAlignmentX(Component.CENTER_ALIGNMENT);
            noMatches.setHorizontalAlignment(SwingConstants.CENTER);
            noMatchPanel.add(noMatches);

            add(noMatchPanel, BorderLayout.CENTER);
        } else {
            JPanel matchesPanel = new JPanel();
            matchesPanel.setLayout(new BoxLayout(matchesPanel, BoxLayout.Y_AXIS));
            matchesPanel.setOpaque(false);

            ClashCard topMatch = matches.get(0);

            // Add top match photo
            matchesPanel.add(Box.createVerticalStrut(20));
            try {
                Image cardImage = null;
                String[] possiblePaths = {
                    "images/" + topMatch.image,             // When running from src directory
                    "src/images/" + topMatch.image,         // When running from root
                    "../images/" + topMatch.image
                };

                for (String path : possiblePaths) {
                    File imageFile = new File(path);
                    if (imageFile.exists()) {
                        cardImage = ImageIO.read(imageFile);
                        break;
                    }
                }

                if (cardImage != null) {
                    // Scale image while maintaining aspect ratio
                    int maxSize = 150;
                    int originalWidth = cardImage.getWidth(null);
                    int originalHeight = cardImage.getHeight(null);

                    double scale = Math.min((double) maxSize / originalWidth, (double) maxSize / originalHeight);
                    int scaledWidth = (int) (originalWidth * scale);
                    int scaledHeight = (int) (originalHeight * scale);

                    Image scaled = cardImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                    JLabel photoLabel = new JLabel(new ImageIcon(scaled));
                    photoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    matchesPanel.add(photoLabel);
                    matchesPanel.add(Box.createVerticalStrut(15));
                }
            } catch (Exception e) {
                System.err.println("Could not load match image: " + e.getMessage());
            }

            JLabel topLabel = new JLabel("TOP MATCH: " + topMatch.name);
            topLabel.setFont(Fonts.ClashFontLarge);
            topLabel.setForeground(CLASH_GOLD);
            topLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            matchesPanel.add(topLabel);
            matchesPanel.add(Box.createVerticalStrut(10));

            String topBioText = generateBio(topMatch);
            JTextArea topBio = new JTextArea(topBioText);
            topBio.setWrapStyleWord(true);
            topBio.setLineWrap(true);
            topBio.setEditable(false);
            topBio.setOpaque(false);
            topBio.setForeground(Color.WHITE);
            topBio.setFont(Fonts.ClashFontSmall);
            topBio.setMaximumSize(new Dimension(400, 100));
            topBio.setAlignmentX(Component.CENTER_ALIGNMENT);
            matchesPanel.add(topBio);

            if (matches.size() > 1) {
                matchesPanel.add(Box.createVerticalStrut(20));
                JLabel othersLabel = new JLabel("Other matches:");
                othersLabel.setFont(Fonts.ClashFontMedium);
                othersLabel.setForeground(Color.WHITE);
                othersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                matchesPanel.add(othersLabel);

                for (int i = 1; i < matches.size(); i++) {
                    JLabel matchLabel = new JLabel("• " + matches.get(i).name);
                    matchLabel.setFont(Fonts.ClashFontSmall);
                    matchLabel.setForeground(Color.WHITE);
                    matchLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    matchesPanel.add(matchLabel);
                }
            }

            JScrollPane scrollPane = new JScrollPane(matchesPanel);
            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);
            scrollPane.setBorder(null);
            add(scrollPane, BorderLayout.CENTER);
        }

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        JButton restartButton = createStyledButton("Start Over", CLASH_GOLD);
        restartButton.addActionListener(e -> {
            parent.getContentPane().removeAll();
            parent.add(new ProfileInputPanel(parent));
            parent.revalidate();
            parent.repaint();
        });
        bottomPanel.add(restartButton);
        add(bottomPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }
}

class ClashCard {
    String name;
    String image;
    String height;
    String attackRange;
    double freeTime;
    double humanoidScore;
    String category;
    String personality;
    int heightFeet;
    int heightInches;

    public ClashCard(String name, String image, String height, String attackRange,
                     double freeTime, double humanoidScore, String category, String personality) {
        this.name = name;
        this.image = image;
        this.height = height;
        this.attackRange = attackRange;
        this.freeTime = freeTime;
        this.humanoidScore = humanoidScore;
        this.category = category;
        this.personality = personality;

        // Parse height string like "6'0\"" into feet and inches
        parseHeight(height);
    }

    private void parseHeight(String heightStr) {
        try {
            // Remove quotes and backslashes
            String cleaned = heightStr.replace("\\", "").replace("\"", "");
            String[] parts = cleaned.split("'");
            this.heightFeet = Integer.parseInt(parts[0].trim());
            this.heightInches = parts.length > 1 ? Integer.parseInt(parts[1].trim()) : 0;
        } catch (Exception e) {
            System.err.println("Error parsing height '" + heightStr + "': " + e.getMessage());
            this.heightFeet = 5;
            this.heightInches = 0;
        }
    }
}
