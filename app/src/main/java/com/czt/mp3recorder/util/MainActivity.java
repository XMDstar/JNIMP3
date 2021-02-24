package com.czt.mp3recorder.util;

import android.media.AudioFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Company:好未来集团
 * ClassName: MainActivity
 * Author: zcc
 * Date: 2/23/21 5:21 PM
 * Description: 测试类
 */
public class MainActivity extends AppCompatActivity {
    public static final int ALIYUN_AUDIO_RATE = 44100;
    public static final int ALIYUN_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_DEFAULT;
    public static final int XES_AI_AUDIO_TATE = 16000;
    public static final int XES_AI_AUDIO_BITRATE = 128;
    public static final int XES_AI_MP3_QUALITY = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((TextView) findViewById(R.id.tv)).setText("点击下方按钮开始编码  libmp3lame版本：" + LameUtil.getLameVersion());
        findViewById(R.id.bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            File dstFile = MainActivity.this.getExternalFilesDir("music");
                            File dest = new File(dstFile, "haha.mp3");
                            LameUtil.init(ALIYUN_AUDIO_RATE,
                                    ALIYUN_CHANNEL_CONFIG,
                                    XES_AI_AUDIO_TATE,
                                    XES_AI_AUDIO_BITRATE,
                                    XES_AI_MP3_QUALITY);//最多支持两声道
                            InputStream is = getAssets().open("pxbf.pcm");
                            OutputStream os = new FileOutputStream(dest);

                            byte[] b = new byte[4096];
                            //定义一个记录索引的变量
                            int len = 0;
                            //循环读取每个数据
                            while ((len = is.read(b)) != -1) {//把读取的数据放到i中
                                byte[] bytes = new byte[len];
                                System.arraycopy(b, 0, bytes, 0, len);

                                short[] shorts = bytesToShort(bytes);

                                int mp3Len = LameUtil.encode(shorts, shorts, shorts.length / 2, b);//我知道为什么要除二了，因为是采样点，声道数为2 采样点的个数就是 buffer的大小除声道数。哈哈哈哈哈哈
                                if (mp3Len > 0) {
                                    os.write(b, 0, mp3Len);
                                }

                                Log.e("mp3", "写入文件: " + mp3Len);
                            }
                            int flush = LameUtil.flush(b);
                            os.write(b, 0, flush);
                            LameUtil.close();
                            Log.e("mp3", "文件写入完成: ");
                            is.close();
                            os.close();
                        } catch (FileNotFoundException e) {
                            Log.e("mp3", "异常：" + e.getMessage());
                            e.printStackTrace();
                        } catch (IOException e) {
                            Log.e("mp3", "异常：" + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    public static short[] bytesToShort(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        short[] shorts = new short[bytes.length / 2];
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
        return shorts;
    }
}