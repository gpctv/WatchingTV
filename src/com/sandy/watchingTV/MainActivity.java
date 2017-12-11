package com.sandy.watchingTV;


import java.lang.reflect.Field;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import bean.Program;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.sandy.watchingTV.R;

import exception_custom.NoInternet;
import exception_custom.NoPGException;
import action.getProgram;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	 static int pos;
	private static Integer pid;
	private static String channelNo;
	private static String TVName;
	private static String TVURL;
	private NavigationDrawerFragment mNavigationDrawerFragment;
	private static ProgressBar progress;
	private static final int Version=Build.VERSION.SDK_INT; //android ����
	static TextView showTime;
	static ListView resultList;
	static Program pList[];
	static int addDayCount;
	AlertDialog search_day = null;
	NumberPicker searchTime;
	static String showDay[];
	static String showDayInCludeWeek[];
	static boolean isSearch;
	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	
	@Override
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
		    if (keyCode == KeyEvent.KEYCODE_BACK) {
				//Intent intent=new Intent(LVSpend.this,MainActivity.class);
				//startActivity(intent);
				android.os.Process.killProcess(android.os.Process.myPid());
		     
		   
		    }
		    return false;
		  }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();
		showDay=canFindDate();
		showDayInCludeWeek=canFindDateShow();
		isSearch=false;
		Toast toast=new Toast(MainActivity.this);
		addtoast(MainActivity.this,"��������ܸ`�ت�",Toast.LENGTH_LONG,toast);
		/* GOOGLE �s�i��T*/
		 AdView mAdView = (AdView) this.findViewById(R.id.adView);
	        AdRequest adRequest = new AdRequest.Builder().build();
	        mAdView.loadAd(adRequest);
	        /* GOOGLE �s�i��T*/ 
		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		try {
			if(Version>11){ //�j�� android 3.0���j��X�{
			 ViewConfiguration mconfig = ViewConfiguration.get(this);
			 Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			 System.out.println("menuKey:"+menuKeyField);
			 if(menuKeyField != null) {
				
			 menuKeyField.setAccessible(true);
			 menuKeyField.setBoolean(mconfig, false);
			 }else{
				 
			 }
			
			 }
			 } catch (Exception ex) {
				 ex.printStackTrace();
			 }
		System.out.println("MainActivity onCreate");
		 
	}
	@SuppressLint("NewApi")
	protected static void addtoast(Context context,String text,int duration,Toast toast){
		GradientDrawable background = new GradientDrawable();//background �e��
		ViewGroup toastView = new FrameLayout(context);// �ΨӸ�toastText���e��
		TextView textView=new TextView(context);//��r�e��
		background.setCornerRadius(50);// �]�w��w�T�����ꨤ�{��
		background.setColor(0xfff0f8ff);//�I���C��
		textView.setTextColor(0xffff69b4);//��r�C��
		textView.setText(text);//��r
		textView.setTextSize(25);//��r�j�p
		toastView.setPadding(30, 30, 30, 30);//�m��
		toast.setGravity(Gravity.TOP|Gravity.LEFT,100,0);
		toastView.addView(textView);
		toastView.setBackground(background);
		toast.setView(toastView);
		toast.setDuration(duration);
		toast.show();
	}

	@Override
	public void onNavigationDrawerItemSelected(HashMap<String,String> data) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager
				.beginTransaction()
				.replace(R.id.container,
						PlaceholderFragment.newInstance(data)).commit();
	}

	public void onSectionAttached(String TVName) {
		System.out.println("onSectionAttached:"+TVName);
		 
			mTitle = TVName;
		 
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		System.out.println("get ID2"+id);
		if (id == R.id.action_settings) {
			return true;
		}
		if(id==R.id.search_day){
			//������
			AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(this);
			LayoutInflater inflater =this.getLayoutInflater();
			View priview=inflater.inflate(R.layout.search_time,null);
			searchTime = (NumberPicker) priview.findViewById(R.id.numberPicker1);
			searchTime.setMaxValue(showDay.length-1);
			searchTime.setMinValue(0);
			searchTime.setDisplayedValues(showDayInCludeWeek);
			dialogBuilder.setView(priview);
			dialogBuilder.setPositiveButton("OK", null);
			dialogBuilder.setNegativeButton("Cancel", null);			
			search_day=dialogBuilder.create();
			search_day.show();
			Button button=search_day.getButton(Dialog.BUTTON_POSITIVE);
			button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO �۰ʲ��ͪ���k Stub
					System.out.println("getShowDay:"+showDay[searchTime.getValue()]);
					System.out.println("numberPicker value:"+searchTime.getValue());
					String day=showDay[searchTime.getValue()];
					addDayCount=searchTime.getValue();
					 Calendar c=turnStringToD(day);
					System.out.println(c.getTime());
					isSearch=true;
					
						
					try {
						System.out.println("day today:"+day +"  "+getToday(0));
						if(day.equals(getToday(0))){
							isSearch=false;
							showTime.setText("����"+day+getWeek(day));
							resultList.post(new Runnable() {

						        @Override
						        public void run() {
						        	
						        	 System.out.println("pos------"+pos);
						        	resultList.setSelectionFromTop(pos, 12);
						        	 
						        }
						    });
						}else{
							showTime.setText(day+getWeek(day));
						}
						
						getProgram programAction=new getProgram(c, progress,pid);
						
						pList=programAction.execute(TVURL).get();
						PlaceholderFragment.getPGBean(pList);
					} catch (InterruptedException e) {
						// TODO �۰ʲ��ͪ� catch �϶�
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO �۰ʲ��ͪ� catch �϶�
						e.printStackTrace();
					}catch(NullPointerException e){
						Toast.makeText(MainActivity.this,"�п�ܹq���x",Toast.LENGTH_SHORT).show();
						
					} catch (NoPGException e) {
						// TODO �۰ʲ��ͪ� catch �϶�
						Toast.makeText(MainActivity.this, "�L���`�ت�", Toast.LENGTH_LONG).show();
						showTime.setText("�L���`�ت�");
						resultList.setAdapter(null);
					}catch(NoInternet e){
						Toast.makeText(MainActivity.this, "�������_�Φ��A�����`", Toast.LENGTH_LONG).show();
						showTime.setText("�������_�Φ��A�����`");
					}catch(Exception e){
						showTime.setText("error");
					}
					
					search_day.dismiss();
					
				}
			});
			
		}
		if(id==R.id.alarmList){
			System.out.println("���AlarmList Class");
			Intent intent=new Intent();
			intent.setClass(MainActivity.this,AlarmList.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
	protected static String[] canFindDate(){
		Calendar addDay=Calendar.getInstance();
		Calendar forFindDate[]=new Calendar[8];
		String calendar[]=new String[8];
		DateFormat dateFormat = 
	            new SimpleDateFormat("yyyy-MM-dd");
		for(int i=0;i<=7;i++){
			forFindDate[i]=Calendar.getInstance();
			forFindDate[i].add(addDay.DATE, i);
			System.out.println("showDay String create"+dateFormat.format(forFindDate[i].getTime()));
			calendar[i]=dateFormat.format(forFindDate[i].getTime());
		}
		return calendar;
	}
	protected static String[] canFindDateShow( ){
		Calendar addDay=Calendar.getInstance();
		Calendar forFindDate[]=new Calendar[8];
		String calendar[]=new String[8];
		DateFormat dateFormat = 
	            new SimpleDateFormat("yyyy-MM-dd");
		for(int i=0;i<=7;i++){
			forFindDate[i]=Calendar.getInstance();
			forFindDate[i].add(addDay.DATE, i);
			System.out.println("showDay String create"+dateFormat.format(forFindDate[i].getTime()));
			calendar[i]=dateFormat.format(forFindDate[i].getTime())+getWeek(dateFormat.format(forFindDate[i].getTime()));
		}
		return calendar;
	}
	protected static Calendar turnStringToD(String date){
		String YMD[]=date.split("-");
		int year=Integer.valueOf(YMD[0]);
		int month=Integer.valueOf(YMD[1]);
		int day=Integer.valueOf(YMD[2]);
		Calendar c=new GregorianCalendar(year,month-1,day );
		return c;
	}
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
	/*
	 * @ i 1��@�� 0���� -1�e�@��
	 */
	protected static String getToday(int i){
		  Calendar today=Calendar.getInstance();
		  Calendar beDay=Calendar.getInstance();
		  Calendar afDay=Calendar.getInstance();
		SimpleDateFormat formate=new SimpleDateFormat("yyyy-MM-dd");
		beDay.add(Calendar.DATE, -1);
		afDay.add(Calendar.DATE, 1);
		String bDay=formate.format(beDay.getTime()); //�e�@��
		String aDay=formate.format(afDay.getTime());//��@��
		String stoday=formate.format(today.getTime());//����
		if(i==0){
			return stoday;
		}else if(i==1){
			return aDay;
		}else if(i==-1){
			return bDay;
		}
		return "0";
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		 
		static Calendar today;
		static Button bNextDay;
		static Button bToday;
		static ArrayList<HashMap<String, String>> pgList=new ArrayList<HashMap<String, String>>();
		static SimpleAdapter listAdapter;
		static String Stoday;
		static String Nowtime;
		static Long todayHashCode;
		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(HashMap<String,String> data) {
			System.out.println("sectionNumber"+data);
			addDayCount=0;
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, 0);
			fragment.setArguments(args);
			 today=Calendar.getInstance();
			
			pid=Integer.valueOf(data.get("PID"));
			channelNo=data.get("ChannelNo");
			TVName=data.get("listName");
			if(TVName.equals("�Ȭw�ȹC�x")){
				TVURL="http://m.atmovies.com.tw/app2/attv.cfm?action=channeltime&cid=CHO31";
			}else if(TVName.equals("���q���s�D�x")){
				TVURL="http://m.atmovies.com.tw/app2/attv.cfm?action=channeltime&cid=CHP45";
		     }else if(TVName.equals("MOMO�ˤl�x")){
		    	 TVURL="http://m.atmovies.com.tw/app2/attv.cfm?action=channeltime&cid=CHP01"; 
		     }else if(TVName.equals("FOX SPORTS 3 HD")){
		    	TVURL="http://m.atmovies.com.tw/app2/attv.cfm?action=channeltime&cid=CHM178"; 
		     }else{
			TVURL=data.get("URL");
			}
			System.out.println("ChannelNo and Pid:"+pid+","+channelNo);
			 return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			isSearch=false;
			showTime=(TextView) rootView.findViewById(R.id.tvShowTime);
			progress=(ProgressBar)rootView.findViewById(R.id.pbContainer);
			 resultList=(ListView) rootView.findViewById(R.id.pgList1);
			 bNextDay=(Button) rootView.findViewById(R.id.btNextDay);
			 bToday=(Button) rootView.findViewById(R.id.btHome);
			 
			 getProgram programAction=new getProgram(today, progress,pid);
			 System.out.println("section label creat");
			 DateFormat dateFormat = 
			            new SimpleDateFormat("yyyy-MM-dd");
			  
			 Nowtime=String.valueOf(today.get(today.HOUR_OF_DAY))+String.valueOf(today.get(today.MINUTE));
			 Stoday=dateFormat.format(today.getTime());
			 showTime.setText("���� "+Stoday+getWeek(Stoday));
			 //2016/05/14�s�W
			 LongClickProgram setTimeDialog=new LongClickProgram(getActivity());
			//����getProgram action
			try {
				listAdapter=new SimpleAdapter(getActivity(),pgList,R.layout.pg_list,new String[]{"PTime","PName"},new int[]{R.id.setTVTime,R.id.setTVName}){
					@Override
					public View getView(int position, View convertView,
							ViewGroup parent) {
						// TODO �۰ʲ��ͪ���k Stub
						
						long code=0;
						View context=super.getView(position, convertView, parent);
						
						HashMap<String,String>  listMap=(HashMap<String,String>)getItem(position);
						TextView mainText ;
						TextView subText;
						subText=(TextView) context.findViewById(R.id.setTVName);
						mainText=(TextView) context.findViewById(R.id.setTVTime);
						System.out.println("hc:"+listMap.get("hashCode"));
						System.out.println("name"+listMap.get("PName"));
						System.out.println("PTime"+listMap.get("PTime"));
						System.out.println("todayHashCode"+todayHashCode);
						System.out.println("date:"+listMap.get("PDate"));
						System.out.println("isSearch"+isSearch);
						
						if(!isSearch){//�p�G�O�j�M�D���Ѹ`�ت��A�C�⤣���u��������
						if(todayHashCode>Long.valueOf(listMap.get("hashCode"))){//�{�b�ɶ��j��ثe�`�خɶ�
							 
							 
							mainText.setTextColor(0xffBEBEBE);//�Ǧ� �N��t��
							subText.setTextColor(0xffBEBEBE);
							if(position<pgList.size()-1){//�קK����
								HashMap<String,String>  tempMap=(HashMap<String,String>)getItem(position+1);
								
								code= Long.valueOf(tempMap.get("hashCode"));//�᭱HashCode
								if(todayHashCode<code){//�᭱HashCode�j��{�b�`��HASHCODE�N��{�b����
									 
									mainText.setTextColor(0xffff1493);//������
									subText.setTextColor(0xffff1493);
									System.out.println("resultList position:"+position);
								    
								}
							}
						}else{
						
						//�٨S����
						subText.setTextColor(0xffff69b4);//����
						mainText.setTextColor(0xffff69b4);
						}
						}else{
							subText.setTextColor(0xffff69b4);//�D����`�د���
							mainText.setTextColor(0xffff69b4);
						
						}
						
						return context;
					}
				 
				};
			 	
				 
				
				pList=programAction.execute(TVURL).get();
				
				getPGBean(pList);
				resultList.setAdapter(listAdapter);
				listAdapter.notifyDataSetChanged();
				resultList.post(new Runnable() {

			        @Override
			        public void run() {
			        	
			        	
			        	 pos=getNowPlay();
			        	 System.out.println("pos------"+pos);
			        	resultList.setSelectionFromTop(pos, 12);
			        	 
			        }
			    });
			} catch (InterruptedException e) {
				// TODO �۰ʲ��ͪ� catch �϶�
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO �۰ʲ��ͪ� catch �϶�
				e.printStackTrace();
			} catch (NoPGException e) {
				// TODO �۰ʲ��ͪ� catch �϶�
				Toast.makeText(getActivity(), "�L���`�ت�", Toast.LENGTH_LONG).show();
				showTime.setText("�L���`�ت�");
				resultList.setAdapter(listAdapter);
			}catch(NoInternet e){
				Toast.makeText(getActivity(), "�������_�Φ��A�����`", Toast.LENGTH_LONG).show();
				showTime.setText("�������_�Φ��A�����`");
			}catch(Exception e){
				showTime.setText("error");
			}
			bNextDay.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO �۰ʲ��ͪ���k Stub
					isSearch=true;
					try {
					addDayCount++;
					if(addDayCount>7){
						addDayCount=7;
						Toast.makeText(getActivity(),"�u���\�d�ߤC�Ѥ��`��",Toast.LENGTH_SHORT).show();
					}
					String day=showDay[addDayCount];//�ϥβ{�����
					Calendar c=turnStringToD(day);
					getProgram programAction=new getProgram(c, progress,pid);
					
						pList=programAction.execute(TVURL).get();//���楿�TURL+���
						getPGBean(pList);//��LIST ��notifyDataSetChanged adapter
						showTime.setText(day+getWeek(day));
					} catch (InterruptedException e) {
						// TODO �۰ʲ��ͪ� catch �϶�
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO �۰ʲ��ͪ� catch �϶�
						e.printStackTrace();
					} catch (NoPGException e) {
						// TODO �۰ʲ��ͪ� catch �϶�
						Toast.makeText(getActivity(), "�L���`�ت�", Toast.LENGTH_LONG).show();
						showTime.setText("�L���`�ت�");
						resultList.setAdapter(listAdapter);
					}catch(NoInternet e){
						Toast.makeText(getActivity(), "�������_�Φ��A�����`", Toast.LENGTH_LONG).show();
						showTime.setText("�������_�Φ��A�����`");
					}catch(Exception e){
						showTime.setText("error");
					}
					
					System.out.println(addDayCount);
				}
			});
			bToday.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO �۰ʲ��ͪ���k Stub
					getProgram programAction=new getProgram(today, progress,pid);
					try {
						isSearch=false;
						pList=programAction.execute(TVURL).get();
						getPGBean(pList);//��LIST ��notifyDataSetChanged adapter
						showTime.setText("���� "+Stoday+getWeek(Stoday));
						resultList.post(new Runnable() {

					        @Override
					        public void run() {
					        	
					        	 System.out.println("pos------"+pos);
					        	resultList.setSelectionFromTop(pos, 12);
					        	 
					        }
					    });
						
						addDayCount=0;//�p�ƾ�
					} catch (InterruptedException e) {
						// TODO �۰ʲ��ͪ� catch �϶�
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO �۰ʲ��ͪ� catch �϶�
						e.printStackTrace();
					} catch (NoPGException e) {
						// TODO �۰ʲ��ͪ� catch �϶�
						Toast.makeText(getActivity(), "�L���`�ت�", Toast.LENGTH_LONG).show();
						showTime.setText("�L���`�ت�");
						resultList.setAdapter(listAdapter);
					}catch(NoInternet e){
						Toast.makeText(getActivity(), "�������_�Φ��A�����`", Toast.LENGTH_LONG).show();
						showTime.setText("�������_�Φ��A�����`");
					}catch(Exception e){
						showTime.setText("error");
					}
					System.out.println(today);
				}
			});
			resultList.setOnItemLongClickListener(setTimeDialog);
			 return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			System.out.println("ARG_SECTION_NUMBER"+getArguments().getInt(
					ARG_SECTION_NUMBER));
			((MainActivity) activity).onSectionAttached(TVName);
		}
		protected static void getPGBean(Program[] pgBean) throws Exception {
			pgList.clear();
			boolean b=false;//�Ĥ@������
			  boolean b2=false;//�ĤG������
			  todayHashCode=Date.valueOf(getToday(0)).getTime()+Long.valueOf(getTimeNow());
				if(pgBean==null){
					System.out.println("internet intrupate");
					throw new NoInternet("�������_�Φ��A�����`");
				}
			  
			  if(pgBean.length==0){
				  System.out.println("bena size:"+pgBean.length);
				  throw new NoPGException("�L���`�ت�");
				  
				   
			  }
			  System.out.println("pgBean day:"+pgBean[0].getPDate());
			  System.out.println("Today day:"+getToday(0));
			if(!pgBean[0].getPDate().equals(getToday(0))){//�D����`��
				for(int i=0;i<pgBean.length;i++){
					HashMap<String,String> list=new HashMap<String,String>();
					 
					list.put("PTime", getTime(pgBean[i].getPTime()));
					list.put("PName", pgBean[i].getPName());
					list.put("PDate", pgBean[i].getPDate());
					list.put("hashCode", "0");
					list.put("TVPGTime", channelNo.toString()+pgBean[i].getPDate()+getTime(pgBean[i].getPTime()));
				     list.put("TVNameChannel", TVName+","+channelNo);
				      
					pgList.add(list);
				}
			}else{
			for(int i=0;i<pgBean.length;i++){
				HashMap<String,String> list=new HashMap<String,String>();
				  if(!b){//��������
					  if(Integer.valueOf(getTime(pgBean[i+1].getPTime()))>Integer.valueOf(getTime(pgBean[i].getPTime()))){//�U�@�Ӹ`�خɶ��j��{�b�`�خɶ��]���{�b���
						  //�@��
						  b=true;//���� 
						  String hashCode=String.valueOf( Date.valueOf(getToday(0)).getTime()+Long.valueOf(getTime(pgBean[i].getPTime())));
						  list.put("PTime",getTime(pgBean[i].getPTime()));
						  list.put("PName", pgBean[i].getPName());
						  list.put("PDate",getToday(0));//�������
						  list.put("hashCode", hashCode);
						  list.put("TVPGTime", channelNo.toString()+getToday(0)+getTime(pgBean[i].getPTime()));
						  list.put("TVNameChannel", TVName+","+channelNo);
						  System.out.println("PTime:"+","+getTime(pgBean[i].getPTime()));
					      System.out.println("PDate:"+","+getToday(0));
					       System.out.println("hashCode"+hashCode);
					  }else{//������e�@��
						  String hashCode=String.valueOf( Date.valueOf(getToday(-1)).getTime()+Long.valueOf(getTime(pgBean[i].getPTime()) ));
						  list.put("PTime",getTime(pgBean[i].getPTime()));
						  list.put("PName", pgBean[i].getPName());
						  list.put("PDate",getToday(-1));//����e�@��
						  list.put("hashCode",hashCode );
						  list.put("TVPGTime", channelNo.toString()+getToday(-1)+getTime(pgBean[i].getPTime()));
						  list.put("TVNameChannel", TVName+","+channelNo);  
						  System.out.println("PTime:"+","+getTime(pgBean[i].getPTime()));
					      System.out.println("PDate:"+","+getToday(-1));
					       
					  }
				  }else{
					  if(!b2){//�������ѤF
						  if(i<pgBean.length-1){
						  if(Integer.valueOf(getTime(pgBean[i+1].getPTime()))>Integer.valueOf(getTime(pgBean[i].getPTime()))){//�U�@�Ӹ`�خɶ��j��{�b�`�خɶ��A���N�٬O����
							  String hashCode=String.valueOf( Date.valueOf(getToday(0)).getTime()+Long.valueOf(getTime(pgBean[i].getPTime()) ));
							  list.put("PTime",getTime(pgBean[i].getPTime()));
							  list.put("PName", pgBean[i].getPName());
							  list.put("PDate",getToday(0));//�������
							  list.put("hashCode", hashCode);
							  list.put("TVPGTime", channelNo.toString()+getToday(0)+getTime(pgBean[i].getPTime()));
							  list.put("TVNameChannel", TVName+","+channelNo);
						      System.out.println("PTime:"+","+getTime(pgBean[i].getPTime()));
						      System.out.println("PDate:"+","+getToday(0));
						       System.out.println("hashCode"+hashCode);
						  }else{//99�j��`�خɶ��G�S����
							  //1��
							  b2=true;//�A����
							  String hashCode=String.valueOf( Date.valueOf(getToday(0)).getTime()+Long.valueOf(getTime(pgBean[i].getPTime())));
							  list.put("PTime",getTime(pgBean[i].getPTime()));
							  list.put("PName", pgBean[i].getPName());
							  list.put("PDate",getToday(0));//�������
							  list.put("hashCode", hashCode);
							  list.put("TVPGTime", channelNo.toString()+getToday(0)+getTime(pgBean[i].getPTime()));
							  list.put("TVNameChannel", TVName+","+channelNo);
						      System.out.println("PTime:"+","+getTime(pgBean[i].getPTime()));
						      System.out.println("PDate:"+","+getToday(0));
						       System.out.println("hashCode"+hashCode);
						  }
						  }else{
							  String hashCode=String.valueOf( Date.valueOf(getToday(0)).getTime()+Long.valueOf(getTime(pgBean[i].getPTime()) ));
							  list.put("PTime",getTime(pgBean[i].getPTime()));
							  list.put("PName", pgBean[i].getPName());
							  list.put("PDate",getToday(0));//�������
							  list.put("hashCode", hashCode);
							  list.put("TVPGTime", channelNo.toString()+getToday(0)+getTime(pgBean[i].getPTime()));
							  list.put("TVNameChannel", TVName+","+channelNo);
						      System.out.println("PTime:"+","+getTime(pgBean[i].getPTime()));
						      System.out.println("PDate:"+","+getToday(0));
						       System.out.println("hashCode"+hashCode);
						  }
						  
					  }else{//�p�G����N�]����@��
						  String hashCode=String.valueOf( Date.valueOf(getToday(1)).getTime()+Long.valueOf(getTime(pgBean[i].getPTime()) ));
						  list.put("PTime",getTime(pgBean[i].getPTime()));
						  list.put("PName", pgBean[i].getPName());
						  list.put("PDate",getToday(1));//�����@��
						  list.put("hashCode", hashCode);
						  list.put("TVPGTime", channelNo.toString()+getToday(1)+getTime(pgBean[i].getPTime()));
						  list.put("TVNameChannel", TVName+","+channelNo);
					      System.out.println("PTime:"+","+getTime(pgBean[i].getPTime()));
					      System.out.println("PDate:"+","+getToday(1));
					       System.out.println("hashCode"+hashCode);
					  }
					  }
			
				  pgList.add(list);
				  }
			}
			
			
			listAdapter.notifyDataSetChanged();
			
		}
	
	protected static int getNowPlay(){
		//��{�b����
		HashMap<String,String> value2=new HashMap<String,String>(); //�s��@��ITEM
		Long todayHashCode=Date.valueOf(getToday(0)).getTime()+Long.valueOf(getTimeNow());
		int position=0;
		for(int i=0;i<pgList.size();i++){
			HashMap<String,String> value=new HashMap<String,String>();//�s����ITEM
			value=pgList.get(i);
			if(todayHashCode>Long.valueOf(value.get("hashCode"))){//�{�b�ɶ���ITEM�j
				if(i<pgList.size()-1){//�קK����
					value2=pgList.get(i+1);
					 
					if(todayHashCode<Long.valueOf(value2.get("hashCode"))){//�᭱�ɶ���ITEM�p
					System.out.println("return now play"+i);
						position= i;//��ITEM �N�O�{�b����ɶ�
					
				}
				}
				 
			}
			 
		}
		return position;
		}
		/*
		 * @ i 1��@�� 0���� -1�e�@��
		 */
		protected static String getToday(int i){
			  Calendar today=Calendar.getInstance();
			  Calendar beDay=Calendar.getInstance();
			  Calendar afDay=Calendar.getInstance();
			SimpleDateFormat formate=new SimpleDateFormat("yyyy-MM-dd");
			beDay.add(Calendar.DATE, -1);
			afDay.add(Calendar.DATE, 1);
			String bDay=formate.format(beDay.getTime()); //�e�@��
			String aDay=formate.format(afDay.getTime());//��@��
			String stoday=formate.format(today.getTime());//����
			if(i==0){
				return stoday;
			}else if(i==1){
				return aDay;
			}else if(i==-1){
				return bDay;
			}
			return "0";
		}
		
		protected static String getTimeNow(){
			SimpleDateFormat formateTime=new SimpleDateFormat("HHmm");
			String nowTime=formateTime.format(today.getTime());
			return nowTime;
		}
		protected static String getTime(String time){
			String t[]=time.split(":");
			return t[0]+t[1];
		}
	
		 
	}
	
}
