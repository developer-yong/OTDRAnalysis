import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * @author coderyong
 */
public class OTDRAnalysisTest {

    private static int LENGTH_SHORT = 2;
    private static int LENGTH_LONG = 4;
    //光速
    private static double SOL = 0.299792458D;

    public static void main(String[] args) throws IOException {
        read("test.sor");
    }

    public static void read(String fileName) throws IOException {
        read(new FileInputStream(fileName));
    }

    public static void read(InputStream input) throws IOException {
        byte[] content = new byte[input.available()];
        if (input.read(content) > 0) {
            read(content);
        }
        input.close();
    }

    public static void read(byte[] content) {
        int offset = 0;
        System.out.println("\n================= Map =================");
        //读取并设置Map区块名称
        String mapBlockId = readStringSpaceZero(content, offset);
        System.out.println("Map Block ID : " + mapBlockId);

        offset += mapBlockId.getBytes().length + 1;
        System.out.println("Map Version : " + readInt(content, offset, LENGTH_SHORT));

        offset += LENGTH_SHORT;
        int mapLength = readInt(content, offset, LENGTH_LONG);
        System.out.println("Map Length : " + mapLength);

        offset += LENGTH_LONG;
        System.out.println("Map Block Count : " + readInt(content, offset, LENGTH_SHORT));

        byte[] mapContent = new byte[mapLength];
        System.arraycopy(content, 0, mapContent, 0, mapLength);
        System.out.println("Map Block Content : " + Arrays.toString(mapContent));
        System.out.println("================= Map =================\n");

        offset += LENGTH_SHORT;
        int contentOffset = mapLength;
        while (offset < mapLength) {

            String blockId = readStringSpaceZero(content, offset);
            System.out.println("\n================= " + blockId + " =================");
            System.out.println("Block ID : " + blockId);

            offset += blockId.getBytes().length + 1;
            System.out.println("Version : " + readInt(content, offset, LENGTH_SHORT));

            offset += LENGTH_SHORT;
            //读取并设置Map区块长度
            int length = readInt(content, offset, LENGTH_LONG);
            System.out.println("Length : " + length);
            offset += 4;

            //设置区块字节内容
            byte[] blockContent = new byte[length];
            System.arraycopy(content, contentOffset, blockContent, 0, length);
            System.out.println("Block Content : " + Arrays.toString(blockContent));

            switch (blockId) {
                case "GenParams":
                    printGenParams(content, contentOffset);
                    break;
                case "SupParams":
                    printSupParams(content, contentOffset);
                    break;
                case "FxdParams":
                    printFxdParams(content, contentOffset);
                    break;
                case "KeyEvents":
                    printKeyEvents(content, contentOffset);
                    break;
                case "LnkParams":
                    printLnkParams(content, contentOffset);
                    break;
                case "DataPts":
                    printDataPts(content, contentOffset);
                    break;
                case "Cksum":
                    printChecksum(content, contentOffset);
                    break;
            }
            contentOffset += length;

            System.out.println("================= " + blockId + " =================\n");
        }
    }

    private static void printGenParams(byte[] content, int offset) {

        String name = readStringSpaceZero(content, offset);
        offset += name.getBytes().length + 1;
        System.out.println("Language Code : " + readString(content, offset, 2));

        offset += 2;
        String cableId = readStringSpaceZero(content, offset);
        System.out.println("Cable ID : " + cableId);

        offset += cableId.getBytes().length + 1;
        String fiberId = readStringSpaceZero(content, offset);
        System.out.println("Fiber ID : " + fiberId);

        offset += fiberId.getBytes().length + 1;
        System.out.println("Fiber Type : " + readInt(content, offset, 2));

        offset += 2;
        System.out.println("Wavelength : " + readInt(content, offset, 2));

        offset += 2;
        String originating = readStringSpaceZero(content, offset);
        System.out.println("Originating Location : " + originating);

        offset += originating.getBytes().length + 1;
        String terminating = readStringSpaceZero(content, offset);
        System.out.println("Terminating Location : " + terminating);

        offset += terminating.getBytes().length + 1;
        String cableCode = readStringSpaceZero(content, offset);
        System.out.println("Cable Code : " + cableCode);

        offset += cableCode.getBytes().length + 1;
        String flag = readString(content, offset, 2);
        System.out.println("Current Data Flag : " + flag);

        offset += 2;
        System.out.println("User Offset : " + readInt(content, offset, 4));

        offset += 4;
        System.out.println("User Offset Distance : " + readInt(content, offset, 4));

        offset += 4;
        String operator = readStringSpaceZero(content, offset);
        System.out.println("Operator : " + operator);

        offset += operator.getBytes().length + 1;
        String comment = readStringSpaceZero(content, offset);
        System.out.println("Comment : " + comment);
    }

    private static void printSupParams(byte[] content, int offset) {
        String name = readStringSpaceZero(content, offset);
        offset += name.getBytes().length + 1;
        String supplierName = readStringSpaceZero(content, offset);
        System.out.println("Supplier Name : " + supplierName);

        offset += supplierName.getBytes().length + 1;
        String mainframeId = readStringSpaceZero(content, offset);
        System.out.println("OTDR Mainframe ID : " + mainframeId);

        offset += mainframeId.getBytes().length + 1;
        String mainframeSOrN = readStringSpaceZero(content, offset);
        System.out.println("OTDR Mainframe S/N : " + mainframeSOrN);

        offset += mainframeSOrN.getBytes().length + 1;
        String moduleId = readStringSpaceZero(content, offset);
        System.out.println("Optical Module ID : " + moduleId);

        offset += moduleId.getBytes().length + 1;
        String moduleSOrN = readStringSpaceZero(content, offset);
        System.out.println("Optical Module S/N : " + moduleSOrN);

        offset += moduleSOrN.getBytes().length + 1;
        String revision = readStringSpaceZero(content, offset);
        System.out.println("Software Revision : " + revision);

        offset += revision.getBytes().length + 1;
        String other = readStringSpaceZero(content, offset);
        System.out.println("Other : " + other);
    }

    private static void printFxdParams(byte[] content, int offset) {
        String name = readStringSpaceZero(content, offset);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        offset += name.getBytes().length + 1;
        System.out.println("Date/Time Stamp : " + format.format(new Date(readInt(content, offset, 4) * 1000)));

        offset += 4;
        System.out.println("Units of Distance : " + readString(content, offset, 2));

        offset += 2;
        System.out.println("Actual Wavelength : " + (float) readInt(content, offset, 2) / 10 + " nm");

        offset += 2;
        System.out.println("Acquisition Offset : " + readInt(content, offset, 4));

        offset += 4;
        System.out.println("Acquisition Offset Distance : " + readInt(content, offset, 4));

        offset += 4;
        int number = readInt(content, offset, 2);
        System.out.println("Total Number of Pulse Widths Used : " + number);

        int[] pulseWidths = new int[number];
        offset += 2;
        for (int i = 0; i < number; i++) {
            pulseWidths[i] = readInt(content, offset, 2);
            offset += 2;
        }
        System.out.println("Pulse Widths Used : " + Arrays.toString(pulseWidths));

        int[] dataSpacing = new int[number];
        for (int i = 0; i < number; i++) {
            dataSpacing[i] = readInt(content, offset, 4);
            offset += 4;
        }
        System.out.println("Data Spacing : " + Arrays.toString(dataSpacing));

        int[] dataPoints = new int[number];
        for (int i = 0; i < number; i++) {
            dataPoints[i] = readInt(content, offset, 4);
            offset += 4;
        }
        System.out.println("Data Points : " + Arrays.toString(dataPoints));

        System.out.println("Group Index : " + readInt(content, offset, 4) / 100000F);

        offset += 4;
        System.out.println("Backscatter Coefficient : " + readInt(content, offset, 2) / 10F);

        offset += 2;
        System.out.println("Number of Averages : " + readInt(content, offset, 4));

        offset += 4;
        System.out.println("Averaging Time : " + readInt(content, offset, 2));

        offset += 2;
        System.out.println("Acquisition Range : " + readInt(content, offset, 4));

        offset += 4;
        System.out.println("Acquisition Range Distance : " + readInt(content, offset, 4));

        offset += 4;
        System.out.println("Front Panel Offset : " + readInt(content, offset, 4));

        offset += 4;
        System.out.println("Noise Floor Level : " + readInt(content, offset, 2) / 1000F + " dB");

        offset += 2;
        System.out.println("Noise Floor Scale Factor : " + readInt(content, offset, 2) / 1000F);

        offset += 2;
        System.out.println("Power Offset First Point : " + readInt(content, offset, 2) / 1000F + " dB");

        offset += 2;
        System.out.println("Loss Threshold : " + readInt(content, offset, 2) / 1000F + " dB");

        offset += 2;
        System.out.println("Reflectance Threshold : " + readInt(content, offset, 2) / 1000F + " dB");

        offset += 2;
        System.out.println("End-of Fiber Threshold : " + readInt(content, offset, 2) / 1000F + " dB");

        offset += 2;
        String traceType = readString(content, offset, 2);
        System.out.println("Trace Type : " + traceType);

        offset += 2;
        int[] coordinates = new int[4];
        for (int i = 0; i < coordinates.length; i++) {
            coordinates[i] = readInt(content, offset, 4);
            offset += 4;
        }
        System.out.println("Window Coordinates : " + Arrays.toString(coordinates));
    }

    private static void printKeyEvents(byte[] content, int offset) {
        String name = readStringSpaceZero(content, offset);
        offset += name.getBytes().length + 1;
        int eventCount = readInt(content, offset, 2);
        System.out.println("Number of Key Events : " + eventCount);

        offset += 2;
        System.out.println("[");
        for (int i = 0; i < eventCount; i++) {
            System.out.println("    {");
            System.out.println("        Event Number : " + readInt(content, offset, 2));
            offset += 2;
            System.out.println("        Event Propagation Time : " + readInt(content, offset, 4));
            offset += 4;
            System.out.println("        Attenuation Coefficient Lead-in Fiber : "
                    + readInt(content, offset, 2) / 1000F + " dB/Km");
            offset += 2;
            System.out.println("        Event Loss : " + readInt(content, offset, 2) / 1000F + " dB");
            offset += 2;
            System.out.println("        Event Reflectance : " + readInt(content, offset, 4) / 1000F + " dB");
            offset += 4;
            System.out.println("        Event Code : " + readString(content, offset, 6));
            offset += 6;
            System.out.println("        Loss Measurement Technique : " + readString(content, offset, 2));
            offset += 2;

            int[] markerLocations = new int[5];
            for (int j = 0; j < markerLocations.length; j++) {
                markerLocations[j] = readInt(content, offset, 4);
                offset += 4;
            }
            System.out.println("        Marker Locations : " + Arrays.toString(markerLocations));
            String comment = readStringSpaceZero(content, offset);
            System.out.println("        Comment : " + comment);
            offset += comment.getBytes().length + 1;
            System.out.println("    },");
        }
        System.out.println("]");

        System.out.println("End-to-End Loss : " + readInt(content, offset, 4) / 1000F + " dB");
        offset += 4;
        System.out.println("End-to-End Marker Positions start : " + readInt(content, offset, 4));
        offset += 4;
        System.out.println("End-to-End Marker Positions end : " + readInt(content, offset, 4));
        offset += 4;
        System.out.println("Optical Return Loss : " + readInt(content, offset, 2) / 1000F + " ORL");
        offset += 2;
        System.out.println("Optical Return Loss Marker Position start : " + readInt(content, offset, 4));
        offset += 4;
        System.out.println("Optical Return Loss Marker Position end : " + readInt(content, offset, 4));
    }

    private static void printLnkParams(byte[] content, int offset) {

        String name = readStringSpaceZero(content, offset);
        offset += name.getBytes().length + 1;
        int number = readInt(content, offset, 2);
        System.out.println("Total Number of Landmarks : " + number);

        offset += 2;
        System.out.println("[");
        for (int i = 0; i < number; i++) {
            System.out.println("\t{");
            System.out.println("\t\tLandmark Number : " + readInt(content, offset, 2));

            offset += 2;
            System.out.println("\t\tLandmark Code : " + readString(content, offset, 2));

            offset += 2;
            System.out.println("\t\tLandmark Location : " + readInt(content, offset, 4));

            offset += 4;
            System.out.println("\t\tRelated Event Number : " + readInt(content, offset, 2));

            offset += 4;
            int[] gps = new int[2];
            for (int j = 0; j < gps.length; j++) {
                gps[j] = readInt(content, offset, 4);
                offset += 4;
            }
            System.out.println("\t\tGPS Information : " + Arrays.toString(gps));

            System.out.println("\t\tFiber Correction Factor Lead-in Fiber : " + readInt(content, offset, 2) / 100F + "%");

            offset += 2;
            System.out.println("\t\tSheath Marker Entering Landmark : " + readInt(content, offset, 4));

            offset += 4;
            System.out.println("\t\tSheath Marker Leaving Landmark : " + readInt(content, offset, 4));

            offset += 4;
            System.out.println("\t\tUnits of Sheath Marker Leaving Landmark : " + readString(content, offset, 2));

            offset += 2;
            System.out.println("\t\tMode Field Diameter Leaving Landmark : " + readString(content, offset, 2));

            offset += 2;
            System.out.println("\t\tComment : " + readStringSpaceZero(content, offset));

            System.out.println("\t}");
        }
        System.out.println("]");
    }

    private static void printDataPts(byte[] content, int offset) {
        String name = readStringSpaceZero(content, offset);
        offset += name.getBytes().length + 1;
        System.out.println("Number of Data Points : " + readInt(content, offset, 4));

        offset += 4;
        System.out.println("Total Number Scale Factors Used : " + readInt(content, offset, 2));

        offset += 2;
        int number = readInt(content, offset, 4);
        System.out.println("Total Data Points Using Scale Factors : " + readInt(content, offset, 4));

        offset += 4;
        System.out.println("    Scale Factor 1 : " + readInt(content, offset, 2));

        offset += 2;
        int[] data = new int[number];
        for (int i = 0; i < number; i++) {
            data[i] = readInt(content, offset, 2);
            offset += 2;
        }
        System.out.println("    Scale Factor n : " + Arrays.toString(data));
    }

    private static void printChecksum(byte[] content, int offset) {
        String name = readStringSpaceZero(content, offset);
        offset += name.getBytes().length + 1;
        int sum = readInt(content, offset, LENGTH_SHORT);
        System.out.println("Checksum : " + sum);
    }

    /**
     * 读取整型数
     *
     * @param b      字节内容
     * @param offset 起始位置偏移量
     * @param length 整型数字节长度 {@link #LENGTH_SHORT,#LENGTH_LONG}
     * @return "小端" 整型数值
     */
    private static int readInt(byte[] b, int offset, int length) {
        byte[] bytes = new byte[LENGTH_LONG];
        System.arraycopy(b, offset, bytes, 0, length);
        return bytesToInt(bytes);
    }

    /**
     * byte[]转int
     * <p>由低位到高位</P>
     *
     * @param bytes 字节数组
     * @return 整型int
     */
    private static int bytesToInt(byte[] bytes) {
        int value = 0;
        for (int i = 0; i < bytes.length; i++) {
            int shift = i * 8;
            value += (bytes[i] & 0x000000FF) << shift;
        }
        return value;
    }

    /**
     * 读取以0为结束符的字符串
     *
     * @param b      字节内容
     * @param offset 起始位置偏移量
     * @return UTF-8字符串
     */
    private static String readStringSpaceZero(byte[] b, int offset) {
        int length = 0;
        while (length + offset <= b.length && b[length + offset] != 0) {
            length++;
        }
        return readString(b, offset, length);
    }

    /**
     * 读取字符串
     *
     * @param b      字节内容
     * @param offset 起始位置偏移量
     * @param length 读取长度
     * @return UTF-8字符串
     */
    private static String readString(byte[] b, int offset, int length) {
        byte[] bytes = new byte[length];
        System.arraycopy(b, offset, bytes, 0, length);
        return new String(bytes);
    }
}
