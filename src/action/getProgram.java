package action;

import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import bean.Program;
import android.app.Fragment;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class getProgram  extends AsyncTask<String, Integer, Program[]> {
	 
     URL url;
    Program Oprogram[];
    String PGURL;
    DateFormat dateFormat;
    Document doc;
    String sdate;
    ProgressBar pb;
    
    int ppid;
	public getProgram( Calendar date,ProgressBar pb ,int pid){
		//example:http://m.atmovies.com.tw/app2/attv.cfm?action=channelTime&CID=CH56&TDay=2016-02-13
	 
		dateFormat = 
	            new SimpleDateFormat("yyyy-MM-dd");
		this.pb=pb;
		sdate=dateFormat.format(date.getTime());
		ppid=pid;
	}
	@Override
	protected void onPreExecute() {
		// TODO 自動產生的方法 Stub
		pb.setVisibility(View.VISIBLE);
		super.onPreExecute();
	}
	@Override
	protected Program[] doInBackground(String... arg0){
		// TODO 自動產生的方法 Stub
		PGURL=arg0[0];//獲取頻道節目LIST URL
		String cURL=PGURL+"&TDay="+sdate; //加上日期
		System.out.println(cURL);
		try {
			url=new URL(cURL);
			doc=Jsoup.parse(url,7000);
			Elements getAllTime=doc.select("font[class=pTime]");
			Elements getPTitle=doc.select("font[class=pTitle]");
			Oprogram=new Program[getAllTime.size()];
			for(int i=0;i<getAllTime.size();i++){
				System.out.println(getAllTime.get(i).text());
				System.out.println(getPTitle.get(i).text());
				Oprogram[i]=new Program();
				Oprogram[i].setPDate(sdate);
				Oprogram[i].setPID(ppid);
				Oprogram[i].setPName(getPTitle.get(i).text());
				Oprogram[i].setPTime(getAllTime.get(i).text());
			}
			return Oprogram;
		}catch( SocketTimeoutException e){
			System.out.println("Internet interupt");
			 
			
		} catch (Exception e) {
			// TODO 自動產生的 catch 區塊
			
			 
			e.printStackTrace();
			 
		}
		return Oprogram;
	}
	@Override
	protected void onProgressUpdate(Integer... values) {
		// TODO 自動產生的方法 Stub
		pb.setVisibility(values[0]);
		super.onProgressUpdate(values);
	}
	
	@Override
	protected void onPostExecute(Program result[]) {
		// TODO 自動產生的方法 Stub
		 
		pb.setVisibility(View.INVISIBLE);
		
		super.onPostExecute(result);
	}

}
