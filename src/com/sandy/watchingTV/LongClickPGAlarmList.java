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
	AlertDialog dialog; //��ܮت��� 
	 private TextView TVName,channel,showTime,Program; //��r�������
	 private NumberPicker setDate; //�������
	 private TimePicker setTime; //�ɶ�����
	 private HashMap Message; //�e��
	 private TextView TVSet;//���D
	 private TextView OPGSetting ;//��l�]�w
	 private String TVChannel,TV,HashID,isAlarm,PGDate,PGTime,
	 PGName,SsetTime,SsetDate;
	 int Hour;
	 int min;
	 AlertListBean listBean;
	 String avdanteDate[];
	 SharedPreferences settings ; //�]�w�s�J�`�ظ�TXML
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
		// TODO �۰ʲ��ͪ���k Stub
		listBean=new AlertListBean();//�ǻ��]�w��
		TextView mainText ;//List������r
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
		int color=mainText.getTextColors().getDefaultColor();//��o��r�C��
		System.out.println("Color defind:"+color);
		System.out.println(String.format("#%06X", color));
		builder.setView(priview);
		builder.setPositiveButton("�]�w", null);
		builder.setNegativeButton("����", null);
		System.out.println("��r���w"+mainText.getText().toString().indexOf("(�`�عL��)"));
		if(color==-16777216){
		dialog=builder.create();
		dialog.show();
		Button Modify_button=dialog.getButton(Dialog.BUTTON_POSITIVE);
		ListView listView = (ListView) arg0;
		Message=(HashMap) listView.getItemAtPosition(arg2);//������᪺���
		setDataBean(Message);//�]�wBEAN
		System.out.println("LongClickAlist"+Message.toString());
		avdanteDate=activeDate(PGDate);
		 
		 int maxValue=avdanteDate.length-1; //�}�C�j�p
		 setDate.setMaxValue(maxValue);
		 setDate.setMinValue(0);
		 setDate.setDisplayedValues(activeDateWeek(PGDate));//�i�]�w�ɶ��}�C
         setDate.setValue(maxValue);//�]�wsetDate����j�p
		 TVName.setText(TV);//�]�w��r
		 channel.setText(TVChannel);//�]�w��r
		 showTime.setText("�ɶ�:"+PGDate+","+PGTime);//�]�w��r
		 Program.setText("�`�ئW��:"+PGName);//�]�w��r
		 OPGSetting.setText(Html.fromHtml("<font color=\"#ff1493\">��l�]�w:"+SsetDate+","+SsetTime.replace("-", ":")+"</font>"));
		//���oTimePicker �ɶ���k
			setTime.setOnTimeChangedListener(new OnTimeChangedListener() {
				
				@Override
				public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
					// TODO �۰ʲ��ͪ���k Stub
					Hour=hourOfDay;
					min=minute;
					
					String SHour;
					String Smin;
					if(Hour<10){//�Q�i��ɹs
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
			//�]�w����Ѽ�
			setDate.setOnValueChangedListener(new OnValueChangeListener(){

				@Override
				public void onValueChange(NumberPicker picker, int oldVal,
						int newVal) {
					// TODO �۰ʲ��ͪ���k Stub
					listBean.setSDate(avdanteDate[newVal]);
				}
				
			});
			System.out.println(setTime.getCurrentHour().toString());//�C�L�w�]�ɶ�
			listBean.setSTime(setTime.getCurrentHour().toString()+"-"+setTime.getCurrentMinute());
			listBean.setPGName("�`�ئW��:"+PGName);
			listBean.setPGTime("�ɶ�:"+PGDate+","+PGTime);
			listBean.setSDate(avdanteDate[setDate.getValue()]);
			listBean.setPDate(PGDate);
			listBean.setTVChannel("�W�D��:"+TVChannel);
			listBean.setTVName(TV+","+TVChannel);
			System.out.println("listBean2:"+listBean.getSDate()+","+
					listBean.getSTime()+","+listBean.getPGName()+","+
							listBean.getPGTime()+","+listBean.getTVChannel()+","+
					listBean.getTVName()+","+listBean.getPDate());
			DialogOKButton dButton=new DialogOKButton(mainActivity,listBean,dialog,simpleAdapter,AlarmList);
			Modify_button.setOnClickListener(dButton);
		}else{
			Toast.makeText(mainActivity, "���`�ؤw���L�A�G�L�k�]�w�w��", Toast.LENGTH_LONG).show();
		}
		return false;
	}
	/**
	 * 
	 * @param PDate �`�ؼ��X���
	 * @return �i�]�w����}�C
	 */
	private String[] activeDate(String PDate){
		String calendar[]=new String[8];
		DateFormat dateFormat = 
	            new SimpleDateFormat("yyyy-MM-dd");
		
		
		
		for(int i=0;i<8;i++){
			Calendar dateValue=turnStringToD(PDate);
			dateValue.add(Calendar.DATE, i+(-7));//�`�ؼ��X�C�ѫe�i�]�w�w��
			System.out.println("setTime is :"+dateFormat.format(dateValue.getTime()));
			calendar[i]=dateFormat.format(dateValue.getTime());	
		}
		return calendar;
	}
	/**
	 * 
	 * @param PDate �`�ؼ��X���
	 * @return �i�]�w����}�CSHOW �P��
	 */
	private String[] activeDateWeek(String PDate){
		String calendar[]=new String[8];
		DateFormat dateFormat = 
	            new SimpleDateFormat("yyyy-MM-dd");
		
		
		
		for(int i=0;i<8;i++){
			Calendar dateValue=turnStringToD(PDate);
			dateValue.add(Calendar.DATE, i+(-7));//�`�ؼ��X�C�ѫe�i�]�w�w��
			String temp=dateFormat.format(dateValue.getTime());
			System.out.println("setTime is :"+temp+getWeek(temp));
			calendar[i]=temp+getWeek(temp);	
		}
		return calendar;
	}
	/**
	 * 
	 * @param date
	 * @return ���ѬP���X
	 */
	protected static String getWeek(String date){
		Calendar  week=turnStringToD(date);
		 ///SimpleDateFormat date2DayFormat = new SimpleDateFormat( "u" );
		  
		 switch(week.get(Calendar.DAY_OF_WEEK) ){
		 case Calendar.MONDAY:
			 return "(�@)";
		 case Calendar.TUESDAY:
			 return "(�G)";
		 case Calendar.WEDNESDAY:
			 return "(�T)";
		 case Calendar.THURSDAY:
			 return "(�|)";
		 case Calendar.FRIDAY:
			 return "(��)";
		 case Calendar.SATURDAY:
			 return "(��)";
		 case Calendar.SUNDAY:
			 return "(��)";
		 default:
				 return "";
		 }
	}
	/**
	 * �r���ഫ�������
	 * @param date �ഫ�r��
	 * @return �������
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
		String TVinfo[]=data.get("TVInfo").toString().split(",");// TVInfo=CH23,�n�ӹq�v�x,023152100:1,�W�D��:CH23
		System.out.println("TVinfo"+data.get("TVInfo").toString());
		TVChannel=TVinfo[0];//CH23
		TV=TVinfo[1];
		String checkAlarm[]=data.get("checkAlarm").toString().split(",");//checkAlarm=023152100:1,�W�D��:CH23
		HashID=checkAlarm[0].split(":")[0];//023152100
		isAlarm=checkAlarm[0].split(":")[1];//1
		String PGInfo[]=data.get("PGInfo").toString().split(",");//PGInfo=�`�ئW��:�H�bʨ�~,�ɶ�:2016-06-15,2100

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
