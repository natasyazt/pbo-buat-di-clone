package id.ac.lectura.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Palet warna LECTURA (#355C7D, #6C5B7B, #C06C84, #F67280, #F8B195)
 * dengan komponen Swing modern bergaya Figma (anti-aliased, flat, rounded).
 */
public final class ThemePalette {
    // Palet warna utama LECTURA (tetap dipertahankan)
    public static final Color NAVY = new Color(0x35, 0x5C, 0x7D);
    public static final Color NAVY_DARK = new Color(0x28, 0x46, 0x5F);
    public static final Color PURPLE = new Color(0x6C, 0x5B, 0x7B);
    public static final Color ROSE = new Color(0xC0, 0x6C, 0x84);
    public static final Color CORAL = new Color(0xF6, 0x72, 0x80);
    public static final Color PEACH = new Color(0xF8, 0xB1, 0x95);
    public static final Color PEACH_SOFT = new Color(0xFD, 0xF0, 0xEB);

    // Warna netral & permukaan bergaya Figma
    public static final Color BG_LIGHT = new Color(0xF8, 0xF7, 0xFA);
    public static final Color SIDEBAR_BG = new Color(0xF3, 0xF1, 0xF6);
    public static final Color WHITE = Color.WHITE;
    public static final Color BORDER_SOFT = new Color(0xE4, 0xE0, 0xEA);
    public static final Color TABLE_HEADER_BG = new Color(0xF4, 0xF2, 0xF7);
    public static final Color TEXT_DARK = new Color(0x24, 0x36, 0x47);
    public static final Color TEXT_MUTED = new Color(0x6C, 0x5B, 0x7B);

    public static final Font FONT_HERO = new Font("Georgia", Font.BOLD, 30);
    public static final Font FONT_SERIF_TITLE = new Font("Georgia", Font.BOLD, 24);
    public static final Font FONT_CARD_TITLE = new Font("SansSerif", Font.BOLD, 15);
    public static final Font FONT_SUBTITLE = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 11);
    public static final Font FONT_SMALL_BOLD = new Font("SansSerif", Font.BOLD, 11);

    private ThemePalette() {
    }

    public static class RoundedPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private final int radius;
        private Color borderColor;

        public RoundedPanel(int radius, Color bg, Color borderColor) {
            this.radius = radius;
            this.borderColor = borderColor;
            setBackground(bg);
            setOpaque(false);
        }

        public void setBorderColor(Color borderColor) {
            this.borderColor = borderColor;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(0, 0, w - 1, h - 1, radius, radius));
            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 2, radius, radius));
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static class RoundedBorder extends AbstractBorder {
        private static final long serialVersionUID = 1L;
        private final int radius;
        private final Color color;
        private final Insets insets;

        public RoundedBorder(int radius, Color color, Insets insets) {
            this.radius = radius;
            this.color = color;
            this.insets = insets;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.draw(new RoundRectangle2D.Double(x + 0.5, y + 0.5, width - 2, height - 2, radius, radius));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return insets;
        }
    }

    public static JButton createPrimaryButton(String text) {
        return createModernButton(text, NAVY, NAVY_DARK, WHITE, null);
    }

    public static JButton createSecondaryButton(String text) {
        return createModernButton(text, WHITE, new Color(0xF4, 0xF2, 0xF7), NAVY, BORDER_SOFT);
    }

    public static JButton createDangerButton(String text) {
        return createModernButton(text, ROSE, CORAL, WHITE, null);
    }

    public static JButton createModernButton(String text, Color normalBg, Color hoverBg, Color fg, Color borderCol) {
        JButton btn = new JButton(text) {
            private static final long serialVersionUID = 1L;
            private boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? hoverBg : normalBg);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                if (borderCol != null) {
                    g2.setColor(borderCol);
                    g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, 10, 10));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(fg);
        btn.setFont(FONT_BOLD);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        return btn;
    }

    public static JTextField createStyledTextField(String initialText) {
        JTextField tf = new JTextField(initialText);
        styleInputField(tf);
        return tf;
    }

    public static JPasswordField createStyledPasswordField(String initialText) {
        JPasswordField pf = new JPasswordField(initialText);
        styleInputField(pf);
        return pf;
    }

    public static void styleInputField(JComponent field) {
        field.setFont(FONT_BODY);
        field.setBackground(WHITE);
        field.setForeground(TEXT_DARK);
        field.setBorder(new RoundedBorder(8, BORDER_SOFT, new Insets(8, 12, 8, 12)));
        field.setPreferredSize(new Dimension(220, 38));
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(44);
        table.setFont(FONT_BODY);
        table.setForeground(TEXT_DARK);
        table.setBackground(WHITE);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(0xEE, 0xEB, 0xF2));
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(0x35, 0x5C, 0x7D, 22));
        table.setSelectionForeground(NAVY_DARK);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TEXT_MUTED);
        header.setFont(new Font("SansSerif", Font.BOLD, 11));
        header.setPreferredSize(new Dimension(0, 38));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.LEFT);
        headerRenderer.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                if (!isSelected) {
                    lbl.setBackground(WHITE);
                }
                return lbl;
            }
        });
    }

    public static JPanel createKpiCard(String title, String value, String subtitle, Color accent) {
        RoundedPanel card = new RoundedPanel(12, WHITE, BORDER_SOFT);
        card.setLayout(new java.awt.BorderLayout(0, 6));
        card.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_SMALL);
        lblTitle.setForeground(TEXT_MUTED);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Georgia", Font.BOLD, 28));
        lblValue.setForeground(NAVY);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(FONT_SMALL);
        lblSub.setForeground(accent);

        card.add(lblTitle, java.awt.BorderLayout.NORTH);
        card.add(lblValue, java.awt.BorderLayout.CENTER);
        card.add(lblSub, java.awt.BorderLayout.SOUTH);
        return card;
    }

    public static javax.swing.border.Border createCardBorder() {
        return BorderFactory.createCompoundBorder(
                new RoundedBorder(12, BORDER_SOFT, new Insets(1, 1, 1, 1)),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)
        );
    }
}
