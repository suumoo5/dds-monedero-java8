package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MonederoTest {
  private Cuenta cuenta;
  private BigDecimal saldo = new BigDecimal(0);
  private BigDecimal limiteExtraccion = new BigDecimal(1000);
  private int limiteCantidadDeDepositos = 3;

  private BigDecimal depositoAceptable = new BigDecimal(1500);
  private BigDecimal depositoAceptable2 = new BigDecimal(456);
  private BigDecimal depositoAceptable3 = new BigDecimal(1900);
  private BigDecimal depositoAceptable4 = new BigDecimal(245);

  private BigDecimal extraccionAceptable = new BigDecimal(300);
  private BigDecimal extraccionAceptable2 = new BigDecimal(100);

  private BigDecimal saldoMuyChico = new BigDecimal(90);
  private BigDecimal saldoMediano = new BigDecimal(5000);
  private BigDecimal cantidadMuyGrandeExtraida = new BigDecimal(1001);

  private BigDecimal cantidadNegativa = new BigDecimal(1500).negate();
  private BigDecimal cantidadResultante = new BigDecimal(945);

  @BeforeEach
  void init() {
    cuenta = new Cuenta(saldo, limiteExtraccion, limiteCantidadDeDepositos);
  }

  @Test
  void Poner() {
    cuenta.poner(depositoAceptable);
    assertEquals(cuenta.getSaldo(), depositoAceptable);
  }

  @Test
  void PonerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.poner(cantidadNegativa));
  }

  @Test
  void TresDepositos() {
    cuenta.poner(depositoAceptable);
    cuenta.poner(depositoAceptable2);
    cuenta.poner(depositoAceptable3);
    assertEquals(cuenta.cantidadDeMovimientoParticularDelDia(TipoMovimiento.DEPOSITO, LocalDate.now()), 3);
  }

  @Test
  void MasDeTresDepositos() {
    assertThrows(MaximaCantidadDepositosException.class, () -> {
          cuenta.poner(depositoAceptable);
          cuenta.poner(depositoAceptable2);
          cuenta.poner(depositoAceptable3);
          cuenta.poner(depositoAceptable4);
    });
  }

  @Test
  void ExtraerMasQueElSaldo() {
    assertThrows(SaldoMenorException.class, () -> {
          cuenta.setSaldo(saldoMuyChico);
          cuenta.sacar(cantidadMuyGrandeExtraida);
    });
  }

  @Test
  public void ExtraerMasDe1000() {
    assertThrows(MaximoExtraccionDiarioException.class, () -> {
      cuenta.setSaldo(saldoMediano);
      cuenta.sacar(cantidadMuyGrandeExtraida);
    });
  }

  @Test
  public void ExtraerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.sacar(cantidadNegativa));
  }

  @Test
  public void DiferenciarDepositoDeExtraccion(){
    cuenta.poner(depositoAceptable);
    cuenta.sacar(extraccionAceptable);
    cuenta.sacar(extraccionAceptable2);
    cuenta.poner(depositoAceptable4);
    cuenta.sacar(extraccionAceptable2);
    cuenta.sacar(extraccionAceptable);

    assertEquals(cuenta.cantidadDeMovimientoParticularDelDia(TipoMovimiento.DEPOSITO,LocalDate.now()), 2);
    assertEquals(cuenta.cantidadDeMovimientoParticularDelDia(TipoMovimiento.EXTRACCION, LocalDate.now()), 4);
    assertEquals(cuenta.getMontoExtraidoA(LocalDate.now()), new BigDecimal(800));
    assertEquals(cuenta.getSaldo(), cantidadResultante);
  }
}