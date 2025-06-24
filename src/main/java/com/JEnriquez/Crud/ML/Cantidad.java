package com.JEnriquez.Crud.ML;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Cantidad {

    private int IdCantidad;
    
    public TipoMoneda IdTipoMoneda;
    
    private int CantidadDinero;
    
    private Double Denominacion;
}
