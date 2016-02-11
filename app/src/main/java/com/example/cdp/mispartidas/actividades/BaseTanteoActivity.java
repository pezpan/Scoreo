package com.example.cdp.mispartidas.actividades;

public abstract class BaseTanteoActivity extends ActionBarActivity {

  public String identificador;
  public Partida partida;
  public Backup backup;
  public int indice;
  public Context context;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      // Establecemos el layout
      setContentView(getLayoutResourceId());
      
      
      
  }
  
  
  
  
  
  protected abstract int getLayoutResourceId();


}
