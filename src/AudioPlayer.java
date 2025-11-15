import javazoom.jl.player.Player;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Random;

public class AudioPlayer {
    private static final Random random = new Random();

    public static void playSound(String resourcePath) {
        new Thread(() -> {
            try {
                InputStream audioSrc = AudioPlayer.class.getResourceAsStream(resourcePath);
                if (audioSrc == null) {
                    System.err.println("Could not find audio file: " + resourcePath);
                    return;
                }

                InputStream bufferedIn = new BufferedInputStream(audioSrc);
                Player player = new Player(bufferedIn);
                player.play();
                player.close();
            } catch (Exception e) {
                System.err.println("Error playing sound: " + resourcePath);
                e.printStackTrace();
            }
        }).start();
    }

    public static void playRandomDialogue() {
        String[] dialogueFiles = {
            "/resources/dialogue/Clash Royale old King Sounds (mp3cut.net).mp3",
            "/resources/dialogue/Clash Royale old King Sounds (mp3cut.net)-2.mp3",
            "/resources/dialogue/Clash Royale old King Sounds (mp3cut.net)-3.mp3",
            "/resources/dialogue/Clash Royale old King Sounds (mp3cut.net)-4.mp3"
        };

        String randomDialogue = dialogueFiles[random.nextInt(dialogueFiles.length)];
        playSound(randomDialogue);
    }

    public static void playCryingKing() {
        playSound("/resources/cryingKing.mp3");
    }

    public static void playDeploySound(String cardName) {
        // Convert card name to sound file name
        // e.g., "Baby Dragon" -> "baby_dragon_deploy.mp3"
        String soundFileName = cardName.toLowerCase()
            .replace(" ", "_")
            .replace("-", "_") + "_deploy.mp3";

        String soundPath = "/sounds/" + soundFileName;
        playSound(soundPath);
    }
}
