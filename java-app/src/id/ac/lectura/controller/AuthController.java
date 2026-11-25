package id.ac.lectura.controller;

import id.ac.lectura.dao.AcademicRepository;
import id.ac.lectura.exception.DataPersistenceException;
import id.ac.lectura.exception.ValidasiInputException;
import id.ac.lectura.model.Dosen;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Controller autentikasi multi-akun dosen (Login, Register, Profil) secara real-time.
 */
public class AuthController {
    private final AcademicRepository repository;
    private final List<Dosen> daftarDosen = new ArrayList<>();
    private final List<Runnable> profileListeners = new ArrayList<>();
    private Dosen dosenAktif;
    private boolean loggedIn;

    public AuthController(AcademicRepository repository, List<Dosen> initialDosenList) {
        this.repository = repository;
        if (initialDosenList != null && !initialDosenList.isEmpty()) {
            this.daftarDosen.addAll(initialDosenList);
        } else {
            this.daftarDosen.add(repository.getDefaultDosen());
        }
        this.dosenAktif = this.daftarDosen.get(0);
        this.loggedIn = false;
    }

    public void addProfileListener(Runnable listener) {
        if (listener != null && !profileListeners.contains(listener)) {
            profileListeners.add(listener);
        }
    }

    private void notifyProfileListeners() {
        for (Runnable r : profileListeners) {
            r.run();
        }
    }

    public List<Dosen> getDaftarDosen() {
        return Collections.unmodifiableList(daftarDosen);
    }

    public Dosen getDosenAktif() {
        return dosenAktif;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void login(String email, String password) throws ValidasiInputException {
        if (email == null || !email.contains("@")) {
            throw new ValidasiInputException("Format email institusi tidak valid.");
        }
        if (password == null || password.length() < 6) {
            throw new ValidasiInputException("Kata sandi minimal 6 karakter.");
        }
        String trimmedEmail = email.trim();
        for (Dosen d : daftarDosen) {
            if (d.getEmail().equalsIgnoreCase(trimmedEmail) && d.getPassword().equals(password)) {
                this.dosenAktif = d;
                this.loggedIn = true;
                notifyProfileListeners();
                return;
            }
        }
        throw new ValidasiInputException("Email atau kata sandi tidak sesuai.");
    }

    public Dosen register(String nama, String nip, String gelar, String email,
                          String prodi, String fakultas, String password, String konfirmasi)
            throws ValidasiInputException, DataPersistenceException {
        if (password == null || password.length() < 6) {
            throw new ValidasiInputException("Kata sandi minimal 6 karakter.");
        }
        if (!password.equals(konfirmasi)) {
            throw new ValidasiInputException("Konfirmasi kata sandi tidak cocok.");
        }
        String cleanEmail = email == null ? "" : email.trim();
        String cleanNip = nip == null ? "" : nip.trim();
        for (Dosen existing : daftarDosen) {
            if (existing.getEmail().equalsIgnoreCase(cleanEmail)) {
                throw new ValidasiInputException("Email " + cleanEmail + " sudah terdaftar.");
            }
            if (!cleanNip.isEmpty() && existing.getNip().equalsIgnoreCase(cleanNip)) {
                throw new ValidasiInputException("NIP " + cleanNip + " sudah terdaftar.");
            }
        }

        String id = "dos-" + UUID.randomUUID().toString().substring(0, 6);
        Dosen baru = new Dosen(id, nip, nama, gelar, email, prodi, fakultas, password);
        baru.validasi();
        daftarDosen.add(baru);
        repository.saveAllDosen(daftarDosen);
        return baru;
    }

    public void updateProfil(String nama, String nip, String gelar,
                             String email, String prodi, String fakultas)
            throws ValidasiInputException, DataPersistenceException {
        String cleanEmail = email == null ? "" : email.trim();
        for (Dosen other : daftarDosen) {
            if (!other.getId().equals(dosenAktif.getId()) && other.getEmail().equalsIgnoreCase(cleanEmail)) {
                throw new ValidasiInputException("Email sudah digunakan oleh akun dosen lain.");
            }
        }
        dosenAktif.setNama(nama);
        dosenAktif.setNip(nip);
        dosenAktif.setGelar(gelar);
        dosenAktif.setEmail(email);
        dosenAktif.setProdi(prodi);
        dosenAktif.setFakultas(fakultas);
        dosenAktif.validasi();
        repository.saveAllDosen(daftarDosen);
        notifyProfileListeners();
    }

    public void ubahPassword(String passwordLama, String passwordBaru, String konfirmasi)
            throws ValidasiInputException, DataPersistenceException {
        if (passwordLama == null || !dosenAktif.getPassword().equals(passwordLama)) {
            throw new ValidasiInputException("Kata sandi lama tidak sesuai.");
        }
        if (passwordBaru == null || passwordBaru.length() < 6) {
            throw new ValidasiInputException("Kata sandi baru minimal 6 karakter.");
        }
        if (!passwordBaru.equals(konfirmasi)) {
            throw new ValidasiInputException("Konfirmasi kata sandi baru tidak cocok.");
        }
        dosenAktif.setPassword(passwordBaru);
        repository.saveAllDosen(daftarDosen);
    }

    public void logout() {
        this.loggedIn = false;
    }
}
