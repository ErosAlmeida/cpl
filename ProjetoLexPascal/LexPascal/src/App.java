import Mecanismo.*;

public class App {
    public static void main(String[] args) throws Exception {
        Executor exec = new Executor();
        exec.CarregarArquivo("src/Pascal/Saudacao.pas");
        exec.ProcessarBufferPrimario();
        exec.ImprimirBufferPrimario();
        exec.ProcessarBufferSecundario();
        exec.ImprimirBufferSecundario();
        exec.AnalisarMontandoTabelaSimbolos();
        exec.ImprimirTabelaSimbolosPrograma();        
    }
}