package com.delpy.deng;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

/*
 * 坦克类，所有的坦克均从该类继承下来的
 */
public class Tank {
	GameView gv;
	int enemy;				//敌我的识别，1和2表示敌人，0表示自己
	int row,col;			//坦克坐标，注意不是以像素为单位，而是以块为单位，就是坦克在风格中的行号与列号
							//每块都是10*10像素大小，行号与列号是很容易转换为真正的坐标的：X=col*10，Y=row*10
	int birthLocation;		//出生时的位置标志，一共有五个地方可出生坦克12345分别表示左上角、上正中央、右上角、城堡左、城堡右
	int dir;				//坦克的运动方向：1234分别表示上、下、左、右
	int v;					//坦克的运动速度，以块为单位，即每个计时周期内向前移动多少块（10像素）
	float vFire;			//坦克打子弹的频率，每个计时周期内打多少个子弹，是一个小数，如0.25,则表示每4个周期打一个子弹
	float fire;				//在每个周期内增加vFire值，当该值大于1时便发射一个子弹，并让自己减1,从而可控制发射子弹的频率
	Bitmap bmp;				//坦克的图片，根据类型、运动方向不同而不同，图片资源已经在GameView类中进行了定义与初始化
	int blood;				//该坦克的血，每碰到一棵子弹，blood便减若干血，当血减为0后，该坦克便死亡
	int rawBlood;
	Random rand;			//随机产生 器
	public Tank(GameView gv,int enemy,int birthLocation,int dir,int v,float vFire,int blood){
		this.gv=gv;
		this.enemy=enemy;
		switch(birthLocation){
		case 1:						//左上角
			this.row=this.col=0;
			break;
		case 2:						//上正中央
			this.row=0;
			this.col=15;
			break;
		case 3:						//上右角
			this.row=0;
			this.col=30;
			break;
		case 4:						//城堡左
			this.row=38;
			this.col=11;
			break;
		case 5:						//城堡右
			this.row=38;
			this.col=19;
			break;
		}
		this.dir=dir;
		this.v=v;
		this.vFire=vFire;
		this.fire=0;
		this.blood=blood;
		this.rawBlood=blood;
		bmp=getTankBitmap(enemy,dir);
		rand=new Random(System.currentTimeMillis());		//用当前时间做种子，new出随机生成器
	}
	public Bitmap getTankBitmap(int enemy,int dir){
		Bitmap bmp=null;
		if(enemy==0){						//我方坦克
			if(gv.hasGetShipBonu){			//如果得到轮船道具
				switch(dir){
				case 1:
					bmp=gv.bmpMyTankRiverUp;break;
				case 2:
					bmp=gv.bmpMyTankRiverDown;break;
				case 3:
					bmp=gv.bmpMyTankRiverLeft;break;
				case 4:
					bmp=gv.bmpMyTankRiverRight;break;
				}
			}else if(gv.hasGetAliveBonu){
				switch(dir){
				case 1:
					bmp=gv.bmpMyTankAliveUp;break;
				case 2:
					bmp=gv.bmpMyTankAliveDown;break;
				case 3:
					bmp=gv.bmpMyTankAliveLeft;break;
				case 4:
					bmp=gv.bmpMyTankAliveRight;break;
				}
			}else{
				switch(dir){
				case 1:
					bmp=gv.bmpMyTankUp;break;
				case 2:
					bmp=gv.bmpMyTankDown;break;
				case 3:
					bmp=gv.bmpMyTankLeft;break;
				case 4:
					bmp=gv.bmpMyTankRight;break;
				}
			}
		}else if(enemy==1){					//敌方坦克1
			switch(dir){
			case 1:
				bmp=gv.bmpAnyTank1Up;break;
			case 2:
				bmp=gv.bmpAnyTank1Down;break;
			case 3:
				bmp=gv.bmpAnyTank1Left;break;
			case 4:
				bmp=gv.bmpAnyTank1Right;break;
			}
		}else if(enemy==2){					//敌方坦克2
			switch(dir){
			case 1:
				bmp=gv.bmpAnyTank2Up;break;
			case 2:
				bmp=gv.bmpAnyTank2Down;break;
			case 3:
				bmp=gv.bmpAnyTank2Left;break;
			case 4:
				bmp=gv.bmpAnyTank2Right;break;
			}
		}
		return bmp;
	}
	public void draw(Canvas canvas){	//在屏幕上将自己画出来		
		bmp=getTankBitmap(enemy,dir);
		canvas.drawBitmap(bmp, col*10, row*10, null);
	}
	public void autoMoveTank(){				//每个周期自动移动自己
		int newRand=rand.nextInt(100);		//产生0-99之间的整数
		if(newRand<85){						//80%的机率沿着原来的方向走
			
		}else{								//否则更改为下一方向
			dir=rand.nextInt(4)+1;	//得到新的方向
		}
		switch(dir){						//判断前方是否有路可走，如果无路，更改方向走之
		case 1:								//向上走
			moveUp();
			break;
		case 2:
			moveDown();
			break;
		case 3:
			moveLeft();
			break;
		case 4:
			moveRight();
			break;           
		}
	}
	public void autoFire(){					//每个周期自动发出子弹
		this.fire+=this.vFire;				//计算是否要发子弹，当大于等于1时，便发出子弹
		if(fire>=1.0f){
			synchronized(gv.bulletsList){
				int nRand=rand.nextInt(3);
				int R=nRand==0?2:nRand==1?3:5;
				int bRow,bCol;
				if(dir==1 || dir==2){		//如果向上或向下，子弹的row与col
					bRow=row;
					bCol=col+1;
				}else {
					bRow=row+1;
					bCol=col;
				}
				Bullet oneBullet=new Bullet(gv,enemy,bRow,bCol,dir,1,R);
				gv.bulletsList.add(oneBullet);		//将该子弹插入到子弹链表中
				fire-=1.0;
			}			
		}
	}
	public void manualFire(){				//我主坦克手动发出子弹
		this.fire+=this.vFire;
		if(fire>=1.0f){
			synchronized (gv.myBulletsList) {
				Bullet oneBullet;
				int bRow,bCol;
				if(dir==1 || dir==2){		//如果向上或向下，子弹的row与col
					bRow=row;
					bCol=col+1;
				}else {
					bRow=row+1;
					bCol=col;
				}
				if(!gv.hasGetPowerBonu){
					oneBullet=new Bullet(gv,0,bRow,bCol,dir,1,3);
				}else{
					oneBullet=new Bullet(gv,0,bRow,bCol,dir,1,5);
				}
				gv.myBulletsList.add(oneBullet);
				fire-=1.0;
			}			
		}
	}
	public void moveUp(){		//如果能向上走，则向上走一步
		if(enemy!=0 || (enemy==0 && !gv.hasGetShipBonu)){	//敌人坦克，或者是我方坦克但是没有接到轮船道具
			if((row>0 && (gv.maps[row-1][col]==0 || gv.maps[row-1][col]==3))&&
					(col+1<=31 && (gv.maps[row-1][col+1]==0 || gv.maps[row-1][col+1]==3)) ){
				row--;
			}
		}		
		if(enemy==0 && gv.hasGetShipBonu){		//如果我方坦克取得了轮船道具，还可以过代号为4的小河
			if((row>0 && (gv.maps[row-1][col]==0 || gv.maps[row-1][col]==3 || gv.maps[row-1][col]==4))&&
					(col+1<=31 && (gv.maps[row-1][col+1]==0 || gv.maps[row-1][col+1]==3 || gv.maps[row-1][col+1]==4)) ){
				row--;
			}
		}
	}
	public void moveDown(){
		if(enemy!=0 || (enemy==0 && !gv.hasGetShipBonu)){	//敌人坦克，或者是我方坦克但是没有接到轮船道具
			if(row<38 && (gv.maps[row+2][col]==0 || gv.maps[row+2][col]==3) &&
					(col+1<=31 && (gv.maps[row+2][col+1]==0 || gv.maps[row+2][col+1]==3)) ){
				row++;
			}
		}
		if(enemy==0 && gv.hasGetShipBonu){		//如果我方坦克取得了轮船道具，还可以过代号为4的小河
			if(row<38 && (gv.maps[row+2][col]==0 || gv.maps[row+2][col]==3 || gv.maps[row+2][col]==4) &&
					(col+1<=31 && (gv.maps[row+2][col+1]==0 || gv.maps[row+2][col+1]==3 || gv.maps[row+2][col+1]==4)) ){
				row++;
			}
		}
	}
	public void moveLeft(){
		if(enemy!=0 || (enemy==0 && !gv.hasGetShipBonu)){	//敌人坦克，或者是我方坦克但是没有接到轮船道具
			if(col>0 && (gv.maps[row][col-1]==0 || gv.maps[row][col-1]==3) &&
					(row<39 && (gv.maps[row+1][col-1]==0 || gv.maps[row+1][col-1]==3))){
				col--;
			}
		}
		if(enemy==0 && gv.hasGetShipBonu){		//如果我方坦克取得了轮船道具，还可以过代号为4的小河
			if(col>0 && (gv.maps[row][col-1]==0 || gv.maps[row][col-1]==3 || gv.maps[row][col-1]==4) &&
					(row<39 && (gv.maps[row+1][col-1]==0 || gv.maps[row+1][col-1]==3 || gv.maps[row+1][col-1]==4))){
				col--;
			}
		}
	}
	public void moveRight(){
		if(enemy!=0 || (enemy==0 && !gv.hasGetShipBonu)){	//敌人坦克，或者是我方坦克但是没有接到轮船道具
			if(col<30 && (gv.maps[row][col+2]==0 || gv.maps[row][col+2]==3) &&
				(row<39 && (gv.maps[row+1][col+2]==0 || gv.maps[row+1][col+2]==3))){
				col++;
			}
		}
		if(enemy==0 && gv.hasGetShipBonu){		//如果我方坦克取得了轮船道具，还可以过代号为4的小河
			if(col<30 && (gv.maps[row][col+2]==0 || gv.maps[row][col+2]==3 || gv.maps[row][col+2]==4) &&
					(row<39 && (gv.maps[row+1][col+2]==0 || gv.maps[row+1][col+2]==3 || gv.maps[row+1][col+2]==4))){
					col++;
				}
		}
		
	}
	public boolean crashWithOtherBullets(){			//检测是否与对方子弹碰撞了
		boolean hasCrash=false;
		if(enemy==0){								//自己是我主坦克
			synchronized (gv.bulletsList) {
				for(int i=0;i<gv.bulletsList.size();i++){		//与所有的敌方子弹进行检测
					int rEnemyBullet=gv.bulletsList.get(i).row;
					int cEnemyBullet=gv.bulletsList.get(i).col;
//					if(row==rEnemyBullet && col==cEnemyBullet || 		//如果四角上有子弹
//							row==rEnemyBullet && col==cEnemyBullet+1 ||
//							row==rEnemyBullet+1 && col==cEnemyBullet ||
//							row==rEnemyBullet+1 && col==cEnemyBullet+1 ){
//						this.blood-=gv.bulletsList.get(i).power;		//血减power（敌子弹的杀伤力）
//						Log.d("MyTankBlood","MyTankBlood="+this.blood);
//						gv.bulletsList.remove(i);						//敌方子弹清除
//						hasCrash=true;
//						break;
//					}
					int xEnemyBullet=cEnemyBullet*10;					//子弹中心坐标
					int yEnemyBullet=rEnemyBullet*10;
					int xMyTank=(col+1)*10;								//我方坦克中心坐标，注意位置
					int yMyTank=(row+1)*10;
					int realDistance=(xMyTank-xEnemyBullet)*(xMyTank-xEnemyBullet)+
						(yMyTank-yEnemyBullet)*(yMyTank-yEnemyBullet);	//两者实际距离的平方
					int minDistance=(10+gv.bulletsList.get(i).R)*(10+gv.bulletsList.get(i).R);	//两者最小距离的平方，一旦小于这个距离，就说明已经碰撞
					if(realDistance<=minDistance){						//如果发生了碰撞
						this.blood-=gv.bulletsList.get(i).power;		//血减power（敌子弹的杀伤力）
						Log.d("MyTankBlood","MyTankBlood="+this.blood);
						gv.bulletsList.remove(i);						//敌方子弹清除
						hasCrash=true;
						break;
					}
				}
			}			
		}else{										//敌方坦克
			synchronized (gv.myBulletsList) {
				for(int i=0;i<gv.myBulletsList.size();i++){
					int rMyBullet=gv.myBulletsList.get(i).row;
					int cMyBullet=gv.myBulletsList.get(i).col;
					int xMyBullet=cMyBullet*10;
					int yMyBullet=rMyBullet*10;
					int xEnemyTank=(col+1)*10;
					int yEnemyTank=(row+1)*10;
					int realDistance=(xEnemyTank-xMyBullet)*(xEnemyTank-xMyBullet)+
						(yEnemyTank-yMyBullet)*(yEnemyTank-yMyBullet);	//两者实习距离
					int minDistance=(10+gv.myBulletsList.get(i).R)*(10+gv.myBulletsList.get(i).R);			//两者最小距离，一旦小于这个距离，就说明已经碰撞
					if(realDistance<minDistance){
						this.blood-=gv.myBulletsList.get(i).power;
						gv.myBulletsList.remove(i);
						hasCrash=true;
						break;
					}
				}
			}
//			synchronized (gv.myBulletsList) {
//				for(int i=0;i<gv.myBulletsList.size();i++){
//					Bullet myBullets=gv.myBulletsList.get(i);
//					int rMyBullet=myBullets.row;
//					int cMyBullet=myBullets.col;
//					if(row==rMyBullet && col==cMyBullet || 		//如果四角上有子弹
//							row==rMyBullet && col==cMyBullet+1 ||
//							row==rMyBullet+1 && col==cMyBullet ||
//							row==rMyBullet+1 && col==cMyBullet+1 ){
//						this.blood-=myBullets.power;		//血减power（我方子弹的杀伤力）
//						Log.d("EnemyTankBlood","EnemyTankBlood="+this.blood);
//						gv.myBulletsList.remove(i);						//我方子弹清除
//						hasCrash=true;
//						break;
//					}
//				}
//			}			
		}
		return hasCrash;
	}
	public boolean hasCrashWithOtherTank(Tank oneTank){			//检测是否与别的坦克发生了碰撞，以决定是否还能前进
		boolean has=false;
		if(oneTank==null){
			return has;
		}
		switch(dir){
		case 1:
			if(row>1 && oneTank.row==row-2 && 
					((oneTank.col==col) || (col>0 && oneTank.col==col-1) || (col<31 && oneTank.col==col+1))){
				has=true;
				return has;
			}
			break;
		case 2:
			if(row<38 && oneTank.row==row+2 &&
					((oneTank.col==col) || (col>0 && oneTank.col==col-1) || (col<31 && oneTank.col==col+1))){
				has=true;
				return has;
			}
			break;
		case 3:
			if(col>1 && oneTank.col==col-2 &&
					((oneTank.row==row) || (row>0 && oneTank.row==row-1) || (row<39 && oneTank.row==row+1))){
				has=true;
				return has;
			}
			break;
		case 4:
			if(col<30 && oneTank.col==col+2 && 
					((oneTank.row==row) || (row>0 && oneTank.row==row-1) || (row<39 && oneTank.row==row+1))){
				has=true;
				return has;
			}
			break;
		}			
		return has;
	}
	public boolean hasGetABonu(){	//判断本坦克是否取得了一个奖品
		boolean has=false;
		synchronized (gv.bonu) {
			if(gv.hasBonu==true && gv.bonu!=null){
				if(row==gv.bonu.row && col==gv.bonu.col || row+1==gv.bonu.row && col==gv.bonu.col ||
						row==gv.bonu.row && col+1==gv.bonu.col || row+1==gv.bonu.row && col+1==gv.bonu.col)
					has=true;
			}
		}		
		return has;
	}
}
