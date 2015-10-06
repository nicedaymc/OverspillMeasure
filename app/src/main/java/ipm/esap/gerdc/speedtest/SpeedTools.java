package ipm.esap.gerdc.speedtest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.StringTokenizer;

import android.util.Log;

public class SpeedTools {
  public static final String BACKEND_SERVER = "http://rg.ipm.edu.mo/speedtest/RecordSpeed";
  public static final String DOWNLOAD_LINK = "http://gerdc.ipm.edu.mo/images/";
  public static final String HOST_NAME = "www.google.com";
  public static final String LOAD_FILE = "banner.jpg";
  public static final String UPLOAD_LINK = "http://gerdc.ipm.edu.mo/speedtest/upload.php";
  public static final int BUFFER_SIZE = 8192;
  private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.ENGLISH);
  private static DecimalFormat df = new DecimalFormat("#.####");

  public static double countSpeed(long t1, long t2, long fileSize) {
    if (t1 == t2) {
      return 0;
    } else {
      double mbps = (fileSize / Math.pow(1024, 2) * 8000) / (t2 - t1);
      return mbps;
    }
  }

  public static String formatNum(double rate) {
    return df.format(rate);
  }

  public static long getIntervalTime(int menuItem) {
    long intervalTime = 0;
    switch (menuItem) {
    case 1: {
      intervalTime = 5 * 60000;
      break;
    }
    case 2: {
      intervalTime = 15 * 60000;
      break;
    }
    case 3: {
      intervalTime = 30 * 60000;
      break;
    }
    case 4: {
      intervalTime = 60 * 60000;
      break;
    }
    default: {
      intervalTime = 2 * 60000;
      break;
    }
    }
    return intervalTime;
  }

  public static long getMillisecond() {
    Calendar calendar = Calendar.getInstance();
    return calendar.getTimeInMillis();
  }

  public static String getSysTime() {
    Calendar calendar = Calendar.getInstance();
    return sdf.format(calendar.getTime());
  }

  public static void pingServer(int times, SpeedBean speedBean) {
    return;
    /*
    try {
      Process process = Runtime.getRuntime().exec("/system/bin/ping -c " + times + " " + HOST_NAME);
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()), 8192);
      int i;
      char[] buffer = new char[BUFFER_SIZE];
      StringBuffer output = new StringBuffer();
      while ((i = reader.read(buffer)) > 0) {
        output.append(buffer, 0, i);
      }
      reader.close();
      String mesg = output.toString() + "\n";
      Log.i("PING", mesg);
      mesg = mesg.substring(mesg.lastIndexOf("---") + 4);
      speedBean.setPacketLoss(Double.parseDouble(mesg.substring(mesg.indexOf("received,") + 9, mesg.indexOf("%"))));
      // Array content: min, avg, max, mDev
      String results[] = new String[4];
      int index = 0;
      if (mesg.indexOf("=") > 0) {
        mesg = mesg.substring(mesg.lastIndexOf("=") + 2, mesg.lastIndexOf("ms"));
        StringTokenizer st = new StringTokenizer(mesg, "/");
        while (st.hasMoreTokens()) {
          results[index++] = st.nextToken().trim();
        }
        speedBean.setRttMin(Double.parseDouble(results[0]));
        speedBean.setRttAvg(Double.parseDouble(results[1]));
        speedBean.setRttMax(Double.parseDouble(results[2]));
        speedBean.setRttMdev(Double.parseDouble(results[3]));
        speedBean.setLatency(speedBean.getRttAvg() / 2);
        speedBean.setJitter(speedBean.getRttMdev() / 2);
      }
    } catch (Exception ex) {
      Log.e("SpeedTools.pingServer()", ex.toString());
      ex.printStackTrace();
    }*/
  }

  public static void sendToServer(SpeedBean bean) {
    return;
    /*
    try {
      URL serverUrl = new URL(BACKEND_SERVER);
      HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
      conn.setRequestMethod("POST");
      conn.setDoOutput(true);
      conn.setDoInput(true);
      DataOutputStream out = new DataOutputStream(conn.getOutputStream());
      String params = new StringBuffer().append("CdmaEcio=").append(bean.getCdmaEcio()).append("&DownSpeed=").append(bean.getDownSpeed()).append("&EvdoDbm=")
          .append(bean.getEvdoDbm()).append("&EvdoEcio=").append(bean.getEvdoEcio()).append("&EvdoSnr=").append(bean.getEvdoSnr()).append("&GsmErrRate=")
          .append(bean.getGsmErrRate()).append("&IpAddress=").append(bean.getIpAddress()).append("&Jitter=").append(bean.getJitter()).append("&Latency=").append(bean.getLatency())
          .append("&PacketLoss=").append(bean.getPacketLoss()).append("&RttAvg=").append(bean.getRttAvg()).append("&RttMax=").append(bean.getRttMax()).append("&RttMdev=")
          .append(bean.getRttMdev()).append("&RttMin=").append(bean.getRttMin()).append("&SignalAsu=").append(bean.getSignalAsu()).append("&SignalDbm=")
          .append(bean.getSignalDbm()).append("&SpotNum=").append(bean.getSpotNum()).append("&TelecomNum=").append(bean.getTelecomNum()).append("&TestTime=")
          .append(bean.getTestTime()).append("&UpSpeed=").append(bean.getUpSpeed()).toString();
      Log.i("SpeedTools.sendToServer()", params);
      out.writeBytes(params);
      out.flush();
      out.close();
      InputStream in = conn.getInputStream();
      in.close();
    } catch (Exception ex) {
      Log.e("SpeedTools.sendToServer()", ex.toString());
      ex.printStackTrace();
    }
    */
  }
}