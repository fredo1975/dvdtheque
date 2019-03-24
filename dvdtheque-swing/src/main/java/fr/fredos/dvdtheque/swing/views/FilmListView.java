package fr.fredos.dvdtheque.swing.views;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FilmListView extends AbstractViewListenerHolder{
	protected final Log logger = LogFactory.getLog(FilmListView.class);
	public FilmListView(final JPanel mainPanel) {
		initFirstTab(mainPanel);
    }
	protected void initFirstTab(final JPanel mainPanel) {
		String[] columnNames = { "First Name", "Last Name", "Sport", "# of Years", "Vegetarian" };

		Object[][] data = { { "Kathy", "Smith", "Snowboarding", new Integer(5), new Boolean(false) },
				{ "John", "Doe", "Rowing", new Integer(3), new Boolean(true) },
				{ "Sue", "Black", "Knitting", new Integer(2), new Boolean(false) },
				{ "Jane", "White", "Speed reading", new Integer(20), new Boolean(true) },
				{ "Joe", "Brown", "Pool", new Integer(10), new Boolean(false) } };

		final JTable table = new JTable(data, columnNames);
		table.setPreferredScrollableViewportSize(new Dimension(800, 100));
		table.setFillsViewportHeight(true);
		JScrollPane scrollPane = new JScrollPane(table);
		JPanel contentPane = new JPanel();
		contentPane.add(scrollPane);
		mainPanel.add(contentPane);
	}
}
