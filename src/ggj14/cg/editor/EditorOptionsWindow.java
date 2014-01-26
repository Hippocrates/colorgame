package ggj14.cg.editor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class EditorOptionsWindow {
	
	//the calling application
	private final EditorMainWindow editor;

	private JFrame frame;

	private int layer = 0; //which layer is selected: back - 0, plat - 1, fore - 2
	private boolean hideInactive = false;
	private boolean showGrid = false;
	
	//A bunch of components on the screen
	private JTextField txtReslvl;
	
	private JSpinner spnrXDim;
	private JSpinner spnrYDim;
	private JCheckBox chckbxShowGrid;

	/**
	 * Create the application.
	 */
	public EditorOptionsWindow() {
		this(null);
	}
	
	public EditorOptionsWindow(EditorMainWindow master) {
		editor = master;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("Colour Game Level Editor Options");
		frame.setBounds(100, 100, 480, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel panel_3 = new JPanel();
		panel.add(panel_3);
		
		JButton btnLoad = new JButton("Load");
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {loadLevel(txtReslvl.getText());}
		});
		panel_3.add(btnLoad);
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {saveLevel(txtReslvl.getText());}
		});
		panel_3.add(btnSave);
		
		txtReslvl = new JTextField();
		txtReslvl.setText("res/map/");
		panel_3.add(txtReslvl);
		txtReslvl.setColumns(10);
		
		JButton btnReset = new JButton("Reset");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {resetLevel();}
		});
		panel_3.add(btnReset);
		
		JPanel panel_1 = new JPanel();
		panel.add(panel_1);
		
		JPanel panel_2 = new JPanel();
		panel.add(panel_2);
		
		JLabel lblXDimension = new JLabel("X Dimension:");
		panel_2.add(lblXDimension);
		
		spnrXDim = new JSpinner();
		spnrXDim.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {changeDim();}
		});
		spnrXDim.setModel(new SpinnerNumberModel(20, EditorMainWindow.MIN_DIM, EditorMainWindow.MAX_DIM, 1));
		spnrXDim.setValue(20);
		panel_2.add(spnrXDim);
		
		Component rigidArea = Box.createRigidArea(new Dimension(30, 20));
		panel_2.add(rigidArea);
		
		JLabel lblYDimension = new JLabel("Y Dimension:");
		panel_2.add(lblYDimension);
		
		spnrYDim = new JSpinner();
		spnrYDim.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {changeDim();}
		});
		spnrYDim.setModel(new SpinnerNumberModel(20, EditorMainWindow.MIN_DIM, EditorMainWindow.MAX_DIM, 1));
		spnrYDim.setValue(20);
		panel_2.add(spnrYDim);
		
		Component rigidArea_1 = Box.createRigidArea(new Dimension(30, 20));
		panel_2.add(rigidArea_1);
		
		chckbxShowGrid = new JCheckBox("Show grid");
		chckbxShowGrid.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {changeGrid(chckbxShowGrid.isSelected());}
		});
		panel_2.add(chckbxShowGrid);
	}
	
	private void loadLevel(String level) {
		try {
			if(editor != null) {editor.loadLevel(level);}
		} catch(IOException e) {

		}
	}

	private void saveLevel(String level) {
		try {
			if(editor != null) {editor.saveLevel(level);}
		} catch(IOException e) {
			
		}
	}
	
	private void resetLevel() {
		if(editor != null) {editor.resetLevel();}
	}
	
	private void changeGrid(boolean newGrid) {
		if(showGrid == newGrid) {return;}
		
		showGrid = newGrid;
		if(editor != null) {editor.repaint();}
	}
	
	private void changeDim() {
		int xDim = ((Integer) spnrXDim.getValue()).intValue();
		int yDim = ((Integer) spnrYDim.getValue()).intValue();
		editor.redimension(xDim, yDim);
	}
	
	//called during level load and we need to change the dimension spinners
	public void setDim(int xDim, int yDim) {
		spnrXDim.setValue(new Integer(xDim));
		spnrYDim.setValue(new Integer(yDim));
	}
	
	public JFrame getFrame() {
		return frame;
	}
	
	//returns the selected layer
	public int getLayer() {
		return layer;
	}
	
	public boolean getHideInactive() {
		return hideInactive;
	}
	
	public boolean getShowGrid() {
		return showGrid;
	}

}
