package ipm.esap.gerdc.speedtest;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements OnItemSelectedListener ,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener , LocationListener{
  private SpeedBean speedBean = new SpeedBean();
  private Spinner spinner2, spinner3;
  private EditText editText;
  private ToggleButton btnSubmit;
  private TextView textView1, textView2;
  private TelephonyManager phoneManager;
  private IPMPhoneStateListener phoneListener;
  private boolean runJobs = false;
  private NetInfo netInfo;
  //private FallbackLocationTracker myTracker;
  String msg="";

  private GoogleApiClient mGoogleApiClient;
  private Location mLastLocation;
  private boolean mRequestingLocationUpdates=true;
  private LocationRequest mLocationRequest;
  private String sim_provider="unknown";
  Geocoder geocoder;
  private double lon, lat;
  private String countryCode="unknown";
  private String phonestate="unknown";


  private void addListenerOnButton() {
    //spinner1 = (Spinner) findViewById(R.id.spinner1);
    //spinner1.setSelection(0);
    //spinner1.setOnItemSelectedListener(this);
    editText=(EditText)findViewById(R.id.editText);

    spinner2 = (Spinner) findViewById(R.id.spinner2);
    spinner2.setSelection(0);
    spinner2.setOnItemSelectedListener(this);
    spinner3 = (Spinner) findViewById(R.id.spinner3);
    spinner3.setSelection(0);
    spinner3.setOnItemSelectedListener(this);
    btnSubmit = (ToggleButton) findViewById(R.id.btnSubmit);
    btnSubmit.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (btnSubmit.isChecked()) {
          Log.i("MainActivity.addListenerOnButton().onClick()", "Start speed test.");
          btnSubmit.setText(R.string.stop_button);
          runJobs = true;


          Toast.makeText(
                  MainActivity.this,
                  new StringBuffer().append("Start Measuring: \nOption1: ").append(editText.getText().toString())     //String.valueOf(spinner1.getSelectedItem())).append("\nOption2: ")
                          .append(String.valueOf(spinner2.getSelectedItem())).append("\nOption3: ").append(String.valueOf(spinner3.getSelectedItem())).toString(), Toast.LENGTH_SHORT)
                  .show();
          //speedBean.setSpotNum(Integer.toString(spinner1.getSelectedItemPosition()));
          speedBean.setTelecomNum(Integer.toString(spinner2.getSelectedItemPosition()));
          scheduleTasks(Integer.parseInt(String.valueOf(spinner3.getSelectedItem())));
        } else {
          Log.i("MainActivity.addListenerOnButton().onClick()", "Stop Measruing.");
          btnSubmit.setText(R.string.start_button);
          runJobs = false;
        }
      }
    });
  }

  private void showMesg(boolean testing) {
    double longtitude=-1;
    double latitude=-1;
    if (mLastLocation!=null){
      longtitude=mLastLocation.getLongitude();
      latitude=mLastLocation.getLatitude();

      if ((longtitude!=lon)||(latitude!=lat)){
        lon=longtitude;
        lat=latitude;
        ReadCountry readCountry=new ReadCountry();
        readCountry.start();
      }

      //System.out.println(mLastLocation.toString());
      //System.out.println("provider: "+mLastLocation.getProvider());
    }else{
      longtitude=-1;
      latitude=-1;
    }
    String mesg = new StringBuffer().append(speedBean.getNetworkOperator() + ", " + speedBean.getConnectType() + ", " + speedBean.getSignalDbm()+"dBm")
            .append("\n" + longtitude + ", " + latitude+"  "+countryCode).toString();

    textView1 = (TextView) findViewById(R.id.textView1);
    textView1.setText(mesg);

    String testLocation=editText.getText().toString();


    mesg = testing ? "" : new StringBuffer() .append("\nTest parameters:\n   Option1: ").append(testLocation)    //String.valueOf(spinner1.getSelectedItem()))
            .append("\n   Option2: ").append(String.valueOf(spinner2.getSelectedItem())).append("\n   Option3: ").append(String.valueOf(spinner3.getSelectedItem()))

            .append("\nMeasuring Results:\n" + msg + "\n   sim: "+sim_provider+" CdmaEcio:"+speedBean.getCdmaEcio()+ " EvdoEcio:"+speedBean.getEvdoEcio()+ " EvdoSnr:"+speedBean.getEvdoSnr()+ " GsmErr:"+speedBean.getGsmErrRate())

            //.append(speedBean.getTestTime()).append("\n   IP Address: ").append(speedBean.getIpAddress())
            .append("\n Signal Strength: ").append(speedBean.getSignalDbm()).append(" dBm ").append(speedBean.getSignalAsu()).append("asu")
         //  .append("asu\n   Download Speed: ")
       // .append(SpeedTools.formatNum(speedBean.getDownSpeed())).append("Mbps\n   Upload Speed: ").append(SpeedTools.formatNum(speedBean.getUpSpeed()))
       // .append(" Mbps\n   Packet Loss: ").append(speedBean.getPacketLoss()).append("%\n   Latency: ").append(SpeedTools.formatNum(speedBean.getLatency()))
        //.append("ms\n   Jitter: ")
         //   .append(SpeedTools.formatNum(speedBean.getJitter()))
         .append("\n Write to file...").toString();

    textView2 = (TextView) findViewById(R.id.textView2);
    textView2.setText(mesg);

    writeToFile(speedBean.getTestTime(),testLocation,sim_provider,speedBean.getNetworkOperator(), speedBean.getCellID(),speedBean.getConnectType(),speedBean.getConTypeInt(),latitude,longtitude,speedBean.getSignalDbm(),speedBean.getSignalAsu(),
    speedBean.getCdmaEcio(),speedBean.getEvdoDbm(),speedBean.getEvdoSnr(),speedBean.getGsmErrRate());
    textView2.append("Write finish. Wait for next measuring..");

    final TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    int dBmlevel=-999;
    int asulevel=-999;
    List<CellInfo> cellInfoList = telephony.getAllCellInfo();

    //Checking if list values are not null
    if (cellInfoList != null) {
      System.out.println("Get All Cells="+cellInfoList.size());
      for (final CellInfo info : cellInfoList) {
        if (info instanceof CellInfoGsm) {
          //GSM Network
          CellSignalStrengthGsm cellSignalStrength = ((CellInfoGsm)info).getCellSignalStrength();
          dBmlevel = cellSignalStrength.getDbm();
          asulevel = cellSignalStrength.getAsuLevel();
          CellInfoGsm cellGsm=(CellInfoGsm)info;
          System.out.println("gsm:"+dBmlevel+" asu:"+asulevel+" Cell Identity"+cellGsm.toString());   //cellGsm.getCellIdentity().toString());
        }
        else if (info instanceof CellInfoCdma) {
          //CDMA Network
          CellSignalStrengthCdma cellSignalStrength = ((CellInfoCdma)info).getCellSignalStrength();
          dBmlevel = cellSignalStrength.getCdmaDbm();
          asulevel = cellSignalStrength.getAsuLevel();

          System.out.println("cdma:"+dBmlevel +" asu:"+asulevel+" Cell Identity"+((CellInfoCdma)info).toString());
        }
        else if (info instanceof CellInfoLte) {
          //LTE Network
          CellSignalStrengthLte cellSignalStrength = ((CellInfoLte)info).getCellSignalStrength();
          dBmlevel = cellSignalStrength.getDbm();
          asulevel = cellSignalStrength.getAsuLevel();
          CellInfoLte cellLte=(CellInfoLte)info;
          System.out.println("lte:"+dBmlevel+" asu:"+asulevel+" Cell Identity"+cellLte.toString());
        }
        else if  (info instanceof CellInfoWcdma) {
          //WCDMA Network
          CellSignalStrengthWcdma cellSignalStrength = ((CellInfoWcdma)info).getCellSignalStrength();
          dBmlevel = cellSignalStrength.getDbm();
          asulevel = cellSignalStrength.getAsuLevel();

          CellInfoWcdma cellWcdma=(CellInfoWcdma)info;

          System.out.println("wcdma:"+dBmlevel+" asu:"+asulevel+" Cell Identity"+cellWcdma.toString());    //cellWcdma.getCellIdentity().toString());
        }
        else{
          //Developed as a Cordova plugin, that's why I'm using callbackContext
         // callbackContext.error("Unknown type of cell signal.");
         System.out.println("Unknown type of cell signal.");
        }
      }
    }

    System.out.println("dbm:" + dBmlevel + " asu:" + asulevel);


  }

  private void writeToFile (String time, String testLocation, String sim, String network, int cellID,String connType,int conTypeInt, double longtitude, double latitude, int signalDbm, int singalAsu ,int cdma_ecio, int evdo_ecio, int evdo_snr, int gsm_err){
    String date=time.substring(0,10);
    String fileName = date+".csv";
    String dirName = "Download";
    //String contentToWrite = "Your Content Goes Here";
    File myDir = new File("sdcard", dirName);

/*if directory doesn't exist, create it*/
    if(!myDir.exists())
      myDir.mkdirs();


    File myFile = new File(myDir, fileName);

/*Write to file*/
    try {
      FileWriter fileWriter = new FileWriter(myFile,true);
      fileWriter.append(time+","+countryCode+","+testLocation+","+sim+","+network+","+cellID+","+conTypeInt+
              ","+connType+","+longtitude+","+latitude+","+signalDbm+","+singalAsu+","+cdma_ecio+","+evdo_ecio+","+evdo_snr+","+gsm_err+","+phonestate+"\n");

      fileWriter.flush();
      fileWriter.close();
    }
    catch(IOException e){
      e.printStackTrace();
    }
  }

  public void startSpeedTest() {
    try {
      
      final TelephonyManager telephony = phoneManager;//(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
      //System.out.println(telephony.getNetworkCountryIso()+" "+telephony.getSimCountryIso()+" "+telephony.isNetworkRoaming());
      speedBean.setRoaming(telephony.isNetworkRoaming()); //record roaming
      int connecTypeInt=telephony.getNetworkType();
      String connectType="";
      String networkOperator=telephony.getNetworkOperatorName();
      speedBean.setNetworkOperator(networkOperator );
      switch (connecTypeInt) {
      case TelephonyManager.NETWORK_TYPE_1xRTT : connectType="1xRTT";break;
      case TelephonyManager.NETWORK_TYPE_CDMA : connectType="CDMA";break;
      case TelephonyManager.NETWORK_TYPE_EDGE : connectType="EDGE";break;
      case TelephonyManager.NETWORK_TYPE_EHRPD : connectType="EHRPD";break;
      case TelephonyManager.NETWORK_TYPE_EVDO_0 : connectType="EVDO_0";break;
      case TelephonyManager.NETWORK_TYPE_EVDO_A : connectType="EVDO_A";break;
      case TelephonyManager.NETWORK_TYPE_EVDO_B : connectType="EVDO_B";break;
      case TelephonyManager.NETWORK_TYPE_GPRS : connectType="GPRS";break;
      case TelephonyManager.NETWORK_TYPE_HSDPA : connectType="HSDPA";break;
      case TelephonyManager.NETWORK_TYPE_HSPA : connectType="HSPA";break;
      case TelephonyManager.NETWORK_TYPE_HSPAP : connectType="HSPAP";break;
      case TelephonyManager.NETWORK_TYPE_HSUPA : connectType="HSUPA";break;
      case TelephonyManager.NETWORK_TYPE_UMTS : connectType="UMTS";break;
        case TelephonyManager.NETWORK_TYPE_LTE : connectType="LTE"; break;
      case TelephonyManager.NETWORK_TYPE_UNKNOWN : connectType="UNKNOWN";break;
      }
      speedBean.setConnectType(connectType);
      speedBean.setConTypeInt(connecTypeInt);
      if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
          final GsmCellLocation location = (GsmCellLocation) telephony.getCellLocation();
          if (location != null) {
              msg="MNO: "+networkOperator+" Conn: "+connectType+ " CID: " + location.getCid();
              speedBean.setCellID(location.getCid());

          }
      }else if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA){
        final CdmaCellLocation location = (CdmaCellLocation) telephony.getCellLocation();
              if (location != null) {
            msg="MNO: "+networkOperator+" Conn: "+connectType+ " CID: "+location.getBaseStationId();
            speedBean.setCellID(location.getBaseStationId());
        }
      }

      //speedBean.setSpotNum(Integer.toString(spinner1.getSelectedItemPosition()));
      speedBean.setTelecomNum(Integer.toString(spinner2.getSelectedItemPosition()));
      speedBean.setIpAddress(netInfo.getIPAddress() == null ? "0.0.0.0" : netInfo.getIPAddress());
      speedBean.setTestTime(SpeedTools.getSysTime());
      SpeedTools.pingServer(10, speedBean);
      //speedBean.setDownSpeed(Loader.downloadFileFromServer(SpeedTools.LOAD_FILE, SpeedTools.DOWNLOAD_LINK + SpeedTools.LOAD_FILE));
     // speedBean.setUpSpeed(Loader.uploadFileToServer(SpeedTools.LOAD_FILE, SpeedTools.UPLOAD_LINK));
      SpeedTools.sendToServer(speedBean);

      
     // System.out.println("Measuring..........");


    } catch (Exception ex) {
      Log.e("MainActivity.startSpeedTest()", ex.toString());
      ex.printStackTrace();
    }
  }

  public void scheduleTasks(int intervalTime) {

    System.out.println("start scheduled task..." + intervalTime);
    final Handler handler = new Handler();
    final Timer timer = new Timer();
    TimerTask doAsynchronousTask = new TimerTask() {
      @Override
      public void run() {
        handler.post(new Runnable() {
          public void run() {
            try {
              if (runJobs) {
                new SpeedTest().execute("Scheduled IPM Speed Test");
                Log.i("MainActivity.scheduleTasks()", "Start scheduled job at " + SpeedTools.getSysTime());
              } else {
                timer.cancel();
                timer.purge();
              }
            } catch (Exception ex) {
              Log.e("MainActivity.scheduleTasks()", ex.toString());
              ex.printStackTrace();
            }
          }
        });
      }
    };
    timer.schedule(doAsynchronousTask, 1, intervalTime);//SpeedTools.getIntervalTime(intervalTime));
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_page);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    netInfo = new NetInfo(this);
    phoneListener = new IPMPhoneStateListener();
    phoneManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

    phoneManager.listen(phoneListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    phoneManager.listen(phoneListener, PhoneStateListener.LISTEN_SERVICE_STATE);

    //myTracker=new FallbackLocationTracker(this);
    //myTracker.start();

    if (phoneManager.getSimState()==TelephonyManager.SIM_STATE_READY){
      sim_provider=phoneManager.getSimOperator();
     // sim_provider=phoneManager.getSimOperatorName();
    }else{
      sim_provider=phoneManager.getSimOperator();
    }
    geocoder= new Geocoder(this, Locale.getDefault());
    buildGoogleApiClient();


    addListenerOnButton();

  }

  protected synchronized void buildGoogleApiClient() {
    mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();
  }

  protected void createLocationRequest() {
    mLocationRequest= new LocationRequest();
    mLocationRequest.setInterval(2000);
    mLocationRequest.setFastestInterval(1000);
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
  }

  protected void startLocationUpdates() {
    createLocationRequest();
    LocationServices.FusedLocationApi.requestLocationUpdates(
            mGoogleApiClient, mLocationRequest, this);
  }
  protected void stopLocationUpdates() {
    LocationServices.FusedLocationApi.removeLocationUpdates(
            mGoogleApiClient, this);
  }

  @Override
  protected void onDestroy() {
    runJobs = false;
    super.onDestroy();
  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    Toast.makeText(parent.getContext(), "Chosen: " + parent.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onNothingSelected(AdapterView<?> arg0) {
  }

  @Override
  protected void onPause() {
    super.onPause();
    phoneManager.listen(phoneListener, PhoneStateListener.LISTEN_NONE);
   // myTracker.stop();
    stopLocationUpdates();
    mRequestingLocationUpdates=false;

  }

  @Override
  protected void onResume() {
    super.onResume();
    phoneManager.listen(phoneListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    //myTracker.start();
    if (!mGoogleApiClient.isConnected()){
      mGoogleApiClient.connect();
    }
    if (!mRequestingLocationUpdates) {
      startLocationUpdates();
      mRequestingLocationUpdates=true;
    }

  }

  @Override
  public void onConnected(Bundle bundle) {
    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
            mGoogleApiClient);
    if (mLastLocation != null) {
      System.out.println("GPS connected...");
     // mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
     // mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
    }
    if (mRequestingLocationUpdates) {
      startLocationUpdates();
      mRequestingLocationUpdates=true;
    }


  }

  @Override
  public void onConnectionSuspended(int i) {

  }

  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {

    System.out.println("ConnectionFailed:"+connectionResult.toString() );

  }

  @Override
  public void onLocationChanged(Location location) {
    mLastLocation = location;
  }

  @Override
  protected void onStart() {
    super.onStart();
   // if (!mResolvingError) {  // more about this later
      mGoogleApiClient.connect();
   // }
  }

  @Override
  protected void onStop() {
    mGoogleApiClient.disconnect();
    super.onStop();
  }
  @Override
  protected void onRestart() {
    super.onRestart();
    mGoogleApiClient.connect();

  }



  class IPMPhoneStateListener extends PhoneStateListener {
    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
      super.onSignalStrengthsChanged(signalStrength);
      speedBean.setCdmaEcio(signalStrength.getCdmaEcio());
      speedBean.setCdmaDbm(signalStrength.getCdmaDbm());
      speedBean.setEvdoDbm(signalStrength.getEvdoDbm());
      speedBean.setEvdoEcio(signalStrength.getEvdoEcio());
      speedBean.setEvdoSnr(signalStrength.getEvdoSnr());
      speedBean.setGsmErrRate(signalStrength.getGsmBitErrorRate());


      speedBean.setSignalAsu(signalStrength.getGsmSignalStrength());
      speedBean.setSignalDbm(signalStrength.getCdmaDbm());
      // dBm = 2 � asu - 113
      if (phoneManager.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
        speedBean.setSignalDbm(signalStrength.getCdmaDbm());
        speedBean.setSignalAsu((speedBean.getSignalDbm() + 113) / 2);
      } else if (phoneManager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
        speedBean.setSignalAsu(signalStrength.getGsmSignalStrength());
        speedBean.setSignalDbm(2 * speedBean.getSignalAsu() - 113);
      }
      System.out.println("phone signal update cdma:"+signalStrength.getCdmaDbm()+" evdo:"+signalStrength.getEvdoDbm()+" gsm:"+signalStrength.getGsmSignalStrength()+ " is gsm?:"+signalStrength.isGsm());
    }
    @Override
    public void onServiceStateChanged (ServiceState serviceState) {
      super.onServiceStateChanged(serviceState);


      switch(serviceState.getState()) {
        case ServiceState.STATE_EMERGENCY_ONLY:
          phonestate ="EMERGENCY_ONLY";
          break;
        case ServiceState.STATE_IN_SERVICE:
          phonestate ="IN_SERVICE";
          break;
        case ServiceState.STATE_OUT_OF_SERVICE:
          phonestate ="OUT_OF_SERVICE";
          break;
        case ServiceState.STATE_POWER_OFF:
          phonestate ="POWER_OFF";
          break;
        default:
          phonestate = "Unknown";
          break;
      }
      System.out.println("service state="+phonestate);
    }
  };

  class SpeedTest extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... params) {
      startSpeedTest();
      return null;
    }

    @Override
    protected void onPostExecute(String result) {
      showMesg(false);
    }

    @Override
    protected void onPreExecute() {
      //showMesg(true);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
  }

  class ReadCountry extends Thread{
    public void run() {

      String result = null;
      try {
        List<Address> addressList = geocoder.getFromLocation(
                lat, lon, 1);
        if (addressList != null && addressList.size() > 0) {
          Address address = addressList.get(0);
         /*
          StringBuilder sb = new StringBuilder();
          for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
            sb.append(address.getAddressLine(i)).append("\n");
          }
          sb.append(address.getLocality()).append("\n");
          sb.append(address.getPostalCode()).append("\n");
          sb.append(address.getCountryName());
          result = sb.toString();
          */
          countryCode=address.getCountryCode();

        }else{
          countryCode="unknown";
        }

       // System.out.println("Got country code:"+countryCode);
      } catch (IOException e) {
        //System.out.println("Unable connect to Geocoder"+ e);
      }

    }
  }
}