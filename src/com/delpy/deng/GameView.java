package com.delpy.deng;

import java.util.LinkedList;
import java.util.List;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback{
	public static final int maxMyTanks=4;
	TankWarActivity father;
	GameViewDrawThread gvdt=null;
	GameViewEnemyTankGoThread gvetgt=null;
	GameViewBulletGoThread gvbgt=null;
	GameViewMyTankDoThread gvmtdt=null;
	GameViewBonuDoThread gvbdt=null;
	Bonus bonu=null;				//屏幕上的道具
	boolean hasBonu;				//屏幕上是否有道具
	boolean hasGetClockBonu;		//是否已经吃到了时钟道具？如果为true，则敌人的坦克将暂停2分钟
	boolean hasGetAliveBonu;		//是否已经吃到了无敌道具？如果为true，则我方坦克不进行与敌人的子弹的碰撞检测
	boolean hasGetShipBonu;			//是否吃到了轮船道具？如果为true，则我方坦克可以过河，同时，坦克图像更改为全黄色
	boolean hasGetPowerBonu;		//是否吃到了星星道具？如果为true，我方坦克的子弹power为1000,否则为500
	Tank myTank=null;				//我方坦克
	int nMyTanks;					//我方坦克剩余数，当此数为0时，便Game Over了
	boolean noMoreTanks;			//我方已经没有坦克了
	boolean gameOver;				//是否已经Game Over了
	int maxEnemyTanks;				//本关敌方坦克最大总数
	int leftEnemyTanks;				//敌方坦克所剩余的坦克数
	int screenEnemyTanks;			//目前屏幕上还有几个坦克，一般每次屏幕上应该有6个坦克
	List <Tank> enemyTanks;			//敌方坦克数组
	List <Bullet> bulletsList;		//敌方坦克发出的子弹链表
	List <Bullet> myBulletsList;	//我方坦克发出的子弹链表
	int[][] maps;
	int allGuanKa,nRows,nCols;		//allGuanKa为所有的关卡数，后面两个为每个关卡的行数与列数
	int nowGuanKa;					//现在是第几关，从0开始算起
	long score;						//分数
	Resources r;
	Bitmap bmpAnyTank1Up,bmpAnyTank1Down,bmpAnyTank1Left,bmpAnyTank1Right;
	Bitmap bmpAnyTank2Up,bmpAnyTank2Down,bmpAnyTank2Left,bmpAnyTank2Right;
	Bitmap bmpMyTankUp,bmpMyTankDown,bmpMyTankLeft,bmpMyTankRight;
	Bitmap bmpMyTankRiverUp,bmpMyTankRiverDown,bmpMyTankRiverLeft,bmpMyTankRiverRight;
	Bitmap bmpMyTankAliveUp,bmpMyTankAliveDown,bmpMyTankAliveLeft,bmpMyTankAliveRight;
	Bitmap bmpBackground,bmpDiamond,bmpLeef,bmpTarget,bmpWall,bmpWater;
	Paint copyrightPaint,infoBackgroundPaint,infoLinePaint,gameOverPaint,touchTextPaint;
	Paint infoTankPaint,infoMenuPaint,infoScorePaint,infoPausePaint;
	Paint pressRectPaint;			//玩家触摸屏幕相当于按键用的Paint
	public GameView(TankWarActivity context,int guanKa){
		super(context);
		this.father=context;
		getHolder().addCallback(this);
		initBitmaps();
		initPaints();
		allGuanKa=Maps.maps.length;
		nowGuanKa=guanKa;
		gameOver=false;
		noMoreTanks=false;
		nMyTanks=maxMyTanks;
		score=0;
		hasBonu=false;
		hasGetClockBonu=false;
		hasGetAliveBonu=false;
		hasGetShipBonu=false;
		hasGetPowerBonu=false;
		bulletsList=new LinkedList<Bullet>();
		myBulletsList=new LinkedList<Bullet>();
		initMaps(nowGuanKa);
		initTanks(nowGuanKa);
		initMyTank(nowGuanKa);
		gvdt=new GameViewDrawThread(this,getHolder());
		gvetgt=new GameViewEnemyTankGoThread(this);
		gvbgt=new GameViewBulletGoThread(this);
		gvmtdt=new GameViewMyTankDoThread(this);
		gvbdt=new GameViewBonuDoThread(this);
	}
	public void initBitmaps(){
		r=getResources();
		bmpAnyTank1Up=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.anymtank1up);
		bmpAnyTank1Down=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.anymtank1down);
		bmpAnyTank1Left=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.anymtank1left);
		bmpAnyTank1Right=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.anymtank1right);
		bmpAnyTank2Up=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.anymtank2up);
		bmpAnyTank2Down=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.anymtank2down);
		bmpAnyTank2Left=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.anymtank2left);
		bmpAnyTank2Right=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.anymtank2right);
		bmpMyTankUp=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.mytankup);
		bmpMyTankDown=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.mytankdown);
		bmpMyTankLeft=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.mytankleft);
		bmpMyTankRight=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.mytankright);
		bmpMyTankRiverUp=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.mytankriverup);
		bmpMyTankRiverDown=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.mytankriverdown);
		bmpMyTankRiverLeft=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.mytankriverleft);
		bmpMyTankRiverRight=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.mytankriverright);
		bmpMyTankAliveUp=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.mytankaliveup);
		bmpMyTankAliveDown=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.mytankalivedown);
		bmpMyTankAliveLeft=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.mytankaliveleft);
		bmpMyTankAliveRight=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.mytankaliveright);
		bmpBackground=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.background);
		bmpDiamond=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.diamond);
		bmpLeef=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.leef);
		bmpTarget=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.target);
		bmpWall=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.wall);
		bmpWater=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.water);
	}
	public void initPaints(){
		copyrightPaint=new Paint();
		copyrightPaint.setTextSize(12);
		copyrightPaint.setColor(Color.WHITE);
		infoBackgroundPaint=new Paint();
		infoBackgroundPaint.setColor(Color.BLACK);
		infoLinePaint=new Paint();
		infoLinePaint.setColor(Color.BLUE);
		gameOverPaint=new Paint();
		gameOverPaint.setColor(Color.RED);
		gameOverPaint.setTextSize(50);
		touchTextPaint=new Paint();
		touchTextPaint.setColor(Color.BLACK);
		touchTextPaint.setTextSize(20);
		infoTankPaint=new Paint();
		infoTankPaint.setColor(Color.YELLOW);
		infoTankPaint.setTextSize(15);
		infoMenuPaint=new Paint();
		infoMenuPaint.setColor(Color.GREEN);
		infoMenuPaint.setTextSize(15);
		infoScorePaint=new Paint();
		infoScorePaint.setColor(Color.rgb(252, 206, 129));
		infoScorePaint.setTextSize(15);
		infoPausePaint=new Paint();
		infoPausePaint.setColor(Color.RED);
		infoPausePaint.setTextSize(30);
		pressRectPaint=new Paint();
		pressRectPaint.setColor(Color.BLACK);	//设置画笔的粗细
		pressRectPaint.setStrokeWidth(3);
	}
	public void initMaps(int guan){					//从地图类中读取本关地图
		nRows=Maps.maps[guan].length;
		nCols=Maps.maps[guan][0].length;
		int i,j;
		maps=new int[nRows][nCols];
		for(i=0;i<nRows;i++){
			for(j=0;j<nCols;j++){
				maps[i][j]=Maps.maps[guan][i][j];
			}
		}
	}
	public void initMyTank(int guan){				//初始化我方坦克
		myTank=new Tank(this,0,4,1,1,0.5f,750);		//我方坦克，4号位置出生，发子弹速度0.5,攻击力750
		nMyTanks--;
	}
	public void initTanks(int guan){				//初始化坦克
		maxEnemyTanks=20;							//敌方坦克总数为20
		leftEnemyTanks=maxEnemyTanks;				//剩余数也为20
		enemyTanks=new LinkedList<Tank>();			//初始化数组
		screenEnemyTanks=0;
	}
	public void makeTargetWith(int n){				//让自己的老家的四周用n来取代
		for(int i=36;i<40;i++){
			for(int j=13;j<19;j++){
				maps[i][j]=n;
			}
		}
		maps[38][15]=5;
		maps[38][16]=maps[39][15]=maps[39][16]=0;
	}
	public void doDraw(Canvas canvas){
		canvas.drawBitmap(bmpBackground, 0,0, null);
		drawMaps(canvas);
		drawInfo(canvas);
		drawTankInfo(canvas);
		if(father.isPause){
			drawPauseInfo(canvas);
		}
		if(this.nMyTanks<=0 || this.gameOver || this.noMoreTanks){
			drawGameOver(canvas);
		}
		drawTanks(canvas);
		if(this.nMyTanks>0 && this.myTank!=null){
			drawMyTank(canvas);
		}
		drawBonus(canvas);
		drawBullets(canvas);
		drawMyBullets(canvas);	
		drawPressRect(canvas);
	}
	public void drawMaps(Canvas canvas){						//画地图信息
		int i,j;
		int X,Y;		//画图像的起始坐标		
		for(i=0;i<nRows;i++){
			for(j=0;j<nCols;j++){
				if(maps[i][j]!=0){
					X=j*10;
					Y=i*10;
					switch(maps[i][j]){
					case 1:
						canvas.drawBitmap(bmpWall, X, Y, null);
						break;
					case 2:
						canvas.drawBitmap(bmpDiamond, X, Y, null);
						break;
					case 3:
						canvas.drawBitmap(bmpLeef, X, Y, null);
						break;
					case 4:
						canvas.drawBitmap(bmpWater, X, Y, null);
						break;
					case 5:
						canvas.drawBitmap(bmpTarget, X, Y, null);
						break;
					}
				}
			}
		}
	}
	public void drawInfo(Canvas canvas){									//画各种信息
		canvas.drawRect(new Rect(0,400,320,480),infoLinePaint);
		canvas.drawRect(new Rect(5,405,315,475), infoBackgroundPaint);
		canvas.drawText("坦克大战，制作：MoiTempete", 10, 455, copyrightPaint);
		canvas.drawText("版权所有：MoiTempete", 10, 470, copyrightPaint);
		canvas.drawText("按Menu键退出",200,440,infoMenuPaint);
	}
	public void drawTanks(Canvas canvas){
		int i,nEnemyTanks=enemyTanks.size();
		Tank oneTank;
		for(i=0;i<nEnemyTanks;i++){
			oneTank=enemyTanks.get(i);
			oneTank.draw(canvas);
		}
	}
	public void drawMyTank(Canvas canvas){
		if(myTank!=null){
			myTank.draw(canvas);
		}
	}
	public void drawBullets(Canvas canvas){
		Bullet oneBullet;
		int nBullets=this.bulletsList.size();		//目前子弹数
		for(int i=0;i<nBullets;i++){
			oneBullet=(Bullet)bulletsList.get(i);
			oneBullet.draw(canvas);
		}
	}
	public void drawMyBullets(Canvas canvas){
		Bullet oneBullet;
		int nBullets=this.myBulletsList.size();		//目前子弹数
		for(int i=0;i<nBullets;i++){
			oneBullet=(Bullet)myBulletsList.get(i);
			oneBullet.drawMyBullet(canvas);
		}
	}
	public void drawGameOver(Canvas canvas){
		canvas.drawText("GAME OVER", 40, 200, gameOverPaint);
		canvas.drawText("请单击Menu返回菜单", 120, 260, touchTextPaint);
	}
	public void drawTankInfo(Canvas canvas){
		canvas.drawText("我方："+nMyTanks, 10, 420, infoTankPaint);
		canvas.drawText("敌人："+this.leftEnemyTanks, 120, 420, infoTankPaint);
		canvas.drawText("关卡："+(this.nowGuanKa+1), 240, 420, infoTankPaint);
		canvas.drawText("分数："+score, 10,440, infoScorePaint);
		canvas.drawText("生命："+myTank.blood, 120, 440, infoScorePaint);
	}
	public void drawPauseInfo(Canvas canvas){
		canvas.drawText("游戏已暂停", 100, 160, infoPausePaint);
		canvas.drawText("按回车键恢复游戏", 40, 200, infoPausePaint);
	}
	public void drawBonus(Canvas canvas){
		if(hasBonu && bonu!=null){
			bonu.draw(canvas);
		}
	}
	public void drawPressRect(Canvas canvas){
//		canvas.drawRect(new Rect(240,200,280,220), pressRectPaint);
//		Path pathAngle=new Path();
//		pathAngle.moveTo(280,190);
//		pathAngle.lineTo(280,230);
//		pathAngle.lineTo(320, 210);
//		pathAngle.close();
//		canvas.drawPath(pathAngle,pressRectPaint);
		canvas.drawLine(240,200,240, 220,pressRectPaint);
		canvas.drawLine(240,200,280,200,pressRectPaint);
		canvas.drawLine(240,220,280,220,pressRectPaint);
		canvas.drawLine(280,190,280,230,pressRectPaint);
		canvas.drawLine(280,190,320,210,pressRectPaint);
		canvas.drawLine(280,230,320,210,pressRectPaint);
		canvas.drawLine(0,210,40, 190,pressRectPaint);
		canvas.drawLine(0,210,40,230,pressRectPaint);
		canvas.drawLine(40,190,40,230,pressRectPaint);
		canvas.drawLine(40,200,80,200,pressRectPaint);
		canvas.drawLine(40,220,80,220,pressRectPaint);
		canvas.drawLine(80,200,80,220,pressRectPaint);
		canvas.drawLine(160,100,140, 140,pressRectPaint);
		canvas.drawLine(160,100,180,140,pressRectPaint);
		canvas.drawLine(140,140,180,140,pressRectPaint);
		canvas.drawLine(150,140,150,180,pressRectPaint);
		canvas.drawLine(150,180,170,180,pressRectPaint);
		canvas.drawLine(170,180,170,140,pressRectPaint);
		canvas.drawLine(150,240,170,240,pressRectPaint);
		canvas.drawLine(150,240,150,280,pressRectPaint);
		canvas.drawLine(170,240,170,280,pressRectPaint);
		canvas.drawLine(140,280,160,320,pressRectPaint);
		canvas.drawLine(160,320,180,280,pressRectPaint);
		canvas.drawLine(180,280,140,280,pressRectPaint);
	}
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}
	public void surfaceCreated(SurfaceHolder holder) {
		if(gvdt!=null && !gvdt.isAlive()){
			gvdt.start();
		}
		if(gvetgt!=null && !gvetgt.isAlive()){
			gvetgt.start();
		}
		if(gvbgt!=null && !gvbgt.isAlive()){
			gvbgt.start();
		}
		if(gvmtdt!=null && !gvmtdt.isAlive()){
			gvmtdt.start();
		}
		if(gvbdt!=null && !gvbdt.isAlive()){
			gvbdt.start();
		}
	}
	public void surfaceDestroyed(SurfaceHolder holder) {
		if(gvdt!=null){
			gvdt.flag=false;
			gvdt=null;
		}
		if(gvetgt!=null){
			gvetgt.flag=false;
			gvetgt=null;
		}
		if(gvbgt!=null){
			gvbgt.flag=false;
			gvbgt=null;
		}
		if(gvmtdt!=null){
			gvmtdt.flag=false;
			gvmtdt=null;
		}
		if(gvbdt!=null){
			gvbdt.flag=false;
			gvbdt=null;
		}
	}
}
