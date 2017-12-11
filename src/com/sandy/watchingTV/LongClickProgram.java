package com.sandy.watchingTV;
 
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import bean.AlertListBean;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;
import android.widget.AdapterView.*;
public class LongClickProgram implements OnItemLongClickListener {
	 AlertDialog dialog; //��ܮت���
	 FragmentActivity mainActivity; //�D�{������
	 private TextView TVName,channel,showTime,Program; //��r�������
	 private NumberPicker setDate; //�������
	 private TimePicker setTime; //�ɶ�����
	 private HashMap Message; //�e��
	 private TextView TVSet;//���D
	 int Hour;
	 int min;
	 AlertListBean listBean;
	 String avdanteDate[];
	 SharedPreferences settings ; //�]�w�s�J�`�ظ�TXML
	 String SID,PGDate,PGTime,STime,SDate,PGName,TVChannel,STVName;
	 public LongClickProgram(FragmentActivity  FA) {
		// TODO �۰ʲ��ͪ��غc�l Stub
		 mainActivity=FA;
		 settings=FA.getSharedPreferences("setAlarmText",0);
		 
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
		int color=mainText.getTextColors().getDefaultColor();//��o��r�C��
		int XMLSize=settings.getAll().size();
		System.out.println("Color defind:"+color);
		System.out.println(String.format("#%06X", color));
		
		builder.setView(priview);
		builder.setPositiveButton("�]�w", null);
		builder.setNegativeButton("����", null);
		
		if(color==-38476 && XMLSize<10 ){//�C�⬰������N���}DIALOG
		dialog=builder.create();
		dialog.show();
		System.out.println("XML Size:"+settings.getAll().size());//�O�o����x���ӼơC
		Button Modify_button=dialog.getButton(Dialog.BUTTON_POSITIVE);
		ListView listView = (ListView) arg0;
		Message=(HashMap) listView.getItemAtPosition(arg2);
		avdanteDate=activeDate(Message.get("PDate").toString());
		System.out.println("Errortest:"+Message.get("PDate").toString());
		int maxValue=avdanteDate.length-1; //�}�C�j�p
		setDate.setMaxValue(maxValue);
		setDate.setMinValue(0);
		setDate.setDisplayedValues(activeDateWeek(Message.get("PDate").toString()));//�i�]�w�ɶ��}�C
		setDate.setValue(maxValue);//�]�wsetDate����j�p
		
		 TVName.setText((CharSequence) Message.get("TVNameChannel"));//�]�w��r
		 channel.setText("�W�D��:"+Message.get("TVNameChannel").toString().split(",")[1]);//�]�w��r
		 showTime.setText("�ɶ�:"+(CharSequence) Message.get("PDate")+","+(CharSequence) Message.get("PTime"));//�]�w��r
		 Program.setText("�`�ئW��:"+(CharSequence) Message.get("PName"));//�]�w��r
		System.out.println("PDate--"+Message.get("PDate")); 
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
				//System.out.println(avdanteDate[newVal]);
			}
			
		});
		setString();
		System.out.println(setTime.getCurrentHour().toString());//�C�L�w�]�ɶ�
		listBean.setSTime(STime);
		listBean.setPGName("�`�ئW��:"+PGName);
		listBean.setPGTime("�ɶ�:"+PGDate+","+PGTime);
		listBean.setSDate(SDate);
		listBean.setPDate(PGDate);
		listBean.setTVChannel("�W�D��:"+TVChannel);
		listBean.setTVName(STVName+","+TVChannel);
		System.out.println("listBean1:"+listBean.getSDate()+","+
		listBean.getSTime()+","+listBean.getPGName()+","+
				listBean.getPGTime()+","+listBean.getTVChannel()+","+
		listBean.getTVName()+","+listBean.getPDate());
		String SID=getSID();
		if(settings.contains(SID)){//�w�g����SID�C�����u��藍�s�W
	
		TVSet.setText(Html.fromHtml("�`�ؼ��X�e����<font color=\"#ff1493\">(���`�ؤw�]�w�x�a�A���i��ɶ�)</font>"));
		Toast.makeText(mainActivity, "���`�ؤw�]�w�Ƶ{�A���i�b�����ɶ�",  Toast.LENGTH_LONG).show();
		
		}
		DialogOKButton dButton=new DialogOKButton(mainActivity,listBean,dialog);
		Modify_button.setOnClickListener(dButton);
		
		}else{
			if (XMLSize >= 10) {
				Toast.makeText(mainActivity, "���F�z���_�Q�Ŷ��A�x���Фų]�w�W�L�Q��",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(mainActivity, "���`�ؤw���L�A�G�L�k�]�w�w��",
						Toast.LENGTH_LONG).show();
			}
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
private  String getSID(){
	String date[]=listBean.getSDate().split("-");//�]�w������Τ��
	String time[]=listBean.getSTime().split("-");//�]�w�ɶ��ɤ�
	String tempPgTime[]=listBean.getPGTime().split(",");//��`�ؼ��X�ɶ���,��
	String tempPgDate[]=tempPgTime[0].split(":");//��`�ؼ��X���
	String tempPgDate2[]=tempPgDate[1].split("-");//�`�ؼ��X����~���
	// int PgTime=Integer.valueOf(tempPgTime[1]);//�`�ؼ��X�ɶ�
	// int PgDate=Integer.valueOf(tempPgDate2[2]);//�`�ؼ��X���
	String ch=listBean.getTVChannel();
	if(ch.indexOf("CHP")>-1){
		SID=listBean.getTVChannel().replace("�W�D��:CHP", "1")//�]�wID �W�D+�`�ؤ��+�ɶ�
				+tempPgDate2[2]+
				tempPgTime[1];
	}else if(ch.indexOf("CHO")>-1){
		SID=listBean.getTVChannel().replace("�W�D��:CHO", "1")//�]�wID �W�D+�`�ؤ��+�ɶ�
				+tempPgDate2[2]+
				tempPgTime[1];
	}else if(ch.indexOf("CHM")>-1){
		SID=listBean.getTVChannel().replace("�W�D��:CHM", "1")//�]�wID �W�D+�`�ؤ��+�ɶ�
				+tempPgDate2[2]+
				tempPgTime[1];
	}else if(ch.indexOf("CHD")>-1){
		SID=listBean.getTVChannel().replace("�W�D��:CHD", "1")//�]�wID �W�D+�`�ؤ��+�ɶ�
				+tempPgDate2[2]+
				tempPgTime[1];
	}else{
	SID=listBean.getTVChannel().replace("�W�D��:CH", "0")//�]�wID �W�D+�`�ؤ��+�ɶ�
			+tempPgDate2[2]+
			tempPgTime[1];
	}
	System.out.println("longClickGetID:"+SID);
	return SID;
}
private void setString(){
	STime=setTime.getCurrentHour().toString()+"-"+setTime.getCurrentMinute();
	SDate=avdanteDate[setDate.getValue()];
	PGName=Message.get("PName").toString();
	PGTime=Message.get("PTime").toString();
	PGDate=Message.get("PDate").toString();
	TVChannel=Message.get("TVNameChannel").toString().split(",")[1];
	STVName=Message.get("TVNameChannel").toString().split(",")[0];
	System.out.println("outPutString1:"+STime+","+
	SDate+","+PGName+","+PGTime+","+PGDate+","+
			TVChannel+","+STVName);
}
	
}
