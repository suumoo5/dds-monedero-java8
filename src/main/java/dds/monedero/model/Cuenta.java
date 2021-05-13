package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.util.stream.Collectors;

public class Cuenta {

  private BigDecimal saldo;
  private List<Movimiento> movimientos = new ArrayList<>();
  private BigDecimal limiteExtraccionDiario;
  private int limiteCantidadDeDepositosDiarios;

  //Asumo que una cuenta que se crea, no tiene movimientos asociados
  public Cuenta(BigDecimal saldo, BigDecimal limiteExtraccionDiario, int limiteCantidadDeDepositosDiarios) {
    this.saldo = saldo;
    this.limiteExtraccionDiario = limiteExtraccionDiario;
    this.limiteCantidadDeDepositosDiarios = limiteCantidadDeDepositosDiarios;
  }

  public void poner(BigDecimal cantidadDepositada) {
    if (esNegativo(cantidadDepositada)) { throw new MontoNegativoException(cantidadDepositada + ": el monto a ingresar debe ser un valor positivo"); }

    if (cantidadDeMovimientoParticularDelDia(TipoMovimiento.DEPOSITO, LocalDate.now()) >= getLimiteCantidadDeDepositosDiarios()) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + getLimiteCantidadDeDepositosDiarios() + " depositos diarios");
    }

    agregarMovimiento(new Movimiento(LocalDate.now(), cantidadDepositada, TipoMovimiento.DEPOSITO));
  }

  public void sacar(BigDecimal cantidadExtraida) {
    if (esNegativo(cantidadExtraida)) {throw new MontoNegativoException(cantidadExtraida + ": el monto a ingresar debe ser un valor positivo");}
    if (quedaSaldoNegativo(cantidadExtraida)) {throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");}

    if (cantidadExtraida.max(cantidadPosibleDeExtracción()).equals(cantidadExtraida)) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $" + getLimiteExtraccionDiario() + " diarios, límite: " + cantidadPosibleDeExtracción());
    }

    agregarMovimiento(new Movimiento(LocalDate.now(), cantidadExtraida, TipoMovimiento.EXTRACCION));
  }

  public BigDecimal cantidadPosibleDeExtracción(){
    return getLimiteExtraccionDiario().subtract(getMontoExtraidoA(LocalDate.now()));
  }

  public BigDecimal getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> movimiento.fueRealizado(TipoMovimiento.EXTRACCION, fecha))
        .map(Movimiento::getMonto)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public int cantidadDeMovimientoParticularDelDia(TipoMovimiento tipoMovimiento, LocalDate fecha){
    return (int) getMovimientos().stream()
        .filter(movimiento -> movimiento.fueRealizado(tipoMovimiento, fecha))
        .count();
  }

  public void agregarMovimiento(Movimiento movimiento) {
    actualizarSaldo(movimiento.calcularValor());
    movimientos.add(movimiento);
  }

  public void actualizarSaldo(BigDecimal valorDeMovimiento){
    setSaldo(getSaldo().add(valorDeMovimiento));
  }

  public boolean quedaSaldoNegativo(BigDecimal cantidadExtraida){
    return esNegativo(getSaldo().subtract(cantidadExtraida));
  }
  public boolean esNegativo(BigDecimal cantidad){
    return cantidad.max(new BigDecimal(0)).equals(new BigDecimal(0));
  }

  public List<Movimiento> getMovimientos() {return movimientos;}
  public BigDecimal getSaldo() {return saldo;}
  public void setSaldo(BigDecimal saldo) {this.saldo = saldo;}
  public BigDecimal getLimiteExtraccionDiario() {return limiteExtraccionDiario;}
  public int getLimiteCantidadDeDepositosDiarios() {return limiteCantidadDeDepositosDiarios;}
}
