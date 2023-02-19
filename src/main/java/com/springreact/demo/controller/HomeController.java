package com.springreact.demo.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

//@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@CrossOrigin(origins = "https://qrappcreate.herokuapp.com", maxAge = 3600)
@RestController
@RequestMapping(path = "/api")
public class HomeController {

    private int countOfVisit=0;

    @GetMapping("/home")
    public ResponseEntity<String> home() {

        return ResponseEntity.ok("homex");
    }

    @GetMapping("/home2")
    public ResponseEntity<String> home2(String txt, String url) {

        return ResponseEntity.ok("Good");
    }

    @GetMapping("/readfile")
    public ResponseEntity<String> readfile() throws IOException {
        //try(FileWriter fw = new FileWriter("logs.txt", true);
        //    BufferedWriter writers = new BufferedWriter(fw);) {

        //    writers.write(String.valueOf(System.currentTimeMillis()) + "\n\r");
       // }
        String content = new String(Files.readAllBytes(Paths.get("logs.txt")));

        return ResponseEntity.ok(content);
    }

    @GetMapping("/getcount")
    public ResponseEntity<String> getcount() throws IOException {
        //try(FileWriter fw = new FileWriter("logs.txt", true);
        //    BufferedWriter writers = new BufferedWriter(fw);) {

        //    writers.write(String.valueOf(System.currentTimeMillis()) + "\n\r");
        // }


        return ResponseEntity.ok(String.valueOf(countOfVisit));
    }

    @GetMapping("/resetcount")
    public ResponseEntity<String> resetcount() throws IOException {
        //try(FileWriter fw = new FileWriter("logs.txt", true);
        //    BufferedWriter writers = new BufferedWriter(fw);) {

        //    writers.write(String.valueOf(System.currentTimeMillis()) + "\n\r");
        // }

    countOfVisit=0;
        return ResponseEntity.ok("countOfVisit reset to 0 successfully");
    }

    @GetMapping("/getimage")
    @ResponseBody
    public ResponseEntity<InputStreamResource> getImageDynamicType(@RequestParam("jpg") boolean jpg) throws IOException {
        MediaType contentType = jpg ? MediaType.IMAGE_JPEG : MediaType.IMAGE_PNG;
        URL url = new URL("https://i.ibb.co/dG3dDnb/1023341.jpg");
        InputStream is = url.openStream();




        return ResponseEntity.ok()
                .contentType(contentType)
                .body(new InputStreamResource(is));
    }

    private final String DIR = "D:\\Files\\qr\\";
    private final String ext = ".png";
    private String LOGO = "https://i.ibb.co/WBHd0kM/badrobot.jpg";
    private String CONTENT = "allan rae tayag G5201684W";
    private final int WIDTH = 400;
    private final int HEIGHT = 400;

    @GetMapping("/qr")
    @ResponseBody
    public ResponseEntity<InputStreamResource> generate(@RequestParam("urlStr") String urlStr) throws IOException {
        // Create new configuration that specifies the error correction
        Map<EncodeHintType, ErrorCorrectionLevel> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        String[] arrStr=urlStr.split(";");

        LOGO=arrStr[1];
        CONTENT=arrStr[0];

        boolean qrColorAvail= arrStr.length>2 ? true:false;

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try(FileWriter fw = new FileWriter("logs.txt", true);
            BufferedWriter writers = new BufferedWriter(fw);) {

            writers.write("["+ String.valueOf(LocalDateTime.now()) + " " + urlStr + "] "+ "\n\r");
        }

        try {

            if(countOfVisit>30)
                CONTENT="Only 30 generations per day allowed.";

            // init directory
            //cleanDirectory(DIR);
            initDirectory(DIR);
            // Create a qr code with the url as content and a size of WxH px
            bitMatrix = writer.encode(CONTENT, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, hints);
            BufferedImage qrImage;

            // Load QR image
            if(qrColorAvail)
                qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix, getMatrixConfig(arrStr[2].toUpperCase(),arrStr[3].toUpperCase()));
            else
                qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix, getMatrixConfig());

            // Load logo image
            BufferedImage overly = getOverly(LOGO);

            // Calculate the delta height and width between QR code and logo
            int deltaHeight = qrImage.getHeight() - overly.getHeight();
            int deltaWidth = qrImage.getWidth() - overly.getWidth();

            // Initialize combined image
            BufferedImage combined = new BufferedImage(qrImage.getHeight(), qrImage.getWidth(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = (Graphics2D) combined.getGraphics();

            // Write QR code to new image at position 0/0
            g.drawImage(qrImage, 0, 0, null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

            // Write logo into combine image at position (deltaWidth / 2) and
            // (deltaHeight / 2). Background: Left/Right and Top/Bottom must be
            // the same space for the logo to be centered
            g.drawImage(overly, (int) Math.round(deltaWidth / 2), (int) Math.round(deltaHeight / 2), null);

            // Write combined image as PNG to OutputStream
            ImageIO.write(combined, "png", os);
            // Store Image
            //Files.copy( new ByteArrayInputStream(os.toByteArray()), Paths.get(DIR + generateRandoTitle(new Random(), 9) +ext), StandardCopyOption.REPLACE_EXISTING);

            MediaType contentType = MediaType.IMAGE_JPEG;
            //URL url = new URL("https://i.ibb.co/dG3dDnb/1023341.jpg");
            InputStream is = new ByteArrayInputStream(os.toByteArray());

            countOfVisit++;
            return ResponseEntity.ok()
                    .contentType(contentType)
                    .body(new InputStreamResource(is));

        } catch (WriterException e) {
            e.printStackTrace();
            //LOG.error("WriterException occured", e);
        } catch (IOException e) {
            e.printStackTrace();
            //LOG.error("IOException occured", e);
        }

        return null;
    }

    @GetMapping(value="/qr2")
    @ResponseBody
    public ResponseEntity<byte[]> generate2(@RequestParam("urlStr") String urlStr, @RequestParam("thumbnail") String thumbnail) {
        // Create new configuration that specifies the error correction
        Map<EncodeHintType, ErrorCorrectionLevel> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        LOGO=thumbnail;
        CONTENT=urlStr;

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            // init directory
            //cleanDirectory(DIR);
            initDirectory(DIR);
            // Create a qr code with the url as content and a size of WxH px
            bitMatrix = writer.encode(CONTENT, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, hints);

            // Load QR image
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix, getMatrixConfig());

            // Load logo image
            BufferedImage overly = getOverly(LOGO);

            // Calculate the delta height and width between QR code and logo
            int deltaHeight = qrImage.getHeight() - overly.getHeight();
            int deltaWidth = qrImage.getWidth() - overly.getWidth();

            // Initialize combined image
            BufferedImage combined = new BufferedImage(qrImage.getHeight(), qrImage.getWidth(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = (Graphics2D) combined.getGraphics();

            // Write QR code to new image at position 0/0
            g.drawImage(qrImage, 0, 0, null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

            // Write logo into combine image at position (deltaWidth / 2) and
            // (deltaHeight / 2). Background: Left/Right and Top/Bottom must be
            // the same space for the logo to be centered
            g.drawImage(overly, (int) Math.round(deltaWidth / 2), (int) Math.round(deltaHeight / 2), null);


            // Write combined image as PNG to OutputStream
            ImageIO.write(combined, "png", os);
            // Store Image
            //Files.copy( new ByteArrayInputStream(os.toByteArray()), Paths.get(DIR + generateRandoTitle(new Random(), 9) +ext), StandardCopyOption.REPLACE_EXISTING);

            MediaType contentType = MediaType.IMAGE_JPEG;
            //URL url = new URL("https://i.ibb.co/dG3dDnb/1023341.jpg");
            InputStream is = new ByteArrayInputStream(os.toByteArray());

            byte[] base64encodedData = Base64.getEncoder().encode(os.toByteArray());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +
                            generateRandoTitle(new Random(), 9) +ext + "\"")
                    .body(base64encodedData);

        } catch (WriterException e) {
            e.printStackTrace();
            //LOG.error("WriterException occured", e);
        } catch (IOException e) {
            e.printStackTrace();
            //LOG.error("IOException occured", e);
        }

        return null;
    }

    public static int toARGB(String nm) {
        Long intval = Long.decode(nm);
        long i = intval.intValue();

        int a = (int) ((i >> 24) & 0xFF);
        int r = (int) ((i >> 16) & 0xFF);
        int g = (int) ((i >> 8) & 0xFF);
        int b = (int) (i & 0xFF);

        return ((a & 0xFF) << 24) |
                ((b & 0xFF) << 16) |
                ((g & 0xFF) << 8)  |
                ((r & 0xFF) << 0);
    }
    private BufferedImage getOverly(String LOGO) throws IOException {
        URL url = new URL(LOGO);
        return ImageIO.read(url);
    }

    private void initDirectory(String DIR) throws IOException {
        Files.createDirectories(Paths.get(DIR));
    }

    private void cleanDirectory(String DIR) {
        try {
            Files.walk(Paths.get(DIR), FileVisitOption.FOLLOW_LINKS)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            // Directory does not exist, Do nothing
        }
    }

    private MatrixToImageConfig getMatrixConfig() {
        // ARGB Colors
        // Check Colors ENUM
        return new MatrixToImageConfig(Color.BLACK.getRGB(), MatrixToImageConfig.WHITE);
    }

    private MatrixToImageConfig getMatrixConfig(String qrColor, String qrBackgroundColor) {
        // ARGB Colors
        // Check Colors ENUM

        if(qrBackgroundColor.toUpperCase().equals("WHITE")){

            if(qrColor.toUpperCase().equals("RED")){
                return new MatrixToImageConfig(Color.RED.getRGB(), MatrixToImageConfig.WHITE);
            }
            if(qrColor.toUpperCase().equals("BLUE")){
                return new MatrixToImageConfig(Color.BLUE.getRGB(), MatrixToImageConfig.WHITE);
            }
            if(qrColor.toUpperCase().equals("GREEN")){
                return new MatrixToImageConfig(Color.GREEN.getRGB(), MatrixToImageConfig.WHITE);
            }
            if(qrColor.toUpperCase().equals("PINK")){
                return new MatrixToImageConfig(Color.PINK.getRGB(), MatrixToImageConfig.WHITE);
            }
            if(qrColor.toUpperCase().equals("ORANGE")){
                return new MatrixToImageConfig(Color.ORANGE.getRGB(), MatrixToImageConfig.WHITE);
            }
            if(qrColor.toUpperCase().equals("YELLOW")){
                return new MatrixToImageConfig(Color.YELLOW.getRGB(), MatrixToImageConfig.WHITE);
            }
            if(qrColor.toUpperCase().equals("MAGENTA")){
                return new MatrixToImageConfig(Color.MAGENTA.getRGB(), MatrixToImageConfig.WHITE);
            }
            if(qrColor.toUpperCase().equals("BLACK")){
                return new MatrixToImageConfig(Color.BLACK.getRGB(), MatrixToImageConfig.WHITE);
            }
            return new MatrixToImageConfig(Color.BLACK.getRGB(), MatrixToImageConfig.WHITE);
        }
        else
        {
            if(qrColor.toUpperCase().equals("RED")){
                return new MatrixToImageConfig(Color.RED.getRGB(), MatrixToImageConfig.BLACK);
            }
            if(qrColor.toUpperCase().equals("BLUE")){
                return new MatrixToImageConfig(Color.BLUE.getRGB(), MatrixToImageConfig.BLACK);
            }
            if(qrColor.toUpperCase().equals("GREEN")){
                return new MatrixToImageConfig(Color.GREEN.getRGB(), MatrixToImageConfig.BLACK);
            }
            if(qrColor.toUpperCase().equals("PINK")){
                return new MatrixToImageConfig(Color.PINK.getRGB(), MatrixToImageConfig.BLACK);
            }
            if(qrColor.toUpperCase().equals("ORANGE")){
                return new MatrixToImageConfig(Color.ORANGE.getRGB(), MatrixToImageConfig.BLACK);
            }
            if(qrColor.toUpperCase().equals("YELLOW")){
                return new MatrixToImageConfig(Color.YELLOW.getRGB(), MatrixToImageConfig.BLACK);
            }
            if(qrColor.toUpperCase().equals("MAGENTA")){
                return new MatrixToImageConfig(Color.MAGENTA.getRGB(), MatrixToImageConfig.BLACK);
            }
            if(qrColor.toUpperCase().equals("WHITE")){
                return new MatrixToImageConfig(Color.WHITE.getRGB(), MatrixToImageConfig.BLACK);
            }
            return new MatrixToImageConfig(Color.WHITE.getRGB(), MatrixToImageConfig.BLACK);
        }

       // return new MatrixToImageConfig(Color.BLACK.getRGB(), MatrixToImageConfig.BLACK);
    }

    private String generateRandoTitle(Random random, int length) {
        return random.ints(48, 122)
                .filter(i -> (i < 57 || i > 65) && (i < 90 || i > 97))
                .mapToObj(i -> (char) i)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    public enum Colors {

        BLUE(0xFF40BAD0),
        RED(0xFFE91C43),
        PURPLE(0xFF8A4F9E),
        ORANGE(0xFFF4B13D),
        WHITE(0xFFFFFFFF),
        BLACK(0xFF000000);

        private final int argb;

        Colors(final int argb){
            this.argb = argb;
        }

        public int getArgb(){
            return argb;
        }
    }
}
