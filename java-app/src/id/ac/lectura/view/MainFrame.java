package id.ac.lectura.view;

import id.ac.lectura.controller.AcademicController;
import id.ac.lectura.controller.AuthController;
import id.ac.lectura.exception.DataPersistenceException;
import id.ac.lectura.util.ThemePalette;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * JFrame utama aplikasi LECTURA (Java Swing MVC) bergaya Figma.
 */
public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final String CARD_LOGIN = "LOGIN";
    private static final String CARD_WORKSPACE = "WORKSPACE";

    private final AcademicController academicController;
    private final AuthController authController;

    private final CardLayout rootLayout = new CardLayout();
    private final JPanel rootContainer = new JPanel(rootLayout);

    private final CardLayout workspaceLayout = new CardLayout();
    private final JPanel workspaceContent = new JPanel(workspaceLayout);

    private SidebarPanel sidebarPanel;
    private JLabel lblBreadcrumb;
    private JMenu navMenu;

    public MainFrame(AcademicController academicController, AuthController authController) {
        this.academicController = academicController;
        this.authController = authController;
        initFrame();
    }

    private void initFrame() {
        setTitle("LECTURA - Sistem Penilaian Akademik Dosen");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1120, 700));
        setSize(1280, 780);
        setLocationRelativeTo(null);

        setJMenuBar(buildMenuBar());

        LoginPanel loginPanel = new LoginPanel(authController, this::showWorkspace);
        JPanel workspacePanel = buildWorkspacePanel();

        rootContainer.add(loginPanel, CARD_LOGIN);
        rootContainer.add(workspacePanel, CARD_WORKSPACE);
        setContentPane(rootContainer);

        showLogin();
    }

    private JPanel buildWorkspacePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        sidebarPanel = new SidebarPanel(
                academicController,
                authController,
                this::navigateTo,
                this::handleLogout
        );

        JPanel rightArea = new JPanel(new BorderLayout());
        rightArea.setBackground(ThemePalette.BG_LIGHT);

        // Top Breadcrumb Bar (matching Figma Images 2, 3, 4)
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setPreferredSize(new Dimension(0, 52));
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ThemePalette.BORDER_SOFT),
                BorderFactory.createEmptyBorder(0, 28, 0, 28)
        ));

        lblBreadcrumb = new JLabel("LECTURA  >  Beranda");
        lblBreadcrumb.setFont(ThemePalette.FONT_SMALL_BOLD);
        lblBreadcrumb.setForeground(ThemePalette.TEXT_MUTED);

        JLabel lblSemester = new JLabel("Semester Genap 2024/2025");
        lblSemester.setFont(ThemePalette.FONT_SMALL);
        lblSemester.setForeground(ThemePalette.TEXT_MUTED);

        topBar.add(lblBreadcrumb, BorderLayout.WEST);
        topBar.add(lblSemester, BorderLayout.EAST);

        DashboardPanel dashboardPanel = new DashboardPanel(academicController, authController, this::navigateTo);
        MataKuliahPanel mataKuliahPanel = new MataKuliahPanel(academicController);
        MahasiswaPanel mahasiswaPanel = new MahasiswaPanel(academicController);
        KomponenPenilaianPanel komponenPanel = new KomponenPenilaianPanel(academicController);
        InputNilaiPanel inputNilaiPanel = new InputNilaiPanel(academicController);
        ProfilePanel profilePanel = new ProfilePanel(authController, academicController);

        workspaceContent.add(dashboardPanel, SidebarPanel.VIEW_DASHBOARD);
        workspaceContent.add(mataKuliahPanel, SidebarPanel.VIEW_MATAKULIAH);
        workspaceContent.add(mahasiswaPanel, SidebarPanel.VIEW_MAHASISWA);
        workspaceContent.add(komponenPanel, SidebarPanel.VIEW_KOMPONEN);
        workspaceContent.add(inputNilaiPanel, SidebarPanel.VIEW_INPUT_NILAI);
        workspaceContent.add(profilePanel, SidebarPanel.VIEW_PROFILE);

        rightArea.add(topBar, BorderLayout.NORTH);
        rightArea.add(workspaceContent, BorderLayout.CENTER);

        panel.add(sidebarPanel, BorderLayout.WEST);
        panel.add(rightArea, BorderLayout.CENTER);
        return panel;
    }

    private JMenuBar buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem miSave = new JMenuItem("Simpan Semua Data");
        miSave.addActionListener(e -> {
            try {
                academicController.saveAll();
                JOptionPane.showMessageDialog(this, "Seluruh data berhasil disimpan ke folder data/.",
                        "Simpan Berhasil", JOptionPane.INFORMATION_MESSAGE);
            } catch (DataPersistenceException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Gagal Simpan", JOptionPane.ERROR_MESSAGE);
            }
        });

        JMenuItem miLogout = new JMenuItem("Keluar Sesi (Logout)");
        miLogout.addActionListener(e -> handleLogout());

        JMenuItem miExit = new JMenuItem("Tutup Aplikasi");
        miExit.addActionListener(e -> System.exit(0));

        fileMenu.add(miSave);
        fileMenu.addSeparator();
        fileMenu.add(miLogout);
        fileMenu.add(miExit);

        navMenu = new JMenu("Navigasi");
        addNavMenuItem(navMenu, "Beranda", SidebarPanel.VIEW_DASHBOARD);
        addNavMenuItem(navMenu, "Mahasiswa", SidebarPanel.VIEW_MAHASISWA);
        addNavMenuItem(navMenu, "Mata Kuliah", SidebarPanel.VIEW_MATAKULIAH);
        addNavMenuItem(navMenu, "Komponen Penilaian", SidebarPanel.VIEW_KOMPONEN);
        addNavMenuItem(navMenu, "Input & Rekap Nilai", SidebarPanel.VIEW_INPUT_NILAI);
        addNavMenuItem(navMenu, "Profil Dosen", SidebarPanel.VIEW_PROFILE);

        JMenu helpMenu = new JMenu("Bantuan");
        JMenuItem miAbout = new JMenuItem("Tentang LECTURA");
        miAbout.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "LECTURA - Sistem Penilaian Akademik Dosen\n"
                        + "Dibangun dengan Java Swing & Arsitektur MVC.",
                "Tentang Aplikasi",
                JOptionPane.INFORMATION_MESSAGE
        ));
        helpMenu.add(miAbout);

        menuBar.add(fileMenu);
        menuBar.add(navMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }

    private void addNavMenuItem(JMenu menu, String title, String viewKey) {
        JMenuItem item = new JMenuItem(title);
        item.addActionListener(e -> {
            if (authController.isLoggedIn()) {
                navigateTo(viewKey);
            }
        });
        menu.add(item);
    }

    private void navigateTo(String viewKey) {
        sidebarPanel.setActiveView(viewKey);
        workspaceLayout.show(workspaceContent, viewKey);
        String label;
        switch (viewKey) {
            case SidebarPanel.VIEW_MATAKULIAH: label = "Mata Kuliah"; break;
            case SidebarPanel.VIEW_MAHASISWA:  label = "Mahasiswa"; break;
            case SidebarPanel.VIEW_KOMPONEN:   label = "Komponen Penilaian"; break;
            case SidebarPanel.VIEW_INPUT_NILAI:label = "Input & Rekap Nilai"; break;
            case SidebarPanel.VIEW_PROFILE:    label = "Profil Dosen"; break;
            default:                           label = "Beranda"; break;
        }
        lblBreadcrumb.setText("LECTURA  >  " + label);
    }

    private void showWorkspace() {
        navMenu.setEnabled(true);
        rootLayout.show(rootContainer, CARD_WORKSPACE);
        navigateTo(SidebarPanel.VIEW_DASHBOARD);
    }

    private void showLogin() {
        navMenu.setEnabled(false);
        rootLayout.show(rootContainer, CARD_LOGIN);
    }

    private void handleLogout() {
        authController.logout();
        showLogin();
    }
}
