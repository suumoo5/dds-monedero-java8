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

  //Asumo que una cuenta que se crea, no tiene movimientos asociados
  public Cuenta(double saldo) { this.saldo = saldo; }

  public void poner(double cuanto) {
    if (esNegativo(cuanto)) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }

    if (getMovimientos().stream().filter(movimiento -> movimiento.isDeposito()).count() >= 3) {//se puede aclarar usando Extraccion y Deposito. Podria hacer esa consulta el Movimiento o en algun TipoMovimiento?
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }

    agregarMovimiento(new Movimiento(LocalDate.now(), cuanto, true));
  }

  public void sacar(double cuanto) {
    if (esNegativo(cuanto)) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }
    if (quedaSaldoNegativo(cuanto)) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }

    //aca abajo hay algo sumamente raro
    double montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    double limite = 1000 - montoExtraidoHoy;
    if (cuanto > limite) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
          + " diarios, límite: " + limite);
    }
    agregarMovimiento(new Movimiento(LocalDate.now(), cuanto, false));
  }

  public double getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> !movimiento.isDeposito() && movimiento.getFecha().equals(fecha)) //se puede aclarar usando Extraccion y Deposito. Podria hacer esa consulta el Movimiento o en algun TipoMovimiento?
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public void agregarMovimiento(Movimiento movimiento) {
    calcularValor(movimiento);  //Chequea a su vez si el movimiento va a ser valido
    movimientos.add(movimiento);
  }

  private void calcularValor(Movimiento movimiento) {
    movimiento.calcularValor(getSaldo());
  }

  public boolean quedaSaldoNegativo(double cantidad){return getSaldo() - cantidad < 0;}
  public boolean esNegativo(double cantidad){return cantidad <= 0;}

  public List<Movimiento> getMovimientos() {return movimientos;}
  public double getSaldo() {return saldo;}
  public void setSaldo(double saldo) {this.saldo = saldo;}

}
