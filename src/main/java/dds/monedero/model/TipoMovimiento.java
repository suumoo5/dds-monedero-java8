package dds.monedero.model;

public enum TipoMovimiento {

  DEPOSITO{
    public double calcularValor(double monto){
      return monto;
    }
  },
  EXTRACCION(){
    public double calcularValor(double monto){
      return -monto;
    }
  };

  public abstract double calcularValor(double monto);
}
