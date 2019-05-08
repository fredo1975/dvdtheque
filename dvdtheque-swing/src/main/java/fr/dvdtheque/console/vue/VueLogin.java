package fr.dvdtheque.console.vue;

import java.awt.AWTEvent;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.fredos.dvdtheque.swing.SpringUtilities;

public class VueLogin extends BaseVueAppli {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected final Log logger = LogFactory.getLog(VueLogin.class);
	final static int GAP = 10;
	private JLabel jLabel1 = new JLabel();
	JPanel jPanel;
	JButton button = new JButton("S'authentifier");
	JTextField loginField;
	JPasswordField mdpField;

	public VueLogin() {
		String methodName = "VueLogin constructor ";
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
	private void init() throws Exception {
		String methodName = "init ";
		logger.info(methodName + "start");
		
		JPanel pan = initHeader();
		pan.add(createFields());
		pan.add(createButtons());
		pan.setOpaque(true);
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panel.add(pan);
		contentPane.add(panel);
		logger.info(methodName + "end");
	}

	protected JComponent createButtons() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		JButton button = new JButton("S'authentifier");
		VueLogin_jButton_actionAdapter al = new VueLogin_jButton_actionAdapter(this);
		button.addActionListener(al);
		panel.add(button);

		// Match the SpringLayout's gap, subtracting 5 to make
		// up for the default gap FlowLayout provides.
		panel.setBorder(BorderFactory.createEmptyBorder(0, 0, GAP, GAP));
		return panel;
	}

	public JComponent createFields() {
		JPanel gpanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JPanel panel = new JPanel(new SpringLayout());
		String[] labelStrings = { "Login: ", "Mot de Passe: " };

		JLabel[] labels = new JLabel[labelStrings.length];
		JComponent[] fields = new JComponent[labelStrings.length];
		int fieldNum = 0;
		loginField = new JTextField(20);
		loginField.setText("");
		fields[fieldNum++] = loginField;
		mdpField = new JPasswordField(20);
		mdpField.setText("");
		fields[fieldNum++] = mdpField;
		for (int i = 0; i < labelStrings.length; i++) {
			labels[i] = new JLabel(labelStrings[i], JLabel.TRAILING);
			labels[i].setLabelFor(fields[i]);
			panel.add(labels[i]);
			panel.add(fields[i]);
		}
		
		SpringUtilities.makeCompactGrid(panel, labelStrings.length, 2, GAP,
				GAP, // init x,y
				GAP, GAP / 2);// xpad, ypad
		gpanel.add(panel);
		return gpanel;
	}

	@Override
	public void affiche() {
		String methodName = "affiche ";
		logger.info(methodName + "start");
		//jLabel1.setText(getSession().getMessage());
		super.affiche();
		logger.info(methodName + "end");
	}
	protected void executeAction(String action) {
		String methodName = "executeAction ";
		logger.info(methodName + "action=" + action);
		// fait exécuter [action] par le contrôleur
		this.setTitle("Dvdtheque : patientez...");
		// action asynchrone - on gêle le formulaire
		this.setEnabled(false);
		// on passe la main à la classe parent
		super.execute(action);
		logger.info(methodName + "end");
	}
	
	void jButton_actionPerformed(ActionEvent e) {
		// action [liste]
		String methodName = "jButton_actionPerformed ";
		logger.info(methodName + "loginField=" + loginField.getText());
		logger.info(methodName + "mdpField=" + mdpField.getText());
		//getSession().initializeUserLoginFields(loginField.getText(), mdpField.getText());
		//getSession().initializeUserLoginFields("fredo", "fredo");
		jMenuItemNouveauFilm.setVisible(true);
		this.executeAction("trylogin");
	}
}

class VueLogin_jButton_actionAdapter implements java.awt.event.ActionListener {
	VueLogin adaptee;

	VueLogin_jButton_actionAdapter(VueLogin adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.jButton_actionPerformed(e);
	}
}
