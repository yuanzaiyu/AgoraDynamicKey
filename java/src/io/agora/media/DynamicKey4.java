package io.agora.media;

import java.io.ByteArrayOutputStream;

public class DynamicKey4 {

    private static final String RECORDING_SERVICE = "ARS";
    private static final String MEDIA_CHANNEL_SERVICE = "ACS";

    /**
     * Generate Dynamic Key for recording service
     * @param vendorKey Vendor key assigned by Agora
     * @param signKey Sign key assigned by Agora
     * @param channelName name of channel to join, limited to 64 bytes and should be printable ASCII characters
     * @param unixTs unix timestamp in seconds when generating the Dynamic Key
     * @param randomInt salt for generating dynamic key
     * @param uid user id, range from 0 - max uint32
     * @param expiredTs should be 0
     * @return String representation of dynamic key
     * @throws Exception if any error occurs
     */
    public static String generateRecordingKey(String vendorKey, String signKey, String channelName, int unixTs, int randomInt, long uid, int expiredTs) throws Exception {
        return doGenerate(vendorKey, signKey, channelName, unixTs, randomInt, uid, expiredTs, RECORDING_SERVICE);
    }

    /**
     * Generate Dynamic Key for media channel service
     * @param vendorKey Vendor key assigned by Agora
     * @param signKey Sign key assigned by Agora
     * @param channelName name of channel to join, limited to 64 bytes and should be printable ASCII characters
     * @param unixTs unix timestamp in seconds when generating the Dynamic Key
     * @param randomInt salt for generating dynamic key
     * @param uid user id, range from 0 - max uint32
     * @param expiredTs service expiring timestamp. After this timestamp, user will not be able to stay in the channel.
     * @return String representation of dynamic key
     * @throws Exception if any error occurs
     */
    public static String generateMediaChannelKey(String vendorKey, String signKey, String channelName, int unixTs, int randomInt, long uid, int expiredTs) throws Exception {
        return doGenerate(vendorKey, signKey, channelName, unixTs, randomInt, uid, expiredTs, MEDIA_CHANNEL_SERVICE);
    }

    private static String doGenerate(String vendorKey, String signKey, String channelName, int unixTs, int randomInt, long uid, int expiredTs, String serviceType) throws Exception {
        String version = "004";
        String unixTsStr = ("0000000000" + Integer.toString(unixTs)).substring(Integer.toString(unixTs).length());
        String randomIntStr = ("00000000" + Integer.toHexString(randomInt)).substring(Integer.toHexString(randomInt).length());
        uid = uid & 0xFFFFFFFFL;
        String uidStr = ("0000000000" + Long.toString(uid)).substring(Long.toString(uid).length());
        String expiredTsStr = ("0000000000" + Integer.toString(expiredTs)).substring(Integer.toString(expiredTs).length());
        String signature = generateSignature4(vendorKey, signKey, channelName, unixTsStr, randomIntStr, uidStr, expiredTsStr, serviceType);
        return String.format("%s%s%s%s%s%s", version, signature, vendorKey, unixTsStr, randomIntStr, expiredTsStr);
    }

    private static String generateSignature4(String vendorKey, String signKey, String channelName, String unixTsStr, String randomIntStr, String uidStr, String expiredTsStr, String serviceType) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(serviceType.getBytes());
        baos.write(vendorKey.getBytes());
        baos.write(unixTsStr.getBytes());
        baos.write(randomIntStr.getBytes());
        baos.write(channelName.getBytes());
        baos.write(uidStr.getBytes());
        baos.write(expiredTsStr.getBytes());
        byte[] sign = DynamicKeyUtil.encodeHMAC(signKey, baos.toByteArray());
        return DynamicKeyUtil.bytesToHex(sign);
    }
}
