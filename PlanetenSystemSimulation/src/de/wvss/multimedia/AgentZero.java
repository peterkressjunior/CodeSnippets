/**
 * 
 */
package de.wvss.multimedia;

/**
 * @author peter
 *
 */
public class AgentZero implements Runnable{
	private int x;
	private int y;
	private int width;
	private int height;
	private int centerX;
	private int centerY;
	private int alpha;
	private int speed;
	
	public AgentZero(){
		x = 0;
		y = 0;
		width = 50;
		height = 50;
		centerX = x+width/2;
		centerY = y+height/2;
		alpha = 0;
		speed = 0;
	}
	
	public String toString(){
		return "x,y,width,height,centerX,centerY,alpha,speed: "+
				x+","+y+","+width+","+height+","+centerX+","+centerY+","+
				alpha+","+speed;
	}
	
	public void setSpeed(int s){
		this.speed = s;
	}

	@Override
	public void run() {
		int i = 0;
		while(true){
			i = (i+1)%360;
			setSpeed(i);
			System.out.println(this);
		}
	}
}
