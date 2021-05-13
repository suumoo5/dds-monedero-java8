package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private double saldo;
  private List<Movimiento> movimientos = new ArrayList<>();
  private double limiteExtraccionDiario;
  private int limiteDepositoDiario;

  //Asumo que una cuenta que se crea, no tiene movimientos asociados
  public Cuenta(double saldo, double limiteExtraccionDiario, int limiteDepositoDiario) {
    this.saldo = saldo;
    this.limiteExtraccionDiario = limiteExtraccionDiario;
    this.limiteDepositoDiario = limiteDepositoDiario;
  }

  public void poner(double cantidadDepositada) {
    if (esNegativo(cantidadDepositada)) { throw new MontoNegativoException(cantidadDepositada + ": el monto a ingresar debe ser un valor positivo"); }

    //se puede aclarar usando Extraccion y Deposito. Podria hacer esa consulta el Movimiento o en algun TipoMovimiento?
    if (getMovimientos().stream().filter(movimiento -> movimiento.isDeposito()).count() >= getLimiteDepositoDiario()) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + getLimiteDepositoDiario() + " depositos diarios");
    }

    agregarMovimiento(new Movimiento(LocalDate.now(), cantidadDepositada, true));
  }

  public void sacar(double cantidadExtraida) {
    if (esNegativo(cantidadExtraida)) {throw new MontoNegativoException(cantidadExtraida + ": el monto a ingresar debe ser un valor positivo");}
    if (quedaSaldoNegativo(cantidadExtraida)) {throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");}

    if (cantidadExtraida > cantidadPosibleDeExtracción()) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $" + getLimiteExtraccionDiario() + " diarios, límite: " + cantidadPosibleDeExtracción());
    }

    agregarMovimiento(new Movimiento(LocalDate.now(), cantidadExtraida, false));
  }

  public double cantidadPosibleDeExtracción(){ return getLimiteExtraccionDiario() - getMontoExtraidoA(LocalDate.now());}

  public double getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> !movimiento.isDeposito() && movimiento.getFecha().equals(fecha)) //se puede aclarar usando Extraccion y Deposito. Podria hacer esa consulta el Movimiento o en algun TipoMovimiento?
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public void agregarMovimiento(Movimiento movimiento) {
    setSaldo(calcularValor(movimiento));
    movimientos.add(movimiento);
  }

  private double calcularValor(Movimiento movimiento) {
    return movimiento.calcularValor(getSaldo());
  }

  public boolean quedaSaldoNegativo(double cantidad){return getSaldo() - cantidad < 0;}
  public boolean esNegativo(double cantidad){return cantidad <= 0;}

  public List<Movimiento> getMovimientos() {return movimientos;}
  public double getSaldo() {return saldo;}
  public void setSaldo(double saldo) {this.saldo = saldo;}
  public double getLimiteExtraccionDiario() {return limiteExtraccionDiario;}
  public int getLimiteDepositoDiario() {return limiteDepositoDiario;}
}
