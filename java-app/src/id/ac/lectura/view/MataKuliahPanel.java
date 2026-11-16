package id.ac.lectura.view;

import id.ac.lectura.controller.AcademicController;
import id.ac.lectura.exception.DataPersistenceException;
import id.ac.lectura.exception.ValidasiInputException;
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
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/**
 * Tampilan CRUD Mata Kuliah bergaya Figma (Full-width Table Card + Modal Dialog Tambah/Ubah).
 */
public class MataKuliahPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final AcademicController controller;
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtSearch;
    private JLabel lblFooterCount;

    public MataKuliahPanel(AcademicController controller) {
        this.controller = controller;
        initUI();
        controller.addChangeListener(this::refreshData);
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 18));
        setBackground(ThemePalette.BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 28, 28));

        // Header Row
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBox.setOpaque(false);
        JLabel title = new JLabel("Mata Kuliah");
        title.setFont(ThemePalette.FONT_SERIF_TITLE);
        title.setForeground(ThemePalette.NAVY_DARK);
        JLabel sub = new JLabel("Kelola mata kuliah dan kelas perkuliahan.");
        sub.setFont(ThemePalette.FONT_SUBTITLE);
        sub.setForeground(ThemePalette.TEXT_MUTED);
        titleBox.add(title);
        titleBox.add(sub);

        JButton btnAdd = ThemePalette.createPrimaryButton("+   Tambah Mata Kuliah");
        btnAdd.addActionListener(e -> openMkDialog(null));

        header.add(titleBox, BorderLayout.WEST);
        header.add(btnAdd, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Main White Card
        ThemePalette.RoundedPanel card = new ThemePalette.RoundedPanel(12, Color.WHITE, ThemePalette.BORDER_SOFT);
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        JPanel toolbar = new JPanel(new BorderLayout(12, 0));
        toolbar.setOpaque(false);

        JPanel leftSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftSearch.setOpaque(false);
        JLabel lblSearch = new JLabel("Cari:");
        lblSearch.setFont(ThemePalette.FONT_SMALL_BOLD);
        lblSearch.setForeground(ThemePalette.TEXT_MUTED);
        txtSearch = ThemePalette.createStyledTextField("");
        txtSearch.setPreferredSize(new Dimension(280, 36));
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { refreshData(); }
            @Override public void removeUpdate(DocumentEvent e) { refreshData(); }
            @Override public void changedUpdate(DocumentEvent e) { refreshData(); }
        });
        leftSearch.add(lblSearch);
        leftSearch.add(txtSearch);

        JButton btnExport = ThemePalette.createSecondaryButton("Ekspor CSV");
        btnExport.addActionListener(e -> handleExportCsv());
        toolbar.add(leftSearch, BorderLayout.WEST);
        toolbar.add(btnExport, BorderLayout.EAST);
        card.add(toolbar, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[]{"ID", "KODE", "MATA KULIAH", "SKS", "KELAS", "SEMESTER", "RUANG", "JADWAL"}, 0) {
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

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        lblFooterCount = new JLabel("Menampilkan 0 data");
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
        String q = txtSearch.getText() == null ? "" : txtSearch.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        int shown = 0;
        for (MataKuliah mk : controller.getMataKuliahList()) {
            if (q.isEmpty() || mk.getKode().toLowerCase().contains(q)
                    || mk.getNama().toLowerCase().contains(q)
                    || mk.getKelas().toLowerCase().contains(q)) {
                tableModel.addRow(new Object[]{
                        mk.getId(), mk.getKode(), mk.getNama(), mk.getSks(),
                        mk.getKelas(), mk.getSemester(), mk.getRuang(), mk.getJadwal()
                });
                shown++;
            }
        }
        lblFooterCount.setText("Menampilkan " + shown + " dari " + controller.getMataKuliahList().size() + " data");
    }

    private MataKuliah getSelectedMk() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        String id = (String) tableModel.getValueAt(row, 0);
        for (MataKuliah mk : controller.getMataKuliahList()) {
            if (mk.getId().equals(id)) return mk;
        }
        return null;
    }

    private void handleEditSelected() {
        MataKuliah mk = getSelectedMk();
        if (mk == null) {
            JOptionPane.showMessageDialog(this, "Pilih mata kuliah pada tabel terlebih dahulu.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        openMkDialog(mk);
    }

    private void handleDeleteSelected() {
        MataKuliah mk = getSelectedMk();
        if (mk == null) {
            JOptionPane.showMessageDialog(this, "Pilih mata kuliah yang akan dihapus.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus " + mk.getKode() + " - " + mk.getNama() + "?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controller.hapusMataKuliah(mk.getId());
            } catch (DataPersistenceException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleExportCsv() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("daftar_matakuliah.csv"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter fw = new FileWriter(fc.getSelectedFile())) {
                fw.write("Kode,Nama,SKS,Kelas,Semester,Ruang,Jadwal\n");
                for (MataKuliah mk : controller.getMataKuliahList()) {
                    fw.write(String.format("%s,\"%s\",%d,%s,%s,\"%s\",\"%s\"\n",
                            mk.getKode(), mk.getNama(), mk.getSks(), mk.getKelas(),
                            mk.getSemester(), mk.getRuang(), mk.getJadwal()));
                }
                JOptionPane.showMessageDialog(this, "Data mata kuliah berhasil diekspor ke CSV.",
                        "Ekspor Berhasil", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Gagal mengekspor: " + ex.getMessage(),
                        "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openMkDialog(MataKuliah existing) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(owner, existing == null ? "Tambah Mata Kuliah" : "Ubah Mata Kuliah", true);
        dialog.setSize(450, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 22, 12, 22));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(5, 4, 5, 4);

        JTextField fKode = ThemePalette.createStyledTextField(existing != null ? existing.getKode() : "");
        JTextField fNama = ThemePalette.createStyledTextField(existing != null ? existing.getNama() : "");
        JSpinner fSks = new JSpinner(new SpinnerNumberModel(existing != null ? existing.getSks() : 3, 1, 6, 1));
        JComboBox<String> fSem = new JComboBox<>(new String[]{"Genap 2024/2025", "Ganjil 2025/2026", "Genap 2025/2026"});
        if (existing != null) fSem.setSelectedItem(existing.getSemester());
        JTextField fKelas = ThemePalette.createStyledTextField(existing != null ? existing.getKelas() : "");
        JTextField fRuang = ThemePalette.createStyledTextField(existing != null ? existing.getRuang() : "");
        JTextField fJadwal = ThemePalette.createStyledTextField(existing != null ? existing.getJadwal() : "");
        JTextArea fDesk = new JTextArea(existing != null ? existing.getDeskripsi() : "", 3, 20);
        fDesk.setLineWrap(true);
        fDesk.setWrapStyleWord(true);

        int r = 0;
        r = addRow(form, g, r, "Kode MK", fKode);
        r = addRow(form, g, r, "Nama MK", fNama);
        r = addRow(form, g, r, "SKS", fSks);
        r = addRow(form, g, r, "Semester", fSem);
        r = addRow(form, g, r, "Kelas", fKelas);
        r = addRow(form, g, r, "Ruang", fRuang);
        r = addRow(form, g, r, "Jadwal", fJadwal);
        addRow(form, g, r, "Deskripsi", new JScrollPane(fDesk));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        btns.setBackground(ThemePalette.BG_LIGHT);
        JButton btnCancel = ThemePalette.createSecondaryButton("Batal");
        JButton btnSave = ThemePalette.createPrimaryButton("Simpan");
        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            try {
                String id = existing != null ? existing.getId() : "mk-" + UUID.randomUUID().toString().substring(0, 6);
                MataKuliah mk = new MataKuliah(id, fKode.getText(), fNama.getText(),
                        (Integer) fSks.getValue(), (String) fSem.getSelectedItem(),
                        fKelas.getText(), fRuang.getText(), fJadwal.getText(), fDesk.getText());
                if (existing == null) {
                    controller.tambahMataKuliah(mk);
                } else {
                    controller.ubahMataKuliah(mk);
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
        g.gridx = 0; g.gridy = row; g.weightx = 0.32;
        JLabel lbl = new JLabel(label);
        lbl.setFont(ThemePalette.FONT_SMALL_BOLD);
        panel.add(lbl, g);
        g.gridx = 1; g.weightx = 0.68;
        panel.add(comp, g);
        return row + 1;
    }
}
