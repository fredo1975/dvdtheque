package fr.fredos.dvdtheque.swing;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.fredos.dvdtheque.swing.model.FilmTableModel;

@Configuration
public class SwingConfig {
	@Bean
    public FilmTableModel filmTableModel() {
        return new FilmTableModel();
    }
}
