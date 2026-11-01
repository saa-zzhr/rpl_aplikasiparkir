package com.mycompany.parkir;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class Parkir {

    // Pengaturan koneksi (XAMPP default: user root, password kosong)
    static final String URL = "jdbc:mysql://localhost:3306/parkir_db";
    static final String USER = "root";
    static final String PASS = "";

    // Tarif per jam
    static final int TARIF_MOTOR = 2000;
    static final int TARIF_MOBIL = 5000;

    // Akun login (satu akun tetap)
    static final String LOGIN_USERNAME = "salsa";
    static final String LOGIN_PASSWORD = "salsa123";

    static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    // Warna dan font tampilan
    static final Color BIRU = new Color(31, 78, 121);
    static final Color BIRU_MUDA = new Color(200, 222, 245);
    static final Color HIJAU = new Color(34, 139, 84);
    static final Color ORANYE = new Color(214, 108, 28);
    static final Color LATAR = new Color(243, 245, 249);
    static final Color GARIS = new Color(208, 214, 222);
    static final Font FONT = new Font("Segoe UI", Font.PLAIN, 14);
    static final Font FONT_TEBAL = new Font("Segoe UI", Font.BOLD, 14);
    static final Font FONT_JUDUL = new Font("Segoe UI", Font.BOLD, 22);

    // Program utama: membuka jendela login
    public static void main(String[] args) {
        UIManager.put("OptionPane.messageFont", FONT);
        UIManager.put("OptionPane.buttonFont", FONT);
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    // Tombol berwarna
    static JButton tombol(String teks, Color warna) {
        JButton b = new JButton(teks);
        b.setFont(FONT_TEBAL);
        b.setBackground(warna);
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(b.getPreferredSize().width + 24, 36));
        return b;
    }

    // Kotak isian dengan garis tipis
    static void gayaField(JTextField f, int lebar) {
        f.setFont(FONT);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GARIS), new EmptyBorder(4, 8, 4, 8)));
        f.setPreferredSize(new Dimension(lebar, 36));
    }

    static JLabel label(String teks) {
        JLabel l = new JLabel(teks);
        l.setFont(FONT_TEBAL);
        l.setForeground(new Color(60, 66, 76));
        return l;
    }

    // Format angka menjadi rupiah, contoh: Rp2.000
    static String rupiah(int nilai) {
        return "Rp" + String.format("%,d", nilai).replace(',', '.');
    }
}

// ---------- JENDELA LOGIN ----------
class LoginFrame extends JFrame {

    private final JTextField txtUser = new JTextField();
    private final JPasswordField txtPass = new JPasswordField();
    private int percobaan = 0;

    LoginFrame() {
        setTitle("Login Parkir");
        setSize(380, 380);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // Header biru
        JPanel header = new JPanel(new GridLayout(2, 1, 0, 4));
        header.setBackground(Parkir.BIRU);
        header.setBorder(new EmptyBorder(22, 20, 22, 20));
        JLabel judul = new JLabel("APLIKASI PARKIR", SwingConstants.CENTER);
        judul.setFont(Parkir.FONT_JUDUL);
        judul.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Silakan login untuk melanjutkan", SwingConstants.CENTER);
        sub.setFont(Parkir.FONT);
        sub.setForeground(Parkir.BIRU_MUDA);
        header.add(judul);
        header.add(sub);

        // Form login
        Parkir.gayaField(txtUser, 200);
        Parkir.gayaField(txtPass, 200);
        JButton btnLogin = Parkir.tombol("Login", Parkir.BIRU);
        btnLogin.addActionListener(e -> prosesLogin());

        JPanel isi = new JPanel(new GridBagLayout());
        isi.setBackground(Color.WHITE);
        isi.setBorder(new EmptyBorder(20, 32, 24, 32));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(4, 0, 4, 0);
        c.gridy = 0;
        isi.add(Parkir.label("Username"), c);
        c.gridy = 1;
        isi.add(txtUser, c);
        c.gridy = 2;
        isi.add(Parkir.label("Password"), c);
        c.gridy = 3;
        isi.add(txtPass, c);
        c.gridy = 4;
        c.insets = new Insets(20, 0, 0, 0);
        isi.add(btnLogin, c);

        add(header, BorderLayout.NORTH);
        add(isi, BorderLayout.CENTER);

        getRootPane().setDefaultButton(btnLogin); // tekan Enter = klik Login
    }

    private void prosesLogin() {
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());

        if (user.equals(Parkir.LOGIN_USERNAME) && pass.equals(Parkir.LOGIN_PASSWORD)) {
            dispose();
            new MainFrame().setVisible(true);
            return;
        }

        percobaan++;
        if (percobaan >= 3) {
            JOptionPane.showMessageDialog(this, "Gagal login 3 kali. Program ditutup.",
                    "Login Gagal", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        JOptionPane.showMessageDialog(this,
                "Username atau password salah. (" + percobaan + "/3)",
                "Login Gagal", JOptionPane.ERROR_MESSAGE);
        txtPass.setText("");
    }
}

// ---------- JENDELA UTAMA ----------
class MainFrame extends JFrame {

    private Connection conn;

    private final JTextField txtPlat = new JTextField();
    private final JComboBox<String> cmbJenis = new JComboBox<>(new String[]{"Motor", "Mobil"});
    private final JLabel lblStatus = new JLabel(" ");
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Plat Nomor", "Jenis", "Jam Masuk", "Jam Keluar", "Total Bayar"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tabel = new JTable(model);

    MainFrame() {
        // Konek ke database dulu
        try {
            conn = DriverManager.getConnection(Parkir.URL, Parkir.USER, Parkir.PASS);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal konek database:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        setTitle("Aplikasi Parkir");
        setSize(900, 600);
        setMinimumSize(new Dimension(800, 480));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Parkir.LATAR);
        setLayout(new BorderLayout());

        // Tutup koneksi saat jendela ditutup
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    if (conn != null) conn.close();
                } catch (SQLException ex) {
                    // diabaikan
                }
            }
        });

        add(buatHeader(), BorderLayout.NORTH);
        add(buatIsi(), BorderLayout.CENTER);

        lblStatus.setFont(Parkir.FONT);
        lblStatus.setForeground(new Color(80, 86, 96));
        lblStatus.setBorder(new EmptyBorder(6, 24, 10, 24));
        add(lblStatus, BorderLayout.SOUTH);

        muatData();
    }

    // ---------- TAMPILAN ----------
    private JPanel buatHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Parkir.BIRU);
        header.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel judul = new JLabel("APLIKASI PARKIR");
        judul.setFont(Parkir.FONT_JUDUL);
        judul.setForeground(Color.WHITE);

        JLabel petugas = new JLabel("Petugas: " + Parkir.LOGIN_USERNAME);
        petugas.setFont(Parkir.FONT);
        petugas.setForeground(Parkir.BIRU_MUDA);

        header.add(judul, BorderLayout.WEST);
        header.add(petugas, BorderLayout.EAST);
        return header;
    }

    private JPanel buatIsi() {
        // Kartu input kendaraan
        Parkir.gayaField(txtPlat, 170);
        txtPlat.addActionListener(e -> kendaraanMasuk()); // Enter = Masuk

        cmbJenis.setFont(Parkir.FONT);
        cmbJenis.setPreferredSize(new Dimension(110, 36));
        cmbJenis.setBackground(Color.WHITE);

        JButton btnMasuk = Parkir.tombol("Masuk", Parkir.HIJAU);
        btnMasuk.addActionListener(e -> kendaraanMasuk());

        JButton btnKeluar = Parkir.tombol("Keluar", Parkir.ORANYE);
        btnKeluar.addActionListener(e -> kendaraanKeluar());

        JButton btnStruk = Parkir.tombol("Cetak Struk", Parkir.BIRU);
        btnStruk.addActionListener(e -> cetakStruk());

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        form.setBackground(Color.WHITE);
        form.setBorder(kartu("Input Kendaraan"));
        form.add(Parkir.label("Plat Nomor"));
        form.add(txtPlat);
        form.add(Parkir.label("Jenis"));
        form.add(cmbJenis);
        form.add(btnMasuk);
        form.add(btnKeluar);
        form.add(btnStruk);

        // Kartu tabel data parkir
        aturTabel();
        JScrollPane scroll = new JScrollPane(tabel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        JPanel panelTabel = new JPanel(new BorderLayout());
        panelTabel.setBackground(Color.WHITE);
        panelTabel.setBorder(kartu("Data Parkir"));
        panelTabel.add(scroll, BorderLayout.CENTER);

        JPanel isi = new JPanel(new BorderLayout(0, 14));
        isi.setBackground(Parkir.LATAR);
        isi.setBorder(new EmptyBorder(18, 24, 6, 24));
        isi.add(form, BorderLayout.NORTH);
        isi.add(panelTabel, BorderLayout.CENTER);
        return isi;
    }

    private TitledBorder kartu(String judul) {
        TitledBorder tb = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Parkir.GARIS), " " + judul + " ");
        tb.setTitleFont(Parkir.FONT_TEBAL);
        tb.setTitleColor(Parkir.BIRU);
        return tb;
    }

    private void aturTabel() {
        tabel.setFont(Parkir.FONT);
        tabel.setRowHeight(30);
        tabel.setShowVerticalLines(false);
        tabel.setGridColor(new Color(228, 232, 238));
        tabel.setSelectionBackground(Parkir.BIRU_MUDA);
        tabel.setSelectionForeground(Color.BLACK);
        tabel.setFillsViewportHeight(true);

        // Header tabel
        DefaultTableCellRenderer hr = new DefaultTableCellRenderer();
        hr.setBackground(Parkir.BIRU);
        hr.setForeground(Color.WHITE);
        hr.setFont(Parkir.FONT_TEBAL);
        hr.setHorizontalAlignment(SwingConstants.CENTER);
        hr.setOpaque(true);
        JTableHeader th = tabel.getTableHeader();
        th.setDefaultRenderer(hr);
        th.setPreferredSize(new Dimension(0, 34));
        th.setReorderingAllowed(false);

        // Isi tabel: baris selang-seling, kendaraan yang masih parkir berwarna hijau
        tabel.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean isSel, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(t, v, isSel, false, row, col);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (col == 0) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else if (col == 5) {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
                if (!isSel) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 248, 252));
                    boolean masihParkir = "-".equals(String.valueOf(t.getValueAt(row, 4)));
                    setForeground(masihParkir ? new Color(20, 120, 60) : Color.BLACK);
                }
                return this;
            }
        });

        tabel.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(130);
        tabel.getColumnModel().getColumn(2).setPreferredWidth(90);
        tabel.getColumnModel().getColumn(3).setPreferredWidth(170);
        tabel.getColumnModel().getColumn(4).setPreferredWidth(170);
        tabel.getColumnModel().getColumn(5).setPreferredWidth(120);

        // Klik baris tabel = plat nomor terisi otomatis
        tabel.getSelectionModel().addListSelectionListener(e -> {
            int baris = tabel.getSelectedRow();
            if (!e.getValueIsAdjusting() && baris >= 0) {
                txtPlat.setText(model.getValueAt(baris, 1).toString());
            }
        });
    }

    // ---------- DATA PARKIR (tabel) ----------
    private void muatData() {
        model.setRowCount(0);
        int total = 0;
        int masihParkir = 0;
        String sql = "SELECT * FROM parkir ORDER BY id DESC";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Timestamp k = rs.getTimestamp("jam_keluar");
                total++;
                if (k == null) masihParkir++;
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("plat_nomor"),
                    rs.getString("jenis"),
                    rs.getTimestamp("jam_masuk").toLocalDateTime().format(Parkir.FORMAT),
                    (k == null) ? "-" : k.toLocalDateTime().format(Parkir.FORMAT),
                    (k == null) ? "-" : Parkir.rupiah(rs.getInt("total_bayar"))
                });
            }
        } catch (SQLException e) {
            tampilError(e);
        }
        lblStatus.setText("Sedang parkir: " + masihParkir + " kendaraan   |   Total data: " + total);
    }

    // ---------- KENDARAAN MASUK ----------
    private void kendaraanMasuk() {
        String plat = txtPlat.getText().trim().toUpperCase();
        if (plat.isEmpty()) {
            peringatan("Plat nomor harus diisi.");
            return;
        }

        try {
            String cek = "SELECT id FROM parkir WHERE plat_nomor = ? AND jam_keluar IS NULL";
            try (PreparedStatement ps = conn.prepareStatement(cek)) {
                ps.setString(1, plat);
                if (ps.executeQuery().next()) {
                    peringatan("Kendaraan " + plat + " masih tercatat parkir.");
                    return;
                }
            }

            String jenis = cmbJenis.getSelectedItem().toString();
            LocalDateTime sekarang = LocalDateTime.now();
            String sql = "INSERT INTO parkir (plat_nomor, jenis, jam_masuk) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, plat);
                ps.setString(2, jenis);
                ps.setTimestamp(3, Timestamp.valueOf(sekarang));
                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(this,
                    plat + " (" + jenis + ") masuk pukul " + sekarang.format(Parkir.FORMAT),
                    "Kendaraan Masuk", JOptionPane.INFORMATION_MESSAGE);
            kosongkanPlat();
            muatData();
        } catch (SQLException e) {
            tampilError(e);
        }
    }

    // ---------- KENDARAAN KELUAR ----------
    private void kendaraanKeluar() {
        String plat = txtPlat.getText().trim().toUpperCase();
        if (plat.isEmpty()) {
            peringatan("Isi plat nomor dulu, atau klik barisnya di tabel.");
            return;
        }

        try {
            int id;
            String jenis;
            LocalDateTime masuk;
            String cari = "SELECT id, jenis, jam_masuk FROM parkir WHERE plat_nomor = ? AND jam_keluar IS NULL";
            try (PreparedStatement ps = conn.prepareStatement(cari)) {
                ps.setString(1, plat);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    peringatan("Kendaraan " + plat + " tidak ditemukan / sudah keluar.");
                    return;
                }
                id = rs.getInt("id");
                jenis = rs.getString("jenis");
                masuk = rs.getTimestamp("jam_masuk").toLocalDateTime();
            }

            LocalDateTime keluar = LocalDateTime.now();
            long jam = hitungJam(masuk, keluar);
            int tarif = jenis.equals("Motor") ? Parkir.TARIF_MOTOR : Parkir.TARIF_MOBIL;
            int total = (int) (jam * tarif);

            String sql = "UPDATE parkir SET jam_keluar = ?, total_bayar = ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setTimestamp(1, Timestamp.valueOf(keluar));
                ps.setInt(2, total);
                ps.setInt(3, id);
                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(this,
                    "Plat        : " + plat + " (" + jenis + ")\n"
                    + "Masuk       : " + masuk.format(Parkir.FORMAT) + "\n"
                    + "Keluar      : " + keluar.format(Parkir.FORMAT) + "\n"
                    + "Lama parkir : " + jam + " jam\n"
                    + "Total bayar : " + Parkir.rupiah(total),
                    "Kendaraan Keluar", JOptionPane.INFORMATION_MESSAGE);
            kosongkanPlat();
            muatData();
        } catch (SQLException e) {
            tampilError(e);
        }
    }

    // ---------- CETAK STRUK ----------
    private void cetakStruk() {
        String plat = txtPlat.getText().trim().toUpperCase();
        if (plat.isEmpty()) {
            peringatan("Isi plat nomor dulu, atau klik barisnya di tabel.");
            return;
        }

        String sql = "SELECT * FROM parkir WHERE plat_nomor = ? AND jam_keluar IS NOT NULL ORDER BY id DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, plat);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                peringatan("Tidak ada transaksi selesai untuk plat " + plat + ".");
                return;
            }
            LocalDateTime masuk = rs.getTimestamp("jam_masuk").toLocalDateTime();
            LocalDateTime keluar = rs.getTimestamp("jam_keluar").toLocalDateTime();
            long jam = hitungJam(masuk, keluar);

            String teks = "==============================\n"
                    + "         STRUK PARKIR\n"
                    + "==============================\n"
                    + "No. Transaksi : " + String.format("P%04d", rs.getInt("id")) + "\n"
                    + "Plat Nomor    : " + rs.getString("plat_nomor") + "\n"
                    + "Jenis         : " + rs.getString("jenis") + "\n"
                    + "Jam Masuk     : " + masuk.format(Parkir.FORMAT) + "\n"
                    + "Jam Keluar    : " + keluar.format(Parkir.FORMAT) + "\n"
                    + "Lama Parkir   : " + jam + " jam\n"
                    + "------------------------------\n"
                    + "TOTAL BAYAR   : " + Parkir.rupiah(rs.getInt("total_bayar")) + "\n"
                    + "==============================\n"
                    + "     Terima kasih, hati-hati\n";

            JTextArea area = new JTextArea(teks);
            area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
            area.setEditable(false);
            area.setBorder(new EmptyBorder(10, 14, 10, 14));
            JOptionPane.showMessageDialog(this, area, "Struk Parkir", JOptionPane.PLAIN_MESSAGE);
            kosongkanPlat();
        } catch (SQLException e) {
            tampilError(e);
        }
    }

    // ---------- BANTUAN ----------
    private long hitungJam(LocalDateTime masuk, LocalDateTime keluar) {
        long menit = Duration.between(masuk, keluar).toMinutes();
        long jam = (menit + 59) / 60;   // dibulatkan ke atas
        return Math.max(1, jam);        // minimal 1 jam
    }

    // Kosongkan kotak plat nomor supaya bisa langsung input plat baru
    private void kosongkanPlat() {
        txtPlat.setText("");
        tabel.clearSelection();
        txtPlat.requestFocusInWindow();
    }

    private void peringatan(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Perhatian", JOptionPane.WARNING_MESSAGE);
    }

    private void tampilError(SQLException e) {
        JOptionPane.showMessageDialog(this, "Terjadi kesalahan database:\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}