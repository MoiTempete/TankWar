package com.delpy.deng;

import java.util.Random;

import android.util.Log;

public class GameViewBulletGoThread extends Thread {
	GameView gv;
	boolean flag=false;
	int sleepSpan=50;				//200毫秒一个周期
	Random rand;
	Bullet oneBullet;
	public GameViewBulletGoThread(GameView father){
		this.gv=father;
		flag=true;
		rand=new Random(System.currentTimeMillis());
	}
	public void run(){
		while(flag){
			if(!gv.father.isPause){
				synchronized (gv.bulletsList) {
					for(int i=0;i<gv.bulletsList.size();i++){
						oneBullet=(Bullet)gv.bulletsList.get(i);
						oneBullet.autoMove();
						if(oneBullet.hasCrashWall() || oneBullet.hasFlyOutOfScreen()){
							gv.bulletsList.remove(i);
						}
					}
				}
				synchronized (gv.myBulletsList) {
					for(int j=0;j<gv.myBulletsList.size();j++){
						oneBullet=(Bullet)gv.myBulletsList.get(j);
						oneBullet.autoMove();
						if(oneBullet.hasFlyOutOfScreen()){		//检测自己的子弹是否已经碰到了墙或飞出了屏幕
							gv.myBulletsList.remove(j);
						}
						if(oneBullet.hasCrashWall()){
							if(gv.hasBonu==false){				//如果还没有出现道具，则随机产生一个1-8号道具
								int getBonus=rand.nextInt(100);
								Log.d("Bonus","hasBonu="+gv.hasBonu+" getBonus+"+getBonus);
								if(getBonus<10){
									int typeBonus=rand.nextInt(8)+1;
									gv.hasBonu=true;
									gv.bonu=new Bonus(gv,typeBonus,oneBullet.row,oneBullet.col);
								}
							}
							gv.myBulletsList.remove(j);
						}
						if(oneBullet.hasCrashWithOtherBullet()){		//检测自己的子弹是否与敌人子弹相遇
							gv.myBulletsList.remove(j);
						}
						synchronized (gv.enemyTanks) {					//检测自己的子弹是否与敌人的坦克相遇
							for(int k=0;k<gv.enemyTanks.size();k++){
								if(gv.enemyTanks.get(k).crashWithOtherBullets()){
									break;
								}
							}
						}
					}
					
				}				
			}
			try{
				Thread.sleep(sleepSpan);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
