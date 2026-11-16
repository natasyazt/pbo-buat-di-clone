package id.ac.lectura.view;

import id.ac.lectura.controller.AcademicController;
import id.ac.lectura.controller.AuthController;
import id.ac.lectura.model.AktivitasLog;
import id.ac.lectura.model.MataKuliah;
import id.ac.lectura.model.RekapNilai;
import id.ac.lectura.util.ThemePalette;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

/**
 * Tampilan Beranda (Dashboard) bergaya Figma dengan palet warna LECTURA.
 */
public class DashboardPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final AcademicController controller;
    private final AuthController authController;
    private final Consumer<String> onNavigate;

    private JPanel kpiContainer;
    private JPanel progressListContainer;
    private VerticalBarChartPanel barChartPanel;
    private JPanel activityListContainer;

    public DashboardPanel(AcademicController controller, AuthController authController,
                          Consumer<String> onNavigate) {
        this.controller = controller;
        this.authController = authController;
        this.onNavigate = onNavigate;
        initUI();
        controller.addChangeListener(this::refreshData);
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(ThemePalette.BG_LIGHT);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(ThemePalette.BG_LIGHT);
        content.setBorder(BorderFactory.createEmptyBorder(24, 28, 28, 28));

        // Welcome Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBox.setOpaque(false);
        JLabel lblWelcome = new JLabel("Selamat datang, " + authController.getDosenAktif().getNama());
        lblWelcome.setFont(ThemePalette.FONT_SERIF_TITLE);
        lblWelcome.setForeground(ThemePalette.NAVY_DARK);
        JLabel lblSub = new JLabel("Berikut ringkasan aktivitas akademik semester ini.");
        lblSub.setFont(ThemePalette.FONT_SUBTITLE);
        lblSub.setForeground(ThemePalette.TEXT_MUTED);
        titleBox.add(lblWelcome);
        titleBox.add(lblSub);

        header.add(titleBox, BorderLayout.WEST);
        content.add(header);
        content.add(Box.createVerticalStrut(18));

        // 4 KPI Cards
        kpiContainer = new JPanel(new GridLayout(1, 4, 14, 0));
        kpiContainer.setOpaque(false);
        kpiContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 118));
        content.add(kpiContainer);
        content.add(Box.createVerticalStrut(18));

        // Middle Split: Progres Input Nilai (left) + Distribusi Nilai Bar Chart (right)
        JPanel midRow = new JPanel(new GridLayout(1, 2, 16, 0));
        midRow.setOpaque(false);
        midRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        // Left Card: Progres per MK
        ThemePalette.RoundedPanel progressCard = new ThemePalette.RoundedPanel(12, Color.WHITE, ThemePalette.BORDER_SOFT);
        progressCard.setLayout(new BorderLayout(0, 12));
        progressCard.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JPanel pHead = new JPanel(new BorderLayout());
        pHead.setOpaque(false);
        JPanel pTitles = new JPanel(new GridLayout(2, 1));
        pTitles.setOpaque(false);
        JLabel pT = new JLabel("Progres Penilaian Mata Kuliah");
        pT.setFont(ThemePalette.FONT_CARD_TITLE);
        pT.setForeground(ThemePalette.NAVY_DARK);
        JLabel pSub = new JLabel("Per mata kuliah yang Anda ampu");
        pSub.setFont(ThemePalette.FONT_SMALL);
        pSub.setForeground(ThemePalette.TEXT_MUTED);
        pTitles.add(pT);
        pTitles.add(pSub);

        JButton btnSeeAll = new JButton("Input Nilai  >");
        btnSeeAll.setFont(ThemePalette.FONT_SMALL_BOLD);
        btnSeeAll.setForeground(ThemePalette.NAVY);
        btnSeeAll.setContentAreaFilled(false);
        btnSeeAll.setBorderPainted(false);
        btnSeeAll.addActionListener(e -> onNavigate.accept(SidebarPanel.VIEW_INPUT_NILAI));

        pHead.add(pTitles, BorderLayout.WEST);
        pHead.add(btnSeeAll, BorderLayout.EAST);
        progressCard.add(pHead, BorderLayout.NORTH);

        progressListContainer = new JPanel();
        progressListContainer.setLayout(new BoxLayout(progressListContainer, BoxLayout.Y_AXIS));
        progressListContainer.setOpaque(false);
        progressCard.add(progressListContainer, BorderLayout.CENTER);

        // Right Card: Distribusi Nilai Vertical Bar Chart
        ThemePalette.RoundedPanel chartCard = new ThemePalette.RoundedPanel(12, Color.WHITE, ThemePalette.BORDER_SOFT);
        chartCard.setLayout(new BorderLayout(0, 12));
        chartCard.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JPanel cTitles = new JPanel(new GridLayout(2, 1));
        cTitles.setOpaque(false);
        JLabel cT = new JLabel("Distribusi Nilai");
        cT.setFont(ThemePalette.FONT_CARD_TITLE);
        cT.setForeground(ThemePalette.NAVY_DARK);
        JLabel cSub = new JLabel("Distribusi huruf mutu mata kuliah aktif");
        cSub.setFont(ThemePalette.FONT_SMALL);
        cSub.setForeground(ThemePalette.TEXT_MUTED);
        cTitles.add(cT);
        cTitles.add(cSub);
        chartCard.add(cTitles, BorderLayout.NORTH);

        barChartPanel = new VerticalBarChartPanel();
        chartCard.add(barChartPanel, BorderLayout.CENTER);

        midRow.add(progressCard);
        midRow.add(chartCard);
        content.add(midRow);
        content.add(Box.createVerticalStrut(18));

        // Bottom Card: Aktivitas Terbaru
        ThemePalette.RoundedPanel actCard = new ThemePalette.RoundedPanel(12, Color.WHITE, ThemePalette.BORDER_SOFT);
        actCard.setLayout(new BorderLayout(0, 12));
        actCard.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JPanel aTitles = new JPanel(new GridLayout(2, 1));
        aTitles.setOpaque(false);
        JLabel aT = new JLabel("Aktivitas Terbaru");
        aT.setFont(ThemePalette.FONT_CARD_TITLE);
        aT.setForeground(ThemePalette.NAVY_DARK);
        JLabel aSub = new JLabel("Perubahan data akademik terakhir");
        aSub.setFont(ThemePalette.FONT_SMALL);
        aSub.setForeground(ThemePalette.TEXT_MUTED);
        aTitles.add(aT);
        aTitles.add(aSub);
        actCard.add(aTitles, BorderLayout.NORTH);

        activityListContainer = new JPanel();
        activityListContainer.setLayout(new BoxLayout(activityListContainer, BoxLayout.Y_AXIS));
        activityListContainer.setOpaque(false);
        actCard.add(activityListContainer, BorderLayout.CENTER);

        content.add(actCard);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    public void refreshData() {
        MataKuliah mk = controller.getSelectedMataKuliah();
        int totalMhsAll = controller.getMahasiswaList().size();
        int totalMk = controller.getMataKuliahList().size();

        List<RekapNilai> rekap = mk != null ? controller.getRekapByMataKuliah(mk.getId()) : List.of();
        int mhsKelas = rekap.size();
        double sum = 0;
        int lulus = 0;
        for (RekapNilai r : rekap) {
            sum += r.getNilaiAkhir();
            if ("LULUS".equals(r.getStatusLulus())) lulus++;
        }
        double avg = mhsKelas > 0 ? sum / mhsKelas : 0.0;
        int passRate = mhsKelas > 0 ? (int) Math.round((lulus * 100.0) / mhsKelas) : 0;

        kpiContainer.removeAll();
        kpiContainer.add(ThemePalette.createKpiCard("Total Mahasiswa", String.valueOf(totalMhsAll),
                mhsKelas + " di kelas aktif", ThemePalette.NAVY));
        kpiContainer.add(ThemePalette.createKpiCard("Mata Kuliah Aktif", String.valueOf(totalMk),
                "Semester Genap 2024/2025", ThemePalette.PURPLE));
        kpiContainer.add(ThemePalette.createKpiCard("Tingkat Kelulusan", passRate + "%",
                lulus + " dari " + mhsKelas + " mahasiswa", ThemePalette.NAVY));
        kpiContainer.add(ThemePalette.createKpiCard("Rata-rata Nilai", String.format("%.1f", avg),
                mk != null ? mk.getKode() + " (" + mk.getKelas() + ")" : "-", ThemePalette.ROSE));
        kpiContainer.revalidate();
        kpiContainer.repaint();

        // Update Progres per MK
        progressListContainer.removeAll();
        int shown = 0;
        for (MataKuliah course : controller.getMataKuliahList()) {
            if (shown >= 3) break;
            int totalBobot = controller.getTotalBobotMataKuliah(course.getId());
            JPanel row = new JPanel(new BorderLayout(12, 0));
            row.setOpaque(false);
            row.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

            JPanel left = new JPanel(new GridLayout(2, 1));
            left.setOpaque(false);
            left.setPreferredSize(new Dimension(210, 34));
            JLabel cName = new JLabel(course.getNama());
            cName.setFont(ThemePalette.FONT_BOLD);
            cName.setForeground(ThemePalette.TEXT_DARK);
            JLabel cCode = new JLabel(course.getKode() + " · " + course.getKelas());
            cCode.setFont(ThemePalette.FONT_SMALL);
            cCode.setForeground(ThemePalette.TEXT_MUTED);
            left.add(cName);
            left.add(cCode);

            JProgressBar bar = new JProgressBar(0, 100);
            bar.setValue(Math.min(100, totalBobot));
            bar.setForeground(ThemePalette.NAVY);
            bar.setPreferredSize(new Dimension(120, 10));

            JLabel pct = new JLabel(totalBobot + "%");
            pct.setFont(ThemePalette.FONT_SMALL_BOLD);
            pct.setForeground(ThemePalette.NAVY);

            row.add(left, BorderLayout.WEST);
            row.add(bar, BorderLayout.CENTER);
            row.add(pct, BorderLayout.EAST);
            progressListContainer.add(row);
            shown++;
        }
        progressListContainer.revalidate();
        progressListContainer.repaint();

        // Update Vertical Bar Chart
        if (mk != null) {
            barChartPanel.setData(controller.getDistribusiHurufMutu(mk.getId()));
        }

        // Update Aktivitas Terbaru
        activityListContainer.removeAll();
        int actCount = 0;
        for (AktivitasLog log : controller.getAktivitasList()) {
            if (actCount >= 4) break;
            JPanel item = new JPanel(new BorderLayout(12, 0));
            item.setOpaque(false);
            item.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xF0, 0xEE, 0xF4)),
                    BorderFactory.createEmptyBorder(8, 0, 8, 0)
            ));
            item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

            JLabel dot = new JLabel("●");
            dot.setForeground("NILAI".equals(log.getKategori()) ? ThemePalette.CORAL : ThemePalette.NAVY);

            JPanel txt = new JPanel(new GridLayout(2, 1));
            txt.setOpaque(false);
            JLabel a1 = new JLabel(log.getAksi());
            a1.setFont(ThemePalette.FONT_BOLD);
            a1.setForeground(ThemePalette.TEXT_DARK);
            JLabel a2 = new JLabel(log.getDetail());
            a2.setFont(ThemePalette.FONT_SMALL);
            a2.setForeground(ThemePalette.TEXT_MUTED);
            txt.add(a1);
            txt.add(a2);

            JLabel time = new JLabel(log.getWaktu());
            time.setFont(ThemePalette.FONT_SMALL);
            time.setForeground(ThemePalette.TEXT_MUTED);

            item.add(dot, BorderLayout.WEST);
            item.add(txt, BorderLayout.CENTER);
            item.add(time, BorderLayout.EAST);
            activityListContainer.add(item);
            actCount++;
        }
        activityListContainer.revalidate();
        activityListContainer.repaint();
    }

    /**
     * Panel grafik batang vertikal bergaya Figma (A, AB, B, BC, C, D, E).
     */
    private static class VerticalBarChartPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private Map<String, Integer> data = Map.of();

        public VerticalBarChartPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(320, 170));
        }

        public void setData(Map<String, Integer> data) {
            this.data = data;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || data.isEmpty()) return;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int maxVal = 1;
            for (int v : data.values()) {
                if (v > maxVal) maxVal = v;
            }

            int n = data.size();
            int w = getWidth();
            int h = getHeight();
            int bottomPad = 24;
            int topPad = 20;
            int availH = Math.max(20, h - bottomPad - topPad);
            int slotW = w / Math.max(1, n);
            int barW = Math.min(34, Math.max(16, slotW - 14));

            int idx = 0;
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                int val = entry.getValue();
                int barH = (int) ((val * 1.0 / maxVal) * availH);
                if (val > 0 && barH < 8) barH = 8;

                int x = idx * slotW + (slotW - barW) / 2;
                int y = h - bottomPad - barH;

                Color barColor = ("D".equals(entry.getKey()) || "E".equals(entry.getKey()))
                        ? ThemePalette.CORAL : ThemePalette.NAVY;

                if (val > 0) {
                    g2.setColor(barColor);
                    g2.fillRoundRect(x, y, barW, barH, 6, 6);
                } else {
                    g2.setColor(ThemePalette.BORDER_SOFT);
                    g2.fillRoundRect(x, h - bottomPad - 3, barW, 3, 2, 2);
                }

                // Value label above bar
                g2.setFont(ThemePalette.FONT_SMALL);
                g2.setColor(ThemePalette.TEXT_MUTED);
                String vStr = String.valueOf(val);
                int vw = g2.getFontMetrics().stringWidth(vStr);
                g2.drawString(vStr, x + (barW - vw) / 2, Math.max(12, y - 5));

                // Grade label below bar
                g2.setFont(ThemePalette.FONT_SMALL_BOLD);
                g2.setColor(ThemePalette.TEXT_DARK);
                int lw = g2.getFontMetrics().stringWidth(entry.getKey());
                g2.drawString(entry.getKey(), x + (barW - lw) / 2, h - 6);

                idx++;
            }
            g2.dispose();
        }
    }
}
