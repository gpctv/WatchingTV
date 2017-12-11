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
	private static final int Version=Build.VERSION.SDK_INT; //android 版本
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
		addtoast(MainActivity.this,"←按此顯示節目表",Toast.LENGTH_LONG,toast);
		/* GOOGLE 廣告橫幅*/
		 AdView mAdView = (AdView) this.findViewById(R.id.adView);
	        AdRequest adRequest = new AdRequest.Builder().build();
	        mAdView.loadAd(adRequest);
	        /* GOOGLE 廣告橫幅*/ 
		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		try {
			if(Version>11){ //大於 android 3.0版強制出現
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
		GradientDrawable background = new GradientDrawable();//background 容器
		ViewGroup toastView = new FrameLayout(context);// 用來裝toastText的容器
		TextView textView=new TextView(context);//文字容器
		background.setCornerRadius(50);// 設定氣泡訊息的圓角程度
		background.setColor(0xfff0f8ff);//背景顏色
		textView.setTextColor(0xffff69b4);//文字顏色
		textView.setText(text);//文字
		textView.setTextSize(25);//文字大小
		toastView.setPadding(30, 30, 30, 30);//置中
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
			//日期選擇
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
					// TODO 自動產生的方法 Stub
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
							showTime.setText("今日"+day+getWeek(day));
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
						// TODO 自動產生的 catch 區塊
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO 自動產生的 catch 區塊
						e.printStackTrace();
					}catch(NullPointerException e){
						Toast.makeText(MainActivity.this,"請選擇電視台",Toast.LENGTH_SHORT).show();
						
					} catch (NoPGException e) {
						// TODO 自動產生的 catch 區塊
						Toast.makeText(MainActivity.this, "無此節目表", Toast.LENGTH_LONG).show();
						showTime.setText("無此節目表");
						resultList.setAdapter(null);
					}catch(NoInternet e){
						Toast.makeText(MainActivity.this, "網路中斷或伺服器異常", Toast.LENGTH_LONG).show();
						showTime.setText("網路中斷或伺服器異常");
					}catch(Exception e){
						showTime.setText("error");
					}
					
					search_day.dismiss();
					
				}
			});
			
		}
		if(id==R.id.alarmList){
			System.out.println("轉跳AlarmList Class");
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
	/*
	 * @ i 1後一天 0今天 -1前一天
	 */
	protected static String getToday(int i){
		  Calendar today=Calendar.getInstance();
		  Calendar beDay=Calendar.getInstance();
		  Calendar afDay=Calendar.getInstance();
		SimpleDateFormat formate=new SimpleDateFormat("yyyy-MM-dd");
		beDay.add(Calendar.DATE, -1);
		afDay.add(Calendar.DATE, 1);
		String bDay=formate.format(beDay.getTime()); //前一天
		String aDay=formate.format(afDay.getTime());//後一天
		String stoday=formate.format(today.getTime());//今天
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
			if(TVName.equals("亞洲旅遊台")){
				TVURL="http://m.atmovies.com.tw/app2/attv.cfm?action=channeltime&cid=CHO31";
			}else if(TVName.equals("壹電視新聞台")){
				TVURL="http://m.atmovies.com.tw/app2/attv.cfm?action=channeltime&cid=CHP45";
		     }else if(TVName.equals("MOMO親子台")){
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
			 showTime.setText("今日 "+Stoday+getWeek(Stoday));
			 //2016/05/14新增
			 LongClickProgram setTimeDialog=new LongClickProgram(getActivity());
			//執行getProgram action
			try {
				listAdapter=new SimpleAdapter(getActivity(),pgList,R.layout.pg_list,new String[]{"PTime","PName"},new int[]{R.id.setTVTime,R.id.setTVName}){
					@Override
					public View getView(int position, View convertView,
							ViewGroup parent) {
						// TODO 自動產生的方法 Stub
						
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
						
						if(!isSearch){//如果是搜尋非今天節目狀態顏色不給只給重粉紅
						if(todayHashCode>Long.valueOf(listMap.get("hashCode"))){//現在時間大於目前節目時間
							 
							 
							mainText.setTextColor(0xffBEBEBE);//灰色 代表演完
							subText.setTextColor(0xffBEBEBE);
							if(position<pgList.size()-1){//避免溢位
								HashMap<String,String>  tempMap=(HashMap<String,String>)getItem(position+1);
								
								code= Long.valueOf(tempMap.get("hashCode"));//後面HashCode
								if(todayHashCode<code){//後面HashCode大於現在節目HASHCODE代表現在撥放
									 
									mainText.setTextColor(0xffff1493);//重粉紅
									subText.setTextColor(0xffff1493);
									System.out.println("resultList position:"+position);
								    
								}
							}
						}else{
						
						//還沒撥放
						subText.setTextColor(0xffff69b4);//粉紅
						mainText.setTextColor(0xffff69b4);
						}
						}else{
							subText.setTextColor(0xffff69b4);//非今日節目粉紅
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
				// TODO 自動產生的 catch 區塊
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO 自動產生的 catch 區塊
				e.printStackTrace();
			} catch (NoPGException e) {
				// TODO 自動產生的 catch 區塊
				Toast.makeText(getActivity(), "無此節目表", Toast.LENGTH_LONG).show();
				showTime.setText("無此節目表");
				resultList.setAdapter(listAdapter);
			}catch(NoInternet e){
				Toast.makeText(getActivity(), "網路中斷或伺服器異常", Toast.LENGTH_LONG).show();
				showTime.setText("網路中斷或伺服器異常");
			}catch(Exception e){
				showTime.setText("error");
			}
			bNextDay.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO 自動產生的方法 Stub
					isSearch=true;
					try {
					addDayCount++;
					if(addDayCount>7){
						addDayCount=7;
						Toast.makeText(getActivity(),"只允許查詢七天內節目",Toast.LENGTH_SHORT).show();
					}
					String day=showDay[addDayCount];//使用現有日期
					Calendar c=turnStringToD(day);
					getProgram programAction=new getProgram(c, progress,pid);
					
						pList=programAction.execute(TVURL).get();//執行正確URL+日期
						getPGBean(pList);//給LIST 並notifyDataSetChanged adapter
						showTime.setText(day+getWeek(day));
					} catch (InterruptedException e) {
						// TODO 自動產生的 catch 區塊
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO 自動產生的 catch 區塊
						e.printStackTrace();
					} catch (NoPGException e) {
						// TODO 自動產生的 catch 區塊
						Toast.makeText(getActivity(), "無此節目表", Toast.LENGTH_LONG).show();
						showTime.setText("無此節目表");
						resultList.setAdapter(listAdapter);
					}catch(NoInternet e){
						Toast.makeText(getActivity(), "網路中斷或伺服器異常", Toast.LENGTH_LONG).show();
						showTime.setText("網路中斷或伺服器異常");
					}catch(Exception e){
						showTime.setText("error");
					}
					
					System.out.println(addDayCount);
				}
			});
			bToday.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO 自動產生的方法 Stub
					getProgram programAction=new getProgram(today, progress,pid);
					try {
						isSearch=false;
						pList=programAction.execute(TVURL).get();
						getPGBean(pList);//給LIST 並notifyDataSetChanged adapter
						showTime.setText("今日 "+Stoday+getWeek(Stoday));
						resultList.post(new Runnable() {

					        @Override
					        public void run() {
					        	
					        	 System.out.println("pos------"+pos);
					        	resultList.setSelectionFromTop(pos, 12);
					        	 
					        }
					    });
						
						addDayCount=0;//計數器
					} catch (InterruptedException e) {
						// TODO 自動產生的 catch 區塊
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO 自動產生的 catch 區塊
						e.printStackTrace();
					} catch (NoPGException e) {
						// TODO 自動產生的 catch 區塊
						Toast.makeText(getActivity(), "無此節目表", Toast.LENGTH_LONG).show();
						showTime.setText("無此節目表");
						resultList.setAdapter(listAdapter);
					}catch(NoInternet e){
						Toast.makeText(getActivity(), "網路中斷或伺服器異常", Toast.LENGTH_LONG).show();
						showTime.setText("網路中斷或伺服器異常");
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
			boolean b=false;//第一次換日
			  boolean b2=false;//第二次換日
			  todayHashCode=Date.valueOf(getToday(0)).getTime()+Long.valueOf(getTimeNow());
				if(pgBean==null){
					System.out.println("internet intrupate");
					throw new NoInternet("網路中斷或伺服器異常");
				}
			  
			  if(pgBean.length==0){
				  System.out.println("bena size:"+pgBean.length);
				  throw new NoPGException("無此節目表");
				  
				   
			  }
			  System.out.println("pgBean day:"+pgBean[0].getPDate());
			  System.out.println("Today day:"+getToday(0));
			if(!pgBean[0].getPDate().equals(getToday(0))){//非今日節目
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
				  if(!b){//換成今天
					  if(Integer.valueOf(getTime(pgBean[i+1].getPTime()))>Integer.valueOf(getTime(pgBean[i].getPTime()))){//下一個節目時間大於現在節目時間設成現在日期
						  //一次
						  b=true;//換日 
						  String hashCode=String.valueOf( Date.valueOf(getToday(0)).getTime()+Long.valueOf(getTime(pgBean[i].getPTime())));
						  list.put("PTime",getTime(pgBean[i].getPTime()));
						  list.put("PName", pgBean[i].getPName());
						  list.put("PDate",getToday(0));//日期今天
						  list.put("hashCode", hashCode);
						  list.put("TVPGTime", channelNo.toString()+getToday(0)+getTime(pgBean[i].getPTime()));
						  list.put("TVNameChannel", TVName+","+channelNo);
						  System.out.println("PTime:"+","+getTime(pgBean[i].getPTime()));
					      System.out.println("PDate:"+","+getToday(0));
					       System.out.println("hashCode"+hashCode);
					  }else{//未換日前一天
						  String hashCode=String.valueOf( Date.valueOf(getToday(-1)).getTime()+Long.valueOf(getTime(pgBean[i].getPTime()) ));
						  list.put("PTime",getTime(pgBean[i].getPTime()));
						  list.put("PName", pgBean[i].getPName());
						  list.put("PDate",getToday(-1));//日期前一天
						  list.put("hashCode",hashCode );
						  list.put("TVPGTime", channelNo.toString()+getToday(-1)+getTime(pgBean[i].getPTime()));
						  list.put("TVNameChannel", TVName+","+channelNo);  
						  System.out.println("PTime:"+","+getTime(pgBean[i].getPTime()));
					      System.out.println("PDate:"+","+getToday(-1));
					       
					  }
				  }else{
					  if(!b2){//換成明天了
						  if(i<pgBean.length-1){
						  if(Integer.valueOf(getTime(pgBean[i+1].getPTime()))>Integer.valueOf(getTime(pgBean[i].getPTime()))){//下一個節目時間大於現在節目時間，那就還是今天
							  String hashCode=String.valueOf( Date.valueOf(getToday(0)).getTime()+Long.valueOf(getTime(pgBean[i].getPTime()) ));
							  list.put("PTime",getTime(pgBean[i].getPTime()));
							  list.put("PName", pgBean[i].getPName());
							  list.put("PDate",getToday(0));//日期今天
							  list.put("hashCode", hashCode);
							  list.put("TVPGTime", channelNo.toString()+getToday(0)+getTime(pgBean[i].getPTime()));
							  list.put("TVNameChannel", TVName+","+channelNo);
						      System.out.println("PTime:"+","+getTime(pgBean[i].getPTime()));
						      System.out.println("PDate:"+","+getToday(0));
						       System.out.println("hashCode"+hashCode);
						  }else{//99大於節目時間故又換日
							  //1次
							  b2=true;//再換日
							  String hashCode=String.valueOf( Date.valueOf(getToday(0)).getTime()+Long.valueOf(getTime(pgBean[i].getPTime())));
							  list.put("PTime",getTime(pgBean[i].getPTime()));
							  list.put("PName", pgBean[i].getPName());
							  list.put("PDate",getToday(0));//日期今天
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
							  list.put("PDate",getToday(0));//日期今天
							  list.put("hashCode", hashCode);
							  list.put("TVPGTime", channelNo.toString()+getToday(0)+getTime(pgBean[i].getPTime()));
							  list.put("TVNameChannel", TVName+","+channelNo);
						      System.out.println("PTime:"+","+getTime(pgBean[i].getPTime()));
						      System.out.println("PDate:"+","+getToday(0));
						       System.out.println("hashCode"+hashCode);
						  }
						  
					  }else{//如果換日就設成後一天
						  String hashCode=String.valueOf( Date.valueOf(getToday(1)).getTime()+Long.valueOf(getTime(pgBean[i].getPTime()) ));
						  list.put("PTime",getTime(pgBean[i].getPTime()));
						  list.put("PName", pgBean[i].getPName());
						  list.put("PDate",getToday(1));//日期後一天
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
		//抓現在撥放
		HashMap<String,String> value2=new HashMap<String,String>(); //存後一個ITEM
		Long todayHashCode=Date.valueOf(getToday(0)).getTime()+Long.valueOf(getTimeNow());
		int position=0;
		for(int i=0;i<pgList.size();i++){
			HashMap<String,String> value=new HashMap<String,String>();//存此次ITEM
			value=pgList.get(i);
			if(todayHashCode>Long.valueOf(value.get("hashCode"))){//現在時間比ITEM大
				if(i<pgList.size()-1){//避免溢位
					value2=pgList.get(i+1);
					 
					if(todayHashCode<Long.valueOf(value2.get("hashCode"))){//後面時間比ITEM小
					System.out.println("return now play"+i);
						position= i;//此ITEM 就是現在撥放時間
					
				}
				}
				 
			}
			 
		}
		return position;
		}
		/*
		 * @ i 1後一天 0今天 -1前一天
		 */
		protected static String getToday(int i){
			  Calendar today=Calendar.getInstance();
			  Calendar beDay=Calendar.getInstance();
			  Calendar afDay=Calendar.getInstance();
			SimpleDateFormat formate=new SimpleDateFormat("yyyy-MM-dd");
			beDay.add(Calendar.DATE, -1);
			afDay.add(Calendar.DATE, 1);
			String bDay=formate.format(beDay.getTime()); //前一天
			String aDay=formate.format(afDay.getTime());//後一天
			String stoday=formate.format(today.getTime());//今天
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
