package id.ac.lectura.view;

import id.ac.lectura.controller.AuthController;
import id.ac.lectura.exception.ValidasiInputException;
import id.ac.lectura.util.ThemePalette;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Tampilan Login LECTURA bergaya Figma dengan palet warna LECTURA.
 */
public class LoginPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final AuthController authController;
    private final Runnable onLoginSuccess;

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JCheckBox chkShowPassword;
    private JCheckBox chkRememberMe;

    public LoginPanel(AuthController authController, Runnable onLoginSuccess) {
        this.authController = authController;
        this.onLoginSuccess = onLoginSuccess;
        initUI();
    }

    private void initUI() {
        setLayout(new GridLayout(1, 2));
        setBackground(ThemePalette.BG_LIGHT);
        add(buildLeftBrandPanel());
        add(buildRightFormPanel());
    }

    private JPanel buildLeftBrandPanel() {
        JPanel panel = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, ThemePalette.NAVY_DARK,
                        getWidth(), getHeight(), ThemePalette.NAVY);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(36, 44, 28, 44));

        // Logo row
        JPanel logoRow = new JPanel(new BorderLayout(12, 0));
        logoRow.setOpaque(false);
        logoRow.setMaximumSize(new Dimension(420, 44));
        logoRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        ThemePalette.RoundedPanel badge = new ThemePalette.RoundedPanel(10, ThemePalette.PEACH, null);
        badge.setLayout(new BorderLayout());
        badge.setPreferredSize(new Dimension(42, 42));
        JLabel lblS = new JLabel("S", JLabel.CENTER);
        lblS.setFont(new Font("Georgia", Font.BOLD, 22));
        lblS.setForeground(ThemePalette.NAVY_DARK);
        badge.add(lblS, BorderLayout.CENTER);

        JPanel brandText = new JPanel(new GridLayout(2, 1));
        brandText.setOpaque(false);
        JLabel title = new JLabel("LECTURA");
        title.setFont(new Font("Georgia", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        JLabel subtitle = new JLabel("Sistem Penilaian Akademik");
        subtitle.setFont(ThemePalette.FONT_SMALL);
        subtitle.setForeground(ThemePalette.PEACH);
        brandText.add(title);
        brandText.add(subtitle);

        logoRow.add(badge, BorderLayout.WEST);
        logoRow.add(brandText, BorderLayout.CENTER);
        panel.add(logoRow);
        panel.add(Box.createVerticalStrut(32));

        // Outlined pill badge
        ThemePalette.RoundedPanel pill = new ThemePalette.RoundedPanel(8, new Color(255, 255, 255, 18), ThemePalette.PEACH);
        pill.setLayout(new BorderLayout());
        pill.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        pill.setMaximumSize(new Dimension(150, 28));
        pill.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel tag = new JLabel("PORTAL AKADEMIK");
        tag.setFont(new Font("SansSerif", Font.BOLD, 10));
        tag.setForeground(ThemePalette.PEACH);
        pill.add(tag, BorderLayout.CENTER);
        panel.add(pill);
        panel.add(Box.createVerticalStrut(16));

        JLabel hero = new JLabel("<html>Penilaian akademik dalam satu<br/>ruang kerja.</html>");
        hero.setFont(ThemePalette.FONT_HERO);
        hero.setForeground(Color.WHITE);
        hero.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(hero);
        panel.add(Box.createVerticalStrut(12));

        JLabel desc = new JLabel("<html>Kelola mahasiswa, komponen penilaian, dan rekap nilai secara terstruktur dan konsisten.</html>");
        desc.setFont(ThemePalette.FONT_BODY);
        desc.setForeground(new Color(0xDC, 0xE7, 0xF2));
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(desc);
        panel.add(Box.createVerticalStrut(26));

        // Wireframe dashboard illustration (matching Figma Image 1)
        JPanel wireframe = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth() - 8;
                int h = getHeight() - 8;

                g2.setColor(new Color(255, 255, 255, 16));
                g2.fill(new RoundRectangle2D.Double(4, 4, w, h, 14, 14));
                g2.setColor(new Color(255, 255, 255, 65));
                g2.setStroke(new BasicStroke(1.2f));
                g2.draw(new RoundRectangle2D.Double(4, 4, w, h, 14, 14));

                // Window dots
                g2.setColor(ThemePalette.PEACH);
                g2.fillOval(18, 16, 6, 6);
                g2.setColor(ThemePalette.CORAL);
                g2.fillOval(29, 16, 6, 6);
                g2.setColor(new Color(255, 255, 255, 100));
                g2.fillOval(40, 16, 6, 6);

                // Top divider
                g2.setColor(new Color(255, 255, 255, 40));
                g2.drawLine(4, 32, w + 4, 32);
                // Sidebar divider
                g2.drawLine(105, 32, 105, h + 4);

                // Sidebar bars
                g2.setColor(ThemePalette.PEACH);
                g2.fillRoundRect(20, 50, 62, 10, 4, 4);
                g2.setColor(new Color(255, 255, 255, 60));
                g2.fillRoundRect(20, 74, 54, 6, 3, 3);
                g2.fillRoundRect(20, 90, 66, 6, 3, 3);
                g2.fillRoundRect(20, 106, 48, 6, 3, 3);
                g2.fillRoundRect(20, 122, 60, 6, 3, 3);

                // Right header bar
                g2.setColor(new Color(255, 255, 255, 140));
                g2.fillRoundRect(122, 48, 110, 10, 4, 4);

                // 3 KPI wireframe boxes
                int boxW = Math.max(40, (w - 146) / 3);
                for (int i = 0; i < 3; i++) {
                    int bx = 122 + i * (boxW + 10);
                    g2.setColor(new Color(255, 255, 255, 22));
                    g2.fillRoundRect(bx, 70, boxW, 44, 8, 8);
                    g2.setColor(new Color(255, 255, 255, 60));
                    g2.drawRoundRect(bx, 70, boxW, 44, 8, 8);
                }

                // Bottom table wireframe box
                g2.setColor(new Color(255, 255, 255, 16));
                g2.fillRoundRect(122, 126, w - 134, Math.max(50, h - 138), 8, 8);
                g2.setColor(new Color(255, 255, 255, 55));
                g2.drawRoundRect(122, 126, w - 134, Math.max(50, h - 138), 8, 8);
                for (int lineY = 144; lineY < h - 10; lineY += 16) {
                    g2.drawLine(134, lineY, w - 24, lineY);
                }

                g2.dispose();
            }
        };
        wireframe.setOpaque(false);
        wireframe.setAlignmentX(Component.LEFT_ALIGNMENT);
        wireframe.setPreferredSize(new Dimension(420, 220));
        wireframe.setMaximumSize(new Dimension(520, 250));
        panel.add(wireframe);

        panel.add(Box.createVerticalGlue());
        JLabel footer = new JLabel("© 2026 LECTURA · Semester Genap 2024/2025");
        footer.setFont(ThemePalette.FONT_SMALL);
        footer.setForeground(new Color(0xC8, 0xD8, 0xE6));
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(footer);

        return panel;
    }

    private JPanel buildRightFormPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(ThemePalette.BG_LIGHT);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setPreferredSize(new Dimension(380, 440));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel kic = new JLabel("SELAMAT DATANG");
        kic.setFont(new Font("SansSerif", Font.BOLD, 10));
        kic.setForeground(ThemePalette.PURPLE);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 6, 0);
        form.add(kic, gbc);

        JLabel title = new JLabel("Selamat Datang di LECTURA!");
        title.setFont(ThemePalette.FONT_SERIF_TITLE);
        title.setForeground(ThemePalette.NAVY_DARK);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 6, 0);
        form.add(title, gbc);

        JLabel sub = new JLabel("Masukkan email dan kata sandi untuk melanjutkan.");
        sub.setFont(ThemePalette.FONT_BODY);
        sub.setForeground(ThemePalette.TEXT_MUTED);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 24, 0);
        form.add(sub, gbc);

        JLabel lblEmail = new JLabel("Email institusi");
        lblEmail.setFont(ThemePalette.FONT_SMALL_BOLD);
        lblEmail.setForeground(ThemePalette.TEXT_DARK);
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 6, 0);
        form.add(lblEmail, gbc);

        txtEmail = ThemePalette.createStyledTextField(authController.getDosenAktif().getEmail());
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 16, 0);
        form.add(txtEmail, gbc);

        JLabel lblPass = new JLabel("Kata sandi");
        lblPass.setFont(ThemePalette.FONT_SMALL_BOLD);
        lblPass.setForeground(ThemePalette.TEXT_DARK);
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 6, 0);
        form.add(lblPass, gbc);

        txtPassword = ThemePalette.createStyledPasswordField("password123");
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 10, 0);
        form.add(txtPassword, gbc);

        JPanel optionsRow = new JPanel(new BorderLayout());
        optionsRow.setOpaque(false);
        chkRememberMe = new JCheckBox("Ingat saya", true);
        chkRememberMe.setOpaque(false);
        chkRememberMe.setFont(ThemePalette.FONT_SMALL);
        chkRememberMe.setForeground(ThemePalette.TEXT_MUTED);

        chkShowPassword = new JCheckBox("Tampilkan sandi");
        chkShowPassword.setOpaque(false);
        chkShowPassword.setFont(ThemePalette.FONT_SMALL);
        chkShowPassword.setForeground(ThemePalette.TEXT_MUTED);
        chkShowPassword.addActionListener(e ->
                txtPassword.setEchoChar(chkShowPassword.isSelected() ? (char) 0 : '•'));

        optionsRow.add(chkRememberMe, BorderLayout.WEST);
        optionsRow.add(chkShowPassword, BorderLayout.EAST);
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 20, 0);
        form.add(optionsRow, gbc);

        JButton btnLogin = ThemePalette.createPrimaryButton("Masuk ke Dashboard   →");
        btnLogin.setPreferredSize(new Dimension(380, 42));
        btnLogin.addActionListener(e -> handleLogin());
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, 10, 0);
        form.add(btnLogin, gbc);

        JButton btnQuick = ThemePalette.createSecondaryButton("Demo Cepat (Dra. Rina Puspita)");
        btnQuick.setPreferredSize(new Dimension(380, 38));
        btnQuick.addActionListener(e -> {
            authController.quickLogin();
            onLoginSuccess.run();
        });
        gbc.gridy = 9;
        gbc.insets = new Insets(0, 0, 18, 0);
        form.add(btnQuick, gbc);

        JLabel hint = new JLabel("Akun demo: rina.puspita@sipena.ac.id / password123");
        hint.setFont(ThemePalette.FONT_SMALL);
        hint.setForeground(ThemePalette.TEXT_MUTED);
        hint.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridy = 10;
        form.add(hint, gbc);

        wrapper.add(form);
        return wrapper;
    }

    private void handleLogin() {
        try {
            authController.login(txtEmail.getText(), new String(txtPassword.getPassword()));
            onLoginSuccess.run();
        } catch (ValidasiInputException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Gagal Masuk", JOptionPane.ERROR_MESSAGE);
        }
    }
}
