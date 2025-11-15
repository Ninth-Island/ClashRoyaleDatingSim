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

    // New fields
    private JSpinner heightFeetSpinner;
    private JSpinner heightInchesSpinner;
    private JComboBox<String> rangeComboBox;
    private JSlider freeTimeSlider;
    private JSlider humannessSlider;
    private JComboBox<String> preferenceComboBox;

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

        // Center panel with form card
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(CARD_BG);
        formCard.setBorder(new RoundedBorder(15, CLASH_GOLD, 3));

        // Title
        JLabel titleLabel = new JLabel("Create Your Profile");
        titleLabel.setFont(Fonts.ClashFontLarge);
        titleLabel.setForeground(CLASH_DARK_BLUE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formCard.add(Box.createVerticalStrut(20));
        formCard.add(titleLabel);
        formCard.add(Box.createVerticalStrut(15));

        // Name field
        JLabel nameLabel = new JLabel("NAME");
        nameLabel.setFont(Fonts.ClashFontSmall);
        nameLabel.setForeground(CLASH_DARK_BLUE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formCard.add(nameLabel);
        formCard.add(Box.createVerticalStrut(5));

        nameField = new JTextField(20);
        nameField.setFont(Fonts.ClashFontMedium);
        nameField.setMaximumSize(new Dimension(260, 35));
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(8, CLASH_BLUE, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        JPanel namePanel = new JPanel();
        namePanel.setOpaque(false);
        namePanel.add(nameField);
        formCard.add(namePanel);
        formCard.add(Box.createVerticalStrut(12));

        // Bio area
        JLabel bioLabel = new JLabel("BIO");
        bioLabel.setFont(Fonts.ClashFontSmall);
        bioLabel.setForeground(CLASH_DARK_BLUE);
        bioLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formCard.add(bioLabel);
        formCard.add(Box.createVerticalStrut(5));

        bioArea = new JTextArea(3, 20);
        bioArea.setLineWrap(true);
        bioArea.setWrapStyleWord(true);
        bioArea.setFont(Fonts.ClashFontSmall);
        bioArea.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        JScrollPane scrollPane = new JScrollPane(bioArea);
        scrollPane.setMaximumSize(new Dimension(260, 80));
        scrollPane.setBorder(new RoundedBorder(8, CLASH_BLUE, 2));
        JPanel bioPanel = new JPanel();
        bioPanel.setOpaque(false);
        bioPanel.add(scrollPane);
        formCard.add(bioPanel);
        formCard.add(Box.createVerticalStrut(12));

        // Desired height
        JLabel heightLabel = new JLabel("DESIRED HEIGHT");
        heightLabel.setFont(Fonts.ClashFontSmall);
        heightLabel.setForeground(CLASH_DARK_BLUE);
        heightLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formCard.add(heightLabel);
        formCard.add(Box.createVerticalStrut(5));

        JPanel heightPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        heightPanel.setOpaque(false);
        heightFeetSpinner = new JSpinner(new SpinnerNumberModel(5, 0, 99, 1));
        heightFeetSpinner.setFont(Fonts.ClashFontSmall);
        ((JSpinner.DefaultEditor) heightFeetSpinner.getEditor()).getTextField().setHorizontalAlignment(JTextField.CENTER);
        heightFeetSpinner.setPreferredSize(new Dimension(60, 30));
        JLabel feetLabel = new JLabel("ft");
        feetLabel.setFont(Fonts.ClashFontSmall);
        heightInchesSpinner = new JSpinner(new SpinnerNumberModel(6, 0, 11, 1));
        heightInchesSpinner.setFont(Fonts.ClashFontSmall);
        ((JSpinner.DefaultEditor) heightInchesSpinner.getEditor()).getTextField().setHorizontalAlignment(JTextField.CENTER);
        heightInchesSpinner.setPreferredSize(new Dimension(60, 30));
        JLabel inchesLabel = new JLabel("in");
        inchesLabel.setFont(Fonts.ClashFontSmall);
        heightPanel.add(heightFeetSpinner);
        heightPanel.add(feetLabel);
        heightPanel.add(heightInchesSpinner);
        heightPanel.add(inchesLabel);
        formCard.add(heightPanel);
        formCard.add(Box.createVerticalStrut(12));

        // Attack Range
        JLabel rangeLabel = new JLabel("ATTACK RANGE");
        rangeLabel.setFont(Fonts.ClashFontSmall);
        rangeLabel.setForeground(CLASH_DARK_BLUE);
        rangeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formCard.add(rangeLabel);
        formCard.add(Box.createVerticalStrut(5));

        rangeComboBox = new JComboBox<>(new String[]{"Short", "Medium", "Long", "Extra Long"});
        rangeComboBox.setFont(Fonts.ClashFontSmall);
        rangeComboBox.setMaximumSize(new Dimension(200, 30));
        rangeComboBox.setPreferredSize(new Dimension(200, 30));
        JPanel rangePanel = new JPanel();
        rangePanel.setOpaque(false);
        rangePanel.add(rangeComboBox);
        formCard.add(rangePanel);
        formCard.add(Box.createVerticalStrut(12));

        // Free time slider
        JLabel freeTimeLabel = new JLabel("DATE'S FREE TIME (0-100)");
        freeTimeLabel.setFont(Fonts.ClashFontSmall);
        freeTimeLabel.setForeground(CLASH_DARK_BLUE);
        freeTimeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formCard.add(freeTimeLabel);
        formCard.add(Box.createVerticalStrut(5));

        freeTimeSlider = new JSlider(0, 100, 50);
        freeTimeSlider.setOpaque(false);
        freeTimeSlider.setMaximumSize(new Dimension(240, 40));
        freeTimeSlider.setPreferredSize(new Dimension(240, 40));
        freeTimeSlider.setMajorTickSpacing(25);
        freeTimeSlider.setPaintTicks(true);
        freeTimeSlider.setPaintLabels(true);
        JPanel freeTimePanel = new JPanel();
        freeTimePanel.setOpaque(false);
        freeTimePanel.add(freeTimeSlider);
        formCard.add(freeTimePanel);
        formCard.add(Box.createVerticalStrut(10));

        // Humanness slider
        JLabel humannessLabel = new JLabel("HOW HUMAN (0-100)");
        humannessLabel.setFont(Fonts.ClashFontSmall);
        humannessLabel.setForeground(CLASH_DARK_BLUE);
        humannessLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formCard.add(humannessLabel);
        formCard.add(Box.createVerticalStrut(5));

        humannessSlider = new JSlider(0, 100, 50);
        humannessSlider.setOpaque(false);
        humannessSlider.setMaximumSize(new Dimension(240, 40));
        humannessSlider.setPreferredSize(new Dimension(240, 40));
        humannessSlider.setMajorTickSpacing(25);
        humannessSlider.setPaintTicks(true);
        humannessSlider.setPaintLabels(true);
        JPanel humannessPanel = new JPanel();
        humannessPanel.setOpaque(false);
        humannessPanel.add(humannessSlider);
        formCard.add(humannessPanel);
        formCard.add(Box.createVerticalStrut(10));

        // Preference
        JLabel preferenceLabel = new JLabel("PREFERENCE");
        preferenceLabel.setFont(Fonts.ClashFontSmall);
        preferenceLabel.setForeground(CLASH_DARK_BLUE);
        preferenceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formCard.add(preferenceLabel);
        formCard.add(Box.createVerticalStrut(5));

        preferenceComboBox = new JComboBox<>(new String[]{"Straight", "Gay", "Something Else"});
        preferenceComboBox.setFont(Fonts.ClashFontSmall);
        preferenceComboBox.setMaximumSize(new Dimension(200, 30));
        preferenceComboBox.setPreferredSize(new Dimension(200, 30));
        JPanel preferencePanel = new JPanel();
        preferencePanel.setOpaque(false);
        preferencePanel.add(preferenceComboBox);
        formCard.add(preferencePanel);
        formCard.add(Box.createVerticalStrut(12));

        // Image preview
        imageLabel = new JLabel("No image");
        imageLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        imageLabel.setForeground(Color.GRAY);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(120, 120));
        imageLabel.setBorder(new RoundedBorder(10, CLASH_BLUE, 2));
        imageLabel.setBackground(Color.WHITE);
        imageLabel.setOpaque(true);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formCard.add(imageLabel);
        formCard.add(Box.createVerticalStrut(10));

        // Upload button
        JButton uploadButton = createStyledButton("Upload Photo", CLASH_BLUE);
        uploadButton.setPreferredSize(new Dimension(180, 35));
        uploadButton.addActionListener(e -> selectImage());
        uploadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        formCard.add(uploadButton);
        formCard.add(Box.createVerticalStrut(15));

        // Continue button inside card
        continueButton = createStyledButton("Find My Matches!", CLASH_GOLD);
        continueButton.setPreferredSize(new Dimension(240, 45));
        continueButton.addActionListener(e -> startMatching(parent));
        continueButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        formCard.add(continueButton);
        formCard.add(Box.createVerticalStrut(20));

        centerWrapper.add(formCard);

        // Wrap in scroll pane to handle overflow
        JScrollPane mainScrollPane = new JScrollPane(centerWrapper);
        mainScrollPane.setOpaque(false);
        mainScrollPane.getViewport().setOpaque(false);
        mainScrollPane.setBorder(null);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(mainScrollPane, BorderLayout.CENTER);
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
            ImageIcon icon = new ImageIcon(selectedImage.getAbsolutePath());
            Image scaled = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaled));
            imageLabel.setText("");
        }
    }

    private void startMatching(JFrame parent) {
        String name = nameField.getText().trim();
        String bio = bioArea.getText().trim();

        if (name.isEmpty() || bio.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!",
                "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Switch to matching screen
        parent.getContentPane().removeAll();
        parent.add(new SwipePanel(parent, name, bio));
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