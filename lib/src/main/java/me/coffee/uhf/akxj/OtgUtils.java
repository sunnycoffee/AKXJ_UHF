package me.coffee.uhf.akxj;

import android.text.TextUtils;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class OtgUtils {

    private static final String TAG = OtgUtils.class.getSimpleName();
    private static List<String> listEnable = new ArrayList<>();
    private static List<String> listDisable = new ArrayList<>();

    private static int NUM_ENABLE_SIZE = 5;
    private static int NUM_DISABLE_SIZE = 4;

    static {


    }


    private static final String NODE_POGO_5V = "/sys/devices/platform/soc/soc:yft_device/yft_5v_switch";


    public static boolean setPOGOPINEnable(final boolean enable) {

        try {

            if (isChange(NODE_POGO_5V, enable)) {
                powerControl0X31(NODE_POGO_5V, enable);
            }
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            Log.v(TAG, "Exception:" + e.getMessage());
            return false;
        }
    }


    private static void powerControl0X31(String node5v, boolean enable) throws Throwable {
        FileOutputStream node_1 = null;
        try {
            byte[] open_one = new byte[]{0x31};
            byte[] close = new byte[]{0x30};
            node_1 = new FileOutputStream(node5v);
            node_1.write(enable ? open_one : close);
            Log.v("OtgUtils", "write  success :" + node5v);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            try {
                if (node_1 != null) {
                    node_1.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private static boolean isChange(String nodepath_pogo5ven, boolean enable) {

        try {
            if (!TextUtils.isEmpty(nodepath_pogo5ven)) {
                return true;
            }

            Log.v("OtgUtils", "isChange()");

            FileInputStream fileInputStream = new FileInputStream(nodepath_pogo5ven);
            byte[] b = new byte[1024];
            String nodeStr = "";
            //开始读文件
            int len = fileInputStream.read(b);
            if (len > 0) {
                nodeStr = new String(b, 0, len);
            }

            if (enable) {
                for (int i = 0; i < NUM_ENABLE_SIZE; i++) {
                    if (nodeStr.contains(listEnable.get(i))) {
                        Log.v("OtgUtils", "isChange()  already  enable " + "    " + nodepath_pogo5ven + "     [" + nodeStr + "]");
                        return false;//已经包含，说明已经上电，不用再上电
                    }
                }
                Log.v("OtgUtils", "isChange()  not   enable " + "    " + nodepath_pogo5ven + "     [" + nodeStr + "]");
                return true;//没有包含，说明没有上电，需要上电
            } else {
                for (int i = 0; i < NUM_DISABLE_SIZE; i++) {
                    if (nodeStr.contains(listDisable.get(i))) {
                        Log.v("OtgUtils", "isChange()  already  disable " + "    " + nodepath_pogo5ven + "     [" + nodeStr + "]");
                        return false;//已经包含，说明已经下电，不用再下电
                    }
                }
                Log.v("OtgUtils", "isChange()  not  disable " + "    " + nodepath_pogo5ven + "     [" + nodeStr + "]");
                return true;//没有包含，说明没有上电，需要上电
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("OtgUtils", "isChange() " + nodepath_pogo5ven + "   Exception:" + e.getMessage());
        }
        return true;
    }


}

