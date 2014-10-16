package com.draw.stats.gui.DrawSwingClient;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import org.springframework.beans.factory.annotation.Autowired;

import com.draw.stats.stats_calculator.CsvSimulatorManager;

public class DrawFrame extends JFrame {


	/**
	 * 
	 */
	private static final long serialVersionUID = 5672529499617613331L;

	JProgressBar jProgressBar;
	@Autowired
	private CsvSimulatorManager csvSimulatorManager;

	public DrawFrame(){
		GridLayout gridLayout = new GridLayout(2,1);
		setLayout(gridLayout);
		jProgressBar = new JProgressBar();
		JButton simulationButton = new JButton("Run Simulation");
		simulationButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					pollProgress();
					csvSimulatorManager.calculateStrategyResults();
				} catch (IOException e1) {
					System.err.println(e1.getMessage());
				}

			}
		});
		simulationButton.setEnabled(true);
		add(jProgressBar);
		add(simulationButton);
	}

	private void pollProgress() {
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.execute( new Runnable() {
			public void run() {				
				try {
					Thread.sleep(1000);
					while(csvSimulatorManager.getProgress() < 100){					
						jProgressBar.setValue(csvSimulatorManager.getProgress());
						jProgressBar.setMaximum(100);
						Thread.sleep(1000);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public CsvSimulatorManager getCsvSimulatorManager() {
		return csvSimulatorManager;
	}

	public void setCsvSimulatorManager(CsvSimulatorManager csvSimulatorManager) {
		this.csvSimulatorManager = csvSimulatorManager;
	}


}
