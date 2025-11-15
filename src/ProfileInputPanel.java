import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

class ProfileInputPanel extends JPanel {
    private JTextField nameField;
    private JTextArea bioArea;
    private JLabel imageLabel;
    private File selectedImage;
    private JButton continueButton;
    private Image logoImage;
    private java.util.List<Image> backgroundImages;
    private Image kingImage;

    // New fields
    private JSpinner heightFeetSpinner;
    private JSpinner heightInchesSpinner;
    private JComboBox<String> rangeComboBox;
    private JSlider freeTimeSlider;
    private JSlider humannessSlider;
    private JComboBox<String> preferenceComboBox;

    // Dialogue system
    private int currentQuestion = 0;
    private JLabel speechBubbleLabel;
    private JPanel inputPanel;
    private JPanel dialogueCard;

    // Stored answers
    private String userName = "";
    private String userBio = "";
    private int heightFeet = 5;
    private int heightInches = 6;
    private String attackRange = "Medium";
    private int freeTime = 50;
    private int humanness = 50;
    private String preference = "Straight";

    // Clash Royale colors
    private static final Color CLASH_BLUE = new Color(74, 144, 226);
    private static final Color CLASH_DARK_BLUE = new Color(45, 88, 167);
    private static final Color CLASH_GOLD = new Color(255, 183, 77);
    private static final Color CARD_BG = new Color(255, 255, 255, 240);

    public ProfileInputPanel(JFrame parent) {
        setLayout(new BorderLayout(0, 0));

        // Load images
        backgroundImages = new java.util.ArrayList<>();
        try {
            logoImage = ImageIO.read(getClass().getResourceAsStream("/resources/main_logo_clashroyale.5e3fbb70__1_.webp"));
            kingImage = ImageIO.read(getClass().getResourceAsStream("/resources/tutorial.png"));

            // Load all character/background images
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

        // Logo at the top
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        JLabel logoLabel = new JLabel();
        if (logoImage != null) {
            Image scaledLogo = logoImage.getScaledInstance(280, 80, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaledLogo));
        }
        logoPanel.add(logoLabel);
        add(logoPanel, BorderLayout.NORTH);

        // Center panel with dialogue card
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        dialogueCard = new JPanel();
        dialogueCard.setLayout(new BoxLayout(dialogueCard, BoxLayout.Y_AXIS));
        dialogueCard.setBackground(CARD_BG);
        dialogueCard.setBorder(new RoundedBorder(15, CLASH_GOLD, 3));

        // King image with speech bubble
        JPanel kingPanel = new JPanel(new BorderLayout());
        kingPanel.setOpaque(false);
        kingPanel.setMaximumSize(new Dimension(500, 280));
        kingPanel.setPreferredSize(new Dimension(500, 280));

        if (kingImage != null) {
            JLabel kingLabel = new JLabel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                    // Draw the king image
                    g2d.drawImage(kingImage, 0, 0, 500, 280, this);

                    // Draw speech bubble text
                    if (speechBubbleLabel != null && speechBubbleLabel.getText() != null) {
                        g2d.setFont(Fonts.ClashFontMedium);
                        g2d.setColor(CLASH_DARK_BLUE);

                        String text = speechBubbleLabel.getText();
                        FontMetrics fm = g2d.getFontMetrics();

                        // Word wrap the text to fit in the speech bubble
                        java.util.List<String> lines = new java.util.ArrayList<>();
                        String[] words = text.split(" ");
                        String currentLine = "";

                        for (String word : words) {
                            String testLine = currentLine.isEmpty() ? word : currentLine + " " + word;
                            if (fm.stringWidth(testLine) < 180) {
                                currentLine = testLine;
                            } else {
                                if (!currentLine.isEmpty()) {
                                    lines.add(currentLine);
                                }
                                currentLine = word;
                            }
                        }
                        if (!currentLine.isEmpty()) {
                            lines.add(currentLine);
                        }

                        // Draw the wrapped text in the speech bubble area (top right)
                        int startX = 295;
                        int startY = 60;
                        int lineHeight = fm.getHeight();

                        for (int i = 0; i < lines.size(); i++) {
                            g2d.drawString(lines.get(i), startX, startY + (i * lineHeight));
                        }
                    }
                }
            };
            kingLabel.setPreferredSize(new Dimension(500, 280));
            kingPanel.add(kingLabel, BorderLayout.CENTER);
        }

        dialogueCard.add(Box.createVerticalStrut(15));
        dialogueCard.add(kingPanel);
        dialogueCard.add(Box.createVerticalStrut(15));

        // Speech bubble label (invisible, used for painting)
        speechBubbleLabel = new JLabel();

        // Input panel (dynamic based on question)
        inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setOpaque(false);
        inputPanel.setMaximumSize(new Dimension(400, 200));
        dialogueCard.add(inputPanel);
        dialogueCard.add(Box.createVerticalStrut(15));

        // Continue button
        continueButton = createStyledButton("Continue", CLASH_GOLD);
        continueButton.setPreferredSize(new Dimension(240, 45));
        continueButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        continueButton.addActionListener(e -> handleContinue(parent));
        dialogueCard.add(continueButton);
        dialogueCard.add(Box.createVerticalStrut(20));

        centerWrapper.add(dialogueCard);

        // Wrap in scroll pane to handle overflow
        JScrollPane mainScrollPane = new JScrollPane(centerWrapper);
        mainScrollPane.setOpaque(false);
        mainScrollPane.getViewport().setOpaque(false);
        mainScrollPane.setBorder(null);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(mainScrollPane, BorderLayout.CENTER);

        // Initialize first question
        showQuestion(0);
    }

    private void showQuestion(int questionNumber) {
        currentQuestion = questionNumber;
        inputPanel.removeAll();

        // Play random king dialogue sound
        AudioPlayer.playRandomDialogue();

        switch (questionNumber) {
            case 0: // Name
                speechBubbleLabel.setText("Welcome, warrior! What shall I call you?");
                nameField = new JTextField(20);
                nameField.setFont(Fonts.ClashFontMedium);
                nameField.setMaximumSize(new Dimension(300, 40));
                nameField.setHorizontalAlignment(JTextField.CENTER);
                nameField.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(8, CLASH_BLUE, 2),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
                nameField.setText(userName);
                JPanel namePanel = new JPanel();
                namePanel.setOpaque(false);
                namePanel.add(nameField);
                inputPanel.add(namePanel);
                break;

            case 1: // Bio
                speechBubbleLabel.setText("Tell me about yourself. What makes you unique?");
                bioArea = new JTextArea(4, 25);
                bioArea.setLineWrap(true);
                bioArea.setWrapStyleWord(true);
                bioArea.setFont(Fonts.ClashFontSmall);
                bioArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
                bioArea.setText(userBio);
                JScrollPane scrollPane = new JScrollPane(bioArea);
                scrollPane.setMaximumSize(new Dimension(320, 100));
                scrollPane.setBorder(new RoundedBorder(8, CLASH_BLUE, 2));
                JPanel bioPanel = new JPanel();
                bioPanel.setOpaque(false);
                bioPanel.add(scrollPane);
                inputPanel.add(bioPanel);
                break;

            case 2: // Photo upload
                speechBubbleLabel.setText("Show me your finest portrait!");
                imageLabel = new JLabel("No image");
                imageLabel.setFont(new Font("Arial", Font.PLAIN, 12));
                imageLabel.setForeground(Color.GRAY);
                imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                imageLabel.setPreferredSize(new Dimension(150, 150));
                imageLabel.setBorder(new RoundedBorder(10, CLASH_BLUE, 2));
                imageLabel.setBackground(Color.WHITE);
                imageLabel.setOpaque(true);
                if (selectedImage != null) {
                    ImageIcon icon = new ImageIcon(selectedImage.getAbsolutePath());
                    Image scaled = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(scaled));
                    imageLabel.setText("");
                }
                JButton uploadButton = createStyledButton("Upload Photo", CLASH_BLUE);
                uploadButton.setPreferredSize(new Dimension(200, 40));
                uploadButton.addActionListener(e -> selectImage());
                JPanel photoPanel = new JPanel();
                photoPanel.setOpaque(false);
                photoPanel.setLayout(new BoxLayout(photoPanel, BoxLayout.Y_AXIS));
                imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                uploadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                photoPanel.add(imageLabel);
                photoPanel.add(Box.createVerticalStrut(10));
                photoPanel.add(uploadButton);
                inputPanel.add(photoPanel);
                break;

            case 3: // Preference (moved here)
                speechBubbleLabel.setText("And what are your romantic preferences?");
                preferenceComboBox = new JComboBox<>(new String[]{"Straight", "Gay", "Something Else"});
                preferenceComboBox.setFont(Fonts.ClashFontMedium);
                preferenceComboBox.setMaximumSize(new Dimension(250, 35));
                preferenceComboBox.setPreferredSize(new Dimension(250, 35));
                preferenceComboBox.setSelectedItem(preference);
                JPanel preferencePanel = new JPanel();
                preferencePanel.setOpaque(false);
                preferencePanel.add(preferenceComboBox);
                inputPanel.add(preferencePanel);
                break;

            case 4: // Height
                speechBubbleLabel.setText("What height are you seeking in a partner?");
                JPanel heightPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
                heightPanel.setOpaque(false);
                heightFeetSpinner = new JSpinner(new SpinnerNumberModel(heightFeet, 0, 99, 1));
                heightFeetSpinner.setFont(Fonts.ClashFontMedium);
                ((JSpinner.DefaultEditor) heightFeetSpinner.getEditor()).getTextField().setHorizontalAlignment(JTextField.CENTER);
                heightFeetSpinner.setPreferredSize(new Dimension(70, 35));
                JLabel feetLabel = new JLabel("ft");
                feetLabel.setFont(Fonts.ClashFontMedium);
                heightInchesSpinner = new JSpinner(new SpinnerNumberModel(heightInches, 0, 11, 1));
                heightInchesSpinner.setFont(Fonts.ClashFontMedium);
                ((JSpinner.DefaultEditor) heightInchesSpinner.getEditor()).getTextField().setHorizontalAlignment(JTextField.CENTER);
                heightInchesSpinner.setPreferredSize(new Dimension(70, 35));
                JLabel inchesLabel = new JLabel("in");
                inchesLabel.setFont(Fonts.ClashFontMedium);
                heightPanel.add(heightFeetSpinner);
                heightPanel.add(feetLabel);
                heightPanel.add(heightInchesSpinner);
                heightPanel.add(inchesLabel);
                inputPanel.add(heightPanel);
                break;

            case 5: // Attack range
                speechBubbleLabel.setText("What attack range suits you best?");
                rangeComboBox = new JComboBox<>(new String[]{"Short", "Medium", "Long", "Extra Long"});
                rangeComboBox.setFont(Fonts.ClashFontMedium);
                rangeComboBox.setMaximumSize(new Dimension(250, 35));
                rangeComboBox.setPreferredSize(new Dimension(250, 35));
                rangeComboBox.setSelectedItem(attackRange);
                JPanel rangePanel = new JPanel();
                rangePanel.setOpaque(false);
                rangePanel.add(rangeComboBox);
                inputPanel.add(rangePanel);
                break;

            case 6: // Free time
                speechBubbleLabel.setText("How much free time does your ideal match have?");
                freeTimeSlider = new JSlider(0, 100, freeTime);
                freeTimeSlider.setOpaque(false);
                freeTimeSlider.setMaximumSize(new Dimension(280, 50));
                freeTimeSlider.setPreferredSize(new Dimension(280, 50));
                freeTimeSlider.setMajorTickSpacing(25);
                freeTimeSlider.setPaintTicks(true);
                freeTimeSlider.setPaintLabels(true);
                JPanel freeTimePanel = new JPanel();
                freeTimePanel.setOpaque(false);
                freeTimePanel.add(freeTimeSlider);
                inputPanel.add(freeTimePanel);
                break;

            case 7: // Humanness
                speechBubbleLabel.setText("How human should your match be?");
                humannessSlider = new JSlider(0, 100, humanness);
                humannessSlider.setOpaque(false);
                humannessSlider.setMaximumSize(new Dimension(280, 50));
                humannessSlider.setPreferredSize(new Dimension(280, 50));
                humannessSlider.setMajorTickSpacing(25);
                humannessSlider.setPaintTicks(true);
                humannessSlider.setPaintLabels(true);
                JPanel humannessPanel = new JPanel();
                humannessPanel.setOpaque(false);
                humannessPanel.add(humannessSlider);
                inputPanel.add(humannessPanel);

                // Change button text for last question
                continueButton.setText("Find My Matches!");
                break;
        }

        inputPanel.revalidate();
        inputPanel.repaint();
        dialogueCard.revalidate();
        dialogueCard.repaint();
    }

    private void handleContinue(JFrame parent) {
        // Save current answer
        switch (currentQuestion) {
            case 0: // Name
                userName = nameField.getText().trim();
                if (userName.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter your name!",
                        "Missing Information", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                break;
            case 1: // Bio
                userBio = bioArea.getText().trim();
                if (userBio.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please tell us about yourself!",
                        "Missing Information", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                break;
            case 2: // Photo (optional, but encourage)
                // Photo is optional, just move on
                break;
            case 3: // Preference (moved here)
                preference = (String) preferenceComboBox.getSelectedItem();
                // If Gay, skip all remaining questions and go straight to Mega Knight
                if (preference.equals("Gay")) {
                    startMatching(parent);
                    return;
                }
                break;
            case 4: // Height
                heightFeet = (Integer) heightFeetSpinner.getValue();
                heightInches = (Integer) heightInchesSpinner.getValue();
                break;
            case 5: // Attack range
                attackRange = (String) rangeComboBox.getSelectedItem();
                break;
            case 6: // Free time
                freeTime = freeTimeSlider.getValue();
                break;
            case 7: // Humanness (last question now)
                humanness = humannessSlider.getValue();
                // All done! Start matching
                startMatching(parent);
                return;
        }

        // Move to next question
        showQuestion(currentQuestion + 1);
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
        button.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(10, bgColor.darker(), 0),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        // Hover effect
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

        // Draw pattern of diagonal lines for texture
        g2d.setColor(new Color(255, 255, 255, 10));
        for (int i = -height; i < width; i += 30) {
            g2d.drawLine(i, 0, i + height, height);
        }

        // Scatter small images around the sides
        if (!backgroundImages.isEmpty()) {
            // Define positions for images (x, y, size, opacity, rotation)
            int[][] positions = {
                // Left side - top to bottom
                {-60, 100, 120, 30, -15},
                {-45, 250, 100, 25, 10},
                {-35, 400, 110, 28, -8},
                {-50, 570, 95, 22, 12},
                {-40, 730, 105, 26, -10},
                {-55, height - 130, 90, 20, 8},

                // Right side - top to bottom
                {width - 140, 110, 115, 28, 18},
                {width - 120, 270, 105, 26, -12},
                {width - 130, 420, 100, 24, 14},
                {width - 115, 590, 110, 25, -16},
                {width - 125, 750, 95, 21, 10},
                {width - 110, height - 150, 100, 23, -11},

                // Additional scattered in corners
                {20, 60, 85, 18, -20},
                {width - 70, 70, 80, 17, 22},
                {15, height - 90, 90, 19, 15},
                {width - 75, height - 100, 85, 18, -18}
            };

            for (int i = 0; i < positions.length; i++) {
                Image img = backgroundImages.get(i % backgroundImages.size());
                int[] pos = positions[i];
                int x = pos[0];
                int y = pos[1];
                int size = pos[2];
                float opacity = pos[3] / 100f;
                double rotation = Math.toRadians(pos[4]);

                // Calculate scaled dimensions
                int imgWidth = (int) (img.getWidth(null) * size / (double) img.getHeight(null));
                int imgHeight = size;

                // Apply transformations
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
                g2d.rotate(rotation, x + imgWidth/2, y + imgHeight/2);
                g2d.drawImage(img, x, y, imgWidth, imgHeight, this);
                g2d.rotate(-rotation, x + imgWidth/2, y + imgHeight/2);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }
        }

        // Add vignette effect
        RadialGradientPaint vignette = new RadialGradientPaint(
            width/2f, height/2f, width/1.5f,
            new float[]{0.0f, 1.0f},
            new Color[]{new Color(0, 0, 0, 0), new Color(0, 0, 0, 100)}
        );
        g2d.setPaint(vignette);
        g2d.fillRect(0, 0, width, height);
    }

    private void selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Image files", "jpg", "jpeg", "png", "gif"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedImage = fileChooser.getSelectedFile();
            if (imageLabel != null) {
                ImageIcon icon = new ImageIcon(selectedImage.getAbsolutePath());
                Image scaled = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaled));
                imageLabel.setText("");
            }
        }
    }

    private void startMatching(JFrame parent) {
        // All data is already stored in instance variables
        if (userName.isEmpty() || userBio.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!",
                "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Switch to matching screen with user preferences
        parent.getContentPane().removeAll();
        String imagePath = selectedImage != null ? selectedImage.getAbsolutePath() : null;
        parent.add(new SwipePanel(parent, userName, userBio, heightFeet, heightInches,
                                  attackRange, freeTime, humanness, preference, imagePath));
        parent.revalidate();
        parent.repaint();
    }
}

// Custom rounded border class
class RoundedBorder extends AbstractBorder {
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
            g2d.draw(new RoundRectangle2D.Double(x + thickness/2.0, y + thickness/2.0,
                width - thickness, height - thickness, radius, radius));
        }

        g2d.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(radius/2 + thickness, radius/2 + thickness,
                         radius/2 + thickness, radius/2 + thickness);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.right = insets.top = insets.bottom = radius/2 + thickness;
        return insets;
    }
}