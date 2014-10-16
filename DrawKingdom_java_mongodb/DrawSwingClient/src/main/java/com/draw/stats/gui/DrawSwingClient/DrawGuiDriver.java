package com.draw.stats.gui.DrawSwingClient;

import javax.swing.JFrame;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

public class DrawGuiDriver {

	static ApplicationContext ctx = new GenericXmlApplicationContext("draw-swing-context.xml");
	public static void main(String [] args){
		DrawFrame drawFrame = (DrawFrame) ctx.getBean("drawFrame");
		drawFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		drawFrame.setSize(500, 500);
		drawFrame.setVisible(true);
		drawFrame.setTitle("Draw");
		drawFrame.setLocation(100,100);
	}
}
