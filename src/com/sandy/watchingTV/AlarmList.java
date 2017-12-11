package com.sandy.watchingTV;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import bean.AlertListBean;
import android.support.v7.app.ActionBarActivity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class AlarmList extends ActionBarActivity   {
	SharedPreferences settings; // 設定存入節目資訊XML
	SharedPreferences.Editor editor;// 設定EDITOR XML
	Button deleteAlarm, deleteAllAlarm;
	ListView AlarmList1;
	AlertListBean alartBean[];
	CustListAdapter simpleAdapter;
	ArrayList<HashMap<String, String>> dataList;
	HashMap<Integer,String> saveDelData;
	protected SparseBooleanArray selectItem;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_alarm_list);
		/* GOOGLE 廣告橫幅*/
		 AdView mAdView = (AdView) findViewById(R.id.adView2);
	        AdRequest adRequest = new AdRequest.Builder().build();
	        mAdView.loadAd(adRequest);
	        /* GOOGLE 廣告橫幅*/ 
		selectItem=new SparseBooleanArray();
		dataList = new ArrayList<HashMap<String, String>>();
		settings = this.getSharedPreferences("setAlarmText", 0);
		saveDelData=new HashMap<Integer,String>();
		editor = settings.edit();
		deleteAlarm = (Button) findViewById(R.id.deleteAlarm);
		deleteAllAlarm = (Button) findViewById(R.id.deleteAllAlarm);
		AlarmList1 = (ListView) findViewById(R.id.alarmListView);
		alartBean = setBean();
		setHasMapList(alartBean);
		simpleAdapter = new CustListAdapter(this, dataList, R.layout.pg_list,
				new String[] { "showTV", "showPG" }, new int[] {
						R.id.setTVTime, R.id.setTVName });
		AlarmList1.setAdapter(simpleAdapter);
		simpleAdapter.notifyDataSetChanged();
		LongClickPGAlarmList LCPGAlist=new LongClickPGAlarmList(this,simpleAdapter,AlarmList1);
		AlarmList1.setOnItemLongClickListener(LCPGAlist);
		/**
		 * 選擇事件
		 */
		OnItemClickListener listviewSelectItem=new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO 自動產生的方法 Stub
				boolean po=!selectItem.get(arg2);
				ListView listView = (ListView) arg0;
				System.out.println("ALSelectItem-"+listView.getItemAtPosition(arg2));
				HashMap c=(HashMap) listView.getItemAtPosition(arg2);
				
				
				if(po){
					String ID=get_ID(c);
					System.out.println("GET ID:"+ID);
					selectItem.put(arg2,po );
					saveDelData.put(arg2,ID);
				}else{
					selectItem.delete(arg2);
					saveDelData.remove(arg2);
				}
				simpleAdapter.notifyDataSetChanged();
			}
			
		};
		AlarmList1.setOnItemClickListener(listviewSelectItem);
		/**
		 * Select 刪除事件
		 */
		OnClickListener deleteClick=new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自動產生的方法 Stub
				AlertDialog.Builder dialog ;
			if(saveDelData.isEmpty()){
				Toast.makeText(getApplicationContext(), "無資料選取", Toast.LENGTH_LONG).show();
			}else{
					  dialog = new AlertDialog.Builder(
							AlarmList.this);
					dialog.setTitle("確定刪除?");
					dialog.setPositiveButton("是",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Object[] bean = saveDelData.keySet()
											.toArray();
									for (int i = 0; i < bean.length; i++) {
										System.out.println("printDeleteTime:"
												+ saveDelData
														.get((Integer) bean[i]));
										deleteXML(saveDelData
												.get((Integer) bean[i]));
										cancleAlarm(saveDelData
												.get((Integer) bean[i]));
									}
									selectItem.clear();
									saveDelData.clear();
									onFresh();
								}

							});
					dialog.setNegativeButton("不!我不刪除", null);
					dialog.show();
				
			}
			}
 
		};
		/**
		 * DeleteAll
		 */
		OnClickListener deleteAllClick=new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自動產生的方法 Stub
				if(AlarmList1.getCount()==0||AlarmList1.getCount()<0){
					Toast.makeText(getApplicationContext(), "無資料", Toast.LENGTH_LONG).show();
				}else{
					AlertDialog.Builder dialog = new AlertDialog.Builder(
							AlarmList.this);
					dialog.setTitle("確定刪除?");
					dialog.setPositiveButton("是",new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO 自動產生的方法 Stub
							deleteAllXML();
							cancleAllAlarm();
							onFresh();
						}
						
					});
					dialog.setNegativeButton("不!我不刪除", null);
					dialog.show();
				}
			}
		};
		deleteAlarm.setOnClickListener(deleteClick);//刪除按鈕註冊
		deleteAllAlarm.setOnClickListener(deleteAllClick);
	}
	protected void cancleAlarm(String SID){
		AlarmManager alarmManger=(AlarmManager)this.getSystemService(this.ALARM_SERVICE); 
		Intent intent= new Intent( );
		intent.setClass(this, AlarmReceiver.class);
		intent.putExtra("SID", SID);//存ID給CLASS呼叫
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Integer.valueOf(SID), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManger.cancel(pendingIntent);
        
        System.out.println("cancel success :"+SID);
	}
	protected void deleteXML(String SID){
		editor.remove(SID);
		editor.commit();
	}
	protected void deleteAllXML(){
		editor.clear();
		editor.commit();
	}
	protected void cancleAllAlarm(){
		for(int i=0;i<AlarmList1.getCount();i++){
		HashMap<String,String> c=(HashMap<String,String>) AlarmList1.getItemAtPosition(i);
	   String getIDTemp[]=c.get("checkAlarm").toString().split(":");
	   String getAllID=getIDTemp[0];
	   System.out.println("getAllID"+i+":"+getAllID);
	   cancleAlarm(getAllID);
		}
	}
	protected String get_ID(HashMap c){
		String temp[]=c.get("checkAlarm").toString().split(":");
		String temp1=temp[0];
		return temp1;
	}
/**
 * 從XML存入BEAN
 * @return
 */
	protected AlertListBean[] setBean() {
		try {
			Map<String, ?> data1 = settings.getAll();

			alartBean = new AlertListBean[data1.size()];
			Object bean[] = data1.keySet().toArray();

			for (int i = 0; i < bean.length; i++) {
				String tempDate[] = data1.get(bean[i].toString()).toString()
						.split(",");
				System.out.println("getAllXML:"+data1.get(bean[i].toString()).toString());
				alartBean[i] = new AlertListBean();
				System.out.println("AlarmList:"+bean[i].toString() + ":" + tempDate[0] + ","
						+ tempDate[1]);
				alartBean[i].setTVChannel(bean[i].toString() + ":"
						+ tempDate[0] + "," + tempDate[1]);// ID+checkID+頻道數CHXX
				alartBean[i].setTVName(tempDate[3] +","+ tempDate[2]);
				System.out.println("alarmlist TVName:"+tempDate[3] + tempDate[2]);
				alartBean[i].setPGName(tempDate[4]);
				alartBean[i].setPDate(tempDate[5]);
				alartBean[i].setPGTime(tempDate[6]);
				alartBean[i].setSDate(tempDate[7]);
				alartBean[i].setSTime(tempDate[8]);
				System.out.println("PG:"+tempDate[4]+","+tempDate[5]+","+tempDate[6]
						+","+tempDate[7]+","+tempDate[8]);

			}
			return alartBean;
		} catch (NullPointerException nulle) {
			Toast.makeText(this, "無資料", Toast.LENGTH_LONG).show();
			return null;
		}

	}
	
/**
 * alartBean存入datalist
 * @param alartBean 
 */
	protected ArrayList<HashMap<String, String>> setHasMapList(AlertListBean[] alartBean) {
		dataList.clear();
		System.out.println(alartBean.length);
		for (int i = 0; i < alartBean.length; i++) {
			HashMap<String, String> list2 = new HashMap<String, String>();
			list2.put("checkAlarm", alartBean[i].getTVChannel());
			list2.put("showTV", alartBean[i].getTVName());
			list2.put("showPG",
					alartBean[i].getPGName() + ",撥出" + alartBean[i].getPDate()
							+ "," + alartBean[i].getPGTime());
			list2.put(
					"TVInfo",
					alartBean[i].getTVName() + ","
							+ alartBean[i].getTVChannel());
			list2.put("PGInfo",
					alartBean[i].getPGName() + "," + alartBean[i].getPDate()
							+ "," + alartBean[i].getPGTime());
			list2.put("setInfo",
					alartBean[i].getSDate() + "," + alartBean[i].getSTime());
			System.out.println("setHasMapList" + alartBean[i].getTVName() + ","
					+ alartBean[i].getPGName() + alartBean[i].getPDate()+","
					+ alartBean[i].getPGTime()+","+alartBean[i].getSDate() + "," 
					+ alartBean[i].getSTime());
			dataList.add(list2);
		}
		return dataList;
	}
	public void onFresh(){
		alartBean = setBean();
		setHasMapList(alartBean);
		AlarmList1.setAdapter(simpleAdapter);
		simpleAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.alarm_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onResume() {
		// TODO 自動產生的方法 Stub
		super.onResume();
	}
	@Override
	protected void onPause() {
		// TODO 自動產生的方法 Stub
		
		super.onPause();
	}
	@Override
	protected void onStop() {
		// TODO 自動產生的方法 Stub
		
		super.onStop();
	}

}
