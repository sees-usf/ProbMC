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

package dipro.vis.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class FileLabel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5259704999730793505L;
	protected JTextField label;
	protected JButton button;
	protected FileFilter filter;

	public FileLabel() {

		FormLayout layout = new FormLayout("75dlu, 3dlu, 15dlu", "p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();

		label = new JTextField();
		label.setBackground(new Color(255, 255, 255));
		label.setEditable(false);

		ImageIcon icon = IconLoader.get("saveas_edit.gif");
		button = new JButton(icon);
		button.addActionListener(this);
		button.setToolTipText("Change file");

		builder.add(label, cc.xy(1, 1));
		builder.add(button, cc.xy(3, 1));
		add(builder.getPanel());
	}

	public FileLabel(String fileName) {
		this();
		if (fileName != null) {
			label.setText(fileName.trim());
		}
	}

	public JTextField getLabel() {
		return label;
	}

	public JButton getButton() {
		return button;
	}

	public void setText(String text) {
		label.setText(text);
	}

	public void setFileFilter(FileFilter fileFilter) {
		filter = fileFilter;
	}

	public String getText() {
		return label.getText();
	}

	public void actionPerformed(ActionEvent e) {
		JFileChooser f = new JFileChooser();
		if (label.getText() != null)
			f.setSelectedFile(new File(label.getText()));
		f.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		f.setSelectedFile(new File(System.getProperty("user.dir") + "/"
				+ label.getText()));
		if (filter instanceof FileFilter)
			f.setFileFilter(filter);
		if (f.showOpenDialog(FileLabel.this) == JFileChooser.APPROVE_OPTION) {
			label.setText(f.getSelectedFile().getPath());
		}
	}
}
