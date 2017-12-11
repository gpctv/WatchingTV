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
		  mParentActivity = parentActivity; //主程式物件
		  spinTask = bar; //bar物件
		  Ms=message; //封面text物件
		 }
	
	@Override
	protected Boolean doInBackground(String... arg0) {
		// TODO 自動產生的方法 Stub
		int progress = 0;
		 File test=new File(mParentActivity.getApplicationInfo().dataDir,"shared_prefs");
		 System.out.println("path:"+test.getPath()+"/Type.xml");
		 File shareFileType=new File(test.getPath()+"/Type.xml");
		File shareFileTV=new File(test.getPath()+"/TV.xml");
		File shareFileTest=new File(test.getPath()+"/test.xml");
		if(shareFileTest.exists()){//存在的話判斷是否要更新節目表
			System.out.println("finish--");
		}else{
			shareFileType.delete();
			shareFileTV.delete();
			SP3=mParentActivity.getSharedPreferences("test",1);
			editor3=SP3.edit();
			editor3.putString("ver", "1");
			editor3.commit();
		}
		
		System.out.println("xml 是否存在 Type:"+shareFileType.exists());
		System.out.println("xml 是否存在 Type:"+shareFileTV.exists());
		 if(isConnection(arg0[0])){ //偵測網路及網頁是否存活
			if(!shareFileType.exists()&&(!shareFileTV.exists())){
			 getType(); //將所有種類賽入pType
			 
			 for(int i=0;i<pType.length;i++){//電視台名稱塞入TVType
				 getTV(pType[i].getUrl(),pType[i].getPID());//將電視台分類
			 }
			 
		 //存成XML
		 saveType();
		 saveTV();
		 return true;
			}else{
				
					   try {
					    Thread.sleep(2000); //延遲3000milliseconds
					    
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
		  Ms.setText("類別清單載入中...");
		  Ms.setText("電視台清單載入中..." );
		  System.out.println("onPreExecute"); //執行前
		  spinTask.setVisibility(View.VISIBLE);
		 }
	 @Override
	 protected void onProgressUpdate(Integer... values) {
	  // TODO Auto-generated method stub
	  super.onProgressUpdate(values);
	  System.out.println("bar Visible");
	 
	  System.out.println("setProgress:"+values[0]);
     
	  
	  spinTask.setVisibility(View.VISIBLE);//Bar打開
	 }
	 @Override
	 protected void onPostExecute(Boolean result) {
	  // TODO Auto-generated method stub
	  super.onPostExecute(result);
	  if(result){
		 
	  System.out.println("onPostExecute");
	  spinTask.setVisibility(View.INVISIBLE); //bar 隱藏
	  Intent intent = new Intent();
	  intent.setClass(mParentActivity, MainActivity.class);
	  mParentActivity.startActivity(intent);//轉跳內容主程式
	  android.os.Process.killProcess(android.os.Process.myPid());//殺了封面
	  }else{
		  Ms.setText("無網路");
		  Toast.makeText(mParentActivity,"無網路",Toast.LENGTH_SHORT).show();
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
	 //獲取所有種類
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
	       //新增MOD
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
				System.out.println(Alla.get(i).attr("abs:href"));//超連結
				String cURL[]=Alla.get(i).attr("abs:href").split("[?]");
				System.out.println(Alla.get(i).text());//電視台名稱
				System.out.println("cannelNO--"+Alla.get(i).attr("abs:href").substring(84));//頻道數字
				System.out.println("&"+cURL[1].split("&")[0]);
				//因為電腦版網頁節目為動態，切換成手機版網頁才能抓到節目
				System.out.println("cURL:"+"http://m.atmovies.com.tw/app2/attv.cfm?"+cURL[1].split("&")[0]+"&cid="+Alla.get(i).attr("abs:href").substring(84, 88));//重組成APP網頁網址
		    	 System.out.println("PID:"+PID);
		    	 HashMap<String,String> subList=new HashMap<String,String>();
               if(Typeurl.substring(62,63).equals("O")){
					//抓MOD的電視台
				subList.put("Channel", Alla.get(i).attr("abs:href").substring(84));
				subList.put("URL", "http://m.atmovies.com.tw/app2/attv.cfm?"+cURL[1].split("&")[0]+"&cid="+Alla.get(i).attr("abs:href").substring(84));//重組成APP網頁網址
			     System.out.println("MOD CHANNEL:"+Alla.get(i).attr("abs:href").substring(84));
				}else{
					subList.put("Channel",Alla.get(i).attr("abs:href").substring(84));//mod頻道號碼字數不同
					subList.put("URL","http://m.atmovies.com.tw/app2/attv.cfm?"+cURL[1].split("&")[0]+"&cid="+Alla.get(i).attr("abs:href").substring(84));//重組成APP網頁網址
				}
               subList.put("PID",String.valueOf(PID));
               subList.put("TVName", Alla.get(i).text());
               
			   mainToList.add(subList);
			}
		
	           }catch (Exception e) {
				// TODO 自動產生的 catch 區塊
				e.printStackTrace();
			} 
		 
	 }
	 
	 protected void saveType(){
		 SP=mParentActivity.getSharedPreferences("Type",0);
		 editor=SP.edit();
		 editor.clear();
		 editor.commit();//將檔案全部淨空
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
		 editor2.commit();//將檔案全部淨空
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
