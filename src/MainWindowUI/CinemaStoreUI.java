package MainWindowUI;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import MediaPlayerUI.MediaPlayerUI;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CinemaStoreUI extends JFrame {
    private JPanel titleCardPanel;
    private JLabel titleCardLabel;
    private Font titleFont;
    private JPanel contentPanel;
    private JPanel itemPanel;
    private JScrollPane itemPanelScroll;
    private JPanel mediaControlPanel;
    private JButton searchButton;
    private JButton addFolderButton;
    private JButton addFileButton;
    private JTextField searchTextField;
    private JPanel dynamicContentPanel;
    private MediaPlayerUI mediaPlayerUI;
    private List<File> storedMediaFiles;

    public CinemaStoreUI() {
        this.initializeUI();
        this.storedMediaFiles = new ArrayList<>();

        try {
            this.loadMediaFiles();
        } catch (IOException e) {
            this.handleException(e, "Error loading media files: " + e.getMessage());
        }

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveMediaFiles();
            }
        });
    }

    private void initializeUI() {
        this.setTitle("THE CINEMA PLAYER");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.getContentPane().setBackground(Color.BLACK);
        this.initializeTitleCardPanel();
        this.initializeMediaControlPanel();
        this.initializeContentPanel();

        this.dynamicContentPanel = new JPanel(new CardLayout());
        this.dynamicContentPanel.add(this.contentPanel, "ALL_MEDIA");

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.BLACK);
        mainPanel.add(this.titleCardPanel, BorderLayout.NORTH);
        mainPanel.add(this.dynamicContentPanel, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.getContentPane().add(scrollPane, BorderLayout.CENTER);
        this.setVisible(true);
    }

    private void initializeTitleCardPanel() {
        this.titleCardPanel = new JPanel();
        this.titleCardLabel = new JLabel();
        this.titleFont = new Font("Arial", Font.BOLD, 66);
        this.titleCardPanel.setLayout(new GridBagLayout());
        this.titleCardPanel.setPreferredSize(new Dimension(600, 140));
        Color titleColor = new Color(173, 216, 230);
        this.titleCardPanel.setBackground(titleColor);
        this.titleCardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        this.titleCardLabel.setText("THE CINEMA PLAYER");
        this.titleCardLabel.setFont(this.titleFont);
        this.titleCardPanel.add(this.titleCardLabel, gbc);
    }

    @SuppressWarnings("unused")
    private void initializeMediaControlPanel() {
        this.mediaControlPanel = new JPanel();
        this.mediaControlPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 5));
        this.mediaControlPanel.setPreferredSize(new Dimension(600, 60));
        Color controlColor = Color.decode("#B0C4DE");
        this.mediaControlPanel.setBackground(controlColor);
        this.mediaControlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        this.searchTextField = new JTextField(20);
        this.searchTextField.setText("Enter to search:");
        this.searchTextField.setForeground(Color.GRAY);
        this.searchTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchTextField.getText().equals("Enter to search:")) {
                    searchTextField.setText("");
                    searchTextField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchTextField.getText().isEmpty()) {
                    searchTextField.setText("Enter to search:");
                    searchTextField.setForeground(Color.GRAY);
                }
            }
        });

        addFileButton = createButton("Add File");
        addFolderButton = createButton("Add From Folder");
        searchButton = createButton("Search");

        addFileButton.addActionListener(e -> {
            try {
                addFile();
            } catch (IOException ex) {
                handleException(ex, "Error adding file: " + ex.getMessage());
            }
        });

        addFolderButton.addActionListener(e -> {
            try {
                addFromFolder();
            } catch (IOException ex) {
                handleException(ex, "Error adding folder: " + ex.getMessage());
            }
        });

        searchButton.addActionListener(e -> searchMedia());

        this.mediaControlPanel.add(this.addFileButton);
        this.mediaControlPanel.add(this.addFolderButton);
        this.mediaControlPanel.add(this.searchTextField);
        this.mediaControlPanel.add(this.searchButton);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(160, 40)); 
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBackground(new Color(173, 216, 230));
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.BOLD, 12));

        button.setMargin(new Insets(5, 15, 5, 15)); 

        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(135, 206, 250));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(173, 216, 230));
            }
        });

        return button;
    }

    private void searchMedia() {
        String searchText = this.searchTextField.getText().trim().toLowerCase();
        if (!searchText.isEmpty() && !searchText.equals("Enter to search:")) {
            Component[] components = this.itemPanel.getComponents();
            for (Component component : components) {
                if (component instanceof JPanel) {
                    JPanel mediaItemPanel = (JPanel) component;
                    JLabel mediaLabel = (JLabel) mediaItemPanel.getComponent(0);
                    String mediaText = mediaLabel.getText().toLowerCase();
                    if (mediaText.contains(searchText)) {
                        mediaItemPanel.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
                    } else {
                        mediaItemPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a valid search term.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initializeContentPanel() {
        this.contentPanel = new JPanel();
        this.itemPanel = new JPanel(new GridLayout(0, 4, 10, 10));
        this.itemPanelScroll = new JScrollPane(this.itemPanel);
        this.contentPanel.setLayout(new BorderLayout());
        this.contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        Color contentColor = Color.decode("#98FB98");
        this.contentPanel.setBackground(contentColor);
        this.itemPanel.setBackground(Color.WHITE);
        this.itemPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.itemPanelScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.itemPanelScroll.setPreferredSize(new Dimension(1200, 600));
        this.contentPanel.add(this.mediaControlPanel, BorderLayout.NORTH);
        this.contentPanel.add(this.itemPanelScroll, BorderLayout.CENTER);
    }

    private void addFile() throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Media Files", "mp4", "mkv", "avi"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (isValidMediaFile(selectedFile)) {
                addMedia(selectedFile);
            } else {
                JOptionPane.showMessageDialog(this, "Unsupported file type: " + selectedFile.getName(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @SuppressWarnings("unused")
    private void addFromFolder() throws IOException {
        JFileChooser folderChooser = new JFileChooser();
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = folderChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = folderChooser.getSelectedFile();
            File[] mediaFiles = selectedFolder
                    .listFiles((dir, name) -> name.endsWith(".mp4") || name.endsWith(".mkv") || name.endsWith(".avi"));
            if (mediaFiles != null) {
                for (File file : mediaFiles) {
                    addMedia(file);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No valid media files found in the selected folder.", "Info",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void addMedia(File mediaFile) {
        if (!storedMediaFiles.contains(mediaFile)) {
            storedMediaFiles.add(mediaFile);
            JPanel mediaItemPanel = createMediaItemPanel(mediaFile);
            itemPanel.add(mediaItemPanel);
            itemPanel.revalidate();
            itemPanel.repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Media file already added: " + mediaFile.getName(), "Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    @SuppressWarnings("unused")
    private JPanel createMediaItemPanel(File mediaFile) {
        JPanel mediaItemPanel = new JPanel();
        mediaItemPanel.setLayout(new BorderLayout());
        mediaItemPanel.setPreferredSize(new Dimension(220, 100));
        mediaItemPanel.setBackground(Color.LIGHT_GRAY);
        mediaItemPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        JLabel mediaLabel = new JLabel(mediaFile.getName());
        mediaLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mediaLabel.setVerticalAlignment(SwingConstants.CENTER); 
        mediaLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mediaLabel.setPreferredSize(new Dimension(220, 50)); 

        JButton playButton = createMediaButton("Play", e -> {
            mediaPlayerUI = new MediaPlayerUI(mediaFile);
            mediaPlayerUI.setVisible(true);
        });

        JButton deleteButton = createMediaButton("Delete", e -> {
            int response = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this media file?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                itemPanel.remove(mediaItemPanel);
                storedMediaFiles.remove(mediaFile);
                itemPanel.revalidate();
                itemPanel.repaint();
            }
        });

        JButton renameButton = createMediaButton("Rename", e -> {
            String newName = JOptionPane.showInputDialog(this, "Enter new name:", mediaFile.getName());
            if (newName != null && !newName.trim().isEmpty()) {
                File newFile = new File(mediaFile.getParent(), newName);
                if (mediaFile.renameTo(newFile)) {
                    mediaLabel.setText(newFile.getName());
                    mediaItemPanel.revalidate();
                    mediaItemPanel.repaint();
                    this.storedMediaFiles.remove(mediaFile);
                    this.storedMediaFiles.add(newFile);
                } else {
                    JOptionPane.showMessageDialog(this, "Error renaming file.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        Dimension buttonSize = new Dimension(90, 30);
        playButton.setPreferredSize(buttonSize);
        renameButton.setPreferredSize(buttonSize);
        deleteButton.setPreferredSize(buttonSize);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        buttonPanel.add(playButton);
        buttonPanel.add(renameButton);
        buttonPanel.add(deleteButton);

        mediaItemPanel.add(mediaLabel, BorderLayout.CENTER); 
        mediaItemPanel.add(buttonPanel, BorderLayout.SOUTH); 

        return mediaItemPanel;
    }

    private JButton createMediaButton(String text, ActionListener actionListener) {
        JButton button = createButton(text);
        button.addActionListener(actionListener);
        return button;
    }

    private void handleException(Exception e, String message) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private boolean isValidMediaFile(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".mp4") || fileName.endsWith(".mkv") || fileName.endsWith(".avi");
    }

    private void saveMediaFiles() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("mediaFiles.txt"))) {
            for (File mediaFile : storedMediaFiles) {
                writer.write(mediaFile.getAbsolutePath());
                writer.newLine();
            }
        } catch (IOException e) {
            handleException(e, "Error saving media files: " + e.getMessage());
        }
    }

    private void loadMediaFiles() throws IOException {
        File file = new File("mediaFiles.txt");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    File mediaFile = new File(line);
                    storedMediaFiles.add(mediaFile);
                    JPanel mediaItemPanel = createMediaItemPanel(mediaFile);
                    itemPanel.add(mediaItemPanel);
                }
                itemPanel.revalidate();
                itemPanel.repaint();
            } catch (Exception e) {
                handleException(e, "Error loading media files: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CinemaStoreUI::new);
    }
}
