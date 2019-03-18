package fr.dvdtheque.console.vue;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.fredos.dvdtheque.swing.SpringUtilities;

public class VueTryLogin extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected final Log logger = LogFactory.getLog(VueTryLogin.class);
	
	final static int GAP = 10;
	private JLabel jLabel1 = new JLabel();
	JPanel jPanel;
	JButton button = new JButton("S'authentifier");
	JTextField loginField;
	JPasswordField mdpField;

	public VueTryLogin() {
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
		this.pack();
		//centerOnScreen(this);
		Dimension d=new Dimension(464 ,400);
		
		setSize(d);
		setMinimumSize(d);
		setMaximumSize(d);
		setPreferredSize(d);
		this.setVisible(true);
		
		JPanel pan = new JPanel();
		pan.add(createFields());
		pan.add(createButtons());
		pan.setOpaque(true);
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panel.add(pan);
		add(panel);
		logger.info(methodName + "end");
	}

	protected JComponent createButtons() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		JButton button = new JButton("S'authentifier");
		VueTryLogin_jButton_actionAdapter al = new VueTryLogin_jButton_actionAdapter(this);
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
	
	void jButton_actionPerformed(ActionEvent e) {
		// action [liste]
		String methodName = "jButton_actionPerformed ";
		logger.info(methodName + "loginField=" + loginField.getText());
		logger.info(methodName + "mdpField=" + mdpField.getText());
		//getSession().initializeUserLoginFields(loginField.getText(), mdpField.getText());
		
		//this.executeAction("trylogin");
	}
}

class VueTryLogin_jButton_actionAdapter implements java.awt.event.ActionListener {
	VueTryLogin adaptee;

	VueTryLogin_jButton_actionAdapter(VueTryLogin adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.jButton_actionPerformed(e);
	}
}