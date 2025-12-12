package me.coffee.uhf.akxj;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import com.rfid.trans.ReadTag;
import com.rfid.trans.ReaderHelp;
import com.rfid.trans.ReaderParameter;
import com.rfid.trans.TagCallback;

/**
 * @author kongfei
 */
public class UHFManager {

    @SuppressLint("StaticFieldLeak")
    public static final UHFManager INSTANCE = new UHFManager();

    private static final String TAG = "UHFManager";
    private static final String PORT = "/dev/ttyS0";
    private Context context;
    private ReaderHelp mReader;
    private ScanCallback mCallback;

    private UHFManager() {
    }

    public void init(Context context) {
        Log.d(TAG, "PDA初始化开始");
        this.context = context.getApplicationContext();
        if (mReader == null) {
            mReader = new ReaderHelp();
            mReader.PowerControll(this.context, true);

            int flag = mReader.Connect(PORT, 57600, 0);
            Log.d(TAG, "串口连接:" + flag);
            if (flag != 0) flag = mReader.Connect(PORT, 115200, 0);
            if (flag == 0) {
                initRfid();
                mReader.SetCallBack(new TagCallback() {
                    @Override
                    public void tagCallback(ReadTag readTag) {
                        if (readTag != null) {
                            final String epc = readTag.epcId;
                            if (mCallback != null) mCallback.onRead(epc);
                        }
                    }

                    @Override
                    public void StopReadCallBack() {
                        mCallback = null;
                    }
                });
            } else {
                Log.d(TAG, "串口连接失败");
            }
        }
    }

    private void initRfid() {
        ReaderParameter param = mReader.GetInventoryPatameter();
        int ReaderType = mReader.GetReaderType();
        if (ReaderType == 0x21 || ReaderType == 0x28 || ReaderType == 0x23 || ReaderType == 0x37 || ReaderType == 0x36) {
            param.Session = 1;//R2000
        } else if (ReaderType == 0x70 || ReaderType == 0x71 || ReaderType == 0x31) {
            param.Session = 254; //Ex10
        } else if (ReaderType == 0x61 || ReaderType == 0x63 || ReaderType == 0x65 || ReaderType == 0x66) {
            param.Session = 1;//C6
        } else {
            param.Session = 0;
        }
        param.IvtType = 0;//epc:0; epc&tid:1; tid:2
        mReader.SetInventoryPatameter(param);
    }

    public void setReadType(int readType) {
        ReaderParameter param = mReader.GetInventoryPatameter();
        param.IvtType = readType;//epc:0; epc&tid:1; tid:2
        mReader.SetInventoryPatameter(param);
    }

    public void setSoundID(int resId) {
        SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
        mReader.SetSoundID(soundPool.load(this.context, resId, 1), soundPool);
    }

    public void start(ScanCallback callback) {
        this.mCallback = callback;
        if (mReader != null) mReader.StartRead();
    }

    public void stop() {
        mCallback = null;
        if (mReader != null) mReader.StopRead();
    }

    public void release() {
        mCallback = null;
        if (mReader != null) {
            mReader.DisConnect();
            if (context != null) mReader.PowerControll(context, false);
        }
        mReader = null;
    }

    public interface ScanCallback {
        void onRead(String code);
    }
}
