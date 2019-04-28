package fr.fredos.dvdtheque.swing;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.fredos.dvdtheque.swing.model.FilmTableModel;
import fr.fredos.dvdtheque.swing.model.TmdbFilmTableModel;
import fr.fredos.dvdtheque.swing.presenter.FilmAddPresenter;
import fr.fredos.dvdtheque.swing.presenter.FilmListPresenter;
import fr.fredos.dvdtheque.swing.presenter.MenuBarPresenter;
import fr.fredos.dvdtheque.swing.views.FilmAddView;
import fr.fredos.dvdtheque.swing.views.FilmListView;
import fr.fredos.dvdtheque.swing.views.MenuBarView;

@Configuration
public class SwingConfig {
	@Bean
    public FilmTableModel filmTableModel() {
        return new FilmTableModel();
    }
	@Bean
    public TmdbFilmTableModel tmdbFilmTableModel() {
		return new TmdbFilmTableModel();
	}
	@Bean
    public FilmListPresenter filmListPresenter() {
        return new FilmListPresenter();
    }
	@Bean
    public FilmAddPresenter filmAddPresenter() {
        return new FilmAddPresenter();
    }
	@Bean
    public MenuBarView menuBarView() {
        return new MenuBarView();
    }
	@Bean
    public MenuBarPresenter MenuBarPresenter() {
		return new MenuBarPresenter();
	}
	@Bean
    public SpinnerDialog spinnerDialog() {
		return new SpinnerDialog();
	}
	@Bean
	public JTable filmListJTable() {
		return new JTable();
	}
	@Bean
	public JTable tmdbFilmListJTable() {
		return new JTable();
	}
	@Bean
	public JPanel headerJPanel() {
		return new JPanel();
	}
	@Bean
	public JPanel contentPane() {
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		contentPane.setOpaque(true);
		contentPane.add(headerJPanel());
		contentPane.add(subPanel(), BorderLayout.CENTER);
		return contentPane;
	}
	@Bean
	public JPanel subPanel() {
		return new JPanel(new CardLayout(0,0));
	}
	@Bean
	public JPanel filmListViewPanel() {
		return new JPanel();
	}
	@Bean
	public JPanel filmAddViewPanel() {
		return new JPanel();
	}
	@Bean
    public FilmListView filmListView() {
        return new FilmListView();
    }
	@Bean
    public FilmAddView filmAddView() {
        return new FilmAddView();
    }
	@Bean
	public JLabel nbrFilmsJLabel() {
        return new JLabel();
    }
	@Bean
	public JLabel nbrTmdbFilmsJLabel() {
        return new JLabel();
    }
	@Bean
	public JLabel savedTmdbFilmsJLabel() {
        return new JLabel();
    }
	@Bean
	public JTextField tmdbSearchTextField() {
		return new JTextField(50);
	}
	@Bean
	protected JButton addTmdbFilmButton() {
		return new JButton("Ajouter le film sélectionné");
	}
}
