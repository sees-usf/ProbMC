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

/**
 * 
 */
package dipro.vis.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The public class <tt>DiProToolBox</tt> is an extended version of the
 * {@link JInternalFrame},that gives one the opportunity to created a internal
 * unfolding like menu-structure. To add an item:
 * <p>
 * addOptionItem(optionalItem)
 * <p>
 * 
 * @author simeonov
 * 
 */
public class ToolFrame extends JFrame {

	private Container m_contPane;
	
	
	public ToolFrame() {
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setTitle("DiPro - Tools");
		initComponents();
	}




	private void initComponents() {
		// Set up ToolBox
		m_contPane = new JPanel();
		m_contPane.setLayout(new BoxLayout(m_contPane, BoxLayout.Y_AXIS));
		setContentPane(m_contPane);
	}

	public void addOption(String itemName, JComponent infoPanel,
			boolean slideable,boolean unfolded) {
		if (slideable) {
			OptionItem newItem = new OptionItem(itemName, infoPanel, slideable,unfolded);
			m_contPane.add(newItem);
		} else {
			m_contPane.add(infoPanel);
		}
	}

	public class OptionItem extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 650978960379306227L;

		private JPanel m_itemBoxElement;
		private JLabel m_itemTitle;
		private JButton m_flodingButton;
		private Icon openIcon = IconLoader.get("eastView.png");
		private Icon closeIcon = IconLoader.get("southView.png");
		private JComponent m_infoPanel;
		private boolean m_unFolded;

		/**
		 * Creates a <tt>OptionItem</tt> instance with the specified: <li>title
		 * for the given option. <li>panel with the required visualization for
		 * that option. <li>if the option should be at first available.
		 * <p>
		 * The private class could only be used as a add on with the
		 * {@link ToolFrame}
		 * 
		 * @param itemName
		 *            - The title to be displayed.
		 * @param infoPanel
		 *            - The visualization panel to be displayed.
		 * @param available
		 *            - If it should right away available.
		 */
		OptionItem(String itemName, JComponent infoPanel, boolean slideable, boolean unfolded
				) {
			m_itemTitle = new JLabel(itemName);
			m_flodingButton = createTransperentButton();
			m_itemBoxElement = new JPanel();
			m_unFolded = unfolded;
			m_infoPanel = infoPanel;
			setUp(slideable);
			unFold();
		}

		private void setUp(boolean slideable) {
			// Sets-up the Layout
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			setBorder(BorderFactory.createLineBorder(Color.BLACK));

			// Sets up the itemBoxElement
			m_itemBoxElement.setBorder(BorderFactory.createRaisedBevelBorder());
			m_itemBoxElement.setLayout(new FlowLayout(FlowLayout.LEFT));

			// ActionListener
			m_flodingButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					unFold();
					validate();
				}
			});

			m_itemBoxElement.add(m_flodingButton);
			m_itemBoxElement.add(m_itemTitle);
			add(m_itemBoxElement);
			// Adds the components to the contentPane
			add(m_infoPanel);

		}

		private boolean isUnfolded() {
			return m_unFolded;
		}

		private void unFold() {
			if (isUnfolded()) {
				m_unFolded = false;
				m_infoPanel.setVisible(m_unFolded);
				m_flodingButton.setIcon(openIcon);
			} else {
				m_unFolded = true;
				m_infoPanel.setVisible(m_unFolded);
				m_flodingButton.setIcon(closeIcon);
			}
			pack();
		}

		private JButton createTransperentButton() {
			JButton transButton = new JButton();
			transButton.setMargin(new Insets(0, 0, 0, 0));
			transButton.setBorder(BorderFactory.createEmptyBorder());
			transButton.setContentAreaFilled(false);
			return transButton;
		}
	}
}
