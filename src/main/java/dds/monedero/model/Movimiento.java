package dds.monedero.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Movimiento {
  private LocalDate fecha;
  private BigDecimal monto;
  private TipoMovimiento tipoMovimiento;

  public Movimiento(LocalDate fecha, BigDecimal monto, TipoMovimiento tipoMovimiento) {
    this.fecha = fecha;
    this.monto = monto;
    this.tipoMovimiento = tipoMovimiento;
  }

  public boolean fueRealizado(TipoMovimiento tipoMovimiento, LocalDate fecha){
    return getTipoMovimiento().equals(tipoMovimiento) && esDeLaFecha(fecha);
  }

  public boolean esDeLaFecha(LocalDate fecha) { return this.fecha.equals(fecha); }

  public BigDecimal calcularValor() { return tipoMovimiento.calcularValor(getMonto()); }

  public BigDecimal getMonto() {return monto;}
  public TipoMovimiento getTipoMovimiento(){return tipoMovimiento;}
}
