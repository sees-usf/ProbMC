//==============================================================================
//	
//	Copyright (c) 2008-
//
//	Chair for Software Engineering - University of Konstanz
//	Prof. Dr. Stefan Leue
//	www.se.inf.uni-konstanz.de
//
//	Authors of this File:
//	* Husain Aljazzar (University of Konstanz)
//	* Florian Leitner-Fischer (University of Konstanz)
//	* Dimitar Simeonov (University of Konstanz)
//------------------------------------------------------------------------------
//	
// This file is part of DiPro.
//
//    DiPro is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    DiPro is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with DiPro.  If not, see <http://www.gnu.org/licenses/>.
//	
//==============================================================================

package dipro.installer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class InstallationGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private Action actionNext, actionBack, actionCancel;
	private JButton buttonNext, buttonBack, buttonCancel;
	private boolean install = false;
	private File prismPath, diproPath;

	private ArrayList<PartPanel> panelSet;
	private ExchangeablePanel panelExchangeableMain;
	private JFileChooser fileChooser;

	private InstallationGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("DiPro - Installation");
		setLayout(new BorderLayout());
		setResizable(false);
		fileChooser = new JFileChooser();

		panelSet = new ArrayList<PartPanel>();
		panelSet.add(new WelcomePanel());
		panelSet.add(new PrismPanel());
		panelSet.add(new DiProPanel());

		panelExchangeableMain = new ExchangeablePanel(panelSet);
		add(panelExchangeableMain, BorderLayout.CENTER);
		initActions();
		initGUIComp();
		pack();
		justifyCenter();
	}

	JFileChooser getFileChooser() {
		return fileChooser;
	}

	private void enableNext(boolean value) {
		buttonNext.setEnabled(value);

	}

	void enableBack(boolean value) {
		buttonBack.setEnabled(value);
	}
	void enableCancel(boolean value) {
		buttonCancel.setEnabled(value);
	}

	private void initActions() {
		actionNext = new AbstractAction("Next") {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (install) {
					actionCancel.setEnabled(false);
					actionNext.setEnabled(false);
					actionBack.setEnabled(false);
					try {
						ZipArchiveExtractor.extractArchive(ClassLoader
								.getSystemResourceAsStream("DiPro.zip"),
								diproPath);
						String os = System.getProperty("os.name").toLowerCase();
						if (os.contains("windows"))
							BashFactory.createWindows(diproPath.getPath(),
									prismPath.getPath());
						else if (os.contains("linux"))
							BashFactory.createLinux(diproPath.getPath(),
									prismPath.getPath());
						else if (os.contains("mac"))
							BashFactory.createMacOS(diproPath.getPath(),
									prismPath.getPath());
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null, e1.getMessage(),
								"Error", JOptionPane.NO_OPTION);
						dispose();
						return;
					}
					panelExchangeableMain.removeAll();
					panelExchangeableMain.add(new JLabel(
							"Installation finished.\n" + diproPath.getPath()));
					enableNext(false);
					enableBack(false);
					enableCancel(true);
					actionCancel.putValue(Action.NAME, "Finish");
					repaint();
					return;
				}
				panelExchangeableMain.nextPanel();
				if (panelExchangeableMain.isLast()) {
					actionNext.putValue(Action.NAME, "Install");
					install = true;
				}
				enableNext(false);
				enableBack(true);
				repaint();
				pack();
				justifyCenter();
			}

		};

		actionBack = new AbstractAction("Back") {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (panelExchangeableMain.isLast()) {
					actionNext.putValue(Action.NAME, "Next");
					install = false;
				}
				panelExchangeableMain.backPanel();

				if (panelExchangeableMain.isFirst())
					actionBack.setEnabled(false);
				else
					actionBack.setEnabled(true);
				enableNext(true);
				repaint();
				pack();
				justifyCenter();
			}
		};

		actionCancel = new AbstractAction("Cancel") {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		};
	}

	void justifyCenter() {
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension panelsize = getSize();
		int x = (int) (screensize.getWidth() - panelsize.getWidth()) / 2;
		int y = (int) (screensize.getHeight() - panelsize.getHeight()) / 2;
		setLocation(x, y);

	}

	private void initGUIComp() {
		buttonBack = new JButton(actionBack);
		buttonCancel = new JButton(actionCancel);
		buttonNext = new JButton(actionNext);
		enableBack(false);

		JPanel panelRight = new JPanel(new FlowLayout(FlowLayout.LEADING));
		panelRight.add(buttonBack);

		panelRight.add(buttonNext);

		JPanel panelButton = new JPanel(new BorderLayout());
		panelButton.setBorder(BorderFactory.createTitledBorder(""));
		Box b = Box.createHorizontalBox();
		b.add(buttonCancel);
		panelButton.add(b, BorderLayout.WEST);
		panelButton.add(panelRight, BorderLayout.EAST);

		add(panelButton, BorderLayout.SOUTH);

	}

	class WelcomePanel extends PartPanel {

		public WelcomePanel() {
			ImageIcon icon = new ImageIcon(loadResource("etc/splash.png"));
			add(new JLabel(icon));
		}

	}

	class DiProPanel extends PartPanel {
		private JButton open;

		public DiProPanel() {
			init();
		}

		public void init() {
			open = new JButton("Select");
			open.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					selectDiPro();
				}

			});
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			panel.add(new JLabel("DiPro installation location:"));
			panel.add(open);
			add(panel, BorderLayout.CENTER);
		}

		private void selectDiPro() {
			JFileChooser directory = new JFileChooser();
			directory.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (directory.showDialog(this, "Select") == JFileChooser.APPROVE_OPTION) {

				diproPath = new File(directory.getSelectedFile().getPath()
						+ "/DiPro");
				install = true;
				enableNext(true);
			} else {
				install = false;
				enableNext(false);
			}
		}

	}

	class PrismPanel extends PartPanel {
		private JButton open;

		public PrismPanel() {
			init();
		}

		public void init() {
			open = new JButton("Select");
			open.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					selectPrism();
				}
			});
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			panel.add(new JLabel("Please specify the path to PRISM directory."));
			panel.add(new JLabel(
					"PRISM can be downloaded at http://www.prismmodelchecker.org/"));
			panel.add(open);
			add(panel, BorderLayout.CENTER);
		}

		private void showWarning() {
			showWarning("The specified PRISM path is not valid.");
		}

		private void selectPrism() {
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (fileChooser.showDialog(this, "Select") == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				if (!file.isDirectory()) {
					showWarning("The path is not correct: " + file.getPath()
							+ "\nPlease select a directory.");
					return;
				}
				File prismFile = new File(file.getPath() + "/lib");
				if (!prismFile.exists()) {
					showWarning("Please select the PRISM directory.");
					return;
				}
				prismFile = new File(file.getPath() + "/lib/prism.jar");
				if (!prismFile.exists()) {
					showWarning("PRISM jar is missing. Please create the prism.jar");
					return;
				}
				prismPath = fileChooser.getSelectedFile();
				enableNext(true);
			} else {
				enableNext(false);
			}
		}

	}

	public static void main(String... str) {

		InstallationGUI gui = new InstallationGUI();
		gui.setVisible(true);

	}
}
