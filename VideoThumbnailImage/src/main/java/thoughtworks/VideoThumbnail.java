package thoughtworks;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Pattern;

public class VideoThumbnail {
    private static final Logger logger = Logger.getLogger("Video logger");

    private static boolean isVideoFile(String extension) {
        List<String> supportedFileFormats = Arrays.asList(
                "MKV", "mov", "3gpp", "avi", "AVI", "MOV", "MP4", "mkv", "mp4");
        return supportedFileFormats.contains(extension);
    }

    public static void main(String[] args) throws IOException, Exception {
        SimpleFormatter formatter = new SimpleFormatter();

        Handler fileHandler = new FileHandler(args[0]+ "/logger.log", true);
        fileHandler.setFormatter(formatter);
        logger.addHandler(fileHandler);
        File directory = new File(args[0]);
        logger.setUseParentHandlers(false);

        File[] patientFolders = directory.listFiles();
        if (patientFolders != null) {
            for (File eachPatientFolder : patientFolders) {
                File[] listOfFiles = eachPatientFolder.listFiles();
                if (listOfFiles != null) {
                    for (File patientFile : listOfFiles) {
                        if (patientFile.isFile()) {
                            String absoluteFilePath = patientFile.getAbsolutePath();
                            String[] nameAndExtension = absoluteFilePath.split(Pattern.quote("."));
                            String extension = nameAndExtension[1];
                            String absoluteFileNameWithoutExtn = nameAndExtension[0];
                            File thumbnailFile = new File(absoluteFileNameWithoutExtn + "_thumbnail.jpg");
                            if (isVideoFile(extension) && !thumbnailFile.exists()) {
                                try {
                                    FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(patientFile);
                                    frameGrabber.start();
                                    Frame frame1 = frameGrabber.grabKeyFrame();
                                    Java2DFrameConverter java2DFrameConverter = new Java2DFrameConverter();
                                    BufferedImage bufferedImage = java2DFrameConverter.convert(frame1);
                                    BufferedImage reSizedImage = Scalr.resize(bufferedImage, 300);
                                    File thumbnail = new File(absoluteFileNameWithoutExtn + "_thumbnail.jpg");
                                    ImageIO.write(reSizedImage, "jpg", thumbnail);
                                    reSizedImage.flush();
                                    logger.info(thumbnail.getName() + " created");
                                    frameGrabber.stop();
                                } catch (IllegalArgumentException exception) {
                                    logger.severe("Could not create thumbnail for " + absoluteFilePath);
                                } catch (Exception e) {
                                    logger.severe("Exception: Could not create thumbnail for " + absoluteFilePath);
                                }
                            }
                        }
                    }
                }
            }
        }

        fileHandler.flush();
    }
}