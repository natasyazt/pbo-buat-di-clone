package id.ac.lectura.view;

import id.ac.lectura.controller.AcademicController;
import id.ac.lectura.exception.DataPersistenceException;
import id.ac.lectura.exception.ValidasiInputException;
import id.ac.lectura.model.KomponenPenilaian;
import id.ac.lectura.model.MataKuliah;
import id.ac.lectura.model.RekapNilai;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * Tampilan Input & Rekap Nilai bergaya Figma (Full-width Table Card + Modal Input Nilai).
 */
public class InputNilaiPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final AcademicController controller;
    private JLabel lblCourseInfo;
    private JRadioButton rbSemua;
    private JRadioButton rbLulus;
    private JRadioButton rbBermasalah;
    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel lblFooterCount;

    public InputNilaiPanel(AcademicController controller) {
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
        JLabel title = new JLabel("Input & Rekap Nilai");
        title.setFont(ThemePalette.FONT_SERIF_TITLE);
        title.setForeground(ThemePalette.NAVY_DARK);
        lblCourseInfo = new JLabel("Mata Kuliah: -");
        lblCourseInfo.setFont(ThemePalette.FONT_SUBTITLE);
        lblCourseInfo.setForeground(ThemePalette.TEXT_MUTED);
        titleBox.add(title);
        titleBox.add(lblCourseInfo);

        JButton btnInput = ThemePalette.createPrimaryButton("Input / Ubah Nilai Terpilih");
        btnInput.addActionListener(e -> handleEditSelected());

        header.add(titleBox, BorderLayout.WEST);
        header.add(btnInput, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Card
        ThemePalette.RoundedPanel card = new ThemePalette.RoundedPanel(12, Color.WHITE, ThemePalette.BORDER_SOFT);
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        // Filter Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);
        JLabel lblF = new JLabel("Filter Status:");
        lblF.setFont(ThemePalette.FONT_SMALL_BOLD);
        lblF.setForeground(ThemePalette.TEXT_MUTED);
        rbSemua = new JRadioButton("Semua", true);
        rbLulus = new JRadioButton("Lulus");
        rbBermasalah = new JRadioButton("Remidi / Tidak Lulus");
        rbSemua.setOpaque(false);
        rbLulus.setOpaque(false);
        rbBermasalah.setOpaque(false);
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbSemua);
        bg.add(rbLulus);
        bg.add(rbBermasalah);
        rbSemua.addActionListener(e -> refreshData());
        rbLulus.addActionListener(e -> refreshData());
        rbBermasalah.addActionListener(e -> refreshData());

        toolbar.add(lblF);
        toolbar.add(rbSemua);
        toolbar.add(rbLulus);
        toolbar.add(rbBermasalah);
        card.add(toolbar, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ThemePalette.styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(ThemePalette.BORDER_SOFT));
        card.add(scroll, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        lblFooterCount = new JLabel("Menampilkan 0 data");
        lblFooterCount.setFont(ThemePalette.FONT_SMALL);
        lblFooterCount.setForeground(ThemePalette.TEXT_MUTED);
        JLabel hint = new JLabel("Klik baris mahasiswa lalu tekan \"Input / Ubah Nilai Terpilih\".");
        hint.setFont(ThemePalette.FONT_SMALL);
        hint.setForeground(ThemePalette.TEXT_MUTED);
        footer.add(lblFooterCount, BorderLayout.WEST);
        footer.add(hint, BorderLayout.EAST);
        card.add(footer, BorderLayout.SOUTH);

        add(card, BorderLayout.CENTER);
    }

    public void refreshData() {
        MataKuliah mk = controller.getSelectedMataKuliah();
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        if (mk == null) {
            lblCourseInfo.setText("Pilih mata kuliah di sidebar.");
            return;
        }
        lblCourseInfo.setText(mk.getKode() + " - " + mk.getNama() + " (" + mk.getKelas() + ")");

        List<KomponenPenilaian> komps = controller.getKomponenByMataKuliah(mk.getId());
        tableModel.addColumn("ID");
        tableModel.addColumn("NIM");
        tableModel.addColumn("NAMA MAHASISWA");
        for (KomponenPenilaian k : komps) {
            tableModel.addColumn(k.getNama() + " (" + k.getBobot() + "%)");
        }
        tableModel.addColumn("NILAI AKHIR");
        tableModel.addColumn("HURUF");
        tableModel.addColumn("INDEKS");
        tableModel.addColumn("STATUS");

        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);

        int shown = 0;
        for (RekapNilai r : controller.getRekapByMataKuliah(mk.getId())) {
            if (rbLulus.isSelected() && !"LULUS".equals(r.getStatusLulus())) continue;
            if (rbBermasalah.isSelected() && "LULUS".equals(r.getStatusLulus())) continue;

            Object[] rowData = new Object[7 + komps.size()];
            rowData[0] = r.getMahasiswa().getId();
            rowData[1] = r.getMahasiswa().getNim();
            rowData[2] = r.getMahasiswa().getNama();
            int idx = 3;
            for (KomponenPenilaian k : komps) {
                rowData[idx++] = String.format("%.1f", r.getNilaiKomponen().getOrDefault(k.getId(), 0.0));
            }
            rowData[idx++] = String.format("%.2f", r.getNilaiAkhir());
            rowData[idx++] = r.getHurufMutu();
            rowData[idx++] = String.format("%.1f", r.getIndeks());
            rowData[idx] = r.getStatusLulus();
            tableModel.addRow(rowData);
            shown++;
        }
        lblFooterCount.setText("Menampilkan " + shown + " mahasiswa");
    }

    private void handleEditSelected() {
        int row = table.getSelectedRow();
        MataKuliah mk = controller.getSelectedMataKuliah();
        if (row < 0 || mk == null) {
            JOptionPane.showMessageDialog(this, "Pilih mahasiswa pada tabel terlebih dahulu.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String mhsId = (String) tableModel.getValueAt(row, 0);
        RekapNilai target = null;
        for (RekapNilai r : controller.getRekapByMataKuliah(mk.getId())) {
            if (r.getMahasiswa().getId().equals(mhsId)) {
                target = r;
                break;
            }
        }
        if (target != null) {
            openNilaiDialog(mk, target);
        }
    }

    private void openNilaiDialog(MataKuliah mk, RekapNilai rekap) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(owner,
                "Input Nilai: " + rekap.getMahasiswa().getNama(), true);
        dialog.setSize(460, 440);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 22, 12, 22));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(6, 4, 6, 4);

        Map<String, JSpinner> spinners = new LinkedHashMap<>();
        int row = 0;
        for (KomponenPenilaian k : controller.getKomponenByMataKuliah(mk.getId())) {
            g.gridx = 0; g.gridy = row; g.weightx = 0.65;
            JLabel lbl = new JLabel(k.getNama() + " (" + k.getBobot() + "%)");
            lbl.setFont(ThemePalette.FONT_SMALL_BOLD);
            form.add(lbl, g);

            g.gridx = 1; g.weightx = 0.35;
            double currentVal = rekap.getNilaiKomponen().getOrDefault(k.getId(), 0.0);
            JSpinner spn = new JSpinner(new SpinnerNumberModel(currentVal, 0.0, 100.0, 1.0));
            spinners.put(k.getId(), spn);
            form.add(spn, g);
            row++;
        }

        g.gridx = 0; g.gridy = row; g.gridwidth = 2;
        JLabel lblNote = new JLabel("Catatan Dosen:");
        lblNote.setFont(ThemePalette.FONT_SMALL_BOLD);
        form.add(lblNote, g);

        JTextArea fNote = new JTextArea(rekap.getCatatanDosen(), 3, 20);
        fNote.setLineWrap(true);
        fNote.setWrapStyleWord(true);
        g.gridy = row + 1;
        form.add(new JScrollPane(fNote), g);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        btns.setBackground(ThemePalette.BG_LIGHT);
        JButton btnCancel = ThemePalette.createSecondaryButton("Batal");
        JButton btnSave = ThemePalette.createPrimaryButton("Simpan Nilai");
        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            try {
                Map<String, Double> skorMap = new LinkedHashMap<>();
                for (Map.Entry<String, JSpinner> entry : spinners.entrySet()) {
                    double val = ((Number) entry.getValue().getValue()).doubleValue();
                    skorMap.put(entry.getKey(), val);
                }
                controller.simpanNilaiMahasiswa(rekap.getMahasiswa().getId(), mk.getId(), skorMap, fNote.getText());
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
}
