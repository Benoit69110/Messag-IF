package ihm.widgets;

import ihm.widgets.style.CustomColors;
import ihm.widgets.style.CustomFonts;

import javax.swing.JLabel;

/**
 * Label customis�.
 * @author aleconte
 * */
public class Label extends JLabel {
	/**
	 * Application d'un nouveau style au label.
	 * @param text Texte du label.
	 * */
	public Label(String text) {
		super(text);
		this.setForeground(CustomColors.TEXT_COLOR);
		this.setBackground(CustomColors.BACKGROUND_DARK_RED);
		this.setFont(CustomFonts.LABEL_FONT);
	}
}
