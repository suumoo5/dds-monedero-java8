package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private double saldo;
  private List<Movimiento> movimientos = new ArrayList<>();
  private double limiteExtraccionDiario;
  private int limiteCantidadDeDepositosDiarios;

  //Asumo que una cuenta que se crea, no tiene movimientos asociados
  public Cuenta(double saldo, double limiteExtraccionDiario, int limiteCantidadDeDepositosDiarios) {
    this.saldo = saldo;
    this.limiteExtraccionDiario = limiteExtraccionDiario;
    this.limiteCantidadDeDepositosDiarios = limiteCantidadDeDepositosDiarios;
  }

  public void poner(double cantidadDepositada) {
    if (esNegativo(cantidadDepositada)) { throw new MontoNegativoException(cantidadDepositada + ": el monto a ingresar debe ser un valor positivo"); }

    if (cantidadDeMovimientoParticularDelDia(TipoMovimiento.DEPOSITO, LocalDate.now()) >= getLimiteCantidadDeDepositosDiarios()) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + getLimiteCantidadDeDepositosDiarios() + " depositos diarios");
    }

    agregarMovimiento(new Movimiento(LocalDate.now(), cantidadDepositada, TipoMovimiento.DEPOSITO));
  }

  public void sacar(double cantidadExtraida) {
    if (esNegativo(cantidadExtraida)) {throw new MontoNegativoException(cantidadExtraida + ": el monto a ingresar debe ser un valor positivo");}
    if (quedaSaldoNegativo(cantidadExtraida)) {throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");}

    if (cantidadExtraida > cantidadPosibleDeExtracción()) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $" + getLimiteExtraccionDiario() + " diarios, límite: " + cantidadPosibleDeExtracción());
    }

    agregarMovimiento(new Movimiento(LocalDate.now(), cantidadExtraida, TipoMovimiento.EXTRACCION));
  }

  public double cantidadPosibleDeExtracción(){
    return getLimiteExtraccionDiario() - getMontoExtraidoA(LocalDate.now());
  }

  public double getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> movimiento.fueRealizado(TipoMovimiento.EXTRACCION, fecha)) //se puede aclarar usando Extraccion y Deposito. Podria hacer esa consulta el Movimiento o en algun TipoMovimiento?
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public int cantidadDeMovimientoParticularDelDia(TipoMovimiento tipoMovimiento, LocalDate fecha){
    return (int) getMovimientos().stream()
        .filter(movimiento -> movimiento.fueRealizado(tipoMovimiento, fecha))
        .count();
  }

  public void agregarMovimiento(Movimiento movimiento) {
    actualizarSaldo(movimiento.calcularValor()); //Se corrobra antes si se puede agregar el movimiento.
    movimientos.add(movimiento);
  }


  public void actualizarSaldo(double valorDeMovimiento){
    setSaldo(getSaldo() + valorDeMovimiento);
  }

  public boolean quedaSaldoNegativo(double cantidadExtraida){return getSaldo() - cantidadExtraida < 0;}
  public boolean esNegativo(double cantidad){return cantidad <= 0;}

  public List<Movimiento> getMovimientos() {return movimientos;}
  public double getSaldo() {return saldo;}
  public void setSaldo(double saldo) {this.saldo = saldo;}
  public double getLimiteExtraccionDiario() {return limiteExtraccionDiario;}
  public int getLimiteCantidadDeDepositosDiarios() {return limiteCantidadDeDepositosDiarios;}
}
