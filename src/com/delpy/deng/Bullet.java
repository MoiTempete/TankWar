package com.delpy.deng;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/*
 * 子弹类
 */
public class Bullet {
	GameView gv;
	int ID;					//子弹的ID号
	int enemy;				//敌我的识别，1和2表示敌人发出的子弹，0表示自己发出的子弹
	int row,col;			//子弹中心原点坐标，注意不是以像素为单位，而是以块为单位，就是子弹在网格中的行号与列号
							//每块都是10*10像素大小，行号与列号是很容易转换为真正的坐标的：X=(col+1)*10，Y=(row+1)*10
	int R;					//子弹的半径，分别为2、3、5,默认为2，所对应的攻击力分别为：300、500、1000
	int dir;				//子弹的运动方向：1234分别表示上、下、左、右
	int v;					//子弹的运动速度，以块为单位，即每个计时周期内向前移动多少块（10像素）
	int power;				//攻击力，该 子弹碰到坦克后，坦克一次减多少血
	Paint bulletPaint;		//敌方子弹Paint
	Paint myBulletPaint;	//我方子弹Paint
	public Bullet(GameView gv,int enemy,int row, int col, int dir, int v, int R){
		this.gv=gv;
		this.enemy=enemy;
		this.row=row;
		this.col=col;
		this.dir=dir;
		this.v=v;
		this.R=R;
		this.power=(R==2?200:R==3?500:1000);
		bulletPaint=new Paint();
		bulletPaint.setColor(Color.WHITE);
		myBulletPaint=new Paint();
		myBulletPaint.setColor(Color.BLACK);
	}
	public void draw(Canvas canvas){		//画出子弹
		canvas.drawCircle(col*10, row*10, R, bulletPaint);
	}
	public void drawMyBullet(Canvas canvas){
		canvas.drawCircle(col*10, row*10                                                      , R, myBulletPaint);
	}
	public void autoMove(){					//子弹自动移动
		switch(dir){
		case 1:
			row--;break;
		case 2:
			row++;break;
		case 3:
			col--;break;
		case 4:
			col++;break;
		}
	}
	public boolean hasFlyOutOfScreen(){
		boolean hasCrash=false;
		if(row<0 || row>39 || col<0 || col>31){	//飞出了屏幕
			hasCrash=true;			
		}
		return hasCrash;
	}
	public boolean hasCrashWall(){	//检测本子弹是否碰到了墙壁、金刚石，是否飞出了屏幕，是否打中了目标体
		int i,j;
		boolean hasCrash=false;
		if(row<0 || row>39 || col<0 || col>31){
			return false;
		}
		if((row==38 && col==15) || (row==38 && col==16) ||
				(row==39 && col==15) || (row==39 && col==16)){	//打中了目标体，Game Over
			hasCrash=true;
			gv.maps[38][15]=0;
			gv.father.myHandler.sendEmptyMessage(100);			//给主Activity发送100号消息
			gv.gameOver=true;
			return hasCrash;
		}
		if(gv.maps[row][col]==1){
			gv.maps[row][col]=0;
			hasCrash=true;
		}else if(gv.maps[row][col]==2){
			hasCrash=true;
		}		
		switch(dir){
		case 1:
		case 2:
			if(col-1>=0){
				if(gv.maps[row][col-1]==1){
					gv.maps[row][col-1]=0;
					hasCrash=true;
				}
				if(gv.maps[row][col-1]==2 ){
					hasCrash=true;
				}
			}
			break;
		case 3:
		case 4:	
			if(row-1>=0){
				if(gv.maps[row-1][col]==1){
					gv.maps[row-1][col]=0;
					hasCrash=true;
				}
				if(gv.maps[row-1][col]==2)
					hasCrash=true;
			}
			break;
		}
		return hasCrash;
	}
	public boolean hasCrashWithOtherBullet(){
		boolean has=false;
		if(enemy==0){			//我方子弹负责检测是否与敌人的子弹相遇
			synchronized(gv.bulletsList){
				for(int i=0;i<gv.bulletsList.size();i++){
					Bullet oneBullet=gv.bulletsList.get(i);
					if(row==oneBullet.row && col==oneBullet.col){	//如果本子弹与敌人某子弹相遇
						gv.bulletsList.remove(i);
						has=true;
						break;
					}else{
						switch(dir){		//如果子弹方向相反，且相差一格，也认为已经碰到了。
						case 1:
							if(row>0 && oneBullet.row==row-1 && oneBullet.col==col && oneBullet.dir==2){
								gv.bulletsList.remove(i);
								has=true;
								return has;
							}
							break;
						case 2:
							if(row<39 && oneBullet.row==row+1 && oneBullet.col==col && oneBullet.dir==1){
								gv.bulletsList.remove(i);
								has=true;
								return has;
							}
							break;
						case 3:
							if(col>0 && oneBullet.col==col-1 && oneBullet.row==row && oneBullet.dir==4){
								gv.bulletsList.remove(i);
								has=true;
								return has;
							}
							break;
						case 4:
							if(col<31 && oneBullet.col==col+1 && oneBullet.row==row && oneBullet.dir==3){
								gv.bulletsList.remove(i);
								has=true;
								return has;
							}
							break;
						}
					}
				}
			}
		}
		return has;
	}
}
