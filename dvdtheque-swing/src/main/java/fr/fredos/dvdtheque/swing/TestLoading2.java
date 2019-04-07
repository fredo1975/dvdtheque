package fr.fredos.dvdtheque.swing;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

public class TestLoading2 {
	public void buildGUI() {
		JPanel p = new JPanel(new BorderLayout());
		final JButton btn = new JButton("Do Some Long Task");
		p.add(btn, BorderLayout.SOUTH);
		final JFrame f = new JFrame();
		f.getContentPane().add(p);
		f.setSize(400, 300);
		f.setLocationRelativeTo(null);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				final SpinnerDialog spinnerDialog = new SpinnerDialog(f);
				SwingWorker<?, ?> worker = new SwingWorker<Void, Integer>() {
					protected Void doInBackground() throws InterruptedException {
						for (int x = 0; x <= 100; x += 10) {
							publish(x);
							Thread.sleep(1000);
						}
						return null;
					}

					protected void process(List<Integer> chunks) {
						int selection = chunks.get(chunks.size() - 1);
						btn.setText("long task up to " + selection + "%");
					}

					protected void done() {
						spinnerDialog.dispose();
						btn.setText("Do Some Long Task");
					}
				};
				worker.execute();
				spinnerDialog.setVisible();
			}
		});
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			new TestLoading2().buildGUI();
		});
		
	}
}
