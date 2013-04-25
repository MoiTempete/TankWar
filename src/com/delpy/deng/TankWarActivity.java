package com.delpy.deng;
/*
 * 游戏：坦克大战，单人游戏
 * 制作：Delpy Deng
 * 版权所有：江西理工大学应用科学学院
 * 游戏操作说明：上下左右方向键控制我方坦克运动方向，空白键发出子弹，回车键暂停/恢复游戏，Menu键弹出菜单
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class TankWarActivity extends Activity {
	TankWarActivity context;
	GameView gv=null;
	View currentView;
	int guanKa;
	int allGuanKa;
	boolean isGameOver;
	boolean isPause;
	int keyCode;		//用户按键上下左右：19,20,21,22,空白：62,回车：66,
	private static final int ITEM_RESTART=Menu.FIRST;
	private static final int ITEM_EXIT=Menu.FIRST+1;
	private static final int ITEM_PAUSE=Menu.FIRST+2;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context=this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        guanKa=0;
        isPause=false;
        allGuanKa=Maps.maps.length;
        initGameView();
    }
    Handler myHandler=new Handler(){
    	public void handleMessage(Message msg) {
    		switch(msg.what){
    		case 0:									//退出游戏
    			finish();
    			break;
    		case 1:									//重新开始游戏
    			initGameView();
    			break;
    		case 2:									//成功过了一关
    			guanKa++;
    			if(guanKa>=allGuanKa){
    				guanKa=0;
    			}
    			initGameView();
    			break;
    		case 100:								//Game Over
    			isGameOver=true;
    			if(gv!=null){
    				gv.gameOver=true;
    			}
    			break;
    		case 200:								//我方没有坦克了
    			isGameOver=true;
    			if(gv!=null){
    				gv.noMoreTanks=true;
    				gv.gameOver=true;
    			}
    		}
    		super.handleMessage(msg);
    	}
    };
    public boolean onTouchEvent(MotionEvent event){		//屏幕单击监听
		int X=(int)(event.getX());
		int Y=(int)(event.getY());
		Log.d("Event","Position:("+X+","+Y+")");
		Rect rUp,rDown,rLeft,rRight,rFire;
		rUp=new Rect(140,100,180,180);
		rDown=new Rect(140,240,180,320);
		rLeft=new Rect(0,190,80,230);
		rRight=new Rect(240,190,320,230);
		rFire=new Rect(140,360,200,400);
		if(currentView==gv && gv!=null)
		{
			if(rUp.contains(X,Y)){
				keyCode=19;
			}else if(rDown.contains(X,Y)){
				keyCode=20;
			}else if(rLeft.contains(X,Y)){
				keyCode=21;
			}else if(rRight.contains(X,Y)){
				keyCode=22;
			}
			if(rFire.contains(X,Y)){			//开火
				keyCode=62;
			}
		}
		if(currentView==gv && gv!=null && gv.gameOver==true || gv==null){
			if(isGameOver){
				final AlertDialog.Builder builder=new AlertDialog.Builder(this);
				builder.setMessage("请选择").setPositiveButton("重新游戏", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						myHandler.sendEmptyMessage(1);
					}
				}).setNegativeButton("退出游戏", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						myHandler.sendEmptyMessage(0);
					}
				});
				AlertDialog ad=builder.create();
				ad.show();
			}
		}
		return super.onTouchEvent(event);
    }
    public void initGameView(){
    	long lScore=0;
    	int nMyTanks=gv.maxMyTanks;
    	boolean passACard=false;
    	if(gv!=null){
    		if(gv.nMyTanks>=gv.maxEnemyTanks){
    			nMyTanks=gv.nMyTanks;
    			lScore=gv.score;
    		}
    		passACard=true;
    	}
    	isGameOver=false;
        gv=new GameView(this,guanKa);
        if(passACard==true){
        	gv.nMyTanks=nMyTanks;
        	gv.score=lScore;
        }
        setContentView(gv);
        currentView=(View)gv;
        
    }
    public boolean onKeyDown(int keyCode,KeyEvent event){
    	this.keyCode=keyCode;			//keyCode是用户的按键，19：上，20：下，21：左，22：右，空白：62,回车：66,
//    	Log.d("keyCode","keyCode="+keyCode);
    	if(keyCode==66){				//回车键为暂停键
    		this.isPause=!this.isPause;    		
    	}
    	return false;
    }
    public boolean onCreateOptionsMenu(Menu menu){
    	menu.add(0,ITEM_RESTART,0,"重新游戏");
    	if(!isPause){
    		menu.add(0,ITEM_PAUSE,0,"暂停游戏");
    	}else{
    		menu.add(0,ITEM_PAUSE,0,"恢复游戏");
    	}
    	menu.add(0,ITEM_EXIT,0,"退出游戏");
    	return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()){
    	case ITEM_RESTART:
    		myHandler.sendEmptyMessage(1);
    		break;
    	case ITEM_EXIT:
    		myHandler.sendEmptyMessage(0);
    		break;
    	case ITEM_PAUSE:
    		this.isPause=!this.isPause;
    		if(isPause)
    			item.setTitle("恢复游戏");
    		else
    			item.setTitle("暂停游戏");
    	}
    	return true;
    }
}