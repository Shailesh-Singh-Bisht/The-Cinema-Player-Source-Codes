package MediaPlayerUI;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.concurrent.TimeUnit;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;

public class MediaPlayerUI extends JFrame {
    private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
    private final JButton playButton;
    private final JButton pauseButton;
    private final JButton forwardButton;
    private final JButton backwardButton;
    private final JPanel controlsPanel;
    private final JProgressBar progressBar;
    private final JLabel currentTimeLabel;
    private final JLabel totalTimeLabel;

    public MediaPlayerUI(File mediaFile) {

        boolean discovered = new NativeDiscovery().discover();
        if (!discovered) {
            JOptionPane.showMessageDialog(this, "VLC native libraries not found!", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1); 
        }

        setTitle("Modern Media Player");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null); 
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.decode("#2C2C2C")); 

        mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
        mediaPlayerComponent.setBackground(Color.BLACK);
        add(mediaPlayerComponent, BorderLayout.CENTER);

        controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlsPanel.setBackground(Color.decode("#2C2C2C")); 

        playButton = createButton("Play", "icons/play.png");
        pauseButton = createButton("Pause", "icons/pause.png");
        forwardButton = createButton("Forward 10s", "icons/forward.png");
        backwardButton = createButton("Backward 10s", "icons/backward.png");

        controlsPanel.add(backwardButton);
        controlsPanel.add(playButton);
        controlsPanel.add(pauseButton);
        controlsPanel.add(forwardButton);
        add(controlsPanel, BorderLayout.SOUTH);

        progressBar = new JProgressBar();
        progressBar.setForeground(Color.decode("#00C853"));
        progressBar.setBackground(Color.decode("#424242"));
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(800, 20));

        currentTimeLabel = new JLabel("00:00:00");
        currentTimeLabel.setForeground(Color.WHITE);
        totalTimeLabel = new JLabel("00:00:00");
        totalTimeLabel.setForeground(Color.WHITE);

        JPanel progressBarPanel = new JPanel(new BorderLayout(10, 0));
        progressBarPanel.setBackground(Color.decode("#2C2C2C"));
        progressBarPanel.add(currentTimeLabel, BorderLayout.WEST);
        progressBarPanel.add(progressBar, BorderLayout.CENTER);
        progressBarPanel.add(totalTimeLabel, BorderLayout.EAST);
        progressBarPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        add(progressBarPanel, BorderLayout.NORTH);

        setupButtonListeners();
        mediaPlayerComponent.mediaPlayer().events().addMediaPlayerEventListener(new MediaPlayerEventHandler(this));

        setVisible(true);
        addWindowListener(new WindowCloseHandler(this));

        loadMedia(mediaFile);
    }

    private JButton createButton(String text, String iconPath) {
        JButton button = new JButton(new ImageIcon(iconPath));
        button.setText(text);
        button.setForeground(Color.WHITE);
        button.setBackground(Color.decode("#424242"));
        button.setFocusPainted(false);
    
        button.setPreferredSize(new Dimension(150, 40));  
        
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.decode("#616161"));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.decode("#424242"));
            }
        });

        button.setToolTipText(text);
        return button;
    }
    

    @SuppressWarnings("unused")
    private void setupButtonListeners() {
        playButton.addActionListener(e -> mediaPlayerComponent.mediaPlayer().controls().play());
        pauseButton.addActionListener(e -> mediaPlayerComponent.mediaPlayer().controls().pause());
        forwardButton.addActionListener(e -> mediaPlayerComponent.mediaPlayer().controls().skipTime(10000));
        backwardButton.addActionListener(e -> mediaPlayerComponent.mediaPlayer().controls().skipTime(-10000));
    }

    private void updateProgressBar() {
        long currentTime = mediaPlayerComponent.mediaPlayer().status().time();
        long totalTime = mediaPlayerComponent.mediaPlayer().media().info().duration();
        progressBar.setMaximum((int) totalTime);
        progressBar.setValue((int) currentTime);
    }

    private void updateLabels() {
        long currentTime = mediaPlayerComponent.mediaPlayer().status().time();
        long totalTime = mediaPlayerComponent.mediaPlayer().media().info().duration();
        currentTimeLabel.setText(formatTime(currentTime));
        totalTimeLabel.setText(formatTime(totalTime));
    }

    private String formatTime(long time) {
        long hours = TimeUnit.MILLISECONDS.toHours(time);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(time) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(time) % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public void loadMedia(File mediaFile) {
        if (mediaFile.exists()) {
            mediaPlayerComponent.mediaPlayer().media().play(mediaFile.getAbsolutePath());
        } else {
            JOptionPane.showMessageDialog(this, "File not found!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void stopMedia() {
        mediaPlayerComponent.mediaPlayer().controls().stop();
        mediaPlayerComponent.release();
    }

    class WindowCloseHandler extends java.awt.event.WindowAdapter {
        private final MediaPlayerUI mediaPlayerUI;

        public WindowCloseHandler(MediaPlayerUI mediaPlayerUI) {
            this.mediaPlayerUI = mediaPlayerUI;
        }

        @Override
        public void windowClosing(java.awt.event.WindowEvent event) {
            mediaPlayerUI.stopMedia();
        }
    }

    class MediaPlayerEventHandler extends MediaPlayerEventAdapter {
        private final MediaPlayerUI mediaPlayerUI;

        public MediaPlayerEventHandler(MediaPlayerUI mediaPlayerUI) {
            this.mediaPlayerUI = mediaPlayerUI;
        }

        @Override
        public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
            SwingUtilities.invokeLater(() -> {
                mediaPlayerUI.updateProgressBar();
                mediaPlayerUI.updateLabels();
            });
        }

        @Override
        public void lengthChanged(MediaPlayer mediaPlayer, long newLength) {
            SwingUtilities.invokeLater(() -> mediaPlayerUI.updateLabels());
        }
    }
}
