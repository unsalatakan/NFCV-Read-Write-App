package com.example.server1.nfcv;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.server1.nfcv.util.NfcVUtil;

import com.example.server1.nfcv.service.NfcService;

import java.io.IOException;
import java.util.Arrays;

public class ReadActivity extends AppCompatActivity {

    private TextView tag_id;
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private TextView mText;
    private String uid;
    private EditText kasa_no, stok_no, irs_no, adet_no, yansanayi_no, tarih_day, tarih_month, tarih_year, oprt_no;
    private Vibrator vibe;
    private Button btn_write, btn_read;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        tag_id = findViewById(R.id.tag_id);

        kasa_no = (EditText) findViewById(R.id.textInputLayout);
        stok_no = (EditText) findViewById(R.id.textInputLayout2);
        irs_no = (EditText) findViewById(R.id.textInputLayout3);
        adet_no = (EditText) findViewById(R.id.textInputLayout4);
        yansanayi_no = (EditText) findViewById(R.id.textInputLayout5);
        tarih_day = (EditText) findViewById(R.id.textInputLayout6day);
        tarih_month = (EditText) findViewById(R.id.textInputLayout6month);
        tarih_year = (EditText) findViewById(R.id.textInputLayout6year);
        oprt_no = (EditText) findViewById(R.id.textInputLayout7);

        mText = (TextView) findViewById(R.id.TextView);

        mText.setText("Okutmak için Kartı Cihaza Yaklaştırın");

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter nfcv = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        mFilters = new IntentFilter[]{nfcv};
        mTechLists = new String[][]{new String[]{NfcV.class.getName()}};

        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        btn_write = findViewById(R.id.button_write);
        btn_read = findViewById(R.id.button_read);


        btn_write.setOnClickListener(view -> {
            vibe.vibrate(100);
            mText.setText("Yazmak için Kartı Cihaza Yaklaştırın");
            ((ButtonInput) this.getApplication()).setButtonInput("1");

        });

        btn_read.setOnClickListener(view -> {
            vibe.vibrate(100);
            kasa_no.setText("");
            stok_no.setText("");
            irs_no.setText("");
            adet_no.setText("");
            yansanayi_no.setText("");
            tarih_day.setText("");
            tarih_month.setText("");
            tarih_year.setText("");
            oprt_no.setText("");
            mText.setText("Okutmak için Kartı Cihaza Yaklaştırın");
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.getAction().trim().equals("android.nfc.action.TECH_DISCOVERED")) {
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            NfcV nfcv = NfcV.get(tagFromIntent);
            try {
                if (nfcv.isConnected())
                    nfcv.close();
                nfcv.connect();
                NfcVUtil nNfcVUtil = new NfcVUtil(nfcv);
                StringBuilder uid3 = new StringBuilder();
                for (int i = 0; i < nNfcVUtil.getBlockNumber(); i++) {
                    uid3.append(nNfcVUtil.readOneBlock(i));
                }
                uid = uid3.substring(16, 112).trim();
                String deneme = kasa_no.getText().toString();
                Log.e(deneme, "kasano");
                if (deneme.length() < 2) {
                    uidTrim();
                    mText.setText("");
                }else if(((ButtonInput) this.getApplication()).getButtonInput() == "1"){
                    getInput();
                    String _writedata = getInput();
                    byte[] data = fromHexString(_writedata);
                    data = Arrays.copyOfRange(data, 0, 4 * 24);
                    try {
                        nNfcVUtil.writeBlock(2, data);
                        ((ButtonInput) this.getApplication()).setButtonInput("0");
                        mText.setText("Başarıyla Yazdırıldı.");
                    } catch (IOException e) {
                        e.printStackTrace();
                        ((ButtonInput) this.getApplication()).setButtonInput("0");
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //NfcAdapter aNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        tag_id.setText("Kart ID: " + getTid(tag.getId()));
        Intent intentService = new Intent(this, NfcService.class);
        intentService.putExtra("tag", tag);
        startService(intentService);
        //readNfcId(nfcV);
        Log.i("NormalActivity", "this is onNewIntent");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("NormalActivity", "this is onResume");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("cn.ident.nas.tester.CHANGEUI");
        mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
    }

    private static String getTid(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private void uidTrim() {
        StringBuilder transformedText = new StringBuilder();
        String degisken;
        StringBuilder Gelen1 = new StringBuilder();
        for (int i = 0; i < (uid).length(); i = i + 8) {
            degisken = uid.substring(i, i + 8);
            for (int cpt2 = 4; cpt2 > 0; cpt2--) {
                Gelen1.append(degisken.substring(cpt2 * 2 - 2, cpt2 * 2));

            }
            transformedText.append(Gelen1);
            Gelen1 = new StringBuilder();
        }

        if (transformedText.length() > 50) {
            String ContnNo = transformedText.substring(1, 10);
            kasa_no.setText(ContnNo);

            String Parts = transformedText.substring(16, 36);
            String stokno = toASCII(Parts);
            stok_no.setText(stokno);

            String Entrance = transformedText.substring(56, 62);
            irs_no.setText(Entrance);

            String Qty = transformedText.substring(48, 54).replaceFirst("^0+(?!$)", "");
            adet_no.setText(Qty);

            String Supp = transformedText.substring(80, 86).replaceFirst("^0+(?!$)", "");
            yansanayi_no.setText(Supp);
            String ConType = transformedText.substring(14, 16);

            String SDateDay, SDateMonth, SDateYear;
            String Amount;
            String Sqnc;

            SDateDay = transformedText.substring(64, 66);
            SDateMonth = transformedText.substring(66, 68);
            SDateYear = transformedText.substring(68, 70);

            String tarihday = SDateDay;
            String tarihmonth = SDateMonth;
            String tarihyear = SDateYear;

            tarih_day.setText(tarihday);
            tarih_month.setText(tarihmonth);
            tarih_year.setText(tarihyear);

            String ContnFull = transformedText.substring(40, 42);

            if ("01".equals(ContnFull)) {
                Amount = transformedText.substring(72, 75);
                Sqnc = transformedText.substring(75, 78);
            } else {
                Amount = "999";
                Sqnc = "001";
            }
            String Oprt = transformedText.substring(88, 94);
            oprt_no.setText(Oprt);
        }

    }

    private String toASCII(String x) {

        if (x.length() % 2 != 0) {
            System.err.println("Invlid hex string.");
            return "Stok kodu hatalı";
        }
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < x.length(); i = i + 2) {
            String s = x.substring(i, i + 2);
            int n = Integer.valueOf(s, 16);
            builder.append((char) n);
        }
        return builder.toString();
    }

    @SuppressLint("DefaultLocale")
    private String getInput() {

        String kasanoR = String.format("%09d", Integer.parseInt(kasa_no.getText().toString()));
        String stoknoR = asciiToHex("0000000000".substring(stok_no.getText().toString().length()) + stok_no.getText().toString());
        String irsnoR = String.format("%06d", Integer.parseInt(irs_no.getText().toString()));
        String adetnoR = String.format("%06d", Integer.parseInt(adet_no.getText().toString()));
        String yansanayinoR = String.format("%06d", Integer.parseInt(yansanayi_no.getText().toString()));
        String tarihdayR = String.format("%02d", Integer.parseInt(tarih_day.getText().toString()));
        String tarihmonthR = String.format("%02d", Integer.parseInt(tarih_month.getText().toString()));
        String tarihyearR = String.format("%02d", Integer.parseInt(tarih_year.getText().toString()));
        String oprtnoR = String.format("%06d", Integer.parseInt(oprt_no.getText().toString()));
        String hamVeri = "0" + kasanoR + "000001" + stoknoR + "313001" + "000000" + adetnoR + "00" + irsnoR + "00" + tarihdayR + tarihmonthR + tarihyearR + "0000000000" + yansanayinoR + "00" + oprtnoR + "00";
        //Log.e(hamVeri, "Son Veri");
        String sonVeri = reverseParse(hamVeri);
        //Log.e(sonVeri, "Son Veri");

        return sonVeri;
    }

    private static String reverseParse(String hamveri) {

        StringBuilder transformedText = new StringBuilder();
        String degisken;
        StringBuilder Gelen1 = new StringBuilder();
        for (int i = 0; i < (hamveri).length(); i = i + 8) {
            degisken = hamveri.substring(i, i + 8);
            for (int cpt2 = 4; cpt2 > 0; cpt2--) {
                Gelen1.append(degisken.substring(cpt2 * 2 - 2, cpt2 * 2));

            }
            transformedText.append(Gelen1);
            Gelen1 = new StringBuilder();
        }
        return transformedText.toString();
    }

    private static String asciiToHex(String y) {
        char[] chars = y.toCharArray();
        StringBuilder hex = new StringBuilder();
        for (char aChar : chars) {
            hex.append(Integer.toHexString((int) aChar));
        }
        Log.e(hex.toString(), "HEX");
        return hex.toString();
    }

    private static byte[] fromHexString(final String encoded) {
        if ((encoded.length() % 2) != 0)
            throw new IllegalArgumentException("Input string must contain an even number of characters");

        final byte result[] = new byte[encoded.length() / 2];
        final char enc[] = encoded.toCharArray();
        for (int i = 0; i < enc.length; i += 2) {
            StringBuilder curr = new StringBuilder(2);
            curr.append(enc[i]).append(enc[i + 1]);
            result[i / 2] = (byte) Integer.parseInt(curr.toString(), 16);
        }
        return result;
    }

}