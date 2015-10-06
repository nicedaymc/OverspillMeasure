package ipm.esap.gerdc.speedtest;

public class SpeedBean {
  private String spotNum, telecomNum, ipAddress, testTime, networkOperator, connectType;

  private double downSpeed, upSpeed, packetLoss, latency, jitter, rttAvg, rttMax, rttMdev, rttMin;

  private int signalLevel, signalAsu, signalDbm, cdmaEcio, cdmaDbm, evdoDbm, evdoEcio, evdoSnr, gsmErrRate, cellID, conTypeInt;

  public SpeedBean() {
    super();
  }

  public int getConTypeInt(){return conTypeInt;}
  public void setConTypeInt(int conTypeInt){this.conTypeInt=conTypeInt;}

  public String getNetworkOperator() {
    return networkOperator;
  }

  public void setNetworkOperator(String networkOperator) {
    this.networkOperator = networkOperator;
  }
  public String getConnectType() {
    return connectType;
  }

  public void setConnectType(String connectType) {
    this.connectType = connectType;
  }

  public String getSpotNum() {
    return spotNum;
  }

  public void setSpotNum(String spotNum) {
    this.spotNum = spotNum;
  }

  public String getTelecomNum() {
    return telecomNum;
  }

  public void setTelecomNum(String telecomNum) {
    this.telecomNum = telecomNum;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public String getTestTime() {
    return testTime;
  }

  public void setTestTime(String testTime) {
    this.testTime = testTime;
  }

  public double getDownSpeed() {
    return downSpeed;
  }

  public void setDownSpeed(double downSpeed) {
    this.downSpeed = downSpeed;
  }

  public double getUpSpeed() {
    return upSpeed;
  }

  public void setUpSpeed(double upSpeed) {
    this.upSpeed = upSpeed;
  }

  public double getPacketLoss() {
    return packetLoss;
  }

  public void setPacketLoss(double packetLoss) {
    this.packetLoss = packetLoss;
  }

  public double getLatency() {
    return latency;
  }

  public void setLatency(double latency) {
    this.latency = latency;
  }

  public double getJitter() {
    return jitter;
  }

  public void setJitter(double jitter) {
    this.jitter = jitter;
  }

  public double getRttAvg() {
    return rttAvg;
  }

  public void setRttAvg(double rttAvg) {
    this.rttAvg = rttAvg;
  }

  public double getRttMax() {
    return rttMax;
  }

  public void setRttMax(double rttMax) {
    this.rttMax = rttMax;
  }

  public double getRttMdev() {
    return rttMdev;
  }

  public void setRttMdev(double rttMdev) {
    this.rttMdev = rttMdev;
  }

  public double getRttMin() {
    return rttMin;
  }

  public void setRttMin(double rttMin) {
    this.rttMin = rttMin;
  }

  public int getSignalLevel() {
    return signalLevel;
  }

  public void setSignalLevel(int signalLevel) {
    this.signalLevel = signalLevel;
  }

  public int getSignalAsu() {
    return signalAsu;
  }

  public void setSignalAsu(int signalAsu) {
    this.signalAsu = signalAsu;
  }

  public int getSignalDbm() {
    return signalDbm;
  }

  public void setSignalDbm(int signalDbm) {
    this.signalDbm = signalDbm;
  }

  public int getCdmaEcio() {
    return cdmaEcio;
  }

  public void setCdmaEcio(int cdmaEcio) {
    this.cdmaEcio = cdmaEcio;
  }

  public int getEvdoDbm() {
    return evdoDbm;
  }

  public void setEvdoDbm(int evdoDbm) {
    this.evdoDbm = evdoDbm;
  }

  public int getCdmaDbm() {
    return cdmaDbm;
  }

  public void setCdmaDbm(int cdmaDbm) {
    this.cdmaDbm = cdmaDbm;
  }

  public int getEvdoEcio() {
    return evdoEcio;
  }

  public void setEvdoEcio(int evdoEcio) {
    this.evdoEcio = evdoEcio;
  }

  public int getEvdoSnr() {
    return evdoSnr;
  }

  public void setEvdoSnr(int evdoSnr) {
    this.evdoSnr = evdoSnr;
  }

  public int getGsmErrRate() {
    return gsmErrRate;
  }

  public void setGsmErrRate(int gsmErrRate) {
    this.gsmErrRate = gsmErrRate;
  }

  public int getCellID() {
    return cellID;
  }

  public void setCellID(int cellID) {
    this.cellID = cellID;
  }
}