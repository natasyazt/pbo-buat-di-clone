package id.ac.lectura;

import id.ac.lectura.controller.AcademicController;
import id.ac.lectura.controller.AuthController;
import id.ac.lectura.dao.AcademicRepository;
import id.ac.lectura.exception.DataPersistenceException;
import id.ac.lectura.exception.ValidasiInputException;
import id.ac.lectura.model.Dosen;
import id.ac.lectura.view.MainFrame;
import java.util.Collections;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Titik masuk utama aplikasi desktop LECTURA (Java Swing).
 */
public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Gunakan LookAndFeel default jika system LookAndFeel gagal
        }

        SwingUtilities.invokeLater(() -> {
            AcademicRepository repository = new AcademicRepository("data");
            AcademicController academicController = new AcademicController(repository);
            List<Dosen> dosenList;
            try {
                dosenList = repository.loadAllDosen();
                academicController.initializeData();
            } catch (DataPersistenceException | ValidasiInputException ex) {
                dosenList = Collections.singletonList(repository.getDefaultDosen());
                JOptionPane.showMessageDialog(null,
                        "Gagal memuat data: " + ex.getMessage(),
                        "Kesalahan Inisialisasi",
                        JOptionPane.ERROR_MESSAGE);
            }

            AuthController authController = new AuthController(repository, dosenList);
            MainFrame frame = new MainFrame(academicController, authController);
            frame.setVisible(true);
        });
    }
}
