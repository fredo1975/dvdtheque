package fr.dvdtheque.console.table;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class RealisateurCellRenderer extends JLabel implements ListCellRenderer{
	private final Color HIGHLIGHT_COLOR = new Color(0, 0, 128);

	public RealisateurCellRenderer() {
		setOpaque(true);
		setIconTextGap(12);
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		RealisateurEntry entry = (RealisateurEntry) value;
		setText(entry.getRealisateur().getPrenom() + " " + entry.getRealisateur().getNom());
		if (isSelected) {
			setBackground(HIGHLIGHT_COLOR);
			setForeground(Color.white);
		} else {
			setBackground(Color.white);
			setForeground(Color.black);
		}
		return this;
	}
}
