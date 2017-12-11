package com.sandy.watchingTV;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import bean.AlertListBean;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.SimpleAdapter;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker; 
import android.widget.Toast;

public class LongClickPGAlarmList extends AlarmList implements OnItemLongClickListener  {
	ActionBarActivity mainActivity;
	AlertDialog dialog; //對話框物件 
	 private TextView TVName,channel,showTime,Program; //文字方塊物件
	 private NumberPicker setDate; //日期物件
	 private TimePicker setTime; //時間物件
	 private HashMap Message; //容器
	 private TextView TVSet;//標題
	 private TextView OPGSetting ;//原始設定
	 private String TVChannel,TV,HashID,isAlarm,PGDate,PGTime,
	 PGName,SsetTime,SsetDate;
	 int Hour;
	 int min;
	 AlertListBean listBean;
	 String avdanteDate[];
	 SharedPreferences settings ; //設定存入節目資訊XML
	 String SID;
	 SimpleAdapter simpleAdapter;
	 ListView AlarmList;
	public LongClickPGAlarmList(ActionBarActivity a,SimpleAdapter simpleAdapter,ListView AlarmList){
		mainActivity=a;	
	settings=mainActivity.getSharedPreferences("setAlarmText",0);
	this.simpleAdapter=simpleAdapter;
	this.AlarmList=AlarmList;
	}
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO 自動產生的方法 Stub
		listBean=new AlertListBean();//傳遞設定值
		TextView mainText ;//List內部文字
		LayoutInflater inflater =mainActivity.getLayoutInflater();
		AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
		View priview=inflater.inflate(R.layout.set_time_alert,null);
		TVName=(TextView) priview.findViewById(R.id.TVName);
		channel=(TextView)priview.findViewById(R.id.channel);
		showTime=(TextView)priview.findViewById(R.id.showTime);
		Program=(TextView)priview.findViewById(R.id.Program);
		setDate=(NumberPicker) priview.findViewById(R.id.setDate);
		setTime=(TimePicker) priview.findViewById(R.id.setTime);
		mainText=(TextView) arg1.findViewById(R.id.setTVTime);
		TVSet=(TextView) priview.findViewById(R.id.TVSet);
		OPGSetting=(TextView) priview.findViewById(R.id.OSetPG);
		int color=mainText.getTextColors().getDefaultColor();//獲得文字顏色
		System.out.println("Color defind:"+color);
		System.out.println(String.format("#%06X", color));
		builder.setView(priview);
		builder.setPositiveButton("設定", null);
		builder.setNegativeButton("取消", null);
		System.out.println("文字探針"+mainText.getText().toString().indexOf("(節目過時)"));
		if(color==-16777216){
		dialog=builder.create();
		dialog.show();
		Button Modify_button=dialog.getButton(Dialog.BUTTON_POSITIVE);
		ListView listView = (ListView) arg0;
		Message=(HashMap) listView.getItemAtPosition(arg2);//抓長按後的資料
		setDataBean(Message);//設定BEAN
		System.out.println("LongClickAlist"+Message.toString());
		avdanteDate=activeDate(PGDate);
		 
		 int maxValue=avdanteDate.length-1; //陣列大小
		 setDate.setMaxValue(maxValue);
		 setDate.setMinValue(0);
		 setDate.setDisplayedValues(activeDateWeek(PGDate));//可設定時間陣列
         setDate.setValue(maxValue);//設定setDate物件大小
		 TVName.setText(TV);//設定文字
		 channel.setText(TVChannel);//設定文字
		 showTime.setText("時間:"+PGDate+","+PGTime);//設定文字
		 Program.setText("節目名稱:"+PGName);//設定文字
		 OPGSetting.setText(Html.fromHtml("<font color=\"#ff1493\">原始設定:"+SsetDate+","+SsetTime.replace("-", ":")+"</font>"));
		//取得TimePicker 時間方法
			setTime.setOnTimeChangedListener(new OnTimeChangedListener() {
				
				@Override
				public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
					// TODO 自動產生的方法 Stub
					Hour=hourOfDay;
					min=minute;
					
					String SHour;
					String Smin;
					if(Hour<10){//十進位補零
						SHour="0"+String.valueOf(Hour);
					}else{
						SHour=String.valueOf(Hour);
					}
					if(min<10){
						Smin="0"+String.valueOf(min);
					}else{
						Smin=String.valueOf(min);
					}
					System.out.println("timeset:"+SHour+":"+Smin);
					listBean.setSTime(SHour+"-"+Smin);
				}
			});
			//設定日期參數
			setDate.setOnValueChangedListener(new OnValueChangeListener(){

				@Override
				public void onValueChange(NumberPicker picker, int oldVal,
						int newVal) {
					// TODO 自動產生的方法 Stub
					listBean.setSDate(avdanteDate[newVal]);
				}
				
			});
			System.out.println(setTime.getCurrentHour().toString());//列印預設時間
			listBean.setSTime(setTime.getCurrentHour().toString()+"-"+setTime.getCurrentMinute());
			listBean.setPGName("節目名稱:"+PGName);
			listBean.setPGTime("時間:"+PGDate+","+PGTime);
			listBean.setSDate(avdanteDate[setDate.getValue()]);
			listBean.setPDate(PGDate);
			listBean.setTVChannel("頻道數:"+TVChannel);
			listBean.setTVName(TV+","+TVChannel);
			System.out.println("listBean2:"+listBean.getSDate()+","+
					listBean.getSTime()+","+listBean.getPGName()+","+
							listBean.getPGTime()+","+listBean.getTVChannel()+","+
					listBean.getTVName()+","+listBean.getPDate());
			DialogOKButton dButton=new DialogOKButton(mainActivity,listBean,dialog,simpleAdapter,AlarmList);
			Modify_button.setOnClickListener(dButton);
		}else{
			Toast.makeText(mainActivity, "此節目已播過，故無法設定定時", Toast.LENGTH_LONG).show();
		}
		return false;
	}
	/**
	 * 
	 * @param PDate 節目撥出日期
	 * @return 可設定日期陣列
	 */
	private String[] activeDate(String PDate){
		String calendar[]=new String[8];
		DateFormat dateFormat = 
	            new SimpleDateFormat("yyyy-MM-dd");
		
		
		
		for(int i=0;i<8;i++){
			Calendar dateValue=turnStringToD(PDate);
			dateValue.add(Calendar.DATE, i+(-7));//節目撥出七天前可設定定時
			System.out.println("setTime is :"+dateFormat.format(dateValue.getTime()));
			calendar[i]=dateFormat.format(dateValue.getTime());	
		}
		return calendar;
	}
	/**
	 * 
	 * @param PDate 節目撥出日期
	 * @return 可設定日期陣列SHOW 星期
	 */
	private String[] activeDateWeek(String PDate){
		String calendar[]=new String[8];
		DateFormat dateFormat = 
	            new SimpleDateFormat("yyyy-MM-dd");
		
		
		
		for(int i=0;i<8;i++){
			Calendar dateValue=turnStringToD(PDate);
			dateValue.add(Calendar.DATE, i+(-7));//節目撥出七天前可設定定時
			String temp=dateFormat.format(dateValue.getTime());
			System.out.println("setTime is :"+temp+getWeek(temp));
			calendar[i]=temp+getWeek(temp);	
		}
		return calendar;
	}
	/**
	 * 
	 * @param date
	 * @return 那天星期幾
	 */
	protected static String getWeek(String date){
		Calendar  week=turnStringToD(date);
		 ///SimpleDateFormat date2DayFormat = new SimpleDateFormat( "u" );
		  
		 switch(week.get(Calendar.DAY_OF_WEEK) ){
		 case Calendar.MONDAY:
			 return "(一)";
		 case Calendar.TUESDAY:
			 return "(二)";
		 case Calendar.WEDNESDAY:
			 return "(三)";
		 case Calendar.THURSDAY:
			 return "(四)";
		 case Calendar.FRIDAY:
			 return "(五)";
		 case Calendar.SATURDAY:
			 return "(六)";
		 case Calendar.SUNDAY:
			 return "(日)";
		 default:
				 return "";
		 }
	}
	/**
	 * 字串轉換日期物件
	 * @param date 轉換字串
	 * @return 日期物件
	 */
	
	protected static Calendar turnStringToD(String date){
		String YMD[]=date.split("-");
		int year=Integer.valueOf(YMD[0]);
		int month=Integer.valueOf(YMD[1]);
		int day=Integer.valueOf(YMD[2]);
		Calendar c=new GregorianCalendar(year,month-1,day );
		return c;
	}
	protected void setDataBean(HashMap data){
		String TVinfo[]=data.get("TVInfo").toString().split(",");// TVInfo=CH23,緯來電影台,023152100:1,頻道數:CH23
		System.out.println("TVinfo"+data.get("TVInfo").toString());
		TVChannel=TVinfo[0];//CH23
		TV=TVinfo[1];
		String checkAlarm[]=data.get("checkAlarm").toString().split(",");//checkAlarm=023152100:1,頻道數:CH23
		HashID=checkAlarm[0].split(":")[0];//023152100
		isAlarm=checkAlarm[0].split(":")[1];//1
		String PGInfo[]=data.get("PGInfo").toString().split(",");//PGInfo=節目名稱:人在囧途,時間:2016-06-15,2100

		PGDate=PGInfo[1].split(":")[1];
		PGTime=PGInfo[2];
		PGName=PGInfo[0].split(":")[1];
		String setInfo[]=data.get("setInfo").toString().split(",");
		SsetTime=setInfo[1];
		SsetDate=setInfo[0];
		System.out.println("Bean2PutOut:"+TVChannel+","+TV+","+HashID+","
				+isAlarm+","+PGDate+","+PGTime+","+PGName+","+SsetTime
				+","+SsetDate);
	}

}
