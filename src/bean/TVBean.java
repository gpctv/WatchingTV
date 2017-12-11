package bean;

public class TVBean {
private String TVName;
private String URL;
private String ChannelNo;
private int PID;
public String getTVName(){
	return this.TVName;
}
public String getURL(){
	return this.URL;
}
public String getChannelNo(){
	return this.ChannelNo;
}
public Integer getPID(){
	return this.PID;
}
public void setTVName(String TVName){
	this.TVName=TVName;
}
public void setURL(String URL){
	this.URL=URL;
}
public void setChannelNO(String ChannelNo){
	this.ChannelNo=ChannelNo;
}
public void setPID(int PID){
	this.PID=PID;
}

}
