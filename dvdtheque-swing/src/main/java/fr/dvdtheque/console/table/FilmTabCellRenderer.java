package fr.dvdtheque.console.table;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import fr.dvdtheque.console.vue.BaseVueAppli;
import fr.fredos.dvdtheque.swing.model.FilmTableModel;

public class FilmTabCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int col) {
		// Cells are by default rendered as a JLabel.
		//JLabel rippedLabel = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
		BufferedImage okkoPic = null;
		// Get the status for the current row.
		FilmTableModel tableModel = (FilmTableModel) table.getModel();
		if (tableModel.getValueAt(row,col).equals(true)) {
			try {
				okkoPic = ImageIO.read(this.getClass().getResource(BaseVueAppli.okPath));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				okkoPic = ImageIO.read(this.getClass().getResource(BaseVueAppli.koPath));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return new JLabel(new ImageIcon(okkoPic));

	}
}
