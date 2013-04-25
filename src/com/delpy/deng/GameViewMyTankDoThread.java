package com.delpy.deng;

import android.util.Log;

/*
 * 线程类：用来处理我方坦克的移动、生成、碰撞检测及发弹等
 */
public class GameViewMyTankDoThread extends Thread {
	GameView gv;
	boolean flag=false;
	int sleepSpan=50;
	public GameViewMyTankDoThread(GameView father){
		this.gv=father;
		flag=true;
	}
	public void run(){
		while(flag){
			if(!gv.father.isPause){
				if(!gv.gameOver){		//如果还没有gameOver
					if(!gv.hasGetAliveBonu && gv.myTank.crashWithOtherBullets()){	//在非无敌状态下，检测是否与敌方子弹发生碰撞
						if(gv.myTank.blood<=0){
							gv.myTank=null;
							if(gv.nMyTanks<=0){					//如果没坦克了，则gameOver
								gv.gameOver=true;	
								gv.noMoreTanks=true;
								Log.d("GameOver","GameOver "+gv.gameOver);
								gv.father.isGameOver=true;
								gv.father.myHandler.sendEmptyMessage(200);
								continue;
							}
						}
					}
					if((gv.myTank==null || (gv.myTank!=null && gv.myTank.blood<=0)) && gv.nMyTanks>0){	//如果我方坦克血为0,还有坦克，则new一个
						gv.initMyTank(gv.nowGuanKa);
					}
					if(gv.hasBonu==true && gv.bonu!=null && gv.myTank.hasGetABonu()){			//如果吃到了一个Bonus，进行处理
						synchronized (gv.bonu) {
							switch(gv.bonu.type){			//根据Bonus的类型进行相应的处理
							case 1:							//时钟，敌人目前屏幕上所有的子弹消失，2分钟内不能移动，不能发弹
								gv.hasGetClockBonu=true;
								synchronized (gv.bulletsList) {
									for(int i=0;i<gv.bulletsList.size();i++){
										Bullet oneBullet=gv.bulletsList.get(i);
										oneBullet=null;
									}
									gv.bulletsList.clear();
								}
								new Thread(){						//2分钟后，敌人可以移动
									public void run(){
										try{
											Thread.sleep(1000*120);
										}catch(Exception e){
											e.printStackTrace();
										}
										gv.hasGetClockBonu=false;
									}
								}.start();
								break;
							case 2:							//炸弹，目前屏幕上所有的敌人坦克均死亡
								synchronized (gv.enemyTanks) {
									int nEnemyTanks=gv.enemyTanks.size();
									for(int i=0;i<gv.enemyTanks.size();i++){
										Tank oneTank=gv.enemyTanks.get(i);
										oneTank=null;										
									}
									gv.enemyTanks.clear();
									gv.screenEnemyTanks=0;
									gv.leftEnemyTanks-=nEnemyTanks;
								}
								break;								
							case 3:							//铲子，将自己的老家用金刚石保护起来，只保护2分钟
								synchronized (gv.maps) {
									gv.makeTargetWith(2);		//调用函数，让自己的老家的四周用2取代
								}
								new Thread(){
									public void run(){
										try{
											Thread.sleep(1000*120);
										}catch(Exception e){
											e.printStackTrace();
										}
										gv.makeTargetWith(1);
									}
								}.start();
								break;
							case 4:								//坦克，我方坦克数加一
								gv.nMyTanks++;
								break;
							case 5:								//我方坦克加满血
								gv.myTank.blood=750;
								break;
							case 6:								//无敌状态
								gv.hasGetAliveBonu=true;
								new Thread(){						//2分钟后，无敌状态消失
									public void run(){
										try{
											Thread.sleep(1000*120);
										}catch(Exception e){
											e.printStackTrace();
										}
										gv.hasGetAliveBonu=false;
									}
								}.start();
								break;
							case 7:									//轮船
								gv.hasGetShipBonu=true;
								new Thread(){						//2分钟后，无敌状态消失
									public void run(){
										try{
											Thread.sleep(1000*120);
										}catch(Exception e){
											e.printStackTrace();
										}
										gv.hasGetShipBonu=false;
										synchronized (gv.myTank) {
											gv.myTank.row=38;
											gv.myTank.col=11;
										}
									}
								}.start();
								break;
							case 8:									//星星，攻击力变为1000
								gv.hasGetPowerBonu=true;
								new Thread(){						//2分钟后，攻击力重新变为500
									public void run(){
										try{
											Thread.sleep(1000*120);
										}catch(Exception e){
											e.printStackTrace();
										}
										gv.hasGetPowerBonu=false;
									}
								}.start();
								break;
							}
						}
						gv.bonu.status=2;					//状态2：Bonus已经被吃了，另有线程负责产生一个新的Bonus
					}
					if(gv.myTank!=null){
						if(gv.father.keyCode==62){			//用户按的空白键，发射子弹，直接发射
							gv.father.keyCode=0;
							gv.myTank.manualFire();
						}else{								//方向键
							boolean hasCrashOtherTank=false;
							synchronized (gv.enemyTanks) {	//检测是否与某个坦克发生了碰撞
								for(int i=0;i<gv.enemyTanks.size();i++){
									if(gv.myTank.hasCrashWithOtherTank(gv.enemyTanks.get(i))){
										hasCrashOtherTank=true;
										break;
									}
								}
							}
							switch(gv.father.keyCode){
							case 19:						//上
								if(gv.myTank.dir!=1)		//原来的方向不是向上，则只转变方向为向上
									gv.myTank.dir=1;
								else if(!hasCrashOtherTank)	//否则，如果前面没有坦克，则试图向上走
									gv.myTank.moveUp();
								gv.father.keyCode=0;
								break;
							case 20:						//下
								if(gv.myTank.dir!=2)
									gv.myTank.dir=2;
								else if(!hasCrashOtherTank)
									gv.myTank.moveDown();
								gv.father.keyCode=0;
								break;
							case 21:						//左
								if(gv.myTank.dir!=3)
									gv.myTank.dir=3;
								else if(!hasCrashOtherTank)
									gv.myTank.moveLeft();
								gv.father.keyCode=0;
								break;
							case 22:						//右
								if(gv.myTank.dir!=4)
									gv.myTank.dir=4;
								else if(!hasCrashOtherTank)
									gv.myTank.moveRight();
								gv.father.keyCode=0;
								break;
							}						
						}
					}
				}
			}
			try{
				Thread.sleep(sleepSpan);
			}catch(Exception e){
				
			}
		}
	}	
}
