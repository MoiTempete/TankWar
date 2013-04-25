package com.delpy.deng;

import android.util.Log;

/*
 * 类：每50毫秒检测道具的状态，如果其状态已经变为2或3了，则将该道具收回
 */
public class GameViewBonuDoThread extends Thread {
	GameView gv;
	boolean flag;
	int sleepSpan=50;
	public GameViewBonuDoThread(GameView gv){
		this.gv=gv;
		flag=true;
	}
	public void run(){
		while(flag){
//			Log.d("hasBonus","hasBonus"+gv.hasBonu);
			if(!gv.father.isPause){
				if(gv.hasBonu==true && gv.bonu!=null && gv.bonu.status!=1){
					gv.hasBonu=false;
					gv.bonu=null;
				}
			}
			try{
				Thread.sleep(50);
			}catch(Exception e){
				
			}
		}
	}
}
