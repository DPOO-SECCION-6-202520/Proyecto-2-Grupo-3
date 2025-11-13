package modelo.usuarios;

/**
 * Clase base que representa a un usuario del sistema.
 * Contiene información básica de autenticación y saldo virtual.
 * La persistencia será manejada por PersistenciaUsuarios.
 */
public abstract class Usuario {
    protected String login;
    protected String password;
    protected double saldoVirtual;
    protected String tipoUsuario;

    //Constructor de la clase Usuario
    public Usuario(String login, String password, String tipoUsuario) {
        this.login = login; // nombre de usuario para login
        this.password = password; // contraseña del usuario 
        this.saldoVirtual = 0.0; // Saldo inicial en 0
        this.tipoUsuario = tipoUsuario; // tipo de usuario (cliente, organizador, administrador)
    }

    //Constructor vacio para persistencia
    public Usuario() {

    }

    // ==================== MÉTODOS GETTER Y SETTER ====================
    // setters nuevos para persistencia
    // establece el login del usuario
    public void setLogin(String login) { this.login = login; }
    // establece la contraseña del usuario
    public void setPassword(String password) { this.password = password; }
    // establece el tipo de usuario
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    // devuelve el login del usuario
    public String getLogin() { return login; }
    
    // devuelve la contraseña del usuario (para validación)
    public String getPassword() { return password; }
    
    // devuelve el saldo virtual actual del usuario
    public double getSaldoVirtual() { return saldoVirtual; }
    
    //Actualiza el saldo virtual del usuario
    public void setSaldoVirtual(double nuevoSaldo) { this.saldoVirtual = nuevoSaldo; }
    
    // Agrega cantidad al saldo virtual (para recargas o reembolsos)
    public void agregarSaldo(double cantidad) {
        this.saldoVirtual += cantidad;
    }
    
    // devuelve el tipo de usuario
    public String getTipoUsuario() { return tipoUsuario; }
    
    /**
     * Método para validar credenciales de login
     * true si las credenciales son correctas
     */
    public boolean validarCredenciales(String loginInput, String passwordInput) {
        return this.login.equals(loginInput) && this.password.equals(passwordInput);
    }
    
    //convierte la info a String
    @Override
    public String toString() {
        return "Usuario{" +
                "login='" + login + '\'' +
                ", tipoUsuario='" + tipoUsuario + '\'' +
                ", saldoVirtual=" + saldoVirtual +
                '}';
    }

}