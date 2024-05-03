import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;
import java.util.Random;
import java.util.TimerTask;

public class Application extends JFrame {
    private static final String WIDTH_KEY = "width";
    private static final String HEIGHT_KEY = "height";
    private static final String POS_X = "x";
    private static final String POS_Y = "y";
    private Container cp;
    private static final int NUM_PLAZZE = 10;
    private static final int NUM_PIANONI = 3;
    private static JTextArea auto;
    private Preferences preferences;

    public Application() {
        super();
        cp = this.getContentPane();
        this.setTitle("Application");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        preferences = Preferences.userNodeForPackage(Application.class);
        int width = preferences.getInt(WIDTH_KEY, 300);
        int height = preferences.getInt(HEIGHT_KEY, 400);
        int posx = preferences.getInt(POS_X, 100);
        int posy = preferences.getInt(POS_Y, 100);

        this.setSize(800, 700);
        this.setLocation(posx, posy);
        this.setResizable(false);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                saveUserDimensions();
                System.exit(0);
            }
        });
        this.setupApp();
       avviaThreadAutoLibere(); 
    }

    private void setupApp() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem start = new JMenuItem("start Simulator");
        JMenuItem save = new JMenuItem("save");
        fileMenu.add(start);
        fileMenu.add(save);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        JPanel carPanel = new JPanel();
        carPanel.setLayout(new GridLayout(NUM_PIANONI, NUM_PLAZZE));
        carPanel.setPreferredSize(new Dimension(1500, 300));

        Random bool = new Random();
        for (int i = 0; i < NUM_PIANONI; i++) {
            for (int j = 0; j < NUM_PLAZZE; j++) {
                 auto = new JTextArea();
                auto.append("Piano " + (i + 1) + ", Posto " + (j + 1) + "\n");
                auto.append(" ");
                auto.setFont(new Font("Arial", Font.BOLD, 16));
                auto.setBackground(Color.WHITE);
                auto.setForeground(Color.BLACK);
                auto.setEditable(false);
                auto.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                if (bool.nextBoolean()) {
                    auto.append("Gia Occupato");
                    auto.setBackground(Color.RED);
                    auto.setForeground(Color.WHITE);
                } else {
                    auto.append("Libero");
                    auto.setBackground(Color.GREEN);
                    auto.setForeground(Color.BLACK);
                }
                carPanel.add(auto);
            }
        }
        start.addActionListener(e->{
            addCar();
        });
        

        cp.add(carPanel);
        setVisible(true);
        pack();
    }
   private void addCar() {
        Timer timer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] stringtarga = {"A", "B", "1", "E", "Y", "4", "6"};
                Random random = new Random();
                String stringTarga = "";
                for (int i = 0; i < stringtarga.length; i++) {
                    int targa = random.nextInt(7);
                    stringTarga += stringtarga[targa];
                }
                Auto nuovaAuto = new Auto(stringTarga);
    
                Component[] componenti = cp.getComponents();
                for (Component componente : componenti) {
                    if (componente instanceof JPanel) {
                        Component[] component = ((JPanel) componente).getComponents();
                        for (Component subcomponent : component) {
                            if (subcomponent instanceof JTextArea) {
                                JTextArea autoTextArea = (JTextArea) subcomponent;
                                synchronized (autoTextArea) {
                                    if (autoTextArea.getText().contains("Libero")) {
                                        autoTextArea.setText("Piano " + autoTextArea.getName() + ", Posto " + autoTextArea.getName() + "\n");
                                        autoTextArea.append("Occupato\n");
                                        autoTextArea.append("Targa: " + nuovaAuto.getTarga() + "\n");
                                        autoTextArea.setBackground(Color.RED);
                                        autoTextArea.setForeground(Color.WHITE);
                                        return;
                                    } 
                                }
                            }
                        }
                    }
                }
            }
        });
        timer.start();
    }

    
    
        

    //metodo che utilizza i thread per liberare i posti auto occupati.
    private void avviaThreadAutoLibere() {
        Thread liberoAutoThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(50000); 
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    liberareAuto();
                }
            }
        });
        liberoAutoThread.start();
    }

    private void liberareAuto() {
        // Genera casualmente quale JTextArea liberare
        Random random = new Random();
        boolean[] daLiberare = new boolean[NUM_PLAZZE * NUM_PIANONI];
        for (int i = 0; i < NUM_PLAZZE * NUM_PIANONI; i++) {
            daLiberare[i] = random.nextBoolean();
        }
    
        Component[] componenti = cp.getComponents();
        int postoCorrente = 0;
        for (Component componente : componenti) {
            if (componente instanceof JPanel) {
                Component[] component = ((JPanel) componente).getComponents();
                for (Component subcomponent : component) {
                    if (subcomponent instanceof JTextArea) {
                        JTextArea auto = (JTextArea) subcomponent;
                        if (auto.getText().contains("Occupato") && daLiberare[postoCorrente]) {
                            auto.setText("Piano " + auto.getName() + ", Posto " + auto.getName() + "\n" + "Libero");
                            auto.setBackground(Color.GREEN);
                            auto.setForeground(Color.BLACK);
                        }
                        postoCorrente++;
                    }
                }
            }
        }
    }
    
    

    public void saveUserDimensions() {
        preferences.putInt(WIDTH_KEY, getWidth());
        preferences.putInt(HEIGHT_KEY, getHeight());
        preferences.putInt(POS_X, getX());
        preferences.putInt(POS_Y, getY());
    }

    public void startApp(boolean packElements) {
        if (packElements)
            this.pack();
        this.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Application().startApp(false);
        });
    }
}
