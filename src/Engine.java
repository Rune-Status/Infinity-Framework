import java.util.logging.Logger;

public class Engine {

    /**
     * Run the engine loop ;
     * <p>
     * (Currently not used)
     *
     * @Start
     */
    public void start() {
        Infinity.loading_message = ("Launch");
        isRunning = true;
        while (isRunning) {
            long time = System.currentTimeMillis();

            update();

            // Delay
            time = (1000 / FPS) - (System.currentTimeMillis() - time);

            if (time > 0) {
                try {
                    Thread.sleep(time);
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * Update important variables ; Setting various
     * integers or booleans;
     *
     * @Update_Process
     */
    public void update() {
        Infinity.frame.getContentPane().setVisible(true);
        Infinity.frame.getContentPane().requestFocus();

        if (!updatedAssets) {
            try {
                //logger.info("Running : Asset Update Process");
                Infinity.loading_message = "Fetching Assets";
                //UpdateAssets();
            } catch (Exception e) {
                logger.info("Unable to check assets..");
            }
        }
        if (Infinity.is_client_downloaded && !Infinity.is_loading) {
            Infinity.frame.getContentPane().setVisible(true);
            isRunning = false;
        }
    }

    boolean isRunning;
    public final int FPS = 8;
    boolean updatedAssets = false;
    public final static Logger logger = Logger.getLogger(Engine.class.getName());
}
