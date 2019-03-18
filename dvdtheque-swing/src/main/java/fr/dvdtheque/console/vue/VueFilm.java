package fr.dvdtheque.console.vue;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.dvdtheque.console.table.RealisateurEntry;
import fr.dvdtheque.console.transferable.PersonneTransferable;
import fr.fredos.dvdtheque.dao.model.object.Dvd;
import fr.fredos.dvdtheque.dao.model.object.Film;
import fr.fredos.dvdtheque.dao.model.object.Personne;
import fr.fredos.dvdtheque.service.dto.PersonneDto;
import fr.fredos.dvdtheque.swing.SpringUtilities;

public class VueFilm extends BaseVueAppli {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Logger logger = LoggerFactory.getLogger(VueFilm.class);
	final static int GAP = 10;
	final static int ORIGIN_YEAR = 1915;
	private Film film;
	JPanel panel, panFilm, panUpdateFilm, panPersonne;
	Calendar cal;
	protected String[] labelFilmStrings = { "Titre: ", "Titre Original: ", "Réalisateur: ", "Année: ", "Dvd Zone: ",
			"Dvd Année: ","Ripped: ","Acteurs: "};
	protected String[] labelNewPersonnesStrings = { "Nom: ", "Prénom: " };

	protected String[] labelPersonneStrings = { "Personnes: " };
	protected JTextField titreField, titreOField, addNomField, updateNomField, addPrenomField, updatePrenomField,
			dateDeNaissanceField, paysField;
	protected JLabel titreLabel, titreOLabel, realisateurLabel, anneeLabel, zoneDvdLabel, anneDvdLabel, acteursLabel,
			nomLabel, prenomLabel, dateDeNaissanceLabel, paysLabel, rippedLabel,okkoPicLabel;
	protected JComboBox<Integer> filmYearComboBox, filmZoneDvdComboBox, filmYearDvdComboBox,rippedComboBox;
	protected JComboBox<RealisateurEntry> filmRealisateurComboBox;
	protected String[] filmYearStrings, filmZoneDvdStrings;
	protected JButton jButtonBackToList = new JButton("Retour à la liste des films");
	protected JButton jButtonUpdateFilm = new JButton("Modifier le film");
	protected JButton jButtonAddFilm = new JButton("Ajouter le film");
	protected JButton jButtonUpdatePersonne = new JButton("Modifier une personne");
	protected JButton jButtonAddPersonne = new JButton("Ajouter une personne");

	protected List<Personne> acteurList = null;
	protected JList<Personne> acteursJList, personneJList;
	protected JLabel updatedLabel, updatedPersonneLabel, addedPersonneLabel;
	protected JPanel buttonListPanel;
	BufferedImage koPic = null;
	BufferedImage okPic = null;
	
	public VueFilm() {
		String methodName = "VueListe constructor ";
		logger.info(methodName + "start");
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		try {
			init();
		} catch (Exception ex) {
			throw new RuntimeException(ex.toString());
		}
		logger.info(methodName + "end");
	}

	// Initialiser le composant
	protected void init() throws Exception {
		String methodName = "init ";
		logger.info(methodName + "start");
		try {
			koPic = ImageIO.read(this.getClass().getResource(BaseVueAppli.koPath));
			okPic = ImageIO.read(this.getClass().getResource(BaseVueAppli.okPath));
			cal = Calendar.getInstance();
			cal.setTime(new Date());
			filmYearStrings = new String[cal.get(Calendar.YEAR) - ORIGIN_YEAR + 1];
			int index = 0;
			for (int i = cal.get(Calendar.YEAR); i >= ORIGIN_YEAR; i--) {
				filmYearStrings[index++] = String.valueOf(i);
				// logger.info(methodName + "i="+i);
			}
			filmZoneDvdStrings = new String[3];
			filmZoneDvdStrings[0] = "1";
			filmZoneDvdStrings[1] = "2";
			filmZoneDvdStrings[2] = "3";
			filmYearComboBox = new JComboBox(filmYearStrings);
			filmYearDvdComboBox = new JComboBox(filmYearStrings);
			filmZoneDvdComboBox = new JComboBox(filmZoneDvdStrings);
			
			Object[] items = {new ImageIcon(okPic), new ImageIcon(koPic)};
			rippedComboBox = new JComboBox(items);
		    
			panel = initHeader();

			panFilm = new JPanel() {
				// Don't allow us to stretch vertically.
				public Dimension getMaximumSize() {
					Dimension pref = getPreferredSize();
					return new Dimension(pref.width, pref.height);
				}
			};
			// panFilm.setBorder(BorderFactory.createTitledBorder("Modifier un
			// Film"));
			panFilm.setLayout(new BoxLayout(panFilm, BoxLayout.Y_AXIS));
			panFilm.setAlignmentX(Component.CENTER_ALIGNMENT);

			updatedLabel = new JLabel("Film modifié");
			updatedLabel.setFont(new Font("", Font.BOLD, 20));
			updatedLabel.setVisible(false);
			updatedLabel.setForeground(Color.red);
			// updatedLabel.setPreferredSize(new Dimension(15, 15));

			jButtonBackToList.addActionListener(new VueInfos_jButtonBackToList_actionAdapter(this));
			jButtonUpdateFilm.addActionListener(new VueInfos_jButtonUpdateFilm_actionAdapter(this));
			jButtonAddFilm.addActionListener(new VueInfos_jButtonAddFilm_actionAdapter(this));
			buttonListPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			panel.add(jButtonBackToList);
			panel.add(updatedLabel);
		} catch (Exception e) {
			getSession().getErreurs().add(e.getMessage());
			e.printStackTrace();
		}
		logger.info(methodName + "end");
	}

	protected JComponent createLoggedAddPersonneEntryFields() {
		String methodName = "createLoggedAddPersonneEntryFields ";
		logger.info(methodName + "start");

		JPanel thePanel = new JPanel();
		thePanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		// c.weightx = 0.5;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		addedPersonneLabel = new JLabel("Personne ajoutée");
		addedPersonneLabel.setFont(new Font("", Font.BOLD, 10));
		addedPersonneLabel.setVisible(false);
		addedPersonneLabel.setForeground(Color.red);

		JPanel localPanel = new JPanel();
		localPanel.setLayout(new BoxLayout(localPanel, BoxLayout.Y_AXIS));
		localPanel.setBorder(BorderFactory.createTitledBorder("Ajouter une personne"));
		localPanel.add(addedPersonneLabel);

		JPanel localPanel1 = new JPanel(new SpringLayout());

		JLabel labelAddNom = new JLabel(labelNewPersonnesStrings[0], JLabel.TRAILING);
		addNomField = new JTextField();
		addNomField.setColumns(20);
		labelAddNom.setLabelFor(addNomField);
		localPanel1.add(labelAddNom);
		localPanel1.add(addNomField);

		JLabel labelAddPrenom = new JLabel(labelNewPersonnesStrings[1], JLabel.TRAILING);
		addPrenomField = new JTextField();
		addPrenomField.setColumns(20);
		labelAddPrenom.setLabelFor(addPrenomField);
		localPanel1.add(labelAddPrenom);
		localPanel1.add(addPrenomField);

		// localPanel1.add(jButtonAddPersonne);
		// localPanel1.add(new JPanel());
		try {
			SpringUtilities.makeCompactGrid(localPanel1, 2, 2, GAP, GAP, GAP, GAP / 2);// xpad,
																						// ypad
		} catch (Exception e) {
			e.printStackTrace();
			getSession().getErreurs().add(e.getMessage());
		}
		localPanel.add(localPanel1);
		jButtonAddPersonne.addActionListener(new VueInfos_jButtonAddPersonne_actionAdapter(this));
		localPanel.add(jButtonAddPersonne);
		thePanel.add(localPanel, c);

		updatedPersonneLabel = new JLabel("Personne modifiée");
		updatedPersonneLabel.setFont(new Font("", Font.BOLD, 10));
		updatedPersonneLabel.setVisible(false);
		updatedPersonneLabel.setForeground(Color.red);

		JPanel localPanel_ = new JPanel();
		localPanel_.setLayout(new BoxLayout(localPanel_, BoxLayout.Y_AXIS));
		localPanel_.setBorder(BorderFactory.createTitledBorder("Modifier une personne"));
		localPanel_.add(updatedPersonneLabel);
		JPanel localPanel2 = new JPanel(new SpringLayout());

		JLabel labelUpdateNom = new JLabel(labelNewPersonnesStrings[0], JLabel.TRAILING);
		updateNomField = new JTextField();
		updateNomField.setColumns(20);
		labelUpdateNom.setLabelFor(updateNomField);
		localPanel2.add(labelUpdateNom);
		localPanel2.add(updateNomField);

		JLabel labelUpdatePrenom = new JLabel(labelNewPersonnesStrings[1], JLabel.TRAILING);
		updatePrenomField = new JTextField();
		updatePrenomField.setColumns(20);
		labelUpdatePrenom.setLabelFor(updatePrenomField);
		localPanel2.add(labelUpdatePrenom);
		localPanel2.add(updatePrenomField);
		try {
			SpringUtilities.makeCompactGrid(localPanel2, 2, 2, GAP, GAP, GAP, GAP / 2);// xpad,
																						// ypad
		} catch (Exception e) {
			e.printStackTrace();
			getSession().getErreurs().add(e.getMessage());
		}
		localPanel_.add(localPanel2);
		localPanel_.add(jButtonUpdatePersonne);
		jButtonUpdatePersonne.addActionListener(new VueInfos_jButtonUpdatePersonne_actionAdapter(this));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		thePanel.add(localPanel_, c);

		logger.info(methodName + "end");
		return thePanel;
	}

	protected JComponent createLoggedPersonneEntryFields() {
		String methodName = "createLoggedPersonneEntryFields ";
		logger.info(methodName + "start");
		JPanel panel = new JPanel(new SpringLayout());
		JLabel[] labels = new JLabel[labelPersonneStrings.length];
		JComponent[] fields = new JComponent[labelPersonneStrings.length];
		int fieldNum = 0;
		Dimension scollerDim = new Dimension(250, 400);
		try {
			List<Personne> personneDtoList = getSession().getPersonneService().findAllPersonne();
			if (CollectionUtils.isNotEmpty(personneDtoList)) {
				DefaultListModel<Personne> personneListModel = new DefaultListModel<Personne>();
				for (Personne personne : personneDtoList) {
					personneListModel.addElement(personne);
				}
				personneJList = new JList<Personne>(personneListModel);
				personneJList.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						Personne p = personneJList.getSelectedValue();
						updateNomField.setText(p.getNom());
						updatePrenomField.setText(p.getPrenom());
						updatedPersonneLabel.setVisible(false);
						addedPersonneLabel.setVisible(false);
						addPrenomField.setText("");
						addNomField.setText("");
					}
				});
				personneJList.clearSelection();
				personneJList.setDragEnabled(true);
				personneJList.setTransferHandler(new FromPersonneTransferHandler(personneJList));
				personneJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			}
			JScrollPane listScrollPane = new JScrollPane(personneJList);
			listScrollPane.setPreferredSize(scollerDim);
			fields[fieldNum++] = listScrollPane;
			for (int i = 0; i < labelPersonneStrings.length; i++) {
				labels[i] = new JLabel(labelPersonneStrings[i], JLabel.TRAILING);
				labels[i].setLabelFor(fields[i]);
				panel.add(labels[i]);
				panel.add(fields[i]);
			}
			SpringUtilities.makeCompactGrid(panel, labelPersonneStrings.length, 2, GAP, GAP, GAP, GAP / 2);// xpad,
																											// ypad
		} catch (Exception e) {
			e.printStackTrace();
			getSession().getErreurs().add(e.getMessage());
		}
		logger.info(methodName + "end");
		return panel;
	}

	protected JComponent createLoggedFilmEntryFields() {
		String methodName = "createLoggedFilmEntryFields ";
		logger.debug(methodName + "start");
		JPanel panel = new JPanel(new SpringLayout());
		JLabel[] labels = new JLabel[labelFilmStrings.length];
		JComponent[] fields = new JComponent[labelFilmStrings.length];

		int fieldNum = 0;
		RealisateurEntry realisateurEntrySelected = null;
		Dimension scollerDim = new Dimension(250, 600);
		List<Personne> actList = new ArrayList<Personne>();
		DefaultListModel<Personne> acteurListModel = null;
		Map<Long, RealisateurEntry> realisateurEntryMap = null;
		Map<Personne, Integer> acteurIndiceMap = null;
		if (film == null) {
			realisateurEntryMap = new HashMap<>(1);
			acteurIndiceMap = new HashMap<Personne, Integer>();
		} else {
			realisateurEntryMap = new HashMap<>(film.getRealisateurs().size());
			acteurIndiceMap = new HashMap<Personne, Integer>(film.getActeurs().size());
		}

		updatedLabel.setVisible(false);
		// Create the text field and set it up.
		titreField = new JTextField();
		titreField.setColumns(20);
		if (null != film && null != film.getTitre()) {
			titreField.setText(film.getTitre());
		}
		fields[fieldNum++] = titreField;
		titreOField = new JTextField();
		titreOField.setColumns(20);
		if (null != film && null != film.getTitreO()) {
			titreOField.setText(film.getTitreO());
		}
		fields[fieldNum++] = titreOField;
		acteurListModel = new DefaultListModel<Personne>();
		try {
			filmRealisateurComboBox = new JComboBox<RealisateurEntry>();
			if (null != getSession().getFilm()) {
				Set<Personne> realisateurs = film.getRealisateurs();
				if (CollectionUtils.isNotEmpty(realisateurs) && realisateurs.size() == 1) {
					Personne realisateur = realisateurs.iterator().next();
					if (null != realisateur.getPrenom() && null != realisateur.getNom()) {
						RealisateurEntry realisateurEntry = new RealisateurEntry(realisateur);
						realisateurEntrySelected = realisateurEntry;
						filmRealisateurComboBox.addItem(realisateurEntry);
						realisateurEntryMap.put(realisateur.getId(), realisateurEntry);
						// acteurIndiceMap.put(p, index++);
					}
				}
				if (CollectionUtils.isNotEmpty(film.getActeurs())) {
					Integer i = 0;
					for (Personne acteur : film.getActeurs()) {
						actList.add(acteur);
						acteurIndiceMap.put(acteur, i++);
						acteurListModel.addElement(acteur);
					}
				}
			}
			acteursJList = new JList(acteurListModel);
			acteursJList.clearSelection();
			acteursJList.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					logger.info(e.toString());
					if (SwingUtilities.isRightMouseButton(e)) {
						logger.info("Row: isRightMouseButton");
						acteursJList.setSelectedIndex(acteursJList.locationToIndex(e.getPoint()));
						JMenuItem menuItem;
						JPopupMenu popup = new JPopupMenu();
						menuItem = new JMenuItem("Enlever cet acteur");
						menuItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent ee) {
								Personne p = acteursJList.getSelectedValue();
								int index = acteursJList.getSelectedIndex();
								if (index >= 0) { // Remove only if a
													// particular item is
													// selected
									DefaultListModel<Personne> am = (DefaultListModel<Personne>) acteursJList
											.getModel();
									am.removeElementAt(index);
									film.getActeurs().remove(p);
								}
								logger.info("" + p.toString());
							}
						});
						popup.add(menuItem);
						popup.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			});
			acteursJList.setTransferHandler(new ToActeurTransferHandler(TransferHandler.MOVE));
			acteursJList.setDropMode(DropMode.INSERT);

			for (Personne personne : getSession().getPersonneService().findAllPersonne()) {
				RealisateurEntry realisateurEntry = new RealisateurEntry(personne);
				filmRealisateurComboBox.addItem(realisateurEntry);
			}
			fields[fieldNum++] = filmRealisateurComboBox;
			if (null != film && null != film.getAnnee()) {
				filmYearComboBox.setSelectedIndex(cal.get(Calendar.YEAR) - film.getAnnee());
			}
			logger.info(methodName + "yearJList.getSelectedIndex()=" + filmYearComboBox.getSelectedIndex());
			fields[fieldNum++] = filmYearComboBox;
			if (null != film && null != film.getDvd().getZone()) {
				filmZoneDvdComboBox.setSelectedIndex(film.getDvd().getZone() - 1);
			}
			fields[fieldNum++] = filmZoneDvdComboBox;
			if (null != film && null != film.getDvd().getAnnee()) {
				filmYearDvdComboBox.setSelectedIndex(cal.get(Calendar.YEAR) - film.getDvd().getAnnee());
			}
			fields[fieldNum++] = filmYearDvdComboBox;

			if(film != null) {
				if(film.isRipped()) {
					okPic = ImageIO.read(this.getClass().getResource(okPath));
					rippedComboBox.setSelectedIndex(0);
				}else {
					koPic = ImageIO.read(this.getClass().getResource(koPath));
					rippedComboBox.setSelectedIndex(1);
				}
			}
			fields[fieldNum++] = rippedComboBox;
			
			JScrollPane listScrollPane = new JScrollPane(acteursJList);
			listScrollPane.setPreferredSize(scollerDim);
			if (CollectionUtils.isNotEmpty(acteurList)) {
				int[] indicesList = new int[acteurList.size()];
				int i = 0;
				for (Personne acteur : acteurList) {
					indicesList[i++] = acteurIndiceMap.get(acteur);
				}
			}
			fields[fieldNum++] = listScrollPane;
			for (int i = 0; i < labelFilmStrings.length; i++) {
				labels[i] = new JLabel(labelFilmStrings[i], JLabel.TRAILING);
				labels[i].setLabelFor(fields[i]);
				panel.add(labels[i]);
				panel.add(fields[i]);
			}
			SpringUtilities.makeCompactGrid(panel, labelFilmStrings.length, 2, GAP, GAP, // init x,y
					GAP, GAP / 2);// xpad, ypad
		} catch (Exception e) {
			e.printStackTrace();
			getSession().getErreurs().add(e.getMessage());
		}
		logger.debug(methodName + "end");
		return panel;
	}

	protected JComponent createNotLoggedFilmEntryFields() throws IOException {
		String methodName = "createNotLoggedFilmEntryFields ";
		logger.debug(methodName + "start");
		cal = Calendar.getInstance();
		cal.setTime(new Date());
		JPanel panel = new JPanel(new SpringLayout());
		// panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

		JLabel[] labels = new JLabel[labelFilmStrings.length];
		JComponent[] fields = new JComponent[labelFilmStrings.length];
		int fieldNum = 0;
		titreLabel = new JLabel();
		if (null != film.getTitre()) {
			titreLabel.setText(film.getTitre());
		}
		logger.debug(methodName + " ########### titreLabel.getText()=" + titreLabel.getText());

		fields[fieldNum++] = titreLabel;

		titreOLabel = new JLabel();
		if (null != film.getTitreO()) {
			titreOLabel.setText(film.getTitreO());
		}
		fields[fieldNum++] = titreOLabel;

		realisateurLabel = new JLabel();
		Set<Personne> realisateurs = film.getRealisateurs();
		if (CollectionUtils.isNotEmpty(realisateurs) && realisateurs.size() == 1) {
			Personne realisateur = realisateurs.iterator().next();
			if (null != realisateur.getPrenom() && null != realisateur.getNom()) {
				realisateurLabel.setText(realisateur.getPrenom() + " " + realisateur.getNom());
			}
		}
		fields[fieldNum++] = realisateurLabel;

		anneeLabel = new JLabel();
		if (null != film.getAnnee()) {
			anneeLabel.setText(film.getAnnee().toString());
		}

		fields[fieldNum++] = anneeLabel;
		zoneDvdLabel = new JLabel();
		if (null != film.getDvd().getZone()) {
			zoneDvdLabel.setText(film.getDvd().getZone().toString());
		}
		fields[fieldNum++] = zoneDvdLabel;
		anneDvdLabel = new JLabel();
		if (null != film.getDvd().getAnnee()) {
			anneDvdLabel.setText(film.getDvd().getAnnee().toString());
		}
		fields[fieldNum++] = anneDvdLabel;
		
		JLabel rippedLabel = null;
		BufferedImage okkoPic = null;
		if(film.isRipped()) {
			okkoPic = ImageIO.read(this.getClass().getResource(okPath));
		}else {
			okkoPic = ImageIO.read(this.getClass().getResource(koPath));
		}
		rippedLabel = new JLabel(new ImageIcon(okkoPic));
		fields[fieldNum++] = rippedLabel;
		
		if (null != acteurList) {
			JPanel jp = new JPanel(new SpringLayout());
			
			if (acteurList.size() == 0) {
				acteursLabel = new JLabel();
				acteursLabel.setText("");
				jp.add(acteursLabel);
			}
			for (Personne acteur : acteurList) {
				acteursLabel = new JLabel();
				acteursLabel.setText(acteur.getPrenom() + " " + acteur.getNom());
				jp.add(acteursLabel);
			}
			SpringUtilities.makeCompactGrid(jp, acteurList.size(), 1, GAP, GAP, // init
																				// x,y
					GAP, GAP / 2);// xpad, ypad
			fields[fieldNum++] = jp;
		}
		
		for (int i = 0; i < labelFilmStrings.length; i++) {
			labels[i] = new JLabel(labelFilmStrings[i], JLabel.TRAILING);
			labels[i].setLabelFor(fields[i]);
			panel.add(labels[i]);
			panel.add(fields[i]);
		}
		SpringUtilities.makeCompactGrid(panel, labelFilmStrings.length, 2, GAP, GAP, // init
																						// x,y
				GAP, GAP / 2);// xpad, ypad
		logger.debug(methodName + "end");
		return panel;
	}

	@Override
	public void affiche() {
		String methodName = "affiche ";
		logger.info(methodName + "start");
		try {
			JPanel panFilmIntermediate = new JPanel() {
				// Don't allow us to stretch vertically.
				public Dimension getMaximumSize() {
					Dimension pref = getPreferredSize();
					return new Dimension(pref.width, pref.height);
				}
			};
			panFilmIntermediate.setLayout(new BoxLayout(panFilmIntermediate, BoxLayout.X_AXIS));
			panFilmIntermediate.setAlignmentX(Component.CENTER_ALIGNMENT);
			panFilm.removeAll();
			panFilm.updateUI();
			if (null != getSession().getFilm()) {
				film = getSession().getFilm();
				acteurList = new ArrayList<Personne>(film.getActeurs());
			}
			if (null != getSession().getUser()) {
				if (null != getSession().getFilm())
					logger.info(methodName + "null != getSession().getUser() logged in and film selected="
							+ getSession().getFilm().getTitre());
				panFilm.setBorder(BorderFactory.createTitledBorder("Modifier un Film"));
				panFilmIntermediate.add(createLoggedFilmEntryFields());
				panFilmIntermediate.add(createLoggedPersonneEntryFields());
				panFilmIntermediate.add(createLoggedAddPersonneEntryFields());
			} else {
				if (null != getSession().getFilm())
					logger.info(methodName
							+ "null != getSession().getUser() || null != getSession().getFilm() not logged and film selected="
							+ getSession().getFilm().getTitre());
				panFilmIntermediate.add(createNotLoggedFilmEntryFields());
			}

			panFilm.add(panFilmIntermediate);
			if (null != getSession().getUser()) {
				JPanel buttonUpdateFilmPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
				if(null != getSession().getFilm()){
					buttonUpdateFilmPanel.add(jButtonUpdateFilm);
					panFilm.add(buttonUpdateFilmPanel);
				}else{
					buttonUpdateFilmPanel.add(jButtonAddFilm);
					panFilm.add(buttonUpdateFilmPanel);
				}
			}
			panFilm.setOpaque(true);
			panel.add(panFilm);
			// panel.add(buttonListPanel);
			this.getContentPane().add(panel);
		} catch (Exception e) {
			getSession().getErreurs().add(e.getMessage());
			e.printStackTrace();
		}
		super.affiche();
		logger.info(methodName + "end");
	}

	void jButtonBackToList_actionPerformed(ActionEvent e) {
		super.executeAction("liste");
	}

	void jButtonUpdateFilm_actionPerformed(ActionEvent e) {
		film.setTitre(titreField.getText());
		film.setTitreO(titreOField.getText());
		RealisateurEntry realisateur = (RealisateurEntry) filmRealisateurComboBox.getSelectedItem();
		Set<Personne> set = new HashSet<Personne>();
		set.add(realisateur.getRealisateur());
		film.setRealisateurs(set);
		Set<Personne> acteurs = new HashSet<Personne>();
		DefaultListModel<Personne> dm = (DefaultListModel<Personne>) acteursJList.getModel();
		for (int i = 0; i < dm.size(); i++) {
			acteurs.add(dm.getElementAt(i));
		}
		film.setActeurs(acteurs);
		if(rippedComboBox.getSelectedIndex()==0) {
			film.setRipped(true);
		}else{
			film.setRipped(false);
		}
		getSession().getFilmService().updateFilm(film);
		updatedLabel.setVisible(true);
	}
	
	void jButtonAddFilm_actionPerformed(ActionEvent e) {
		film = new Film();
		film.setTitre(titreField.getText());
		film.setTitreO(titreOField.getText());
		film.setAnnee(Integer.parseInt((String) filmYearComboBox.getSelectedItem()));
		Dvd dvd = new Dvd();
		dvd.setAnnee(Integer.parseInt((String) filmYearDvdComboBox.getSelectedItem()));
		dvd.setZone(Integer.parseInt((String) filmZoneDvdComboBox.getSelectedItem()));
		film.setDvd(dvd);
		RealisateurEntry realisateur = (RealisateurEntry) filmRealisateurComboBox.getSelectedItem();
		Set<Personne> set = new HashSet<Personne>();
		set.add(realisateur.getRealisateur());
		film.setRealisateurs(set);
		Set<Personne> acteurs = new HashSet<Personne>();
		DefaultListModel<Personne> dm = (DefaultListModel<Personne>) acteursJList.getModel();
		for (int i = 0; i < dm.size(); i++) {
			acteurs.add(dm.getElementAt(i));
		}
		film.setActeurs(acteurs);
		getSession().getFilmService().updateFilm(film);
		updatedLabel.setVisible(true);
	}

	void jButtonUpdatePersonne_actionPerformed(ActionEvent e) throws Exception {
		updatedPersonneLabel.setVisible(true);
		Personne selectedPersonne = personneJList.getSelectedValue();
		int index = personneJList.getSelectedIndex();
		selectedPersonne.setPrenom(updatePrenomField.getText());
		selectedPersonne.setNom(updateNomField.getText());
		PersonneDto personneDto = PersonneDto.toDto(selectedPersonne);
		getSession().getPersonneService().updatePersonne(selectedPersonne);
		DefaultListModel<Personne> dm = (DefaultListModel<Personne>) personneJList.getModel();
		dm.removeElementAt(index);
		dm.add(index, PersonneDto.fromDto(personneDto));
	}

	void jButtonAddPersonne_actionPerformed(ActionEvent e) throws Exception {
		addedPersonneLabel.setVisible(true);
		//Personne personne = new Personne(addPrenomField.getText(), addNomField.getText());
		Personne personne = new Personne();
		
		Long personneId = getSession().getPersonneService().savePersonne(personne);
		personne.setId(personneId);
		DefaultListModel<Personne> dm = (DefaultListModel<Personne>) personneJList.getModel();
		dm.addElement(personne);
	}
}

class VueInfos_jButtonBackToList_actionAdapter implements java.awt.event.ActionListener {
	VueFilm adaptee;

	public VueInfos_jButtonBackToList_actionAdapter(VueFilm adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.jButtonBackToList_actionPerformed(e);
	}

}

class VueInfos_jButtonUpdateFilm_actionAdapter implements java.awt.event.ActionListener {
	VueFilm adaptee;

	public VueInfos_jButtonUpdateFilm_actionAdapter(VueFilm adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.jButtonUpdateFilm_actionPerformed(e);
	}

}

class VueInfos_jButtonAddFilm_actionAdapter implements java.awt.event.ActionListener {
	VueFilm adaptee;

	public VueInfos_jButtonAddFilm_actionAdapter(VueFilm adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.jButtonAddFilm_actionPerformed(e);
	}

}

class VueInfos_jButtonUpdatePersonne_actionAdapter implements java.awt.event.ActionListener {
	VueFilm adaptee;

	public VueInfos_jButtonUpdatePersonne_actionAdapter(VueFilm adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		try {
			adaptee.jButtonUpdatePersonne_actionPerformed(e);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}

class VueInfos_jButtonAddPersonne_actionAdapter implements java.awt.event.ActionListener {
	VueFilm adaptee;

	public VueInfos_jButtonAddPersonne_actionAdapter(VueFilm adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		try {
			adaptee.jButtonAddPersonne_actionPerformed(e);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}

class ToActeurTransferHandler extends TransferHandler {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int action;

	public ToActeurTransferHandler(int action) {
		this.action = action;
	}

	@Override
	public boolean canImport(TransferSupport support) {
		return support.isDataFlavorSupported(PersonneTransferable.LIST_PERSONNE_DATA_FLAVOR);

	}

	@Override
	public boolean importData(TransferSupport support) {
		boolean accept = false;
		// if we can't handle the import, say so
		if (!canImport(support)) {
			return false;
		}
		try {
			Transferable t = support.getTransferable();
			Object value = t.getTransferData(PersonneTransferable.LIST_PERSONNE_DATA_FLAVOR);
			if (value instanceof Personne) {
				Component component = support.getComponent();
				if (component instanceof JList<?>) {
					DefaultListModel<Personne> dm = (DefaultListModel<Personne>) ((JList<Personne>) component)
							.getModel();
					Personne p = (Personne) value;
					dm.addElement(p);
					accept = true;
				}
			}
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return accept;
	}

}

class FromPersonneTransferHandler extends TransferHandler {

	private static final long serialVersionUID = -6349822068361266951L;
	private JList<Personne> personneJList;
	private int index = 0;
	DefaultListModel<Personne> dm;

	public FromPersonneTransferHandler(JList<Personne> personneJList) {
		this.personneJList = personneJList;
	}

	@Override
	protected Transferable createTransferable(JComponent c) {
		index = personneJList.getSelectedIndex();
		dm = (DefaultListModel<Personne>) personneJList.getModel();
		if (index < 0 || index >= dm.getSize()) {
			return null;
		}
		Transferable t = null;
		if (c instanceof JList) {
			JList<Personne> list = (JList<Personne>) c;
			Object value = list.getSelectedValue();
			if (value instanceof Personne) {
				Personne p = (Personne) value;
				t = new PersonneTransferable(p);
			}
		}
		return t;
	}

	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {
		if (action != MOVE) {
			return;
		}
		dm.removeElementAt(index);
	}

	@Override
	public int getSourceActions(JComponent c) {
		return COPY;
	}
}
