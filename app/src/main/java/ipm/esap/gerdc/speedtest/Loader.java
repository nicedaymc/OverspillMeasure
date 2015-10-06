package ipm.esap.gerdc.speedtest;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Environment;
import android.util.Log;

public class Loader {
  public static double uploadFileToServer(String filename, String targetUrl) throws Exception {
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";
    File sourceFile = new File(Environment.getExternalStorageDirectory() + "/" + filename);
    int bytesRead, bytesAvailable, bufferSize;
    byte[] buffer;

    long t1 = SpeedTools.getMillisecond();
    FileInputStream fileInputStream = new FileInputStream(sourceFile);
    URL url = new URL(targetUrl);
    // Open a HTTP connection to the URL
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setDoInput(true); // Allow Inputs
    conn.setDoOutput(true); // Allow Outputs
    conn.setUseCaches(false); // Don't use a Cached Copy
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Connection", "Keep-Alive");
    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
    conn.setRequestProperty("uploaded_file", filename);
    conn.setConnectTimeout(30000);

    DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
    dos.writeBytes(twoHyphens + boundary + lineEnd);
    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + filename + "\"" + lineEnd);
    dos.writeBytes(lineEnd);

    // create a buffer of maximum size
    bytesAvailable = fileInputStream.available();
    bufferSize = Math.min(bytesAvailable, SpeedTools.BUFFER_SIZE);
    buffer = new byte[bufferSize];
    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
    long fileSize = bytesAvailable;
    while (bytesRead > 0) {
      dos.write(buffer, 0, bufferSize);
      bytesAvailable = fileInputStream.available();
      bufferSize = Math.min(bytesAvailable, SpeedTools.BUFFER_SIZE);
      bytesRead = fileInputStream.read(buffer, 0, bufferSize);
    }

    // send multipart form data necessary after file data...
    dos.writeBytes(lineEnd);
    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

    // Responses from the server (code and message)
    int serverResponseCode = conn.getResponseCode();
    String serverResponseMessage = conn.getResponseMessage();
    Log.i("Loader.uploadFileToServer()", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
    if (serverResponseCode == 200) {
      String msg = "File Upload Completed.\nSee uploaded file here : http://gerdc.ipm.edu.mo/speedtest/uploads/" + filename;
      Log.i("Loader.uploadFileToServer()", msg);
    }
    fileInputStream.close();
    dos.flush();
    dos.close();
    conn.disconnect();
    long t2 = SpeedTools.getMillisecond();
    Log.i("Loader.uploadFileToServer()", "Finish uploaded " + fileSize + " bytes!\nFrom: " + t1 + " to " + t2);
    return SpeedTools.countSpeed(t1, t2, fileSize);
  }

  public static double downloadFileFromServer(String filename, String urlString) throws Exception {
    long t1 = SpeedTools.getMillisecond();
    File extDir = Environment.getExternalStorageDirectory();
    BufferedInputStream in = null;
    while (in == null) {
      in = new BufferedInputStream((new URL(urlString)).openStream());
    }
    FileOutputStream fout = new FileOutputStream(extDir + "/" + filename);
    byte data[] = new byte[SpeedTools.BUFFER_SIZE];
    int count;
    long fileSize = 0;
    while ((count = in.read(data, 0, SpeedTools.BUFFER_SIZE)) != -1) {
      fout.write(data, 0, count);
      fileSize += count;
    }
    in.close();
    fout.close();
    long t2 = SpeedTools.getMillisecond();
    Log.i("Loader.downloadFileFromServer()", "Finish downloaded " + fileSize + " bytes!\nFrom: " + t1 + " to " + t2);
    return SpeedTools.countSpeed(t1, t2, fileSize);
  }
}