package dds.monedero.model;

import java.time.LocalDate;

//Dividir a Movimiento en Extraccion y Deposito. Clase TipoMovimiento?
public class Movimiento {
  private LocalDate fecha;
  //En ningún lenguaje de programación usen jamás doubles para modelar dinero en el mundo real
  //siempre usen numeros de precision arbitraria, como BigDecimal en Java y similares
  private double monto;
  private boolean esDeposito; //Esto vuela. Reemplazarlo por TipoMovimiento (clase? Interface?)

  public Movimiento(LocalDate fecha, double monto, boolean esDeposito) {
    this.fecha = fecha;
    this.monto = monto;
    this.esDeposito = esDeposito;
  }

  public boolean fueDepositado(LocalDate fecha) {return isDeposito() && esDeLaFecha(fecha);} //Haciendo Extraccion y Deposito habria que ver de nuevo esta funcion

  public boolean fueExtraido(LocalDate fecha) {return isExtraccion() && esDeLaFecha(fecha);} //Haciendo Extraccion y Deposito habria que ver de nuevo esta funcion

  public boolean esDeLaFecha(LocalDate fecha) {
    return this.fecha.equals(fecha);
  }

  public boolean isDeposito() {
    return esDeposito;
  } // Red flag --> vuela

  public boolean isExtraccion() {
    return !esDeposito;
  } // Red flag --> vuela

  //Agregar directamente el Movimiento
  public void agregateA(Cuenta cuenta) {
    cuenta.setSaldo(calcularValor(cuenta));
    cuenta.agregarMovimiento(fecha, monto, esDeposito);
  }

  //Podria hacerse polimorifica si existe Extraccion y Deposito
  public double calcularValor(Cuenta cuenta) {
    if (esDeposito) {
      return cuenta.getSaldo() + getMonto();
    } else {
      return cuenta.getSaldo() - getMonto();
    }
  }

  public double getMonto() {
    return monto;
  }
  public LocalDate getFecha() {
    return fecha;
  }
}

/* Notas
- Cuenta ya conoce a Movimiento, sin embargo, Movimiento pasa a Cuenta por paramentro en algunos metodos

 */