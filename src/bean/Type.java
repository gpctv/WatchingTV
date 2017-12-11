package bean;

public class Type {
private String typeName;
private String url;
private int PID;

public void setTypeName(String typeName){
	
	this.typeName=typeName;
	
}
public void setUrl(String url){
	this.url=url;
}
public void setPID(int pid){
	this.PID=pid;
}
public String getTypeName(){
	return this.typeName;
}
public String getUrl(){
	return this.url;
}
public Integer getPID(){
	return this.PID;
}

}
