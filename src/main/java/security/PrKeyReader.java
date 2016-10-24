package security;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by USER on 24.10.2016.
 */
public class PrKeyReader {
    public static byte[] getPrKey(String login) throws FileNotFoundException {
        byte[] part1 = getFromSMARTCARD(login);
        byte[] part2 = getFromLocal(login);
        ByteBuffer bb = ByteBuffer.allocate(part1.length + part2.length);
        bb.put(part1);
        bb.put(part2);

        return bb.array();
    }

    private static byte[] getFromLocal(String login) throws FileNotFoundException {
        String localFile = "pr_key/" + login + "_KBRS2";
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        URL url1 = loader.getResource(localFile);
        int firstFileLength = 0;
        if (url1 == null) {
            throw new FileNotFoundException(localFile + " is not exist.");
        }
        File p1 = new File(url1.getFile());
        byte[] bytes = new byte[0];
        try {
            FileInputStream fs = new FileInputStream(p1);
            bytes = new byte[700];
            firstFileLength = fs.read(bytes);
            if (firstFileLength < 600) {
                throw new IOException(localFile + " is corrupted.");
            }
            fs.close();
        } catch (IOException ignored) {}
        return Arrays.copyOfRange(bytes, 0, firstFileLength);
    }

    private static byte[] getFromSMARTCARD(String login) throws FileNotFoundException {
        String usbFile = login + "_KBRS";
        File usb;
        try {
            usb = File.listRoots()[3];
        } catch (IndexOutOfBoundsException e) {
            throw new FileNotFoundException("Insert you smartCard.");
        }
        FileInputStream fis;
        try {
            fis = new FileInputStream(usb.toString() + usbFile);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(usb.toString() + usbFile + " is not found");
        }

        int fileLength = 0;
        byte[] bytes = new byte[0];
        try {
            bytes = new byte[700];
            fileLength = fis.read(bytes);
            if (fileLength < 600) {
                throw new IOException(usb.toString() + usbFile + " is corrupted.");
            }
            fis.close();
        } catch (IOException ignored) {}
        return Arrays.copyOfRange(bytes, 0, fileLength);
    }
}
