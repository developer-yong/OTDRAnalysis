import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author coderyong
 */
public class OTDRAnalysis {

    private static int LENGTH_SHORT = 2;
    private static int LENGTH_LONG = 4;

    public static void main(String[] args) throws IOException {
        System.out.println(read("test.sor"));
    }

    /**
     * 读取OTDR文件内容
     *
     * @param fileName 文件名
     * @return 区块集合信息
     */
    public static List<Map<String, Object>> read(String fileName) throws IOException {
        return read(new FileInputStream(fileName));
    }

    /**
     * 读取OTDR输入流内容
     *
     * @param input 文件输入流
     * @return 区块集合信息
     */
    public static List<Map<String, Object>> read(InputStream input) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = input.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
        byte[] content = out.toByteArray();
        out.close();
        input.close();
        //获取字节内容
        return read(content);
    }

    /**
     * 读取OTDR字节内容
     *
     * @param content 字节内容
     * @return 区块集合信息
     */
    public static List<Map<String, Object>> read(byte[] content) {
        List<Map<String, Object>> blocks = new ArrayList<>();
        int offset = 0;
        //创建Map块
        Map<String, Object> map = new HashMap<>();
        //读取并设置Map区块名称
        String mapBlockId = readStringSpaceZero(content, offset);
        map.put("blockId", mapBlockId);

        offset += mapBlockId.getBytes().length + 1;
        //读取并设置Map区块版本
        map.put("version", readInt(content, offset, LENGTH_SHORT));

        offset += LENGTH_SHORT;
        //读取并设置Map区块长度
        int mapLength = readInt(content, offset, LENGTH_LONG);
        map.put("length", mapLength);

        offset += LENGTH_LONG;
        //读取并设置区块数
        map.put("blockCount", readInt(content, offset, LENGTH_SHORT));

        //设置区块字节内容
        byte[] mapContent = new byte[mapLength];
        System.arraycopy(content, 0, mapContent, 0, mapLength);
        map.put("content", Arrays.toString(mapContent));
        blocks.add(map);

        offset += LENGTH_SHORT;
        int contentOffset = mapLength;
        while (offset < mapLength) {

            //创建区块
            Map<String, Object> block = new HashMap<>();
            //读取并设置区块名称
            String blockId = readStringSpaceZero(content, offset);
            block.put("blockId", blockId);

            offset += blockId.getBytes().length + 1;
            //读取并设置区块版本
            block.put("version", readInt(content, offset, LENGTH_SHORT));

            offset += LENGTH_SHORT;
            //读取并设置Map区块长度
            int length = readInt(content, offset, LENGTH_LONG);
            block.put("length", length);
            offset += 4;

            //设置区块字节内容
            byte[] blockContent = new byte[length];
            System.arraycopy(content, contentOffset, blockContent, 0, length);
            block.put("content", Arrays.toString(blockContent));

            switch (String.valueOf(block.get("blockId"))) {
                case "GenParams":
                    readGenParams(block, blockContent);
                    break;
                case "SupParams":
                    readSupParams(block, blockContent);
                    break;
                case "FxdParams":
                    readFxdParams(block, blockContent);
                    break;
                case "KeyEvents":
                    readKeyEvents(block, blockContent);
                    break;
                case "LnkParams":
                    readLnkParams(block, blockContent);
                    break;
                case "DataPts":
                    readDataPts(block, blockContent);
                    break;
                case "Cksum":
                    readChecksum(block, blockContent);
                    break;
            }
            blocks.add(block);
            contentOffset += length;
        }
        return blocks;
    }

    /**
     * 读取一般参数块
     *
     * @param block   区块对象
     * @param content 区块字节内容
     */
    private static void readGenParams(Map<String, Object> block, byte[] content) {
        int offset = 0;

        String blockId = readStringSpaceZero(content, offset);
        block.put("blockId", blockId);

        offset += blockId.getBytes().length + 1;
        block.put("languageCode", readString(content, offset, LENGTH_SHORT));

        offset += LENGTH_SHORT;
        String cableId = readStringSpaceZero(content, offset);
        block.put("cableId", cableId);

        offset += cableId.getBytes().length + 1;
        String fiberId = readStringSpaceZero(content, offset);
        block.put("fiberID", fiberId);

        offset += fiberId.getBytes().length + 1;
        block.put("fiberType", readInt(content, offset, LENGTH_SHORT));

        offset += LENGTH_SHORT;
        block.put("wavelength", readInt(content, offset, LENGTH_SHORT));

        offset += LENGTH_SHORT;
        String originating = readStringSpaceZero(content, offset);
        block.put("originatingLocation", originating);

        offset += originating.getBytes().length + 1;
        String terminating = readStringSpaceZero(content, offset);
        block.put("terminatingLocation", terminating);

        offset += terminating.getBytes().length + 1;
        String cableCode = readStringSpaceZero(content, offset);
        block.put("cableCode", cableCode);

        offset += cableCode.getBytes().length + 1;
        String flag = readString(content, offset, LENGTH_SHORT);
        block.put("dataFlag", flag);

        offset += LENGTH_SHORT;
        block.put("userOffset", readInt(content, offset, LENGTH_LONG));

        offset += LENGTH_LONG;
        block.put("userOffsetDistance", readInt(content, offset, LENGTH_LONG));

        offset += LENGTH_LONG;
        String operator = readStringSpaceZero(content, offset);
        block.put("operator", operator);

        offset += operator.getBytes().length + 1;
        String comment = readStringSpaceZero(content, offset);
        block.put("comment", comment);
    }

    /**
     * 读取供应商参数块
     *
     * @param block   区块对象
     * @param content 区块字节内容
     */
    private static void readSupParams(Map<String, Object> block, byte[] content) {
        int offset = 0;

        String blockId = readStringSpaceZero(content, offset);
        block.put("blockId", blockId);

        offset += blockId.getBytes().length + 1;
        String supplierName = readStringSpaceZero(content, offset);
        block.put("supplierName", supplierName);

        offset += supplierName.getBytes().length + 1;
        String mainframeId = readStringSpaceZero(content, offset);
        block.put("mainframeId", mainframeId);

        offset += mainframeId.getBytes().length + 1;
        String mainframeSOrN = readStringSpaceZero(content, offset);
        block.put("mainframeSOrN", mainframeSOrN);

        offset += mainframeSOrN.getBytes().length + 1;
        String moduleId = readStringSpaceZero(content, offset);
        block.put("opticalModuleId", moduleId);

        offset += moduleId.getBytes().length + 1;
        String moduleSOrN = readStringSpaceZero(content, offset);
        block.put("opticalModuleSOrN", moduleSOrN);

        offset += moduleSOrN.getBytes().length + 1;
        String revision = readStringSpaceZero(content, offset);
        block.put("softwareRevision", revision);

        offset += revision.getBytes().length + 1;
        String other = readStringSpaceZero(content, offset);
        block.put("other", other);
    }

    /**
     * 读取固定参数块
     *
     * @param block   区块对象
     * @param content 区块字节内容
     */
    private static void readFxdParams(Map<String, Object> block, byte[] content) {
        int offset = 0;

        String blockId = readStringSpaceZero(content, offset);
        block.put("blockId", blockId);

        offset += blockId.getBytes().length + 1;
        block.put("timeStamp", readInt(content, offset, LENGTH_LONG) * 1000);

        offset += LENGTH_LONG;
        block.put("distanceUnits", readString(content, offset, LENGTH_SHORT));

        offset += LENGTH_SHORT;
        block.put("actualWavelength", readInt(content, offset, LENGTH_SHORT) / 10);

        offset += LENGTH_SHORT;
        block.put("acquisitionOffset", readInt(content, offset, LENGTH_LONG));

        offset += LENGTH_LONG;
        block.put("acquisitionOffsetDistance", readInt(content, offset, LENGTH_LONG));

        offset += LENGTH_LONG;
        int pulseNumber = readInt(content, offset, LENGTH_SHORT);
        block.put("pulseNumber", pulseNumber);

        offset += LENGTH_SHORT;
        int[] pulseWidths = new int[pulseNumber];
        for (int i = 0; i < pulseNumber; i++) {
            pulseWidths[i] = readInt(content, offset, LENGTH_SHORT);
            offset += LENGTH_SHORT;
        }
        block.put("pulseWidths", pulseWidths);

        float[] dataSpacing = new float[pulseNumber];
        for (int i = 0; i < pulseNumber; i++) {
            dataSpacing[i] = readInt(content, offset, LENGTH_LONG) / 10000F;
            offset += LENGTH_LONG;
        }
        block.put("dataSpacing", dataSpacing);

        int[] dataPoints = new int[pulseNumber];
        for (int i = 0; i < pulseNumber; i++) {
            dataPoints[i] = readInt(content, offset, LENGTH_LONG);
            offset += LENGTH_LONG;
        }
        block.put("dataPoints", dataPoints);

        block.put("groupIndex", readInt(content, offset, LENGTH_LONG) / 100000F);

        offset += LENGTH_LONG;
        block.put("backscatterCoefficient", readInt(content, offset, LENGTH_SHORT) / 10F);

        offset += LENGTH_SHORT;
        block.put("averages", readInt(content, offset, LENGTH_LONG));

        offset += LENGTH_LONG;
        block.put("averagingTime", readInt(content, offset, LENGTH_SHORT));

        offset += LENGTH_SHORT;
        block.put("acquisitionRange", readInt(content, offset, LENGTH_LONG));

        offset += LENGTH_LONG;
        block.put("acquisitionRangeDistance", readInt(content, offset, LENGTH_LONG));

        offset += LENGTH_LONG;
        block.put("frontPanelOffset", readInt(content, offset, LENGTH_LONG));

        offset += LENGTH_LONG;
        block.put("noiseFloorLevel", readInt(content, offset, LENGTH_SHORT) / 1000F);

        offset += LENGTH_SHORT;
        block.put("noiseFloorScaleFactor", readInt(content, offset, LENGTH_SHORT) / 1000F);

        offset += LENGTH_SHORT;
        block.put("powerOffsetFirstPoint", readInt(content, offset, LENGTH_SHORT) / 1000F);

        offset += LENGTH_SHORT;
        block.put("lossThreshold", readInt(content, offset, LENGTH_SHORT) / 1000F);

        offset += LENGTH_SHORT;
        block.put("reflectanceThreshold", readInt(content, offset, LENGTH_SHORT) / -1000F);

        offset += LENGTH_SHORT;
        block.put("endThreshold", readInt(content, offset, LENGTH_SHORT) / 1000F);

        offset += LENGTH_SHORT;
        String traceType = readString(content, offset, LENGTH_SHORT);
        block.put("traceType", traceType);

        offset += LENGTH_SHORT;
        int[] coordinates = new int[4];
        for (int i = 0; i < coordinates.length; i++) {
            coordinates[i] = readInt(content, offset, 4);
            offset += 4;
        }
        block.put("windowCoordinates", coordinates);
    }

    /**
     * 读取关键事件块
     *
     * @param block   区块对象
     * @param content 区块字节内容
     */
    private static void readKeyEvents(Map<String, Object> block, byte[] content) {
        int offset = 0;

        String blockId = readStringSpaceZero(content, offset);
        block.put("blockId", blockId);

        offset += blockId.getBytes().length + 1;
        int eventsNumber = readInt(content, offset, LENGTH_SHORT);
        block.put("eventsNumber", eventsNumber);

        offset += LENGTH_SHORT;
        List<Map<String, Object>> events = new ArrayList<>();
        for (int i = 0; i < eventsNumber; i++) {
            Map<String, Object> event = new HashMap<>();
            event.put("eventNumber", readInt(content, offset, LENGTH_SHORT));

            offset += LENGTH_SHORT;
            event.put("eventPropagationTime", readInt(content, offset, LENGTH_LONG));

            offset += LENGTH_LONG;
            event.put("attenuation", readInt(content, offset, LENGTH_SHORT) / 1000F);

            offset += LENGTH_SHORT;
            event.put("eventLoss", readInt(content, offset, LENGTH_SHORT) / 1000F);

            offset += LENGTH_SHORT;
            event.put("eventReflectance", readInt(content, offset, LENGTH_LONG) / 1000F);

            offset += LENGTH_LONG;
            event.put("eventCode", readString(content, offset, 6));

            offset += 6;
            event.put("lossMeasurementTechnique", readString(content, offset, LENGTH_SHORT));

            offset += LENGTH_SHORT;
            int[] markerLocations = new int[5];
            for (int j = 0; j < markerLocations.length; j++) {
                markerLocations[j] = readInt(content, offset, LENGTH_LONG);
                offset += LENGTH_LONG;
            }
            event.put("markerLocations", markerLocations);

            String comment = readStringSpaceZero(content, offset);
            event.put("comment", comment);

            offset += comment.getBytes().length + 1;
            events.add(event);
        }
        block.put("events", events);

        block.put("End-to-EndLoss", readInt(content, offset, LENGTH_LONG) / 1000F);

        int[] markerPositions = new int[2];
        offset += LENGTH_LONG;
        markerPositions[0] = readInt(content, offset, LENGTH_LONG);
        offset += LENGTH_LONG;
        markerPositions[1] = readInt(content, offset, LENGTH_LONG);
        block.put("End-to-EndMarkerPositions", markerPositions);

        offset += LENGTH_LONG;
        block.put("opticalReturnLoss", readInt(content, offset, LENGTH_SHORT) / 1000F);

        int[] returnLossPosition = new int[2];
        offset += LENGTH_SHORT;
        returnLossPosition[0] = readInt(content, offset, LENGTH_LONG);
        offset += LENGTH_LONG;
        returnLossPosition[1] = readInt(content, offset, LENGTH_LONG);
        block.put("opticalReturnMarkerPosition", returnLossPosition);
    }

    /**
     * 读取链接参数块
     *
     * @param block   区块对象
     * @param content 区块字节内容
     */
    private static void readLnkParams(Map<String, Object> block, byte[] content) {
        int offset = 0;

        String blockId = readStringSpaceZero(content, offset);
        block.put("blockId", blockId);

        offset += blockId.getBytes().length + 1;
        int number = readInt(content, offset, LENGTH_SHORT);
        block.put("landmarksNumber", number);

        offset += LENGTH_SHORT;
        List<Map<String, Object>> landmarks = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            Map<String, Object> landmark = new HashMap<>();
            landmark.put("landmarkNumber", readInt(content, offset, LENGTH_SHORT));

            offset += LENGTH_SHORT;
            landmark.put("landmarkCode", readString(content, offset, LENGTH_SHORT));

            offset += LENGTH_SHORT;
            landmark.put("landmarkLocation", readInt(content, offset, LENGTH_LONG));

            offset += LENGTH_LONG;
            landmark.put("relatedEventNumber", readInt(content, offset, LENGTH_SHORT));

            offset += LENGTH_SHORT;
            int[] gps = new int[2];
            for (int j = 0; j < gps.length; j++) {
                gps[j] = readInt(content, offset, LENGTH_LONG);
                offset += LENGTH_LONG;
            }
            landmark.put("GPSInformation", gps);

            landmark.put("correctionFactor", readInt(content, offset, LENGTH_SHORT) / 100F + "%");

            offset += LENGTH_SHORT;
            landmark.put("enteringLandmark", readInt(content, offset, LENGTH_LONG));

            offset += LENGTH_LONG;
            int leavingLandmark = readInt(content, offset, LENGTH_LONG);

            offset += LENGTH_LONG;
            String units = readString(content, offset, LENGTH_SHORT);
            landmark.put("leavingLandmark", leavingLandmark + units);

            offset += LENGTH_SHORT;
            landmark.put("diameterLeavingLandmark", readString(content, offset, LENGTH_SHORT));

            offset += LENGTH_SHORT;
            landmark.put("comment", readStringSpaceZero(content, offset));
            landmarks.add(landmark);
        }
        block.put("landmarks", landmarks);
    }

    /**
     * 读取数据点块
     *
     * @param block   区块对象
     * @param content 区块字节内容
     */
    private static void readDataPts(Map<String, Object> block, byte[] content) {
        int offset = 0;

        String blockId = readStringSpaceZero(content, offset);
        block.put("blockId", blockId);

        offset += blockId.getBytes().length + 1;
        block.put("pointsNumber", readInt(content, offset, LENGTH_LONG));

        offset += LENGTH_LONG;
        block.put("scaleFactors", readInt(content, offset, LENGTH_SHORT));

        offset += LENGTH_SHORT;
        int number = readInt(content, offset, LENGTH_LONG);
        block.put("totalScaleFactors", number);

        offset += LENGTH_LONG;
        block.put("scaleFactor1", readInt(content, offset, LENGTH_SHORT));

        offset += LENGTH_SHORT;
        int[] data = new int[number];
        for (int i = 0; i < number; i++) {
            data[i] = readInt(content, offset, 2);
            offset += LENGTH_SHORT;
        }
        block.put("scaleFactorN", data);

    }

    /**
     * 读取校验块
     *
     * @param block   区块对象
     * @param content 区块字节内容
     */
    private static void readChecksum(Map<String, Object> block, byte[] content) {
        int offset = 0;

        String blockId = readStringSpaceZero(content, offset);
        block.put("blockId", blockId);

        offset += blockId.getBytes().length + 1;
        block.put("checksum", readInt(content, offset, LENGTH_SHORT));
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