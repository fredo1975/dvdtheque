package fr.fredos.dvdtheque.swing;

import java.awt.CardLayout;

import javax.swing.JPanel;
import javax.swing.JTable;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.fredos.dvdtheque.swing.model.FilmTableModel;
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
	JTable filmListJTable() {
		return new JTable();
	}
	@Bean
	JPanel headerJPanel() {
		return new JPanel();
	}
	@Bean
	JPanel contentPane() {
		return new JPanel();
	}
	@Bean
	JPanel subPanel() {
		return new JPanel(new CardLayout());
	}
	@Bean
	JPanel filmListViewPanel() {
		return new JPanel();
	}
	@Bean
	JPanel filmAddViewPanel() {
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
}
