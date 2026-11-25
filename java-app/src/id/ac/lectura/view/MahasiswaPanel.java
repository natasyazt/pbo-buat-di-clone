package id.ac.lectura.view;

import id.ac.lectura.controller.AcademicController;
import id.ac.lectura.exception.DataPersistenceException;
import id.ac.lectura.exception.ValidasiInputException;
import id.ac.lectura.model.Mahasiswa;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/**
 * Tampilan CRUD Mahasiswa bergaya Figma (Full-width Table Card + Modal Dialog Tambah/Ubah).
 */
public class MahasiswaPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final AcademicController controller;
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtSearch;
    private JComboBox<String> cboFilterStatus;
    private JLabel lblFooterCount;

    public MahasiswaPanel(AcademicController controller) {
        this.controller = controller;
        initUI();
        controller.addChangeListener(this::refreshData);
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 18));
        setBackground(ThemePalette.BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 28, 28));

        // Header Row (Title + "+ Tambah Mahasiswa" button)
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBox.setOpaque(false);
        JLabel title = new JLabel("Mahasiswa");
        title.setFont(ThemePalette.FONT_SERIF_TITLE);
        title.setForeground(ThemePalette.NAVY_DARK);
        JLabel sub = new JLabel("Kelola data mahasiswa yang terdaftar.");
        sub.setFont(ThemePalette.FONT_SUBTITLE);
        sub.setForeground(ThemePalette.TEXT_MUTED);
        titleBox.add(title);
        titleBox.add(sub);

        JButton btnAdd = ThemePalette.createPrimaryButton("+   Tambah Mahasiswa");
        btnAdd.addActionListener(e -> openMahasiswaDialog(null));

        header.add(titleBox, BorderLayout.WEST);
        header.add(btnAdd, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Main Rounded White Card
        ThemePalette.RoundedPanel card = new ThemePalette.RoundedPanel(12, Color.WHITE, ThemePalette.BORDER_SOFT);
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        // Toolbar: Search (left) + Filter & Ekspor (right)
        JPanel toolbar = new JPanel(new BorderLayout(12, 0));
        toolbar.setOpaque(false);

        txtSearch = ThemePalette.createStyledTextField("");
        txtSearch.setPreferredSize(new Dimension(280, 36));
        txtSearch.setToolTipText("Cari NIM, nama, atau kelas mahasiswa...");
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { refreshData(); }
            @Override public void removeUpdate(DocumentEvent e) { refreshData(); }
            @Override public void changedUpdate(DocumentEvent e) { refreshData(); }
        });

        JPanel leftSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftSearch.setOpaque(false);
        JLabel lblSearch = new JLabel("Cari:");
        lblSearch.setFont(ThemePalette.FONT_SMALL_BOLD);
        lblSearch.setForeground(ThemePalette.TEXT_MUTED);
        leftSearch.add(lblSearch);
        leftSearch.add(txtSearch);

        JPanel rightTools = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightTools.setOpaque(false);
        cboFilterStatus = new JComboBox<>(new String[]{"Semua Status", "Aktif", "Cuti", "Berisiko", "Non-Aktif"});
        cboFilterStatus.setPreferredSize(new Dimension(140, 36));
        cboFilterStatus.addActionListener(e -> refreshData());

        JButton btnExport = ThemePalette.createSecondaryButton("Ekspor CSV");
        btnExport.addActionListener(e -> handleExportCsv());
        rightTools.add(cboFilterStatus);
        rightTools.add(btnExport);

        toolbar.add(leftSearch, BorderLayout.WEST);
        toolbar.add(rightTools, BorderLayout.EAST);
        card.add(toolbar, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "NIM", "NAMA MAHASISWA", "PROGRAM STUDI", "KELAS", "ANGKATAN", "KEHADIRAN", "STATUS"}, 0) {
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

        // Card Footer: count + Ubah / Hapus action buttons
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
        String keyword = txtSearch.getText();
        String statusStr = (String) cboFilterStatus.getSelectedItem();
        List<Mahasiswa> list;
        if (statusStr == null || "Semua Status".equals(statusStr)) {
            list = controller.cariMahasiswa(keyword);
        } else {
            list = controller.cariMahasiswa(keyword, Mahasiswa.StatusMahasiswa.fromLabel(statusStr));
        }

        tableModel.setRowCount(0);
        for (Mahasiswa m : list) {
            tableModel.addRow(new Object[]{
                    m.getId(), m.getNim(), m.getNama(), m.getProdi(),
                    m.getKelas(), m.getAngkatan(), m.getKehadiranPersen() + "%",
                    "● " + m.getStatus().getLabel()
            });
        }
        lblFooterCount.setText("Menampilkan " + list.size() + " dari " + controller.getMahasiswaList().size() + " data");
    }

    private Mahasiswa getSelectedMahasiswa() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        String id = (String) tableModel.getValueAt(row, 0);
        for (Mahasiswa m : controller.getMahasiswaList()) {
            if (m.getId().equals(id)) return m;
        }
        return null;
    }

    private void handleEditSelected() {
        Mahasiswa m = getSelectedMahasiswa();
        if (m == null) {
            JOptionPane.showMessageDialog(this, "Pilih mahasiswa pada tabel terlebih dahulu.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        openMahasiswaDialog(m);
    }

    private void handleDeleteSelected() {
        Mahasiswa m = getSelectedMahasiswa();
        if (m == null) {
            JOptionPane.showMessageDialog(this, "Pilih mahasiswa yang akan dihapus.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus " + m.getNama() + " (" + m.getNim() + ")?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controller.hapusMahasiswa(m.getId());
            } catch (DataPersistenceException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleExportCsv() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("daftar_mahasiswa.csv"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter fw = new FileWriter(fc.getSelectedFile())) {
                fw.write("NIM,Nama,Email,Prodi,Kelas,Angkatan,Kehadiran,Status\n");
                for (Mahasiswa m : controller.getMahasiswaList()) {
                    fw.write(String.format("%s,\"%s\",%s,\"%s\",%s,%s,%d,%s\n",
                            m.getNim(), m.getNama(), m.getEmail(), m.getProdi(),
                            m.getKelas(), m.getAngkatan(), m.getKehadiranPersen(), m.getStatus().getLabel()));
                }
                JOptionPane.showMessageDialog(this, "Data mahasiswa berhasil diekspor ke CSV.",
                        "Ekspor Berhasil", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Gagal mengekspor: " + ex.getMessage(),
                        "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openMahasiswaDialog(Mahasiswa existing) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(owner, existing == null ? "Tambah Mahasiswa" : "Ubah Mahasiswa", true);
        dialog.setSize(460, 560);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 22, 12, 22));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(5, 4, 5, 4);

        JTextField fNim = ThemePalette.createStyledTextField(existing != null ? existing.getNim() : "");
        JTextField fNama = ThemePalette.createStyledTextField(existing != null ? existing.getNama() : "");
        JTextField fEmail = ThemePalette.createStyledTextField(existing != null ? existing.getEmail() : "");
        JTextField fAngkatan = ThemePalette.createStyledTextField(existing != null ? existing.getAngkatan() : "2023");
        JTextField fProdi = ThemePalette.createStyledTextField(existing != null ? existing.getProdi() : "Teknik Komputer");
        JTextField fKelas = ThemePalette.createStyledTextField(existing != null ? existing.getKelas() : "A");
        JSpinner fHadir = new JSpinner(new SpinnerNumberModel(existing != null ? existing.getKehadiranPersen() : 90, 0, 100, 1));
        JComboBox<Mahasiswa.StatusMahasiswa> fStatus = new JComboBox<>(Mahasiswa.StatusMahasiswa.values());
        if (existing != null) fStatus.setSelectedItem(existing.getStatus());

        int r = 0;
        r = addRow(form, g, r, "NIM", fNim);
        r = addRow(form, g, r, "Nama Lengkap", fNama);
        r = addRow(form, g, r, "Email", fEmail);
        r = addRow(form, g, r, "Angkatan", fAngkatan);
        r = addRow(form, g, r, "Program Studi", fProdi);
        r = addRow(form, g, r, "Kelas", fKelas);
        r = addRow(form, g, r, "Kehadiran (%)", fHadir);
        r = addRow(form, g, r, "Status", fStatus);

        g.gridx = 0; g.gridy = r; g.gridwidth = 2;
        form.add(new JLabel("Mata Kuliah Diambil:"), g);

        JPanel mkBox = new JPanel();
        mkBox.setLayout(new BoxLayout(mkBox, BoxLayout.Y_AXIS));
        mkBox.setBackground(Color.WHITE);
        Map<String, JCheckBox> checks = new LinkedHashMap<>();
        for (MataKuliah mk : controller.getMataKuliahList()) {
            JCheckBox cb = new JCheckBox(mk.getKode() + " - " + mk.getNama() + " (" + mk.getKelas() + ")");
            cb.setBackground(Color.WHITE);
            if (existing != null && existing.mengambilMataKuliah(mk.getId())) {
                cb.setSelected(true);
            }
            checks.put(mk.getId(), cb);
            mkBox.add(cb);
        }
        g.gridy = r + 1;
        JScrollPane mkScroll = new JScrollPane(mkBox);
        mkScroll.setPreferredSize(new Dimension(380, 95));
        form.add(mkScroll, g);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        btns.setBackground(ThemePalette.BG_LIGHT);
        JButton btnCancel = ThemePalette.createSecondaryButton("Batal");
        JButton btnSave = ThemePalette.createPrimaryButton("Simpan");
        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            try {
                List<String> selectedMks = new ArrayList<>();
                for (Map.Entry<String, JCheckBox> entry : checks.entrySet()) {
                    if (entry.getValue().isSelected()) selectedMks.add(entry.getKey());
                }
                String id = existing != null ? existing.getId() : "mhs-" + UUID.randomUUID().toString().substring(0, 6);
                Mahasiswa mhs = new Mahasiswa(id, fNim.getText(), fNama.getText(), fEmail.getText(),
                        fAngkatan.getText(), fProdi.getText(), fKelas.getText(),
                        (Integer) fHadir.getValue(),
                        (Mahasiswa.StatusMahasiswa) fStatus.getSelectedItem(), selectedMks);
                if (existing == null) {
                    controller.tambahMahasiswa(mhs);
                } else {
                    controller.ubahMahasiswa(mhs);
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
        g.gridwidth = 1;
        g.gridx = 0; g.gridy = row; g.weightx = 0.35;
        JLabel lbl = new JLabel(label);
        lbl.setFont(ThemePalette.FONT_SMALL_BOLD);
        panel.add(lbl, g);
        g.gridx = 1; g.weightx = 0.65;
        panel.add(comp, g);
        return row + 1;
    }
}
