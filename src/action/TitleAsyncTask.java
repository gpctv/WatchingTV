package action;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import bean.TVBean;
import bean.Type;

import com.sandy.watchingTV.MainActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class TitleAsyncTask extends AsyncTask<String, Integer, Boolean> {
	private Activity mParentActivity;
	private ProgressBar spinTask;
	 private int waitnumber =0;
	 private TextView Ms;
	 private SharedPreferences SP;
	 private SharedPreferences SP2;
	 private SharedPreferences SP3;
	 private static SharedPreferences.Editor editor;
	 private static SharedPreferences.Editor editor2;
	 private static SharedPreferences.Editor editor3;
	 ArrayList<HashMap<String,String>> mainToList=new ArrayList<HashMap<String,String>>();
	 Type pType[]=null;
     String TVSource; 
     URL url;
	Document doc;
	 public TitleAsyncTask(Activity parentActivity, ProgressBar bar,TextView message) {
		  // TODO Auto-generated constructor stub
		  super();
		  System.out.println("TitleAsyncTask Start");
		  mParentActivity = parentActivity; //�D�{������
		  spinTask = bar; //bar����
		  Ms=message; //�ʭ�text����
		 }
	
	@Override
	protected Boolean doInBackground(String... arg0) {
		// TODO �۰ʲ��ͪ���k Stub
		int progress = 0;
		 File test=new File(mParentActivity.getApplicationInfo().dataDir,"shared_prefs");
		 System.out.println("path:"+test.getPath()+"/Type.xml");
		 File shareFileType=new File(test.getPath()+"/Type.xml");
		File shareFileTV=new File(test.getPath()+"/TV.xml");
		File shareFileTest=new File(test.getPath()+"/test.xml");
		if(shareFileTest.exists()){//�s�b���ܧP�_�O�_�n��s�`�ت�
			System.out.println("finish--");
		}else{
			shareFileType.delete();
			shareFileTV.delete();
			SP3=mParentActivity.getSharedPreferences("test",1);
			editor3=SP3.edit();
			editor3.putString("ver", "1");
			editor3.commit();
		}
		
		System.out.println("xml �O�_�s�b Type:"+shareFileType.exists());
		System.out.println("xml �O�_�s�b Type:"+shareFileTV.exists());
		 if(isConnection(arg0[0])){ //���������κ����O�_�s��
			if(!shareFileType.exists()&&(!shareFileTV.exists())){
			 getType(); //�N�Ҧ������ɤJpType
			 
			 for(int i=0;i<pType.length;i++){//�q���x�W�ٶ�JTVType
				 getTV(pType[i].getUrl(),pType[i].getPID());//�N�q���x����
			 }
			 
		 //�s��XML
		 saveType();
		 saveTV();
		 return true;
			}else{
				
					   try {
					    Thread.sleep(2000); //����3000milliseconds
					    
					   } catch (InterruptedException e) {
					    // TODO Auto-generated catch block
					    e.printStackTrace();
					   } 
					  
				return true;
			}
		 }else{
			 return false;
		 }
   
		
		 
	}
	@Override
	 protected void onPreExecute() {
		  // TODO Auto-generated method stub
		  super.onPreExecute();
		  Ms.setText("���O�M����J��...");
		  Ms.setText("�q���x�M����J��..." );
		  System.out.println("onPreExecute"); //����e
		  spinTask.setVisibility(View.VISIBLE);
		 }
	 @Override
	 protected void onProgressUpdate(Integer... values) {
	  // TODO Auto-generated method stub
	  super.onProgressUpdate(values);
	  System.out.println("bar Visible");
	 
	  System.out.println("setProgress:"+values[0]);
     
	  
	  spinTask.setVisibility(View.VISIBLE);//Bar���}
	 }
	 @Override
	 protected void onPostExecute(Boolean result) {
	  // TODO Auto-generated method stub
	  super.onPostExecute(result);
	  if(result){
		 
	  System.out.println("onPostExecute");
	  spinTask.setVisibility(View.INVISIBLE); //bar ����
	  Intent intent = new Intent();
	  intent.setClass(mParentActivity, MainActivity.class);
	  mParentActivity.startActivity(intent);//������e�D�{��
	  android.os.Process.killProcess(android.os.Process.myPid());//���F�ʭ�
	  }else{
		  Ms.setText("�L����");
		  Toast.makeText(mParentActivity,"�L����",Toast.LENGTH_SHORT).show();
	  }
	 }
	 protected boolean isConnection(String url1){
		 try{
		 URL url = new URL(url1);
		    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
		    urlc.setRequestProperty("Connection", "close");
		    urlc.setConnectTimeout(1000 * 30); // mTimeout is in seconds
		    urlc.connect();
		    if (urlc.getResponseCode() == 200) {
		         
		        return true;
		    }else{
		    	System.out.println("false internet");
		   return false;
		    }
		 }catch (Exception e){
			 e.printStackTrace();
			 return false;
		 }
	 }
	 //����Ҧ�����
	 protected void getType(){
		 try{
			   TVSource="http://tv.atmovies.com.tw/tv/attv.cfm?action=showtime";
			   url=new URL(TVSource);
			  doc=Jsoup.parse(url,3000);
	          Elements EAllHref=doc.select("a[href~=(groupid)]");
	          pType=new Type[EAllHref.size()+1];
	         for(int i=0;i<EAllHref.size();i++){
	    	        System.out.println(EAllHref.get(i).attr("abs:href"));
	    	      System.out.println(EAllHref.get(i).text());
	    	      System.out.println("PID:"+i);
	    	     pType[i]=new Type();
	    	     pType[i].setPID(i);
	    	     pType[i].setTypeName(EAllHref.get(i).text());
	    	     pType[i].setUrl(EAllHref.get(i).attr("abs:href"));
	    	
	               }
	       //�s�WMOD
	         pType[EAllHref.size()]=new Type();
	         pType[EAllHref.size()].setPID(EAllHref.size());
	         pType[EAllHref.size()].setTypeName("MOD");
	         pType[EAllHref.size()].setUrl("http://tv.atmovies.com.tw/tv/attv.cfm?action=showtime&groupid=O");
	         System.out.println("MOD");
	         System.out.println("http://tv.atmovies.com.tw/tv/attv.cfm?action=showtime&groupid=O");
	         System.out.println("MOD pid:"+EAllHref.size());
			  }catch(Exception e){
				  e.printStackTrace();
			  }
	 }
	 protected void getTV(String Typeurl,int PID){
		 try{
			url=new URL(Typeurl);
			doc=Jsoup.parse(url,10000);
			Elements Alla=doc.select("a[href~=(channeltime&tday)]");
			System.out.println("Alla Size:"+Alla.size());
			
			 
			
			for(int i=0;i<Alla.size();i++){
				System.out.println(Alla.get(i).attr("abs:href"));//�W�s��
				String cURL[]=Alla.get(i).attr("abs:href").split("[?]");
				System.out.println(Alla.get(i).text());//�q���x�W��
				System.out.println("cannelNO--"+Alla.get(i).attr("abs:href").substring(84));//�W�D�Ʀr
				System.out.println("&"+cURL[1].split("&")[0]);
				//�]���q���������`�ج��ʺA�A����������������~����`��
				System.out.println("cURL:"+"http://m.atmovies.com.tw/app2/attv.cfm?"+cURL[1].split("&")[0]+"&cid="+Alla.get(i).attr("abs:href").substring(84, 88));//���զ�APP�������}
		    	 System.out.println("PID:"+PID);
		    	 HashMap<String,String> subList=new HashMap<String,String>();
               if(Typeurl.substring(62,63).equals("O")){
					//��MOD���q���x
				subList.put("Channel", Alla.get(i).attr("abs:href").substring(84));
				subList.put("URL", "http://m.atmovies.com.tw/app2/attv.cfm?"+cURL[1].split("&")[0]+"&cid="+Alla.get(i).attr("abs:href").substring(84));//���զ�APP�������}
			     System.out.println("MOD CHANNEL:"+Alla.get(i).attr("abs:href").substring(84));
				}else{
					subList.put("Channel",Alla.get(i).attr("abs:href").substring(84));//mod�W�D���X�r�Ƥ��P
					subList.put("URL","http://m.atmovies.com.tw/app2/attv.cfm?"+cURL[1].split("&")[0]+"&cid="+Alla.get(i).attr("abs:href").substring(84));//���զ�APP�������}
				}
               subList.put("PID",String.valueOf(PID));
               subList.put("TVName", Alla.get(i).text());
               
			   mainToList.add(subList);
			}
		
	           }catch (Exception e) {
				// TODO �۰ʲ��ͪ� catch �϶�
				e.printStackTrace();
			} 
		 
	 }
	 
	 protected void saveType(){
		 SP=mParentActivity.getSharedPreferences("Type",0);
		 editor=SP.edit();
		 editor.clear();
		 editor.commit();//�N�ɮץ����b��
		 for(int i=0;i<pType.length;i++){
			 System.out.println("Type:"+pType[i].getTypeName()+","+pType[i].getUrl()+","+pType[i].getPID());
		 editor.putString(Integer.toString(pType[i].getPID()), pType[i].getTypeName()+","+pType[i].getUrl()+","+pType[i].getPID());
		 editor.commit();
		 }
		 
	 }
	 protected void saveTV(){
		 SP2=mParentActivity.getSharedPreferences("TV",1);
		 editor2=SP2.edit();
		 editor2.clear();
		 editor2.commit();//�N�ɮץ����b��
		 HashMap<String, String> subListValue ;
		 Iterator<HashMap<String, String>> value= mainToList.iterator();
			 for(int j=0;j<mainToList.size();j++){
				 subListValue=value.next();
			 System.out.println("TV:"+subListValue.get("PID")+","+subListValue.get("TVName")
					 +","+subListValue.get("URL")+subListValue.get("Channel") );
			 
			 editor2.putString(Integer.toString(j),subListValue.get("PID")+","+subListValue.get("TVName")
					 +","+subListValue.get("URL")+","+subListValue.get("Channel"));
			 editor2.commit();
			 }
		
		
	 }
	
}
