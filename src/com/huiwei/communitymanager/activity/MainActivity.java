
package com.huiwei.communitymanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huiwei.commonlib.Preferences;
import com.huiwei.communitymanager.R;
import com.huiwei.communitymanager.activity.BaseFragment.OnMenuPopListener;
import com.huiwei.communitymanager.adapter.MainMenuItemAdapter;
import com.huiwei.communitymanager.task.LogoutTask;
import com.huiwei.communitymanager.utils.Constants;
import com.huiwei.communitymanager.utils.Data;

public class MainActivity extends FragmentActivity {

    private final static int ANIMATION_DURATION_FAST = 350;
    private final static int ANIMATION_DURATION_SLOW = 250;
    private final static int MOVE_DISTANCE = 10;

	private boolean slided = false;
	private int screenWidth;
	private float mPositionX;

	private int tabIndex = 0;
	private ListView mainMenuListView;
	private RelativeLayout layoutTab;
	private LinearLayout layoutContent;
	private TextView waiterName;
	
	private MainMenuItemAdapter adapter;

	private UserCenterFragment fragmentUserCenter;
	private CommunityShoppingFragment fragmentCommunityShopping;
	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;
	
	private long mExitTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (Preferences.GetBoolean(this, "keep_screen_on", true)) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		}
		
		setContentView(R.layout.activity_main);
		
		Data.fillMainNenu(this);
		
		initMainMenu();
		initFragment();
		
		waiterName = (TextView)findViewById(R.id.tv_waitress);
		waiterName.setText(Data.memberInfo.realName);
		
		layoutTab = (RelativeLayout)findViewById(R.id.layout_tab);
		layoutContent = (LinearLayout)findViewById(R.id.layout_main);
		layoutContent.setOnTouchListener(mOnTouchListener);
		
		screenWidth = getResources().getDisplayMetrics().widthPixels;
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}

	private void initMainMenu() {
		mainMenuListView = (ListView)findViewById(R.id.lv_menu);
		adapter = new MainMenuItemAdapter(this);
		mainMenuListView.setAdapter(adapter);
		mainMenuListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				adapter.setSelectItemID(position);
				adapter.notifyDataSetChanged();
				
				fragmentTransaction = fragmentManager.beginTransaction();
				switch (position) {
				case Constants.MENU_USER_CENTER:
					fragmentTransaction.show(fragmentUserCenter);
		            fragmentTransaction.hide(fragmentCommunityShopping);    
					break;
				case Constants.MENU_COMMUNITY_SHOPPING:
					fragmentTransaction.hide(fragmentUserCenter);
					fragmentTransaction.show(fragmentCommunityShopping);
					break;
				case Constants.MENU_AROUND_STORES:
					break;
					
				default:
					break;
				}
				
				if (position != tabIndex) {
					fragmentTransaction.commit();
					tabIndex = position;
				}
				
				slideIn();
			}
		});
	}
	
	private void initFragment() {
		fragmentUserCenter = new UserCenterFragment();
		fragmentCommunityShopping = new CommunityShoppingFragment();
		
		fragmentUserCenter.setOnMenuPopListener(menuPopListener);
		fragmentCommunityShopping.setOnMenuPopListener(menuPopListener);

		fragmentManager = getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.layout_content, fragmentUserCenter);
		fragmentTransaction.add(R.id.layout_content, fragmentCommunityShopping);
		fragmentTransaction.show(fragmentUserCenter);  
		fragmentTransaction.commit();
	}
	
	OnMenuPopListener menuPopListener = new OnMenuPopListener() {
		
		@Override
		public void onMenuPop() {
			if (slided) {
				slideIn();
			} else {
				slideOut();
			}
		}
	};

	private OnTouchListener mOnTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (v.getId() == R.id.layout_main) {
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					mPositionX = event.getX();
					break;
				case MotionEvent.ACTION_MOVE:
					final float currentX = event.getX();
					if (currentX - mPositionX <= -MOVE_DISTANCE && !slided) {
						slideOut();
					} else if (currentX - mPositionX >= MOVE_DISTANCE
							&& slided) {
						slideIn();
					}
					break;
				}
				return true;
			}
			return false;
		}
	};

	private void slideOut() {
		TranslateAnimation translate = new TranslateAnimation(-layoutTab.getWidth(),
				0, 0, 0);
		translate.setDuration(ANIMATION_DURATION_SLOW);
		translate.setFillAfter(true);
		layoutTab.startAnimation(translate);
		layoutTab.getAnimation().setAnimationListener(
				new Animation.AnimationListener() {

					@Override
					public void onAnimationStart(Animation anim) {
						
					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation anima) {
						slided = true;
//						layoutTab.clearAnimation();
					}
				});
		TranslateAnimation animation = new TranslateAnimation(
				0, layoutTab.getWidth(), 0, 0);
		animation.setDuration(ANIMATION_DURATION_FAST);
		animation.setFillAfter(true);
		layoutContent.startAnimation(animation);
		layoutContent.getAnimation().setAnimationListener(
				new Animation.AnimationListener() {

					@Override
					public void onAnimationStart(Animation anim) {
						
					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation anima) {
						layoutContent.clearAnimation();
						RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams
								(screenWidth, LayoutParams.MATCH_PARENT);
						lp.setMargins(layoutTab.getWidth(), 0, -layoutTab.getWidth(), 0);
						layoutContent.setLayoutParams(lp);
					}
				});
	}

	private void slideIn() {
		TranslateAnimation translate = new TranslateAnimation(
				0, -layoutTab.getWidth(), 0, 0);
		translate.setDuration(ANIMATION_DURATION_FAST);
		translate.setFillAfter(true);
		layoutTab.clearAnimation();
		layoutTab.startAnimation(translate);
		layoutTab.getAnimation().setAnimationListener(
				new Animation.AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
						
					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						slided = false;
						
					}
				});
		
		TranslateAnimation mainAnimation = new TranslateAnimation(
				0, -layoutTab.getWidth(), 0, 0);
		mainAnimation.setDuration(ANIMATION_DURATION_SLOW);
		mainAnimation.setFillAfter(true);
		layoutContent.clearAnimation();
		layoutContent.startAnimation(mainAnimation);
		layoutContent.getAnimation().setAnimationListener(
				new Animation.AnimationListener() {

					@Override
					public void onAnimationStart(Animation anim) {
						
					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation anima) {
						layoutContent.clearAnimation();
						RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams
								(screenWidth, LayoutParams.MATCH_PARENT);
						lp.setMargins(0, 0, 0, 0);
						layoutContent.setLayoutParams(lp);
					}
				});
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (slided) {
			slideIn();
		} else {
			slideOut();
		}
		return true;
	}
	
	@Override
	public void onBackPressed() {
		if (System.currentTimeMillis() - mExitTime > Constants.INTERVAL) {
			Toast.makeText(this, getResources().getString(R.string.exit_app), 
					Toast.LENGTH_SHORT).show();
			mExitTime = System.currentTimeMillis();
		} else {
			LogoutTask at = new LogoutTask(this, null);
			at.execute();
			
			finish();
			System.exit(0);
		}
	}
	
	@Override
	public void onDestroy() {

		super.onDestroy();
	}

}
