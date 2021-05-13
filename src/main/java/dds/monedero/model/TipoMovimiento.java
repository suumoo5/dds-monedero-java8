package dds.monedero.model;
import java.math.BigDecimal;

public enum TipoMovimiento {

  DEPOSITO{
    public BigDecimal calcularValor(BigDecimal monto){
      return monto;
    }
  },
  EXTRACCION(){
    public BigDecimal calcularValor(BigDecimal monto){
      return monto.negate();
    }
  };

  public abstract BigDecimal calcularValor(BigDecimal monto);
}
