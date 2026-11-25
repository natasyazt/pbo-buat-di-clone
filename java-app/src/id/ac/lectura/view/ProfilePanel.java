package id.ac.lectura.view;

import id.ac.lectura.controller.AcademicController;
import id.ac.lectura.controller.AuthController;
import id.ac.lectura.exception.DataPersistenceException;
import id.ac.lectura.exception.ValidasiInputException;
import id.ac.lectura.model.Dosen;
import id.ac.lectura.util.ThemePalette;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * Tampilan Profil Dosen LECTURA untuk mengelola identitas akademik dan kata sandi secara real-time.
 */
public class ProfilePanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final AuthController authController;
    private final AcademicController academicController;

    private JLabel lblAvatar;
    private JLabel lblSummaryName;
    private JLabel lblSummaryMeta;
    private JLabel lblSummaryProdi;

    private JTextField txtNama;
    private JTextField txtNip;
    private JTextField txtGelar;
    private JTextField txtEmail;
    private JTextField txtProdi;
    private JTextField txtFakultas;

    private JPasswordField txtOldPass;
    private JPasswordField txtNewPass;
    private JPasswordField txtConfirmPass;

    public ProfilePanel(AuthController authController, AcademicController academicController) {
        this.authController = authController;
        this.academicController = academicController;
        initUI();
        refreshData();
        authController.addProfileListener(this::refreshData);
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(ThemePalette.BG_LIGHT);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(ThemePalette.BG_LIGHT);
        content.setBorder(BorderFactory.createEmptyBorder(24, 28, 28, 28));

        // Page Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBox.setOpaque(false);
        JLabel title = new JLabel("Profil Dosen");
        title.setFont(ThemePalette.FONT_SERIF_TITLE);
        title.setForeground(ThemePalette.NAVY_DARK);
        JLabel sub = new JLabel("Kelola informasi identitas akademik dan keamanan akun Anda.");
        sub.setFont(ThemePalette.FONT_SUBTITLE);
        sub.setForeground(ThemePalette.TEXT_MUTED);
        titleBox.add(title);
        titleBox.add(sub);
        header.add(titleBox, BorderLayout.WEST);

        content.add(header);
        content.add(Box.createVerticalStrut(18));

        // Top Profile Summary Card
        ThemePalette.RoundedPanel summaryCard = new ThemePalette.RoundedPanel(12, Color.WHITE, ThemePalette.BORDER_SOFT);
        summaryCard.setLayout(new BorderLayout(18, 0));
        summaryCard.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));
        summaryCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 112));

        ThemePalette.RoundedPanel avatarBox = new ThemePalette.RoundedPanel(64, ThemePalette.PEACH, null);
        avatarBox.setPreferredSize(new Dimension(64, 64));
        avatarBox.setLayout(new BorderLayout());
        lblAvatar = new JLabel("RP", JLabel.CENTER);
        lblAvatar.setFont(new Font("Georgia", Font.BOLD, 22));
        lblAvatar.setForeground(ThemePalette.NAVY_DARK);
        avatarBox.add(lblAvatar, BorderLayout.CENTER);

        JPanel infoCol = new JPanel(new GridLayout(3, 1, 0, 3));
        infoCol.setOpaque(false);
        lblSummaryName = new JLabel("-");
        lblSummaryName.setFont(new Font("Georgia", Font.BOLD, 18));
        lblSummaryName.setForeground(ThemePalette.NAVY_DARK);
        lblSummaryMeta = new JLabel("-");
        lblSummaryMeta.setFont(ThemePalette.FONT_BODY);
        lblSummaryMeta.setForeground(ThemePalette.TEXT_MUTED);
        lblSummaryProdi = new JLabel("-");
        lblSummaryProdi.setFont(ThemePalette.FONT_SMALL);
        lblSummaryProdi.setForeground(ThemePalette.PURPLE);
        infoCol.add(lblSummaryName);
        infoCol.add(lblSummaryMeta);
        infoCol.add(lblSummaryProdi);

        ThemePalette.RoundedPanel roleBadge = new ThemePalette.RoundedPanel(10, ThemePalette.NAVY, null);
        roleBadge.setLayout(new BorderLayout());
        roleBadge.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        JLabel lblRole = new JLabel("Dosen Pengampu");
        lblRole.setFont(ThemePalette.FONT_SMALL_BOLD);
        lblRole.setForeground(Color.WHITE);
        roleBadge.add(lblRole, BorderLayout.CENTER);

        JPanel badgeWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 14));
        badgeWrap.setOpaque(false);
        badgeWrap.add(roleBadge);

        summaryCard.add(avatarBox, BorderLayout.WEST);
        summaryCard.add(infoCol, BorderLayout.CENTER);
        summaryCard.add(badgeWrap, BorderLayout.EAST);

        content.add(summaryCard);
        content.add(Box.createVerticalStrut(18));

        // Bottom 2-Column Forms
        JPanel formsGrid = new JPanel(new GridLayout(1, 2, 16, 0));
        formsGrid.setOpaque(false);

        formsGrid.add(buildEditProfileCard());
        formsGrid.add(buildChangePasswordCard());
        content.add(formsGrid);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildEditProfileCard() {
        ThemePalette.RoundedPanel card = new ThemePalette.RoundedPanel(12, Color.WHITE, ThemePalette.BORDER_SOFT);
        card.setLayout(new BorderLayout(0, 14));
        card.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));

        JPanel head = new JPanel(new GridLayout(2, 1, 0, 2));
        head.setOpaque(false);
        JLabel t = new JLabel("Informasi Profil");
        t.setFont(ThemePalette.FONT_CARD_TITLE);
        t.setForeground(ThemePalette.NAVY_DARK);
        JLabel s = new JLabel("Perubahan disimpan langsung ke penyimpanan lokal.");
        s.setFont(ThemePalette.FONT_SMALL);
        s.setForeground(ThemePalette.TEXT_MUTED);
        head.add(t);
        head.add(s);
        card.add(head, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(5, 2, 5, 2);

        txtNama = ThemePalette.createStyledTextField("");
        txtNip = ThemePalette.createStyledTextField("");
        txtGelar = ThemePalette.createStyledTextField("");
        txtEmail = ThemePalette.createStyledTextField("");
        txtProdi = ThemePalette.createStyledTextField("");
        txtFakultas = ThemePalette.createStyledTextField("");

        int r = 0;
        r = addRow(form, g, r, "Nama Lengkap", txtNama);
        r = addRow(form, g, r, "NIP", txtNip);
        r = addRow(form, g, r, "Jabatan / Gelar", txtGelar);
        r = addRow(form, g, r, "Email Institusi", txtEmail);
        r = addRow(form, g, r, "Program Studi", txtProdi);
        addRow(form, g, r, "Fakultas", txtFakultas);

        card.add(form, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.setOpaque(false);
        JButton btnSave = ThemePalette.createPrimaryButton("Simpan Perubahan Profil");
        btnSave.addActionListener(e -> handleSaveProfile());
        footer.add(btnSave);
        card.add(footer, BorderLayout.SOUTH);

        return card;
    }

    private JPanel buildChangePasswordCard() {
        ThemePalette.RoundedPanel card = new ThemePalette.RoundedPanel(12, Color.WHITE, ThemePalette.BORDER_SOFT);
        card.setLayout(new BorderLayout(0, 14));
        card.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));

        JPanel head = new JPanel(new GridLayout(2, 1, 0, 2));
        head.setOpaque(false);
        JLabel t = new JLabel("Keamanan Akun");
        t.setFont(ThemePalette.FONT_CARD_TITLE);
        t.setForeground(ThemePalette.NAVY_DARK);
        JLabel s = new JLabel("Ubah kata sandi untuk menjaga keamanan portal dosen.");
        s.setFont(ThemePalette.FONT_SMALL);
        s.setForeground(ThemePalette.TEXT_MUTED);
        head.add(t);
        head.add(s);
        card.add(head, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(6, 2, 6, 2);

        txtOldPass = ThemePalette.createStyledPasswordField("");
        txtNewPass = ThemePalette.createStyledPasswordField("");
        txtConfirmPass = ThemePalette.createStyledPasswordField("");

        int r = 0;
        r = addRow(form, g, r, "Kata Sandi Lama", txtOldPass);
        r = addRow(form, g, r, "Kata Sandi Baru", txtNewPass);
        addRow(form, g, r, "Konfirmasi Sandi", txtConfirmPass);

        JPanel formTopAlign = new JPanel(new BorderLayout());
        formTopAlign.setOpaque(false);
        formTopAlign.add(form, BorderLayout.NORTH);
        card.add(formTopAlign, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.setOpaque(false);
        JButton btnChangePass = ThemePalette.createSecondaryButton("Ubah Kata Sandi");
        btnChangePass.addActionListener(e -> handleChangePassword());
        footer.add(btnChangePass);
        card.add(footer, BorderLayout.SOUTH);

        return card;
    }

    private int addRow(JPanel panel, GridBagConstraints g, int row, String label, java.awt.Component comp) {
        g.gridx = 0; g.gridy = row; g.weightx = 0.36;
        JLabel lbl = new JLabel(label);
        lbl.setFont(ThemePalette.FONT_SMALL_BOLD);
        lbl.setForeground(ThemePalette.TEXT_DARK);
        panel.add(lbl, g);

        g.gridx = 1; g.weightx = 0.64;
        panel.add(comp, g);
        return row + 1;
    }

    public void refreshData() {
        Dosen d = authController.getDosenAktif();
        lblAvatar.setText(extractInitials(d.getNama()));
        lblSummaryName.setText(d.getNama());
        lblSummaryMeta.setText("NIP: " + d.getNip() + "  ·  " + d.getEmail());
        lblSummaryProdi.setText(d.getProdi() + " — " + d.getFakultas());

        txtNama.setText(d.getNama());
        txtNip.setText(d.getNip());
        txtGelar.setText(d.getGelar());
        txtEmail.setText(d.getEmail());
        txtProdi.setText(d.getProdi());
        txtFakultas.setText(d.getFakultas());
    }

    public static String extractInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "DS";
        String cleaned = name.replaceAll("^(Dra?\\.|Prof\\.|Ir\\.)\\s*", "").trim();
        String[] parts = cleaned.split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        }
        return ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
    }

    private void handleSaveProfile() {
        try {
            authController.updateProfil(
                    txtNama.getText(),
                    txtNip.getText(),
                    txtGelar.getText(),
                    txtEmail.getText(),
                    txtProdi.getText(),
                    txtFakultas.getText()
            );
            academicController.logAktivitas("Perbarui Profil", "SISTEM",
                    "Profil dosen diperbarui: " + authController.getDosenAktif().getNama());
            JOptionPane.showMessageDialog(this, "Profil dosen berhasil disimpan.",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
        } catch (ValidasiInputException | DataPersistenceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Gagal Menyimpan Profil", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleChangePassword() {
        try {
            authController.ubahPassword(
                    new String(txtOldPass.getPassword()),
                    new String(txtNewPass.getPassword()),
                    new String(txtConfirmPass.getPassword())
            );
            txtOldPass.setText("");
            txtNewPass.setText("");
            txtConfirmPass.setText("");
            academicController.logAktivitas("Ubah Kata Sandi", "SISTEM",
                    "Kata sandi akun dosen diperbarui");
            JOptionPane.showMessageDialog(this, "Kata sandi berhasil diperbarui.",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
        } catch (ValidasiInputException | DataPersistenceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Gagal Mengubah Kata Sandi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
