package com.JEnriquez.Crud.ML;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Usuario {
    private int IdUsuario;
    private String Nombre;
    private String Username;
    private String Password;
    private double Saldo;
    public Rol rol;
}
