package bean;

public class AlertListBean {
	String TVName;
	String TVChannel;
	String PGTime;
	String PGName;
	String PDate;
	String STime;
	String SDate;
	public void setPDate(String PDate){
		this.PDate=PDate;
	}
	public void setTVName(String TVName){
		this.TVName=TVName;
	}
	public void setTVChannel(String TVChannel){
		this.TVChannel=TVChannel;
	}
	public void setPGTime(String PGTime){
		this.PGTime=PGTime;
		
	}
	public void setPGName(String PGName){
		this.PGName=PGName;
	}
	public void setSTime(String STime){
		this.STime=STime;
	}
	public void setSDate(String SDate){
		this.SDate=SDate;
	}
	
	public String getTVName(){
		return this.TVName;
	}
	public String getTVChannel(){
		return this.TVChannel;
	}
	public String getPGTime(){
		return this.PGTime;
	}
	public String getPGName(){
		return this.PGName;
	}
	public String getSTime(){
		return this.STime;
	}
	public String getSDate(){
		return this.SDate;
	}
	public String getPDate(){
		return this.PDate;
	}
	
}
