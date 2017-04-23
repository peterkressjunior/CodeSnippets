package de.wvss.multimedia;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Planeten extends Canvas implements MouseListener, MouseMotionListener {

	private static final long serialVersionUID = 1233066460006961519L;
	
	/*** leinwand abmessungen **/
	final int WIDTH = 1000;
	final int HEIGHT = 700;
	final int CENTERX = 500;
	final int CENTERY = 350;

	/**scroll-balken**/
	boolean leiste = false;
	boolean leisteDuo = false;
	private Rectangle2D schieberVertikal = new Rectangle2D.Double(WIDTH-75,300,30,10);
	private Rectangle2D schieberHorizontal = new Rectangle2D.Double(300,HEIGHT-55, 10,30);

	private BufferedImage background;
	private BufferedImage sonne;
	private BufferedImage merkur;
	private BufferedImage venus;
	private BufferedImage erde;
	private BufferedImage mond;
	private BufferedImage mars;
	private BufferedImage jupiter;
	private BufferedImage saturn;
	private BufferedImage uranus;
	private BufferedImage neptun;
	
	/** position der himmelskoerper**/
	int alpha = 0;
	int beta = 0;
	int gamma =  0;
	int delta = 0;
	int epsilon = 0;
	int zeta = 0;
	int eta = 0;
	int theta = 0;
	int lota = 0;
	int kappa = 0;
	
	boolean umlaufbahnen = true;
	
	public Planeten(){
		try{
			background = ImageIO.read(new File("img/apfelbaumgemahlt.jpg"));
			sonne = ImageIO.read(new File("img/sonne.png"));
			merkur = ImageIO.read(new File("img/merkur.png"));
			venus = ImageIO.read(new File("img/venus.png"));
			erde = ImageIO.read(new File("img/erde.png"));
			mond = ImageIO.read(new File("img/mond.png"));
			mars = ImageIO.read(new File("img/mars.png"));
			jupiter = ImageIO.read(new File("img/jupiter.png"));
			saturn = ImageIO.read(new File("img/saturn.png"));
			uranus = ImageIO.read(new File("img/uranus.png"));
			neptun = ImageIO.read(new File("img/neptun.png"));
		}catch(IOException e){
			e.printStackTrace();
		}
		addMouseMotionListener(this);
		addMouseListener(this);
		play();
	}
	public void update(Graphics g){
		paint(g);
	}
	public void paint(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		BufferedImage bi = (BufferedImage) createImage(1000, 700);
		Graphics2D big = (Graphics2D)bi.getGraphics();
		big.setRenderingHint( RenderingHints.KEY_ANTIALIASING, 
				RenderingHints.VALUE_ANTIALIAS_ON); 
		
		big.drawImage(background, null, 0,0);
		big.setPaint(new Color(60,60,60));
		
		big.translate(CENTERX, CENTERY);
		big.scale( schieberHorizontal.getCenterX()/(double)500, schieberHorizontal.getCenterX()/(double)500);
		big.translate(-CENTERX, -CENTERY);
		
		big.translate(CENTERX-50, CENTERY-50);
		big.rotate(Math.PI/180*alpha, 50, 50);
		big.drawImage(sonne, null, 0, 0);
		big.rotate(-Math.PI/180*alpha, 50, 50);
		big.translate(-CENTERX+50, -CENTERY+50);
		
		big.setPaint(new GradientPaint(0F, 0F, Color.BLACK, 1000F, 700F, Color.WHITE, false));
		if(umlaufbahnen){
			big.translate(CENTERX-69, CENTERY-69);
			big.drawOval(0,0,138,138);
			big.translate(-CENTERX+69, -CENTERY+69);
		}
		
		big.rotate(Math.PI/180*beta, CENTERX, CENTERY);
		big.translate(CENTERX+60, CENTERY-7);
		big.drawImage(merkur, null, 0, 0);
		big.translate(-CENTERX-60, -CENTERY+7);
		big.rotate(-Math.PI/180*beta, CENTERX, CENTERY);
		
		if(umlaufbahnen){
			big.translate(CENTERX-115, CENTERY-115);
			big.drawOval(0,0,230,230);
			big.translate(-CENTERX+115, -CENTERY+115);
		}
		
		big.rotate(Math.PI/180*gamma, CENTERX, CENTERY);
		big.translate(CENTERX+100, CENTERY-15);
		big.drawImage(venus, null, 0, 0);
		big.translate(-CENTERX-100, -CENTERY+15);
		big.rotate(-Math.PI/180*gamma, CENTERX, CENTERY);
		
		if(umlaufbahnen){
			big.translate(CENTERX-175, CENTERY-175);
			big.drawOval(0,0,350,350);
			big.translate(-CENTERX+175, -CENTERY+175);
		}
		
		big.rotate(Math.PI/180*delta, CENTERX, CENTERY);
		big.translate(CENTERX+160, CENTERY-15);
		big.drawImage(erde, null, 0, 0);
			if(umlaufbahnen){
				big.translate(-11, -11);
				big.drawOval(0,0,54,54);
				big.translate(11, 11);
			}
			big.rotate(Math.PI/180*epsilon*4, 15, 15);
			big.translate(40, 8);
			big.drawImage(mond, null, 0, 0);
			big.translate(-40, -8);
			big.rotate(-Math.PI/180*epsilon*4, 15, 15);
		big.translate(-CENTERX-160, -CENTERY+15);
		big.rotate(-Math.PI/180*delta, CENTERX, CENTERY);
		
		if(umlaufbahnen){
			big.translate(CENTERX-232, CENTERY-232);
			big.drawOval(0,0,464,464);
			big.translate(-CENTERX+232, -CENTERY+232);
		}
		
		big.rotate(Math.PI/180*zeta, CENTERX, CENTERY);
		big.translate(CENTERX+220, CENTERY-12);
		big.drawImage(mars, null, 0, 0);
		big.translate(-CENTERX-220, -CENTERY+12);
		big.rotate(-Math.PI/180*zeta, CENTERX, CENTERY);
		
		if(umlaufbahnen){
			big.translate(CENTERX-285, CENTERY-285);
			big.drawOval(0,0,570,570);
			big.translate(-CENTERX+285, -CENTERY+285);
		}
		
		big.rotate(Math.PI/180*eta, CENTERX, CENTERY);
		big.translate(CENTERX+260, CENTERY-25);
		big.drawImage(jupiter, null, 0, 0);
		big.translate(-CENTERX-260, -CENTERY+25);
		big.rotate(-Math.PI/180*eta, CENTERX, CENTERY);
		
		if(umlaufbahnen){
			big.translate(CENTERX-345, CENTERY-345);
			big.drawOval(0,0,690,690);
			big.translate(-CENTERX+345, -CENTERY+345);
		}
		
		big.rotate(Math.PI/180*theta, CENTERX, CENTERY);
		big.translate(CENTERX+320, CENTERY-25);
		big.drawImage(saturn, null, 0, 0);
		big.translate(-CENTERX-320, -CENTERY+25);
		big.rotate(-Math.PI/180*theta, CENTERX, CENTERY);
		
		//uranus wird gezeichnet
		if(umlaufbahnen){
			big.translate(CENTERX-400, CENTERY-400);
			big.drawOval(0,0,800,800);
			big.translate(-CENTERX+400, -CENTERY+400);
		}
		big.rotate(Math.PI/180*lota, CENTERX, CENTERY);
		big.translate(CENTERX+380, CENTERY-20);
		big.drawImage(uranus, null, 0, 0);
		big.translate(-CENTERX-380, -CENTERY+20);
		big.rotate(-Math.PI/180*lota, CENTERX, CENTERY);
		
		//neptun wird gezeichnet
		if(umlaufbahnen){
			big.translate(CENTERX-470, CENTERY-470);
			big.drawOval(0,0,940,940);
			big.translate(-CENTERX+470, -CENTERY+470);
		}
		big.rotate(Math.PI/180*kappa, CENTERX, CENTERY);
		big.translate(CENTERX+450, CENTERY-20);
		big.drawImage(neptun, null, 0, 0);
		big.translate(-CENTERX-450, -CENTERY+20);
		big.rotate(-Math.PI/180*kappa, CENTERX, CENTERY);
		
		//die skallierung wird wieder auf normal gesetzt
		big.translate(CENTERX, CENTERY);
		big.scale(1/(schieberHorizontal.getCenterX()/(double)500), 1/(schieberHorizontal.getCenterX()/(double)500));
		big.translate(-CENTERX, -CENTERY);

		//ecken werden gezeichnet
		big.setPaint(new Color(200,200,200));
		big.drawLine(10, 10, 10, 20);
		big.drawLine(10, 10, 20, 10);
		big.drawLine(WIDTH-20, 10, WIDTH-10, 10);
		big.drawLine(WIDTH-10, 10, WIDTH-10, 20);
		big.drawLine(10, HEIGHT-20, 10, HEIGHT-10);
		big.drawLine(10, HEIGHT-10, 20, HEIGHT-10);
		big.drawLine(WIDTH-20, HEIGHT-10, WIDTH-10, HEIGHT-10);
		big.drawLine(WIDTH-10, HEIGHT-10, WIDTH-10, HEIGHT-20);
	
		//halbkreise an den seiten in der mitte
		big.drawArc(10, CENTERY, 19, 19, 0, 180);
		big.fillArc(10, CENTERY, 20, 20, 180, 180);
		big.drawArc(WIDTH-30, CENTERY-10, 19, 19, 180, 180);
		big.fillArc(WIDTH-30, CENTERY-10, 20, 20, 0, 180);
		
		/**checkbox flugbahnen an/aus************************/
		big.drawRect(100,100,13,13);
		if(umlaufbahnen){ 
			big.fillRect(103, 103, 8, 8);
			big.drawString("Umlaufbahnen deaktivieren", 120, 112);
		}else{
			big.drawString("Umlaufbahnen aktivieren", 120, 112);
		}
		/****************************************************/
		
		/**scroll-Leiste rechts - vertikal*******************/
		big.drawLine(WIDTH-60,100,WIDTH-60,HEIGHT-100);
		if(leisteDuo){
			big.setPaint(Color.WHITE);
			big.drawRect((int)schieberVertikal.getX()-2,(int)schieberVertikal.getY()-2,
					(int)schieberVertikal.getWidth()+3,
					(int)schieberVertikal.getHeight()+3);
		}
		big.fill(schieberVertikal);
		big.translate(350, -110);
		big.rotate(-55, CENTERX, CENTERY);
		big.drawString("speed -/+",280, 260);
		big.rotate(55, CENTERX, CENTERY);
		big.translate(-350, 110);
		/****************************************************/
		
		/**scroll-Leiste unten - horizontal******************/
		big.drawLine(100, HEIGHT-40, WIDTH-100, HEIGHT-40);
		if(leiste){
			big.setPaint(Color.WHITE);
			big.drawRect((int)schieberHorizontal.getX()-2,(int)schieberHorizontal.getY()-2,
					(int)schieberHorizontal.getWidth()+3,
					(int)schieberHorizontal.getHeight()+3);
		}
		big.fill(schieberHorizontal);
		big.drawString("zoom -/+ ", 25, HEIGHT-36);
		/****************************************************/
		
		//das zwischen-gezeichnete bild wird auf die leinwand gezeichnet 
		g2.drawImage(bi, null, 0, 0);
		
		//"ping pong"-spielen mit der paint() methode
		play();
	}
	
	/** wichtig: das canvas übergibt dem Frame seine Groesse**/
	public Dimension getPreferredSize() {   
		return new Dimension(1000, 700);
             
	}

	
	//hier werden die positionen neu gesetzt und repainted
	public void play(){
			try{
				Thread.sleep(26);
				
				alpha = (alpha+10+(int)(schieberVertikal.getCenterY()/500*10))%361;
				beta = (beta+9+(int)(schieberVertikal.getCenterY()/500*10))%361;
				gamma = (gamma+8+(int)(schieberVertikal.getCenterY()/500*10))%361;
				delta = (delta+7+(int)(schieberVertikal.getCenterY()/500*10))%361;
				epsilon = (epsilon+3+(int)(schieberVertikal.getCenterY()/500*10))%361;
				zeta = (zeta+5+(int)(schieberVertikal.getCenterY()/500*10))%361;
				eta = (eta+4+(int)(schieberVertikal.getCenterY()/500*10))%361;
				theta = (theta+3+(int)(schieberVertikal.getCenterY()/500*10))%361;
				lota = (lota+2+(int)(schieberVertikal.getCenterY()/500*10))%361;
				kappa = (kappa+1+(int)(schieberVertikal.getCenterY()/500*10))%361;
				
				
			}catch(InterruptedException e){
				e.printStackTrace();
			}
			repaint();
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if((e.getX()>=100) && (e.getX()<=270) && (e.getY()>=96) && (e.getY()<=120)){
			umlaufbahnen = !umlaufbahnen;
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
		if((e.getX()>=schieberVertikal.getMinX()) && (e.getX()<=schieberVertikal.getMaxX()) 
				&& (e.getY()>=schieberVertikal.getMinY()) && (e.getY()<=schieberVertikal.getMaxY())){
			leisteDuo = true;
		}
		if((e.getX()>=schieberHorizontal.getMinX()) && (e.getX()<=schieberHorizontal.getMaxX()) 
				&& (e.getY()>=schieberHorizontal.getMinY()) && (e.getY()<=schieberHorizontal.getMaxY())){
			leiste = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		leiste = false;
		leisteDuo = false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		if(leiste){
			if((e.getX()>=100) && (e.getX()<=900)){
				schieberHorizontal.setRect(e.getX()-5,schieberHorizontal.getY(),schieberHorizontal.getWidth(),
						schieberHorizontal.getHeight());
			}else if(e.getX()<100){
				schieberHorizontal.setRect(100,schieberHorizontal.getY(),schieberHorizontal.getWidth(),
						schieberHorizontal.getHeight());
			}else{
				schieberHorizontal.setRect(900,schieberHorizontal.getY(),schieberHorizontal.getWidth(),
						schieberHorizontal.getHeight());
			}
				repaint();
		}
		
		if(leisteDuo){
			if((e.getY()>=100) && (e.getY()<=600)){
				schieberVertikal.setRect(schieberVertikal.getX(),e.getY()-5,schieberVertikal.getWidth(),
						schieberVertikal.getHeight());
			}else if(e.getY()<100){
				schieberVertikal.setRect(schieberVertikal.getX(),100,schieberVertikal.getWidth(),
						schieberVertikal.getHeight());
			}else{
				schieberVertikal.setRect(schieberVertikal.getX(),600,schieberVertikal.getWidth(),
						schieberVertikal.getHeight());
			}
				repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		if((e.getX()>=schieberVertikal.getMinX()) && (e.getX()<=schieberVertikal.getMaxX()) 
				&& (e.getY()>=schieberVertikal.getMinY()) && (e.getY()<=schieberVertikal.getMaxY())){
			leisteDuo = true;
		}else{
			leisteDuo = false;
		}
		
		if((e.getX()>=schieberHorizontal.getMinX()) && (e.getX()<=schieberHorizontal.getMaxX()) 
				&& (e.getY()>=schieberHorizontal.getMinY()) && (e.getY()<=schieberHorizontal.getMaxY())){
			leiste = true;
		}else{
			leiste = false;
		}
	}
}
