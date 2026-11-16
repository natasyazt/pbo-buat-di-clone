package id.ac.lectura.controller;

import id.ac.lectura.exception.ValidasiInputException;
import id.ac.lectura.model.Dosen;

/**
 * Controller autentikasi portal dosen LECTURA.
 */
public class AuthController {
    private final Dosen dosenAktif;
    private boolean loggedIn;

    public AuthController(Dosen dosenAktif) {
        this.dosenAktif = dosenAktif;
        this.loggedIn = false;
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
        if (dosenAktif.getEmail().equalsIgnoreCase(email.trim())
                && dosenAktif.getPassword().equals(password)) {
            this.loggedIn = true;
            return;
        }
        throw new ValidasiInputException("Email atau kata sandi tidak sesuai.");
    }

    public void quickLogin() {
        this.loggedIn = true;
    }

    public void logout() {
        this.loggedIn = false;
    }
}
