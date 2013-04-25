package com.delpy.deng;

import java.util.Random;

import android.util.Log;

/*
 * 生成敌方坦克，每个周期修改敌方坦克的位置，并处理其移动、发放子弹等操作
 */
public class GameViewEnemyTankGoThread extends Thread {
	GameView gv;
	boolean flag=false;
	int sleepSpan=400;				//400毫秒一个周期
	Random rand;
	int i;
	public GameViewEnemyTankGoThread(GameView father){
		this.gv=father;
		flag=true;
		rand=new Random(System.currentTimeMillis());
	}
	public void run(){
		while(flag){
			if(!gv.father.isPause){
				synchronized (gv.enemyTanks) {
					for(int i=0;i<gv.enemyTanks.size();i++){
						Tank enemyTank=gv.enemyTanks.get(i);
						if(enemyTank.blood<=0){				//这个坦克已经没有血了，删除之
							int score=enemyTank.rawBlood;
							gv.score+=score;
							gv.leftEnemyTanks--;
							gv.screenEnemyTanks--;
							gv.enemyTanks.remove(i);
							Log.d("EnemyTanks","敌人坦克剩余数："+gv.leftEnemyTanks);
							if(gv.hasBonu==false){				//如果还没有出现道具，则随机产生一个1-8号道具
								int getBonus=rand.nextInt(100);
								Log.d("Bonus","hasBonu="+gv.hasBonu+" getBonus="+getBonus);
								if(getBonus<20){
									int typeBonus=rand.nextInt(8)+1;
									gv.hasBonu=true;
									gv.bonu=new Bonus(gv,typeBonus,enemyTank.row,enemyTank.col);
								}
							}
						}											
					}
				}			
				if(gv.leftEnemyTanks<=0){		//如果敌人没有坦克了，则过了一关
					gv.father.myHandler.sendEmptyMessage(2);
				}	
				if(gv.enemyTanks.size()<6 && gv.leftEnemyTanks>=6){	//如果屏幕上的坦克数小于6且还有敌方坦克，产生一个坦克
					int nRand=rand.nextInt(15)+10;					//随机发子弹周期
					float vFire=nRand/100.0f;
					//new出一个坦克，参数分别表示：GameView，敌人坦克类型，出生地，初始前进方向，速度，发出子弹速度，杀伤力
					Tank oneTank;
					oneTank=new Tank(gv,rand.nextInt(2)+1,rand.nextInt(3)+1,2,1,vFire,500+rand.nextInt(500));
					gv.enemyTanks.add(oneTank);
					gv.screenEnemyTanks++;
					Log.d("screenEnemyTanks","screenEnemyTanks:"+gv.screenEnemyTanks);
				}
				if(!gv.hasGetClockBonu){										//如果没有接到时钟道具，则移动坦克，处理发弹
					synchronized (gv.enemyTanks) {
						for(i=0;i<gv.enemyTanks.size();i++){					//移动坦克，处理坦克发子弹
							Tank oneTank=gv.enemyTanks.get(i);
							oneTank.autoFire();						
							boolean has=false;
							for(int j=0;j!=i && j<gv.enemyTanks.size();j++){	//第i个坦克没有与别的坦克发生碰撞
								if(oneTank.hasCrashWithOtherTank(gv.enemyTanks.get(j))){
									has=true;
									break;
								}									
							}
							if(oneTank.hasCrashWithOtherTank(gv.myTank)){
								has=true;
							}
							if(!has){
								oneTank.autoMoveTank();	
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
