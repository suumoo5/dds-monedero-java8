package dds.monedero.model;

import java.time.LocalDate;

public class Movimiento {
  private LocalDate fecha;
  //En ningún lenguaje de programación usen jamás doubles para modelar dinero en el mundo real
  //siempre usen numeros de precision arbitraria, como BigDecimal en Java y similares
  private double monto;
  private TipoMovimiento tipoMovimiento;

  public Movimiento(LocalDate fecha, double monto, TipoMovimiento tipoMovimiento) {
    this.fecha = fecha;
    this.monto = monto;
    this.tipoMovimiento = tipoMovimiento;
  }

  public boolean fueRealizado(TipoMovimiento tipoMovimiento, LocalDate fecha){
    return getTipoMovimiento().equals(tipoMovimiento) && esDeLaFecha(fecha);
  }

  public boolean esDeLaFecha(LocalDate fecha) { return this.fecha.equals(fecha); }

  public double calcularValor() { return tipoMovimiento.calcularValor(getMonto()); }

  public double getMonto() {return monto;}
  public TipoMovimiento getTipoMovimiento(){return tipoMovimiento;}
}
