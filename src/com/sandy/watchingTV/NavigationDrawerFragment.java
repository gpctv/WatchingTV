package com.sandy.watchingTV;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import bean.TVBean;
import bean.Type;

import com.sandy.watchingTV.R;






import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Fragment used for managing interactions for and presentation of a navigation
 * drawer. See the <a href=
 * "https://developer.android.com/design/patterns/navigation-drawer.html#Interaction"
 * > design guidelines</a> for a complete explanation of the behaviors
 * implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

	/**
	 * Remember the position of the selected item.
	 */
	private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

	/**
	 * Per the design guidelines, you should show the drawer on launch until the
	 * user manually expands it. This shared preference tracks this.
	 */
	private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

	/**
	 * A pointer to the current callbacks instance (the Activity).
	 */
	private NavigationDrawerCallbacks mCallbacks;

	/**
	 * Helper component that ties the action bar to the navigation drawer.
	 */
	private ActionBarDrawerToggle mDrawerToggle;

	private DrawerLayout mDrawerLayout;
	private ExpandableListView mDrawerListView;
	private View mFragmentContainerView;
	static ArrayList<HashMap<String,String>> mainlist = new ArrayList<HashMap<String,String>>();
	static ArrayList<ArrayList<HashMap<String,String>>> subItem2=new ArrayList<ArrayList<HashMap<String,String>>>();
	private int mCurrentSelectedPosition = 0;
	private boolean mFromSavedInstanceState;
	private boolean mUserLearnedDrawer;
	private ProgressBar mPB;
  private TVBean tv[];
  private Type t[];
  SharedPreferences settings ;
  SharedPreferences settings2 ;
   SimpleExpandableListAdapter SEAdapter;
   SparseBooleanArray Selected=new SparseBooleanArray();
   private static final int Version=Build.VERSION.SDK_INT; //android 版本
	public NavigationDrawerFragment() {
		
	}
//1.先CREATE
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
       
		// Read in the flag indicating whether or not the user has demonstrated
		// awareness of the
		// drawer. See PREF_USER_LEARNED_DRAWER for details.
	   System.out.println(Version);
		settings=getActivity().getSharedPreferences("Type", 0); //獲取TYPE XML
		settings2=getActivity().getSharedPreferences("TV", 1);	//獲取TV XML
		getTypeBean(settings,settings2); //將TYPE TV 塞到TYPEBEAN &subItem2 mainlist 
		//**第一次安裝的人會展開DRAW 讓USER知道
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
	 mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);
		
		if (savedInstanceState != null) {
			mCurrentSelectedPosition = savedInstanceState
					.getInt(STATE_SELECTED_POSITION);
			mFromSavedInstanceState = true;
		}
		//********//
		System.out.println("onCreate");
		// Select either the default item (0) or the last selected item.
		System.out.print("mCurrentSelectedPosition"+mCurrentSelectedPosition);
		//selectItem(mCurrentSelectedPosition);
		
	}
//3.進行onActivityCreated
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Indicate that this fragment would like to influence the set of
		// actions in the action bar.
		setHasOptionsMenu(true);
		System.out.println("onActivityCreated");
		OnChildClickListener ChildOnClick=new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO 自動產生的方法 Stub
			String position=String.valueOf(groupPosition)+String.valueOf(childPosition);
			    int iPosition=	Integer.valueOf(position);
				boolean po=!Selected.get(iPosition);
				System.out.println("iPosition"+iPosition);
				System.out.println("parent:"+parent.getItemAtPosition(groupPosition));
			    HashMap<String,String> object=(HashMap<String,String> )SEAdapter.getChild(groupPosition, childPosition);
				
			    System.out.println(object.toString());//get 電視台名稱資料
			    selectItem(object);
			    Selected.clear();//單選，所以選其他的必須清空SELECTED
				System.out.println("groupPosition:"+groupPosition);
				System.out.println( "childPosition"+childPosition);
				//選擇設定
				if(po){
					Selected.put(iPosition, po); //將選擇座標放入SELECTED
				}else{
					Selected.delete(iPosition);
				}
				SEAdapter.notifyDataSetChanged();
				
				return false;
			}
		};
		OnGroupClickListener onGroupClickListener=new OnGroupClickListener() {
			
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				// TODO 自動產生的方法 Stub
				return false;
			}
		};
		mDrawerListView.setOnChildClickListener(ChildOnClick);	
		mDrawerListView.setOnGroupClickListener(onGroupClickListener);
	}
//2.創造FRAGMENT VIEW
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//創建fragment_navigation_draw 物件
		View rootView=inflater.inflate(R.layout.fragment_navigation_drawer,container,false);
		//rootView上面建立ProgressBar 物件
		
		//listView 物件
		mDrawerListView = (ExpandableListView) rootView.findViewById(R.id.ELV1);
		//set listview adapter
				SEAdapter=new SimpleExpandableListAdapter(getActionBar()
						.getThemedContext(),mainlist, R.layout.expandable_main,
						 new String[] {"typeName"},new int[]{R.id.item1},
						 subItem2,R.layout.expandable_sub,
						 new String[] {"listName"},
						 new int[]{R.id.listText}){
							 @Override
							public View getChildView(int groupPosition,
									int childPosition, boolean isLastChild,
									View convertView, ViewGroup parent) {
								 String key=String.valueOf(groupPosition)+String.valueOf(childPosition);
								 System.out.println("getChildView:"+key);//獲取選擇座標
								 View view=super.getChildView(groupPosition, childPosition, isLastChild,
											convertView, parent);//獲取CHILD VIEW
								 if(Selected.get(Integer.valueOf(key))){
									 view.setBackgroundColor(0xffffc0cb); //選擇的項目標光棒粉色
									 
								  }else{
									 view.setBackgroundColor(0xffdcdcdc);//沒選擇的項目不標光棒
								  }
								 
								// TODO 自動產生的方法 Stub
								return super.getChildView(groupPosition, childPosition, isLastChild,
										convertView, parent);
							}
						 };
				mDrawerListView.setAdapter(SEAdapter);
		
		
		mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
		
		System.out.println("getRootView");
		return rootView;
	}
//點了ITEM後判斷
	public boolean isDrawerOpen() {
		System.out.println("isDrawerOpen");
		
		return mDrawerLayout != null
				&& mDrawerLayout.isDrawerOpen(mFragmentContainerView);
		
	}

	/**
	 * Users of this fragment must call this method to set up the navigation
	 * drawer interactions.
	 * 
	 * @param fragmentId
	 *            The android:id of this fragment in its activity's layout.
	 * @param drawerLayout
	 *            The DrawerLayout containing this fragment's UI.
	 */
	public void setUp(int fragmentId, DrawerLayout drawerLayout) {
		mFragmentContainerView = getActivity().findViewById(fragmentId);
		mDrawerLayout = drawerLayout;

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// set up the drawer's list view with items and click listener

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the navigation drawer and the action bar app icon.
		mDrawerToggle = new ActionBarDrawerToggle(getActivity(), /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.navigation_drawer_open, /*
										 * "open drawer" description for
										 * accessibility
										 */
		R.string.navigation_drawer_close /*
										 * "close drawer" description for
										 * accessibility
										 */
		) {
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if (!isAdded()) {
					return;
				}

				getActivity().supportInvalidateOptionsMenu(); // calls
																// onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (!isAdded()) {
					return;
				}
//判斷是否第一次裝是否要展開draw讓USER知道
				if (!mUserLearnedDrawer) {
					// The user manually opened the drawer; store this flag to
					// prevent auto-showing
					// the navigation drawer automatically in the future.
					mUserLearnedDrawer = true;
					SharedPreferences sp = PreferenceManager
							.getDefaultSharedPreferences(getActivity());
					sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true)
							.commit();
				}

				getActivity().supportInvalidateOptionsMenu(); // calls
																// onPrepareOptionsMenu()
			}
		};

		// If the user hasn't 'learned' about the drawer, open it to introduce
		// them to the drawer,
		// per the navigation drawer design guidelines.
		if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
			mDrawerLayout.openDrawer(mFragmentContainerView);
		}

		// Defer code dependent on restoration of previous instance state.
		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});

		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	private void selectItem(HashMap<String,String> data) {
		mCurrentSelectedPosition = 1;
		 System.out.println("mCurrentSelectedPosition:"+mCurrentSelectedPosition);
		if (mDrawerListView != null) {
			mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
			
		}
		if (mDrawerLayout != null) {
			mDrawerLayout.closeDrawer(mFragmentContainerView);
		}
		if (mCallbacks != null) {
			mCallbacks.onNavigationDrawerItemSelected(data);
		}
	}
//開始
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallbacks = (NavigationDrawerCallbacks) activity;
			System.out.println("onAttach run");
		} catch (ClassCastException e) {
			throw new ClassCastException(
					"Activity must implement NavigationDrawerCallbacks.");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		System.out.println("mCurrentSelectedPosition"+mCurrentSelectedPosition);
		System.out.println("STATE_SELECTED_POSITION"+STATE_SELECTED_POSITION);
		outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Forward the new configuration the drawer toggle component.
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// If the drawer is open, show the global app actions in the action bar.
		// See also
		// showGlobalContextActionBar, which controls the top-left area of the
		// action bar.
		if (mDrawerLayout != null && isDrawerOpen()) {
			inflater.inflate(R.menu.global, menu);
			showGlobalContextActionBar();
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			System.out.println("item-"+item.toString());
			return true;
		}
		System.out.println("get item id-"+item.getItemId());

		if (item.getItemId() == R.id.action_example) {
			//Toast.makeText(getActivity(), "Example action.", Toast.LENGTH_SHORT)
			//		.show();
			return true;
		}
		

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Per the navigation drawer design guidelines, updates the action bar to
	 * show the global app 'context', rather than just what's in the current
	 * screen.
	 */
	private void showGlobalContextActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(R.string.app_name);
	}

	private ActionBar getActionBar() {
		return ((ActionBarActivity) getActivity()).getSupportActionBar();
	}

	/**
	 * Callbacks interface that all activities using this fragment must
	 * implement.
	 */
	public static interface NavigationDrawerCallbacks {
		/**
		 * Called when an item in the navigation drawer is selected.
		 */
		void onNavigationDrawerItemSelected(HashMap<String,String> position);
	}
	/**
	 * sharePreferences object
	 * 種類塞入TypeBean 再塞入LIST(mainlist)
	 * @param settings 
	 */
	protected void getTypeBean(SharedPreferences settings,SharedPreferences settings2){
		Map<String,?>data1=settings.getAll();//share
		Map<String,?>subData=settings2.getAll();//get sub share
			t=new Type[data1.size()];
		    tv=new TVBean[subData.size()];
		 Object[] key=data1.keySet().toArray();//get XML key
		 for(int i=0;i<key.length;i++){//將種類塞入TypeBean
			 t[i]=new Type();
			String TypeBean[]= data1.get(key[i].toString()).toString().split(",");//抓此KEY VALUE 一長串資料再分割
			System.out.println( "data get:"+data1.get(key[i].toString()).toString());
			System.out.println("TypeBean:"+TypeBean[0]);
			System.out.println("TypeBean:"+TypeBean[1]);
			System.out.println("TypeBean:"+TypeBean[2]);
			t[i].setPID(Integer.valueOf(TypeBean[2]));
			t[i].setTypeName(TypeBean[0]);
			t[i].setUrl(TypeBean[1]);
		 }
		 Object[] key2=subData.keySet().toArray();
		 for(int i=0;i<key2.length;i++){//電視台名稱:
			 tv[i]=new TVBean();
			 String TVBean[]=subData.get(key2[i].toString()).toString().split(",");
		     System.out.println("listdata get:"+subData.get(key2[i].toString()).toString());
		     System.out.println("TVBean:"+TVBean[0]);
		     System.out.println("TVBean:"+TVBean[1]);
		     System.out.println("TVBean:"+TVBean[2]);
		     System.out.println("TVBean:"+TVBean[3]);
		     tv[i].setPID(Integer.valueOf(TVBean[0]));
		     tv[i].setTVName(TVBean[1]);
		     tv[i].setURL(TVBean[2]);
		     tv[i].setChannelNO(TVBean[3]);
		 }
		 for(int i=0;i<t.length;i++){//種類:再塞入要顯示的List
			 HashMap<String,String> list2=new HashMap<String,String>();
			 System.out.println(t[i].getTypeName());
			 list2.put("typeName", t[i].getTypeName());
			 list2.put("PID", t[i].getPID().toString());
			 list2.put("URL", t[i].getUrl());
			 mainlist.add(list2);
			 
			 ArrayList<HashMap<String, String>> subList = new ArrayList<HashMap<String, String>>();
			
			 
			 for(int j=0;j<tv.length;j++){
				 if(t[i].getPID()==tv[j].getPID()){//PID 相同才塞入相同組別
			HashMap<String, String> chlidList = new HashMap<String, String>();
			 chlidList.put("listName", tv[j].getTVName());
			 chlidList.put("PID", tv[j].getPID().toString());
			 chlidList.put("ChannelNo", tv[j].getChannelNo());
			 chlidList.put("URL", tv[j].getURL());
			 subList.add(chlidList);
			 
				 }
				 
			 }
			 subItem2.add(subList);
		 }
	}
	
}
