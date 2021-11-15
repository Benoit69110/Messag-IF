package gui_old.widgets;

import javax.swing.JComboBox;

import gui_old.widgets.style.CustomColors;
import gui_old.widgets.style.CustomFonts;

/**
 * ComboBox customis�e.
 * @author aleconte
 * */
public class ComboBox<E> extends JComboBox<E> {
	/**
	 * Application d'un nouveau style � la combobox.
	 * */
	public ComboBox() {
		this.setForeground(CustomColors.TEXT_COLOR);
		this.setBackground(CustomColors.BACKGROUND_MEDIUM_RED);
		this.setFont(CustomFonts.LABEL_FONT);
	}
}
