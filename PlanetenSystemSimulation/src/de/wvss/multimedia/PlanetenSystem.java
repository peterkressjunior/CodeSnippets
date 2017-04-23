package de.wvss.multimedia;

import java.awt.*;
import java.awt.event.*;

public class PlanetenSystem extends Frame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5576196926188425343L;

	public PlanetenSystem(){
		setBackground(Color.BLACK ); 					
		setForeground(Color.lightGray);
		setTitle("Peterchens Mondfahrt");
		addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
            	System.exit(0);
            }
        });
		setResizable(false);
		setLocation(100,20);
	}
	
	public static void main(String[] argl){
		PlanetenSystem sonnenSystem = new PlanetenSystem();
		sonnenSystem.add(new Planeten());
		sonnenSystem.pack();
		sonnenSystem.setVisible(true);
	}

	
}

