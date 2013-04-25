package com.delpy.deng;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

/*
 * 游戏道具类，当我方坦克碰到道具后，产生相应的效果
 * 道具类型type：1、定时器；2、炸弹；3、铲子；4、坦克；5、食物（加满血）；6、无敌；7、轮船；8、星星（吃了武器攻击力加强）
 * 每个道具由我方坦克击毁敌方坦克或墙体而产生，击毁敌方坦克后的几率为20%，击中墙体的
 * 几率为10%，且一次只出一个道具，该道具在屏幕上的生存期为120秒，在此期间，屏幕上只有一个
 * 道具，当我方坦克碰到道具后，道具的生效期也为120秒
 */
public class Bonus {
	GameView gv;
	int type;
	int row,col;
	int status;		//道具状态：1表示已产生正在屏幕上显示，2表示被我方坦克接收，3表示时间到自动消失
	boolean flashStatus;	//闪烁状态
	public static final long MAX_EXIST_MIL_TIME=120*1000;		//最大生存期120秒
	long startTime;
	boolean flag=false;
	Resources r;
	Bitmap clock1,clock2,boom1,boom2,shovel1,shovel2,tank1,tank2,food1,food2,alive1,alive2,ship1,ship2,star1,star2;	
	public Bonus(GameView gv,int type,int row,int col){
		this.gv=gv;
		this.type=type;
		this.row=row;
		this.col=col;
		this.startTime=System.currentTimeMillis();
		this.status=1;
		this.flag=true;
		this.flashStatus=true;
		initBitmap();
		new Thread(){								//控制每秒钟闪烁一次
			public void run(){
				while(status==1){
					flashStatus=!flashStatus;
					try{
						Thread.sleep(1000);
					}catch(Exception e){
						
					}
				}
			}
		}.start();
		new Thread(){								//控制道具生存期
			public void run(){
				while(status==1){
					long existTimes=System.currentTimeMillis()-startTime;
					if(existTimes>=MAX_EXIST_MIL_TIME){
						status=3;					//更改状态，3为自动消失
					}
					try{
						Thread.sleep(200);			//200毫秒检测一次
					}catch(Exception e){
						
					}
				}
			}
		}.start();
	}
	public void initBitmap(){
		r=gv.getResources();
		clock1=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.clock1);
		clock2=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.clock2);
		boom1=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.boom1);
		boom2=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.boom2);
		shovel1=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.shovel1);
		shovel2=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.shovel2);
		tank1=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.tank1);
		tank2=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.tank2);
		food1=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.food1);
		food2=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.food2);
		alive1=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.alive1);
		alive2=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.alive2);
		ship1=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.ship1);
		ship2=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.ship2);
		star1=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.star1);
		star2=(Bitmap)BitmapFactory.decodeResource(r,R.drawable.star2);
	}
	public Bitmap getBitmap(int type,boolean flashStatus){
		Bitmap bmp=null;
		switch(type){
		case 1:
			if(flashStatus){
				bmp=clock1;
			}else{
				bmp=clock2;
			}
			break;
		case 2:
			if(flashStatus){
				bmp=boom1;
			}else{
				bmp=boom2;
			}
			break;
		case 3:
			if(flashStatus){
				bmp=shovel1;
			}else{
				bmp=shovel2;
			}
			break;
		case 4:
			if(flashStatus){
				bmp=tank1;
			}else{
				bmp=tank2;
			}
			break;
		case 5:
			if(flashStatus){
				bmp=food1;
			}else{
				bmp=food2;
			}
			break;
		case 6:
			if(flashStatus){
				bmp=alive1;
			}else{
				bmp=alive2;
			}
			break;
		case 7:
			if((flashStatus)){
				bmp=ship1;
			}else{
				bmp=ship2;
			}
			break;
		case 8:
			if(flashStatus){
				bmp=star1;
			}else{
				bmp=star2;
			}
			break; 
		}
		return bmp;
	}
	public void draw(Canvas canvas){
		if(status==1){
			Bitmap bmp=getBitmap(type,flashStatus);
			canvas.drawBitmap(bmp,col*10,row*10,null);
		}
	}
}
