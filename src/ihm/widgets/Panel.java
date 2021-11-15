package ihm.widgets;

import ihm.widgets.style.CustomColors;

import java.awt.Dimension;

import javax.swing.JPanel;

/**
 * Panel customis�.
 * @author aleconte
 * */
public class Panel extends JPanel {
	
	/**
	 * Application d'un nouveau style au panel.
	 * */
	public Panel() {
		this.setBackground(CustomColors.BACKGROUND_DARK_RED);
	}
	
	/**
	 * Application d'un nouveau style au panel.
	 * @param dim Dimension souhait�e pour le panel.
	 * */
	public Panel(Dimension dim) {
		this();
		this.setPreferredSize(dim);
	}
}
