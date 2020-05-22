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

import java.lang.reflect.InvocationTargetException;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MainMenuBar extends JMenuBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2449332415186612544L;

	JMenuItem saveItem;
	JMenuItem saveAsItem;

	public MainMenuBar() throws SecurityException, IllegalArgumentException,
			NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException, Exception {
		super();
		init();
	}

	protected void init() throws SecurityException, IllegalArgumentException,
			NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException, Exception {

		/* Add menus to view */
		add(getFileMenu());
		add(getVisualizationMenu());

		/* Set border */
		// this.setBorderPainted(true);
	}

	private JMenu getFileMenu() throws Exception {

		JMenu menu = new JMenu("File");

		/* Create new experiment */
		// menu.add(new CreateNewExperimentMenu());
		/* Load experiment */
		JMenuItem item = new JMenuItem(
			ActionsManager.getInstance().newExperiment);
		menu.add(item);
//		
//		item = new JMenuItem(
//				ActionsManager.getInstance().openExperimentFile);
//		item.setIcon(IconLoader.get("fldr_obj.gif"));
//		menu.add(item);
//		item = new JMenuItem(ActionsManager.getInstance().loadNextExperiment);
//		menu.add(item);
//		item = new JMenuItem(ActionsManager.getInstance().reloadExperiment);
//		menu.add(item);
//		/* Save Experiment */
//		saveItem = new JMenuItem(ActionsManager.getInstance().saveExperiment);
//		saveItem.setIcon(IconLoader.get("save_edit_deactivated.gif"));
//		saveItem.setEnabled(false);
//		menu.add(saveItem);
//
//		/* Save Experiment as */
//		saveAsItem = new JMenuItem(
//				ActionsManager.getInstance().saveExperimentAs);
//		saveAsItem.setIcon(IconLoader.get("saveas_edit_deactivated.gif"));
//		saveAsItem.setEnabled(false);
//		menu.add(saveAsItem);

		// menu.add(graphFileSaveAs);
		// menu.add(graphImageSaveAs);
		// menu.addSeparator();
		// menu.add(dotFileSaveAs);
		menu.addSeparator();
		menu.add(ActionsManager.getInstance().closeApplication);
		return menu;

	}
	
	
	private JMenu getVisualizationMenu() throws Exception {
		JMenu menu = new JMenu("Visualization");
//		JMenuItem item = new JMenuItem(ActionsManager.getInstance().exportImage);
//		menu.add(item);
//		item = new JMenuItem(ActionsManager.getInstance().visualizeFromScratch);
//		menu.add(item);
//		return menu;
		menu.add(new JMenuItem(ActionsManager.getInstance().visualizeFromScratch));
		return menu;
	}

	public void setExperimentSaveAble(boolean save) {
		saveItem.setEnabled(save);
		saveAsItem.setEnabled(save);

		if (save) {
			saveItem.setIcon(IconLoader.get("save_edit.gif"));
			saveAsItem.setIcon(IconLoader.get("saveas_edit.gif"));
		} else {
			saveItem.setIcon(IconLoader.get("save_edit_deactivated.gif"));
			saveAsItem.setIcon(IconLoader.get("saveas_edit_deactivated.gif"));
		}
	}

}
