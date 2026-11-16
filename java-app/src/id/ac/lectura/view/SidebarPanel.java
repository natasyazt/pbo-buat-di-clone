package id.ac.lectura.view;

import id.ac.lectura.controller.AcademicController;
import id.ac.lectura.controller.AuthController;
import id.ac.lectura.exception.DataPersistenceException;
import id.ac.lectura.exception.ValidasiInputException;
import id.ac.lectura.model.Dosen;
import id.ac.lectura.model.MataKuliah;
import id.ac.lectura.util.ThemePalette;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Sidebar navigasi modern bergaya Figma dengan palet warna LECTURA.
 */
public class SidebarPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    public static final String VIEW_DASHBOARD = "dashboard";
    public static final String VIEW_MATAKULIAH = "matakuliah";
    public static final String VIEW_MAHASISWA = "mahasiswa";
    public static final String VIEW_KOMPONEN = "komponen";
    public static final String VIEW_INPUT_NILAI = "input-nilai";

    private final AcademicController controller;
    private final AuthController authController;
    private final Consumer<String> onNavigate;
    private final Runnable onLogout;

    private final Map<String, JButton> navButtons = new LinkedHashMap<>();
    private JComboBox<MataKuliah> cboActiveCourse;
    private boolean updatingCombo = false;
    private String activeView = VIEW_DASHBOARD;

    public SidebarPanel(AcademicController controller, AuthController authController,
                        Consumer<String> onNavigate, Runnable onLogout) {
        this.controller = controller;
        this.authController = authController;
        this.onNavigate = onNavigate;
        this.onLogout = onLogout;
        initUI();
        controller.addChangeListener(this::refreshData);
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(ThemePalette.SIDEBAR_BG);
        setPreferredSize(new Dimension(250, 720));
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ThemePalette.BORDER_SOFT));

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setOpaque(false);
        top.setBorder(BorderFactory.createEmptyBorder(20, 16, 12, 16));

        // Logo Row (matching Figma)
        JPanel logoPanel = new JPanel(new BorderLayout(10, 0));
        logoPanel.setOpaque(false);
        logoPanel.setMaximumSize(new Dimension(220, 42));
        logoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        ThemePalette.RoundedPanel icon = new ThemePalette.RoundedPanel(10, ThemePalette.NAVY, null);
        icon.setLayout(new BorderLayout());
        icon.setPreferredSize(new Dimension(38, 38));
        JLabel lblS = new JLabel("S", JLabel.CENTER);
        lblS.setFont(new Font("Georgia", Font.BOLD, 20));
        lblS.setForeground(ThemePalette.PEACH);
        icon.add(lblS, BorderLayout.CENTER);

        JPanel brandTxt = new JPanel(new GridLayout(2, 1));
        brandTxt.setOpaque(false);
        JLabel title = new JLabel("LECTURA");
        title.setFont(new Font("Georgia", Font.BOLD, 17));
        title.setForeground(ThemePalette.NAVY_DARK);
        JLabel sub = new JLabel("Sistem Penilaian Akademik");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 10));
        sub.setForeground(ThemePalette.TEXT_MUTED);
        brandTxt.add(title);
        brandTxt.add(sub);

        logoPanel.add(icon, BorderLayout.WEST);
        logoPanel.add(brandTxt, BorderLayout.CENTER);
        top.add(logoPanel);
        top.add(Box.createVerticalStrut(16));

        // Subtle divider
        JPanel div = new JPanel();
        div.setBackground(ThemePalette.BORDER_SOFT);
        div.setMaximumSize(new Dimension(220, 1));
        div.setAlignmentX(Component.LEFT_ALIGNMENT);
        top.add(div);
        top.add(Box.createVerticalStrut(14));

        // Navigation Items
        addNavButton(top, VIEW_DASHBOARD, "Beranda");
        addNavButton(top, VIEW_MAHASISWA, "Mahasiswa");
        addNavButton(top, VIEW_MATAKULIAH, "Mata Kuliah");
        addNavButton(top, VIEW_KOMPONEN, "Komponen Penilaian");
        addNavButton(top, VIEW_INPUT_NILAI, "Input & Rekap Nilai");

        top.add(Box.createVerticalStrut(14));

        // Active Course Quick Switcher
        JLabel lblCourse = new JLabel("MATA KULIAH AKTIF");
        lblCourse.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblCourse.setForeground(ThemePalette.TEXT_MUTED);
        lblCourse.setAlignmentX(Component.LEFT_ALIGNMENT);
        top.add(lblCourse);
        top.add(Box.createVerticalStrut(6));

        cboActiveCourse = new JComboBox<>();
        cboActiveCourse.setMaximumSize(new Dimension(218, 34));
        cboActiveCourse.setAlignmentX(Component.LEFT_ALIGNMENT);
        cboActiveCourse.setFont(ThemePalette.FONT_SMALL);
        cboActiveCourse.setBackground(Color.WHITE);
        cboActiveCourse.addActionListener(e -> {
            if (!updatingCombo) {
                MataKuliah mk = (MataKuliah) cboActiveCourse.getSelectedItem();
                if (mk != null) {
                    controller.setSelectedMataKuliahId(mk.getId());
                }
            }
        });
        top.add(cboActiveCourse);

        add(top, BorderLayout.NORTH);

        // Bottom: Help Card (dashed border like Figma) + User Profile
        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(8, 16, 16, 16));

        JPanel helpCard = new JPanel(new BorderLayout(0, 6)) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xEC, 0xE9, 0xF2));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.setColor(ThemePalette.PURPLE);
                g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                        10f, new float[]{4f, 4f}, 0f));
                g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 2, getHeight() - 2, 12, 12));
                g2.dispose();
            }
        };
        helpCard.setOpaque(false);
        helpCard.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        helpCard.setMaximumSize(new Dimension(218, 102));
        helpCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel hTitle = new JLabel("PUSAT BANTUAN");
        hTitle.setFont(new Font("SansSerif", Font.BOLD, 10));
        hTitle.setForeground(ThemePalette.PURPLE);
        JLabel hBody = new JLabel("<html>Butuh bantuan menggunakan LECTURA?</html>");
        hBody.setFont(ThemePalette.FONT_SMALL);
        hBody.setForeground(ThemePalette.TEXT_DARK);
        JButton btnGuide = new JButton("Pelajari Panduan");
        btnGuide.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnGuide.setForeground(ThemePalette.NAVY);
        btnGuide.setContentAreaFilled(false);
        btnGuide.setBorderPainted(false);
        btnGuide.setHorizontalAlignment(JButton.LEFT);
        btnGuide.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        btnGuide.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnGuide.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Panduan Singkat LECTURA:\n"
                        + "1. Pilih Mata Kuliah Aktif di sidebar.\n"
                        + "2. Atur Komponen & Bobot (total 100%).\n"
                        + "3. Input nilai mahasiswa di menu Input & Rekap Nilai.\n"
                        + "4. Seluruh data tersimpan otomatis ke folder data/.",
                "Panduan LECTURA", JOptionPane.INFORMATION_MESSAGE));

        helpCard.add(hTitle, BorderLayout.NORTH);
        helpCard.add(hBody, BorderLayout.CENTER);
        helpCard.add(btnGuide, BorderLayout.SOUTH);

        bottom.add(helpCard);
        bottom.add(Box.createVerticalStrut(12));

        // User Profile Card
        Dosen dosen = authController.getDosenAktif();
        JPanel profileRow = new JPanel(new BorderLayout(10, 0));
        profileRow.setOpaque(false);
        profileRow.setMaximumSize(new Dimension(218, 42));
        profileRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        ThemePalette.RoundedPanel avatar = new ThemePalette.RoundedPanel(32, ThemePalette.PEACH, null);
        avatar.setPreferredSize(new Dimension(34, 34));
        avatar.setLayout(new BorderLayout());
        JLabel lblRp = new JLabel("RP", JLabel.CENTER);
        lblRp.setFont(ThemePalette.FONT_SMALL_BOLD);
        lblRp.setForeground(ThemePalette.NAVY_DARK);
        avatar.add(lblRp, BorderLayout.CENTER);

        JPanel dTxt = new JPanel(new GridLayout(2, 1));
        dTxt.setOpaque(false);
        JLabel dName = new JLabel(dosen.getNama());
        dName.setFont(new Font("SansSerif", Font.BOLD, 11));
        dName.setForeground(ThemePalette.NAVY_DARK);
        JLabel dRole = new JLabel("Dosen Pengampu");
        dRole.setFont(new Font("SansSerif", Font.PLAIN, 10));
        dRole.setForeground(ThemePalette.TEXT_MUTED);
        dTxt.add(dName);
        dTxt.add(dRole);

        profileRow.add(avatar, BorderLayout.WEST);
        profileRow.add(dTxt, BorderLayout.CENTER);
        bottom.add(profileRow);
        bottom.add(Box.createVerticalStrut(10));

        JPanel actionRow = new JPanel(new GridLayout(1, 2, 8, 0));
        actionRow.setOpaque(false);
        actionRow.setMaximumSize(new Dimension(218, 32));
        actionRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnReset = ThemePalette.createSecondaryButton("Reset");
        btnReset.setFont(ThemePalette.FONT_SMALL_BOLD);
        btnReset.addActionListener(e -> handleReset());

        JButton btnLogout = ThemePalette.createDangerButton("Keluar");
        btnLogout.setFont(ThemePalette.FONT_SMALL_BOLD);
        btnLogout.addActionListener(e -> onLogout.run());

        actionRow.add(btnReset);
        actionRow.add(btnLogout);
        bottom.add(actionRow);

        add(bottom, BorderLayout.SOUTH);
        setActiveView(VIEW_DASHBOARD);
    }

    private void addNavButton(JPanel container, String viewKey, String label) {
        JButton btn = new JButton(label) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                boolean isActive = viewKey.equals(activeView);
                if (isActive) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(ThemePalette.NAVY);
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        btn.setHorizontalAlignment(JButton.LEFT);
        btn.setMaximumSize(new Dimension(218, 38));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setFont(ThemePalette.FONT_BOLD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        btn.addActionListener(e -> {
            setActiveView(viewKey);
            onNavigate.accept(viewKey);
        });
        navButtons.put(viewKey, btn);
        container.add(btn);
        container.add(Box.createVerticalStrut(4));
    }

    public void setActiveView(String viewKey) {
        this.activeView = viewKey;
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            boolean isActive = entry.getKey().equals(activeView);
            JButton b = entry.getValue();
            b.setForeground(isActive ? Color.WHITE : ThemePalette.TEXT_DARK);
            b.repaint();
        }
    }

    public void refreshData() {
        updatingCombo = true;
        cboActiveCourse.removeAllItems();
        String selectedId = controller.getSelectedMataKuliahId();
        MataKuliah toSelect = null;
        for (MataKuliah mk : controller.getMataKuliahList()) {
            cboActiveCourse.addItem(mk);
            if (mk.getId().equals(selectedId)) {
                toSelect = mk;
            }
        }
        if (toSelect != null) {
            cboActiveCourse.setSelectedItem(toSelect);
        }
        updatingCombo = false;
    }

    private void handleReset() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Kembalikan seluruh data ke nilai awal demo?",
                "Konfirmasi Reset Data",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controller.resetToDemoData();
            } catch (DataPersistenceException | ValidasiInputException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
