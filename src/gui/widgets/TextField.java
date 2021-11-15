package gui.widgets;

import gui.widgets.style.CustomColors;
import gui.widgets.style.CustomFonts;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;

/**
 * TextField customisé.
 * @author aleconte
 * */
public class TextField extends JFormattedTextField {
	
	/**
	 * Application d'un nouveau style au textfield.
	 * */
	public TextField() {
		this.setForeground(CustomColors.TEXT_COLOR);
		this.setBackground(CustomColors.BACKGROUND_MEDIUM_RED);
		this.setFont(CustomFonts.LABEL_FONT);
		this.setBorder(BorderFactory.createLineBorder(CustomColors.BORDER_COLOR));
	}
}
