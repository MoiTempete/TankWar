package com.delpy.deng;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameViewDrawThread extends Thread {
	GameView gameView;
	boolean flag=true;
	private int sleepSpan=25;
	SurfaceHolder surfaceHolder=null;
	public GameViewDrawThread(GameView gameView,SurfaceHolder surfaceHolder){
		this.gameView=gameView;
		this.surfaceHolder=surfaceHolder;
	}
	public void run(){
		Canvas c;
		while(flag){
			c=null;
			try{
				c=surfaceHolder.lockCanvas(null);
				synchronized(this.surfaceHolder){
					try{
						gameView.doDraw(c);
					}catch(Exception e){
						
					}
				}
			}finally{
				if(c!=null){
					surfaceHolder.unlockCanvasAndPost(c);
				}
			}
			try{
				Thread.sleep(sleepSpan);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	public void setFlag(boolean flag){
		this.flag=flag;		
	}
}