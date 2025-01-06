package com.example.containertracker.utils.printer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.printer.io.BluetoothPort;
import com.printer.io.PortManager;

import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Time 2017/8/2
 */
public class DeviceConnFactoryManager {

    public static final byte FLAG = 0x10;
    public static final String ACTION_CONN_STATE = "action_connect_state";
    public static final String ACTION_QUERY_PRINTER_STATE = "action_query_printer_state";
    public static final String STATE = "state";
    public static final String DEVICE_ID = "id";
    public static final int CONN_STATE_DISCONNECT = 0x90;
    public static final int CONN_STATE_CONNECTING = CONN_STATE_DISCONNECT << 1;
    public static final int CONN_STATE_FAILED = CONN_STATE_DISCONNECT << 2;
    public static final int CONN_STATE_CONNECTED = CONN_STATE_DISCONNECT << 3;
    private static final String TAG = DeviceConnFactoryManager.class.getSimpleName();
    /**
     * ESC Menanyakan status real-time dari status Paper out printer
     */
    private static final int ESC_STATE_PAPER_ERR = 0x20;
    /**
     * ESC Perintah untuk menanyakan status real-time dari printer Status pembukaan penutup printer
     */
    private static final int ESC_STATE_COVER_OPEN = 0x04;
    /**
     * ESC Perintah untuk menanyakan status printer real-time Status kesalahan printer
     */
    private static final int ESC_STATE_ERR_OCCURS = 0x40;
    /**
     * ESC daya rendah
     */
    private static final int ESC_LOW_POWER = 0x31;
    /**
     * ESC Baterai sedang
     */
    private static final int ESC_AMID_POWER = 0x32;
    /**
     * ESC baterai tinggi
     */
    private static final int ESC_HIGH_POWER = 0x33;
    /**
     * ESC Pengisian
     */
    private static final int ESC_CHARGING = 0x35;
    private static final int READ_DATA = 10000;
    private static final String READ_DATA_CNT = "read_data_cnt";
    private static final String READ_BUFFER_ARRAY = "read_buffer_array";
    public static boolean whichFlag = true;
    private PortManager mPort;
    private PrinterReader reader;
    private final String macAddress;
    private final Context mContext;

    private boolean isOpenPort;
    /**
     * Perintah status real-time printer permintaan ESC
     */
    private final byte[] esc = {0x10, 0x04, 0x02};
    /**
     * Perintah status printer kueri TSC
     */
    private final byte[] tsc = {0x1b, '!', '?'};
    private final byte[] cpcl = {0x1b, 0x68};
    private byte[] sendCommand;
    /**
     * Tentukan apakah perintah yang digunakan oleh printer adalah perintah ESC
     */
    private PrinterCommand currentPrinterCommand;

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == READ_DATA) {
                int cnt = msg.getData().getInt(READ_DATA_CNT); //Panjang data >0;
                byte[] buffer = msg.getData().getByteArray(READ_BUFFER_ARRAY);  //data
                //Hanya nilai pengembalian status kueri yang diproses di sini, nilai pengembalian lainnya dapat diuraikan dengan mengacu pada manual pemrograman
                if (buffer == null) {
                    return;
                }
                int result = judgeResponseType(buffer[0]); //pergeseran data ke kanan
                String status = "Printer terhubung secara normal";
                if (sendCommand == esc) {
                    //Atur mode printer saat ini ke mode ESC
                    if (currentPrinterCommand == null) {
                        currentPrinterCommand = PrinterCommand.ESC;
                        sendStateBroadcast(CONN_STATE_CONNECTED);
                    } else {//Status printer kueri
                        if (result == 0) {//Pertanyaan Status Pencetak
                            Intent intent = new Intent(ACTION_QUERY_PRINTER_STATE);
                            intent.putExtra(DEVICE_ID, macAddress);
                            mContext.sendBroadcast(intent);
                        } else if (result == 1) {//Menanyakan status real-time printer
                            if (whichFlag) {
                                if ((buffer[0] & ESC_STATE_PAPER_ERR) > 0) {
                                    status += "Printer kehabisan kertas";
                                }
                                if ((buffer[0] & ESC_STATE_COVER_OPEN) > 0) {
                                    status += "Buka penutup printer";
                                }
                                if ((buffer[0] & ESC_STATE_ERR_OCCURS) > 0) {
                                    status += "kesalahan pencetak";
                                }
                            } else {
                                if ((buffer[0] == ESC_LOW_POWER)) {
                                    status = "Baterai: baterai lemah";
                                } else if ((buffer[0] == ESC_AMID_POWER)) {
                                    status = "Kekuatan: kekuatan sedang";
                                } else if ((buffer[0] == ESC_HIGH_POWER)) {
                                    status = "Baterai: baterai tinggi";
                                } else if ((buffer[0] == ESC_CHARGING)) {
                                    status = "Pengisian";
                                }
                            }
                            Log.i("TAG", "state：" + status);
                            String mode = "mode cetak:ESC";
                            Toast.makeText(mContext, mode + " " + status, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else if (sendCommand == tsc) {
                    //Atur mode printer saat ini ke mode TSC
                    if (currentPrinterCommand == null) {
                        currentPrinterCommand = PrinterCommand.TSC;
                        sendStateBroadcast(CONN_STATE_CONNECTED);
                    } else {
                        if (cnt == 1) {//Menanyakan status real-time printer
                            if ((buffer[0] & ESC_STATE_PAPER_ERR) > 0) {
                                status += "Printer kehabisan kertas";
                            }
                            if ((buffer[0] & ESC_STATE_COVER_OPEN) > 0) {
                                status += "Buka penutup printer";
                            }
                            if ((buffer[0] & ESC_STATE_ERR_OCCURS) > 0) {
                                status += "kesalahan pencetak";
                            }
                            Log.i("TAG", "state：" + status);
                            String mode = "mode cetak:TSC";
                            Toast.makeText(mContext, mode + " " + status, Toast.LENGTH_SHORT).show();
                        } else {//Pertanyaan Status Pencetak
                            Intent intent = new Intent(ACTION_QUERY_PRINTER_STATE);
                            intent.putExtra(DEVICE_ID, macAddress);
                            mContext.sendBroadcast(intent);
                        }
                    }
                } else if (sendCommand == cpcl) {
                    if (currentPrinterCommand == null) {
                        currentPrinterCommand = PrinterCommand.CPCL;
                        sendStateBroadcast(CONN_STATE_CONNECTED);
                    } else {
                        if (cnt == 1) {
                            if ((buffer[0] & ESC_STATE_PAPER_ERR) > 0) {
                                status += "Printer kehabisan kertas";
                            }
                            if ((buffer[0] & ESC_STATE_COVER_OPEN) > 0) {
                                status += "Buka penutup printer";
                            }
                            Log.i("TAG", "state：" + status);
                            String mode = "mode cetak:CPCL";
                            Toast.makeText(mContext, mode + " " + status, Toast.LENGTH_SHORT).show();
                        } else {//Pertanyaan Status Pencetak
                            Intent intent = new Intent(ACTION_QUERY_PRINTER_STATE);
                            intent.putExtra(DEVICE_ID, macAddress);
                            mContext.sendBroadcast(intent);
                        }
                    }
                }
            }
        }
    };

    public DeviceConnFactoryManager(Build build) {
        this.macAddress = build.macAddress;
        this.mContext = build.context;
    }

    public void closeAllPort() {
        if (this.mPort != null) {
            reader.cancel();
            boolean b = this.mPort.closePort();
            if (b) {
                this.mPort = null;
                isOpenPort = false;
                currentPrinterCommand = null;
            }
        }
        sendStateBroadcast(CONN_STATE_DISCONNECT);
        mPort = null;
    }

    /**
     * port terbuka (mulai koneksi)
     */
    public void openPort() {
        isOpenPort = false;
        sendStateBroadcast(CONN_STATE_CONNECTING);

        mPort = new BluetoothPort(macAddress);
        isOpenPort = mPort.openPort();

        //Setelah port berhasil dibuka, periksa perintah printer ESC dan TSC yang digunakan untuk menghubungkan printer
        if (isOpenPort) {
            queryCommand();
        } else {
            if (this.mPort != null) {
                this.mPort = null;
            }
            sendStateBroadcast(CONN_STATE_FAILED);
        }
    }

    private void sendStateBroadcast(int state) {
        Intent intent = new Intent(ACTION_CONN_STATE);
        intent.putExtra(STATE, state);
        intent.putExtra(DEVICE_ID, macAddress);
        mContext.sendBroadcast(intent);
    }

    /**
     * Query perintah printer yang digunakan oleh printer yang terhubung saat ini (ESC (EscCommand.java), TSC (LabelCommand.java))
     */
    private void queryCommand() {
        //Buka utas data pengembalian printer baca
        reader = new PrinterReader();
        reader.start(); //baca utas data
        //Query perintah yang digunakan oleh printer
        queryPrinterCommand(); //
    }

    /**
     * Minta perintah yang saat ini digunakan oleh printer (TSC, ESC)
     */
    private void queryPrinterCommand() {
        //Kumpulan utas menambahkan tugas
        ThreadPool.getInstantiation().addTask(() -> {
            //Kirim ESC untuk menanyakan perintah status printer
            sendCommand = esc;
            Vector<Byte> data = new Vector<>(esc.length);
            for (byte b : esc) {
                data.add(b);
            }
            sendDataImmediately(data); //kirim data esc
            //Mulai penghitung waktu, dan kirim perintah status printer kueri TSC ketika tidak ada nilai balik setiap 2000 milidetik
            final ThreadFactoryBuilder threadFactoryBuilder = new ThreadFactoryBuilder("Timer");
            final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1, threadFactoryBuilder);
            scheduledExecutorService.schedule(threadFactoryBuilder.newThread(() -> {
                if (currentPrinterCommand == null || currentPrinterCommand != PrinterCommand.ESC) {
                    Log.e(TAG, Thread.currentThread().getName());
                    //Kirim perintah status printer kueri TSC
                    sendCommand = tsc;
                    Vector<Byte> data12 = new Vector<>(tsc.length);
                    for (byte b : tsc) {
                        data12.add(b);
                    }
                    sendDataImmediately(data12);
                    //Mulai penghitung waktu, dan kirim perintah status printer kueri CPCL ketika tidak ada nilai balik setiap 2000 milidetik
                    scheduledExecutorService.schedule(threadFactoryBuilder.newThread(() -> {
                        if (currentPrinterCommand == null || (currentPrinterCommand != PrinterCommand.ESC && currentPrinterCommand != PrinterCommand.TSC)) {
                            Log.e(TAG, Thread.currentThread().getName());
                            //Kirim perintah status printer kueri CPCL
                            sendCommand = cpcl;
                            Vector<Byte> data1 = new Vector<>(cpcl.length);
                            for (byte b : cpcl) {
                                data1.add(b);
                            }
                            sendDataImmediately(data1);
                            //Mulai timer, setiap 2000 milidetik printer tidak memiliki respons untuk berhenti membaca utas data printer dan menutup port
                            scheduledExecutorService.schedule(threadFactoryBuilder.newThread(() -> {
                                if (currentPrinterCommand == null) {
                                    if (reader != null) {
                                        reader.cancel();
                                        mPort.closePort();
                                        isOpenPort = false;
                                        mPort = null;
                                        sendStateBroadcast(CONN_STATE_FAILED);
                                    }
                                }
                            }), 2000, TimeUnit.MILLISECONDS);
                        }
                    }), 2000, TimeUnit.MILLISECONDS);
                }
            }), 2000, TimeUnit.MILLISECONDS);
        });
    }

    public void sendDataImmediately(final Vector<Byte> data) {
        if (this.mPort == null) {
            return;
        }
        try {
            this.mPort.writeDataImmediately(data, 0, data.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int readDataImmediately(byte[] buffer) throws IOException {
        return this.mPort.readData(buffer);
    }

    /**
     * Tentukan apakah itu status real-time (10 04 02) atau status kueri (1D 72 01)
     */
    private int judgeResponseType(byte r) {
        return (byte) ((r & FLAG) >> 4);
    }

    /**
     * Gunakan pola builder untuk meneruskan data, hapus decoupling
     */
    public static final class Build {
        private String macAddress;
        private Context context;

        public DeviceConnFactoryManager.Build setMacAddress(String macAddress) {
            this.macAddress = macAddress;
            return this;
        }

        public DeviceConnFactoryManager.Build setContext(Context context) {
            this.context = context;
            return this;
        }

        public DeviceConnFactoryManager build() {
            return new DeviceConnFactoryManager(this);
        }
    }

    /**
     * Buka utas untuk membaca data
     */
    public class PrinterReader extends Thread {
        private boolean isRun;

        private final byte[] buffer = new byte[100];

        public PrinterReader() {
            isRun = true;
        }

        @Override
        public void run() {
            try {
                while (isRun) {
                    //Baca informasi pengembalian printer
                    int len = readDataImmediately(buffer);
                    if (len > 0) {
                        Message message = Message.obtain();
                        message.what = READ_DATA;
                        Bundle bundle = new Bundle();
                        bundle.putInt(READ_DATA_CNT, len); //数据长度
                        bundle.putByteArray(READ_BUFFER_ARRAY, buffer); //数据
                        message.setData(bundle);
                        mHandler.sendMessage(message);
                    }
                }
            } catch (Exception e) {
                closeAllPort();
            }
        }

        public void cancel() {
            isRun = false;
        }
    }
}