package id.ac.lectura.view;

import id.ac.lectura.controller.AcademicController;
import id.ac.lectura.exception.DataPersistenceException;
import id.ac.lectura.exception.ValidasiInputException;
import id.ac.lectura.model.KomponenKuis;
import id.ac.lectura.model.KomponenPenilaian;
import id.ac.lectura.model.KomponenTugas;
import id.ac.lectura.model.KomponenUjian;
import id.ac.lectura.model.MataKuliah;
import id.ac.lectura.util.ThemePalette;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.UUID;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * Tampilan CRUD Komponen Penilaian bergaya Figma (Full-width Table Card + Modal Dialog).
 */
public class KomponenPenilaianPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final AcademicController controller;
    private JLabel lblCourseInfo;
    private JProgressBar barTotalBobot;
    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel lblFooterCount;

    public KomponenPenilaianPanel(AcademicController controller) {
        this.controller = controller;
        initUI();
        controller.addChangeListener(this::refreshData);
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 18));
        setBackground(ThemePalette.BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 28, 28));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBox.setOpaque(false);
        JLabel title = new JLabel("Komponen Penilaian");
        title.setFont(ThemePalette.FONT_SERIF_TITLE);
        title.setForeground(ThemePalette.NAVY_DARK);
        lblCourseInfo = new JLabel("Mata Kuliah: -");
        lblCourseInfo.setFont(ThemePalette.FONT_SUBTITLE);
        lblCourseInfo.setForeground(ThemePalette.TEXT_MUTED);
        titleBox.add(title);
        titleBox.add(lblCourseInfo);

        JButton btnAdd = ThemePalette.createPrimaryButton("+   Tambah Komponen");
        btnAdd.addActionListener(e -> openKomponenDialog(null));

        header.add(titleBox, BorderLayout.WEST);
        header.add(btnAdd, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // White Card
        ThemePalette.RoundedPanel card = new ThemePalette.RoundedPanel(12, Color.WHITE, ThemePalette.BORDER_SOFT);
        card.setLayout(new BorderLayout(0, 14));
        card.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        // Top Bobot Indicator Bar inside Card
        JPanel bobotRow = new JPanel(new BorderLayout(14, 0));
        bobotRow.setOpaque(false);
        JLabel lblBobot = new JLabel("Total Bobot Mata Kuliah (Target 100%):");
        lblBobot.setFont(ThemePalette.FONT_SMALL_BOLD);
        lblBobot.setForeground(ThemePalette.TEXT_DARK);
        barTotalBobot = new JProgressBar(0, 100);
        barTotalBobot.setStringPainted(true);
        barTotalBobot.setPreferredSize(new Dimension(240, 20));
        bobotRow.add(lblBobot, BorderLayout.WEST);
        bobotRow.add(barTotalBobot, BorderLayout.CENTER);
        card.add(bobotRow, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "JENIS", "KATEGORI", "NAMA KOMPONEN", "BOBOT (%)", "KKM", "KETERANGAN"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ThemePalette.styleTable(table);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(ThemePalette.BORDER_SOFT));
        card.add(scroll, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        lblFooterCount = new JLabel("Menampilkan 0 komponen");
        lblFooterCount.setFont(ThemePalette.FONT_SMALL);
        lblFooterCount.setForeground(ThemePalette.TEXT_MUTED);

        JPanel rowActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rowActions.setOpaque(false);
        JButton btnEdit = ThemePalette.createSecondaryButton("Ubah Terpilih");
        JButton btnDelete = ThemePalette.createDangerButton("Hapus Terpilih");
        btnEdit.addActionListener(e -> handleEditSelected());
        btnDelete.addActionListener(e -> handleDeleteSelected());
        rowActions.add(btnEdit);
        rowActions.add(btnDelete);

        footer.add(lblFooterCount, BorderLayout.WEST);
        footer.add(rowActions, BorderLayout.EAST);
        card.add(footer, BorderLayout.SOUTH);

        add(card, BorderLayout.CENTER);
    }

    public void refreshData() {
        MataKuliah mk = controller.getSelectedMataKuliah();
        tableModel.setRowCount(0);
        if (mk == null) {
            lblCourseInfo.setText("Pilih mata kuliah di sidebar.");
            barTotalBobot.setValue(0);
            barTotalBobot.setString("0%");
            return;
        }
        lblCourseInfo.setText(mk.getKode() + " - " + mk.getNama() + " (" + mk.getKelas() + ")");
        int totalBobot = controller.getTotalBobotMataKuliah(mk.getId());
        barTotalBobot.setValue(Math.min(100, totalBobot));
        barTotalBobot.setString(totalBobot + "% / 100%");
        barTotalBobot.setForeground(totalBobot == 100 ? ThemePalette.NAVY : ThemePalette.CORAL);

        var list = controller.getKomponenByMataKuliah(mk.getId());
        for (KomponenPenilaian k : list) {
            tableModel.addRow(new Object[]{
                    k.getId(), k.getJenisKomponen(), k.getKategoriPenilaian(),
                    k.getNama(), k.getBobot() + "%", k.getKkm(), k.getKeterangan()
            });
        }
        lblFooterCount.setText("Menampilkan " + list.size() + " komponen penilaian");
    }

    private KomponenPenilaian getSelectedKomponen() {
        int row = table.getSelectedRow();
        MataKuliah mk = controller.getSelectedMataKuliah();
        if (row < 0 || mk == null) return null;
        String id = (String) tableModel.getValueAt(row, 0);
        for (KomponenPenilaian k : controller.getKomponenByMataKuliah(mk.getId())) {
            if (k.getId().equals(id)) return k;
        }
        return null;
    }

    private void handleEditSelected() {
        KomponenPenilaian k = getSelectedKomponen();
        if (k == null) {
            JOptionPane.showMessageDialog(this, "Pilih komponen pada tabel terlebih dahulu.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        openKomponenDialog(k);
    }

    private void handleDeleteSelected() {
        KomponenPenilaian k = getSelectedKomponen();
        if (k == null) {
            JOptionPane.showMessageDialog(this, "Pilih komponen yang akan dihapus.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus komponen \"" + k.getNama() + "\"?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controller.hapusKomponen(k.getId());
            } catch (DataPersistenceException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openKomponenDialog(KomponenPenilaian existing) {
        MataKuliah mk = controller.getSelectedMataKuliah();
        if (mk == null) return;

        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(owner, existing == null ? "Tambah Komponen" : "Ubah Komponen", true);
        dialog.setSize(440, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 22, 12, 22));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(6, 4, 6, 4);

        JRadioButton rbTugas = new JRadioButton("Tugas", existing == null || existing instanceof KomponenTugas);
        JRadioButton rbKuis = new JRadioButton("Kuis", existing instanceof KomponenKuis);
        JRadioButton rbUjian = new JRadioButton("Ujian", existing instanceof KomponenUjian);
        rbTugas.setBackground(Color.WHITE);
        rbKuis.setBackground(Color.WHITE);
        rbUjian.setBackground(Color.WHITE);
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbTugas);
        bg.add(rbKuis);
        bg.add(rbUjian);
        JPanel rPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        rPanel.setBackground(Color.WHITE);
        rPanel.add(rbTugas);
        rPanel.add(rbKuis);
        rPanel.add(rbUjian);

        JTextField fNama = ThemePalette.createStyledTextField(existing != null ? existing.getNama() : "");
        JSpinner fBobot = new JSpinner(new SpinnerNumberModel(existing != null ? existing.getBobot() : 20, 1, 100, 5));
        JSpinner fKkm = new JSpinner(new SpinnerNumberModel(existing != null ? existing.getKkm() : 60, 0, 100, 5));
        JCheckBox fPrak = new JCheckBox("Komponen Praktikum / Proyek",
                existing instanceof KomponenTugas && ((KomponenTugas) existing).isTugasPraktikum());
        fPrak.setBackground(Color.WHITE);
        JTextArea fKet = new JTextArea(existing != null ? existing.getKeterangan() : "", 3, 20);
        fKet.setLineWrap(true);
        fKet.setWrapStyleWord(true);

        int r = 0;
        r = addRow(form, g, r, "Jenis Komponen", rPanel);
        r = addRow(form, g, r, "Nama Komponen", fNama);
        r = addRow(form, g, r, "Bobot (%)", fBobot);
        r = addRow(form, g, r, "KKM", fKkm);
        r = addRow(form, g, r, "Opsi Tugas", fPrak);
        addRow(form, g, r, "Keterangan", new JScrollPane(fKet));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        btns.setBackground(ThemePalette.BG_LIGHT);
        JButton btnCancel = ThemePalette.createSecondaryButton("Batal");
        JButton btnSave = ThemePalette.createPrimaryButton("Simpan");
        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            try {
                String id = existing != null ? existing.getId() : "k-" + UUID.randomUUID().toString().substring(0, 6);
                String nama = fNama.getText();
                int bobot = (Integer) fBobot.getValue();
                int kkm = (Integer) fKkm.getValue();
                String ket = fKet.getText();

                KomponenPenilaian komp;
                if (rbTugas.isSelected()) {
                    komp = new KomponenTugas(id, mk.getId(), nama, bobot, kkm, ket, fPrak.isSelected());
                } else if (rbKuis.isSelected()) {
                    komp = new KomponenKuis(id, mk.getId(), nama, bobot, kkm, ket, 2);
                } else {
                    komp = new KomponenUjian(id, mk.getId(), nama, bobot, kkm, ket,
                            nama.toUpperCase().contains("UAS") ? "UAS" : "UTS");
                }

                if (existing == null) {
                    controller.tambahKomponen(komp);
                } else {
                    controller.ubahKomponen(komp);
                }
                dialog.dispose();
            } catch (ValidasiInputException | DataPersistenceException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(),
                        "Validasi Gagal", JOptionPane.ERROR_MESSAGE);
            }
        });
        btns.add(btnCancel);
        btns.add(btnSave);

        dialog.add(new JScrollPane(form), BorderLayout.CENTER);
        dialog.add(btns, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private int addRow(JPanel panel, GridBagConstraints g, int row, String label, java.awt.Component comp) {
        g.gridx = 0; g.gridy = row; g.weightx = 0.35;
        JLabel lbl = new JLabel(label);
        lbl.setFont(ThemePalette.FONT_SMALL_BOLD);
        panel.add(lbl, g);
        g.gridx = 1; g.weightx = 0.65;
        panel.add(comp, g);
        return row + 1;
    }
}
