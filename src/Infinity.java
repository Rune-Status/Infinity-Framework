/*************************************************************
 * @file: Infinity.java
 * @source: adapted from Horstmann and Cornell, Core Java
 * @history: Visualization Course (framework);
 *
 *
 * TERMS OF USE SHOULD REMAIN IN THIS PROGRAM AT ALL TIMES
 *
 * THIS IS A FREE PROJECT ALL ADDITIONS OR USES
 * SHOULD NOT BE SOLD. THIS IS FOR EDUCATION ONLY AND
 * MEANT TO HELP THE COMMUNITY NOT PROFIT OFF IT!
 *
 * THANKS
 *
 * @trees <rune-server.ee/members/trees><snowman5069@icloud.com>
 *************************************************************/

import javafx.stage.Screen;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Logger;
import javax.swing.*;

class Infinity extends JPanel {

    public static void startUp() {
        engine.start();
        frame.show();
    }

    public static void main(final String... args)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException, IOException,
            UnsupportedLookAndFeelException {

        try {
            showScreen();
        } catch (final MalformedURLException e) {
            Infinity.logger.severe("Encounter error: " + e.getClass());
            Infinity.logger.severe("Error message: " + e.getMessage());
            Infinity.logger.severe("Error cause: " + e.getCause());
        }
        final SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {

            @Override
            protected Void doInBackground() throws Exception {
                logger.info("Attempting to grab jar");
                jarGrabber();
                return null;
            }

            @Override
            protected void done() {
                Infinity.is_loading = false;
                frame.getContentPane().add(instance, BorderLayout.CENTER);
                instance.init();
                instance.setBackground(Color.BLACK);
            }

        };
        worker.execute();
        Infinity.startUp();
        Infinity.is_loading = false;
    }

    public void paintComponent(Graphics g) {
        Graphics2D graphics2d = (Graphics2D) g;
        graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        super.paintComponent(g);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        width = frame.getWidth();
        height = frame.getHeight();

        Font f = new Font("SansSerif", Font.BOLD, 14);
        Font fi = new Font("SansSerif", Font.BOLD + Font.ITALIC, 14);

        if (is_loading) {
            g.setFont(f);
            Image img1 = Toolkit.getDefaultToolkit().getImage("infinity.gif");
            graphics2d.drawImage(img1, cx - (400 / 2), cy - (300 / 2), this);
            g.setFont(fi);
            g.drawString(loading_message, cx - (g.getFontMetrics().stringWidth(loading_message) / 2), cy + 40);
        } else {
            ((Graphics2D) g).setBackground(Color.BLACK);
            instance.setBounds(width / 2 - (760 / 2), height / 2 - (565 / 2), gamePanel.getWidth(), gamePanel.getHeight());
            frame.paintAll(g);
            if (!client_launched) {
                frame.setLayout(new BorderLayout());
                gamePanel.setLayout(new BorderLayout());
                gamePanel.add(this, BorderLayout.CENTER);
                gamePanel.setBackground(Color.BLACK);
                frame.getContentPane().add(gamePanel, BorderLayout.CENTER);
                frame.pack();
                frame.setMinimumSize(new Dimension(760, 565));
                client_launched = true;
            }

        }
    }

    static class Frame extends JFrame {
        public Frame(String s) {
            setTitle(s);
            setSize(300, 150);
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
            Container contentPane = getContentPane();
            contentPane.add(new Infinity());
        }
    }

    protected static void showScreen() throws MalformedURLException {
        final JLabel background = new JLabel(new ImageIcon(
                new URL(SPLASH_IMAGE)));
        //background.setOpaque(true);
        // background.setLayout(new BorderLayout());
        frame.setVisible(true);
        Infinity.is_loading = true;
    }

    public static void jarGrabber() throws ClassNotFoundException,
            InstantiationException, IllegalAccessException, IOException,
            UnsupportedLookAndFeelException {
        final URL url = new URL(JAR_URL);
        final InputStream is = url.openStream();
        final byte[] b = new byte[2048];
        int length;
        final HttpURLConnection connection = (HttpURLConnection) url
                .openConnection();
        loading_message = "Fetching Jar";

        connection.setRequestProperty("Range", "bytes=" + downloaded + "-");
        connection.connect();

        if ((connection.getResponseCode() / 100) != 2) {
            logger.info("Unable to find file");
            return;
        }

        size = connection.getContentLength();
        while ((length = is.read(b)) != -1) {
            downloaded += length;
        }
        is.close();
        classLoader = new URLClassLoader(new URL[]{(url)});
        instance = (Applet) classLoader.loadClass(MAIN_CLASS)
                .newInstance();
        logger.info("[Successfully Loaded Jar Applet] : " + instance);
        Infinity.is_client_downloaded = true;
        instance.init();
        frame.setLayout(new BorderLayout());
        frame.setBackground(Color.BLACK);

    }

    public final static Logger logger = Logger.getLogger(Infinity.class.getName());

    public static boolean is_client_downloaded = false;
    public static boolean is_loading = true;
    public static String loading_message = "";
    public static boolean client_launched = false;

    JPanel gamePanel = new JPanel();

    int width;
    int height;

    static Frame frame = new Frame("Infinity");
    static Engine engine = new Engine();

    public static Applet instance = new Applet();
    private static String MAIN_CLASS = "Client";
    private static URLClassLoader classLoader;
    private final static String JAR_URL = "http://www.smite.io/game_assets/smite.jar";
    public final static String SPLASH_IMAGE = "https://vignette3.wikia.nocookie.net/runescape2/images/8/86/Logging_in_-_please_wait.gif/revision/latest?cb=20141125024917";

    private static int downloaded; // number of bytes downloaded
    private static int size; // size of download in bytes

}
